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
<title>SPA and RTDB</title>
<script>
	$(function() {
		function confirm(condition, info) {
			$("#reminder").dialog({
				modal : true,
				buttons : {
					"Confirm" : function() {
						$.post("./searchCaseInfo.do", {
							condition : condition,
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
						$("#check-spartdb").dialog("close");
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

		

		$("#search")
				.click(
						function() {
							var ds = $('input[type="radio"][name="ds"]:checked');
							var r = $('input[type="checkbox"][name="r"]:checked');
							var c = $('input[type="checkbox"][name="c"]:checked');
							var bd = $('input[type="checkbox"][name="bd"]:checked');
							var m = $('input[type="radio"][name="m"]:checked');
							var ln = $('input[type="radio"][name="ln"]:checked');
							var sd = $('input[type="radio"][name="sd"]:checked');
							var pr = $('input[type="checkbox"][name="pr"]:checked');
							var cs = $('input[type="checkbox"][name="cs"]:checked');
							var fi = document.getElementById("featureid").value;
							var author = document.getElementById("author").value;
							var server = document.getElementById("server").options[document.getElementById("server").selectedIndex].value;
							var condition = "";
							if (ds.length == 0 && r.length == 0 && c.length == 0 && bd.length == 0 && m.length == 0
									&& ln.length == 0 && sd.length == 0	&& pr.length == 0 && cs.length == 0 && fi == "" && author == "") {
								$("#reminder")
										.dialog(
												{
													open : function(event, ui) {
														$(this).html("");
														$(this)
																.append(
																		"<p>Please select a query condition!</p>");
													}
												});
								return;
							}
							if(ds.length == 0){
								condition += ";";
							}else{
								for (var i = 0; i < ds.length; i++) {
									condition += ds.get(i).value;
									if(i == ds.length-1){
										condition += ";";
									}else{
										condition += ",";
									}
								}
							}
							
							if(r.length == 0){
								condition += ";";
							}else{
								for (var i = 0; i < r.length; i++) {
									condition += r.get(i).value;
									if(i == r.length-1){
										condition += ";";
									}else{
										condition += ",";
									}
								}
							}
							
							if(c.length == 0){
								condition += ";";
							}else{
								for (var i = 0; i < c.length; i++) {
									condition += c.get(i).value;
									if(i == c.length-1){
										condition += ";";
									}else{
										condition += ",";
									}
								}
							}	
								
							if(bd.length == 0){
								condition += ";";
							}else{
								for (var i = 0; i < bd.length; i++) {
									condition += bd.get(i).value;
									if(i == bd.length-1){
										condition += ";";
									}else{
										condition += ",";
									}
								}
							}
							
							if(m.length == 0){
								condition += ";";
							}else{
								for (var i = 0; i < m.length; i++) {
									condition += m.get(i).value;
									if(i == m.length-1){
										condition += ";";
									}else{
										condition += ",";
									}
								}
							}
							
							if(ln.length == 0){
								condition += ";";
							}else{
								for (var i = 0; i < ln.length; i++) {
									condition += ln.get(i).value;
									if(i == ln.length-1){
										condition += ";";
									}else{
										condition += ",";
									}
								}
							}	
								
							
							if(sd.length == 0){
								condition += ";";
							}else{
								for (var i = 0; i < sd.length; i++) {
									condition += sd.get(i).value;
									if(i == sd.length-1){
										condition += ";";
									}else{
										condition += ",";
									}
								}
							}
							
							if(pr.length == 0){
								condition += ";";
							}else{	
								for (var i = 0; i < pr.length; i++) {
									condition += pr.get(i).value;
									if(i == pr.length-1){
										condition += ";";
									}else{
										condition += ",";
									}
								}
							}	
								
							if(cs.length == 0){
								condition += ";";
							}else{	
								for (var i = 0; i < cs.length; i++) {
									condition += cs.get(i).value;
									if(i == cs.length-1){
										condition += ";";
									}else{
										condition += ",";
									}
								}
							}	
								
							condition += fi;
							condition += ";";
							condition += author;
							condition += ";";
							condition += server;
							condition += "";
							
							confirm(condition, "The lib will run case according to this condition, please submit it carefully !");
						});
		});
</script>
</head>
<body>
<%
		String auth = session.getAttribute("auth").toString();
%>
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
<!-- 						<li><a href="./searchInfo.do">Case Search</a></li> -->
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
<%-- 				<c:if test="${SPA==null || fn:length(SPA)==0 || RTDB==null || fn:length(RTDB)== 0}"> --%>
<!-- 					<div class="alert alert-warning" role="alert">Can not read spa and rtdb!</div> -->
<%-- 				</c:if> --%>
				<h3 class="text-center">Case Search</h3>
				<form action="com.alucn.web.server/SRMangerServlet" method="post"
					name="submitspartdb" id="submitspartdb">
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">data_source</h4>
							<c:if test="${data_source!=null && fn:length(data_source) > 0}">
								<c:forEach items="${data_source}" var="ds">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="radio"
											name="ds" value="${ds}">${ds}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">release</h4>
							<c:if test="${release!=null && fn:length(release) > 0}">
								<c:forEach items="${release}" var="r">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="checkbox"
											name="r" value="${r}">${r}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">customer</h4>
							<c:if test="${customer!=null && fn:length(customer) > 0}">
								<c:forEach items="${customer}" var="c">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="checkbox"
											name="c" value="${c}">${c}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">base_data</h4>
							<c:if test="${base_data!=null && fn:length(base_data) > 0}">
								<c:forEach items="${base_data}" var="bd">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="checkbox"
											name="bd" value="${bd}">${bd}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">mate</h4>
							<c:if test="${mate!=null && fn:length(mate) > 0}">
								<c:forEach items="${mate}" var="m">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="radio"
											name="m" value="${m}">${m}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">lab_number</h4>
							<c:if test="${lab_number!=null && fn:length(lab_number) > 0}">
								<c:forEach items="${lab_number}" var="ln">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="radio"
											name="ln" value="${ln}">${ln}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">special_data</h4>
							<c:if test="${special_data!=null && fn:length(special_data) > 0}">
								<c:forEach items="${special_data}" var="sd">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="radio"
											name="sd" value="${sd}">${sd}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">porting_release</h4>
							<c:if test="${porting_release!=null && fn:length(porting_release) > 0}">
								<c:forEach items="${porting_release}" var="pr">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="checkbox"
											name="pr" value="${pr}">${pr}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">case_status</h4>
							<c:if test="${case_status!=null && fn:length(case_status) > 0}">
								<c:forEach items="${case_status}" var="cs">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="checkbox"
											name="cs" value="${cs}">${cs}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div><br/>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<div class="col-xs-6 col-sm-3 column">
								<label class="checkbox-inline">
									Feature ID:  
								</label>
							</div>
							<div class="col-xs-6 col-sm-3 column">
								<label class="checkbox-inline">
									<input type="text" name="featureid" id="featureid" value="">   
								</label>
							</div>
							<div class="col-xs-6 col-sm-3 column">
								<label class="checkbox-inline">
									Author:
								</label>
							</div>
							<div class="col-xs-6 col-sm-3 column">
								<label class="checkbox-inline">
									<input type="text" name="author" id="author" value="">  
								</label>
							</div>
						</div>
					</div>
					
					<br/><br/>
					
					<div class="row clearfix">
						<div class="col-md-12 column">
							<div class="col-xs-6 col-sm-3 column">
								<label class="checkbox-inline">
									Server:  
								</label>
							</div>
							<div class="col-xs-6 col-sm-3 column">
								&nbsp;&nbsp;&nbsp;&nbsp;
								<select class="checkbox-inline" style="width:174px;height:25px" id="server" name="server"> 
									<c:if test="${servers!=null && fn:length(servers) > 0}">
										<%
										if(auth.equals("errorCases")){
										%>
										<c:forEach items="${servers}" var="s">
											<option value="${s}">${s}</option>
										</c:forEach>
										<%
										} else{
										%>
										<option value="0">--Please Select--</option>
										<%
										}
										%>
									</c:if>
									<c:if test="${servers==null || fn:length(servers) == 0}">
										<option value="0">--Please Select--</option>
									</c:if>
								</select> 
							</div>
						</div>
					</div>
					<br/><br/>
					
					<div class="row clearfix">
						<div class="col-md-12 column text-right">
<!-- 							<button type="button" class="btn btn-default" id="add">update DB</button> -->
							<button type="button" class="btn btn-default" id="search">search</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>