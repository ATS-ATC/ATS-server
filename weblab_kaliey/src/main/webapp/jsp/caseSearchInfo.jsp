<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>

<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-tagsinput.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-tabdrop.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/bootstrap-tagsinput.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/tabdrop.css" rel="stylesheet" />

<script src="${pageContext.request.contextPath}/google-code-prettify/prettify.js"></script>
<link href="${pageContext.request.contextPath}/google-code-prettify/prettify.css" rel="stylesheet" />

<script src="./js/echarts.js"></script>

<script>
$(function(){
	window.prettyPrint && prettyPrint();
	//$('.nav-tabs:first').tabdrop();
	//$('.nav-tabs:last').tabdrop({text: 'More options'});
	$('.nav-pills').tabdrop({text: 'With pills'});
	
	var fail_count = ${battchStatusCount.fail_count};
	var success_count = ${battchStatusCount.success_count};
	var runing_count = ${battchStatusCount.runing_count};
	if(runing_count > 0) {
		$('#running').removeAttr("hidden");
	}
	else if(success_count > 0 && fail_count == 0 && runing_count == 0){
		$('#pass').removeAttr("hidden");
	}
	else if(success_count > 0 && fail_count > 0 && runing_count == 0) {
		$('#main').removeAttr("hidden");
		var myChart = echarts.init(document.getElementById('main'));
		var option = {
			   title : {
			        text: 'Running Case Rate',
			       /*  subtext: Date(), */
			        x:'center'
			    }, 
			    tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c} ({d}%)"
			    },
			    toolbox: {
	    	        show: true,
	    	        feature: {
	    	            dataView: {show: true, readOnly: false},
	    	            restore: {show: true},
	    	            saveAsImage: {show: true}
	    	        }
	    	    },
			    legend: {
			        orient: 'vertical',
			        left: 'right',  
	                x : 'right',  
	                y : 'bottom',
			        data: ['Failed','Successful']
			    }, 
			    series : [
			    	{
			            name:'Case Status',
			            type:'pie',
			            avoidLabelOverlap: true,
			            radius: [0, '65%'],
						/*radius: ['40%', '55%'], */
			            label: {
			                normal: {
			                    formatter: '  {b|{b}：}{c}  {per|{d}%}  ',
			                    backgroundColor: '#eee',
			                    borderColor: '#aaa',
			                    borderWidth: 1,
			                    borderRadius: 4,
			                    rich: {
			                        b: {
			                            fontSize: 13,
			                            lineHeight: 23
			                        },
			                        per: {
			                            color: '#eee',
			                            backgroundColor: '#334455',
			                            padding: [2, 4],
			                            borderRadius: 2
			                        }
			                    }
			                }
			            },
			            data:[
			                {value:fail_count , name:'Failed'},
			                {value:success_count , name:'Successful'}
			            ]
			        }
			    ]
			};
		myChart.setOption(option);
	}
	else if(fail_count > 0 && success_count == 0 && runing_count == 0){
		$('#fail').removeAttr("hidden");
	}
	else if(success_count == 0 && fail_count == 0 && runing_count == 0){
		$('#error').removeAttr("hidden");
	}
});
</script>
<title>caseSearchInfo</title>
</head>
<body style="background-color:#ECEFF3;padding-top:10px;">

