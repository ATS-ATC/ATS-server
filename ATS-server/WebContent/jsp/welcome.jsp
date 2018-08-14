<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/jquery/jquery-3.2.1.js"></script>
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/granim.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	var granimInstance = new Granim({
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
	});
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
			
			
			<div class="col-md-12" style="height:100%;weight:100%;padding-top:80px;" >
				<%-- <img alt="" src="${pageContext.request.contextPath}/images/pro2.png" class="img-responsive" alt="Responsive image"> --%>
				
				<%-- <div class="col-md-4" style="text-align: center;">
					<img alt="" src="${pageContext.request.contextPath}/images/pro.png" class="img-responsive" alt="Responsive image">
				</div>
				<div class="col-md-4" style="text-align: center;">
					<img alt="" src="${pageContext.request.contextPath}/images/pro.png" class="img-responsive" alt="Responsive image">
				</div>
				<div class="col-md-4" style="text-align: center;">
					<img alt="" src="${pageContext.request.contextPath}/images/pro.png" class="img-responsive" alt="Responsive image">
				</div> --%>
			</div>
			<div class="col-md-12" style="height:100%;weight:100%; padding-top:20px;" >
				<%-- <img alt="" src="${pageContext.request.contextPath}/images/pro3.png" class="img-responsive" alt="Responsive image"> --%>
			</div>
		</div>
	</div>
</body>
</html>