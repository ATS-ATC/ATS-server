<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
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

<meta http-equiv="refresh" content="5*60">
<title>Home</title>
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
						<li><a href="./getServerInfo.do">Servers</a></li>
						<li><a href="./getErrorCaseInfo.do">Error
								Cases</a></li>
						<li class="dropdown"><a href="#" class="dropdown-toggle"
							data-toggle="dropdown">Admin<strong class="caret"></strong></a>
							<ul class="dropdown-menu">
								<li><a href="./getSpaAndRtdbInfo.do">SPA
										and RTDB</a></li>
								<li><a href="./config.do">Config Manage</a></li>
								<li class="divider"></li>
							</ul></li>
					</ul>
					<form class="navbar-form navbar-left">
						<div class="form-group">
							<input type="text" class="form-control" />
						</div>
						<button type="submit" class="btn btn-default">Search</button>
					</form>
					<ul class="nav navbar-nav navbar-right">
						<li id="logout"><a href="./userLogout.do">Logout</a></li>
					</ul>
				</div>
			</div>
			</nav>
		</div>
		<div class="row clearfix">
			<div class="col-md-12 column"></div>
		</div>
		<div class="row clearfix">
			<div class="col-md-6 column">
				<ul class="list-group">
				<jsp:useBean id="now" class="java.util.Date" />
				<fmt:formatDate value="${now}" type="both" dateStyle="long" pattern="yyyy-MM-dd HH:mm:ss" var="cc"/>
					<li class="list-group-item"><span class="badge">${cc}</span>
						Refresh time:</li>
					<li class="list-group-item" style="background-color: #4CAF50">
						<span class="badge">${S}</span> Successful cases:
					</li>
					<li class="list-group-item" style="background-color: #fb564a">
						<span class="badge">${F}</span> Failed cases:
					</li>
					<li class="list-group-item" style="background-color: #67bde8">
						<span class="badge">${I}</span> Initial cases:
					</li>
					<li class="list-group-item" style="background-color: #FFC107">
						<span class="badge">${J}</span> Pending cases:
					</li>
					<li class="list-group-item" style="background-color: #a969fd">
						<span class="badge">${R}</span> Resubmit cases:
					</li>
					<li class="list-group-item"
						style="color: #ffffff; background-color: rgb(67, 67, 72)"><span
						class="badge">${T}</span> Total cases:</li>
				</ul>
			</div>
			<div class="col-md-6 column">
				<div id="container"
					style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>
			</div>
		</div>
	</div>

	<script language="JavaScript">
		Highcharts
				.chart(
						'container',
						{
							chart : {

								plotBackgroundColor : null,
								plotBorderWidth : null,
								plotShadow : false,
								type : 'pie'
							},
							title : {
								text : 'Cases Status'
							},
							tooltip : {
								pointFormat : '{series.name}: <b>{point.percentage:.1f}%</b>'
							},
							plotOptions : {
								pie : {
									allowPointSelect : true,
									cursor : 'pointer',
									dataLabels : {
										enabled : true,
										format : '<b>{point.name}</b>: {point.percentage:.1f} %',
										style : {
											color : (Highcharts.theme && Highcharts.theme.contrastTextColor)
													|| 'black'
										}
									}
								}
							},
							credits : {
								enabled : false
							},
							series : [ {
								name : 'Cases',
								colorByPoint : true,
								data : [ {
									name : 'Successful',
									color : '#4CAF50',
									y : ${S}/${T}
								}, {
									name : 'Failed',
									color : '#fb564a',
									y : ${F}/${T},
									sliced : true,
									selected : true
								}, {
									name : 'Pending',
									color : '#FFC107',
									y : ${J}/${T}
								}, {
									name : 'Initial',
									color : '#67bde8',
									y : ${I}/${T}
								}, {
									name : 'Resubmit',
									color : '#a969fd',
									y : ${R}/${T}
								} ]
							} ]
						});
	</script>
</body>
</html>