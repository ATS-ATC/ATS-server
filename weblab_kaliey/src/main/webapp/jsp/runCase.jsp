<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html style="background-color:#ECEFF3;height: 100%;left:0px;bottom: 0px;top: 0px;">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="${pageContext.request.contextPath}/jquery-3.4.1/jquery-3.4.1.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.css" >
<script src="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/css/bootstrap.css">
<script src="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/js/bootstrap.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.css">
<script src="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.js"></script>
<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet">
<title>Run Case</title>
<style type="text/css">
/* .rr:hover , .cc:hover {
 	background-color: #FFFFEF
} */
</style>
</head>
<body style="background-color:#FFFFFF;height: 100%;left:0px;bottom: 0px;top: 0px;">
<!-- <body style="background-color:#F4F7FC;height: 100%;left:0px;bottom: 0px;top: 0px;"> -->
<div class="container-fluid" style="background-color: #FFFFFF;height: 100%;left:0px;bottom: 0px;top: 0px;">
	<div class="row">
	  	<div class="col-md-4">
	  		<div class="row" style="padding-top: 20%;padding-left: 10%">
	  			<div class="col-md-6 rr" style="border-right-style: inset;border-right-color: #E3E6E0">
	  				<a href="${pageContext.request.contextPath}/runcaseinfo1.do" style="text-decoration:none;">
		  				<h2 style="color: #8996A4">Rr</h2>
						<h5 style="color: #945659;margin-bottom: 5px;margin-top: 15px">release regression</h5>
						<h6 style="color: #83AE9B;margin-top: 5px">SUnit Admin define</h6>
					</a>
	  			</div>
	  			<div class="col-md-6 cc" style="border-right-style: inset ;border-right-color: #E3E6E0 ">
	  				<a href="${pageContext.request.contextPath}/searchInfo.do" style="text-decoration:none;">
		  				<h2 style="color: #676563">Cc</h2>
		  				<h5 style="color: #945659;margin-bottom: 5px;margin-top: 15px">cetify xxxxxx</h5>
		  				<h6 style="color: #83AE9B;margin-top: 5px">uyyy yyy yy yy  yyyyyyy</h6>
	  				</a>
	  			</div>
	  		</div>
	  		<div class="row" style="padding-top: 20%;padding-left: 10%">
	  			<div class="col-md-12">
	  				<div style="font-weight: 740;font-size: 40px;color: #676563;">Log analysis</div>
	  				<p style="margin-top: 7%;">
		  				<h6 style="color: #83AE9B">uyyy yyy yy yy  yyyyyyy  yyy yy yy</h6>
		  				<h6 style="color: #83AE9B">uyyy yyy yy yy  yyyyyyy</h6>
		  				<h6 style="color: #83AE9B">uyyy yyy yy yy  yyyyyyy</h6>
	  				</p>
	  				<button type="button"  style="margin-top: 7%;border-color:#945659;border-radius:50px ;padding-left: 32px;padding-right: 32px;color: #945659;background-color: #FFFFFF;box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04)">
	  					check log
	  				</button>
	  			</div>
	  		</div>
		</div>
	  	<div class="col-md-8">
	  		<div class="row" style="padding-top: 11%;padding-left: 4%">
	  			<!-- background-color: #EF4566; -->
		  		<div style="border-radius: 50%;height: 350px;width: 350px;position: absolute;right: 30px;top:30px;text-align: center;">
		  			<span class="glyphicon glyphicon-globe" style="color: #945659;font-size: 350px;"></span>
		  			<!-- <span class="glyphicon glyphicon-globe" style="color: #F69A9A;font-size: 200px;"></span> -->
		  		</div>
	  			<div class="col-md-12">
	  				<div style="font-weight: 740;font-size: 40px;color: #676563;font-family:Aharoni,Georgia,Serif;">Run Case</div>
	  				<div style="padding-top: 20%;z-index: 222">
	  					<div class="row" style="background-color: #FFFFFF;height: 35px;width: 68%;box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
    border-radius: 4px">
    					12
    					</div>
    					<div class="row" style="width: 70%;margin-top: 3%;">
	    					<div class="col-md-7" style="height: 190px;background-color: #FFFFFF;box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
	    					border-radius: 4px;">11</div>
	    					<div class="col-md-4" style="height: 190px;background-color: #FFFFFF;box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
	    					border-radius: 4px;margin-left: 5%;">
	    					22
	    					</div>
    					</div>
	  				</div>
	  			</div>
	  		</div>
	  		
		</div>
	</div>
</div>
</body>
</html>