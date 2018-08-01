package com.alucn.weblab.selenium;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
	 * @throws IOException 
	 */
    public static void login(WebDriver driver,String url) throws NoSuchElementException, IOException {
    	
    	String string = Thread.currentThread().getContextClassLoader().getResource("").getPath(); 
    	System.out.println(string);
    	int num=string.indexOf("WEB-INF");
    	String wpath=string.substring(1,num)+"/conf";
    	//int num=string.indexOf("target");                      //java启动
    	//String wpath=string.substring(1,num)+"/WebContent/conf";//java启动
    	//System.out.println(path);
    	Properties properties = new Properties();
    	String configPath = System.getenv("WEBLAB_CONF");
    	String path = configPath==null?wpath+"/jira.properties":configPath+"/jira.properties";
    	BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
    	properties.load(bufferedReader);
    	String jiraId = properties.getProperty("jiraId");
    	String jiraPass = properties.getProperty("jiraPass");
    	//System.out.println(jiraId+":"+jiraPass);
    	
    	
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-form-username")));
        //driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS); 
        
        
    	driver.findElement(By.id("login-form-username")).click();
    	//driver.findElement(By.id("login-form-username")).sendKeys("lkhuang");
    	driver.findElement(By.id("login-form-username")).sendKeys(jiraId);
    	
    	driver.findElement(By.id("login-form-password")).click();
    	//driver.findElement(By.id("login-form-password")).sendKeys("@WSXxdr5");
    	driver.findElement(By.id("login-form-password")).sendKeys(jiraPass);
    	
    	driver.findElement(By.id("login-form-submit")).click();
		
		logger.info("================login action done!!================");
    	
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
			
			/*File file = new File("d:/jira-version.txt");  
	        PrintStream ps = new PrintStream(new FileOutputStream(file));  
	        ps.println(driver.getPageSource());*/
	        
            WebElement caseStatus =  driver.findElement(By.id("customfield_13641-val"));
	    	//logger.info(caseStatus.getText());
	    	result.put("caseStatus", caseStatus.getText().trim());
	    	
	    	WebElement Feature = driver.findElement(By.id("customfield_13636-val"));
	    	//logger.info(Feature.getText());
	    	result.put("Feature", Feature.getText().trim());
	    	
	    	WebElement Assignee = driver.findElement(By.id("assignee-val"));
	    	result.put("Assignee", Assignee.getText().trim());
	    	
	    	WebElement versions = driver.findElement(By.id("versions-field"));
	    	String vHtml = versions.getAttribute("innerHTML").trim();
	    	int indexOf = vHtml.indexOf(">")+1;
	    	int lastIndexOf = vHtml.lastIndexOf("<");
	    	String substring = vHtml.substring(indexOf, lastIndexOf);//.replace(">", "")
	    	//System.out.println(substring);
	    	result.put("Versions", substring);
	    	
	    	
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
    public static boolean setComment(WebDriver driver,String target,String msg) throws FileNotFoundException, InterruptedException {
    	String url = "https://greenhopper.app.alcatel-lucent.com/browse/"+target;
    	driver.get(url);
    	Thread.sleep(3000);
    	WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("footer-comment-button")));
        String rurl = driver.findElement(By.id("footer-comment-button")).getAttribute("href");
    	driver.get(rurl);
    	logger.info(driver.getCurrentUrl());
    	JavascriptExecutor jse = (JavascriptExecutor) driver ;
    	String script = "$(\"#comment\").val(\""+msg+"\");\r\n" + 
    			"$(\"#comment-add-submit\").trigger(\"click\");";
    	jse.executeScript(script);
    	//logger.info(driver.getCurrentUrl().equals(rurl));
    	
    	for(int i =0;i<10;i++) {
    		if(driver.getCurrentUrl().equals(rurl)) {
        		Thread.sleep(1000);
        	}else {
        		break;
        	}
    	}
    	//logger.info(driver.getCurrentUrl().equals(rurl));
    	logger.info(driver.getCurrentUrl());
    	boolean flag = false;
    	if(!driver.getCurrentUrl().equals(rurl)) {
    		flag=true;
    	}
    	return flag;
    }
    public static void testTrigger(WebDriver driver) throws FileNotFoundException, InterruptedException {
    	String url = "http://135.242.16.163:8080/weblab/userLogin.do";
    	driver.get(url);
    	//System.out.println("快拔网线！");
    	Thread.sleep(5000);
    	System.out.println(driver.getCurrentUrl());
    	if(!"about:blank".equals(driver.getCurrentUrl())) {
    		logger.info("network work");
    		System.out.println("network work");
    		JavascriptExecutor jse = (JavascriptExecutor) driver ;
    		/*$("#userName").val("Administrator");
	    	$("#passWord").val("Admin");
	    	$("#submit").trigger("click");*/
    		String script = 
    				"$(\"#userName\").val(\"Administrator\");\r\n" + 
    				"$(\"#passWord\").val(\"Admin\");\r\n" + 
    				"$(\"#submit\").trigger(\"click\");";
    		jse.executeScript(script);
    		Thread.sleep(5000);
    		System.out.println(driver.getCurrentUrl());
    	}else {
			logger.info("network error");
			System.out.println("network error");
		}
    }
    static final String HOST = "135.251.33.15";
    static final String PORT = "80";
    static final String phantomjs ="D:\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe";
    public static void main(String[] args) throws NoSuchElementException, IOException {
    	System.setProperty("phantomjs.binary.path", phantomjs);
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        capabilities.setJavascriptEnabled(true);
        ArrayList<String> cliArgsCap = new ArrayList<>();
        cliArgsCap.add("--proxy=http://"+HOST+":"+PORT);
        cliArgsCap.add("--load-images=no");
        //cliArgsCap.add("--disk-cache=yes");
        //cliArgsCap.add("--ignore-ssl-errors=true");
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
        WebDriver driver = new PhantomJSDriver(capabilities);
        
        
        JiraSelenium.login(driver,"https://greenhopper.app.alcatel-lucent.com/login.jsp");
        Map<String, Object> issuesInfo = getIssuesInfo(driver,"SUREPAYRD-19620");
        System.out.println(issuesInfo);
        try {
			//setComment(driver,"SUREPAYRD-19904","test return2");
        	//testTrigger(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//=========================================================================================
    	//String string = JiraSelenium.class.getResource("/").toString();
    	/*String string = Thread.currentThread().getContextClassLoader().getResource("").getPath(); 
    	int num=string.indexOf("target");
    	String wpath=string.substring(1,num)+"WebContent/conf";
    	//System.out.println(path);
    	Properties properties = new Properties();
    	String configPath = System.getenv("WEBLAB_CONF");
    	String path = configPath==null?wpath+"/jira.properties":configPath+"/jira.properties";
    	BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
    	properties.load(bufferedReader);
    	String jira_id = properties.getProperty("jiraId");
    	String jira_pass = properties.getProperty("jiraPass");
    	System.out.println(jira_id+":"+jira_pass);*/
    	//=========================================================================================
	}
}
