<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<script src="./js/bootstrap-select.min.js"></script>
<link href="./jquery-ui/jquery-ui.min.css" rel="stylesheet">
<link href="./css/bootstrap-select.min.css" rel="stylesheet">
<script src="./jquery-ui/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<script src="${pageContext.request.contextPath}/js/loading.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/loading.css" rel="stylesheet" />
<!-- <link href="./css/adminstyle.css" rel="stylesheet"> -->
<title>SPA and RTDB</title>
<style type="text/css">
.ln {
	margin-right: 10px;
}
.dropdown-toggle{
	padding-top: 3px;
    padding-bottom: 3px;
}
.bootstrap-table .JCLRgrip.JCLRLastGrip {
    display: none;
}
.fixed-table-container{
	height:auto; padding-bottom: 41px;
}
.fixed-table-body{
	height:auto;
}
</style>
<script>
$(function() {
	//得到查询的参数
	queryParams = function (params) {
	    var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
	        limit: params.limit,   //页面大小
	        offset: params.offset  //页码
	    };
	    return temp;
	};
	$('#runLog').bootstrapTable({
        url: 'searchCaseRunLog.do',   //请求后台的URL（*）
        method: 'get',                      //请求方式（*）
        toolbar: '#toolbar',                //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: false,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: queryParams,//传递参数（*）
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber:1,                       //初始化加载第一页，默认第一页
        pageSize: 10,                       //每页的记录行数（*）
        pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
        //search: true,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
        strictSearch: true,
        showColumns: true,                  //是否显示所有的列
        showRefresh: true,                  //是否显示刷新按钮
        minimumCountColumns: 2,             //最少允许的列数
        clickToSelect: true,                //是否启用点击选中行
        //height: 500,                       //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "case_name",               //每一行的唯一标识，一般为主键列
        showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        detailView: false,                   //是否显示父子表
        rowStyle: function (row, index) {
            //这里有5个取值代表5中颜色['active', 'success', 'info', 'warning', 'danger'];
            var strclass = "";
            if (row.s_case == '1') {
            	if(row.f_case == '1'){
	                strclass = 'warning';//还有一个active
            	}else if (row.f_case == "0"){
            		strclass = 'success';//还有一个active
            	}
            } else if (row.s_case == "0") {
            	strclass = 'danger';//还有一个active
            } else{
            	return {}
            }
            return { classes: strclass }
        },
        columns: [ {
            field: 'int_id',
            title: 'int_id'
        }, {
            field: 'title',
            title: 'title',
            formatter:function(value,row,index){
            	var a = '<a href="./searchCaseRunLogInfo.do?int_id='+row.int_id+'" >'+value+'</a>';
            	return a;
            }
        }, {
            field: 'condition',
            title: 'condition'
        }, {
            field: 'author',
            title: 'author'
        }, {
            field: 'datetime',
            title: 'datetime'
        }
        ]
    });
	//var u = Math.random(1000)
	
	
	var conform = false;
	
	function getCondition (){
		var condition = "";
		var ds = $('option[name="ds"]:checked');
		var r = $('option[name="r"]:checked');
		var c = $('option[name="c"]:checked');
		var bd = $('option[name="bd"]:checked');
		var m = $('option[name="m"]:checked');
		var ln = $('option[name="ln"]:checked');
		var sd = $('option[name="sd"]:checked');
		var pr = $('option[name="pr"]:checked');
		var cs = $('option[name="cs"]:checked'); 
		var fi = document.getElementById("featureid").value;
		var author = document.getElementById("author").value;
		//var server = document.getElementById("server").options[document.getElementById("server").selectedIndex].value;
		var server =$('#server').val(); 
		if(server.length==0){
			$("#reminder").dialog({
				open : function(event, ui) {
					$(this).html("");
					$(this).append("<p>Please select a server to run case !</p>");
				}
			});
			return;
		}
		if (ds.length == 0 && r.length == 0 && c.length == 0 && bd.length == 0 && m.length == 0
				&& ln.length == 0 && sd.length == 0	&& pr.length == 0 && cs.length == 0 && fi == "" && author == "") {
			$("#reminder").dialog({
				open : function(event, ui) {
					$(this).html("");
					$(this).append("<p>Please select a query condition!</p>");
				}
			});
			return;
		}
		if(ds.length == 0){
			condition += ";";
		}else{
			for (var i = 0; i < ds.length; i++) {
				condition += ds.get(i).value;
				if(i == ds.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}
		
		if(r.length == 0){
			condition += ";";
		}else{
			for (var i = 0; i < r.length; i++) {
				condition += r.get(i).value;
				if(i == r.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}
		
		if(c.length == 0){
			condition += ";";
		}else{
			for (var i = 0; i < c.length; i++) {
				condition += c.get(i).value;
				if(i == c.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}	
			
		if(bd.length == 0){
			condition += ";";
		}else{
			for (var i = 0; i < bd.length; i++) {
				condition += bd.get(i).value;
				if(i == bd.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}
		
		if(m.length == 0){
			condition += ";";
		}else{
			for (var i = 0; i < m.length; i++) {
				condition += m.get(i).value;
				if(i == m.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}
		
		if(ln.length == 0){
			condition += ";";
		}else{
			for (var i = 0; i < ln.length; i++) {
				condition += ln.get(i).value;
				if(i == ln.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}	
			
		
		if(sd.length == 0){
			condition += ";";
		}else{
			for (var i = 0; i < sd.length; i++) {
				condition += sd.get(i).value;
				if(i == sd.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}
		
		if(pr.length == 0){
			condition += ";";
		}else{	
			for (var i = 0; i < pr.length; i++) {
				condition += pr.get(i).value;
				if(i == pr.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}	
			
		if(cs.length == 0){
			condition += ";";
		}else{	
			for (var i = 0; i < cs.length; i++) {
				condition += cs.get(i).value;
				if(i == cs.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}	
			
		condition += fi;
		condition += ";";
		condition += author;
		condition += ";";
		condition += server;
		condition += "";
		
		return condition;
	}
	
	  
	$("#btn_query").click(function(){
		var condition = "";
		condition = getCondition();
		var oTable = new TableInit(condition);
	    oTable.Init();
		$('#tb_departments').bootstrapTable('refresh',{
    		url: 'searchCaseInfo.do',
    		queryParams: oTable.queryParams		
    	});
    });
	
	
	var TableInit = function (outCondition) {
	    var oTableInit = new Object();
	    //初始化Table
	    oTableInit.Init = function () {
	    	
	        $('#tb_departments').bootstrapTable({
	            url: 'searchCaseInfo.do',   //请求后台的URL（*）
	            method: 'get',                      //请求方式（*）
	            toolbar: '#toolbar',                //工具按钮用哪个容器
	            striped: true,                      //是否显示行间隔色
	            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
	            pagination: true,                   //是否显示分页（*）
	            sortable: false,                     //是否启用排序
	            sortOrder: "asc",                   //排序方式
	            queryParams: oTableInit.queryParams,//传递参数（*）
	            sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
	            pageNumber:1,                       //初始化加载第一页，默认第一页
	            pageSize: 10,                       //每页的记录行数（*）
	            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
	            //search: true,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
	            strictSearch: true,
	            showColumns: true,                  //是否显示所有的列
	            showRefresh: true,                  //是否显示刷新按钮
	            minimumCountColumns: 2,             //最少允许的列数
	            clickToSelect: true,                //是否启用点击选中行
	            //height: 500,                       //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
	            uniqueId: "case_name",               //每一行的唯一标识，一般为主键列
	            showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
	            cardView: false,                    //是否显示详细视图
	            detailView: false,                   //是否显示父子表
	            columns: [{
	                checkbox: true
	            },  {
	                field: 'case_name',
	                title: 'case_name'
	            }, {
	                field: 'release',
	                title: 'release'
	            }, {
	                field: 'customer',
	                title: 'customer'
	            }, {
	                field: 'base_data',
	                title: 'base_data'
	            }, {
	                field: 'mate',
	                title: 'mate'
	            }, {
	                field: 'lab_number',
	                title: 'lab_number'
	            }, {
	                field: 'special_data',
	                title: 'special_data'
	            }, {
	                field: 'porting_release',
	                title: 'porting_release'
	            }, {
	                field: 'author',
	                title: 'author'
	            } , {
	                field: 'case_status',
	                title: 'case_status'
	            }  
	            ]
	        });
	    };
	   	//var u = Math.random(1000)
	    //得到查询的参数
	    oTableInit.queryParams = function (params) {
	        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
	            limit: params.limit,   //页面大小
	            offset: params.offset,  //页码
	            caseName:$("#txt_search_case_name").val(),
	            condition: outCondition
	        };
	        return temp;
	    };
	    return oTableInit;
	};


	/* var ButtonInit = function () {
	    var oInit = new Object();
	    var postdata = {};

	    oInit.Init = function () {
	        //初始化页面上面的按钮事件
	    };

	    return oInit;
	}; */
	
	$("#search").click(function() {
		//$('#tb_departments').bootstrapTable('refreshOptions',{condition: outCondition}); 
		var outCondition = getCondition();
		if(outCondition==null){
			return false;
		}
		var oTable = new TableInit(outCondition);
		oTable.Init();
	     
	    var ser = $('#server').val();
	    $("#ser").html(""+ser);
	    $('#myModal').modal('show');
		//confirm(condition, "The lib will run case according to this condition, please submit it carefully !");
	});
	$("#myModal").on("hide.bs.modal",function(e){window.location.reload()});  
	var ids="";
	$("#Rerunning").click(function(){
		var a  = $('#tb_departments').bootstrapTable('getSelections');
		if(a.length==0){
			alert("At least checked one line");
			return false;
		}else{
			for(var i=0;i<a.length;i++){
				if(i==0 || i=="0"){
					ids+=a[i].case_name;
				}else{
					ids+=","+a[i].case_name;
				}
			}
			$('#delcfmModel').modal("show");
		}
		
    });
	
	$("#ids_confirm").click(function(){
		alert(ids)
		$('#delcfmModel').modal('hide');
		var condition = getCondition();
		//alert(ids);
		$.get("rerunning.do", {
			ids : ids,
			condition: condition
		}, function(data) {
			if(data.msg != null){
				alert(data.msg);
			}
			//alert("success case count:"+data.s_case+" fail case count:"+data.f_case)
			if(data.s_case != null){
				alert("success case count:"+data.s_case+" fail case count:"+data.f_case)
			}
			$('#loaddingModel').modal('hide');
			$('#myModal').modal('hide');
		});
		ids="";
		$('#loaddingModel').modal('show');
		
		
		
		
	});
	
	
	
	
	
	$(".fixed-table-container").css("padding-bottom","41px");
});
</script>
</head>
<body>
<%
		String auth = session.getAttribute("auth").toString();
%>
			<div class="panel panel-default" style="margin-top: 10px;margin-left: 10px;margin-right: 10px;">
			<font color="while"></font>
				<div class="panel-body" style="padding-top: 0px;">
            	<div id="reminder"></div>
            	<div class="row">
            		<form action="com.alucn.web.server/SRMangerServlet" method="post" name="submitspartdb" id="submitspartdb">
            		<div class="col-md-12" style="padding-right: 0px;padding-left: 0px;">
            			<div class="panel-group">
			            <div class="panel panel-default">
			                <!-- 折叠panel头部 -->
			                <div class="panel-heading">
			                    <h4 class="panel-title" data-toggle="collapse" data-target="#chanel_demo">
			                    <a href="#" >More Options</a>
			                    <span class="glyphicon glyphicon-chevron-down navbar-right" style="padding-right: 10px;"></span>
			                    </h4>
			                </div>
			                <!-- 折叠panel内容 -->
			                <div class="collapse panel-collapse" id="chanel_demo"><!-- 添加一个collapse类会默认隐藏折叠内容 -->
			                    <div class="panel-body">
			                            
										
										
										<div class="row" style="margin-bottom: 10px;margin-right: 10px;">
											<div class="col-md-6">
												<h4 style="display: inline;">Author</h4>
												<input class="nav navbar-nav navbar-right" type="text" name="author" id="author" value=""
												style="padding-bottom: 4px; padding-top: 4px;width: 219px;border-radius:4px;">
											</div>
											<div class="col-md-6">
												<h4 style="display: inline;">Base Data</h4>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" >
												    <c:if test="${base_data!=null && fn:length(base_data) > 0}">
														<c:forEach items="${base_data}" var="bd">
															<option name="bd" value="${bd}">${bd}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
										</div>
										
										
										<div class="row" style="margin-bottom: 10px;margin-right: 10px;">
											<div class="col-md-6">
												<h4 style="display: inline;">Mate</h4>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" data-max-options="1">
												    <c:if test="${mate!=null && fn:length(mate) > 0}">
														<c:forEach items="${mate}" var="m">
															<option  name="m" value="${m}">${m}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
											<div class="col-md-6">
												<h4 style="display: inline;">Lab Number</h4>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" data-max-options="1">
												    <c:if test="${lab_number!=null && fn:length(lab_number) > 0}">
														<c:forEach items="${lab_number}" var="ln">
															<option name="ln" value="${ln}">${ln}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
										</div>
										
										
										<div class="row" style="margin-bottom: 10px;margin-right: 10px;">
											<div class="col-md-6 ">
												<h4 style="display: inline;">Special Data</h4>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" data-max-options="1">
												    <c:if test="${special_data!=null && fn:length(special_data) > 0}">
														<c:forEach items="${special_data}" var="sd">
															<option name="sd" value="${sd}">${sd}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
											<div class="col-md-6 " >
												<h4 style="display: inline;">Porting Release</h4>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" >
												    <c:if test="${porting_release!=null && fn:length(porting_release) > 0}">
														<c:forEach items="${porting_release}" var="pr">
															<option name="pr" value="${pr}">${pr}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
											
										</div>
			                    </div>
			                </div>              
			            </div>
			        </div>
            		</div>
            		<div class="col-md-12">
            			<div class="row" style="margin-bottom: 10px;">
							<div class="col-md-4">
								<div style="margin-right: 13px;">
									<h4 style="display: inline;">Release</h4>
									<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" >
									    <c:if test="${release!=null && fn:length(release) > 0}">
											<c:forEach items="${release}" var="r">
												<option name="r" value="${r}">${r}</option>
											</c:forEach>
										</c:if>
									</select>
								</div>
							</div>
							<div class="col-md-4">
								<div style="margin-right: 13px;">
									<h4 style="display: inline;">Case Status</h4>
									<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" >
									    <c:if test="${case_status!=null && fn:length(case_status) > 0}">
											<c:forEach items="${case_status}" var="cs">
												<option name="cs" value="${cs}">${cs}</option>
											</c:forEach>
										</c:if>
									</select>
								</div>
							</div>
							<div class="col-md-4">
									<div style="margin-right: 28px;">
									<h4 style="display: inline;">Customer</h4>
									<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" >
									    <c:if test="${customer!=null && fn:length(customer) > 0}">
											<c:forEach items="${customer}" var="c">
												<option name="c" value="${c}">${c}</option>
											</c:forEach>
										</c:if>
									</select>
									</div>
							</div>
						</div>
            			<div class="row" style="margin-bottom: 10px">
            				<div class="col-md-4">
	            				<div style="margin-right: 13px;">
									<h4 style="display: inline;">Data Source</h4>
									<select class="selectpicker nav navbar-nav navbar-right" title="--Please Select--" multiple data-live-search="true" data-max-options="1" >
									    <c:if test="${data_source!=null && fn:length(data_source) > 0}">
											<c:forEach items="${data_source}" var="ds">
												<option name='ds' value="${ds}">${ds}</option>
											</c:forEach>
										</c:if>
									</select>
								</div>
							</div>
            				<div class="col-md-4">
            					<div style="margin-right: 13px;">
									<h4 style="display: inline;">Lab Cluster</h4>
									<select id="server" name="server" title="--Please Select--" class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" data-max-options="1">
										<c:if test="${servers!=null && fn:length(servers) > 0}">
											<%-- <% if(auth.equals("errorCases")){ %> --%>
												<c:forEach items="${servers}" var="s">
													<option name='s' value="${s}">${s}</option>
												</c:forEach>
											<%-- <%}%> --%>
											</c:if>
									</select>
								</div>
							</div>
							<div class="col-md-4">
								<div style="margin-right: 13px;">
									<h4 style="display: inline;">Feature ID</h4>
									<input class="nav navbar-nav navbar-right" type="text" name="featureid" id="featureid" value=""
										style="padding-bottom: 2px; padding-top: 2px;width: 219px;margin-right: 0.1px;border-radius:4px;">
								</div>
							</div>
            			</div>
            		</div>
            		<br/><br/>
					
					<div class="row">
						<div class="col-md-12 column text-right">
							<!-- <button type="button" class="btn btn-danger" id="add" style="margin-right: 10px;">update DB</button> -->
							<button type="button" class="btn btn-primary " id="search" style="margin-right: 30px;">&nbsp;&nbsp;&nbsp;Search&nbsp;&nbsp;&nbsp;</button>
						</div>
					</div>
				</form>
            	</div>
            	<br>
            	<div class="row" style="padding-bottom: 5px;padding-top:5px;background-color: #E0E0EB">
            	</div>
            	<div class="row" style="margin-left: 10px;margin-right: 13px;margin-top: 10px;">
					<table id="runLog"  class="text-nowrap"></table>
				</div>
            	<div id="myModal" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog">
				    <div class="modal-dialog modal-lg" role="document">
				        <div class="modal-content">
				            <div class="modal-header">
				                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span>
				                </button>
				                 <h4 class="modal-title">Select Case</h4>
				 
				            </div>
				            <div class="modal-body">
				            	<form id="formSearch" class="form-horizontal">
				                    <div class="form-group" style="margin-top:15px">
				                        <label class="control-label col-sm-2" for="txt_search_feature">case_name</label>
				                        <div class="col-sm-3">
				                            <input type="text" class="form-control" id="txt_search_case_name">
				                        </div>
				                        <div class="col-sm-3"></div>
				                        <div class="col-sm-4" style="text-align:right;">
				                            <button type="button" style="margin-left:50px" id="btn_query" class="btn btn-primary ">Search</button>
				                        </div>
				                    </div>
				                </form>
				                <div class="row">
				                	<div class="col-md-12 table-responsive" style="height:398px;overflow:scroll">
					                	<table id="tb_departments" class="table table-bordered table-striped text-nowrap" style="width:100%;height: 100%;"></table>
				                	</div>
				                </div>
				                <h4>Please select case to be run in <strong style="color: red;"><span id="ser"></span></strong></h4>
				            </div>
				            <div class="modal-footer">
				                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				                <button type="button" class="btn btn-primary" id="Rerunning">&nbsp;&nbsp;&nbsp;&nbsp;Run&nbsp;&nbsp;&nbsp;&nbsp;</button>
				            </div>
				        </div>
				        <!-- /.modal-content -->
				    </div>
				    <!-- /.modal-dialog -->
				</div>
				<!-- /.modal -->
				
    		</div>
    	</div>
    	<!-- 信息删除确认 -->  
		<div class="modal fade" id="delcfmModel">  
		  <div class="modal-dialog">  
		    <div class="modal-content message_align">  
		      <div class="modal-header">  
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>  
		        <h4 class="modal-title">attention</h4>  
		      </div>  
		      <div class="modal-body">  
		      	<p>The lib will run case according to this condition, please submit it carefully !</p>
		        <p>You are sure to rerun these cases ?</p>
		      </div>  
		      <div class="modal-footer">  
		         <button type="button" class="btn btn-default" data-dismiss="modal">cancel</button>  
		         <button type="button" class="btn btn-primary" data-dismiss="modal" id="ids_confirm">confirm</button>  
		      </div>  
		    </div><!-- /.modal-content -->  
		  </div><!-- /.modal-dialog -->  
		</div><!-- /.modal -->  
    	<!--等待页面 -->  
		<div class="modal fade" id="loaddingModel">  
		  <div class="modal-dialog">  
		    <div class="modal-content message_align">  
		      <div class="modal-header">  
		        <h4 class="modal-title"><center>loadding</center></h4>  
		      </div>  
		      <div class="modal-body">  
		        <center>
		        	  <div class="spinner spinnerOne"></div>
					  <div class="spinner spinnerTwo"><span></span></div>
					  <div class="spinner spinnerThree"><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span>
					  </div>
					  <div class="spinner spinnerFour"><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span>
					  </div>
					  <div class="spinner spinnerFive"><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span>
					  </div>
		        </center>
		      </div>  
		    </div><!-- /.modal-content -->  
		  </div><!-- /.modal-dialog -->  
		</div><!-- /.modal -->  
		
</body>
</html>