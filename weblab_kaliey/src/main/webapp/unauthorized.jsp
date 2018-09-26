<%@ page isELIgnored="false"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0,user-scalable=no">
<title>Nokia Corporation</title>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.11.3.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap.js"></script>
<link href="<%=request.getContextPath() %>/css/bootstrap.min.css" rel="stylesheet" media="screen">
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="bookmark" href="images/favicon.ico" type="image/x-icon" />
</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-xs-6 col-md-6 col-lg-6">
				<div class="jumbotron">
				  <h1>Attention</h1>
				  <p>${login}，The operation that you access needs to be careful, please contact the super administrator to handle...</p>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
