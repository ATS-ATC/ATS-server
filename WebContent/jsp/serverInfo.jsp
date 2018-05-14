<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.alucn.casemanager.server.common.model.ServerSort" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="refresh" content="5">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<link href="./css/style.css" rel="stylesheet" />
<title>Server Info</title>
</head>
<body>
	<div class="container">
		<div class="row clearfix">
			<div class="col-md-12 column">
				<nav class="navbar navbar-default" role="navigation">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle">
						<span class="sr-only">Toggle navigation</span><span
							class="icon-bar"></span><span class="icon-bar"></span><span
							class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="./userLoginBackHome.do">Home</a>
				</div>
				<div class="collapse navbar-collapse"
					id="bs-example-navbar-collapse-1">
					<ul class="nav navbar-nav">
						<li class="active"><a href="./getServerInfo.do">Servers</a></li>
<!-- 						<li><a href="./searchInfo.do">Case Search</a></li> -->
						<li><a href="./getErrorCaseInfo.do">Error Cases</a></li>
						<li class="dropdown"><a href="#" class="dropdown-toggle"
							data-toggle="dropdown">Admin<strong class="caret"></strong></a>
							<ul class="dropdown-menu">
								<li><a href="./getSpaAndRtdbInfo.do">SPA
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
		<%--Server informations show below --%>
<!-- 		<div class="servers"> -->
			<c:forEach items="${infos}" var="set">
				<div class="col-xs-6 col-md-3 aServer" style="border: 3px solid #ddd;border-radius: 10px;width: auto;height: auto;padding:15px;margin:10px;">
					<c:forEach items="${set.value}" var="servers">
						<c:choose> 
						     <c:when test="${fn:length(servers.getMap())>1}">
							     <div class="col-xs-6 col-md-3 aServer" style="border: 3px dashed #ddd;border-radius: 10px;width: auto;height: auto;margin:10px;">
									<table border="0">
									<tr>
									<c:forEach items="${servers.getMap()}" var="server">
										<td>
										<c:choose>
										   <c:when test="${server.value.lab.serverType == 'Group' }">  
										        <a href="./getServerDetails.do?serverName=${server.value.lab.serverName}"
													class="thumbnail " style="border: 3px solid #ddd"> <font size=4>${server.value.lab.serverName}</font><br>
													<font size=4>${server.value.lab.serverType}/${server.value.lab.serverMate}</font><br>
													<font size=4>${server.value.lab.serverRelease}</font><br>
													<font size=4>${server.value.lab.serverProtocol}</font><br>
													<font size=5 id="${server.value.taskStatus.status}">${server.value.taskStatus.status}</font>
												</a>
										   </c:when>
										   <c:otherwise> 
											   <a href="./getServerDetails.do?serverName=${server.value.lab.serverName}" class="thumbnail " style="border: 3px solid #CCCC99"> 
											   		<font size=4>${server.value.lab.serverName}</font><br>
													<font size=4>${server.value.lab.serverType}/${server.value.lab.serverMate}</font><br>
													<font size=4>${server.value.lab.serverRelease}</font><br>
													<font size=4>${server.value.lab.serverProtocol}</font><br>
													<font size=5 id="${server.value.taskStatus.status}">${server.value.taskStatus.status}</font>
												</a>
										   </c:otherwise>
										</c:choose>
										</td>
									</c:forEach>
									</tr>
									</table>
								</div>
							 </c:when>      
						     <c:otherwise>  
							     <div class="col-xs-6 col-md-3 aServer" style="border: 3px dashed white;border-radius: 10px;width: auto;height: auto;margin:10px;">
									<table border="0">
										<tr>
										    <c:forEach items="${servers.getMap()}" var="server">
										     	<td>
										     		<c:choose>
													   <c:when test="${server.value.lab.serverType == 'Group' }">  
													        <a href="./getServerDetails.do?serverName=${server.value.lab.serverName}" class="thumbnail "  style="border: 3px solid #ddd"> 
															<font size=4>${server.value.lab.serverName}</font><br>
															<font size=4>${server.value.lab.serverType}/${server.value.lab.serverMate}</font><br>
															<font size=4>${server.value.lab.serverRelease}</font><br> 
															<font size=4>${server.value.lab.serverProtocol}</font><br>
															<font size=5 id="${server.value.taskStatus.status}">${server.value.taskStatus.status}</font>
															</a>
													   </c:when>
													   <c:otherwise> 
														   <a href="./getServerDetails.do?serverName=${server.value.lab.serverName}" class="thumbnail " style="border: 3px solid #CCCC99"> 
															<font size=4>${server.value.lab.serverName}</font><br>
															<font size=4>${server.value.lab.serverType}/${server.value.lab.serverMate}</font><br>
															<font size=4>${server.value.lab.serverRelease}</font><br> 
															<font size=4>${server.value.lab.serverProtocol}</font><br>
															<font size=5 id="${server.value.taskStatus.status}">${server.value.taskStatus.status}</font>
															</a>
													   </c:otherwise>
													</c:choose>
												</td>
											</c:forEach>
										</tr>
									</table>
								</div>
						  	 </c:otherwise> 
						</c:choose>
					</c:forEach>
					<div class="col-xs-6 col-md-3 aServer" style="border: 3px dashed white;border-radius: 10px;width: auto;height: auto;margin:10px;">
						<table border="0">
						<tr>
							<td>
								<a id="addlink" href="./addServerInfo.do" class="thumbnail " > 
									<span id="add"><font size=4>+</font></span>
								</a>
		                   </td>
	                   </tr>
	                   </table>
                   </div>
				</div>
			</c:forEach>
			<div class="col-xs-6 col-md-3 aServer" style="border: 3px solid #ddd;border-radius: 10px;width: auto;height: auto;padding:15px;margin:10px;">
<!-- 				<div class="col-xs-6 col-md-3 aServer"> -->
					<a id="addlink" href="./addServerInfo.do"
						class="thumbnail "> <span id="add"><font size=4>+</font></span>
					</a>
<!-- 				</div> -->
			</div>
		</div>
<!-- 	</div> -->
</body>
</html>