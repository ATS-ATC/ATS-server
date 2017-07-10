<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="./jquery-ui/jquery-ui.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<script src="./jquery-ui/jquery-ui.min.js"></script>
<link href="./css/styleDetail.css" rel="stylesheet" type="text/css" />

<title>Add New Server</title>
<script type="text/javascript">
	$(function() {

		function showcheckbox(sr) {
			var id, val;
			var text;
			if (sr == "SPA") {
				id = "#check-spa";
			} else if (sr == "RTDB") {
				id = "#check-rtdb";
			}

			var dialog = $(id)
					.dialog(
							{
								modal : true,
								autoOpen : false,
								title : sr,
								height : 400,
								width : 350,
								buttons : {
									"Confirm" : function() {
										if (sr == "SPA") {
											id = "#check-spa";
											val = $('input[type="checkbox"][name="spa"]:checked');
											text = document
													.getElementById("serverSPA");
										} else if (sr == "RTDB") {
											id = "#check-rtdb";
											val = $('input[type="checkbox"][name="rtdb"]:checked');
											text = document
													.getElementById("serverRTDB");
										}
										text.value = "";
										for (var i = 0; i < val.length; i++) {
											var e = val.get(i);
											if (e.checked == true) {
												text.value = text.value
														+ e.value + ",";
											}
										}
										text.value=text.value.substring(0,text.value.length-1)
										$(this).dialog("close");
									},
									"Cancel" : function() {
										$(this).dialog("close");
									}
								}
							});
			$(id).removeClass("hide");
			dialog.dialog("open");

		}

		$("#checkspa").click(function() {
			showcheckbox("SPA");
		});
		$("#checkrtdb").click(function() {
			showcheckbox("RTDB");
		});

		function addConfirm(info, flag) {
			$("#reminder").dialog({
				modal : true,

				buttons : {
					"Confirm" : function() {
						if (flag == true) {
							$.post("./addServerDetails.do", {
								serverName : $("#serverName").val(),
								serverIp : $('#serverIp').val(),
								serverRelease : $("#serverRelease").val(),
								serverProtocol : $("#serverProtocol").val(),
								serverSPA : $("#serverSPA").val(),
								serverRTDB : $("#serverRTDB").val()
							}, function(data) {
								$("#reminder").dialog({
									buttons : {
										"Confirm" : function() {
											$(this).dialog("close");
										}
									},
									open : function(event, ui) {
										$(this).html("");
										$(this).append(data);
									}
								});
							});
						}
						$(this).dialog("close");
						if(flag){
							location.href="./getServerInfo.do";
						}
					},
					"Cancel" : function() {
						$(this).dialog("close");
					}
				},
				open : function(event, ui) {
					$(this).html("");
					$(this).append("<p>" + info + "</p>");
				}
			});
		}

		$("#addserver").submit(
				function() {
					event.preventDefault(); //prevent submiting the #addserver form
					var info = null;
					if ($("#serverName").val() == ""
							|| $("#serverName").val() == null) {
						info = "Please enter a server name!";
					} else if ($("#serverIp").val() == ""
							|| $("#serverIp").val() == null) {
						info = "Please enter an IP address!";
					} else if ($("#serverRelease").val() == ""
							|| $("#serverRelease").val() == null) {
						info = "Please enter Release Number!";
					} else if ($("#serverProtocol").val() == ""
							|| $("#serverProtocol").val() == null) {
						info = "Please enter a Protocol!";
					} else if ($("#serverSPA").val() == ""
							|| $("#serverSPA").val() == null) {
						info = "Please enter SPAs!";
					} else if ($("#serverRTDB").val() == ""
							|| $("#serverRTDB").val() == null) {
						info = "Please enter RTDBs!";
					}

					if (info == null)
						addConfirm("Confirm to add this server?", true);
					else
						addConfirm(info, false);
				});
	});
