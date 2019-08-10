/*
 * The MIT License
 *
 * Copyright (c) 2017, aoju.org All rights reserved.
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
package org.aoju.bus.logger.dialect.tinylog;

import org.aoju.bus.core.utils.ArrayUtils;
import org.aoju.bus.core.utils.StringUtils;
import org.aoju.bus.logger.AbstractLog;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntryForwarder;
import org.pmw.tinylog.Logger;

/**
 * <a href="http://www.tinylog.org/">tinylog</a> log.<br>
 *
 * @author Kimi Liu
 * @version 3.0.0
 * @since JDK 1.8
 */
public class TinyLog extends AbstractLog {

    private static final long serialVersionUID = -4848042277045993735L;

    /**
     * 堆栈增加层数，因为封装因此多了两层，此值用于正确获取当前类名
     */
    private static final int DEPTH = 2;

    private int level;
    private String name;

    public TinyLog(Class<?> clazz) {
        this.name = clazz.getName();
        this.level = Logger.getLevel(name).ordinal();
    }

    public TinyLog(String name) {
        this.name = name;
        this.level = Logger.getLevel(name).ordinal();
    }

    /**
     * 如果最后一个参数为异常参数，则获取之，否则返回null
     *
     * @param arguments 参数
     * @return 最后一个异常参数
     * @since 4.0.3
     */
    private static Throwable getLastArgumentIfThrowable(Object... arguments) {
        if (ArrayUtils.isNotEmpty(arguments) && arguments[arguments.length - 1] instanceof Throwable) {
            return (Throwable) arguments[arguments.length - 1];
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.level <= org.pmw.tinylog.Level.TRACE.ordinal();
    }

    @Override
    public void trace(String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.TRACE, format, arguments);
    }

    @Override
    public void trace(Throwable t, String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.TRACE, t, format, arguments);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.level <= org.pmw.tinylog.Level.DEBUG.ordinal();
    }

    @Override
    public void debug(String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.DEBUG, format, arguments);
    }

    @Override
    public void debug(Throwable t, String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.DEBUG, t, format, arguments);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.level <= org.pmw.tinylog.Level.INFO.ordinal();
    }

    @Override
    public void info(String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.INFO, format, arguments);
    }

    @Override
    public void info(Throwable t, String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.INFO, t, format, arguments);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.level <= org.pmw.tinylog.Level.WARNING.ordinal();
    }

    @Override
    public void warn(String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.WARN, format, arguments);
    }

    @Override
    public void warn(Throwable t, String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.WARN, t, format, arguments);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.level <= org.pmw.tinylog.Level.ERROR.ordinal();
    }

    @Override
    public void error(String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.ERROR, format, arguments);
    }

    @Override
    public void error(Throwable t, String format, Object... arguments) {
        log(org.aoju.bus.logger.level.Level.ERROR, t, format, arguments);
    }

    @Override
    public void log(org.aoju.bus.logger.level.Level level, String format, Object... arguments) {
        LogEntryForwarder.forward(DEPTH, toTinyLevel(level), getLastArgumentIfThrowable(level, arguments), format, arguments);
    }

    @Override
    public void log(org.aoju.bus.logger.level.Level level, Throwable t, String format, Object... arguments) {
        LogEntryForwarder.forward(DEPTH, toTinyLevel(level), t, format, arguments);
    }

    /**
     * 将Level等级转换为Tinylog的Level等级
     *
     * @param level Level等级
     * @return Tinylog的Level
     * @since 4.0.3
     */
    private Level toTinyLevel(org.aoju.bus.logger.level.Level level) {
        Level tinyLevel;
        switch (level) {
            case TRACE:
                tinyLevel = Level.TRACE;
                break;
            case DEBUG:
                tinyLevel = Level.DEBUG;
                break;
            case INFO:
                tinyLevel = Level.INFO;
                break;
            case WARN:
                tinyLevel = Level.WARNING;
                break;
            case ERROR:
                tinyLevel = Level.ERROR;
                break;
            case OFF:
                tinyLevel = Level.OFF;
                break;
            default:
                throw new Error(StringUtils.format("Can not identify level: {}", level));
        }
        return tinyLevel;
    }
}