/*
 * The MIT License
 *
 * Copyright (c) 2015-2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.health.hardware.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.health.common.windows.PerfWildcardQuery;
import org.aoju.bus.health.common.windows.PerfWildcardQuery.PdhCounterWildcardProperty;
import org.aoju.bus.health.common.windows.WmiQueryHandler;
import org.aoju.bus.health.common.windows.WmiUtils;
import org.aoju.bus.health.hardware.AbstractSensors;
import org.aoju.bus.logger.Logger;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * WindowsSensors class.
 * </p>
 *
 * @author Kimi Liu
 * @version 5.5.3
 * @since JDK 1.8+
 */
public class WindowsSensors extends AbstractSensors {

    private static final String BASE_SENSOR_CLASS = "Sensor";
    private final WmiQuery<OhmHardwareProperty> ohmHardwareQuery = new WmiQuery<>(WmiUtils.OHM_NAMESPACE,
            "Hardware WHERE HardwareType=\"CPU\"", OhmHardwareProperty.class);
    private final WmiQuery<OhmHardwareProperty> owhVoltageQuery = new WmiQuery<>(WmiUtils.OHM_NAMESPACE,
            "Hardware WHERE SensorType=\"Voltage\"", OhmHardwareProperty.class);
    private final WmiQuery<OhmSensorProperty> ohmSensorQuery = new WmiQuery<>(WmiUtils.OHM_NAMESPACE, null,
            OhmSensorProperty.class);
    private final WmiQuery<FanProperty> fanQuery = new WmiQuery<>("Win32_Fan", FanProperty.class);
    private final WmiQuery<VoltProperty> voltQuery = new WmiQuery<>("Win32_Processor", VoltProperty.class);
    private final PerfWildcardQuery<ThermalZoneProperty> thermalZonePerfCounters = new PerfWildcardQuery<>(
            ThermalZoneProperty.class, "Thermal Zone Information",
            "Win32_PerfRawData_Counters_ThermalZoneInformation WHERE Name LIKE \"%cpu%\"");
    private final WmiQueryHandler wmiQueryHandler = WmiQueryHandler.createInstance();

    @Override
    public double queryCpuTemperature() {
        // Attempt to fetch value from Open Hardware Monitor if it is running,
        // as it will give the most accurate results and the time to query (or
        // attempt) is trivial
        double tempC = getTempFromOHM();
        if (tempC > 0d) {
            return tempC;
        }

        // If we get this far, OHM is not running. Try from PDH/WMI
        tempC = getTempFromPerfCounters();

        // Other fallbacks to WMI are unreliable so we omit them
        // Win32_TemperatureProbe is the official location but is not currently
        // populated and is "reserved for future use"
        // MSAcpu_ThermalZoneTemperature only updates during a high temperature
        // event and is otherwise unchanged/misleading.
        return tempC;
    }

    private double getTempFromOHM() {
        WmiResult<OhmHardwareProperty> ohmHardware = this.wmiQueryHandler.queryWMI(ohmHardwareQuery);
        if (ohmHardware.getResultCount() > 0) {
            Logger.debug("Found Temperature data in Open Hardware Monitor");
            String cpuIdentifier = WmiUtils.getString(ohmHardware, OhmHardwareProperty.IDENTIFIER, 0);
            if (cpuIdentifier.length() > 0) {
                StringBuilder sb = new StringBuilder(BASE_SENSOR_CLASS);
                sb.append(" WHERE Parent = \"").append(cpuIdentifier);
                sb.append("\" AND SensorType=\"Temperature\"");
                ohmSensorQuery.setWmiClassName(sb.toString());
                WmiResult<OhmSensorProperty> ohmSensors = this.wmiQueryHandler.queryWMI(ohmSensorQuery);
                if (ohmSensors.getResultCount() > 0) {
                    double sum = 0;
                    for (int i = 0; i < ohmSensors.getResultCount(); i++) {
                        sum += WmiUtils.getFloat(ohmSensors, OhmSensorProperty.VALUE, i);
                    }
                    return sum / ohmSensors.getResultCount();
                }
            }
        }
        return 0;
    }

    private double getTempFromPerfCounters() {
        double tempC = 0d;
        long tempK = 0L;
        Map<ThermalZoneProperty, List<Long>> valueListMap = this.thermalZonePerfCounters.queryValuesWildcard();
        List<Long> valueList = valueListMap.get(ThermalZoneProperty.TEMPERATURE);
        if (valueList != null && !valueList.isEmpty()) {
            Logger.debug("Found Temperature data in PDH or WMI Counter");
            tempK = valueList.get(0);
        }

        if (tempK > 2732L) {
            tempC = tempK / 10d - 273.15;
        } else if (tempK > 274L) {
            tempC = tempK - 273d;
        }
        return tempC < 0d ? 0d : tempC;
    }

    @Override
    public int[] queryFanSpeeds() {
        // Attempt to fetch value from Open Hardware Monitor if it is running
        int[] fanSpeeds = getFansFromOHM();
        if (fanSpeeds.length > 0) {
            return fanSpeeds;
        }

        // If we get this far, OHM is not running.
        // Try to get from conventional WMI
        fanSpeeds = getFansFromWMI();
        if (fanSpeeds.length > 0) {
            return fanSpeeds;
        }

        return Normal.EMPTY_INT_ARRAY;
    }

