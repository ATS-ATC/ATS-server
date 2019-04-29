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
			<div class="row" style="margin-left: 10px; margin-right: 13px; margin-top: 10px;">
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
				url : 'addlablogJson.do',  
				method : 'get', 
				toolbar : '#toolbar', 
				toolbarAlign : "left",
				striped : false, 
				cache : false,  
				pagination : true,  
				sortable : false, 
				sortOrder : "asc",  
				queryParams : oTableInit.queryParams,
				sidePagination : "server",  
				pageNumber : 1,  
				pageSize : 10,  
				pageList : [ 10, 25, 50, 100 ],  
				strictSearch : true,
				showColumns : true,  
				showRefresh : true,  
				minimumCountColumns : 2,  
				clickToSelect : true,  
				uniqueId : "id",  
				showToggle : true,  
				cardView : false,  
				detailView : false,  
				singleSelect : true,
				 columns : [ 
				{
					field : 'id',
					title : 'ID'
				},  {
					field : 'status',
					title : 'Status',
					formatter : 'resultFormatter'
				}, {
					field : 'type',
					title : 'Type',
					formatter : 'typeFormatter'
				}, {
					field : 'labname',
					title : 'LabName'
				}, {
					field : 'enwtpps',
					title : 'Release'
				}, {
					field : 'ss7',
					title : 'Protocol'
				}, {
					field : 'createtime',
					title : 'CreateTime'
				}, {
					field : 'modifytime',
					title : 'ModifyTime'
				}, {
					field : 'stateflag',
					title : 'Stateflag',
					formatter : 'statusFormatter'
				} ]
			});
		};

		oTableInit.queryParams = function(params) {
			var temp = {  
				limit : params.limit,  
				offset : params.offset,  
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
		};

		return oInit;
	};
	function statusFormatter(value, row, index) {
		if (row.stateflag == '0') {
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
		}else{
			return value;
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