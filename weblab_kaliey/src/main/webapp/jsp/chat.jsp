<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<head>
<script src="${pageContext.request.contextPath}/js/sockjs-0.3.min.js"></script>
<script src="${pageContext.request.contextPath}/jquery/jquery-3.2.1.js"></script>

<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<link href="${pageContext.request.contextPath}/css/font-awesome.css" type="text/css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/chat.css" type="text/css" rel="stylesheet">
<script type="text/javascript">
	$(function(){
		var websocket;
        if('WebSocket' in window) {
           	//console.log("此浏览器支持websocket");
           	if(websocket == null) {
            	websocket = new WebSocket("ws://"+window.location.host+"/weblab/myHandler.do");
           	}
        } else if('MozWebSocket' in window) {
            alert("This browser only supports MozWebSocket");
        } else {
            alert("This browser only supports SockJS");
        }
        
        websocket.onmessage = function(evnt) {
        	var data = JSON.parse(evnt.data);
        	if(data.onlineFlag=="true"){
        		$("#inbox_chat").html("");
        		for(var i=0;i<data.users.length;i++){
	        		$("#inbox_chat").html(
	        			$("#inbox_chat").html()+
		        		"<div class='chat_list active_chat'>"+
		        		"<div class='chat_people'>"+
		        		"<div class='chat_img'>"+
		        		"<img src='./img/user-profile.png' alt='sunil'>"+
		        		"</div>"+
		        		"<div class='chat_ib'>"+
		        		"<h5 style='display:inline'>"+
		        		data.users[i]+"<span class='chat_date'></span>"+
		        		"</h5>"+
		        		"<span id='status'> [ on ]</span>"+
		        		"<!-- <p>Simple chat room designed for easier communication.</p> -->"+
		        		"</div>"+
		        		"</div>"+
		        		"</div>"
	        		);
        		}
        	}else if(data.onlineFlag=="false"){
	        	var susername = '${username}';
	        	if(data.username == susername){
	        		$("#chatinfo").html($("#chatinfo").html()+ 
	        		"<div class='outgoing_msg'>"+
							"<div class='sent_msg'>"+
							"<p>"+data.payload+"</p>"+
							"<span class='time_date'>"+data.time+"</span>"+
						"</div>"+
					"</div>"
					);
	        	}else {
	        		var bcount = parent.$("#bcount").text();
	        		bcount++;
	        		parent.$("#bcount").text(bcount);
	        		parent.$("#bcount").attr("style","background-color:red");
	        		parent.$("#bell").attr("style","color:red");
	        		parent.$("#chat").attr("class","shake shake-constant shake-constant--hover");
	        		
	        		$("#chatinfo").html($("#chatinfo").html()+ 
	       				"<div class='incoming_msg'>"+
	        				"<div class='incoming_msg_img'>"+
	        					"<img src='./img/user-profile.png' alt='sunil'>"+
	        				"</div>"+
	        				"<div class='received_msg'>"+
		        				"<div class='received_withd_msg'>"+
			        				"<span>"+data.username+"</span>"+
			        				"<p>"+data.payload+"</p>"+
			        				"<span class='time_date'>"+data.time+"</span>"+
		        				"</div>"+
	        				"</div>"+
	       				"</div>"
	      			);
				}
	        	$('.msg_history').scrollTop($('.msg_history')[0].scrollHeight);
        	}
        	//{
				//"len": 2,
				//"payload": "11",
				//"time": "2019-01-16 14:09:52",
				//"username": "0.7120070231332593"
			//}
            //$("#chatinfo").html($("#chatinfo").html() + "<br/>" + evnt.data);
        };
        websocket.onerror = function(evnt) {
        	$("#status").text(" [ error ]")
        };
        websocket.onopen = function(evnt) {
            $("#status").text(" [ on ]")
        };
        websocket.onclose = function(evnt) {
            $("#status").text(" [ off ]")
        }
        $("#msg_send_btn").click(function(){
        	send();
        });
        function send() {
            if(websocket != null) {
                var message = document.getElementById('write_msg').value;
                if(message.trim()==""){
                	alert("please input something");
                	return;
                }
                websocket.send(message);
            } else {
                alert('未与服务器链接.');
            }
        }
	});
</script>
</head>
<body>
	<div class="container" >
		<div class="messaging">
			<div class="inbox_msg">
				<div class="inbox_people">
					<!-- <div class="headind_srch">
						<div class="recent_heading">
							<h4 style="display: inline;"></h4>
							<span id="tou"></span>
						</div>
					</div> -->
					<div class="inbox_chat" id="inbox_chat">
						<%-- <c:if test="${online!=null && fn:length(online) > 0}">
							<c:forEach items="${online}" var="ds">
								<div class="chat_list active_chat">
									<div class="chat_people">
										<div class="chat_img">
											<img src="./img/user-profile.png" alt="sunil">
										</div>
										<div class="chat_ib">
											<h5>
												${ds }<span class="chat_date"></span>
											</h5>
											<!-- <p>Simple chat room designed for easier communication.</p> -->
										</div>
									</div>
								</div>
							</c:forEach>
						</c:if> --%>
						<!-- <div class="chat_list active_chat">
							<div class="chat_people">
								<div class="chat_img">
									<img src="./img/user-profile.png" alt="sunil">
								</div>
								<div class="chat_ib">
									<h5>
										SUnit<span class="chat_date"></span>
									</h5>
									<p>Simple chat room designed for easier communication.</p>
								</div>
							</div>
						</div> -->
					</div>
				</div>
				<div class="mesgs">
					<div class="msg_history">
						<div id="chatinfo"></div>
						<!-- <div class="incoming_msg">
							<div class="incoming_msg_img">
								<img src="./img/user-profile.png" alt="sunil">
							</div>
							<div class="received_msg">
								<div class="received_withd_msg">
									<span>root</span>
									<p>11Test which is a new approach to have all solutions</p>
									<span class="time_date"> 11:01 AM | June 9</span>
								</div>
							</div>
						</div>
						<div class="outgoing_msg">
							<div class="sent_msg">
								<p>Test which is a new approach to have all solutions</p>
								<span class="time_date"> 11:01 AM | June 9</span>
							</div>
						</div> -->
					</div>
					<div class="type_msg">
						<div class="input_msg_write">
							<input id="write_msg" type="text" class="write_msg" placeholder="Type a message" style="padding-left: 10px;"/>
							<button class="msg_send_btn" type="button" id="msg_send_btn" style="margin-right:5px;">
								<i class="fa fa-paper-plane-o" aria-hidden="true"></i>
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>