<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="./jquery/jquery-3.2.1.js"></script>
<style>
* {
    box-sizing: border-box;
}
html {
    display: table;
    height: 100%;
    width:100%;
    margin: 0;
    padding: 0;
    background-color: #282828;
}

.block{
    display: table;
    height: 100%;
    width:100%;
    margin: 0;
    padding: 0;
    text-align: center;
    font-family: 'Roboto', sans-serif;
}

.block__cell{
    padding-left: 200px;
    padding-top: 200px;
    display: table-cell;
    vertical-align: middle;
}

.btn{
    text-decoration: none;
    line-height: 46px;
    padding: 0 30px 0 55px;
    position: relative;
    text-align: center;
    display: inline-block;
    background-color: #319bef;
    color: #fff;
    font-weight: 500;
    border-radius: 23px;
    font-size: 16px;
    transition:  all 0.5s linear;
    -o-transition:  all 0.5s linear;
    -webkit-transition: all 0.5s linear;
    -moz-transition: all 0.5s ease;
  overflow: hidden;
}

.btn__icon{
    width: 24px;
    height: 24px;
    background-color: #fff;
    border: 0px solid #319bef;
    border-radius: 50%;
    display: inline-block;
    top: 11px;
    position: absolute;
    left: 20px;


}
.btn .btn__icon:before{
    content: '';
    left:0px;
    top:0px;
    position: absolute;
    transition:  all 0.3s linear;
    -o-transition:  all 0.3s linear;
    -webkit-transition: all 0.3s linear;
    -moz-transition: all 0.3s ease;
}

.btn--activate .btn__icon:before{
    width: 24px;
    height: 24px;
    background-image: url('data:image/svg+xml;utf8;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iaXNvLTg4NTktMSI/Pgo8IS0tIEdlbmVyYXRvcjogQWRvYmUgSWxsdXN0cmF0b3IgMTYuMC4wLCBTVkcgRXhwb3J0IFBsdWctSW4gLiBTVkcgVmVyc2lvbjogNi4wMCBCdWlsZCAwKSAgLS0+CjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+CjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgdmVyc2lvbj0iMS4xIiBpZD0iQ2FwYV8xIiB4PSIwcHgiIHk9IjBweCIgd2lkdGg9IjI0cHgiIGhlaWdodD0iMjRweCIgdmlld0JveD0iMCAwIDI2OC44MzEgMjY4LjgzMiIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgMjY4LjgzMSAyNjguODMyOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+CjxnPgoJPHBhdGggZD0iTTIyMy4yNTUsODMuNjU5bC04MC03OS45OThjLTQuODgxLTQuODgxLTEyLjc5Ny00Ljg4MS0xNy42NzgsMGwtODAsODBjLTQuODgzLDQuODgyLTQuODgzLDEyLjc5NiwwLDE3LjY3OCAgIGMyLjQzOSwyLjQ0LDUuNjQsMy42NjEsOC44MzksMy42NjFzNi4zOTctMS4yMjEsOC44MzktMy42NjFsNTguNjYxLTU4LjY2MXYyMTMuNjU0YzAsNi45MDMsNS41OTcsMTIuNSwxMi41LDEyLjUgICBjNi45MDEsMCwxMi41LTUuNTk3LDEyLjUtMTIuNVY0Mi42NzdsNTguNjYxLDU4LjY1OWM0Ljg4Myw0Ljg4MSwxMi43OTcsNC44ODEsMTcuNjc4LDAgICBDMjI4LjEzNyw5Ni40NTUsMjI4LjEzNyw4OC41NDEsMjIzLjI1NSw4My42NTl6IiBmaWxsPSIjMzE5YmVmIi8+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPGc+CjwvZz4KPC9zdmc+Cg==');
    background-repeat: no-repeat;
    background-size: 10px;
    background-position-x: center;
    background-position-y: center;

}

.btn .btn__icon:after{
    content: '';
    top: 0px;
    left: 0px;
    position: absolute;
    transition:  all 0.3s linear;
    -o-transition:  all 0.3s linear;
    -webkit-transition: all 0.3s linear;
    -moz-transition: all 0.3s linear;
}

.btn--activate .btn__icon:after{
   width: 24px;
   height: 24px;
   background-image: url('data:image/svg+xml;utf8;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iaXNvLTg4NTktMSI/Pgo8c3ZnIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDI2IDI2IiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAyNiAyNiIgd2lkdGg9IjI0cHgiIGhlaWdodD0iMjRweCI+CiAgPHBhdGggZD0ibS4zLDE0Yy0wLjItMC4yLTAuMy0wLjUtMC4zLTAuN3MwLjEtMC41IDAuMy0wLjdsMS40LTEuNGMwLjQtMC40IDEtMC40IDEuNCwwbC4xLC4xIDUuNSw1LjljMC4yLDAuMiAwLjUsMC4yIDAuNywwbDEzLjQtMTMuOWgwLjF2LTguODgxNzhlLTE2YzAuNC0wLjQgMS0wLjQgMS40LDBsMS40LDEuNGMwLjQsMC40IDAuNCwxIDAsMS40bDAsMC0xNiwxNi42Yy0wLjIsMC4yLTAuNCwwLjMtMC43LDAuMy0wLjMsMC0wLjUtMC4xLTAuNy0wLjNsLTcuOC04LjQtLjItLjN6IiBmaWxsPSIjMmY4OWQxIi8+Cjwvc3ZnPgo=');
    background-repeat: no-repeat;
    background-size: 8px;
    background-position-x: center;
    background-position-y: 34px;
}

