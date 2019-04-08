<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0,user-scalable=no">
<title>Nokia Corporation</title>
<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>

<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="bookmark" href="images/favicon.ico" type="image/x-icon" />
<style type="text/css">
.main h1 {
	color: #fff;
	text-shadow:0 0 5px #CCCCCC, 0 0 10px #CCCCCC, 0 0 15px #CCCCCC, 0 0 20px #095816, 0 0 25px #095816, 0 0 30px #095816, 0 0 50px #095816, 0 0 80px #095816, 0 0 100px #095816, 0 0 150px #095816
}
</style>
<script type="text/javascript">
$(function(){
	var s = '${login}，The operation that you access needs to be careful, please contact the super administrator to handle...';
	var con = $('#info');
	var index = 0;
	var length = s.length;
	var tId = null;
	var flag = 0;

	function start(){
	  con.text('');
	  
	  tId=setInterval(function(){
	    con.append(s.charAt(index));
	    if(index++ === length){
		    clearInterval(tId);
		    index = 0;
		    flag++;
		    if(flag>=1){
		    	clearInterval(tId);
		    	return;
		    }else {
		    	start()
			}
   		}
	  },100);
	  
	}

	start();
});

</script>
</head>
<body style="background-color: #FBFCFC">
	<div class="container-fluid" >
		<div class="row" style="margin-left: 30px;margin-top: 30px">
			<div class="col-xs-11 col-md-11 col-lg-11">
				<div class="main">
				  <h1>Attention</h1>
				  <p id="info" ></p>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
