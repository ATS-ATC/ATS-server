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
		function confirm(click, info, spa, rtdb) {
			$("#reminder").dialog({
				modal : true,
				buttons : {
					"Confirm" : function() {
						$.post("./removeSpaAndRtdbInfo.do", {
							click : click,
							spa : spa,
							rtdb : rtdb
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

		$("#add")
				.click(
						function() {
							var spa;
							var rtdb;

							var addsr = $('#addsr')
									.dialog(
											{
												autoOpen : false,
												height : 300,
												width : 350,
												title : "Add SPA AND RTDB",
												modal : true,
												buttons : {
													"Confirm" : function() {
														spa = $('#addspa')
																.val();
														rtdb = $('#addrtdb')
																.val();
														if (spa == ""
																&& rtdb == "") {
															$("#reminder")
																	.dialog(
																			{
																				buttons : {
																					"Confirm" : function() {
																						$(
																								this)
																								.dialog(
																										"close");
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
																									"Please enter SPA or RTDB!");
																				}
																			});
															return;
														}
														$
																.post(
																		"./addSpaAndRtdbInfo.do",
																		{
																			click : "add",
																			spa : spa,
																			rtdb : rtdb
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
																												.reload();
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
														$(this).dialog("close");
														$("#check-spartdb")
																.dialog("close");
													},
													"Cancel" : function() {
														$(this).dialog("close");
													}
												}
											});
							$('#addsr').css("display", "block");
							addsr.dialog("open");
						});

		$("#remove")
				.click(
						function() {
							var spa = $('input[type="checkbox"][name="spa"]:checked');
							var rtdb = $('input[type="checkbox"][name="rtdb"]:checked');
							var s = "";
							var r = "";
							if (spa.length == 0 && rtdb.length == 0) {
								$("#reminder")
										.dialog(
												{
													open : function(event, ui) {
														$(this).html("");
														$(this)
																.append(
																		"<p>RTDB or SPA was not be checked!</p>");
													}
												});
								return;
							}
							for (var i = 0; i < spa.length; i++) {
								s += spa.get(i).value;
								s += ",";
							}
							for (var i = 0; i < rtdb.length; i++) {
								r += rtdb.get(i).value;
								r += ",";
							}
							confirm("remove", "Confirm to remove this server?",
									s, r);
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
				<c:if test="${SPA==null || fn:length(SPA)==0 || RTDB==null || fn:length(RTDB)== 0}">
					<div class="alert alert-warning" role="alert">Can not read spa and rtdb!</div>
				</c:if>
				<h3 class="text-center">SPA and RTDB Manager</h3>
				<form action="com.alucn.web.server/SRMangerServlet" method="post"
					name="submitspartdb" id="submitspartdb">
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">SPA</h4>
							<c:if test="${SPA!=null && fn:length(SPA) > 0}">
								<c:forEach items="${SPA}" var="spa">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="checkbox"
											name="spa" value="${spa}">${spa}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					<div class="row clearfix">
						<div class="col-md-12 column">
							<h4 class="col-md-12 column">RTDB</h4>
							<c:if test="${RTDB!=null && fn:length(RTDB) > 0}">
								<c:forEach items="${RTDB}" var="rtdb">
									<div class="col-xs-6 col-sm-3 column">
										<label class="checkbox-inline"> <input type="checkbox"
											name="rtdb" value="${rtdb}">${rtdb}
										</label>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					<div class="row clearfix">
						<div class="col-md-12 column text-right">
							<button type="button" class="btn btn-default" id="add">Add</button>
							<button type="button" class="btn btn-default" id="remove">Remove</button>
						</div>
						<div id="addsr" style="display: none">
							<div class="form-group">
								<label for="addspa">SPA:</label> <input type="text"
									class="form-control" id="addspa"
									placeholder="Separated by commas">
							</div>
							<div class="form-group">
								<label for="addrtdb">RTDB:</label> <input type="text"
									class="form-control" id="addrtdb"
									placeholder="Separated by commas">
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>