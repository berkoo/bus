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
package org.aoju.bus.starter;

/**
 * 全局扩展配置
 *
 * @author Kimi Liu
 * @version 5.5.3
 * @since JDK 1.8+
 */
public class BusXExtend {

    /**
     * 数据源配置
     */
    public static final String DATASOURCE = "spring.datasource";
    /**
     * 缓存配置
     */
    public static final String CACHE = "extend.cache";
    /**
     * 跨域支持
     */
    public static final String CORS = "extend.cors";
    /**
     * Druid监控
     */
    public static final String DRUID = "extend.druid";
    /**
     * Druid监控
     */
    public static final String DUBBO = "extend.dubbo";
    /**
     * 国际化支持
     */
    public static final String I18N = "extend.i18n";
    /**
     * 限流支持
     */
    public static final String LIMITER = "extend.limiter";
    /**
     * Mybatis/Mapper
     */
    public static final String MYBATIS = "extend.mybatis";
    /**
     * API网关
     */
    public static final String METRIC = "extend.metric";
    /**
     * 授权登陆
     */
    public static final String OAUTH = "extend.oauth";
    /**
     * 文件在线预览
     */
    public static final String PREVIEW = "extend.preview";
    /**
     * 数据脱敏
     */
    public static final String SENSITIVE = "extend.sensitive";
    /**
     * websocket
     */
    public static final String WEBSOCKET = "extend.websocket";
    /**
     * 存储设置
     */
    public static final String STORAGE = "extend.storage";
    /**
     * 存储设置
     */
    public static final String SWAGGER = "extend.swagger";
    /**
     * XSS/重复读取失效
     */
    public static final String WRAPPER = "extend.wrapper";

}
