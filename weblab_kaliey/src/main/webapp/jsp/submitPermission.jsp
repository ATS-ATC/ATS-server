<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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

<script src="${pageContext.request.contextPath}/js/moment.min.js"></script>
<script src="${pageContext.request.contextPath}/js/date.format.js"></script>
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
<title>Submit Permission</title>
</head>
<body style="background-color: #ECEFF3;">
    <!-- style="background-color:#ECEFF3;" -->
    <div class="panel-body" style="padding-bottom: 0px;">
        <div class="panel panel-default">
            <div class="panel-heading">Submit Permission</div>
            <div class="panel-body" style="padding-bottom: 0px;">
                <div class="row" style="margin-left: 10px;margin-right: 13px;">
                    <table id="permission_search_para"  class="text-nowrap" style="background-color: #FBFCFC"></table>
                </div>
                <br/>
                <div class="row" style="margin-bottom: 10px">
	                <div class="col-md-12 column text-right">      
		                <div class="col-md-8  text-right"></div>           
		                <div class="col-md-4  text-right">                   
		                    <button type="button" class="btn btn-primary " id="btn_query" style="margin-right: 30px;">&nbsp;&nbsp;&nbsp;Search&nbsp;&nbsp;&nbsp;</button>
		                </div>
		            </div>
                </div>
            </div>
        </div>
        
        <div class="panel panel-default">
            <div class="panel-body" style="padding-bottom: 0px;">
                <div class="row" style="margin-left: 10px;margin-right: 13px;">
                    <table id="permission_add_para"  class="text-nowrap" style="background-color: #FBFCFC"></table>
                </div>
                <br/>
                <div class="row" style="margin-bottom: 10px">
                    <div class="col-md-12 column text-right">      
                        <div class="col-md-8  text-right"></div>           
                        <div class="col-md-4  text-right">                   
                            <button type="button" class="btn btn-primary " id="btn_update" style="margin-right: 30px;">&nbsp;&nbsp;&nbsp;Add&nbsp;&nbsp;&nbsp;</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div style="background-color: white;">
            <div class="row"
                style="margin-left: 10px; margin-right: 13px; margin-top: 10px;">
                <table id="tb_feature_permission" data-mobile-responsive="true" class="text-nowrap" style="background-color: #FBFCFC"></table>
            </div>
        </div>
        
        <div id="reminder"></div>
    </div>

