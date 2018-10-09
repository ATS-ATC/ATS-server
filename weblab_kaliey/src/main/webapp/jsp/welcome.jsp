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
.state-overview .terques { background: #6ccac9;}
.state-overview .red { background: #ff6c60;}
.state-overview .yellow { background: #f8d347;}
.state-overview .blue {background: #57c8f2;}
.state-overview .darkblue{ background-color:#438eb9}
.state-overview .green{ background-color:#093}
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
<body style="background-color:#ECEFF3;">
	<!-- <canvas id="canvas-basic"></canvas> -->
	<div class="container-fluid" >
		<div class="row" style="height:100%;weight:100%;padding-top:27px;">
			
			<div class="col-md-10"  >
				<%-- <img alt="" src="${pageContext.request.contextPath}/images/Snipaste_2018-06-08_15-56-26.png" class="img-responsive" alt="Responsive image"> --%>
				&nbsp;&nbsp;&nbsp;<font size="5" style="font-weight:800;">${login },</font></br>
				&nbsp;&nbsp;&nbsp;Welcome to SurePay Automation Case Management System !
			</div>
			<div class="col-md-2"  >
				<img align="right" style="height: 50px;width: 115px;display: inline;" src="${pageContext.request.contextPath}/images/sunit.gif" class="img-responsive" alt="Responsive image"/>
			</div>
			
			
			<%-- <div class="col-md-12" style="height:100%;weight:100%;padding-top:80px;" >
				<img alt="" src="${pageContext.request.contextPath}/images/pro2.png" class="img-responsive" alt="Responsive image">
				
				<div class="col-md-4" style="text-align: center;">
					<img alt="" src="${pageContext.request.contextPath}/images/pro.png" class="img-responsive" alt="Responsive image">
				</div>
				<div class="col-md-4" style="text-align: center;">
					<img alt="" src="${pageContext.request.contextPath}/images/pro.png" class="img-responsive" alt="Responsive image">
				</div>
				<div class="col-md-4" style="text-align: center;">
					<img alt="" src="${pageContext.request.contextPath}/images/pro.png" class="img-responsive" alt="Responsive image">
				</div>
			</div>
			<div class="col-md-12" style="height:100%;weight:100%; padding-top:20px;" >
				<img alt="" src="${pageContext.request.contextPath}/images/pro3.png" class="img-responsive" alt="Responsive image">
			</div> --%>
		</div>
		<!-- <div class="row" style="margin-top: 30px; margin-bottom: 5px;">
			&nbsp;&nbsp;&nbsp;
			<h4 style="display: inline;">My workspace</h4>
			<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
		</div> -->
		<div class="row" style="margin-top: 30px; margin-bottom: 5px;">
			<div class="state-overview clearfix" >
                  <div class="col-lg-3 col-sm-6">
                      <section class="panel">
                      <a href="#" title="My Case">
                          <div class="symbol terques">
                             <i class="icon-globe"></i>
                          </div>
                          <div class="value">
                              <h1 id="all_case">${caseCount[0].allCase }</h1>
                              <p>My Case</p>
                          </div>
                          </a>
                      </section>
                  </div>
                  <div class="col-lg-3 col-sm-6">
                      <section class="panel">
                          <div class="symbol green">
                              <i class="icon-check"></i>
                          </div>
                          <div class="value">
                              <h1 id="s_case">${caseCount[0].sCase }</h1>
                              <p>My Success Case</p>
                          </div>
                      </section>
                  </div>
                  <div class="col-lg-3 col-sm-6">
                      <section class="panel">
                          <div class="symbol red">
                              <i class="icon-exclamation-sign"></i>
                          </div>
                          <div class="value">
                              <h1 id="f_case">${caseCount[0].fCase }</h1>
                              <p>My Fail Case</p>
                          </div>
                      </section>
                  </div>
                  <div class="col-lg-3 col-sm-6">
                      <section class="panel">
                          <div class="symbol darkblue">
                              <i class="icon-bar-chart"></i>
                          </div>
                          <div class="value">
                              	<h1 id="s_rate">
                              		<c:if test="${caseCount[0].allCase !=0}">
										<fmt:formatNumber type="number" value="${(caseCount[0].sCase/caseCount[0].allCase*100) }" pattern="0.00" maxFractionDigits="2"/>%
                              		</c:if>
                              		<c:if test="${caseCount[0].allCase==0}">
                              			0
                              		</c:if>
								</h1>
                              <p>My Success Rate</p>
                          </div>
                      </section>
                  </div>
              </div>
		</div>
		
	</div>
	
</body>
</html>