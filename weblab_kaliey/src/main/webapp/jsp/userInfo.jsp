<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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

<script src="${pageContext.request.contextPath}/js/bootstrap-select.min.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-select.min.css" rel="stylesheet">

 <style  type="text/css">
.dropdown-toggle{
	padding-top: 3px;
    padding-bottom: 3px;
}
#edit,#ndept,#nroles,#deleteUser{
    padding-top: 3px;
    padding-bottom: 3px;
}
</style>
<script type="text/javascript">
</script>
<title>User Management</title>
</head>
<body style="background-color:#ECEFF3;"><!-- style="background-color:#ECEFF3;" -->
    <div class="panel-body" style="padding-bottom:0px;">
        <div class="panel panel-default">
            <div class="panel-heading">User Management</div>
            <div class="panel-body" style="padding-bottom: 0px;">
                <form id="formSearch" class="form-horizontal">
                    <div class="form-group" style="margin-top:15px">
                        <label class="control-label col-sm-2" for="txt_search_feature">User Name</label>
                        <div class="col-sm-3">
                            <input type="text" class="form-control" id="txt_search_username">
                        </div>
                        <div class="col-sm-3"></div>
                        <div class="col-sm-4" style="text-align:right;">
                            <button type="button" style="margin-left:50px" id="btn_query" class="btn btn-primary ">Search</button>
                        </div>
                    </div>
                </form>
            </div>
        </div> 
        <div id="toolbar" style="text-align:left;">
        	<shiro:hasPermission name="user:edit">
	        <button id="edit" class="btn btn-info">
				<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>  <strong>Edit</strong> 
			</button>
			</shiro:hasPermission>
			
	        <!-- 
	        <button id="deleteUser" class="btn btn-danger">
				<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>  delete 
			</button> 
			-->
			
			
	        <%-- <button id="ndept" class="btn btn-default" onclick="javascript:window.location.href='${pageContext.request.contextPath}/getDeptInfo.do'">
				<i class="icon-trophy"></i>  dept 
			</button> --%>
	        <%-- <button id="nroles" class="btn btn-default" onclick="javascript:window.location.href=${pageContext.request.contextPath}/getRolesInfo.do'">
				<i class="icon-github-alt"></i>  roles 
			</button> --%>
        </div>
        <div style="background-color: white;">
	        <div class="row" style="margin-left: 10px;margin-right: 13px;margin-top: 10px; ">
	        	<table id="tb_departments" class="text-nowrap" ></table>
	        </div>
        </div>
    </div>
    
    
    <!-- 模态框（Modal） -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	                <h4 class="modal-title" id="myModalLabel">Edit User</h4>
	            </div>
	            <div class="modal-body">
					<div class="row">
        				<div class="col-md-12">
        					<form class="form-horizontal" role="form">
								  <div class="form-group">
								    <label for="eid" class="col-sm-2 control-label" style="text-align: left;">id</label>
								    <div class="col-sm-6">
								      <input type="text" class="form-control" id="eid"  placeholder="id" disabled="disabled">
								    </div>
								  </div>
								  <div class="form-group">
								    <label for="eusername" class="col-sm-2 control-label" style="text-align: left;">username</label>
								    <div class="col-sm-6">
								      <input type="text" class="form-control" id="eusername"  placeholder="username" disabled="disabled">
								    </div>
								  </div>
								  <div class="form-group">
								    <label for="eroles" class="col-sm-2 control-label" style="text-align: left;">roles</label>
								    <div class="col-sm-6" id="eroles">
								      <!-- <input type="text" class="form-control" id="eroles"  placeholder="roles" disabled="disabled" style="display: inline;"> -->
								    </div>
								    <div class="col-sm-4" style="padding-left: 0px;">
								    	<span id="sroles" class="glyphicon glyphicon-edit btn" aria-hidden="true" style="display: inline;"></span>
								    </div>
								  </div>
								  <div class="form-group">
								    <label for="edept" class="col-sm-2 control-label" style="text-align: left;">group</label>
								    <div class="col-sm-6" id="sedept">
