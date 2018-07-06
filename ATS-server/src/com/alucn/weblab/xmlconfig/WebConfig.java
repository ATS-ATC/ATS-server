package com.alucn.weblab.xmlconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alucn.weblab.interceptors.LoginInterceptors;

/**
 * @author haiqiw
 * 2017年6月5日 下午2:11:33
 * desc:weblab-servlet.xml
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages={"com.alucn.weblab.controller"})
public class WebConfig extends WebMvcConfigurerAdapter {
	
	@Override  
    public void addViewControllers(ViewControllerRegistry registry){  
        registry.addViewController("/login").setViewName("login");  
	}  
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptors()).addPathPatterns("/**");
	}
}