</body>
<script type="text/javascript">
    $.fn.bootstrapBtn = $.fn.button.noConflict();
    function deleteRow(row){ 
        row.deleteFlag = "true";
        $('#tb_feature_permission').bootstrapTable('remove',{field:"deleteFlag", values:["true"]});
    };
    
    function confirm_delete(row, info) {
        $("#reminder").dialog({
            modal : true,
            closeOnEscape:false,
            buttons : {
                "Confirm" : function() {             	
                    $.ajax({
                        url:"deleteSubmitPermission.do",
                        data:"feature_number="+row.feature_number + "&user_name=" + row.user_name + "&type=" + row.type,
                        
                        success:function(data){
                            if(data.result=="SUCCESS"){
                            	deleteRow(row);
                            }else  {
                                alert("Delete failed.");
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
    
    
    
    function ftc_date_format(value, row, index) {
       if (value instanceof Date)
       {
    	   return value.format("Y-m-d");
       }
       else
       {
    	   var tmp_date = '2099/12/31'
              if(value != 0)
              {
                  tmp_date = value + '';
                  tmp_date = tmp_date.slice(0,4) + '/' + tmp_date.slice(4,6) + '/' + tmp_date.slice(6,8);
              }                       
  
              var date = new Date(tmp_date);
              return date.format("Y-m-d");
       }
          
    };
    
    function saveData(index, field, value) {
        $('#tb_feature_permission').bootstrapTable('updateCell', {
            index: index,       //行索引
            field: field,       //列名
            value: value        //cell值
        })
    }; 
        
    function addFunctionAlty(value, row, index) {
       return [
       '<button id="delete" type="button" class="btn btn-danger">Delete</button>'
       ].join('');
    };
 
   	window.operateEvents = {
        'click #delete': function (e, value, row, index) 
        {  		       		     
           confirm_delete(row, "Confirm to delete this record?")
         }
     };
    
   	
    	
    $(function() {
    	var editable_obj = ${editable_obj};
    	
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
            console.log(table_data_dict);
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
        
        var permission_search_columns = ${permission_search}['columns']  
        var permission_search_datas = ${permission_search}['datas']
  
        var permission_add_columns = ${permission_add}['columns']  
        var permission_add_datas = ${permission_add}['datas']
        
        var permission_search_para_table = $('#permission_search_para')
        var permissionSearchParaTable = new ParaTableInit(permission_search_para_table, permission_search_columns, permission_search_datas);
        permissionSearchParaTable.Init();
        
        var permission_add_para_table = $('#permission_add_para')
        var permissionAddParaTable = new ParaTableInit(permission_add_para_table, permission_add_columns, permission_add_datas);
        permissionAddParaTable.Init();

        init_editables(editable_obj);
        

        $("#btn_query").click(function() {
            $('#tb_feature_permission').bootstrapTable('refresh', {
                url : 'getSubmitPermissionInfo.do',
                queryParams : oTable.queryParams
            });
        });
        
        $("#btn_update").click(function() {
        	var sdata = get_table_data(['permission_add_para'], true);
        	sdata['type'] = sdata['scenario'];
        	sdata['case_num'] = sdata['auto_case_num'];
        	sdata['is_special'] = "N";
        	sdata['sec_data_num'] = "0";
        	console.log(sdata);
        	$.ajax({
                url:"newSubmitPermission.do",
                data:sdata,
                success:function(data){
                    //alert(data);
                    if(data.result=="SUCCESS"){
                        alert("Permission added.");
                        $('#tb_feature_permission').bootstrapTable('insertRow', {
                        	index: 0,
                        	row: sdata
                        	});
                    }else {
                        alert("Add permission failed.");                      
                    }
                }
            });
        	
        });
        
        var TableInit = function() {
            var oTableInit = new Object();
            //初始化Table
            oTableInit.Init = function() {
                $('#tb_feature_permission').bootstrapTable({
                    url : 'getSubmitPermissionInfo.do', //请求后台的URL（*）
                    method : 'get', //请求方式（*）
                    //toolbar : '#toolbar', //工具按钮用哪个容器
                    //toolbarAlign : "left",
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
                    //showRefresh : true, //是否显示刷新按钮
                    minimumCountColumns : 2, //最少允许的列数
                    //clickToSelect : true, //是否启用点击选中行
                    //height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                    uniqueId : "row_id", //每一行的唯一标识，一般为主键列
                    //showToggle : true, //是否显示详细视图和列表视图的切换按钮
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
                        field:'row_id',
                        title:'Index',
                        formatter:function(value,row,index){
                            row.row_id = index;
                            return index+1;
                        }
                      },
                      {
                        field : 'feature_number',
                        title : 'Feature Number',
                        editable: {
                            type: 'text',
                            title: 'Feature Number',
                            validate: function (v) {
                                if (!v) return 'Feature number must exist!';

                            }
                        }
                    }, {
                        field : 'user_name',
                        title : 'User Name',
                        editable: {
                            type: 'text',
                            title: 'User Name',
                            validate: function (v) {
                                if (!v) return 'User name must exist!';

                            }
                        }
                    }, {
                        field : 'case_num',
                        title : 'Auto Case Num',
                        editable: {
                            type: 'text',
                            title: 'Auto Case Num',
                            validate: function (v) {
                                if (!v) return 'Auto case num must exist!';

                            }
                        }
                    }, {
                        field : 'ftc_date',
                        title : 'FTC Date',  
                        formatter: ftc_date_format,
                        editable: {
                            type: 'date',
                            title: 'FTC Date',
                            format: 'yyyy-mm-dd',
                            viewformat: 'yyyymmdd'
                            
                        }
                    }, {
                        field : 'is_special',
                        title : 'Special Data',
                        <shiro:hasPermission name="permission:edit">
                        editable: {
                            type: 'select',
                            title: 'Special Data',
                            source:[{value:"N",text:"N"},{value:"Y",text:"Y"}]
                        }
                        </shiro:hasPermission>
                    } , {
                        field : 'sec_data_num',
                        title : 'Second Data Num',
                        <shiro:hasPermission name="permission:edit">
                        editable: {
                            type: 'text',
                            title: 'Auto Case Num',
                            validate: function (v) {
                                if (!v) return 'Second Data num must exist!';

                            }
                        }
                        </shiro:hasPermission>
                    }, {
                        field : 'type',
                        title : 'Case Type',
                        editable: {
                            type: 'select',
                            title: 'Case Type',
                            source:'[{value:"dft",text:"DFT"},{value:"ut",text:"UT"},{value:"ar",text:"AR"}]'
                        }
                    },{
                         field: 'operate',
                         title: 'Operation',
                         events: operateEvents,
                         formatter: addFunctionAlty
                     }],
                     onEditableSave: function (field, row, rowIndex, oldValue, $element) { 
                         if(field == 'ftc_date')
                         {
                             var new_ftc_date = row['ftc_date'] + '';
                             row[field] = new_ftc_date.split('-').join('');
                         }
                         row["field"] = field;
                         row["value"] = oldValue;    
                         $.ajax({
                             url: "updateSubmitPermission.do",
                             data: row, 
                               
                             success: function (data) {
                                 //alert(data.result)
                                 if (data.result == "SUCCESS") {
                                     alert('Update success.');
                                 }
                                 else
                                 {
                                     alert('Update failed.');
                                     saveData(rowIndex, field, oldValue);
                                 }
                             },
                             error: function () {
                                 alert('Edit Failed.');
                             }
                             
                         });
                     }
                     
                
                });
                
                
            };

            //得到查询的参数
            oTableInit.queryParams = function(params) {
                var temp = { //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
                    limit : params.limit, //页面大小
                    offset : params.offset, //页码
                    condition : get_table_data(['permission_search_para'], false)
                };
                console.log(temp);
                return temp;
            };
            return oTableInit;
            
            
        };
        var oTable = new TableInit();
        oTable.Init();

    });

    
    
</script>
</html>