<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%--
<meta http-equiv="refresh" content="5*60">
 --%>
<script src="${pageContext.request.contextPath}/jquery/jquery-3.2.1.js"></script>

<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />

<script src="${pageContext.request.contextPath}/js/bootstrap-tagsinput.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-tagsinput.css" rel="stylesheet" />

<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet">

<style type="text/css">
.dropdown-toggle {
	padding-top: 3px;
	padding-bottom: 3px;
}

#edit, #add ,#edelete{
	padding-top: 3px;
	padding-bottom: 3px;
}
</style>
<script type="text/javascript">
	
</script>
<title>Dept Management</title>
</head>
<body style="background-color: #ECEFF3;">
	<!-- style="background-color:#ECEFF3;" -->
	<div class="panel-body" style="padding-bottom: 0px;">
		<div class="panel panel-default">
			<!-- <div class="panel-heading">Dept Management</div>
			<div class="panel-body" style="padding-bottom: 0px;">
				<form id="formSearch" class="form-horizontal">
					<div class="form-group" style="margin-top: 15px">
						<label class="control-label col-sm-2" for="txt_search_feature">Group Name</label>
						<div class="col-sm-3">
							<input type="text" class="form-control" id="txt_search_dept">
						</div>
						<div class="col-sm-3"></div>
						<div class="col-sm-4" style="text-align: right;">
							<button type="button" style="margin-left: 50px" id="btn_query"
								class="btn btn-primary ">Search</button>
						</div>
					</div>
				</form>
			</div> -->
		</div>
		<div id="toolbar" style="text-align: left;">
			<!--
			<shiro:hasPermission name="dept:create">
			<button id="add" class="btn btn-success">
				<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
				Add
			</button>
			</shiro:hasPermission>
			<shiro:hasPermission name="dept:edit">
			<button id="edit" class="btn btn-info">
				<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
				Edit
			</button>
			</shiro:hasPermission>
		 
			<button id="edelete" class="btn btn-danger" onclick="edelete()">
				<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
				delete
			</button> 
			-->
		</div>
		<div style="background-color: white;">
			<div class="row"
				style="margin-left: 10px; margin-right: 13px; margin-top: 10px;">
				<table id="tb_departments" class="text-nowrap"></table>
			</div>
		</div>
	</div>

	<!--新增dept -->
	<div class="modal fade " id="addModal" tabindex="-1" role="dialog" aria-labelledby="addModalLabel" aria-hidden="true">
	    <div class="modal-dialog ">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	                <h4 class="modal-title" id="addModalLabel">Create Group</h4>
	            </div>
	            <div class="modal-body" id="add_body">
	            	<div class="row">
        				<div class="col-md-12">
        					<form class="form-horizontal" role="form">
								  <div class="form-group">
								    <label for="adeptname" class="col-sm-2 control-label" style="text-align: left;">groupname</label>
								    <div class="col-sm-6">
								      <input type="text" class="form-control" id="adeptname"  placeholder="deptname" >
								    </div>
								  </div>
								  <div class="form-group">
								    <label for="aremark" class="col-sm-2 control-label" style="text-align: left;">remark</label>
								    <div class="col-sm-6">
										<input type="text" class="form-control" id="aremark"  placeholder="remark" style="display: inline;">
								    </div>
								  </div>
							</form>
        				</div>
					</div>
				</div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal">Clsoe</button>
	                <button type="button" class="btn btn-primary" id="addSubmit">Submit</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
	<!--修改dept -->
	<div class="modal fade " id="editModal" tabindex="-1" role="dialog" aria-labelledby="editModalLabel" aria-hidden="true">
	    <div class="modal-dialog ">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	                <h4 class="modal-title" id="editModalLabel">Edit Group</h4>
	            </div>
	            <div class="modal-body" id="edit_body">
	            	<div class="row">
        				<div class="col-md-12">
        					<form class="form-horizontal" role="form">
								  <div class="form-group">
								    <label for="eid" class="col-sm-2 control-label" style="text-align: left;"  >id</label>
								    <div class="col-sm-6">
								      <input type="text" class="form-control" id="eid"  placeholder="id" disabled="disabled">
								    </div>
								  </div>
								  <div class="form-group">
								    <label for="edeptname" class="col-sm-2 control-label" style="text-align: left;">groupname</label>
								    <div class="col-sm-6">
								      <input type="text" class="form-control" id="edeptname"  placeholder="deptname" disabled="disabled">
								    </div>
								    <div class="col-sm-4" style="padding-left: 0px;">
								    	<span id="sdept" class="glyphicon glyphicon-edit btn" aria-hidden="true" style="display: inline;"></span>
								    </div>
								  </div>
								  <div class="form-group">
								    <label for="eremark" class="col-sm-2 control-label" style="text-align: left;">remark</label>
								    <div class="col-sm-6">
										<input type="text" class="form-control" id="eremark"  placeholder="remark" disabled="disabled" style="display: inline;">
								    </div>
								    <div class="col-sm-4" style="padding-left: 0px;">
								    	<span id="sremark" class="glyphicon glyphicon-edit btn" aria-hidden="true" style="display: inline;"></span>
								    </div>
								  </div>
								   <div class="form-group">
								    <label for="estateflag" class="col-sm-2 control-label" style="text-align: left;">stateflag</label>
								    <div class="col-sm-6">
									    <div class="radio" style="display: inline;">
										    <label>
										        <input type="radio" name="optionsRadios" id="normal" value="normal"><span class="label label-success">normal</span>
										    </label>
										</div>
										<div class="radio" style="display: inline;">
										    <label>
										        <input type="radio" name="optionsRadios" id="disable" value="disable"><span class="label label-default">disable</span>
										    </label>
										</div>
								  	</div>
								  </div>
							</form>
        				</div>
					</div>
				</div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal">Clsoe</button>
	                <button type="button" class="btn btn-primary" id="editSubmit">Submit</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