</script>
</head>
<body>
	<div class="container">
		<div class="row clearfix">
			<div class="col-md-12 column">
				<nav class="navbar navbar-default" role="navigation">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse"
						data-target="#bs-example-navbar-collapse-1">
						<span class="sr-only">Toggle navigation</span><span
							class="icon-bar"></span><span class="icon-bar"></span><span
							class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="./userLoginBackHome.do">Home</a>
				</div>
				<div class="collapse navbar-collapse"
					id="bs-example-navbar-collapse-1">
					<ul class="nav navbar-nav">
						<li class="active"><a href="./getServerInfo.do">Servers</a></li>
						<li><a href="./getErrorCaseInfo.do">Error Cases</a></li>
						<li class="dropdown"><a href="#" class="dropdown-toggle"
							data-toggle="dropdown">Admin<strong class="caret"></strong></a>
							<ul class="dropdown-menu">
								<li class="active"><a href="./getSpaAndRtdbInfo.do">SPA
										and RTDB</a></li>
										<li><a href="./config.do">Config Manage</a></li>
								<li class="divider"></li>
							</ul></li>
					</ul>
					<form class="navbar-form navbar-left" role="search">
						<div class="form-group">
							<input type="text" class="form-control" />
						</div>
						<button type="submit" class="btn btn-default">Search</button>
					</form>
					<ul class="nav navbar-nav navbar-right">
						<li id="logout"><a href="./userLogout.do">Logout</a></li>
					</ul>
				</div>
				</nav>
			</div>
		</div>
		<%--Navbar over --%>
		<%-- --%>
		<div class="row clearfix">
			<div class="col-md-12 column">
				<form id="addserver" name="addserver" method="post"
					action="./addServerDetails.do">
					<table class="detail table ">
						<tr>
							<td>Server:</td>
							<td><input class="form-control" placeholder="CERTIFIEDXX"
								type="text" name="serverName" id="serverName"></td>
							<td>IP:</td>
							<td><input class="form-control"
								placeholder="135.xxx.^_^.xxx" type="text" name="serverIp"
								id="serverIp"></td>
						</tr>
						<tr>
							<td>Release:</td>
							<td><input class="form-control" placeholder="SP31.2"
								type="text" name="serverRelease" id="serverRelease"></td>
							<td>Protocol:</td>
							<td><select class="form-control" name="serverProtocol"
								id="serverProtocol">
									<option>ANSI</option>
									<option>ITU</option>
							</select></td>
						</tr>
						<tr>
							<td>
								<div class="row clearfix">
									<div class="col-md-12 column">SPA:</div>
								</div>

							</td>
							<td colspan="3" class="text-right">
								<div class="row clearfix">
									<div class="col-md-10 column">
										<input class="form-control" placeholder="Separated by commas"
											type="text" name="serverSPA" id="serverSPA">
									</div>
									<div class="col-md-2 column text-right">
										<button type="button" class="btn btn-default" name="checkspa"
											id="checkspa">Select</button>
									</div>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="row clearfix">
									<div class="col-md-12 column">RTDB:</div>
								</div>
							<td colspan="3" class="text-right">
								<div class="row clearfix">
									<div class="col-md-10 column">
										<input class="form-control" placeholder="Separated by commas"
											type="text" name="serverRTDB" id="serverRTDB">
									</div>
									<div class="col-md-2 column text-right">
										<button type="button" class="btn btn-default "
											name="checkrtdb" id="checkrtdb">Select</button>
									</div>
								</div>
							</td>

						</tr>
						<tr>
							<td>
								<div class="row clearfix">
									<div class="col-md-12 column">Attach Servers:</div>
								</div>
							<td colspan="3" class="text-right">
								<div class="row clearfix">
									<div class="col-md-10 column">
										<input class="form-control" placeholder="Separated by commas"
											type="text" name="serverRTDB" id="serverRTDB">
									</div>
									<div class="col-md-2 column text-right">
										<button type="button" class="btn btn-default "
											name="checkrtdb" id="checkrtdb">Select</button>
									</div>
								</div>
							</td>

						</tr>
						<tr>
							<td colspan="3"></td>
							<td class="submit"><input class="btn btn-default"
								type="submit" value="Install Server" name="addserver"></td>
						</tr>
					</table>
					<div id="check-spa" class="hide">
						<c:if test="${SPA!=null && fn:length(SPA) > 0}">
							<c:forEach items="${SPA}" var="spa">
								<div class="form-group col-md-6 column">
									<label class="checkbox-inline"> <input type="checkbox"
										name="spa" value="${spa}">${spa}
									</label>
								</div>
							</c:forEach>
						</c:if>
					</div>
					<div id="check-rtdb" class="hide">
						<c:if test="${RTDB!=null && fn:length(RTDB) > 0}">
								<c:forEach items="${RTDB}" var="rtdb">
									<div class="form-group col-md-6 column">
										<label class="checkbox-inline"> <input type="checkbox"
											name="rtdb" value="${rtdb}">${rtdb}
										</label>
									</div>
								</c:forEach>
							</c:if>
					</div>
				</form>

				<div id="reminder"></div>
			</div>
		</div>
	</div>

</body>
</html>