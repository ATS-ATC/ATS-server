<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<script src="./js/bootstrap-select.min.js"></script>
<link href="./css/bootstrap-select.min.css" rel="stylesheet">
<link href="./jquery-ui/jquery-ui.min.css" rel="stylesheet">
<script src="./jquery-ui/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"></script>
<script src="${pageContext.request.contextPath}/js/loading.js"></script>
<script src="${pageContext.request.contextPath}/js/xlsx.full.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jszip.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-table.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/css/loading.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/js/fileinput.min.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/css/fileinput.min.css" rel="stylesheet">
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
/* function myrefresh() {
	//alert(1)
	//$('#table').bootstrapTable('destroy'); 
	$('#runLog').bootstrapTable('refresh',{
		url: 'searchCaseRunLog.do'
	});
	//alert(2)
} */
$(function() {
	queryParams = function (params) {
	    var temp = {    
	        limit: params.limit,    
	        offset: params.offset   
	    };
	    return temp;
	};
	$('#runLog').bootstrapTable({
        url: 'searchCaseRunLog.do',   //请求后台的URL（*）
        method: 'get',                      //请求方式（*）
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: false,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: queryParams,			//传递参数（*）
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber:1,                       //初始化加载第一页，默认第一页
        pageSize: 10,                       //每页的记录行数（*）
        pageList: [10,25,50,100],        	//可供选择的每页的行数（*）
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
       /*  rowStyle: function (row, index) {
            //这里有5个取值代表5中颜色['active', 'success', 'info', 'warning', 'danger'];
            var strclass = "";
            if (row.batch_status == 'Complete') {
	                strclass = 'info';//还有一个active
            } else if (row.batch_status == "Running") {
            	strclass = '';//还有一个active
            } else if (row.batch_status == "Error") {
            	strclass = 'danger';//还有一个active
            }
            return { classes: strclass }
        }, */
        columns: [ {
            field: 'int_id',
            title: 'ID'
        }, {
            field: 'title',
            title: 'Title',
            formatter:function(value,row,index){
            	var a = '<a href="./searchCaseRunLogInfo.do?int_id='+row.int_id+'" >'+value+'</a>';
            	return a;
            }
        }
         , {
        	 field: 'running_count',
             title: 'Batch Status',
             formatter: function statusFormatter(value, row, index) {
         		if (value == 0) {
        		    return '<span class="label label-success">Complete</span>';
        	  	} else if(value > 0){
        		    return '<span class="label label-warning">Running</span>';
        	  	}
        	}
        } 
        , {
            field: 'server_info',
            title: 'Server Info'
        }, {
            field: 'author',
            title: 'Author'
        }, {
            field: 'create_time',
            title: 'Date Time'
        }/* ,{
        	field: 'deptid',
            title: 'Group Id'
        } */
        ]
    });
	//var u = Math.random(1000)
	//setInterval('myrefresh()',1000*10*6); //指定20秒刷新一次
	
	var conform = false;
	
	function getCondition (server){
		var condition = "";
		var ds = $('option[name="ds"]:checked');
		var r = $('option[name="r"]:checked');
		var wr = $('option[name="wr"]:checked');
		var c = $('option[name="c"]:checked');
		var bd = $('option[name="bd"]:checked');
		var m = $('option[name="m"]:checked');
		var ln = $('option[name="ln"]:checked');
		var sd = $('option[name="sd"]:checked');
		var pr = $('option[name="pr"]:checked');
		var cs = $('option[name="cs"]:checked'); 
		var proq = $('option[name="proq"]:checked'); 
		var fi = document.getElementById("featureid").value;
		var author = document.getElementById("author").value;
		//var server = document.getElementById("server").options[document.getElementById("server").selectedIndex].value;
		/* var server =$('#server').val(); 
		if(server.length==0){
			$("#reminder").dialog({
				open : function(event, ui) {
					$(this).html("");
					$(this).append("<p>Please select a server to run case !</p>");
				}
			});
			return;
		} */
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
		if(proq.length == 0){
			condition += ";";
		}else{	
			for (var i = 0; i < proq.length; i++) {
				condition += proq.get(i).value;
				if(i == proq.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}	
		if(wr.length == 0){
			condition += ";";
		}else{	
			for (var i = 0; i < wr.length; i++) {
				condition += wr.get(i).value;
				if(i == wr.length-1){
					condition += ";";
				}else{
					condition += ",";
				}
			}
		}	
		condition += server;
		condition += ";";
		return condition;
	}
	
	  
	$("#btn_query").click(function(){
		$('#tb_departments').bootstrapTable('destroy');
		var condition = "";
		condition = getCondition("placeholder");
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
	            //strictSearch: true,
	            //showColumns: true,                  //是否显示所有的列
	            //showRefresh: true,                  //是否显示刷新按钮
	            minimumCountColumns: 2,             //最少允许的列数
	            clickToSelect: true,                //是否启用点击选中行
	            //height: 500,                       //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
	            uniqueId: "case_name",               //每一行的唯一标识，一般为主键列
	            //showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
	            cardView: false,                    //是否显示详细视图
	            detailView: false,                   //是否显示父子表
	            columns: [{
	                checkbox: true
	            },  {
	                field: 'case_name',
	                title: 'case_name'
	            }, {
	                field: 'base_release',
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
		$('#tb_departments').bootstrapTable('destroy'); 
		var outCondition = getCondition("placeholder");
		if(outCondition==null){
			return false;
		}
		var oTable = new TableInit(outCondition);
		oTable.Init();
		$("#inCaseType").val("select");
		$("#selectCase").removeAttr("style");
		$("#Rerunning").removeAttr("style");
		$("#importCase").attr("style","display:none");
	    /* var ser = $('#server').val();
	    $("#ser").html(""+ser); */
	    $('#myModal').modal('show');
		//confirm(condition, "The lib will run case according to this condition, please submit it carefully !");
	});
	//$("#myModal").on("hide.bs.modal",function(e){window.location.reload()});  
	
	
	
	var ids="";
	$("#Rerunning").click(function(){
		var server =$('#server').val();
		var formtitle =$('#formtitle').val();
		if(""==formtitle){
			alert("Please input title !");
			return false;
		}
		if(server==null){
			alert("Please select a server to run case !");
			return false;
		}else if(server.length==0){
			alert("Please select a server to run case !");
			return false;
		}
		var a  = $('#tb_departments').bootstrapTable('getSelections');
		var flag = $("#select_all").is(":checked");
		if(a.length==0 && !flag){
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
			if(flag){
				$("#cases_count").html("<font style='color:red;'><stong> Warning : All Case Selected !!! </stong></font>");
			}else {
				$("#cases_count").html("<font style='color:red;'><stong>"+a.length+" Case Selected</stong></font>");
			}
			$('#delcfmModel').modal("show");
		}
		
    });
	
	$("#ids_confirm").click(function(){
		
		$('#delcfmModel').modal('hide');
		var server = $('#server').val(); 
		var flag = $("#select_all").is(":checked");
		var formtitle =$('#formtitle').val();
		if(""==formtitle){
			alert("Please input title !");
			return false;
		}
		if(!flag){
			alert(ids)
		}
		if(server.length==0){
			alert("Please select a server to run case !");
			return false;
		}
		var condition = getCondition(server);
		//alert(ids);
		$.get("rerunning.do", {
			ids : ids,
			condition: condition,
			flag:flag,
			formtitle:formtitle
		}, function(data) {
			if(data.msg != null){
				alert(data.msg);
			}
			//alert("success case count:"+data.s_case+" fail case count:"+data.f_case)
			if(data.s_case != null){
				alert("submit case count:  "+data.s_case+" \nnonsupport case count:  "+data.f_case)
			}
			$('#loaddingModel').modal('hide');
			$('#myModal').modal('hide');
		});
		ids="";
		$('#loaddingModel').modal('show');
	});
	
	
	$("#select_all").click(function(event){
		if($("#select_all").is(":checked")){
			$("#tb_departments").find('input').prop('checked',$(this).prop('checked'));
			/*阻止向上冒泡，以防再次触发点击操作*/
			event.stopPropagation();

		}else{
			$("#tb_departments").find('input').prop('checked',$("#tb_departments").find('input:checked').length == $("#tb_departments").length ? true : false);
			/*阻止向上冒泡，以防再次触发点击操作*/
			event.stopPropagation();
		}
	})
	
	$("#import").click(function(){
		$("#selectCase").attr("style","display:none");
		$("#Rerunning").attr("style","display:none");
		$("#importCase").removeAttr("style");
		$("#inCaseType").val("import");
	 	$('#myModal').modal('show');
	})
	function readFile(file) {
	    var name = file.name;
	    var reader = new FileReader();
	    reader.onload = function (e) {
	        var data = e.target.result;
	        var wb = XLSX.read(data, { type: "binary" });
	        // console.log(wb.Strings.length);
	        // alert(wb.Strings.length);
	        if(wb.Strings.length>0) {
		        $("#inputbox").attr("style","display:none");
		        $("#inputDiv").removeAttr("hidden");
		        var inhtml = "<tbody>";
		        for(var i=0;i<wb.Strings.length;i++){
		        	inhtml =inhtml +"<tr><td>"+wb.Strings[i].t+"</td></tr>";
		        }
		        inhtml = inhtml + "</tbody>"
		        $("#inpuntTable").html(inhtml)
		        $("#importCount").text(wb.Strings.length)
	        }
	        // alert(1)
	    };
	    reader.readAsBinaryString(file);
	}
	function addLis() {
	    var xlf = document.getElementById('kv-explorer');
	    var drop = document.getElementById('drop');
	    drop.addEventListener("dragenter", handleDragover, false);
	    drop.addEventListener("dragover", handleDragover, false);
	    drop.addEventListener("drop", onDropDown, false); 
	    if(xlf.addEventListener) {
	    	xlf.addEventListener('change', handleFile, false);
    	}
	}
	
	//$("#kv-explorer").fileinput();
	
	addLis();
	
	function handleDragover(e) {
	    e.stopPropagation();
	    e.preventDefault();
	    e.dataTransfer.dropEffect = 'copy';
	}
	
	function onDropDown(e) {
	    e.stopPropagation();
	    e.preventDefault();
	    var files = e.dataTransfer.files;
	    var f = files[0];
	    readFile(f);
	} 
	
	function handleFile(e) {
	    var files = e.target.files;
	    var f = files[0];
	    readFile(f);
	}
	
	$(".fixed-table-container").css("padding-bottom","41px");
	
	
	$("#RunOnly").click(function(){
		var inCaseType = $("#inCaseType").val();
		var server = $('#server').val(); 
		if(server.length==0){
			alert("Please select a server to run case !");
			return false;
		}
		var formtitle =$('#formtitle').val();
		if(""==formtitle){
			alert("Please input title !");
			return false;
		}
		var hotslide =$('#hotslide').val();
		if("import" == inCaseType){
			//alert("import")
			//alert($("#inpuntTable").get())
			var set = [];
			$('#inpuntTable').each(function(index) {
			    // console.log(index)
			    var table = [];
			    $(this).find('tr').each(function() {
			        var row = [];
			        $(this).find('th,td').each(function() {
			            row.push($(this).text().trim());
			        });
			        table.push(row);
			    });
			 
			    set.push(table);
			})
			//alert(set)
			if(set.length==0){
				alert("Please import case to run !");
				return false;
			}
			$.get("onlyrun.do", {
				set : set.join(","),
				server: server.join(","),
				flag:"",
				condition:"",
				formtitle:formtitle,
				hotslide:hotslide
			}, function(data) {
				if(data.msg != null){
					alert(data.msg);
				}
				//alert("success case count:"+data.s_case+" fail case count:"+data.f_case)
				if(data.s_case != null){
					alert("submit case count:  "+data.s_case+" \nnonsupport case count:  "+data.f_case)
				}
				$('#loaddingModel').modal('hide');
				$('#myModal').modal('hide');
			});
			set = [];
			$('#loaddingModel').modal('show');
			
		} else if ("select" == inCaseType) {
			//alert("select")
			var sets = "";
			var a  = $('#tb_departments').bootstrapTable('getSelections');
			var flag = $("#select_all").is(":checked");
			if(a.length==0 && !flag){
				alert("At least checked one line");
				return false;
			}else{
				for(var i=0;i<a.length;i++){
					if(i==0 || i=="0"){
						sets+=a[i].case_name;
					}else{
						sets+=","+a[i].case_name;
					}
				}
			}
			var condition = getCondition(server);
			//alert(ids);
			$.get("onlyrun.do", {
				set : sets,
				server: server.join(","),
				flag:flag,
				condition:condition,
				formtitle:formtitle,
				hotslide:hotslide
			}, function(data) {
				if(data.msg != null){
					alert(data.msg);
				}
				//alert("success case count:"+data.s_case+" fail case count:"+data.f_case)
				if(data.s_case != null){
					alert("submit case count:  "+data.s_case+" \nnonsupport case count:  "+data.f_case)
				}
				$('#loaddingModel').modal('hide');
				$('#myModal').modal('hide');
			});
			ids="";
			$('#loaddingModel').modal('show');
		}
	})
	
	
});


function selectApar(){
	var deptid = $("#dept").val();
	if(deptid==""){
		$("#dserver").html("");
		return;
	}
	/* var protocol = $("#protocol").val();
	if(protocol==""){
		$("#dserver").html("");
		return;
	}  */
	/* var ideptid = $("#idept").val();ebang 
	if(ideptid==""){
		$("#idserver").html("");
		return;
	}
	var iprotocol = $("#iprotocol").val();
	if(iprotocol==""){
		$("#idserver").html("");
		return;
	}  */
	// alert(idept);
	$("#dserver").removeAttr("style");
	// $("#idserver").removeAttr("style");
	//$("#server").find("option:not(:first)").remove();
	
	$.ajax({
		url : "searchDeptServer.do?deptid="+deptid,
		success : function(data) {
			//alert(data);
			/* for ( var i in data) {
				var temp = data[i].apartmentName;
				$("#server").append(new Option(temp,temp));
			} */
			var dept="<select class='selectpicker' title='--Please Select--' id='server' multiple data-live-search='true'>"
			// var idept="<select class='selectpicker' title='--Please Select--' id='iserver' multiple data-live-search='true'>"
				//var dept="";
				for(i=0;i<data.length;i++){
					/* if(data[i].serverProtocol == protocol || protocol == 'ALL'){
						dept=dept+"<option value='"+data[i].serverName+"'>"+data[i].serverName +" ("+data[i].serverProtocol+")</option>";
						// idept=idept+"<option value='"+data[i].serverName+"'>"+data[i].serverName +" ("+data[i].serverProtocol+")</option>";
					} */
					dept=dept+"<option value='"+data[i].serverName+"'>"+data[i].serverName +" ("+data[i].serverProtocol+")</option>";
				}
			dept=dept+"</select>";
			// idept=idept+"</select>";
			$("#dserver").html(dept);
			// $("#idserver").html(idept);
			$('#server').selectpicker('refresh');
		}
	})
	
}



</script>
<style type="text/css">
 .file {
    position: relative;/*绝对定位!*/
    display: inline-block;/*设置为行内元素*/
    background: #D0EEFF;
    border: 1px solid #99D3F5;
    border-radius: 4px;
    padding: 4px 12px;
    overflow: hidden;
    color: #1E88C7;
    text-decoration: none;
    text-indent: 0;
    line-height: 20px;
}
.file input {
    position: absolute;/*相对定位*/
    right: 0;
    top: 0;
    opacity: 0;/*将上传组件设置为透明的*/
    font-size: 100px;
}
.file:hover {
    background: #AADDFF;
    border-color: #78C3F3;
    color: #004974;
    text-decoration: none;
}

</style>
</head>
<body>
<%
		String auth = session.getAttribute("auth").toString();
%>
			<div class="panel panel-default" style="margin-top: 10px;margin-left: 10px;margin-right: 10px;">
			<font color="while"></font>
				<div class="panel-body" style="padding-top: 0px;background-color: #FFFFFF">
            	<div id="reminder"></div>
            	<div class="row">
            		<form action="com.alucn.web.server/SRMangerServlet" method="post" name="submitspartdb" id="submitspartdb">
            		<div class="col-md-12" style="padding-right: 0px;padding-left: 0px;">
            			<div class="panel-group">
			            <div class="panel panel-default">
			                <!-- 折叠panel头部 -->
			                <div class="panel-heading">
			                    <h4 class="panel-title" data-toggle="collapse" data-target="#chanel_demo">
			                    <a href="#" ><strong>More Options</strong></a>
			                    <span class="glyphicon glyphicon-chevron-down navbar-right" style="padding-right: 10px;"></span>
			                    </h4>
			                </div>
			                <!-- 折叠panel内容 -->
			                <div class="collapse panel-collapse" id="chanel_demo"><!-- 添加一个collapse类会默认隐藏折叠内容 -->
			                    <div class="panel-body">
										<div class="row" style="margin-bottom: 10px;margin-right: 10px;">
											<div class="col-md-4">
												<c:if test="${Math.ceil(Math.random()*10) > 9}" var="flag" scope="session">
													<h5 style="display: inline;" data-toggle="tooltip" data-placement="bottom" title="You make millions of decisions that mean nothing and then one day your order takes out and it changes your life."><strong>Author</strong></h5>
												</c:if>
												 <c:if test="${not flag}">
													<h5 style="display: inline;"><strong>Author</strong></h5>
												</c:if>
												<input class="nav navbar-nav navbar-right" type="text" name="author" id="author" value=""
												style="padding-bottom: 2px; padding-top: 2px;width: 219px;border-radius:4px;">
											</div>
											<div class="col-md-4">
												<h5 style="display: inline;"><strong>Base Data</strong></h5>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" >
												    <c:if test="${base_data!=null && fn:length(base_data) > 0}">
														<c:forEach items="${base_data}" var="bd">
															<option name="bd" value="${bd}">${bd}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
											<div class="col-md-4">
												<h5 style="display: inline;"><strong>Mate</strong></h5>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" data-max-options="1">
												    <c:if test="${mate!=null && fn:length(mate) > 0}">
														<c:forEach items="${mate}" var="m">
															<option  name="m" value="${m}">${m}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
										</div>
										<div class="row" style="margin-bottom: 10px;margin-right: 10px;">
											<div class="col-md-4">
												<h5 style="display: inline;"><strong>Lab Number</strong></h5>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" data-max-options="1">
												    <c:if test="${lab_number!=null && fn:length(lab_number) > 0}">
														<c:forEach items="${lab_number}" var="ln">
															<option name="ln" value="${ln}">${ln}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
											<div class="col-md-4">
												<h5 style="display: inline;"><strong>Special Data</strong></h5>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" data-max-options="1">
												    <c:if test="${special_data!=null && fn:length(special_data) > 0}">
														<c:forEach items="${special_data}" var="sd">
															<option name="sd" value="${sd}">${sd}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
											<div class="col-md-4" >
												<h5 style="display: inline;"><strong>Porting Release</strong></h5>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" >
												    <c:if test="${porting_release!=null && fn:length(porting_release) > 0}">
														<c:forEach items="${porting_release}" var="pr">
															<option name="pr" value="${pr}">${pr}</option>
														</c:forEach>
													</c:if>
												</select>
											</div>
										</div>
										<div class="row" style="margin-bottom: 10px;margin-right: 10px;">
											<div class="col-md-4">
												<h5 style="display: inline;"><strong>Release</strong></h5>
												<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" >
												    <c:if test="${release!=null && fn:length(release) > 0}">
														<c:forEach items="${release}" var="r">
															<option name="r" value="${r}">${r}</option>
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
									<h5 style="display: inline;"><strong>WorkableRelease</strong></h5>
									<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true"  data-max-options="1">
									    <c:if test="${release!=null && fn:length(release) > 0}">
											<c:forEach items="${release}" var="wr">
												<option name="wr" value="${wr}">${wr}</option>
											</c:forEach>
										</c:if>
									</select>
								</div>
							</div>
							<div class="col-md-4">
								<div style="margin-right: 13px;">
									<h5 style="display: inline;"><strong>Case Status</strong></h5>
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
									<h5 style="display: inline;"><strong>Customer</strong></h5>
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
									<h5 style="display: inline;"><strong>Data Source</strong></h5>
									<select class="selectpicker nav navbar-nav navbar-right"  data-live-search="true" data-max-options="1" >
									    <c:if test="${data_source!=null && fn:length(data_source) > 0}">
											<c:forEach items="${data_source}" var="ds">
												<option name='ds' value="${ds}">${ds}</option>
											</c:forEach>
										</c:if>
									</select>
								</div>
							</div>
							<div class="col-md-4">
								<div style="margin-right: 0;">
									<h5 style="display: inline;"><strong>Feature ID</strong></h5>
									<input class="nav navbar-nav navbar-right" type="text" name="featureid" id="featureid" value=""
										style="padding-bottom: 2px; padding-top: 2px;width: 219px;margin-right: 0.1px;border-radius:4px;">
								</div>
							</div>
							<div class="col-md-4">
								<div style="margin-right: 28px;">
									<h5 style="display: inline;"><strong>Protocol</strong></h5>
									<select class="selectpicker nav navbar-nav navbar-right" multiple data-live-search="true" data-max-options="1" >
										<option name='proq' value="ANSI">ANSI</option>
										<option name='proq' value="ITU">ITU</option>
									</select>
								</div>
							</div>
            			</div>
            		</div>
            		<br/><br/>
					
					<div class="row">
						<div class="col-md-12 column text-right">
							<button type="button" class="btn btn-default " id="import" >&nbsp;&nbsp;&nbsp;Import&nbsp;&nbsp;&nbsp;</button>
							<button type="button" class="btn btn-primary " id="search" style="margin-right: 30px;">&nbsp;&nbsp;&nbsp;Search&nbsp;&nbsp;&nbsp;</button>
						</div>
					</div>
				</form>
            	</div>
            	<br>
            	<div class="row" style="padding-bottom: 5px;padding-top:5px;background-color: #E0E0EB">
            	</div>
            	<div class="row" style="margin-left: 10px;margin-right: 13px;margin-top: 10px;">
					<table id="runLog"  class="text-nowrap" style="background-color: #FBFCFC"></table>
				</div>
            	<div id="myModal" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog">
				    <div class="modal-dialog modal-lg" role="document">
				        <div class="modal-content">
				            <div class="modal-header">
				                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span>
				                </button>
				                 <h4 class="modal-title" style="display: inline;">Select Case   </h4><code id="importCount"></code>
				            </div>
				            <div class="modal-body">
				            	<!-- <form id="formSearch" class="form-horizontal">
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
				                </form> -->
				                <input id="inCaseType" hidden />
				                <div class="row form-horizontal form-group" style="margin-top: 10px;">
				                	 <label for="formtitle" class="col-sm-1 control-label">Title</label>
				                	 <div class="col-sm-5">
				                	 	<input class="form-control" type="text" name="formtitle" id="formtitle" value="" />
				                	 </div>
				                	 <br><br>
				                	 <label for="hotslide" class="col-sm-1 control-label">Hotslide</label>
				                	 <div class="col-sm-8">
				                	 	<input class="form-control" type="text" name="hotslide" id="hotslide" value="" />
				                	 </div>
				                </div>
				                <div id="selectCase" class="row">
				                	<div class="col-md-12 table-responsive" style="max-height:398px;overflow:scroll">
					                	 <div id="toolbar" class="btn-group" >
								         	<div class="checkbox form-group" style="margin-bottom: 0px;">
											    <label>
											      <input id="select_all" type="checkbox" style="margin-top: 10px;">Select All Case
											    </label>
											</div>
								        </div>
					                	<table id="tb_departments" class="table table-bordered table-striped text-nowrap" style="width:100%;height: 100%;background-color: #FBFCFC"></table>
				                	</div>
				                </div>
				                <div id="importCase" class="row" style="display: none;">
				                	<div id="inputbox"  class="file-loading">
				                		<div id="drop" style="margin-left: 20px;margin-right: 20px;">
					    					<p><input id="kv-explorer" type="file" class="file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" multiple data-min-file-count="1"/></p>
					    				</div>
					    			</div>
					    			<div id="inputDiv" class="table-responsive" hidden style="margin-left: 20px;margin-right: 20px;max-height:298px;overflow:scroll">
					                	<table id="inpuntTable" class="table table-bordered table-condensed table-hover table-striped text-nowrap" style="width:100%;height: 100%;background-color: #FBFCFC">
					                	</table>
						            </div>
				                </div>
				                <div class="row">
					                <div class="col-md-12">
					                	<h4 style="display: inline;">Please select case to be run in <strong style="color: red;"><span id="ser"></span></strong></h4>
										<!-- <select id="protocol" name="protocol">
											<option name='pro' value="ALL">ALL</option>
											<option name='pro' value="ITU">ITU</option>
											<option name='pro' value="ANSI">ANSI</option>
										</select> -->
										<select id="dept" name="dept" onchange="selectApar()" title="--Please Select Group--" class="selectpicker" multiple data-live-search="true" data-max-options="1"><!-- data-max-options="1" -->
											<c:if test="${deptmap!=null && fn:length(deptmap) > 0}">
												<%-- <% if(auth.equals("errorCases")){ %> --%>
													<c:forEach items="${deptmap}" var="s">
														<option name='s' value="${s.value}">${s.key}</option>
													</c:forEach>
												<%-- <%}%> --%>
											</c:if>
										</select>
										<span id="dserver" style="display: none;"></span>
										<%--<select id="server" name="server" title="--Please Select--" class="selectpicker" multiple data-live-search="true" disabled="disabled"><!-- data-max-options="1" -->
											 <c:if test="${servers!=null && fn:length(servers) > 0}">
												<% if(auth.equals("errorCases")){ %>
													<c:forEach items="${servers}" var="s">
														<option name='s' value="${s}">${s}</option>
													</c:forEach>
												<%}%>
												</c:if> 
										</select>--%>
									</div>
								</div>
								
				            </div>
				            <div class="modal-footer">
				                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				                <button type="button" class="btn btn-default" id="RunOnly">&nbsp;&nbsp;&nbsp;&nbsp;Only Run&nbsp;&nbsp;&nbsp;&nbsp;</button>
				                <!-- 暂时关闭该功能 -->
				                <%-- <shiro:hasPermission name="case:update">
				                	<button type="button" class="btn btn-primary" id="Rerunning">&nbsp;&nbsp;&nbsp;&nbsp;Update Run&nbsp;&nbsp;&nbsp;&nbsp;</button>
				                </shiro:hasPermission> --%>
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
		        <p id="cases_count"></p>
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
		        <h4 class="modal-title"><center>Being submitted</center></h4>  
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
					  <h4 style="color: red;"><strong>Being submitted , Please don't close the page !</strong></h4>
		        </center>
		      </div>  
		    </div><!-- /.modal-content -->  
		  </div><!-- /.modal-dialog -->  
		</div><!-- /.modal -->  
		
</body>
</html>