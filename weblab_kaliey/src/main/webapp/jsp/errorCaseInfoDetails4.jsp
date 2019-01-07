<%@page import="java.util.Set"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="${pageContext.request.contextPath}/jquery/jquery-3.2.1.js"></script>

<script
	src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<link
	href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">

<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />

<script src="${pageContext.request.contextPath}/js/bootstrap-datetimepicker.min.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-datetimepicker.min.css" rel="stylesheet" />

<script type="text/javascript">
	$(document).ready(function() {
						function submitConfirm(info, flag, f, c) {
							$("#reminder").dialog({
												modal : true,
												buttons : {
													"Confirm" : function() {
														if (flag == 1) {
															$.post("./setMarkCase.do",
																	{
																	featureName : $("#featureName").val(),
																	errorcases : c,
																	failedreasons : f
																	},
																	function(data) {
																		$("#reminder").dialog({
																								buttons : {
																										"Confirm" : function() {
																											$(this).dialog("close");
																											window.location.reload()
																										}
																									},
																									open : function(event,ui) {
																										$(this).html("");
																										$(this).append(data);
																									}
																								});
																			});
															//document.submiterrors.submit();//submit the  form
															//$('input:checkbox[name=errorcases]:checked').addClass("disabled");
														} else if (flag == 2) {
															$('input:checkbox').removeAttr('disabled');
															//$('input:checkbox').prop('checked',false);
														}
														$(this).dialog("close");
													},
													"Cancel" : function() {
														$(this).dialog("close");
													}
												},
												open : function(event, ui) {
													$(this).html("");
													$(this).append("<p>" +info+ "</p>");
												}
											});
						}

						$("#reset").bind("click",function() {
											submitConfirm("If check green boxs, last command would be overwrited!",2);
										});

						$("#submiterrors").submit(function() {
											event.preventDefault();
											var failed = $('input[type="radio"][name="failedreasons"]:checked');
											var checkedcase = $('input[type="checkbox"][name="errorcases"]:checked');
											var err_desc = document.getElementById("desc").value;
											if (checkedcase.length == 0) {
												submitConfirm("Please check a case?",3);
												return;
											}
											if (failed.length == 0) {
												submitConfirm("Please check a failed reason?",3);
												return;
											}

											if (err_desc.trim() == "") {
												submitConfirm("Please input a failed describe?",3);
												return;
											}

											var f = "";
											var c = "";
											for (var i = 0; i < failed.length; i++) {
												f = failed.get(i).value;
												f += "@";
												f += err_desc;
											}
											for (var i = 0; i < checkedcase.length; i++) {
												c += checkedcase.get(i).value
												c += ",";
											}
											submitConfirm("Confirm to submit?",1, f, c.substring(0,c.length - 1));

										});
					
						 $('.form_datetime').datetimepicker({
						        //language:  'fr',
						        weekStart: 1,
						        todayBtn:  1,
								autoclose: 1,
								todayHighlight: 1,
								startView: 2,
								forceParse: 0,
						        showMeridian: 1
						    });
					});
	function showTime(){
		var failed = $('input[type="radio"][name="failedreasons"]:checked').val();
		var flag1 = failed.indexOf("code bug") != -1;
		var flag2 = failed.indexOf("case issue") != -1;
		if(flag1 || flag2){
			//alert(failed);
			$("#showtime").removeAttr('style');
		}else{
			$("#dtp_input").val("");
			$("#dtp_input1").val("");
			$("#showtime").attr('style','display: none;');
		}
	}
	
