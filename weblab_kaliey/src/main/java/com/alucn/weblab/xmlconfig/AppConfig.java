package com.alucn.weblab.xmlconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.alucn.weblab.controller"})
public class AppConfig {
	
	
	
	/*//    配置jsp视图解析器
	 * @Bean
    public ViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver viewResolver;
        viewResolver = new InternalResourceViewResolver();
        viewResolver.setOrder(2);//设置优先级
        viewResolver.setPrefix("/jsp/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setViewClass(JstlView.class);
        //for debug envirment
        viewResolver.setCache(false);
        return viewResolver;
    }*/
}
