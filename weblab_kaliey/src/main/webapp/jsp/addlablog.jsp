<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
<title>add lab log</title>
</head>
<body style="background-color: #ECEFF3;">
	<!-- style="background-color:#ECEFF3;" -->
	<div class="panel-body" style="padding-bottom: 0px;">
		<div class="panel panel-default">
			<div class="panel-heading">lab log</div>
			<div class="panel-body" style="padding-bottom: 0px;">
				<form id="formSearch" class="form-horizontal">
					<div class="form-group" style="margin-top: 15px">
						<label class="control-label col-sm-2" for="txt_search_feature">labname</label>
						<div class="col-sm-3">
							<input type="text" class="form-control" id="txt_search_lab">
						</div>
						<div class="col-sm-3"></div>
						<div class="col-sm-4" style="text-align: right;">
							<button type="button" style="margin-left: 50px" id="btn_query"
								class="btn btn-primary ">Search</button>
						</div>
					</div>
				</form>
			</div>
		</div>
		<!-- <div id="toolbar" style="text-align: left;">
			<button id="add" class="btn btn-success">
				<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
				add
			</button>
			<button id="edit" class="btn btn-info">
				<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
				edit
			</button>
			
			<button id="edelete" class="btn btn-danger" onclick="edelete()">
				<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
				delete
			</button> 
			
		</div> -->
		<div style="background-color: white;">
			<div class="row"
				style="margin-left: 10px; margin-right: 13px; margin-top: 10px;">
				<table id="tb_departments" class="text-nowrap"></table>
			</div>
		</div>
	</div>


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
				url : 'addlablogJson.do',
				queryParams : oTable.queryParams
			});
		});

	});

	var TableInit = function() {
		var oTableInit = new Object();
		//初始化Table
		oTableInit.Init = function() {
			$('#tb_departments').bootstrapTable({
				url : 'addlablogJson.do', //请求后台的URL（*）
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
					title : 'id'
				}, {
					field : 'status',
					title : 'status',
					formatter : 'resultFormatter'
				}, {
					field : 'type',
					title : 'type',
					formatter : 'typeFormatter'
				}, {
					field : 'labname',
					title : 'labname'
				}, {
					field : 'enwtpps',
					title : 'enwtpps'
				}, {
					field : 'ss7',
					title : 'ss7'
				}, {
					field : 'createtime',
					title : 'createtime'
				}, {
					field : 'modifytime',
					title : 'modifytime'
				}, {
					field : 'stateflag',
					title : 'stateflag',
					formatter : 'statusFormatter'
				} ]
			});
		};

		//得到查询的参数
		oTableInit.queryParams = function(params) {
			var temp = { //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
				limit : params.limit, //页面大小
				offset : params.offset, //页码
				labname : $("#txt_search_lab").val()
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
	function resultFormatter(value, row, index) {
		if (row.status == 'Installing') {
			return '<span class="label label-info">'+value+'</span>';
		} else if(row.status == 'Succeed'){
			return '<span class="label label-success">'+value+'</span>';
		} else if(row.status == 'Failed'){
			return '<span class="label label-danger">'+value+'</span>';
		}
	}
	function typeFormatter(value, row, index) {
		if (row.type == 'new') {
			return '<span class="label label-primary">'+value+'</span>';
		} else {
			return '<span class="label label-success">'+value+'</span>';
		}
	}
	function countFormatter(value, row, index) {
		return "<span class='badge' >" + value + "</span>";
	}
</script>
</html>