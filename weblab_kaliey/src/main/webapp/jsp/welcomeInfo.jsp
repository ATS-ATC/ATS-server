<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="${pageContext.request.contextPath}/jquery/jquery-3.2.1.js"></script>

<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />

<script src="${pageContext.request.contextPath}/js/bootstrap-tagsinput.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-tagsinput.css" rel="stylesheet" />

<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet">

<script src="${pageContext.request.contextPath}/js/bootstrap-select.min.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-select.min.css" rel="stylesheet">

<script type="text/javascript">
</script>
<title>User Management</title>
</head>
<body style="background-color:#ECEFF3;"><!-- style="background-color:#ECEFF3;" -->
	<ol class="breadcrumb" style="padding-top: 3px;padding-bottom: 3px;">
	  <li><a href="${pageContext.request.contextPath}/getWelcome.do">Home</a></li>
	  <li class="active">Info</li>
	</ol>
    <div class="panel-body" style="padding-bottom:0px;">
        <div style="background-color: white;">
	        <div class="row" style="margin-left: 10px;margin-right: 13px;margin-top: 10px; margin-bottom: 20px;padding-top: 20px;">
	        	<table id="tb_departments" class="text-nowrap" style="background-color: #FBFCFC;padding-top: 20px;"></table>
	        </div>
        </div>
    </div>
</body>
<script type="text/javascript">
$(function () {
    var oTable = new TableInit();
    oTable.Init();
});

var TableInit = function () {
    var oTableInit = new Object();
    oTableInit.Init = function () {
        $('#tb_departments').bootstrapTable({
            url: 'getWelcomeInfoJson.do?feature_number=${feature_number}',
            method: 'get',                      
            striped: false,                       
            cache: false,                       
            pagination: true,                   
            sortable: true,                     
            sortOrder: "asc",                   
            queryParams: oTableInit.queryParams, 
            sidePagination: "server",            
            pageNumber:1,                        
            pageSize: 10,                        
            pageList: [10, 25, 50, 100],         
            minimumCountColumns: 2,             
            clickToSelect: true,                 
            uniqueId: "id",                      
            singleSelect : true,
            rowStyle: function (row, index) {
                var strclass = "";
                if (row.submit_rate <100 || row.fail_rate>0) {
                    strclass = 'danger';
                } else {
                	strclass = 'info';
                }
                return { classes: strclass }
            },
            columns: [
            {
                 field: 'feature_number',
                 title: 'FeatureID'
            }, {
                field: 'user_name',
                title: 'Owner'
            }, {
                field: 'ftc_date',
                title: 'FTC',
            }, {
                field: 'case_num',
                title: 'TargetAutoCases',
            }, {
                field: 'dft',
                title: 'ActualSubmittedAutoCases',
            }, {
                field: 'fail',
                title: 'CertifyFailedCases',
            }, {
                field: 'uncheck',
                title: 'CertifyFailedNotCheckedCases',
            }, {
                field: 'submit_rate',
                title: 'SubmitRate',
                formatter: function (value, row, index) {
                	return Math.round(row.submit_rate)+'%';
                }
            }, {
                field: 'fail_rate',
                title: 'FailRate',
                formatter: function (value, row, index) {
                	return Math.round(row.fail_rate)+'%';
                }
            }, {
                field: 'to_reporter',
                title: 'ReportTo',
            }]
        });
    };

    oTableInit.queryParams = function (params) {
        var temp = {
            limit: params.limit,  
            offset: params.offset, 
        };
        return temp;
    };
    return oTableInit;
};
</script> 
</html>