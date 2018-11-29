<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<script src="./highcharts/highcharts.js"></script>
<script src="./highcharts/modules/exporting.js"></script>
<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet">
<!-- <script src="./js/echarts.js"></script>
<script src="./js/echarts-gl.js"></script> -->
<!-- <meta http-equiv="refresh" content="5*60"> -->
<title>Nokia Corporation</title>
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="bookmark" href="images/favicon.ico" type="image/x-icon" />
<script type="text/javascript">
	$(function() {
		var height=document.documentElement.clientHeight;
		var head = $("#head").height();
		var hidemenu = $("#hide_left_menu").height();
		var home = $("#home").height();
		//alert("head:=="+head)//50
		//alert("height:==="+height)//648
		//alert("hide_left_menu:==="+hidemenu)//28
		//alert("home:==="+home)//32*7
		
		//七级目录，单位长度*菜单数量7
		var fill = head+hidemenu+(home*7)+14;
		
		document.getElementById('main-info').style.height=height-head+'px';
		//document.getElementById('fill').style.height=height-316+'px';
		document.getElementById('fill').style.height=height-fill+'px';
		
		$("#config-manage").click(function(){
			var active = $("#config-info").is(".in");
			if(active){
				//$("#fill").css("height",height-316+'px');
				$("#fill").css("height",height-fill+'px');
				//document.getElementById('fill').style.height=height-316+32+'px';
			}else{
				//document.getElementById('fill').style.height=height-316-32+'px';
				//二级菜单展开fill+展开长度:单位长度*二级菜单数量4
				var fill2 = fill + (home*4);
				
				$("#fill").css("height",height-fill2+'px');
			}
		}); 
	});
	function menuClick(menuUrl) {
		$("#main-info").attr('src',menuUrl+"?time="+new Date().getTime());
		/* var flag = $("#home_hidden").attr("hidden");
		alert(flag!=null)
		if(flag!=null){
			$("#home_hidden").attr("hidden","hidden");
			$("#main-info").attr('src',menuUrl); 
		}else{
			$("#main-info").attr('src',menuUrl); 
			$('#left-menu').hide();
			$("#home_hidden").removeAttr('hidden');
		} */
	}
	
	$(document).ready(function () {
		<%-- var auth ="<%=session.getAttribute("auth").toString()%>";
		//管理员用户可以访问任意菜单：
		if("all"==auth){
			
		}
		//普通用户不能访问的菜单：
		else if("errorCases"==auth){
			$("#case-search").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#ats-servers").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#spa-and-rtdb").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#config-manage").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#config-manage_h").attr('onclick', "alert('The current user does not have access!');return false;");
		}
		//非正常登陆的所有菜单都不可以访问：
		else{
			$("#case-search").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#ats-servers").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#ats-servers_h").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#spa-and-rtdb").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#config-manage").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#case-status").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#case-status_h").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#error-cases").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#error-cases_h").attr('onclick', "alert('The current user does not have access!');return false;");
		} --%>
		
		$('ul.nav > li').click(function (e) {
			//e.preventDefault();
			$('ul.nav > li').removeClass('active');
			$(this).addClass('active');
		});
		
		$('#hide_left_menu').click(function () {
			//alert(1)
			$('#left-menu').hide();
			$("#home_hidden").removeAttr('hidden');
			$('#right-info').attr('class','col-md-12');
			$('#home_right').css('padding-left','30px');
			$('#main-info').attr('src', $('#main-info').attr('src'));
			var height=document.documentElement.clientHeight;
			document.getElementById('fill_hidden').style.height=height-303+'px';
		});
		$('#show_left_menu').click(function(){
			$('#left-menu').show();
			$('#home_hidden').attr('hidden','hidden');
			$('#right-info').attr('class','col-md-10');
			$('#home_right').css('padding-left','0');
			$('#main-info').attr('src', $('#main-info').attr('src'));
		});
		$('#home_hidden').click(function(){
			$('#left-menu').show();
			$('#home_hidden').attr('hidden','hidden');
			$('#right-info').attr('class','col-md-10');
			$('#home_right').css('padding-left','0');
			$('#main-info').attr('src', $('#main-info').attr('src'));
		});
	});
</script>
<style type="text/css">
	.btn-default:hover,
	.btn-default:active,
	.btn-default.active,
	.open > .dropdown-toggle.btn-default 
	.btn-default:active:hover,
	.btn-default.active:hover,
	.open > .dropdown-toggle.btn-default:hover,
	.btn-default:active:focus,
	.btn-default.active:focus,
	.open > .dropdown-toggle.btn-default:focus,
	.btn-default:active.focus,
	.btn-default.active.focus,
	.open > .dropdown-toggle.btn-default.focus
	{
	  color: #333;
	  background-color: #37424F;
	}
	
	.nav > li > a:hover,
	.nav > li > a:focus {
	  text-decoration: none;
	  background-color: #37424F;
	}
	.btn-default:active,
	.btn-default.active,
	.open > .dropdown-toggle.btn-default {
	  background-color: #0087B4;
	}