</script>
<title>Feature</title>
</head>
<body style="background-color: #ECEFF3;">
	<div class="panel panel-primary" style="margin: 10px;">
		<div class="panel-heading">
			<h3 class="panel-title">
				<strong>Feature: ${featureName}</strong>
			</h3>
		</div>
		<div class="panel-body">
			<div class="row">
				<div class="col-md-12 column">
					<form action="./setMarkCase.do" method="post" name="submiterrors" id="submiterrors">
						<div class="row">
							<div class="col-md-12 column">
								<h4 class="col-md-12 column">Failed Cases</h4>
								<div style="background-color: white;">
									<div class="row" style="margin-left: 10px; margin-right: 13px; margin-top: 10px;">
										<table id="tb_departments" class="text-nowrap" data-toggle="table">
											<thead>
												<tr>
													<th>CaseName</th>
													<th>Reason</th>
													<th>Desc</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${errorCaseList}" var="errorCaseMap">
													<div class="col-xs-6 col-sm-3 column">
														<label class="checkbox-inline"> 
															<c:choose>
																<c:when test="${errorCaseMap.err_reason!=null && errorCaseMap.err_reason!=\"\"}">
																	<tr>
																		<td>
																			<input type="checkbox" name="errorcases" value="<c:out value="${errorCaseMap.casename}"/>" disabled />
																			<c:if test="${errorCaseMap.report_path!=null}" var="norPathFlag" scope="session">
																				<a href="${errorCaseMap.report_path }" target="_blank">
																					<c:out value="${errorCaseMap.casename}"></c:out>
																					<span class="glyphicon glyphicon-link"></span>
																				</a>
																			</c:if> <c:if test="${not norPathFlag}">
																				<c:out value="${errorCaseMap.casename}"></c:out>
																			</c:if>
																		</td>
																		<td><c:out value="${errorCaseMap.err_reason}" /></td>
																		<td><c:out value="${errorCaseMap.err_desc}" /></td>
																	</tr>
																</c:when>
																<c:when test="${(errorCaseMap.err_reason==null || errorCaseMap.err_reason==\"\") && errorCaseMap.err_reason_his!=null && errorCaseMap.err_reason_his!=\"\"}">
																	<tr>
																		<td>
																			<input type="checkbox" name="errorcases" value="<c:out value="${errorCaseMap.casename}"/>"  />
																			<c:if test="${errorCaseMap.report_path!=null}" var="norPathFlag" scope="session">
																				<a href="${errorCaseMap.report_path }" target="_blank">
																					<font color=red><c:out value="${errorCaseMap.casename}"></c:out></font>
																					<span class="glyphicon glyphicon-link"></span>
																				</a>
																			</c:if> 
																			<c:if test="${not norPathFlag}">
																				<font color=red><c:out value="${errorCaseMap.casename}"></c:out></font>
																			</c:if>
																		</td>
																		<td><font color=gray>[ his : <c:out value="${errorCaseMap.err_reason_his}" /> ]</font></td>
																		<td><font color=gray>[ his : <c:out value="${errorCaseMap.err_desc_his}" /> ]</font></td>
																	</tr>
																</c:when>
																<c:when test="${(errorCaseMap.err_reason==null || errorCaseMap.err_reason==\"\") && (errorCaseMap.err_reason_his==null || errorCaseMap.err_reason_his==\"\")}">
																	<tr>
																		<td>
																			<input type="checkbox" name="errorcases" value="<c:out value="${errorCaseMap.casename}"/>"  />
																			<c:if test="${errorCaseMap.report_path!=null}" var="norPathFlag" scope="session">
																				<a href="${errorCaseMap.report_path }" target="_blank">
																					<font color=red><c:out value="${errorCaseMap.casename}"></c:out></font>
																					<span class="glyphicon glyphicon-link"></span>
																				</a>
																			</c:if> <c:if test="${not norPathFlag}">
																				<font color=red><c:out value="${errorCaseMap.casename}"></c:out></font>
																			</c:if>
																		</td>
																		<td></td>
																		<td></td>
																	</tr>
																</c:when>
															</c:choose>
														</label>
													</div>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
						<br>
						<br>
						<div class="row ">
							<div class="col-md-12 column">
								<h4 class="col-md-12 column">Failed Reason</h4>
								<c:forEach items="${errorReasonList}" var="errorReasonMap">
									<div class="col-xs-6 col-sm-3 column">
										<label class="radio-inline"> 
											<input type="radio" name="failedreasons" value="${errorReasonMap.error_tpye}" onchange="showTime()">
											<span style="color:${errorReasonMap.err_mark}">${errorReasonMap.error_tpye}</span>
										</label>
									</div>
								</c:forEach>
							</div>
						</div>
						<br>
						<br>
						<div class="row" id='showtime' style="display: none;">
							<div class="col-md-12 column">
								<div class="form-group">
					                <label for="dtp_input1" class="col-md-2 control-label">DateTime Picking</label>
					                <div class="input-group date form_datetime col-md-5" data-date-format="dd MM yyyy - HH:ii p" data-link-field="dtp_input1">
					                    <input id="dtp_input" class="form-control" size="16" type="text" value="" readonly>
					                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
										<span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
					                </div>
									<input type="hidden" id="dtp_input1" value="" /><br/>
					            </div>
				            </div>
			            </div>
						<div class="row ">
							<div class="col-md-12 column">
								<h4 class="col-md-12 column">Failed Describe</h4>
								<div class="col-xs-6 col-sm-3 column">
									<label class="radio-inline"> <textarea rows="3"
											cols="135" id="desc" name="desc"></textarea>
									</label>
								</div>
							</div>
						</div>
						<br>
						<br>
						<div class="row ">
							<div class="col-md-12 column text-right">
								<input type="hidden" name="featureName" id="featureName" value=" ${featureName}">
								<button type="button" class="btn btn-default" id="reset">Reset</button>
								<input class="btn btn-default" type="submit" name="submiterrrorreason" value="Submit">
							</div>
						</div>
					</form>
				</div>
			</div>
			<div id="reminder"></div>
		</div>
	</div>
</body>
</html>