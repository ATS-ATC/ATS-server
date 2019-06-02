<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="${pageContext.request.contextPath}/jquery-3.4.1/jquery-3.4.1.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.css" >
<script src="${pageContext.request.contextPath}/jquery-ui-1.12.1/jquery-ui.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/css/bootstrap.css">
<script src="${pageContext.request.contextPath}/bootstrap-3.4.1/dist/js/bootstrap.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.css">
<script src="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/bootstrap-table.js"></script>

<script src="${pageContext.request.contextPath}/bootstrap-table-v1.5.4/extensions/editable/bootstrap-table-editable.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap3-editable-1.5.1/bootstrap3-editable/css/bootstrap-editable.css">
<script src="${pageContext.request.contextPath}/bootstrap3-editable-1.5.1/bootstrap3-editable/js/bootstrap-editable.js"></script>

<link href="${pageContext.request.contextPath}/css/bootstrap-tagsinput.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/js/bootstrap-tagsinput.js"></script>

<link href="${pageContext.request.contextPath}/css/loading.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/js/loading.js"></script>

<script src="${pageContext.request.contextPath}/js/xlsx.full.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jszip.js"></script>

<link href="${pageContext.request.contextPath}/css/fileinput.min.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/fileinput.min.js" type="text/javascript"></script>

<link href="${pageContext.request.contextPath}/css/bootstrap-select.min.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/bootstrap-select.min.js"></script>

<script src="${pageContext.request.contextPath}/js/moment.min.js"></script>
<script src="${pageContext.request.contextPath}/js/date.format.js"></script>

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

