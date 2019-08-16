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
package org.aoju.bus.spring.servlet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.aoju.bus.core.utils.IoUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @author Kimi Liu
 * @version 3.0.6
 * @since JDK 1.8
 */
public class BodyCacheHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final static byte[] DEFAULT_BYTE = new byte[0];
    private byte[] body;
    private ServletInputStreamWrapper inputStreamWrapper;

    BodyCacheHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.body = IoUtils.readBytes(request.getInputStream());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body != null ? this.body : DEFAULT_BYTE);
        // 初始 ServletInputStreamWrapper
        this.inputStreamWrapper = new ServletInputStreamWrapper(byteArrayInputStream);
        // 设置 InputStream 到我们自己的包装类中
        this.inputStreamWrapper.setInputStream(byteArrayInputStream);
    }


    public byte[] getBody() {
        return body;
    }

    @Override
    public ServletInputStream getInputStream() {
        return this.inputStreamWrapper;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.inputStreamWrapper));
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private static class ServletInputStreamWrapper extends ServletInputStream {

        private InputStream inputStream;

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return this.inputStream.read();
        }
    }

}
