<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />
<title>caseSearchInfo</title>
</head>
<body style="background-color:#ECEFF3;padding-top:27px;">

<div style="padding-left: 10px;padding-right: 10px;">
	
	<div class="panel panel-primary">
	    <div class="panel-heading">
	        <h3 class="panel-title">${searchCaseRunLogInfoById[0].title }</h3>
	    </div>
	    <div class="panel-body">
	        <div class="row">
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="int_id">int_id</label>
					    <input type="text" class="form-control" id="int_id" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].int_id }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="author">author</label>
					    <input type="text" class="form-control" id="author" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].author }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="datetime">datetime</label>
					    <input type="text" class="form-control" id="datetime" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].datetime }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="stateflag">stateflag</label>
					    <input type="text" class="form-control" id="stateflag" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].stateflag }">
					 </div>
	        	</div>
	        </div>
	    </div>
	</div>
	<div class="panel panel-info">
	    <div class="panel-heading" data-toggle="collapse" data-target="#condition">
	        <h3 class="panel-title" style="display: inline;">query condition</h3>
	        <div class="nav navbar-nav navbar-right">
	        	<span class="glyphicon glyphicon-chevron-down " aria-hidden="true" style="display: inline;padding-right: 10px;"></span>
	        </div>
	    </div>
	    <div class="panel-body collapse" id="condition">
	        <div class="row">
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="dataSource">dataSource</label>
					    <input type="text" class="form-control" id="dataSource" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.dataSource }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="release">release</label>
					    <input type="text" class="form-control" id="release" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.release }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="customer">customer</label>
					    <input type="text" class="form-control" id="customer" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.customer }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="base_data">base_data</label>
					    <input type="text" class="form-control" id="base_data" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.base_data }">
					 </div>
	        	</div>
	        </div>	
	        <div class="row">
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="mate">mate</label>
					    <input type="text" class="form-control" id="mate" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.mate }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="lab_number">lab_number</label>
					    <input type="text" class="form-control" id="lab_number" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.lab_number }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="special_data">special_data</label>
					    <input type="text" class="form-control" id="special_data" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.special_data }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="porting_release">porting_release</label>
					    <input type="text" class="form-control" id="porting_release" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.porting_release }">
					 </div>
	        	</div>
	        </div>
	        <div class="row">
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="case_status">case_status</label>
					    <input type="text" class="form-control" id="case_status" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.case_status }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="feature_number">feature_number</label>
					    <input type="text" class="form-control" id="feature_number" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.feature_number }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="author">author</label>
					    <input type="text" class="form-control" id="author" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.author }">
					 </div>
	        	</div>
	        	<div class="col-md-3">
	        		<div class="form-group">
					    <label for="server">server</label>
					    <input type="text" class="form-control" id="server" style="display: inline;" disabled value="${searchCaseRunLogInfoById[0].condition.server }">
					 </div>
	        	</div>
	        </div>
        </div>
	</div>
	<div class="panel panel-primary">
	    <div class="panel-heading">
	        <h3 class="panel-title">Will Running Case</h3>
	    </div>
	    <div class="panel-body">
	        <div class="row" >
	        <div class="table-responsive pre-scrollable"  style="padding-left: 10px;padding-right: 10px; height: 250px;" >
	        	<table class="table table-bordered text-nowrap"  >
	        		<thead>
	        			<tr>
	        				<th>case_name</th>
	        				<th>case_status</th>
	        				<th>feature_number</th>
	        				<th>release</th>
	        				<th>author</th>
	        				<th>special_data</th>
	        				<th>lab_number</th>
	        				<th>submit_date</th>
	        				<th>mate</th>
	        				<th>customer</th>
	        				<th>base_data</th>
	        				<th>porting_release</th>
	        			</tr>
	        		</thead>
	        		<tbody>
	        			<c:forEach items="${scase }" var="data" >
	        				<%-- ${data } 
	        				<br>
	        				----------------------
	        				<br> --%>
	        				<tr>
	        					<td>${data.case_name }</td>
	        					<td>${data.case_status }</td>
	        					<td>${data.feature_number }</td>
	        					<td>${data.release }</td>
	        					<td>${data.author }</td>
	        					<td>${data.special_data }</td>
	        					<td>${data.lab_number }</td>
	        					<td>${data.submit_date }</td>
	        					<td>${data.mate }</td>
	        					<td>${data.customer }</td>
	        					<td>${data.base_data }</td>
	        					<td>${data.porting_release }</td>
	        				</tr>
	        			</c:forEach> 
	        			<%-- <c:forEach items='${scase }' var="s" varStatus="sc">
	        				<tr>
	        					<c:forEach items='${s }' var="se" >
	        						${se } <br>
									<td>${se.value}</td>				
								</c:forEach>
								---------------------------------
								<br>
	        				</tr>
	        			</c:forEach> --%>
	        		</tbody>
	        	</table>
	        </div>
	        </div>
        </div>
  	</div>
	<script type="text/javascript">
		$(function(){
			//alert('${scase }')
			var scase = '${scase }';
		})
	</script>
</div>
</body>
</html>