.hide_header {
height: 1px; border: none; display:none
}
</style>
<script>
    $.fn.bootstrapBtn = $.fn.button.noConflict();
    function confirm_cancel(index, row, info) {
	    $("#reminder").dialog({
	        modal : true,
	        closeOnEscape:false,
	        buttons : {
	            "Confirm" : function() {                
	                $.ajax({
	                    url:"cancelBatch.do",
	                    data:"batch_id="+row.int_id,
	                    
	                    success:function(data){
	                        if(data.result=="SUCCESS"){
	                        	 $('#runLog').bootstrapTable('updateCell', {
	                                 index: index,       //行索引
	                                 field: 'running_count',       //列名
	                                 value: 0        //cell值
	                             })
	                        }else  {
	                            alert("Cancel failed.");
	                        }
	                    }
	                });
	                $(this).dialog("close");
	            },
	            "Cancel" : function() {
	                $(this).dialog("close");
	            }
	        },
	        open : function(event, ui) {
	            $(".ui-dialog-titlebar-close").hide();
	            $(this).html("");
	            $(this).append("<p>" + info + "</p>");
	        }
	    });
	
	};

    function cancel_button_format(value, row, index) {
       if(row.running_count == 0)
       {
    	   return ['<button id="cancel" type="button" class="btn btn-danger"  disabled>Cancel</button>'].join('');
       }
       return ['<button id="cancel" type="button" class="btn btn-danger">Cancel</button>'].join('');
    };
	 
    window.cancel_events = {
        'click #cancel': function (e, value, row, index) 
        {                        
           confirm_cancel(index, row, "Confirm to cancel this task?")
         }
     };
	
	$(function() {
		
		var editable_obj = ${editable_obj};
		var run_mode = "search";
		
		queryParams = function (params) {
		    var temp = {    
		        limit: params.limit,    
		        offset: params.offset   
		    };
		    return temp;
		};
		
		/*
		$('.form_datetime').datetimepicker({
	        //language:  'fr',
	        weekStart: 1,
	        todayBtn:  1,
	        autoclose: 1,
	        todayHighlight: 1,
	        startView: 2,
	        minView: "month",
	        format: "yyyy-mm-dd",
	        forceParse: 0,
	        showMeridian: 1
	    });
		*/
		var do_text = function(obj){ 
	        var text_name = '#' + obj['id'];
	        $(text_name).editable({   
	        	type: 'text',
	            title: obj['id'],
	            placement: 'bottom',
	            success: function(response, newValue) {
	            	$(this).editable('setValue',newValue)//update backbone model
	            }
	        });
	    }
		
		var do_checklist = function(obj){
			var checklist_name = '#' + obj['id'];
			$(checklist_name).editable({
		        type: 'checklist',
		        value: obj['value'],    
		        placement: 'bottom',
		        source: obj['source'],
		        success: function(response, newValue) {
	                $(this).editable('setValue',newValue)//update backbone model
	                console.log($(this).editable('getValue'));
	            }
		    });
		}
		
		var do_select = function(obj){
	        var select_name = '#' + obj['id'];
	        $(select_name).editable({
	        	type: 'select',
	            value: obj['value'],   
	            placement: 'bottom',
	            source: obj['source'],
	            success: function(response, newValue) {
	            	if(obj['id'] == 'base-scenario')
	            	{
	            		var old_value = $(this).editable('getValue')['base-scenario'];
	                    if(newValue == 'ar')
	                    {
	                        if(old_value != 'ar')
	                        {
	                            var tagParaTable = new ParaTableInit(tag_para_table, base_columns, ar_datas);
	                            tagParaTable.Init();
	                            init_editables(editable_obj);
	                        }
	                    }
	                    else
	                    {
	                        if(old_value == 'ar')
	                        {
	                            var tagParaTable = new ParaTableInit(tag_para_table, base_columns, dft_datas);
	                            tagParaTable.Init();
	                            init_editables(editable_obj);
	                        }
	                    }
	            	}
	                $(this).editable('setValue',newValue)//update backbone model
	            }
	        });
	    }
		
		var do_date = function(obj){
	        var date_name = '#' + obj['id'];
	        $(date_name).editable({
	        	type: 'date',
	        	format: 'yyyymmdd',
	        	placement: 'bottom',
	            viewformat: 'yyyymmdd',
	            success: function(response, newValue) {
	                $(this).editable('setValue',newValue)//update backbone model
	            }
	        });
	    }
		
		var init_editables = function(editables){
			for (let myedit_type in editables) 
		    {   
		        var myedit_list = editables[myedit_type];
		        for (var i = 0; i < myedit_list.length; i++) 
		        {            
		             //window[func](myedit_list[i]);  failed
		             if(myedit_type == "text")
		             {
		                 do_text(myedit_list[i]);
		             }
		             else if(myedit_type == "date")
		             {
		                 do_date(myedit_list[i]);
		             }else if(myedit_type == "select")
		             {
		                 do_select(myedit_list[i]);
		             }else if(myedit_type == "checklist")
		             {
		                 do_checklist(myedit_list[i]);
		             }
		        }
		    }
		}
		
		var get_table_data = function(table_ids, return_dict){
	        
	        var table_data = "";
	        var table_data_dict = {};
	        for (var l = 0; l < table_ids.length; l++)
	        {
	            var table_name = '#'+ table_ids[l];
	             var rows = $(table_name).bootstrapTable('getData');//行的数据
	             for(var i=0;i<rows.length;i++){
	                 var tmp_value = rows[i].value.toString();
	                 var id_index = tmp_value.indexOf("id=\"")+4;
	                 tmp_value = tmp_value.slice(id_index);
	                 var value_id = tmp_value.slice(0, tmp_value.indexOf('"'));
	                 var real_value = $('#'+value_id).editable('getValue')[value_id];
	                 if(typeof(real_value)!= "undefined" &&  real_value != "")
	                 {
	                     table_data += "&" + rows[i].name + "=" + real_value;
	                     table_data_dict[rows[i].name] = real_value;
	                 }
	                 else
	                 {
	                     table_data_dict[rows[i].name] = '';
	                 }
	             }
	        }
	         if(table_data != "")
	         {
	             table_data = table_data.slice(1);
	         }
	         if (return_dict)
	         {
	             return table_data_dict;
	         }
	         return table_data;
	             
	    }
	    
	    var ParaTableInit = function (l_table, l_columns, l_datas) {
	        var LparaTableInit = new Object();
	        //初始化Table
	        LparaTableInit.Init = function () {
	        	l_table.bootstrapTable('destroy');
	            l_table.bootstrapTable({
	                striped: true,
	                cache: false,                       
	                sortable: false,                              
	                strictSearch: true,
	                //showColumns: true,                 
	                minimumCountColumns: 2,   
	                clickToSelect: false,           
	                cardView: false,        
	                detailView: false,      
	                columns: l_columns,
	                data: l_datas
	
	            });
	        };
	        
	        return LparaTableInit;
	    };
	    
	    var base_columns = ${base}['columns']
	    
	    var base_datas = ${base}['datas']
	    var dft_datas = ${dft}
	    var ar_datas = ${ar}
	    
	    var base_para_table = $('#base_para')
	    var baseParaTable = new ParaTableInit(base_para_table, base_columns, base_datas);
	    baseParaTable.Init();
	    
	    var tag_para_table = $('#tag_para')
	    var tagParaTable = new ParaTableInit(tag_para_table, base_columns, dft_datas);
	    tagParaTable.Init();
	    
	    var sanity_columns = ${sanity}['columns'];
	    var sanity_datas = ${sanity}['datas'];
	    var sanity_table = $('#sanity_para')
	    
	    var sanityParaTable = new ParaTableInit(sanity_table, sanity_columns, sanity_datas);
	    sanityParaTable.Init();
	    
	    var weekly_columns = ${weekly}['columns'];
	    var weekly_datas = ${weekly}['datas'];
	    var weekly_table = $('#weekly_para')
	    
	    var weeklyParaTable = new ParaTableInit(weekly_table, weekly_columns, weekly_datas);
	    weeklyParaTable.Init();
		
	    
	    
	    init_editables(editable_obj);
	    
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
	        },
	        {
	            field: 'run_mode',
	            title: 'Mode',
	            formatter: function statusFormatter(value, row, index) {
	                if (value == 'sanity') {
	                    return '<span class="label label-success">sanity</span>';
	                } else {
	                    return '<span class="label label-info">' + value + '</span>';
	                }
	            }
	        },
	        {
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
	        } ,{
	            field: 'operate',
	            title: 'Operation',
	            events: cancel_events,
	            formatter: cancel_button_format
	
	        } 
	        ]
	    });
		//var u = Math.random(1000)
		//setInterval('myrefresh()',1000*10*6); //指定20秒刷新一次
		
		var conform = false;
		/*
		$("#btn_query").click(function(){
			
			$('#case_list_table').bootstrapTable('destroy');
			var condition = "";
			condition = getCondition("placeholder");
			var oTable = new TableInit(condition);
		    oTable.Init();
			$('#case_list_table').bootstrapTable('refresh',{
	    		url: 'searchCaseInfo.do',
	    		queryParams: oTable.queryParams		
	    	});
	    });
		*/
		
		var TableInit = function (outCondition) {
		    var oTableInit = new Object();
		    //初始化Table
		    oTableInit.Init = function () {
		    	
		        $('#case_list_table').bootstrapTable({
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
			
			run_mode = "search";
			//$('#case_list_table').bootstrapTable('refreshOptions',{condition: outCondition}); 
			$('#case_list_table').bootstrapTable('destroy'); 
			
			//var outCondition = getCondition("placeholder");
			var outCondition = get_table_data(['base_para', 'tag_para'], false);
			if(outCondition==null){
				return false;
			}
			var oTable = new TableInit(outCondition);
			oTable.Init();
			$("#selectCase").removeAttr("style");
			$("#Rerunning").removeAttr("style");
			$("#weekly_div").removeAttr("style");
			$("#sanity_div").attr("style","display:none");
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
			var a  = $('#case_list_table').bootstrapTable('getSelections');
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
				$("#case_list_table").find('input').prop('checked',$(this).prop('checked'));
				/*阻止向上冒泡，以防再次触发点击操作*/
				event.stopPropagation();
	
			}else{
				$("#case_list_table").find('input').prop('checked',$("#case_list_table").find('input:checked').length == $("#case_list_table").length ? true : false);
				/*阻止向上冒泡，以防再次触发点击操作*/
				event.stopPropagation();
			}
		})
		
		$("#import").click(function(){
			run_mode = "import";
			$("#selectCase").attr("style","display:none");
			$("#Rerunning").attr("style","display:none");
			$("#sanity_div").attr("style","display:none");
			$("#weekly_div").removeAttr("style");
			$("#importCase").removeAttr("style");
		 	$('#myModal').modal('show');
		})
		
		$("#sanity_check").click(function(){
			run_mode = "sanity";
	        $("#selectCase").attr("style","display:none");
	        $("#Rerunning").attr("style","display:none");
	        $("#importCase").attr("style","display:none");
	        $("#weekly_div").attr("style","display:none");
	        $("#sanity_div").removeAttr("style");
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
			var server = $('#server').val(); 
			if(typeof(server) == "undefined" || server.length==0){
				alert("Please select a server to run case !");
				return false;
			}
			var formtitle =$('#formtitle').val();
			if(""==formtitle){
				alert("Please input title !");
				return false;
			}
			var condition = get_table_data(['base_para','weekly_para'], false);
			if("import" == run_mode){
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
				if(set.length==0 ){
					alert("Please import case to run !");
					return false;
				}
				$.get("onlyrun.do", {
					set : set.join(","),
					server: server.join(","),
					allselect:"",
					condition: condition,
					formtitle:formtitle,
					run_mode: run_mode
					
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
				
			} else if ("search" == run_mode) {
				//alert("select")
				var sets = "";
				var a  = $('#case_list_table').bootstrapTable('getSelections');
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
				var condition = get_table_data(['base_para','tag_para', 'weekly_para'], false);
				$.get("onlyrun.do", {
					set : sets,
					server: server.join(","),
					allselect:flag,
					condition:condition,
					formtitle:formtitle,
					run_mode: run_mode
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
				$('#loaddingModel').modal('show');
			} else if ("sanity" == run_mode)
			{
				var condition = get_table_data(['sanity_para'], false);
				var cons = condition.split('&');
				var rows = $('#sanity_para').bootstrapTable('getData');
				
				console.log(cons);
				console.log(rows);
				if(rows.length != cons.length)
				{
					alert("All parameter for sanity check are required!");
		            return false;
				}
				$.get("onlyrun.do", {
	                set : '',
	                server: server.join(","),
	                allselect:"",
	                condition: condition,
	                formtitle: formtitle,
	                run_mode: run_mode
	                
	            }, function(data) {
	                if(data.msg != null){
	                    alert(data.msg);
	                }           
	                $('#loaddingModel').modal('hide');
	                $('#myModal').modal('hide');
	            });
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
						dept=dept+"<option value='"+data[i].serverName+"'>"+data[i].serverName +" ("+data[i].setName + "-" +data[i].serverProtocol+")</option>";
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
			<div class="panel-heading">
		        <h3 class="panel-title"><strong>Run Case</strong></h3>
		    </div>
		    <br/>
			<font color="while"></font>
				<div class="panel-body" style="padding-top: 0px;background-color: #FFFFFF">

            	<div class="row" style="margin-left: 10px;margin-right: 13px;">
            		
            		<div class="row" style="max-height:400px; overflow-y: scroll;" >
	                    <div class="col-md-6 column" style="padding-right:0px;">
	                        <table id="base_para"  class="text-nowrap" style="background-color: #FBFCFC"></table>
	                    </div>
	                    <div class="col-md-6 column">
	                        <table id="tag_para" class="text-nowrap" style="background-color: #FBFCFC"></table>
	                    </div>
	                </div>
            			
            		<br/><br/>
					
					<div class="row">
						<div class="col-md-12 column text-right">
						    <button type="button" class="btn btn-primary" id="sanity_check" >&nbsp;&nbsp;&nbsp;Sanity Check&nbsp;&nbsp;&nbsp;</button>
							<button type="button" class="btn btn-primary" id="import" >&nbsp;&nbsp;&nbsp;Import&nbsp;&nbsp;&nbsp;</button>
							<button type="button" class="btn btn-primary" id="search" style="margin-right: 30px;">&nbsp;&nbsp;&nbsp;Search&nbsp;&nbsp;&nbsp;</button>
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
				                
				                <div class="row form-horizontal form-group" style="margin-top: 10px;">
				                	 <label style="text-align:left" for="formtitle" class="col-sm-2 control-label">Title</label>
				                	 <div class="col-sm-8">
				                	 	<input class="form-control" type="text" name="formtitle" id="formtitle" value="" />
				                	 </div>
				                	 <!--  
				                	 <br><br>
				                	 <label style="text-align:left" for="hotslide" class="col-sm-2 control-label">Hotslide/Official</label>
				                	 <div class="col-sm-8">
				                	 	<input class="form-control" type="text" name="hotslide" id="hotslide" value="" />
				                	 </div>
				                	 -->
				                </div>

                                <div id="sanity_div" class="row" style="display: none;">
                                    <div class="col-md-12 table-responsive" style="max-height:398px;overflow:scroll">
                                        <table id="sanity_para"  class="table table-bordered table-striped text-nowrap" style="width:100%;height: 100%;background-color: #FBFCFC"></table>
                                    </div>
                                </div>
                                <input id="in_weekly" hidden />
                                <div id="weekly_div" class="row" style="display: none;">
                                    <div class="col-md-12 table-responsive" style="max-height:398px;overflow:scroll">
                                        <table id="weekly_para"  class="table table-bordered table-striped text-nowrap" style="width:100%;height: 100%;background-color: #FBFCFC"></table>
                                    </div>
                                </div>
				                <div id="selectCase" class="row" >
				                	<div class="col-md-12 table-responsive" style="max-height:398px;overflow:scroll">
					                	 <div id="toolbar" class="btn-group" >
								         	<div class="checkbox form-group" style="margin-bottom: 0px;">
											    <label>
											      <input id="select_all" type="checkbox" style="margin-top: 10px;">Select All Case
											    </label>
											</div>
								        </div>
					                	<table id="case_list_table" class="table table-bordered table-striped text-nowrap" style="width:100%;height: 100%;background-color: #FBFCFC"></table>
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
				                <div class="row">
		                            <div class="col-md-8 column">
		                                <!--div class="form-group">
		                                    <label style="text-align:left" for="dtp_input1" class="col-md-4 control-label">Schedule Date</label>
		                                    <div class="input-group date form_datetime col-md-4" data-date-format="yyyyMMdd" data-link-field="dtp_input1">
		                                        <input id="dtp_input" class="form-control" size="16" type="text" value="" readonly>
		                                        <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
		                                        <span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
		                                    </div> 
		                                </div-->
		                            </div>
		                        
			                        <div class="col-md-4 column text-right">
			                             <button type="button" class="btn btn-default" data-dismiss="modal">&nbsp;&nbsp;&nbsp;&nbsp;Close&nbsp;&nbsp;&nbsp;&nbsp;</button>
                                         <button type="button" class="btn btn-default" id="RunOnly">&nbsp;&nbsp;&nbsp;&nbsp;Only Run&nbsp;&nbsp;&nbsp;&nbsp;</button>
			                        </div>
			                    </div>
				               
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
		<div id="reminder"></div>
</body>
</html>