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
		//alert(head)
		//alert(height)
		document.getElementById('main-info').style.height=height-head+'px';
		document.getElementById('fill').style.height=height-316+32+'px';
		
		$("#config-manage").click(function(){
			var active = $("#config-info").is(".in");
			if(active){
				$("#fill").css("height",height-284+'px');
				//document.getElementById('fill').style.height=height-316+32+'px';
			}else{
				//document.getElementById('fill').style.height=height-316-32+'px';
				$("#fill").css("height",height-345+'px');
			}
		}); 
	});
	function menuClick(menuUrl) {
		$("#main-info").attr('src',menuUrl); 
	}
	
	$(document).ready(function () {
		var auth ="<%=session.getAttribute("auth").toString()%>";
		if("all"==auth){
			
		}else if("errorCases"==auth){
			$("#ats-servers").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#spa-and-rtdb").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#config-manage").attr('onclick', "alert('The current user does not have access!');return false;");
		}else{
			$("#ats-servers").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#spa-and-rtdb").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#config-manage").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#case-status").attr('onclick', "alert('The current user does not have access!');return false;");
			$("#error-cases").attr('onclick', "alert('The current user does not have access!');return false;");
		}
		
		$('ul.nav > li').click(function (e) {
			//e.preventDefault();
			$('ul.nav > li').removeClass('active');
			$(this).addClass('active');
		});
		
		$('#hide_left_menu').click(function () {
			//alert(1)
			$('#left-menu').hide();
			$('#right-info').attr('class','col-md-12');
			$('#main-info').attr('src', $('#main-info').attr('src'));
		});
		$('#show_left_menu').click(function(){
			$('#left-menu').show();
			$('#right-info').attr('class','col-md-10');
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
					        	<!-- <form class="navbar-form navbar-left" role="search" action="">
						            <div class="form-group">
						                <input type="text" class="form-control" placeholder="Search">
						            </div>
						            <button type="submit" class="btn btn-default">Search</button>
						        </form> -->
					        </li>
							<li><a href="#"><span class="glyphicon glyphicon-user"></span> ${login }</a></li>
				            <li><a href="./userLogout.do"><span class="glyphicon glyphicon-log-in"></span> login out</a></li> 
				        </ul> 
				    </div> 
				</nav>
			</div>
			<div id="left-menu" class="col-md-2" style="padding-left: 0px; padding-right: 0px;background-color:#293038" >
					<div class="row">
						<div class="col-md-12" >
							<a id="hide_left_menu">
								<center style="background-color: rgb(66,81,95);height:28px;">
									<button type="button" class="btn btn-link btn-sm" style="color: #AEB9C2;text-decoration:none;border:none;outline: none;padding-top: 8px;padding-left:0px;padding-right:0px;">
										<span class="glyphicon glyphicon-menu-hamburger" aria-hidden="true"></span>
									</button>
								</center>
							</a>
						<ul class="nav nav-pills nav-stacked">
						  	<li id="case-status" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getStatistics.do')">
							  	<a href="#" style="padding-top: 0px;padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default " style="color: white;text-decoration:none;border:none;outline:none;padding-left:23px;padding-right:103px;">
										<span class="glyphicon glyphicon-dashboard" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Dashboard
									</button>
								</a>
							</li>
						  	<li id="case-search" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/searchInfo.do')">
							  	<a href="#" style="padding-top: 0px;padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default " style="color: white;text-decoration:none;border:none;outline:none;padding-left:23px;padding-right:108px;">
										<span class="glyphicon glyphicon-play" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Run Case
									</button>
								</a>
							</li>
							<li id="ats-servers" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getServerInfo.do')"  >
							  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default " style="color: white;text-decoration:none;border:none;outline:none;padding-left:23px;padding-right:93px;">
										<span class="glyphicon glyphicon-object-align-bottom" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Lab Management
									</button>
								</a>
							</li>
							<li id="error-cases" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getErrorCaseInfo.do')" >
							  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default " style="color: white;text-decoration:none;border:none;outline:none;padding-left:23px;padding-right:93px;">
										<span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Bad Case Management
									</button>
								</a>
							</li>
							
							<li id="config-manage" role="presentation" ><%-- onclick="menuClick('${pageContext.request.contextPath}/config.do')" --%>
							  	<a href="#config-info" class="nav-header menu-first collapsed" data-toggle="collapse" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default " style="color: white;text-decoration:none;border:none;outline:none;padding-left:23px;padding-right:93px;">
										<span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
										&nbsp;&nbsp;&nbsp;&nbsp;Configuration
									</button>
								</a>
								<ul id="config-info" class="nav nav-list collapse menu-second" style="background-color: #42515F">
							        <li id="spa-and-rtdb" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/config.do')">
									  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
									  		<button type="button" class="btn btn-link btn-default " style="color: white;text-decoration:none;border:none;outline:none;padding-left:23px;padding-right:93px;">
												&nbsp;&nbsp;&nbsp;&nbsp;** Certification Server
											</button>
										</a>
									</li>
							        <li id="spa-and-rtdb" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getSpaAndRtdbInfo.do')">
									  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
									  		<button type="button" class="btn btn-link btn-default " style="color: white;text-decoration:none;border:none;outline:none;padding-left:23px;padding-right:93px;">
												&nbsp;&nbsp;&nbsp;&nbsp;** SPA & RTDB
											</button>
										</a>
									</li>
							    </ul>
							</li>
						    
							<li id="releases" role="presentation" onclick="menuClick('${pageContext.request.contextPath}/getReleases.do')">
							  	<a href="#" style="padding-top: 0px; padding-bottom: 0px;padding-left:0px;padding-right:0px;">
							  		<button type="button" class="btn btn-link btn-default " style="color: white;text-decoration:none;border:none;outline:none;padding-left:23px;padding-right:109px;">
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
				<!-- <div class="row">
					<iframe style="height:28px;width:100%;background-color:#ECEFF3;" scrolling="yes" allowtransparency="yes" >
						123
					</iframe>
				</div>
				<div class="row">height:597px; -->
					<iframe  src="${pageContext.request.contextPath}/getWelcomeInfo.do" id="main-info" style="width:100%;background-color:#ECEFF3;" scrolling="yes" allowtransparency="yes" >
<!-- 					<iframe  src="jsp/welcome.jsp" id="main-info" style="height:638px;width:100%;background-color:#ECEFF3;" scrolling="yes" allowtransparency="yes" > -->
					</iframe>
				<!-- </div> -->
			</div>
		</div>
	</div>
</body>
</html>