<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- <meta http-equiv="refresh" content="5">页面每5秒刷新一次 -->
<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>

<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<script src="${pageContext.request.contextPath}/js/bootstrap-table-treegrid.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.treegrid.min.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery.treegrid.css">

<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />

<script src="./js/bootstrap-select.min.js"></script>
<link href="./css/bootstrap-select.min.css" rel="stylesheet">

<style type="text/css">
.bootstrap-select:not([class*=col-]):not([class*=form-control]):not(.input-group-btn){
width: 100%;
}

</style>

<title>Server Info</title>
</head>
<body style="background-color:#ECEFF3;">
<div class="panel panel-primary" style="margin: 10px;">
    <div class="panel-heading">
        <h3 class="panel-title">Lab Management</h3>
    </div>
    <div class="panel-body">
    	<div id="toolbar" style="text-align:left;">
    		<!-- <a class="btn btn-success btn-sm" type="button" href="./addServerInfo.do" id="add"> -->
    		<a class="btn btn-primary btn-sm" type="button" href="#" id="add">
			   	<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>&nbsp;&nbsp;add new lab&nbsp;&nbsp;
		 	</a>
    		<a class="btn btn-success btn-sm" type="button" href="#" id="addExist">
			   	<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>&nbsp;&nbsp;add exist lab&nbsp;&nbsp;
		 	</a>
    		<a class="btn btn-info btn-sm" type="button" href="#" id="checkAddLog">
			   	<span class="glyphicon glyphicon-search" aria-hidden="true"></span>&nbsp;&nbsp;check add log&nbsp;&nbsp;
		 	</a>
		 	
    	</div>
       	<table id="table"></table>
    </div>
</div>
<!--新增server -->
<div class="modal fade bs-example-modal-lg" id="addModal" tabindex="-1" role="dialog" aria-labelledby="addModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title" id="addModalLabel">Add Lab Info</h4>
            </div>
            <div class="modal-body" id="add_body">
            	<div class="row">
       				<div class="col-md-12">
       					<form class="form-horizontal" role="form">
							  <div class="form-group">
							    <label for="aservername" class="col-sm-2 control-label" style="text-align: left;">servername</label>
							    <div class="col-sm-4">
							      <input type="text" class="form-control" id="aservername"  placeholder="servername" >
							    </div>
							    <label for="arelease" class="col-sm-2 control-label" style="text-align: left;">release</label>
							    <div class="col-sm-4">
									<input type="text" class="form-control" id="arelease"  placeholder="release" style="display: inline;">
							    </div>
							  </div>
							  <div class="form-group">
							    <label for="aprotocol" class="col-sm-2 control-label" style="text-align: left;">protocol</label>
							    <div class="col-sm-4">
									<!-- <input type="text" class="form-control" id="aprotocol"  placeholder="protocol" style="display: inline;"> -->
									<select class="form-control" name="aprotocol" id="aprotocol">
										<option>ITU</option>
										<option>ANSI</option>
									</select>
							    </div>
							    <label for="ainsflag" class="col-sm-2 control-label" style="text-align: left;" data-toggle="tooltip" data-placement="bottom" title="yes means complete reloading  /  no means only reload spa and rtdb" >reload platform<sup>[1]</sup></label>
							    <div class="col-sm-4">
							    	<div class="radio" style="display: inline;">
									    <label>
									        <input type="radio" name="optionsRadios" id="yes" value="1"><span class="label label-success">yes</span>
									    </label>
									</div>
									<div class="radio" style="display: inline;">
									    <label>
									        <input type="radio" name="optionsRadios" id="no" value="0" checked="checked"><span class="label label-default">no</span>
									    </label>
									</div>
							    </div>
							   <!--  <div class="col-sm-8"></div>
							    <div class="col-sm-4">
								    <font size="1" color="#d3d3d3">yes means complete reloading</font> <br/>
									<font size="1" color="#d3d3d3">no means only reload spa and rtdb</font>
							    </div> -->
							    <!-- <label for="aservertype" class="col-sm-2 control-label" style="text-align: left;">servertype</label>
							    <div class="col-sm-4">
									<input type="text" class="form-control" id="aservertype"  placeholder="servertype" style="display: inline;">
									<select class="form-control" name="aservertype" id="aservertype">
										<option>L</option>
										<option>G</option>
									</select>
							    </div> -->
							  </div>
							   <!--<div class="form-group">
							    <label for="amatetype" class="col-sm-2 control-label" style="text-align: left;">matetype</label>
							    <div class="col-sm-4">
									<input type="text" class="form-control" id="amatetype"  placeholder="matetype" style="display: inline;">
									<select class="form-control" name="amatetype" id="amatetype">
										<option>P</option>
										<option>S</option>
									</select>
							    </div> 
							  </div>-->
							  <div class="form-group">
							    <label for="adept" class="col-sm-2 control-label" style="text-align: left;">dept</label>
							    <div class="col-sm-4">
									<input type="text" class="form-control" id="adept"  placeholder="dept" style="display: inline;" value="${deptname }" disabled="disabled">
									<input type="text" class="form-control" id="hdept"  style="display: none;" value="${deptid }" >
							    </div>
							  </div>
							  <div class="form-group">
							    <label for="aspa" class="col-sm-2 control-label" style="text-align: left;">spa</label>
							    <div class="col-sm-8">
									<!-- <input type="text" class="form-control" id="aspa"  placeholder="spa" style="display: inline;"> -->
									<select id="sspa" name="sspa" title="--  please select spa  --" class="selectpicker" multiple data-live-search="true" ><!-- data-max-options="1" -->
										<c:if test="${SPA!=null && fn:length(SPA) > 0}">
											<c:forEach items="${SPA}" var="ospa">
												<option name='ospa' value="${ospa}">${ospa}</option>
											</c:forEach>
										</c:if>
									</select>
							    </div>
							    <!-- <div class="col-sm-2" style="padding-left: 0px;">
							    	<span id="esspa" class="glyphicon glyphicon-edit btn" aria-hidden="true" style="display: inline;"></span>
							    </div> -->
							  </div>
							  <div class="form-group">
							    <label for="adb" class="col-sm-2 control-label" style="text-align: left;">db</label>
							    <div class="col-sm-8">
									<!-- <input type="text" class="form-control" id="adb"  placeholder="db" style="display: inline;"> -->
									<select id="sdb" name="sdb" title="--  please select rtdb  --" class="selectpicker" multiple data-live-search="true" ><!-- data-max-options="1" -->
										<c:if test="${RTDB!=null && fn:length(RTDB) > 0}">
											<c:forEach items="${RTDB}" var="odb">
												<option name='odb' value="${odb}">${odb}</option>
											</c:forEach>
										</c:if>
									</select>
							    </div>
							    <!-- <div class="col-sm-2" style="padding-left: 0px;">
							    	<span id="esdb" class="glyphicon glyphicon-edit btn" aria-hidden="true" style="display: inline;"></span>
							    </div> -->
							  </div>
							  
							  
						</form>
       				</div>
				</div>
			</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">clsoe</button>
                <button type="button" class="btn btn-primary" id="addSubmit">submit</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->





