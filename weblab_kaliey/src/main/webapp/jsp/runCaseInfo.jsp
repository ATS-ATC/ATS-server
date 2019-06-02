<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<html
	style="background-color:#ECEFF3;height: 100%;left:0px;bottom: 0px;top: 0px;">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="${pageContext.request.contextPath}/jquery-3.4.1/jquery-3.4.1.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.css" >
<script src="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/css/bootstrap.css">
<script src="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/js/bootstrap.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.css">
<script src="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.js"></script>
<link href="${pageContext.request.contextPath}/css/font-awesome.min.css"
	rel="stylesheet">
<title>Run Case</title>
<script type="text/javascript">
	$(function () {
		
		
	})
	function choosetype(type){
		alert(type)
		
	}
</script>
</head>
<body
	style="background-color: #FFFFFF; height: 100%; left: 0px; bottom: 0px; top: 0px;">
	<div class="container-fluid"
		style="background-color: #FFFFFF; height: 100%; left: 0px; bottom: 0px; top: 0px;">
		<div class="row" style="margin-top: 2%; margin-left: 2%">
			<div class="col-md-12">
				<h4>Release regression</h4>
				<HR
					style="FILTER: alpha(opacity = 100, finishopacity = 0, style = 1); margin-top: 0px"
					align=left width=500 color=#987cb9 SIZE=1>
				<div class="row">
					<div class="form-group col-sm-4">
						<label for="title"><span style="color: red">*</span>Title</label>
						<input type="text" class="form-control" id="title"
							placeholder="Please input form title"> <br> <label
							for="intype">In type</label>
						<div class="btn-group btn-group-justified" role="group"
							aria-label="...">
							<div class="btn-group" role="group">
								<button type="button" class="btn btn-primary" onclick="choosetype('search')">
									<span class="glyphicon glyphicon-search"></span>&nbsp;&nbsp;search
									case
								</button>
							</div>
							<div class="btn-group" role="group">
								<button type="button" class="btn btn-default" onclick="choosetype('import')">
									<span class="glyphicon glyphicon-cloud-upload"></span>&nbsp;&nbsp;import
									case
								</button>
							</div>
						</div>
						
						
					</div>
					<div class="form-group col-sm-8 table-responsive" style="display: none;">
						<label for="tempTable">Will Run Case</label>
						<table id="tempTable" class="table table-hover table-condensed table-bordered">
							<thead>
								<tr>
									<th>case name</th>
									<th>feature number</th>
									<th>case status</th>
									<th>release</th>
									<th>protocol</th>
									<th>author</th>
								</tr>
							</thead>
							<tbody id="tempCase">
								
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>