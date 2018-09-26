<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<style>
.onoffswitch {
	margin-top:10px;
	margin-left:10px;
    position: relative; width: 90px;
    -webkit-user-select:none; -moz-user-select:none; -ms-user-select: none;
}
.onoffswitch-checkbox {
    display: none;
}
.onoffswitch-label {
    display: block; overflow: hidden; cursor: pointer;
    border: 2px solid #999999; border-radius: 20px;
}
.onoffswitch-inner {
    display: block; width: 200%; margin-left: -100%;
    transition: margin 0.3s ease-in 0s;
}
.onoffswitch-inner:before, .onoffswitch-inner:after {
    display: block; float: left; width: 50%; height: 30px; padding: 0; line-height: 30px;
    font-size: 14px; color: white; font-family: Trebuchet, Arial, sans-serif; font-weight: bold;
    box-sizing: border-box;
}
.onoffswitch-inner:before {
    content: "ON";
    padding-left: 10px;
    background-color: #34A7C1; color: #FFFFFF;
}
.onoffswitch-inner:after {
    content: "OFF";
    padding-right: 10px;
    background-color: #EEEEEE; color: #999999;
    text-align: right;
}
.onoffswitch-switch {
    display: block; width: 18px; margin: 6px;
    background: #FFFFFF;
    position: absolute; top: 0; bottom: 0;
    right: 56px;
    border: 2px solid #999999; border-radius: 20px;
    transition: all 0.3s ease-in 0s; 
}
.onoffswitch-checkbox:checked + .onoffswitch-label .onoffswitch-inner {
    margin-left: 0;
}
.onoffswitch-checkbox:checked + .onoffswitch-label .onoffswitch-switch {
    right: 0px; 
}
</style>
</head>
<body>
<div class="container-fluid">
		<div class="row">
		  	<div class="col-md-6" style="padding-left: 0px; padding-right: 0px;">
		  		是否正在运行：
			  	<div class="onoffswitch">
				    <input type="checkbox" name="onoffswitch" class="onoffswitch-checkbox" id="myonoffswitch" >
				    <label class="onoffswitch-label" for="myonoffswitch">
				        <span class="onoffswitch-inner"></span>
				        <span class="onoffswitch-switch"></span>
				    </label>
				</div>
				开关:
			  	<div class="onoffswitch">
				    <input type="checkbox" name="onoffswitch" class="onoffswitch-checkbox" id="myonoffswitch2" >
				    <label class="onoffswitch-label" for="myonoffswitch2">
				        <span class="onoffswitch-inner"></span>
				        <span class="onoffswitch-switch"></span>
				    </label>
				</div>
		  	</div>
		</div>
</div>



<script>
setTimeout("self.location.reload();",30000);
$(document).ready(function(){
	
	var running= '${ running}';
	var Flag= '${ Flag}';
	//alert(running);
	if(running=='true'){
		$('#myonoffswitch').attr('checked','checked');
	}
	if(Flag=='true'){
		$('#myonoffswitch2').attr('checked','checked');
	}
  	$('#myonoffswitch').click(function(){
	    if($('#myonoffswitch').is(':checked')){
		    $.ajax({
		    	url:"statusSetJiraCaseTbl.do",
		    	success:function(result){
		    		//alert(result["running"]);
		    		//alert(result["Flag"]);
		        	if(!result["running"]){
		        		$.ajax({
						    	url:"loopSetJiraCaseTbl.do"
						    })
		        		$.ajax({
						    	url:"statusSetJiraCaseTbl.do",
						    	success:function(result){
						    		if(!result["running"]){
						    			$('#myonoffswitch').removeAttr('checked');
						    		}
						    		if(result["Flag"]){
						    			$('#myonoffswitch2').attr('checked','checked');
						    		}
						    	}
						    })
		        	}
		    	}
		    });
	    }else{
	    	return false;
	    }
  	});
  	$('#myonoffswitch2').click(function(){
	    if($('#myonoffswitch2').is(':checked')){
	    	var flag =true;
		    $.ajax({
		    	async:false,
		    	url:"statusSetJiraCaseTbl.do",
		    	success:function(result){
		    		//alert(result["running"]);
		    		//alert(result["Flag"]);
		    		if(result["running"]){
		    			if(!result["Flag"]){
		    				flag = false;
		    				return false;
		    			}
		    		}
		        	if(!result["running"]){
		        		$.ajax({
						    	url:"loopSetJiraCaseTbl.do"
						    })
		        		$.ajax({
						    	url:"statusSetJiraCaseTbl.do",
						    	success:function(result){
						    		if(!result["running"]){
						    			$('#myonoffswitch2').removeAttr('checked');
						    		}else{
						    			$('#myonoffswitch').attr('checked','checked');
						    		}
						    	}
						    })
		        	}
		    	}
		    });
		    if(!flag){
		    	return false;
		    }
	    }else {
	    	$.ajax({
		    	url:"stopSetJiraCaseTbl.do",
		    	success:function(result){
		    		if("turn off"==result){
		    			$('#myonoffswitch2').removeAttr('checked');
		    		}
		    	}
		    })
		}

  });
  
  
});
</script>
</body>
</html>