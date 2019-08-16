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
package org.aoju.bus.poi.excel;

import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.utils.IoUtils;
import org.apache.poi.poifs.filesystem.FileMagic;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Excel文件工具类
 *
 * @author Kimi Liu
 * @version 3.0.6
 * @since JDK 1.8
 */
public class ExcelFileUtils {

    /**
     * 是否为XLS格式的Excel文件（HSSF）
     * XLS文件主要用于Excel 97~2003创建
     *
     * @param in excel输入流
     * @return 是否为XLS格式的Excel文件（HSSF）
     */
    public static boolean isXls(InputStream in) {
        final PushbackInputStream pin = IoUtils.toPushbackStream(in, 8);
        try {
            return FileMagic.valueOf(pin) == FileMagic.OLE2;
        } catch (IOException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 是否为XLSX格式的Excel文件（XSSF）
     * XLSX文件主要用于Excel 2007+创建
     *
     * @param in excel输入流
     * @return 是否为XLSX格式的Excel文件（XSSF）
     */
    public static boolean isXlsx(InputStream in) {
        if (false == in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        try {
            return FileMagic.valueOf(in) == FileMagic.OOXML;
        } catch (IOException e) {
            throw new InstrumentException(e);
        }
    }

}
