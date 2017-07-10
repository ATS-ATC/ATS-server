<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<link href="./jquery-ui/jquery-ui.min.css" rel="stylesheet">
<script src="./jquery-ui/jquery-ui.min.js"></script>
<link href="./css/adminstyle.css" rel="stylesheet">
<title>Config Manage</title>
<script>
	$(function() {
		function confirm(click, info, configKey, configValue) {
			$("#reminder").dialog({
				modal : true,
				buttons : {
					"Confirm" : function() {
						$.post("./uodateConfig.do", {
							click : click,
							configKey : configKey,
							configValue : configValue,
						}, function(data) {
							$("#reminder").dialog({
								buttons : {
									"Confirm" : function() {
										$(this).dialog("close");
										window.location.reload();
									}
								},
								open : function(event, ui) {
									$(this).html("");
									$(this).append(data);
								}
							});

						});
						$(this).dialog("close");
						$("#update").dialog("close");
					},
					"Cancel" : function() {
						$(this).dialog("close");
					}
				},
				open : function(event, ui) {
					$(this).html("");
					$(this).append("<p>" + info + "</p>");
				}
			});

		}

		$("#update")
				.click(
						function() {
							var configKey='';
							var configValue='';
							$("input[name='configKey']").each(  
									function(){  
										configKey += $(this).val();
										configKey += ",";
									}  
														)  
														
							$("input[name='configValue']").each(  
									function(){  
										configValue += $(this).val();
										configValue += ",";  
									}  
														) 
														
							var arr = $('input[type="text"][name="configValue"]');
							confirm("update", "Confirm to update config?", configKey, configValue);
						});
	});
</script>
</head>
<body>
	<div class="container">
		<div class="row clearfix">
			<div class="col-md-12 column">
				<nav class="navbar navbar-default" role="navigation">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse"
						data-target="#bs-example-navbar-collapse-1">
						<span class="sr-only">Toggle navigation</span><span
							class="icon-bar"></span><span class="icon-bar"></span><span
							class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="./userLoginBackHome.do">Home</a>
				</div>
				<div class="collapse navbar-collapse"
					id="bs-example-navbar-collapse-1">
					<ul class="nav navbar-nav">
						<li><a href="./getServerInfo.do">Servers</a></li>
						<li><a href="./getErrorCaseInfo.do">Error Cases</a></li>
						<li class="dropdown active"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown">Admin<strong
								class="caret"></strong></a>
							<ul class="dropdown-menu">
								<li class="active"><a href="./getSpaAndRtdbInfo.do">SPA
										and RTDB</a></li>
										<li><a href="./config.do">Config Manage</a></li>
								<li class="divider"></li>
							</ul></li>
					</ul>
					<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
							<input type="text" class="form-control" />
						</div>
						<button type="submit" class="btn btn-default">Search</button>
					</form>
					<ul class="nav navbar-nav navbar-right">
						<li id="logout"><a href="./userLogout.do">Logout</a></li>
					</ul>
				</div>
				</nav>
			</div>
		</div>
		<%--Navbar over --%>

		<div class="row clearfix">
			<div class="col-md-12 column" style="margin-bottom: 30px">
				<div id="reminder"></div>
				<c:if test="${config==null || fn:length(config)==0}">
					<div class="alert alert-warning" role="alert">Can not read config info!</div>
				</c:if>
				<h3 class="text-center">Config Manage</h3>
				<form action="" method="post"
					name="submitspartdb" id="submitspartdb">
					<div class="row clearfix">
						<div class="col-md-12 column">
							<c:if test="${config!=null && fn:length(config) > 0}">
								<c:forEach items="${config}" var="con">
										<div class="col-xs-6 col-sm-3 column"  style="width:520px;">
											<div class="checkbox-inline">    
											    <c:out value="${con.con_key}"/>
											</div> 
											<div class="checkbox-inline" style="float:right;  padding:3px; width:220px;">
												<input type="hidden" name="configKey" id="configKey-<c:out value="${con.con_key}"/>" value="<c:out value="${con.con_key}"/>"> 
												<input type="text" name="configValue" id="configValue-<c:out value="${con.con_key}"/>" value="<c:out value="${con.con_value}"/>">  
											</div>
										</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					<div class="row clearfix">
						<div class="col-md-12 column text-right">
							<button type="button" class="btn btn-default" id="update">Update</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>