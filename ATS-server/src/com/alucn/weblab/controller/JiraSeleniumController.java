package com.alucn.weblab.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alucn.weblab.selenium.JiraSelenium;
import com.alucn.weblab.service.JiraSeleniumService;



@Controller
public class JiraSeleniumController {
	
	public static Logger logger = Logger.getLogger(JiraSeleniumController.class);

    static final String HOST = "135.251.33.15";
    static final String PORT = "80";
    //static final String phantomjs ="D:\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe";
    //static final String phantomjs ="/opt/phantomjs/bin/phantomjs";
    
    static boolean Flag = false;
    static boolean running =false;

    @Autowired(required=true)
    private JiraSeleniumService jiraSeleniumService;
    
    @RequestMapping(value="/statusSetJiraCaseTbl")
    @ResponseBody
    public String statusSetJiraCaseTbl(){
    	
    	String msg="on-off flag : "+Flag
    			+"<br> is running flag : "+running;
    	
    	return msg;
    }
    @RequestMapping(value="/testSetJiraCaseTbl")
    @ResponseBody
    public String testSetJiraCaseTbl(){
    	try {
			jiraSeleniumService.testTempJiraTbl();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return "";
    }
    
    
    
    @RequestMapping(value="/stopSetJiraCaseTbl")
    @ResponseBody
    public String stopSetJiraCaseTbl(){
    	
    	Flag=false;
    	
    	return "turn off";
    }
    
    @RequestMapping(value="/loopSetJiraCaseTbl")
    @ResponseBody
    public String loopSetJiraCaseTbl() {
    	if(running) {
    		return "running";
    	}
    	running=true;
    	Flag=true;
    	while(true) {
    		if (!Flag) {
				break;
			}
    		try {
	    		logger.info("==================loopSetJiraCaseTbl running!!!==================");
	    		Thread.sleep(1000*60*1);
	    		logger.info("==================loopSetJiraCaseTbl start!!!==================");
				this.setJiraCaseTbl();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
    		
    	}
    	logger.info("loopSetJiraCaseTbl stop!!!");
    	return "close";
    }

    //http://localhost:8080/weblab/setJiraCaseTbl.do
    @RequestMapping(value="/setJiraCaseTbl")
    @ResponseBody
    public String setJiraCaseTbl() throws Exception {
    	running = true;
    	
    	long startMili=System.currentTimeMillis(); 
    	String configPath = System.getenv("WEBLAB_CONF");
    	String phantomjs;
    	if(configPath==null) {
    		phantomjs ="D:\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe";
    	}else {
    		phantomjs ="/opt/phantomjs/bin/phantomjs";
		}
        System.setProperty("phantomjs.binary.path", phantomjs);
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        ArrayList<String> cliArgsCap = new ArrayList<>();
        cliArgsCap.add("--proxy=http://"+HOST+":"+PORT);
        cliArgsCap.add("--load-images=no"); 
        cliArgsCap.add("--disk-cache=no"); 
        cliArgsCap.add("--ignore-ssl-errors=true"); 
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);

        WebDriver driver = new PhantomJSDriver(capabilities);

        JiraSelenium.login(driver,"https://greenhopper.app.alcatel-lucent.com/login.jsp");
        Thread.sleep(100);
        Set<Cookie> cookies= driver.manage().getCookies();
        //System.out.println("cookie:====="+cookies);
        
        ArrayList<String> issues = JiraSelenium.getIssues(driver,"https://greenhopper.app.alcatel-lucent.com/issues/?filter=54758");

        Map iMap = new HashedMap();
        /*if(issues.size()>0) {
			for (String target : issues) {
			Map<String, Object> issuesInfo = JiraSelenium.getIssuesInfo(driver,target);
			iMap.put(target, issuesInfo);
			}
		}*/
        
        ExecutorService executor = Executors.newCachedThreadPool();
        
        //int count = issues.size();
        int count = 10;
        //System.out.println("count:======="+count);
        Future[] futures = new Future[count];
        for (int i = 0; i < count; i++) {
            futures[i]= executor.submit(new Callable<Map<String,Object>>() {
                public Map<String,Object> call() throws InterruptedException, CloneNotSupportedException, IOException, ClassNotFoundException {
                Map<String,Object> aMap = new HashMap<>();
                String target = null;
                  while(true) {
                     if(issues.size()>0) {
                         synchronized (JiraSeleniumController.class) {
                             for (int i=0 ;i<issues.size();i++) {
                                 target=issues.get(i);
                                 issues.remove(i);
                                 break;
                             }
                         }
                         
                        WebDriver ddriver = new PhantomJSDriver(capabilities);
                        ddriver.get("https://greenhopper.app.alcatel-lucent.com");
                        ddriver.manage().deleteAllCookies();
                        for (Cookie loadedCookie : cookies) {
                    	    //System.out.println(String.format("%s -> %s", loadedCookie.getName(), loadedCookie.getValue()));
                    	    JavascriptExecutor js = (JavascriptExecutor) ddriver;
                        	js.executeScript("document.cookie = \""+loadedCookie.getName()+"="+loadedCookie.getValue()+";path=/;domain=greenhopper.app.alcatel-lucent.com\"");    
                        }
                        Map<String, Object> issuesInfo = JiraSelenium.getIssuesInfo(ddriver,target);
                        //System.out.println(Thread.currentThread().getName()+"=========:issuesInfo:========"+issuesInfo);
                        logger.info(Thread.currentThread().getName()+":issuesInfo:========"+issuesInfo);
                        aMap.put(target, issuesInfo);
                        ddriver.quit();
                     }else {
                         break;
                     }
                 }
                 return aMap;
             }});
            //System.out.println("next task...");
        }
        try {
            for (Future<Map<String, Object>> result : futures) {
                if(result.get().size()>0) {
                    Set<String> keySet = result.get().keySet();
                    for (String string : keySet) {
                        //System.out.println("key:="+string+" value:="+result.get().get(string));
                        iMap.put(string, result.get().get(string));
                    }
                }
            }
            //System.out.println(iMap);
            logger.info(iMap);
            //System.out.println("------------------------------------");
        }finally {
        	driver.quit();
            executor.shutdown();
        }

        jiraSeleniumService.setTempJiraTbl(iMap);

        driver.quit();
        long endMili=System.currentTimeMillis();
        running = false;
        logger.info("cost : "+formatTime(endMili-startMili));
        return formatTime(endMili-startMili);
    }
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
        
        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+" d ");
        }
        if(hour > 0) {
            sb.append(hour+" h ");
        }
        if(minute > 0) {
            sb.append(minute+" m ");
        }
        if(second > 0) {
            sb.append(second+" s ");
        }
        if(milliSecond > 0) {
            sb.append(milliSecond+" ms ");
        }
        return sb.toString();
    }
}