</style>
</head>
<body>
	<div class="container-fluid">
		<div class="row" ><!-- sytle="height:718px;" -->
		  	<div id="head" class="col-md-12" style="padding-left: 0px; padding-right: 0px;height: 50px;">
		  		<nav class="navbar navbar-default" role="navigation"> 
				    <div class="container-fluid"> 
				        <div class="navbar-header"> 
				            <a class="navbar-brand" href="${pageContext.request.contextPath}/userLoginBackHome.do"  style="padding-top: 1px;">
		  						<img style="display:inline;"  src="./images/logo.png" class="img-responsive" alt="Responsive image" >
							</a> 
							<a class="navbar-brand" id="show_left_menu">
		  					<font size="3" style="font-weight:bold;">SurePay Automation Case Management System</font><!-- SACMS -->
							</a>
				        </div> 
				        <ul class="nav navbar-nav navbar-right">
				        	<li>
				        		<a href="#"><span class="glyphicon glyphicon-bell"></span>&nbsp;<span class="badge">0</span></a>
					        	<!-- <form class="navbar-form navbar-left" role="search" action="">
						            <div class="form-group">
						                <input type="text" class="form-control" placeholder="Search">
						            </div>
						            <button type="submit" class="btn btn-default">Search</button>
						        </form> -->
					        </li>
							<li><a href="#"><span class="glyphicon glyphicon-user"></span> ${login }</a></li>
					        <li><a href="${pageContext.request.contextPath}/SACMS.chm"><span class="glyphicon glyphicon-book"></span> readme</a></li>
				            <li><a href="./userLogout.do"><span class="glyphicon glyphicon-log-in"></span> login out</a></li> 
				        </ul> 
				    </div> 
				</nav>
			</div>
			<div id="left-menu" class="col-md-2" style="padding-left: 0px; padding-right: 0px;background-color:#293038" >
					<div class="row">
						<div class="col-md-12" >
							<a><!-- id="hide_left_menu" -->
								<center id="hide_left_menu" style="background-color: rgb(66,81,95);height:28px;">
									<button type="button" class="btn btn-link btn-sm &nbsp;&nbsp;" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
										<span class="glyphicon glyphicon-menu-hamburger" aria-hidden="true"></span>
									</button>
								</center>
							</a>
						<ul class="nav nav-pills nav-stacked">
						  	<li id="home" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getWelcomeInfo.do')">
							  	<a href="#" style="padding-top: 0px;padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default  btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:132px; -->
										&nbsp;&nbsp;
										<span class="glyphicon glyphicon-home" aria-hidden="true"></span>
										<!-- <i class="icon-home"></i> -->
										&nbsp;&nbsp;&nbsp;&nbsp;Home
									</button>
								</a>
							</li>
						  	<li id="case-status" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getStatistics.do')">
							  	<a href="#" style="padding-top: 0px;padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:103px; -->
										&nbsp;&nbsp;
										<span class="glyphicon glyphicon-dashboard" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Dashboard
									</button>
								</a>
							</li>
						  	<li id="case-search" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/searchInfo.do')">
							  	<a href="#" style="padding-top: 0px;padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:108px; -->
										&nbsp;&nbsp;
										<span class="glyphicon glyphicon-play" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Run Case
									</button>
								</a>
							</li>
							<li id="ats-servers" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getServerInfo.do')"  >
							  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:93px; -->
										&nbsp;&nbsp;
										<span class="glyphicon glyphicon-object-align-bottom" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Lab Management
									</button>
								</a>
							</li>
							<li id="error-cases" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getErrorCaseInfo.do')" >
							  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:93px; -->
										&nbsp;&nbsp;
										<span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Bad Case Management
									</button>
								</a>
							</li>
							
							<li role="presentation" ><%-- onclick="menuClick('${pageContext.request.contextPath}/config.do')" --%>
							  	<a id="config-manage" href="#config-info" class="nav-header menu-first collapsed" data-toggle="collapse" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:93px; -->
										&nbsp;&nbsp;
										<span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Configuration
									</button>
								</a>
								<ul id="config-info" class="nav nav-list collapse menu-second" style="background-color: #42515F">
							        <li id="spa-and-rtdb" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/config.do')">
									  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
									  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:93px; -->
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-caret-right"></i>&nbsp;&nbsp; Certification Server
											</button>
										</a>
									</li>
							        <li id="spa-and-rtdb" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getSpaAndRtdbInfo.do')">
									  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
									  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:93px; -->
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-caret-right"></i>&nbsp;&nbsp; SPA & RTDB
											</button>
										</a>
									</li>
							        <li id="usermanagement" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getUserInfo.do')">
									  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
									  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:93px; -->
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-caret-right"></i>&nbsp;&nbsp; User Management
											</button>
										</a>
									</li>
							        <li id="deptmanagement" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getDeptInfo.do')">
									  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
									  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:93px; -->
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-caret-right"></i>&nbsp;&nbsp; Group Management
											</button>
										</a>
									</li>
							        <%-- <li id="rolesmanagement" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getRolesInfo.do')">
									  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
									  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:93px; -->
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-caret-right"></i>&nbsp;&nbsp; Role Management
											</button>
										</a>
									</li> --%>
							    </ul>
							</li>
						    
							<li id="releases" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getReleases.do')">
							  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default btn-block" style="color: white;text-decoration:none;border:none;outline:none;text-align: left;"><!-- padding-left:23px;padding-right:109px; -->
										&nbsp;&nbsp;
										<span class="glyphicon glyphicon-tag" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;SUnit Releases
									</button>
								</a>
							</li>
						  <li id="fill" role="presentation" ></li>
						  <!-- <li role="presentation" style="height:415px;"></li>style="height:365px;" -->
						</ul>
						
						</div>
					</div>
			</div>
			<div id="right-info" class="col-md-10" style="padding-left: 0px; padding-right: 0px;background-color:#ECEFF3" ><!-- style="height:500px" -->
				<div id="home_hidden" style="background-color: rgb(66,81,95);position: absolute;"hidden="hidden">
					<center style="background-color: rgb(66,81,95);height:30px;width: 30px;">
						<button type="button" class="btn btn-link btn-sm" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
							<span class="glyphicon glyphicon-menu-hamburger" aria-hidden="true"></span>
						</button>
					</center>
					<ul class="nav nav-pills nav-stacked">
						  	<li id="home_h" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getWelcomeInfo.do')">
							  	<center style="background-color: rgb(66,81,95);height:30px;width: 30px;">
									<button type="button" class="btn btn-link btn-sm" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
										<span class="glyphicon glyphicon-home" aria-hidden="true"></span>
									</button>
								</center>
							</li>
						  	<li id="case-status_h" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getStatistics.do')">
							  	<center style="background-color: rgb(66,81,95);height:30px;width: 30px;">
									<button type="button" class="btn btn-link btn-sm" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
										<span class="glyphicon glyphicon-dashboard" aria-hidden="true"></span>
									</button>
								</center>
							</li>
						  	<li id="case-search_h" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/searchInfo.do')">
							  	<center style="background-color: rgb(66,81,95);height:30px;width: 30px;">
									<button type="button" class="btn btn-link btn-sm" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
										<span class="glyphicon glyphicon-play" aria-hidden="true"></span>
									</button>
								</center>
							</li>
							<li id="ats-servers_h" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getServerInfo.do')"  >
							  	<center style="background-color: rgb(66,81,95);height:30px;width: 30px;">
									<button type="button" class="btn btn-link btn-sm" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
										<span class="glyphicon glyphicon-object-align-bottom" aria-hidden="true"></span>
									</button>
								</center>
							</li>
							<li id="error-cases_h" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getErrorCaseInfo.do')" >
							  	<center style="background-color: rgb(66,81,95);height:30px;width: 30px;">
									<button type="button" class="btn btn-link btn-sm" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
										<span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span>
									</button>
								</center>
							</li>
							
							<li id="config-manage_h" role="presentation" ><%-- onclick="menuClick('${pageContext.request.contextPath}/config.do')" --%>
							  	<center style="background-color: rgb(66,81,95);height:30px;width: 30px;">
									<button type="button" class="btn btn-link btn-sm" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
										<span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
									</button>
								</center>
							</li>
						    
							<li id="releases_h" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getReleases.do')">
							  	<center style="background-color: rgb(66,81,95);height:30px;width: 30px;">
									<button type="button" class="btn btn-link btn-sm" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
										<span class="glyphicon glyphicon-tag" aria-hidden="true"></span>
									</button>
								</center>
							</li>
						  <li id="fill_hidden" role="presentation" ></li>
						</ul>
				</div>
				<div id="home_right">
					<iframe  src="${pageContext.request.contextPath}/getWelcomeInfo.do" id="main-info" name="showList" style="width:100%;background-color:#ECEFF3;" scrolling="yes" allowtransparency="yes" >
					</iframe>
				</div>
			</div>
		</div>
	</div>
</body>
</html>