<div style="padding-left: 10px;padding-right: 10px;">
	
	<div class="panel panel-default">
	    <div class="panel-body">
		    <h3 style="font-weight: 800;text-align: center;">${searchCaseRunLogInfoById[0].title }</h3>
		    <br><br>
	        <h4 style="font-weight: 800">Form Info</h4>
	        <hr>
	        <div class="row">
	        	<!-- <div class="col-md-2" style="text-align: right;">
	        		<span class="glyphicon glyphicon-tags" style="color: #8897A6;font-size: 30px;padding-right: 10px;"></span>
	        	</div> -->
	        	<div class="col-md-12"  style="padding-left: 30px;padding-right: 10px;">
	        		<strong style="color:#8897A6 ">ID : ${searchCaseRunLogInfoById[0].int_id } </strong> 
	        		<br>
	        		<strong>Author : </strong><code>${searchCaseRunLogInfoById[0].author }</code>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  <code>${searchCaseRunLogInfoById[0].create_time }</code>
	        		<!-- <hr> -->
	        		<br><br>
	        		<div class="row">
	        			<div class="col-md-5">
			        		<strong>dataSource : </strong>${searchCaseRunLogInfoById[0].condition.dataSource }<br>
			        		<strong>release : </strong>${searchCaseRunLogInfoById[0].condition.release }<br>
			        		<strong>customer : </strong>${searchCaseRunLogInfoById[0].condition.customer }<br>
			        		<strong>base_data : </strong>${searchCaseRunLogInfoById[0].condition.base_data }<br>
			        		<strong>mate : </strong>${searchCaseRunLogInfoById[0].condition.mate }<br>
			        		<strong>lab_number : </strong>${searchCaseRunLogInfoById[0].condition.lab_number }<br>
			        		<strong>special_data : </strong>${searchCaseRunLogInfoById[0].condition.special_data }<br>
			        		<strong>porting_release : </strong>${searchCaseRunLogInfoById[0].condition.porting_release }<br>
			        		<strong>case_status : </strong>${searchCaseRunLogInfoById[0].condition.case_status }<br>
			        		<strong>feature_number : </strong>${searchCaseRunLogInfoById[0].condition.feature_number }<br>
			        		<strong>author : </strong>${searchCaseRunLogInfoById[0].condition.author }<br>
			        		<strong>server : </strong>${searchCaseRunLogInfoById[0].condition.server }<br>
			        		<strong>protocol : </strong>${searchCaseRunLogInfoById[0].condition.protocol }<br>
			        		<strong>workable_release : </strong>${searchCaseRunLogInfoById[0].condition.workable_release }<br>
		        		</div>
		        		<div class="col-md-7">
		        			<div id="main" style="width: 100%;height:280px;background-color:#FFFFFF" hidden></div>
		        			<div id="fail" style="text-align: right;transform:rotate(-7deg);margin-right: 50px" hidden>
		        				<img src="./images/allFail.png" style="height: 100px;width: 200px">
		        			</div>
		        			<div id="pass" style="text-align: right;transform:rotate(-7deg);margin-right: 50px" hidden>
		        				<img src="./images/allPass.png" style="height: 100px;width: 200px">
		        			</div>
		        			<div id="running" style="text-align: right;transform:rotate(-7deg);margin-right: 50px" hidden>
		        				<img src="./images/running.png" style="height: 100px;width: 200px">
		        			</div>
		        			<div id="error" style="text-align: right;transform:rotate(-7deg);margin-right: 50px" hidden>
		        				<img src="./images/error.png" style="height: 100px;width: 200px">
		        			</div>
		        		</div>
	        		</div>
	        	</div>
	        </div>
	        
	        <br><br>
	        
	        
	        <h4 style="font-weight: 800">Server Info</h4>
	        <hr>
	        <div class="row" style="padding-left: 30px;padding-right: 10px;">
	        	<ul class="nav nav-pills">
		    	 <c:forEach items="${server_info }" var="data" varStatus="status">
		    	 	<li <c:if test="${status.index==0}">class="active"</c:if> >
	    				<a href="#${data.serverName }" data-toggle="tab" style="padding-top: 5px;padding-bottom: 5px;">${data.serverName }</a>
	    			</li>
	    		 </c:forEach> 
	    		</ul>
	    		<div class="tab-content">
	    		<c:forEach items="${server_info }" var="data" varStatus="status">
	    			<div <c:if test="${status.index!=0}">class="tab-pane"</c:if> <c:if test="${status.index==0}">class="tab-pane active"</c:if> id="${data.serverName }">
		        <div class="row" style="margin-top: 30px;">
		        	<div class="col-md-3">
		        		 <div class="form-group">
						    <label for="serverName">serverName</label>
						    <strong>
							    <input type="text" class="form-control" id="serverName" style="display: inline;border-radius:4px;" disabled 
							    value="${data.serverName }">
						    </strong>
						 </div>
		        	</div>
		        	<div class="col-md-3">
		        		 <div class="form-group">
						     <label for="serverIp">serverIp</label>
						    <input type="text" class="form-control" id="serverIp" style="display: inline;border-radius:4px;" disabled 
						    value="${data.serverIp }">
						 </div>
		        	</div>
		        	<div class="col-md-3">
		        		 <div class="form-group">
						     <label for="serverRelease">serverRelease</label>
						    <input type="text" class="form-control" id="serverRelease" style="display: inline;border-radius:4px;" disabled 
						    value="${data.serverRelease }">
						 </div>
		        	</div>
		        	<div class="col-md-3">
		        		 <div class="form-group">
						     <label for="serverProtocol">serverProtocol</label>
						    <input type="text" class="form-control" id="serverProtocol" style="display: inline;border-radius:4px;" disabled 
						    value="${data.serverProtocol }">
						 </div>
		        	</div>
		        </div>	
		        <div class="row">
		        	<div class="col-md-3">
		        		 <div class="form-group">
						     <label for="serverType">serverType</label>
						    <input type="text" class="form-control" id="serverType" style="display: inline;border-radius:4px;" disabled 
						    value="${data.serverType }">
						 </div>
		        	</div>
		        	<div class="col-md-3">
		        		 <div class="form-group">
						     <label for="serverMate">serverMate</label>
						    <input type="text" class="form-control" id="serverMate" style="display: inline;border-radius:4px;" disabled 
						    value="${data.serverMate }">
						 </div>
		        	</div>
		        	<div class="col-md-3">
		        		 <div class="form-group">
						     <label for="mateServer">mateServer</label>
						    <input type="text" class="form-control" id="mateServer" style="display: inline;border-radius:4px;" disabled 
						    value="${data.mateServer }">
						 </div>
		        	</div>
		        	<div class="col-md-3">
		        		 <div class="form-group">
						     <label for="setName">setName</label>
						    <input type="text" class="form-control" id="setName" style="display: inline;border-radius:4px;" disabled 
						    value="${data.setName }">
						 </div>
		        	</div>
		        </div>	
		        <div class="row">
		        	<div class="col-md-12">
		        		<div class="form-group">
						     <label for="serverSPA">serverSPA</label><br>
						    <input type="text" data-role="tagsinput" class="form-control" id="serverSPA" style="display: inline;border-radius:4px;" disabled 
						    value='${fn:replace(fn:replace(data.serverSPA,"[",""),"]","") }'>
						 </div>
		        	</div>
		        	<div class="col-md-12">
		        		<div class="form-group">
						     <label for="serverRTDB">serverRTDB</label><br>
						    <input type="text" data-role="tagsinput" class="form-control" id="serverRTDB" style="display: block;border-radius:4px;" disabled 
						    value='${fn:replace(fn:replace(data.serverRTDB,"[",""),"]","") }'>
						 </div>
		        	</div>
		        </div>
				</div>
		        </c:forEach> 
		        </div>
	        </div>
	        
	        <br><br>
	        
	        <h4 style="font-weight: 800">Running Info </h4>
	        <br>
	        <div class="row" >
		        <div class="table-responsive pre-scrollable"  style="padding-left: 30px;padding-right: 10px; max-height: 850px;" >
		        	<table class="table table-bordered text-nowrap"  >
		        		<thead>
		        			<tr>
		        				<th>case_name</th>
		        				<th>run_result</th>
		        				<th>target_labs</th>
		        				<th>complete_labs</th>
		        				<!-- <th>submit_owner</th>
		        				<th>submit_date</th> -->
		        				<th>complete_date</th>
		        				<th>case_cost</th>
		        				<!-- <th>target_release</th>
		        				<th>submit_channel</th> -->
		        			</tr>
		        		</thead>
		        		<tbody>
		        			<c:forEach items="${scase }" var="data" >
		        				<tr>
		        					<td>${data.case_name }</td>
		        					<td style="background-color:#D9EDF7">${data.run_result }</td>
		        					<td>${data.target_labs }</td>
		        					<td>${data.complete_labs }</td>
		        					<%-- <td>${data.submit_owner }</td>
		        					<td>${data.submit_date }</td> --%>
		        					<td>${data.complete_date }</td>
		        					<td>${data.case_cost }</td>
		        					<%-- <td>${data.target_release }</td>
		        					<td>${data.submit_channel }</td> --%>
		        				</tr>
		        			</c:forEach> 
		        		</tbody>
		        	</table>
		        </div>
	        </div>
	        
	    </div>
	</div>
</div>
</body>
</html>