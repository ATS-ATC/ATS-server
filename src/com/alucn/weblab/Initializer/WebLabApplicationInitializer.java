package com.alucn.weblab.Initializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.alucn.casemanager.server.listener.MainListener;
import com.alucn.weblab.constants.Constants;
import com.alucn.weblab.xmlconfig.AppConfig;
import com.alucn.weblab.xmlconfig.WebConfig;

/**
 * @author haiqiw
 * 2017年7月13日 上午9:18:35
 * desc:WebLabApplicationInitializer
 */
public class WebLabApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) {
		addServlet(container);
		createWebContext(WebConfig.class, AppConfig.class);
		initServer(container);
	}

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("login");
	}

	private void addServlet(ServletContext servletContext) {
		ServletRegistration.Dynamic registration = servletContext.addServlet(Constants.SERVLET_NAME,
				new DispatcherServlet());
		registration.setLoadOnStartup(1);
		registration.addMapping("*.do");
	}

	private AnnotationConfigWebApplicationContext createWebContext(Class<?>... annotatedClasses) {
		AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
		webContext.register(annotatedClasses);
		return webContext;
	}

	public void initServer(ServletContext servletContext) {
//		String configPath = System.getenv("WEBLAB_CONF");
		String [] args = {servletContext.getRealPath("conf")};
//		String[] args = { configPath };
		MainListener.init(args);
	}
}
