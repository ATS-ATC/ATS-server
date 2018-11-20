<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
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

<script src="${pageContext.request.contextPath}/js/bootstrap-table-export.min.js"></script>
<script src="${pageContext.request.contextPath}/js/tableExport.min.js"></script>

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
<body ><!-- style="background-color:#ECEFF3;" -->
    <div class="panel-body" style="padding-bottom:0px;">
        <div class="panel panel-default">
            <div class="panel-heading">Feature</div>
            <div class="panel-body" style="padding-bottom: 0px;">
                <form id="formSearch" class="form-horizontal">
                    <div class="form-group" style="margin-top:15px">
                        <label class="control-label col-sm-2" for="txt_search_feature">Feature</label>
                        <div class="col-sm-3">
                            <input type="text" class="form-control" id="txt_search_feature">
                        </div>
                        <div class="col-sm-3"></div>
                        <div class="col-sm-4" style="text-align:right;">
                            <button type="button" style="margin-left:50px" id="btn_query" class="btn btn-primary ">Search</button>
                        </div>
                    </div>
                </form>
            </div>
        </div> 
        
        
        <div id="toolbar" style="text-align:right;">
        	<!-- <div id="retime" class="time-item" style="display: none;">
			    <strong id="second_show"></strong>
			</div> -->
        	<form id="export" action="getExportCaseInfoTable.do">
        		<input value="${qtype }" name="qtype" type="hidden" />
        		<input id="etype" name="etype" type="hidden" />
        		<input id="ftype" name="ftype" type="hidden" />
        		<div id="dropdown" class="dropdown" >
				  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
				   	<span class="glyphicon glyphicon-export" aria-hidden="true"></span>&nbsp;&nbsp;Export
				   	<strong id="second_show" style="display: none;"></strong>
				    <span class="caret"></span>
				  </button>
				  <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
				    <li><a href="#" onclick="exportcase('current','csv')"><code>csv</code> Export Current Column </a></li>
				    <li><a href="#" onclick="exportcase('all','csv')"><code>csv</code> Export All Column </a></li>
				    <li class="divider"></li>
				    <li><a href="#" onclick="exportcase('current','json')"><code>json</code> Export Current Column </a></li>
				    <li><a href="#" onclick="exportcase('all','json')"><code>json</code> Export All Column</a></li>
				  </ul>
				</div>
		        <!-- <button id="export" class="btn btn-default" type="submit">
					<span class="glyphicon glyphicon-export" aria-hidden="true"></span>
				</button> -->
        	</form>
        </div>
        <table id="tb_departments" class="text-nowrap"></table>
    </div>
</body>
</html>
</body>
<script type="text/javascript">

	function exportcase(etype,ftype){
		var stop;
		//alert(type)
		//$("#dropdown").css("display","none");
		$("#dropdownMenu1").attr("disabled","disabled");
		$("#etype").attr("value",etype);
		$("#ftype").attr("value",ftype);
		$("#export").submit();
		var second = 10;
		stop = setInterval(function(){
			$("#second_show").css("display","inline");
			if(second <= 9) second = '0'+second;
			$('#second_show').html(second+'s');
			second--;
		}, 1000); 
		//防止重复点击，设置重新显示时间10s
		setTimeout(function(){
			//$("#dropdown").css("display","inline");
			$("#dropdownMenu1").removeAttr("disabled");
			window.clearInterval(stop)
			$("#second_show").css("display","none");
		},10000);
	}
	
</script>
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
    		url: 'getQueryCaseInfoTable.do?qtype=${qtype}',
    		queryParams: oTable.queryParams		
    	});  
    });
    
    /* $("#export").click(function(){
    	//alert(1)
    	$("#export").css("display","none");
    	//$("#export").css("display","inline")
    	$.ajax({
    		url:"getExportCaseInfoTable.do",
    		data:{
    			qtype:"${qtype}"
    		},
    		success:function(data,textStatus){
    			alert(data)
    		}
    	});
    	
    	
    }); */
    
});


var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#tb_departments').bootstrapTable({
            url: 'getQueryCaseInfoTable.do?qtype=${qtype}',   //请求后台的URL（*）
            method: 'get',                      //请求方式（*）
            toolbar: '#toolbar',                //工具按钮用哪个容器
            toolbarAlign:"right",
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
            //height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            rowStyle: function (row, index) {
                //这里有5个取值代表5中颜色['active', 'success', 'info', 'warning', 'danger'];
                var strclass = "";
                if (row.Feature != "1") {
                    strclass = 'info';//还有一个active
                }
                else {
                    return {};
                }
                return { classes: strclass }
            },
            columns: [{
                field: 'feature_number',
                title: 'Feature'
                /* formatter:function(value,row,index){
                	var a = '<a href="./getQueryCaseInfoDetails.do?featureName='+value+'" >'+value+'</a>';
                	return a;
                } */
            }, {
                field: 'case_name',
                title: 'CasesName'
            }, {
                field: 'author',
                title: 'Author'
            }, {
                field: 'release',
                title: 'Release'
            }, {
                field: 'code_changed_spa',
                title: 'SPA'
            }, {
                field: 'functionality',
                title: 'Functionality'
            } , {
                field: 'base_data',
                title: 'BaseData'
            }  , {
                field: 'case_status',
                title: 'CaseStatus',
                formatter:function(value,row,index){
                	switch (value) {
					case 'S':
						a="Successful";
						break;
					case 'P':
						a="Pending";
						break;
					case 'O':
						a="Obsolete";
						break;
					case 'PP':
						a="Pre-Pending";
						break;
					case 'R':
						a="Resubmit";
						break;
					case 'F':
						a="Failed";
						break;
					case 'I':
						a="Initial";
						break;
					}
                	return a;
                }
            }   , {
                field: 'call_type',
                title: 'CallType'
            }   , {
                field: 'customer',
                title: 'Customer'
            }, {
                field: 'porting_release',
                title: 'PortingRelease'
            }, {
                field: 'jira_id',
                title: 'JiraId',
                visible: false //默认不显示
            }, {
                field: 'hodingduration',
                title: 'HodingDuration',
            }/* , {
                field: 'submit_date',
                title: 'submit_date',
            } */
            
            ]
        });
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            limit: params.limit,   //页面大小
            offset: params.offset,  //页码
            feature: $("#txt_search_feature").val()
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


</script> 
</html>