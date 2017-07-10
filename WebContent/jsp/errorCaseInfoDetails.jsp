<%@page import="java.util.Set"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="./jquery-ui/jquery-ui.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<script src="./jquery-ui/jquery-ui.min.js"></script>
<link href="./css/style.css" rel="stylesheet">
<script type="text/javascript">
	$(document)
			.ready(
					function() {
						function submitConfirm(info, flag, f, c) {
							$("#reminder")
									.dialog(
											{
												modal : true,
												buttons : {
													"Confirm" : function() {
														if (flag == 1) {
															$
																	.post(
																			"./setMarkCase.do",
																			{
																				featureName : $(
																						"#featureName")
																						.val(),
																				errorcases : c,
																				failedreasons : f
																			},
																			function(
																					data) {
																				$(
																						"#reminder")
																						.dialog(
																								{
																									buttons : {
																										"Confirm" : function() {
																											$(
																													this)
																													.dialog(
																															"close");
																											window.location
																													.reload()
																										}
																									},
																									open : function(
																											event,
																											ui) {
																										$(
																												this)
																												.html(
																														"");
																										$(
																												this)
																												.append(
																														data);
																									}
																								});
																			});
															//document.submiterrors.submit();//submit the  form
															//$('input:checkbox[name=errorcases]:checked').addClass("disabled");
														} else if (flag == 2) {
															$('input:checkbox')
																	.removeAttr(
																			'disabled');
															//$('input:checkbox').prop('checked',false);
														}
														$(this).dialog("close");
													},
													"Cancel" : function() {
														$(this).dialog("close");
													}
												},
												open : function(event, ui) {
													$(this).html("");
													$(this).append(
															"<p>" + info
																	+ "</p>");
												}
											});
						}

						$("#reset")
								.bind(
										"click",
										function() {
											submitConfirm(
													"If check green boxs, last command would be overwrited!",
													2);
										});

						$("#submiterrors")
								.submit(
										function() {
											event.preventDefault();
											var failed = $('input[type="radio"][name="failedreasons"]:checked');
											var checkedcase = $('input[type="checkbox"][name="errorcases"]:checked');
											if (checkedcase.length == 0) {
												submitConfirm(
														"Please check a case?",
														3);
												return;
											}
											if (failed.length == 0) {
												submitConfirm(
														"Please check a failed error?",
														3);
												return;
											}

											var f = "";
											var c = "";
											for (var i = 0; i < failed.length; i++) {
												f = failed.get(i).value
											}
											for (var i = 0; i < checkedcase.length; i++) {
												c += checkedcase.get(i).value
												c += ",";
											}
											submitConfirm("Confirm to submit?",
													1, f, c.substring(0,c.length-1));

										});

					});
</script>
<title>Feature</title>
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
						<li class="active"><a href="./getErrorCaseInfo.do">Error Cases</a></li>
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
		<%--Navbar over --%>

		<div class="row clearfix">
			<div class="col-md-12 column">
				<h3 class="text-center">Feature: ${featureName}</h3>
				<form action="./setMarkCase.do" method="post"
					name="submiterrors" id="submiterrors">
					<div class="row clearfix">
						<div class="col-md-12 column">

							<h4 class="col-md-12 column">Failed Cases</h4>
							<c:forEach items="${errorCaseList}" var="errorCaseMap">
								<div class="col-xs-6 col-sm-3 column">
									<label class="checkbox-inline">
									<c:choose>
									   <c:when test="${errorCaseMap.err_reason!=null && errorCaseMap.err_reason!=\"\"}">  
									   		<input type="checkbox" name="errorcases" value="<c:out value="${errorCaseMap.casename}"/>" disabled> <span style="color: green"><c:out value="${errorCaseMap.casename}"/>-<c:out value="${errorCaseMap.err_reason}"/></span>     
									  	</c:when>
									   	<c:otherwise> 
									  		<input type="checkbox" name="errorcases" value="<c:out value="${errorCaseMap.casename}"/>"><span style="color: red"><c:out value="${errorCaseMap.casename}"/></span>     
									   	</c:otherwise>
									</c:choose>
									</label>
								</div>
							</c:forEach>
						</div>
					</div>
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">Failed Reason</h4>
							<c:forEach items="${errorReasonList}" var="errorReasonMap">
								<div class="col-xs-6 col-sm-3 column">
									<label class="radio-inline"> <input type="radio"
										name="failedreasons" value="${errorReasonMap.error_tpye}"> <span style="color:${errorReasonMap.err_mark}">${errorReasonMap.error_tpye}</span>
									</label>
								</div>
							</c:forEach>
						</div>
					</div>
					<div class="row clearfix">
						<div class="col-md-12 column text-right">
							<input type="hidden" name="featureName" id="featureName" value=" ${featureName}">
							<button type="button" class="btn btn-default" id="reset">Reset</button>
							<input class="btn btn-default" type="submit"
								name="submiterrrorreason" value="Submit">
						</div>
					</div>
				</form>
			</div>
		</div>
		<div id="reminder"></div>
	</div>

</body>
</html>