</body>

<script type="text/javascript">
$("#addSubmit").click(function(){
	var sdata = "";
	var aservername = $("#aservername").val().replace(" ","");
	if(aservername==""){
		alert("servername is required");
		return false;
	}else {
		sdata="aservername="+aservername
	}
	var arelease = $("#arelease").val().replace(" ","");
	if(arelease==""){
		alert("release is required");
		return false;
	}else {
		sdata=sdata+"&arelease="+arelease
	}
	var aprotocol = $("#aprotocol").val().replace(" ","");
	/* var aservertype = $("#aservertype").val().replace(" ","");
	var amatetype = $("#amatetype").val().replace(" ",""); */
	var hdept = $("#hdept").val().replace(" ","");
	var ainsflag = $("input[name='optionsRadios']:checked").val().replace(" ","");
	//alert(ainsflag);
	//"&aservertype="+aservertype+"&amatetype="+amatetype+
	sdata=sdata+"&aprotocol="+aprotocol+"&hdept="+hdept+"&ainsflag="+ainsflag
	var sspa = $("#sspa").val();
	if(sspa==null){
		alert("spa is required");
		return false;
	}else {
		sdata=sdata+"&sspa="+sspa
	}
	var sdb = $("#sdb").val();
	if(sdb==null){
		alert("db is required");
		return false;
	}else {
		sdata=sdata+"&sdb="+sdb
	}
	alert(sdata);
	
	$.ajax({
		url:"installLab.do",
		data:sdata,
		success:function(data){
			if(data.result=="fail"){
				alert(data.msg);
				return;
			}
			if(data.result=="success"){
				alert(data.msg);
				//跳转到log页面
				window.location.reload();
			}
		}
	})
	
});
$("#add").click(function(){
	/* $.ajax({
		url:"getAllSpaAndDb.do",
		success:function(data){
			var spa="";
			if(data.SPA.length>0){
				for(var i=0;i<data.SPA.length;i++){
					spa=spa+"<option name='ospa' value='"+data.SPA[i]+"'>"+data.SPA[i]+"</option>";
				}
			}
			$("#sspa").html(spa);
		}
	}); */
	
	$("#addModal").modal("show");
});
$(function() {
	var $table = $('#table');
  	$table.bootstrapTable({
	    url: 'getServerInfoJson.do',
	    toolbar: '#toolbar',                //工具按钮用哪个容器
	    //height: $(window).height(),
	    //striped: true,
	    sidePagination: 'server',
	    //这里是标志id  和 parentIdField有关系
	    idField: 'id',
	    rowStyle: function (row, index) {
            //['active', 'success', 'info', 'warning', 'danger'];
            var strclass = "";
            if (row.type == "set") {
                strclass = 'info';
            }
            else {
                return {};
            }
            return { classes: strclass } 
            /* if (index==1){
        		return {css:{"background-color":"red"}}
        	}else{
        		return {css:{"background-color":"green"}}
        	} */

        },
	    columns: [
	      {
	        field: 'ck',
	        checkbox: true
	      },
	      {
	        field: 'name',
	        title: 'name',
        	formatter:function(value,row,index){
        		if(row.type == 'set'){
        			return value;
        		}else if(row.type == 'server'){
	            	var a = '<a href="./getServerDetails.do?serverName='+value+'" >'+value+'</a>';
	            	return a;
        		}
            }
	      },
	      /* {
		        field: 'type',
		        title: 'type',
		        sortable: true,
		        align: 'center',
		        formatter: 'typeFormatter'
		   }, */
	      {
	        field: 'status',
	        title: 'status',
	        align: 'center',
	        formatter: 'statusFormatter'
	      },{
		        field: 'serverIp',
		        title: 'serverIp',
		    }
	      ,{
		        field: 'serverRelease',
		        title: 'serverRelease',
		    }
	      ,{
		        field: 'serverProtocol',
		        title: 'serverProtocol',
		    }
	      ,{
		        field: 'serverType',
		        title: 'serverType',
		    }
	      ,{
		        field: 'serverMate',
		        title: 'serverMate',
		    }
	      ,{
		        field: 'mateServer',
		        title: 'mateServer',
		    }
	    ],
	    //最主要的就是下面  定义哪一列作为展开项  定义父级标志 这里是pid
	    //定义的列一定是要在表格中展现的  换言之就是上方有这个列 不然报错
	      
	    // bootstrap-table-treegrid.js 插件配置
	   	treeShowField: 'name',
	    parentIdField: 'pid',
	    onLoadSuccess: function(data) {
	      $table.treegrid({
	    	  //initialState: 'collapsed',//收缩
	          treeColumn: 1,//指明第几列数据改为树形
	          expanderExpandedClass: 'glyphicon glyphicon-triangle-bottom',
	          expanderCollapsedClass: 'glyphicon glyphicon-triangle-right',
	          onChange: function() {
	              $table.bootstrapTable('resetWidth');
	          }
	      });
	    }
	    // bootstrap-table-treetreegrid.js 插件配置
	  });
});
function statusFormatter(value, row, index) {
	if(row.type == 'set'){
		return '';
	}else if(row.type == 'server'){
		/* if (value == 0) {
		    return '<span class="label label-success">Idle</span>';
	  	} else if(value == 1){
		    return '<span class="label label-danger">Dead</span>';
	  	} else if(value == 2){
		    return '<span class="label label-warning">Running</span>';
	  	} else if(value == 3){
		    return '<span class="label label-info">Ready</span>';
	  	} else if(value == 4){
		    return '<span class="label label-default">Finished</span>';
	  	} */
		if (value == 'Idle') {
		    return '<span class="label label-success">Idle</span>';
	  	} else if(value == 'Dead'){
		    return '<span class="label label-danger">Dead</span>';
	  	} else if(value == 'Running'){
		    return '<span class="label label-warning">Running</span>';
	  	} else if(value == 'Ready'){
		    return '<span class="label label-info">Ready</span>';
	  	} else if(value == 'Finished'){
		    return '<span class="label label-default">Finished</span>';
	  	}else{
	  		return '<span class="label label-default">'+value+'</span>';
	  	}
	}
}
/* function typeFormatter(value, row, index) {
	//alert(row.status)
  if (value === 'set') {
    return '';
  } else {
	  if (row.status === 0) {
		    return '<span class="label label-success">Idle</span>';
	  } else {
		    return '<span class="label label-default">dead</span>';
	  }
  }
} */

</script>

</html>