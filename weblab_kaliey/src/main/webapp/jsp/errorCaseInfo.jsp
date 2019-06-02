<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%--
<meta http-equiv="refresh" content="5*60">
 --%>
<script src="${pageContext.request.contextPath}/jquery-3.4.1/jquery-3.4.1.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.css" >
<script src="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/css/bootstrap.css">
<script src="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/js/bootstrap.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.css">
<script src="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.js"></script>


 <style  type="text/css">
.dropdown-toggle{
	padding-top: 3px;
    padding-bottom: 3px;
}
</style>
<script type="text/javascript">
</script>
<title>Features</title>
</head>
<body style="background-color:#ECEFF3;"><!-- style="background-color:#ECEFF3;" -->
    <div class="panel-body" style="padding-bottom:0px;">
        <div class="panel panel-default">
            <div class="panel-heading"><strong>Feature</strong></div>
            <div class="panel-body" style="padding-bottom: 0px;">
                <form id="formSearch" class="form-horizontal">
                    <div class="form-group" style="margin-top:15px;">
                       	<label class="control-label col-sm-2" for="txt_search_feature">Feature</label>
                        <div class="col-sm-3">
                            <input type="text" class="form-control" id="txt_search_feature" style="display: inline;">
                        </div>
                        <div class="col-sm-3"></div>
                        <div class="col-sm-4" style="text-align:right;">
                            <button type="button" style="margin-left:50px" id="btn_query" class="btn btn-primary ">Search</button>
                        </div>
                    </div>
                </form>
            </div>
        </div> 
        <div class="row" style="padding-left: 15px;padding-right: 15px;">
	        <div class="table-responsive"  style="background-color: #FFFFFF;padding-left: 20px;padding-right: 20px;" >
	        	<table id="tb_departments" class="text-nowrap" style="background-color: #FBFCFC"></table>
	        </div>
        </div>
    </div>
</body>
<script type="text/javascript">
$(function () {
    //1.初始化Table
    var oTable = new TableInit();
    oTable.Init();

    //2.初始化Button的点击事件
    var oButtonInit = new ButtonInit();
    oButtonInit.Init();

    $("#btn_query").click(function(){
    	$('#tb_departments').bootstrapTable('refresh',{
    		url: 'getErrorCaseInfoTable.do',
    		queryParams: oTable.queryParams		
    	});  
    });
    
});


var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#tb_departments').bootstrapTable({
            url: 'getErrorCaseInfoTable.do',   //请求后台的URL（*）
            method: 'get',                      //请求方式（*）
            toolbar: '#toolbar',                //工具按钮用哪个容器
            toolbarAlign:"right",
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortable: true,                     //是否启用排序
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
            //height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            //uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            rowStyle: function (row, index) {
                //这里有5个取值代表5中颜色['active', 'success', 'info', 'warning', 'danger'];
                var strclass = "";
                if (row.ucount ==0 ) {
                    strclass = 'active';//还有一个active
                }
                else if (row.ucount >0) {
                    strclass = 'info';//还有一个active
                }
                else {
                    return {};
                }
                return { classes: strclass }
            },
            columns: [{
                field: 'feature',
                title: 'Feature',
                formatter:function(value,row,index){
                	var a = '<a href="./getErrorCaseInfoDetails4.do?featureName='+value+'" ><strong>'+value+'</strong></a>';
                	return a;
                }
            }, {
                field: 'fcount',
                title: 'Error Cases',
                sortable: true,
				formatter : 'countFormatter'
            } , {
                field: 'ccount',
                title: 'Checked Cases',
                sortable: true,
                formatter : 'countFormatter'
            }, {
                field: 'ucount',
                title: 'Unchecked Cases',
                sortable: true,
                formatter : 'countFormatter'
            } , {
                field: 'owner',
                title: 'Owner'
            }, {
                field: 'servername',
                title: 'Server Name'
            }]
        });
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            limit: params.limit,   //页面大小
            offset: params.offset,  //页码
            feature: $("#txt_search_feature").val(),
            sort: params.sort,      //排序列名  
            sortOrder: params.order //排位命令（desc，asc）
        };
        return temp;
    };
    return oTableInit;
};


var ButtonInit = function () {
    var oInit = new Object();
    var postdata = {};

    oInit.Init = function () {
        //初始化页面上面的按钮事件
    };

    return oInit;
};
function countFormatter(value, row, index) {
	return "<span class='badge' >" + value + "</span>";
}

</script> 
</html>