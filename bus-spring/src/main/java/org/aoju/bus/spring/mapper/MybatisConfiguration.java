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
package org.aoju.bus.spring.mapper;


import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.utils.StringUtils;
import org.aoju.bus.pager.plugin.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * mybatis 配置类
 *
 * @author Kimi Liu
 * @version 3.0.0
 * @since JDK 1.8
 */
@EnableConfigurationProperties(value = {MybatisProperties.class})
public class MybatisConfiguration {

    @Autowired
    MybatisProperties properties;

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
        try {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            if (StringUtils.isNotBlank(this.properties.getTypeAliasesPackage())) {
                bean.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
            }
            PageInterceptor interceptor = new PageInterceptor();
            Properties properties = new Properties();
            properties.setProperty("autoDelimitKeywords", this.properties.getAutoDelimitKeywords());
            properties.setProperty("reasonable", this.properties.getReasonable());
            properties.setProperty("supportMethodsArguments", this.properties.getSupportMethodsArguments());
            properties.setProperty("returnPageInfo", this.properties.getReturnPageInfo());
            properties.setProperty("params", this.properties.getParams());
            interceptor.setProperties(properties);

            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Interceptor[] plugins = new Interceptor[]{
                    interceptor,
                    new PerformanceHandler(),
                    new SQLExplainHandler()};
            bean.setPlugins(plugins);

            bean.setMapperLocations(resolver.getResources(this.properties.getXmlLocation()));
            return bean.getObject();
        } catch (Exception e) {
            throw new InstrumentException(e);
        }
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}