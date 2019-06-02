<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script src="${pageContext.request.contextPath}/jquery-3.4.1/jquery-3.4.1.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.css" >
<script src="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/css/bootstrap.css">
<script src="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/js/bootstrap.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.css">
<script src="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.js"></script>

<link href="${pageContext.request.contextPath}/css/bootstrap-tagsinput.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/js/bootstrap-tagsinput.js"></script>

<link href="${pageContext.request.contextPath}/css/tabdrop.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/js/bootstrap-tabdrop.js"></script>

<script src="${pageContext.request.contextPath}/google-code-prettify/prettify.js"></script>
<link href="${pageContext.request.contextPath}/google-code-prettify/prettify.css" rel="stylesheet" />

<script src="${pageContext.request.contextPath}/js/echarts.js"></script>

<script>
$(function(){
	window.prettyPrint && prettyPrint();
	//$('.nav-tabs:first').tabdrop();
	//$('.nav-tabs:last').tabdrop({text: 'More options'});
	$('.nav-pills').tabdrop({text: 'With pills'});
	
	var fail_count = ${battchStatusCount.fail_count};
	var success_count = ${battchStatusCount.success_count};
	var runing_count = ${battchStatusCount.runing_count};
	var cancel_count = ${battchStatusCount.cancel_count};
	
	var init_chart = function(fields, datas)
	{
		$('#main').removeAttr("hidden");
        var myChart = echarts.init(document.getElementById('main'));
        var option = {
               title : {
                    text: 'Running Case Rate',
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
                    data: fields
                }, 
                series : [
                    {
                        name:'Case Status',
                        type:'pie',
                        avoidLabelOverlap: true,
                        radius: [0, '65%'],
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
                        data:datas
                    }
                ]
            };
        myChart.setOption(option);
	}
	
	if(success_count == 0 && fail_count == 0 && runing_count > 0) {
		$('#running').removeAttr("hidden");
	}
	else if(success_count > 0 || fail_count > 0 ) {
		if(cancel_count > 0)
		{
			var fields = ['Failed','Successful', 'Cancel'];
			
			var datas = [{value:fail_count , name:'Failed'},
                {value:success_count , name:'Successful'},
                {value:cancel_count, name: 'Cancel'}];

		}
		else if(runing_count > 0)
		{
			var fields = ['Failed','Successful', 'Running'];
            
            var datas = [{value:fail_count , name:'Failed'},
                {value:success_count , name:'Successful'},
                {value:runing_count, name: 'Running'}];
		}
		else
		{
		    var fields = ['Failed','Successful'];
            
            var datas = [{value:fail_count , name:'Failed'},
                {value:success_count , name:'Successful'}];
		}
		init_chart(fields, datas);
	}
	else if(success_count == 0 && fail_count == 0 && runing_count == 0 && cancel_count > 0){
		$('#cancel').removeAttr("hidden");
	}
	
	$('#export').click(function(){
		$("#subExport").attr("disabled","disabled");
		var exportForm = document.getElementById('exportForm');
		exportForm.submit();
	});
	
	
});
</script>
<title>caseSearchInfo</title>
</head>
<body style="background-color:#ECEFF3;padding-top:10px;">

<div style="padding-left: 10px;padding-right: 10px;">
	
	<div class="panel panel-default">
	    <div class="panel-body">
	    	<div style="text-align: center;">
		    	<h3 style="font-weight: 800;display: inline;">${searchCaseRunLogInfoById[0].title }</h3>
		    	<sub id="export">
		    		<form action="exportCaseInfo.do" style="display: inline;" id="exportForm">
		    			<input name="logID" value="${searchCaseRunLogInfoById[0].int_id }" hidden/>
		    			<div id="subExport" class="btn"  style="background-color: #E1E6E0;color: #124191;padding-top: 1px;padding-bottom: 1px;">
		    				<span class="glyphicon glyphicon-export" aria-hidden="true" style="padding-bottom: 5px;">export
		    			</div>
		    		</form>
		    	</sub>
		  	</div>  
		    <br><br>
	        <h4 style="font-weight: 800 ;">Form Info</h4>
	        <hr>
	        <div class="row">
	        	<!-- <div class="col-md-2" style="text-align: right;">
	        		<span class="glyphicon glyphicon-tags" style="color: #8897A6;font-size: 30px;padding-right: 10px;"></span>
	        	</div> -->
	        	<div class="col-md-12"  style="padding-left: 30px;padding-right: 10px;">
	        		<strong style="color:#8897A6 ">ID : ${searchCaseRunLogInfoById[0].int_id } </strong> 
	        		<br>
	        		<strong>Submiter : </strong><code>${searchCaseRunLogInfoById[0].author }</code>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  <code>${searchCaseRunLogInfoById[0].create_time }</code>
	        		<!-- <hr> -->
	        		<br><br>
	        		<div class="row">
	        			<div class="col-md-5">
			        		${searchCaseRunLogInfoById[0].condition}
		        		</div>
		        		<div class="col-md-7">
		        			<div id="main" style="width: 100%;height:280px;background-color:#FFFFFF" hidden></div>
		        			<div id="cancel" style="text-align: right;transform:rotate(-7deg);margin-right: 50px" hidden>
		        				<img src="./images/cancel.png" style="height: 100px;width: 200px">
		        			</div>
		        			<div id="running" style="text-align: right;transform:rotate(-7deg);margin-right: 50px" hidden>
		        				<img src="./images/running.png" style="height: 100px;width: 200px">
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
		        				<th>hotslide</th>
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
		        					<td>${data.hotslide }</td>
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