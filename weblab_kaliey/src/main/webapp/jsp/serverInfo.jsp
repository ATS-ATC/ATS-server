<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.alucn.casemanager.server.common.model.ServerSort" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="refresh" content="5"><!-- 页面每5秒刷新一次 -->
<script src="${pageContext.request.contextPath}/jquery-3.4.1/jquery-3.4.1.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.css" >
<script src="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/css/bootstrap.css">
<script src="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/js/bootstrap.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.css">
<script src="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.js"></script>
<link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" />
<title>Server Info</title>
</head>
<body style="background-color:#ECEFF3;">
	<div class="container">
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