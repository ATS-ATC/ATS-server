<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<title>Nokia Corporation</title>
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="bookmark" href="images/favicon.ico" type="image/x-icon" />

<%-- <script src="${pageContext.request.contextPath}/js/md5.js"></script> --%>
<%-- <script src="${pageContext.request.contextPath}/js/sha256.js"></script> --%>
<script src="${pageContext.request.contextPath}/js/aes.js"></script>
<script type="text/javascript">
/* $(function(){
	$("#submit").click(function(){
		var passwordfield =  $("#password-field").val();
	 	var str3 = hex_md5(passwordfield);
		//alert(str3);
		$("#password-field").val(str3);
	});
}); */
/* $(function(){
	$("#submit").click(function(){
		var passwordfield =  $("#password-field").val();
	 	var str3 = sha256_digest(passwordfield);
		alert(str3);
		$("#password-field").val(str3);
	});
}); */
/* $(function(){
	$("#submit").click(function(){
		var passwordfield =  $("#password-field").val(); */
	 	/* var str3 = sha256_digest(passwordfield);
		alert(str3); */
		
		
		
		/* var key = CryptoJS.enc.Utf8.parse("8NONwyJtHesysWpM");
	    //var plaintText = 'ABCDEFGH'; // 明文
	    var plaintText = passwordfield; // 明文
	    var encryptedData = CryptoJS.AES.encrypt(plaintText, key, {
	        mode: CryptoJS.mode.ECB,
	        padding: CryptoJS.pad.Pkcs7
	    });
	 
	    console.log("加密前："+plaintText);
	    console.log("加密后："+encryptedData);
	    alert("加密后："+encryptedData); */
	 
	    /* encryptedData = encryptedData.ciphertext.toString();
	 
	    var encryptedHexStr = CryptoJS.enc.Hex.parse(encryptedData);
	    var encryptedBase64Str = CryptoJS.enc.Base64.stringify(encryptedHexStr);
	 
	    var decryptedData = CryptoJS.AES.decrypt(encryptedBase64Str, key, {
	        mode: CryptoJS.mode.ECB,
	        padding: CryptoJS.pad.Pkcs7
	    });
	 
	    var decryptedStr = decryptedData.toString(CryptoJS.enc.Utf8);
	 
	    console.log("解密后:"+decryptedStr); */
	 
		 /* var pwd = "PCsUFtgog9/qpqmqXsuCRQ==";
	    //加密服务端返回的数据
	    var decryptedData = CryptoJS.AES.decrypt(pwd, key, {
	        mode: CryptoJS.mode.ECB,
	        padding: CryptoJS.pad.Pkcs7
	    });
		
		console.log("解密服务端返回的数据:"+decryptedStr); */
		
		
		
		/* $("#password-field").val(str3);
	});
});  */
$(document).keyup(function(event){
    if(event.keyCode ==13){
      $("#submit").trigger("click");
    }
    /* $("#submit").click(function(){
   	 	$("#submit").attr("disabled","disabled");
  		$("#loginForm").submit();
    	setTimeout(function(){
 			$("#submit").removeAttr("disabled");
 		},10000);
    }); */
})
function showPass() {
	var input = $("#password-field");
	var showPass = $("#showPass");
	
	if (input.attr("type") == "password") {
	  	input.attr("type", "text");
	  	showPass.attr("class","glyphicon glyphicon-eye-open");
	} else {
	  	input.attr("type", "password");
	  	showPass.attr("class","glyphicon glyphicon-eye-close");
	}
}; 
</script>
<style type="text/css">
#showPass,#user{
    position:absolute;
    right:10%;
    top:25%
}
</style>
</head>
<body>
	<div class="container-fluid">
		<div class="row">
		  	<div class="col-md-9" style="padding-left: 0px; padding-right: 0px;">
				<img class="img-responsive" alt="Responsive image"  src="images/233.png" style="width: 100%;height: auto;">
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
					<form class="form-horizontal" action="./userLoginCheckOut.do" method="post" id='loginForm'>
					  <div class="form-group">
					    <div class="col-sm-12">
					      <input type="text" class="form-control" id="username" name="username" placeholder="username">
					      <span id="user" class="glyphicon glyphicon-user"></span>
					    </div>
					  </div>
					  <div class="form-group">
					    <div class="col-sm-12">
					      <input type="password" class="form-control" id="password-field" name="password" placeholder="password">
					      <span toggle="#password-field" class="glyphicon glyphicon-eye-close" id="showPass" onclick="showPass()"></span>
					    </div>
					  </div>
					  <input type="hidden" value="${token}" name="Reqtoken"/>
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