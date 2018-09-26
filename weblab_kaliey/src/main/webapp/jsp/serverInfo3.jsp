<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- <meta http-equiv="refresh" content="5">页面每5秒刷新一次 -->

<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery.treegrid.css">

<title>Server Info</title>
</head>
<body style="background-color:#ECEFF3;">
<div class="panel panel-primary" style="margin: 10px;">
    <div class="panel-heading">
        <h3 class="panel-title">Lab Management</h3>
    </div>
    <div class="panel-body">
    	<div id="toolbar" style="text-align:left;">
    		<a class="btn btn-success btn-sm" type="button" href="./addServerInfo.do">
			   	<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;add lab&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		 	</a>
    	</div>
       	<table id="table"></table>
    </div>
</div>
</body>
<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-table-treegrid.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.treegrid.min.js"></script>
<script type="text/javascript">
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
		if (value == 0) {
		    return '<span class="label label-success">Idle</span>';
	  	} else if(value == 1){
		    return '<span class="label label-danger">Dead</span>';
	  	} else if(value == 2){
		    return '<span class="label label-warning">Running</span>';
	  	} else if(value == 3){
		    return '<span class="label label-info">Ready</span>';
	  	} else if(value == 4){
		    return '<span class="label label-default">Running</span>';
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