<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="${pageContext.request.contextPath}/jquery-3.4.1/jquery-3.4.1.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.css" >
<script src="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/css/bootstrap.css">
<script src="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/js/bootstrap.js"></script>
<title>Nokia Corporation</title>
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="bookmark" href="images/favicon.ico" type="image/x-icon" />
<script type="text/javascript">
$(document).keyup(function(event){
    if(event.keyCode ==13){
      $("#submit").trigger("click");
    }
})
</script>
<style type="text/css">
body{ 
	background: #FFFFFF url(images/237.png) no-repeat fixed left;
	/* background:url(images/245.jpg);
	background-size:100%;
	background-repeat:no-repeat; */
}
</style>
</head>
<body>
	<div class="container-fluid">
		<div class="row">
		  	<div class="col-md-9" style="padding-left: 0px; padding-right: 0px;">
				<!-- <img class="img-responsive" alt="Responsive image"  src="images/233.png"> -->
			</div>
		  	<div class="col-md-3">
				<div class="row">
				  <div class="col-md-10 col-md-offset-1" style="padding-top:150px;">
				  	<center>
				  		<img src="./images/logo.png" class="img-responsive" alt="Responsive image" >
				  	</center>
				  </div>
				</div>
				<div class="row">
				  <div class="col-md-10 col-md-offset-1" style="padding-top:10px;">
				  	<center><font size="3" style="font-weight:bold;">SurePay Automation Case Management System</font></center>
				  </div>
				</div>
				<div class="row">
				  <div class="col-md-10 col-md-offset-1" style="padding-top:78px;">
					<form class="form-horizontal" action="./releasesLoginCheckOut.do" method="post" >
					  <div class="form-group">
					    <div class="col-sm-12">
					      <input type="text" class="form-control" id="username" name="username" placeholder="username">
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-12">
					      <input type="password" class="form-control" id="password" name="password" placeholder="password">
					    </div>
					  </div>
					  <c:if test="${loginResult != 'success' && loginResult != 'login'}">
							<div class="alert alert-dismissable alert-danger text-center" id="warning">
								<strong>User Name or Password is incorrect!</strong>
							</div>
						</c:if>
					  <div class="form-group">
					    <div class="col-sm-12" style="padding-top:10px">
					      <button id="submit" type="submit" class="btn btn-primary btn-lg btn-block" style="padding-top: 5px;padding-bottom: 5px;">Sign in</button>
					    </div>
					  </div>
					</form>
				  </div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>