    private int[] getFansFromOHM() {
        WmiResult<OhmHardwareProperty> ohmHardware = this.wmiQueryHandler.queryWMI(ohmHardwareQuery);
        if (ohmHardware.getResultCount() > 0) {
            Logger.debug("Found Fan data in Open Hardware Monitor");
            String cpuIdentifier = WmiUtils.getString(ohmHardware, OhmHardwareProperty.IDENTIFIER, 0);
            if (cpuIdentifier.length() > 0) {
                StringBuilder sb = new StringBuilder(BASE_SENSOR_CLASS);
                sb.append(" WHERE Parent = \"").append(cpuIdentifier);
                sb.append("\" AND SensorType=\"Fan\"");
                ohmSensorQuery.setWmiClassName(sb.toString());
                WmiResult<OhmSensorProperty> ohmSensors = this.wmiQueryHandler.queryWMI(ohmSensorQuery);

                if (ohmSensors.getResultCount() > 0) {
                    int[] fanSpeeds = new int[ohmSensors.getResultCount()];
                    for (int i = 0; i < ohmSensors.getResultCount(); i++) {
                        fanSpeeds[i] = (int) WmiUtils.getFloat(ohmSensors, OhmSensorProperty.VALUE, i);
                    }
                    return fanSpeeds;
                }
            }
        }
        return Normal.EMPTY_INT_ARRAY;
    }

    private int[] getFansFromWMI() {
        WmiResult<FanProperty> fan = this.wmiQueryHandler.queryWMI(fanQuery);
        if (fan.getResultCount() > 1) {
            Logger.debug("Found Fan data in WMI");
            int[] fanSpeeds = new int[fan.getResultCount()];
            for (int i = 0; i < fan.getResultCount(); i++) {
                fanSpeeds[i] = (int) WmiUtils.getUint64(fan, FanProperty.DESIREDSPEED, i);
            }
            return fanSpeeds;
        }
        return Normal.EMPTY_INT_ARRAY;
    }

    @Override
    public double queryCpuVoltage() {
        // Attempt to fetch value from Open Hardware Monitor if it is running
        double volts = getVoltsFromOHM();
        if (volts > 0d) {
            return volts;
        }

        // If we get this far, OHM is not running.
        // Try to get from conventional WMI
        volts = getVoltsFromWMI();

        return volts;
    }

    private double getVoltsFromOHM() {
        WmiResult<OhmHardwareProperty> ohmHardware = this.wmiQueryHandler.queryWMI(owhVoltageQuery);
        if (ohmHardware.getResultCount() > 0) {
            Logger.debug("Found Voltage data in Open Hardware Monitor");
            // Look for identifier containing "cpu"
            String voltIdentifierStr = null;
            for (int i = 0; i < ohmHardware.getResultCount(); i++) {
                String id = WmiUtils.getString(ohmHardware, OhmHardwareProperty.IDENTIFIER, i);
                if (id.toLowerCase().contains("cpu")) {
                    voltIdentifierStr = id;
                    break;
                }
            }
            // If none found, just get the first one
            if (voltIdentifierStr == null) {
                voltIdentifierStr = WmiUtils.getString(ohmHardware, OhmHardwareProperty.IDENTIFIER, 0);
            }
            // Now fetch sensor
            StringBuilder sb = new StringBuilder(BASE_SENSOR_CLASS);
            sb.append(" WHERE Parent = \"").append(voltIdentifierStr);
            sb.append("\" AND SensorType=\"Voltage\"");
            ohmSensorQuery.setWmiClassName(sb.toString());
            WmiResult<OhmSensorProperty> ohmSensors = this.wmiQueryHandler.queryWMI(ohmSensorQuery);
            if (ohmSensors.getResultCount() > 0) {
                return WmiUtils.getFloat(ohmSensors, OhmSensorProperty.VALUE, 0);
            }
        }
        return 0d;
    }

    private double getVoltsFromWMI() {
        WmiResult<VoltProperty> voltage = wmiQueryHandler.queryWMI(voltQuery);
        if (voltage.getResultCount() > 1) {
            Logger.debug("Found Voltage data in WMI");
            int decivolts = WmiUtils.getUint16(voltage, VoltProperty.CURRENTVOLTAGE, 0);
            // If the eighth bit is set, bits 0-6 contain the voltage
            // multiplied by 10. If the eighth bit is not set, then the bit
            // setting in VoltageCaps represents the voltage value.
            if (decivolts > 0) {
                if ((decivolts & 0x80) == 0) {
                    decivolts = WmiUtils.getUint32(voltage, VoltProperty.VOLTAGECAPS, 0);
                    // This value is really a bit setting, not decivolts
                    if ((decivolts & 0x1) > 0) {
                        return 5.0;
                    } else if ((decivolts & 0x2) > 0) {
                        return 3.3;
                    } else if ((decivolts & 0x4) > 0) {
                        return 2.9;
                    }
                } else {
                    // Value from bits 0-6, divided by 10
                    return (decivolts & 0x7F) / 10d;
                }
            }
        }
        return 0d;
    }

    enum OhmHardwareProperty {
        IDENTIFIER
    }

    enum OhmSensorProperty {
        VALUE
    }

    enum FanProperty {
        DESIREDSPEED
    }

    enum VoltProperty {
        CURRENTVOLTAGE, VOLTAGECAPS
    }

    /*
     * For temperature query
     */
    enum ThermalZoneProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME("*cpu*"),
        // Remaining elements define counters
        TEMPERATURE("Temperature");

        private final String counter;

        ThermalZoneProperty(String counter) {
            this.counter = counter;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getCounter() {
            return counter;
        }
    }
}