<!-- 								    <input type="text" class="form-control" id="edept"  placeholder="dept" disabled="disabled" style="display: inline;">
								      	<select id="edept" name="edept" class="selectpicker" style="display: none;"></select>
 -->								</div>
								    <div class="col-sm-4" style="padding-left: 0px;">
								    	<span id="sdept" class="glyphicon glyphicon-edit btn" aria-hidden="true" style="display: inline;"></span>
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
	                <button type="button" class="btn btn-default" data-dismiss="modal">clsoe</button>
	                <button type="button" class="btn btn-primary" id="editSubmit">submit</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
	
    <!-- 角色模态框（Modal）bs-example-modal-lg modal-lg -->
	<div class="modal fade " id="roleModal" tabindex="-1" role="dialog" aria-labelledby="myRoleModalLabel" aria-hidden="true">
	    <div class="modal-dialog ">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	                <h4 class="modal-title" id="myRoleModalLabel">Select Roles</h4>
	            </div>
	            <div class="modal-body" id="role_body">
				</div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal">clsoe</button>
	                <button type="button" class="btn btn-primary" id="sRoleSubmit">submit</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
	
</body>
<script type="text/javascript">
$("#editSubmit").click(function(){
	var ehroles = $("#ehroles").val();
	var edept = $("#edept").val();
	/* alert(edept);
	return;  */
	var estateflag = $("input[name='optionsRadios']:checked").val();
	/* alert(ehroles);
	alert(edept);
	alert(estateflag); */
	var a  = $('#tb_departments').bootstrapTable('getSelections');
	var aid = a[0].id;
	//var aroles = a[0].roles;
	var aroles= new Array(); //定义一数组 
	aroles=a[0].roles.split(","); //字符分割
	aroles.sort();
	
	var adept = a[0].deptids;
	var amstateflag = a[0].stateflag;
	var astateflag="normal";
	if(amstateflag!=0){
		astateflag="disable";
	}
	/* alert(aroles);
	alert(adept);
	alert(astateflag); */
	
	var sdata ="id="+aid+"&";
	var updateflag="1";
	
	if(ehroles!=aroles){
		sdata=sdata+"erole="+ehroles+"&";
		updateflag="0";
	}else {
		sdata=sdata+"erole=&";
	}
	//alert(adept+"=="+edept);
	if(edept==""){
		alert("At least checked one group");
		return;
	}else if(edept!=adept){
		sdata=sdata+"edept="+edept+"&";
		updateflag="0";
	}else {
		sdata=sdata+"edept=&";
	}
	if(estateflag!=astateflag){
		sdata=sdata+"estateflag="+estateflag+"&";
		updateflag="0";
	}else {
		sdata=sdata+"estateflag=&";
	}
	
	if(updateflag=="1"){
		alert("Nothing has changed");
		return;
	}
	
	$.ajax({
		url:"editUserInfo.do",
		data:sdata,
		success:function(data){
			//alert(data);
			if(data=="fail"){
				alert("Sorry, data update failed.");
			}else if(data=="success"){
				alert("Congratulations, data updated successfully.");
				window.location.reload();
			}
		}
	});
	
});


//清除弹窗原数据
$("#myModal").on("hidden.bs.modal", function() {
	var dept= "<input type='text' class='form-control' id='edept'  placeholder='dept' disabled='disabled' style='display: inline;'>";
	$("#sedept").html(dept);
    $(this).removeData("bs.modal");
});
$("#roleModal").on("hidden.bs.modal", function() {
    $(this).removeData("bs.modal");
});


/* function showRolePermission(data){
	//alert(data);
	var htm = "";
	$.ajax({
		url:"getRolePermission.do",
		data:"rolename="+data,
		success:function(data){
			//alert(data);
			
		}
	});
	
	$("#role_permission_body").html();
	$("#rolePermissionModal").modal("show");
}
function hideRolePermission(){
	$("#rolePermissionModal").modal("hide");
} */
$("#sdept").click(function(){
	var a  = $('#tb_departments').bootstrapTable('getSelections');
	//alert(a[0].dept_name);
	$.ajax({
		url:"getAllDept.do",
		success: function(data){
			var scheck="";
			var dept="<select class='selectpicker' id='edept' multiple data-live-search='true'>"
			//var dept="";
			for(i=0;i<data.length;i++){
				//alert(data[i].dept_name)
				//if(data[i].dept_name==a[0].dept_name){
				if(a[0].depts.indexOf(data[i].dept_name)>=0){
					scheck="selected";
				}else {
					scheck="";
				}
				dept=dept+"<option value='"+data[i].id+"' "+scheck+">"+data[i].dept_name+"</option>";
			}
			dept=dept+"</select>";
			$("#sedept").html(dept);
			//$("#sedept").html("");
			//$("#edept.selectpicker").append(dept);
			$('#edept').selectpicker('refresh');
			$('#edept').removeAttr("style");
		}
	});	
})

