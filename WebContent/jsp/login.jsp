<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./jquery/jquery.cookie.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<link href="./css/style.css" rel="stylesheet">
<title>Please Login</title>
</head>
<body>
	<div class="container ">
		<div class="row clearfix">
			<div class="col-md-4 column"></div>
			<div class="col-md-4 column">
				<form class="form-horizontal login"
					action="./userLoginCheckOut.do" method="post">
					<div class="form-group login-title">
						<strong>Please Login</strong>
					</div>
					<div class="alert alert-dismissable alert-danger text-center"
						style="display: none" id="warning">
						<button type="button" class="close">×</button>
						<strong>User Name or Password is incorrect!</strong>
					</div>
					<div class="form-group">
						<label for="inputUserName" class="col-sm-4 control-label">User</label>
						<div class="col-sm-8">
							<input type="text" name="userName" class="form-control"
								id="username" />
						</div>
					</div>
					<div class="form-group">
						<label for="inputPassword" class="col-sm-4 control-label">Password</label>
						<div class="col-sm-8">
							<input type="password" name="passWord" class="form-control"
								id="password" />
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-4 col-sm-8">
							<div class="checkbox">
								<label><input type="checkbox" id="remember" />Remember
									me</label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-4 col-sm-8">
							<input type="submit" class="btn btn-default" id="login"
								value="Sign in"><br>
						</div>
					</div>
				</form>
			</div>
			<div class="col-md-4 column"></div>
		</div>
	</div>
</body>
</html>