</body>
<script type="text/javascript">
	
	$(function() {
		//1.初始化Table
		var oTable = new TableInit();
		oTable.Init();

		//2.初始化Button的点击事件
		var oButtonInit = new ButtonInit();
		oButtonInit.Init();

		$("#btn_query").click(function() {
			$('#tb_departments').bootstrapTable('refresh', {
				url : 'getServerStatusLogJson.do',
				queryParams : oTable.queryParams
			});
		});

	});

	var TableInit = function() {
		var oTableInit = new Object();
		//初始化Table
		oTableInit.Init = function() {
			$('#tb_departments').bootstrapTable({
				url : 'getServerStatusLogJson.do', //请求后台的URL（*）
				method : 'get', //请求方式（*）
				toolbar : '#toolbar', //工具按钮用哪个容器
				toolbarAlign : "left",
				striped : false, //是否显示行间隔色
				cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
				pagination : true, //是否显示分页（*）
				sortable : false, //是否启用排序
				sortOrder : "asc", //排序方式
				queryParams : oTableInit.queryParams,//传递参数（*）
				sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
				pageNumber : 1, //初始化加载第一页，默认第一页
				pageSize : 10, //每页的记录行数（*）
				pageList : [ 10, 25, 50, 100 ], //可供选择的每页的行数（*）
				//search: true,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
				strictSearch : true,
				showColumns : true, //是否显示所有的列
				showRefresh : true, //是否显示刷新按钮
				minimumCountColumns : 2, //最少允许的列数
				clickToSelect : true, //是否启用点击选中行
				//height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
				uniqueId : "id", //每一行的唯一标识，一般为主键列
				showToggle : true, //是否显示详细视图和列表视图的切换按钮
				cardView : false, //是否显示详细视图
				detailView : false, //是否显示父子表
				singleSelect : true,
				/* rowStyle: function (row, index) {
				    var style = "";  
				    if(row.roles.){
				    	style='danger';             
				    }
				return { classes: style }
				} , */
				columns : [ {
					checkbox : true
				}, {
					field : 'id',
					title : 'Id'
				}, {
					field : 'labname',
					title : 'LabName'
				}, {
					field : 'ip',
					title : 'Ip'
				}, {
					field : 'release',
					title : 'Release',
				}, {
					field : 'protocol',
					title : 'Protocol',
				}, {
					field : 'startstatus',
					title : 'StartStatus',
				}, {
					field : 'starttime',
					title : 'StartTime',
				}, {
					field : 'endstatus',
					title : 'EndStatus',
				}, {
					field : 'endtime',
					title : 'EndTime',
				}, {
					field : 'hodingtime',
					title : 'Time(d/hh:mm:ss)',
				}]
			});
		};

		//得到查询的参数
		oTableInit.queryParams = function(params) {
			var temp = { //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
				limit : params.limit, //页面大小
				offset : params.offset, //页码
				serverName : '${serverName}'
			};
			return temp;
		};
		return oTableInit;
	};

	var ButtonInit = function() {
		var oInit = new Object();
		var postdata = {};

		oInit.Init = function() {
			//初始化页面上面的按钮事件
		};

		return oInit;
	};
	function statusFormatter(value, row, index) {
		if (row.stateflag == '0') {
			//圆角
			/*<span class='badge bg-orange'  style='padding:5px 10px;'> </span> */
			return '<span class="label label-success">normal</span>';
		} else {
			return '<span class="label label-default">disable</span>';
		}
	}
	function countFormatter(value, row, index) {
		return "<span class='badge' >" + value + "</span>";
	}
</script>
</html>