function tagStyle(strs){
	var tag= "";
	for (i=0;i<strs.length ;i++ ){ 
		if(strs[i]=="admin"){
			tag=tag+"<span class='label label-danger'>"+strs[i]+"</span> ";
		}else if (strs[i]=="super") {
			tag=tag+"<span class='label label-primary'>"+strs[i]+"</span> ";
		}else if (strs[i]=="user") {
			tag=tag+"<span class='label label-info'>"+strs[i]+"</span> ";
		}else {
			tag=tag+"<span class='label label-default'>"+strs[i]+"</span> ";
		}
	}
	return tag;
}
$("#sRoleSubmit").click(function(){
	var tag = "";
	//var strs="";
	var list = new Array();
	$.each($('input:checkbox[name="rolesCheck"]:checked'),function(){
        //window.alert("你选了："+ $(this).val());
        var crole= $(this).val();
        if(crole=="admin"){
			tag=tag+"<span class='label label-danger'>"+crole+"</span> ";
		}else if (crole=="super") {
			tag=tag+"<span class='label label-primary'>"+crole+"</span> ";
		}else if (crole=="user") {
			tag=tag+"<span class='label label-info'>"+crole+"</span> ";
		}else {
			tag=tag+"<span class='label label-default'>"+crole+"</span> ";
		}
        //strs=strs+crole+",";
        list.push(crole);
    });
	if(tag==""){
		alert("At least checked one role");
		return;
	}
	/* if(strs.indexOf(",")>=0){
		strs.substring(0,strs.length-1);
	} */
	/* var sear=new RegExp(',');
	if(sear.test(strs)) {
		//alert('Yes');
		strs = strs.substring(0,strs.length-1);
	} */
	//alert(list);
	tag=tag+"<input id='ehroles' value='"+list+"' type='hidden' />";
	//tag=tag+"<input id='ehroles' value='"+strs+"' type='hidden' />";
	$("#eroles").html(tag);
	$("#roleModal").modal("hide");
})

$("#sroles").click(function(){
	var a  = $('#tb_departments').bootstrapTable('getSelections');
	var strs= new Array(); //定义一数组 
	strs=a[0].roles.split(","); //字符分割
	
	$.ajax({ 
		url: "getAllRoles.do",
		success: function(data){
	        //alert(data[0].role_name);
	        var str ='';
			for (i=0;i<data.length ;i++ ){
				var slabel ="";
				var sdisabled = "";
				if(data[i].role_name=="admin"){
					sdisabled = "disabled";
					slabel="danger";
				}else if (data[i].role_name=="super") {
					slabel="primary";
				}else if (data[i].role_name=="user") {
					slabel="info";
				}else {
					slabel="default";
				}
				var scheck='';
				if(strs.indexOf(data[i].role_name) >-1){
					scheck='checked="checked"';
				}else {
					scheck='';
				}
				str = str+'<div class="checkbox" style="display: inline;">'+ //onmouseover ="showRolePermission(&apos;'+data[i].role_name+'&apos;)" onmouseout ="hideRolePermission()"
				    '<label>'+
				        '<input type="checkbox" name="rolesCheck" value="'+data[i].role_name+'" '+scheck+' '+sdisabled+'><span class="label label-'+slabel+'">'+data[i].role_name+'</span>'+
				    '</label>'+
				'</div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
			} 
			$("#role_body").html(str);
	    }
	});
	$("#roleModal").modal("show");
});