.btn--activate:hover{
    background-color: #2f89d1;
}

.btn--activate:hover .btn__icon{
    border-color: #2f89d1;
}

.btn--activate:hover .btn__icon:before{
    background-position-y: -34px;
}

.btn--activate:hover .btn__icon:after{
    background-position-y: center;
}

.btn--waiting{
  background-color: #2f89d1;
}

.btn--waiting .btn__icon{
background-color: transparent;
}

/*.btn--waiting .btn__icon:before{
  width:20px;
  height:20px;
  top: 2px;
  left: 2px;
  border-radius:50%;
  background-color:#2f89d1;
  z-index: 1;
}*/

.btn--waiting .btn__icon:after{
  width:20px;
  height:20px;
  top: 0px;
  left: 0px;
  border-radius:50%;
  animation:rotation infinite linear 0.5s;
  transition:none;
  border-top: 2px solid transparent;
  border-left: 2px solid #fff;
  border-right: 2px solid transparent;
  border-bottom: 2px solid transparent;
  z-index: 0;
}


.btn--activated {
  background-color: #44cc71;
}

.btn--activated .btn__icon:after{
  background-image: url(data:image/svg+xml;utf8;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iaXNvLTg4NTktMSI/Pgo8c3ZnIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDI2IDI2IiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAyNiAyNiIgd2lkdGg9IjI0cHgiIGhlaWdodD0iMjRweCI+CiAgPHBhdGggZD0ibS4zLDE0Yy0wLjItMC4yLTAuMy0wLjUtMC4zLTAuN3MwLjEtMC41IDAuMy0wLjdsMS40LTEuNGMwLjQtMC40IDEtMC40IDEuNCwwbC4xLC4xIDUuNSw1LjljMC4yLDAuMiAwLjUsMC4yIDAuNywwbDEzLjQtMTMuOWgwLjF2LTguODgxNzhlLTE2YzAuNC0wLjQgMS0wLjQgMS40LDBsMS40LDEuNGMwLjQsMC40IDAuNCwxIDAsMS40bDAsMC0xNiwxNi42Yy0wLjIsMC4yLTAuNCwwLjMtMC43LDAuMy0wLjMsMC0wLjUtMC4xLTAuNy0wLjNsLTcuOC04LjQtLjItLjN6IiBmaWxsPSIjNDRjYzcxIi8+Cjwvc3ZnPgo=);
width: 24px;
height: 24px;
background-size: 10px;
background-repeat: no-repeat;
background-position-x: center;
background-position-y: center;
  animation: activated 0.3s linear 1;
}

.btn__text{
position: relative;

}

.btn__text:before{
content: attr(data-after);
position: absolute;
top: -27px;
  color: transparent;
z-index: -1;
  color: #fff;
        transition:  all 0.2s linear;
    -o-transition:  all 0.2s linear;
    -webkit-transition: all 0.2s linear;
    -moz-transition: all 0.2s linear;
}

.btn__text:after{
content: attr(data-wait);
position: absolute;
  color: transparent;
top: 2px;
  left: 0;
z-index: -1;
  color: #fff;
        transition:  all 0.2s linear;
    -o-transition:  all 0.2s linear;
    -webkit-transition: all 0.2s linear;
    -moz-transition: all 0.2s linear;
}

.btn--waiting .btn__text{
  color: transparent;
}

.btn--waiting .btn__text:after{
top: -13px;
z-index: 1;
}

.btn--activated .btn__text:before{
top: -13px;
z-index: 1;
}

.btn--activated .btn__text{
  color: transparent;
}

@keyframes rotation {
  0% {
    transform: rotateZ(0deg);
  }
  100% {
    transform: rotateZ(360deg);
  }
}

@keyframes activated {
  0% {
    background-position-y: 34px;
  }
  100% {
    background-position-y: center;
  }
}

</style>
</head>
<body>
<div class="block__cell">
    <a href="#" class="btn btn--activate" id="btnActivation">
        <span class="btn__icon"></span>
        <span class="btn__text" data-wait="running" data-after="running" id="btnText"></span>
    </a>
</div>

<script>
$(document).ready(function(){
	
	var running= '${ running}';
	//alert(running);
	if(running){
		$('#btnActivation').addClass('btn--waiting');
	}else {
		$('#btnText').innerText='start';
	}
  	$('#btnActivation').click(function(){
    if(!$('#btnActivation').hasClass(('btn--activated'))){
    	
        $('#btnActivation').removeClass('btn--activate');
	    $('#btnActivation').addClass('btn--waiting');
	    setTimeout(function(){
	      removeWaiting();
	    }, 3000); 
    }

  });
  
  function removeWaiting(){
      $('#btnActivation').removeClass('btn--waiting');
      $('#btnActivation').addClass('btn--activated');
  }
  
});
</script>
</body>
</html>