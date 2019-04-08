package com.alucn.weblab.xmlconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alucn.weblab.interceptors.LoginInterceptors;

@Configuration
@EnableWebMvc //相当于 <mvc:annotation-driven/>
@ComponentScan(basePackages={"com.alucn.weblab.controller"})
public class WebConfig extends WebMvcConfigurerAdapter {
	
	//  配置默认的defaultServlet处理
    // <mvc:default-servlet-handler/>
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // 配置静态资源处理
        configurer.enable("default");//对静态资源的请求转发到容器缺省的servlet，而不使用DispatcherServlet
    }
    /**
     * 这是定义一个ParameterizableViewController调用时立即转到视图的快捷方式。
     * 如果在视图生成响应之前没有Java控制器逻辑要执行，则在静态情况下使用它。
     * <mvc:view-controller path="/" view-name="home"/>
     * @param registry
     */
	@Override  
    public void addViewControllers(ViewControllerRegistry registry){  
        registry.addViewController("/login").setViewName("login");  
	}  
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptors()).addPathPatterns("/**");
	}
}
