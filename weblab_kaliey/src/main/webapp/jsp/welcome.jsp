<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/jquery/jquery-3.2.1.js"></script>
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/granim.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/loading-bar.css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/loading-bar.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	/* var result;
	$.ajax({
		url:"${pageContext.request.contextPath}/getWelcomeInfo.do", 
		async:false,
		success: function(data) {
			result=data;
		}
	});
	alert(result.allCase)
	$("#all_case").html(result.allCase);
	$("#s_case").val(result.sCase);
	$("#f_case").val(result.fCase);
	$("#s_rate").val((result.sCase/result.allCase)*100+"%"); */
	
	/* var granimInstance = new Granim({
	    element: '#canvas-basic',
	    name: 'basic-gradient',
	    direction: 'top-bottom',  //'left-right', 'diagonal', 'top-bottom', 'radial'
	    opacity: [1, 1],
	    isPausedWhenNotInView: true,
	    states : {
	        "default-state": {
	            gradients: [
	                ['#AA076B', '#61045F'],
	                ['#02AAB0', '#00CDAC'],
	                ['#DA22FF', '#9733EE']
	            ]
	        }
	    }
	}); */
	
});
</script>
<style type="text/css">
#canvas-basic {
    position: absolute;
    display: block;
    width: 100%;
    height: 100%;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
}
.state-overview .terques { 
	background: #2193b0;  /* fallback for old browsers */
	background: -webkit-linear-gradient(to right, #6dd5ed, #2193b0);  /* Chrome 10-25, Safari 5.1-6 */
	background: linear-gradient(to right, #6dd5ed, #2193b0); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
}
.state-overview .red { 
	background: #FF4B2B;  /* fallback for old browsers */
	background: -webkit-linear-gradient(to right, #ff6c60, #FF4B2B);  /* Chrome 10-25, Safari 5.1-6 */
	background: linear-gradient(to right, #ff6c60, #FF4B2B); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
}

.state-overview .yellow { background: #f8d347;}
.state-overview .darkblue {
	background: #4286f4;  /* fallback for old browsers */
	background: -webkit-linear-gradient(to right, #4286f4, #438eb9);  /* Chrome 10-25, Safari 5.1-6 */
	background: linear-gradient(to right, #4286f4, #438eb9); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
}
.state-overview .green{ 
	background: #093;  /* fallback for old browsers */
	background: -webkit-linear-gradient(to right, #52c234, #093);  /* Chrome 10-25, Safari 5.1-6 */
	background: linear-gradient(to right, #52c234, #093); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
}
.state-overview .symbol {width: 40%;padding: 25px 15px; -webkit-border-radius: 4px 0px 0px 4px;border-radius: 4px 0px 0px 4px;}
.state-overview .symbol i {color: #fff;font-size: 50px;}
.state-overview .value {width: 58%;padding-top: 21px;}
.state-overview .value {float: right;}
.state-overview .value h1 {font-weight: 300;}
.state-overview .value h1, .state-overview .value p {margin: 0;padding: 0;color: #666666;}
.state-overview .symbol, .state-overview .value {display: inline-block;text-align: center;}
.state-overview .panel{border:1px solid #dddddd; margin-bottom:15px;}
</style>
</head>
<style type="text/css">
.jumbotron{
	color: #222; 
	text-shadow: 0px 2px 3px #555;
}
</style>
<body style="background-color:#ECEFF3;">
	<div class="container-fluid" >
		<div class="row" style="height:100%;weight:100%;padding-top:27px;">
			<div class="col-md-10"  >
				<%-- <img alt="" src="${pageContext.request.contextPath}/images/Snipaste_2018-06-08_15-56-26.png" class="img-responsive" alt="Responsive image"> --%>
				<%-- &nbsp;&nbsp;&nbsp;<font size="5" style="font-weight:800;"><span class="jumbotron" style="padding-left: 0px; padding-right: 0px;">${login },</span></font></br> --%>
				&nbsp;&nbsp;&nbsp;<font size="5" style="font-weight:800;">${login },</font></br>
				&nbsp;&nbsp;&nbsp;<span >Welcome to SurePay Automation Case Management System !</span>
			</div>
			<div class="col-md-2"  >
				<img align="right" style="height: 50px;width: 115px;display: inline;margin-right: 7px;" src="${pageContext.request.contextPath}/images/sunit.gif" class="img-responsive" alt="Responsive image"/>
			</div>
		<div class="row" style="margin-top: 30px; margin-bottom: 5px;padding-top: 70px;padding-left: 20px;padding-right: 20px;">
			<div class="state-overview clearfix" >
                  <div class="col-lg-3 col-sm-6">
                      <section class="panel">
	                      <a href="#" title="My Case">
	                          <div class="symbol terques">
	                             <i class="icon-globe"></i>
	                          </div>
	                          <div class="value" >
	                              <h1 id="all_case">${caseCount[0].allCase }</h1>
	                              <p><strong >My Case</strong></p>
	                          </div>
                          </a>
                      </section>
                  </div>
                  <div class="col-lg-3 col-sm-6">
                      <section class="panel">
                          <div class="symbol green">
                              <i class="icon-check"></i>
                          </div>
                          <div class="value" >
                              <h1 id="s_case">${caseCount[0].sCase }</h1>
                              <p><strong >My Success Case</strong></p>
                          </div>
                      </section>
                  </div>
                  <div class="col-lg-3 col-sm-6">
                      <section class="panel">
                          <div class="symbol red">
                              <i class="icon-exclamation-sign"></i>
                          </div>
                          <div class="value" >
                              <h1 id="f_case">${caseCount[0].fCase }</h1>
                              <p><strong>My Fail Case</strong></p>
                          </div>
                      </section>
                  </div>
                  <div class="col-lg-3 col-sm-6">
                      <section class="panel">
                          <div class="symbol darkblue">
                              <i class="icon-bar-chart"></i>
                          </div>
                          <div class="value" >
                              	<h1 id="s_rate">
                              		<c:if test="${caseCount[0].allCase !=0}">
										<fmt:formatNumber type="number" value="${(caseCount[0].sCase/caseCount[0].allCase*100) }" pattern="0.00" maxFractionDigits="2"/>%
                              		</c:if>
                              		<c:if test="${caseCount[0].allCase==0}">
                              			0
                              		</c:if>
								</h1>
                              <p><strong>My Success Rate</strong></p>
                          </div>
                      </section>
                  </div>
              </div>
		</div>
		<div class="panel-body" style="margin-left: 20px;margin-right: 20px ;background-color: white;">
			<div class="row" style="margin-left: 10px;margin-right: 13px;margin-top: 10px;">
				<table id="tb_departments" class="text-nowrap table-borderless" style="background-color: #FBFCFC"></table>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function () {
    var oTable = new TableInit();
    oTable.Init();
})
var TableInit = function () {
    var oTableInit = new Object();
    oTableInit.Init = function () {
        $('#tb_departments').bootstrapTable({
            url: 'getWelcomeBottom.do',   		
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
            strictSearch: true,
            minimumCountColumns: 2,             
            clickToSelect: true,                 
            uniqueId: "ID",                      
           	rowStyle: function (row, index) {
                var strclass = "";
                if (row.submit_rate <100 || row.fail_rate>0) {
                    strclass = 'danger';
                } else {
                	strclass = 'info';
                }
                return { classes: strclass }
            },
            columns: [{
                field: 'feature_number',
                title: 'FeatureID',
                formatter:function(value,row,index){
                	var a = '<a href="./getWelcomeInfo.do?feature_number='+value+'" ><strong>'+value+'</strong></a>';
                	return a;
                } 
            }, {
                field: 'ftc_date',
                title: 'FTC'
            }, {
                field: 'case_num',
                title: 'TargetAutoCases'
            }, {
                field: 'dft',
                title: 'ActualSubmittedAutoCases'
            }, {
                field: 'fail',
                title: 'CertifyFailedCases'
            }, {
                field: 'uncheck',
                title: 'CertifyFailedNotCheckedCases'
            }, {
                field: 'submit_rate',
                title: 'SubmitRate',
                formatter: function (value, row, index) {
                	return Math.round(row.submit_rate)+'%';
                }
            }, {
                field: 'fail_rate',
                title: 'FailRate',
                //formatter: 'statusFormatter'
                formatter: function (value, row, index) {
                	return Math.round(row.fail_rate)+'%';
                }
            }
            ]
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
/* function statusFormatter(value, row, index) {
	if(row.fail_rate > 0){
		var fail = Math.round(row.fail_rate)
		var temp = 
			'<div class="progress">'+
			'    <div class="progress-bar" role="progressbar" '+
			'        aria-valuemin="0" aria-valuemax="100" style="width: '+fail+'%;">'+
			'        <span class="sr-only">'+fail+'%</span>'+
			'    </div>'+
			'</div>'
		return temp;
	}else{
		return Math.round(row.fail_rate)+'%';
	}
}  */
</script> 
</body>
</html>