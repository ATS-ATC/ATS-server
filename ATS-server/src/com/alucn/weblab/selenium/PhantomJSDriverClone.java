package com.alucn.weblab.selenium;

import java.io.Serializable;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;

public class PhantomJSDriverClone extends PhantomJSDriver implements Serializable {

	private static final long serialVersionUID = -6013187191856714207L;

	public PhantomJSDriverClone() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PhantomJSDriverClone(Capabilities desiredCapabilities) {
		super(desiredCapabilities);
		// TODO Auto-generated constructor stub
	}

	public PhantomJSDriverClone(PhantomJSDriverService service, Capabilities desiredCapabilities) {
		super(service, desiredCapabilities);
		// TODO Auto-generated constructor stub
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		// TODO Auto-generated method stub
		return super.getScreenshotAs(target);
	}

	@Override
	public Object executePhantomJS(String script, Object... args) {
		// TODO Auto-generated method stub
		return super.executePhantomJS(script, args);
	}

/*	@Override
	public Object clone() throws CloneNotSupportedException {
		PhantomJSDriverClone pClone = null;
		try {
			pClone=(PhantomJSDriverClone) super.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pClone;
	}
*/	
}
