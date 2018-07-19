package com.alucn.weblab.selenium;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JiraSelenium {
	
	public static Logger logger = Logger.getLogger(JiraSelenium.class);
	/**
	 * <pre>
	 * Example: JiraSelenium.login(driver,"https://greenhopper.app.alcatel-lucent.com/login.jsp");
	 * Description: login the system
	 * Arguments: driver;url
	 * Return: void
	 * Variable：none
	 * </pre>
	 */
    public static void login(WebDriver driver,String url) throws NoSuchElementException {
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-form-username")));
        //driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS); 
        
        
    	driver.findElement(By.id("login-form-username")).click();
    	driver.findElement(By.id("login-form-username")).sendKeys("lkhuang");
    	
    	driver.findElement(By.id("login-form-password")).click();
    	driver.findElement(By.id("login-form-password")).sendKeys("@WSXxdr5");
    	
    	driver.findElement(By.id("login-form-submit")).click();
		
		logger.info("================login success!!================");
    	
	}
    /**
     * <pre>
     * Example: JiraSelenium.getIssues(driver,"https://greenhopper.app.alcatel-lucent.com/issues/?filter=54758");
     * Description: Get the intermediate variable,For splicing urls
     * Arguments: driver;url
     * Return: ArrayList<String>
     * Variable：none
     * </pre>
     * @throws InterruptedException,NoSuchElementException 
     */
    public static ArrayList<String> getIssues(WebDriver driver,String url) throws InterruptedException,NoSuchElementException {
    	ArrayList<String> targetUrls = new ArrayList<String>();
    	driver.get(url);
    	//driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    	/*List<WebElement> findElements = driver.findElements(By.className("issuerow"));
    	for(WebElement element :findElements) {
    		
    		WebElement status = element.findElement(By.className("status"));
    		String text = status.getText();
    		if("RESOLVED".equals(text)) {
				continue;
			}else if ("CLOSED".equals(text)) {
				continue;
			}else {
				logger.info("text:=========="+text);
				String attribute = element.getAttribute("data-issuekey");
				targetUrls.add(attribute);
			}
    		//logger.info(attribute);
    	}*/
    	issuerowInfo(driver,targetUrls);
    	String totalCount = driver.findElement(By.className("results-count-total")).getText();
    	int total = Integer.parseInt(totalCount);
    	int i = total/50+1;
    	if(i>1) {
    		int j = (i-1)*50;
    		String url2 = "https://greenhopper.app.alcatel-lucent.com/issues/?filter=54758&startIndex="+j;
    		driver.get(url2);
        	issuerowInfo(driver,targetUrls);
        	/*List<WebElement> findElements2 = driver.findElements(By.className("issuerow"));
        	for(WebElement element :findElements2) {
        		WebElement status = element.findElement(By.className("status"));
        		String text = status.getText().trim();
        		
        		if("RESOLVED".equals(text)) {
        			continue;
        		}else if ("CLOSED".equals(text)) {
        			continue;
				}else {
        			logger.info("text:=========="+text);
        			String attribute = element.getAttribute("data-issuekey");
        			targetUrls.add(attribute);
				}
        	}*/
    		//logger.info(url2);
    		i--;
    	}
    	
    	/*File file = new File("d:/jira2.txt");  
        PrintStream ps = new PrintStream(new FileOutputStream(file));  
        ps.println(targetUrls);*/
    	logger.info("targetUrls:======================="+targetUrls);
    	return targetUrls;
    }
    
    public static ArrayList<String> issuerowInfo(WebDriver driver,ArrayList<String> targetUrls) throws InterruptedException,NoSuchElementException{
	    	WebDriverWait wait = new WebDriverWait(driver, 10);
	        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[starts-with(@id,'issuerow')]")));
	        //driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS); 
	        
	        /*File file = new File("d:/jira6.txt");  
	        PrintStream ps = new PrintStream(new FileOutputStream(file));  
	        ps.println(driver.getPageSource());*/
	        
	    	//List<WebElement> findElements = driver.findElements(By.className("issuerow"));
        
			Thread.sleep(3000);
		
        logger.info("=============find issuerow start================");
    	//List<WebElement> findElements = driver.findElements(By.xpath("//tr[starts-with(@id,'issuerow')]"));
    	List<WebElement> findElements = driver.findElements(By.className("issuerow"));
    	logger.info("findElements:===="+findElements.size());
    	for(WebElement element :findElements) {
    		 WebElement status = element.findElement(By.className("status"));
    		 String text = status.getText();
    		 if(!"RESOLVED".equals(text)&&!"CLOSED".equals(text)) {
    			//logger.info("text:=========="+text);
				String attribute = element.getAttribute("data-issuekey");
				targetUrls.add(attribute);
    		 }
    	}
		return targetUrls;
    }
    /**
     * <pre>
     * Example: JiraSelenium.getIssuesInfo(driver,"SUREPAYRD-17687");
     * Description: Gets the specified element 
     * Arguments: driver;target
     * Return: Map<String,Object>
     * Variable：none
     * </pre>
     */
    public static Map<String ,Object> getIssuesInfo(WebDriver driver,String target) {
    	Map<String ,Object> result  = new HashedMap(); 
    	//result.put("target",target);
    	String url = "https://greenhopper.app.alcatel-lucent.com/browse/"+target;
    	driver.get(url);
    	
    	/*String pageSource = driver.getPageSource();
    	File file = new File("d:/jira3.txt");  
        PrintStream ps = new PrintStream(new FileOutputStream(file));  
        ps.println(pageSource);*/
    	//logger.info("url:======="+url);
    	//logger.info(url);
    	//driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    	try {
    		WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("customfield_13641-val")));
			//driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS); 
			//Thread.sleep(3000);
            
            WebElement caseStatus =  driver.findElement(By.id("customfield_13641-val"));
	    	//logger.info(caseStatus.getText());
	    	result.put("caseStatus", caseStatus.getText().trim());
	    	
	    	WebElement Feature = driver.findElement(By.id("customfield_13636-val"));
	    	//logger.info(Feature.getText());
	    	result.put("Feature", Feature.getText().trim());
	    	
	    	WebElement Assignee = driver.findElement(By.id("assignee-val"));
	    	result.put("Assignee", Assignee.getText().trim());
	    	
	    	List<WebElement> labels = driver.findElements(By.cssSelector("#wrap-labels .labels"));
	    	List labelList = new ArrayList<>();
	    	for (WebElement webElement : labels) {
	    		List<WebElement> caseListA = webElement.findElements(By.className("lozenge"));
	    		for (WebElement webElement2 : caseListA) {
	    			//logger.info("webElement2.getText().trim():+=================="+webElement2.getText().trim());
	    			labelList.add(webElement2.getText().trim()==null?"":webElement2.getText().trim());
				}
	    		//logger.info("labelList.toString():============="+labelList.toString());
	    		result.put("labels", labelList.toString().replace("[","").replace("]", ""));
			}
	    	//List<WebElement> caseLists = driver.findElements(By.cssSelector("#customfield_13599-1316992-value > li"));
	    	//List<WebElement> caseLists = driver.findElements(By.cssSelector("#customfield_13599-1268377-value > li"));
	    	//List<WebElement> caseLists = driver.findElements(By.cssSelector("ul[id ^='customfield_13599'])"));
	    	List<WebElement> caseListsUl = driver.findElements(By.xpath("//ul[starts-with(@id,'customfield_13599')]"));
	    	List caselist = new ArrayList<>();
	    	for (WebElement webElement : caseListsUl) {
	    		List<WebElement> caseListA = webElement.findElements(By.className("lozenge"));
	    		for (WebElement webElement2 : caseListA) {
	    			String text = webElement2.getText().trim().replace(",", "");
	    			//logger.info(text);
	    			caselist.add(text);
				}
	    		/*logger.info(webElement.getAttribute("title"));
	    		caselist.add(webElement.getAttribute("title"));*/
	    	}
	    	//logger.info("caselist:======="+caselist);
	    	result.put("caselist", caselist);
    	} catch (Exception e) {
    		e.printStackTrace();
    		logger.info("null target:==-=-=-=-=-="+target);
    		driver.quit();
    		return null;
		}	
    	return result;
    }
    public static void getIssuesInfoUrl(WebDriver driver,String target) throws InterruptedException, FileNotFoundException {
    	String url = "https://greenhopper.app.alcatel-lucent.com/browse/"+target;
    	driver.get(url);
    }
    public static void setComment(WebDriver driver,String target) throws FileNotFoundException, InterruptedException {
    	String url = "https://greenhopper.app.alcatel-lucent.com/browse/"+target;
    	driver.get(url);
    	
    	WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("footer-comment-button")));
        
        
        String rurl = driver.findElement(By.id("footer-comment-button")).getAttribute("href");
    	driver.get(rurl);
    	
    	System.out.println(driver.getCurrentUrl());
    	//driver.switchTo().frame("mce_0_ifr");
    	String pageSource = driver.getPageSource();
    	File file = new File("d:/jira9.txt");  
        PrintStream ps = new PrintStream(new FileOutputStream(file));  
        ps.println(pageSource);
    	/*id:1335901
    	comment:
    	commentLevel:
    	atl_token:B28N-N0OU-TOAR-EWV5|78fd97c89a965a1f6fd55cf35becae6bf80491ec|lin
    	Add:Add*/
    	
    	/*wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tinymce")));
    	driver.findElement(By.cssSelector("#tinymce > p")).sendKeys("test3 for post");
    	driver.findElement(By.id("issue-comment-add-submit")).click();*/
    	//System.out.println(rurl);
    	//wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tinymce")));
        /*driver.findElement(By.cssSelector("#tinymce > p")).click();
    	driver.findElement(By.cssSelector("#tinymce > p")).sendKeys("test3 for post");
    	JavascriptExecutor jse = (JavascriptExecutor) driver ;
    	jse.executeScript("");
    	driver.findElement(By.id("issue-comment-add-submit")).click();*/
    	
    	
    	
    	/*JavascriptExecutor jse = (JavascriptExecutor) driver ;

        try {
        	String script =
        			"function setHeader(){\r\n" + 
        			"		var xmlhttp=new XMLHttpRequest();\r\n" + 
        			"		xmlhttp.open(\"POST\",\"https://greenhopper.app.alcatel-lucent.com/rest/api/2/issue/SUREPAYRD-19904/comment\");\r\n" + 
        			"		xmlhttp.setRequestHeader(\"Content-type\",\"application/json\");\r\n" + 
        			"		return xmlhttp.responseText; \r\n" + 
        			"}\r\n" + 
        			"function httpPost(URL, PARAMS) {\r\n" + 
        			"	 setHeader();\r\n" + 
        			"    var temp = document.createElement(\"form\");\r\n" + 
        			"    temp.action = URL;\r\n" + 
        			"    temp.method = \"post\";\r\n" + 
        			"    temp.style.display = \"none\";\r\n" + 
        			"\r\n" + 
        			"    for (var x in PARAMS) {\r\n" + 
        			"        var opt = document.createElement(\"textarea\");\r\n" + 
        			"        opt.name = x;\r\n" + 
        			"        opt.value = PARAMS[x];\r\n" + 
        			"        temp.appendChild(opt);\r\n" + 
        			"    }\r\n" + 
        			"\r\n" + 
        			"    document.body.appendChild(temp);\r\n" + 
        			"    temp.submit();\r\n" + 
        			"\r\n" + 
        			"    return temp;\r\n" + 
        			"}\r\n" + 
        			"var params = {\r\n" + 
        			"        \"body\":'test3'\r\n" + 
        			"    };\r\n" + 
        			"httpPost(\"https://greenhopper.app.alcatel-lucent.com/rest/api/2/issue/SUREPAYRD-19904/comment\", params);"
        		 ;
        	String script2=
        			"function setHeader(){\r\n" + 
        			"		var xmlhttp=new XMLHttpRequest();\r\n" + 
        			"		xmlhttp.open(\"POST\",\"https://greenhopper.app.alcatel-lucent.com/rest/api/2/issue/SUREPAYRD-19904/comment\",\"true\");\r\n" + 
        			"		xmlhttp.setRequestHeader(\"Content-type\",\"application/json\");\r\n" + 
        			"		xmlhttp.send(\"body=test3\");\r\n" + 
        			"		return xmlhttp.responseText; \r\n" + 
        			"}\r\n" + 
        			"setHeader()";
        	System.out.println(script2);
            String resp = (String) jse.executeScript(script2);
            System.out.println(resp);

        } catch (Exception e) {
            //.......... Exception 
        	e.printStackTrace();
        }*/
    }
    static final String HOST = "135.251.33.15";
    static final String PORT = "80";
    static final String phantomjs ="D:\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe";
    public static void main(String[] args) {
    	System.setProperty("phantomjs.binary.path", phantomjs);
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        ArrayList<String> cliArgsCap = new ArrayList<>();
        cliArgsCap.add("--proxy=http://"+HOST+":"+PORT);
        cliArgsCap.add("--load-images=no");
        //cliArgsCap.add("--disk-cache=yes");
        cliArgsCap.add("--ignore-ssl-errors=true");
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);

        WebDriver driver = new PhantomJSDriver(capabilities);

        JiraSelenium.login(driver,"https://greenhopper.app.alcatel-lucent.com/login.jsp");
        //getIssuesInfo(driver,"SUREPAYRD-17687");
        try {
			setComment(driver,"SUREPAYRD-19904");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