$("#deleteUser").click(function(){
	var a  = $('#tb_departments').bootstrapTable('getSelections');
	if(a.length==0){
		alert("At least checked one line");
		return false;
	}else{
		//a[0].id
		$.ajax({ 
		url: "deleteUser.do",
		data:"id="+a[0].id,
		success: function(data){
				alert(data);
				window.location.reload();
			}
		})
	}
});
$("#edit").click(function(){
	var a  = $('#tb_departments').bootstrapTable('getSelections');
	if(a.length==0){
		alert("At least checked one line");
		return false;
	}else{
		if(a[0].id=="1"){
			alert("The root is not allowed to be modified.");
			return false;
		}
		//alert(a[0].dept_name);
		$("#eid").val(a[0].id);
		$("#eusername").val(a[0].username);
		var strs= new Array(); //定义一数组 
		strs=a[0].roles.split(","); //字符分割
		strs.sort();
		/* var tag= "";
		for (i=0;i<strs.length ;i++ ){ 
			if(strs[i]=="admin"){
				tag=tag+"<span class='label label-danger'>"+strs[i]+"</span> ";
			}else if (strs[i]=="super") {
				tag=tag+"<span class='label label-primary'>"+strs[i]+"</span> ";
			}else if (strs[i]=="user") {
				tag=tag+"<span class='label label-info'>"+strs[i]+"</span> ";
			}else {
				tag=tag+"<span class='label label-default'>"+strs[i]+"</span> ";
			}
		}  */
		var tag = tagStyle(strs);
		tag=tag+"<input id='ehroles' value='"+strs+"' type='hidden' />";
		$("#eroles").html(tag);
		var edept = "<input type='text' id='edept' value='"+a[0].deptids+"' style='display: none;'>"+
		"<input type='text' class='form-control' value='"+a[0].depts+"' placeholder='dept' disabled='disabled' style='display: inline;'>";
		$("#sedept").html(edept);
		$("#estateflag").val(a[0].dept_name);
		if(a[0].stateflag==0){
			$("#normal").prop("checked",true);
		}else{
			$("#disable").prop("checked",true);
		}
		$("#myModal").modal("show");
	}
})


$(function () {
    //1.初始化Table
    var oTable = new TableInit();
    oTable.Init();

    //2.初始化Button的点击事件
    var oButtonInit = new ButtonInit();
    oButtonInit.Init();

    $("#btn_query").click(function(){
    	$('#tb_departments').bootstrapTable('refresh',{
    		url: 'getUserInfoJson.do',
    		queryParams: oTable.queryParams		
    	});  
    });
    
});


var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#tb_departments').bootstrapTable({
            url: 'getUserInfoJson.do',   //请求后台的URL（*）
            method: 'get',                      //请求方式（*）
            toolbar: '#toolbar',                //工具按钮用哪个容器
            toolbarAlign:"left",
            striped: false,                      //是否显示行间隔色
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
            uniqueId: "id",                     //每一行的唯一标识，一般为主键列
            showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            singleSelect : true,
            /* rowStyle: function (row, index) {
                var style = "";  
                if(row.roles.){
                	style='danger';             
                }
            return { classes: style }
        	} , */
            columns: [{
                checkbox: true
            }, {
                 field: 'id',
                 title: 'Id'
            }, {
                field: 'username',
                title: 'User Name'
            }, {
                field: 'roles',
                title: 'Roles',
                formatter: 'tagsFormatter'
            },/* {
                field: 'deptids',
                title: 'deptids',
            },  {
                field: 'deptid',
                title: 'Group Id',
                sortable: true
            }, */ {
                field: 'depts',
                title: 'Group Name',
                //formatter: 'iconFormatter',
                sortable: true
            }, {
                field: 'stateflag',
                title: 'State Flag',
                formatter: 'statusFormatter'
            }]
        });
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            limit: params.limit,   //页面大小
            offset: params.offset,  //页码
            username: $("#txt_search_username").val(),
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
function iconFormatter(value, row, index) {
	if(row.dept_name == 'headquarters'){
		return ' <i class="icon-trophy"></i> '+value;
	}else if(row.dept_name == 'certify'){
		return ' <i class="icon-user-md"></i> '+value;
	}else {
		return ' <i class="icon-user"></i> '+value;
	}
}
function statusFormatter(value, row, index) {
	if(row.stateflag == '0'){
		//圆角
		/*<span class='badge bg-orange'  style='padding:5px 10px;'> </span> */
		return '<span class="label label-success">normal</span>';
	}else{
		return '<span class="label label-default">disable</span>';
	}
}
function tagsFormatter(value, row, index) {
	var strs= new Array(); //定义一数组 
	strs=value.split(","); //字符分割
	strs.sort();
	/* var tag= "";
	for (i=0;i<strs.length ;i++ ){ 
		if(strs[i]=="admin"){
			tag=tag+"<span class='label label-danger'>"+strs[i]+"</span> ";
		}else if (strs[i]=="super") {
			tag=tag+"<span class='label label-primary'>"+strs[i]+"</span> ";
		}else if (strs[i]=="user") {
			tag=tag+"<span class='label label-info'>"+strs[i]+"</span> ";
		}else {
			tag=tag+"<span class='label label-default'>"+strs[i]+"</span> ";
		}
	} */
	var tag = tagStyle(strs);
	return tag;
}
</script> 
</html>