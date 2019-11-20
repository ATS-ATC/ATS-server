<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
<script>
	function saveData(index, field, value) {
	    $('#tb_config').bootstrapTable('updateCell', {
	        index: index,       //行索引
	        field: field,       //列名
	        value: value        //cell值
	    })
	}; 
	
	$(function() {

		var configs = ${config};
		var TableInit = function() {
            var oTableInit = new Object();
            //初始化Table
            oTableInit.Init = function() {
                $('#tb_config').bootstrapTable({
                	striped: true,
                    cache: false,                       
                    sortable: false,                              
                    strictSearch: true,
                    //showColumns: true,                 
                    minimumCountColumns: 2,   
                    clickToSelect: false,           
                    cardView: false,        
                    detailView: false,      
                    data: configs, 
                    columns : [ 
                      {
                        field : 'con_key',
                        title : 'config name'

                    }, {
                        field : 'con_value',
                        title : 'config value',
                        editable: {
                            type: 'text',
                            title: 'config value'
                        }
                    }],
                     onEditableSave: function (field, row, rowIndex, oldValue, $element) { 
                         $.ajax({
                             url: "updateConfig.do",
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

            return oTableInit;
            
            
        };
        var oTable = new TableInit();
        oTable.Init();
	});
</script>
</head>
<body style="background-color:#ECEFF3;">
	<div class="panel-body" style="padding-bottom: 0px;">
        <div class="panel panel-default">
            <div class="panel-heading">Config Manage</div>
            
            <div style="background-color: white;">
	             <div class="row"  style="margin-left: 15%; margin-right: 15%; margin-top: 10px;">
	                 <table id="tb_config" data-mobile-responsive="true" class="text-nowrap" style="background-color: #FBFCFC"></table>
	             </div>
	        </div>
        </div>
	    
    </div>
</body>
</html>