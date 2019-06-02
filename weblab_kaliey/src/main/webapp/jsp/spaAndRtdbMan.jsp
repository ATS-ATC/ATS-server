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

<link href="${pageContext.request.contextPath}/css/adminstyle.css" rel="stylesheet">
<script>
    $.fn.bootstrapBtn = $.fn.button.noConflict();
    function checkbox_format(value, row, index) {
    if(value == "" || typeof(value)== "undefined")
    {
        return "";
    }
   
    
       return [   
       '<input type="checkbox" id="'+ value + '" value="">&nbsp;&nbsp;' + value +'&nbsp;&nbsp;</input>'
       ].join('');
    }
     
	$(function() {
		function confirm(click, info, spa, rtdb) {
			$("#reminder").dialog({
				modal : true,
				buttons : {
					"Confirm" : function() {
						$.post("./removeSpaAndRtdbInfo.do", {
							click : click,
							spa : spa,
							rtdb : rtdb
						}, function(data) {
							$("#reminder").dialog({
								buttons : {
									"Confirm" : function() {
										$(this).dialog("close");
										window.location.reload();
									}
								},
								open : function(event, ui) {
									$(this).html("");
									$(this).append(data);
								}
							});

						});
						$(this).dialog("close");
						$("#check-spartdb").dialog("close");
					},
					"Cancel" : function() {
						$(this).dialog("close");
					}
				},
				open : function(event, ui) {
					$(this).html("");
					$(this).append("<p>" + info + "</p>");
				}
			});

		}

		$("#add").click(function() {
			var spa;
			var rtdb;

			var addsr = $('#addsr').dialog(
			{
				autoOpen : false,
				height : 300,
				width : 350,
				title : "Add SPA AND RTDB",
				modal : true,
				buttons : {
					"Confirm" : function() {
						spa = $('#addspa').val();
						rtdb = $('#addrtdb').val();
						if (spa == "" && rtdb == "") {
							$("#reminder").dialog(
							{
								buttons : {
									"Confirm" : function() {
										$(this).dialog("close");
									}
								},
								open : function(event,ui) {
									$(this).html("");
									$(this).append("Please enter SPA or RTDB!");
								}
							});
							return;
						}
						$.post("./addSpaAndRtdbInfo.do",{
							click : "add",
							spa : spa,
							rtdb : rtdb
						},
						function(data) {
							$("#reminder").dialog(
							{
								buttons : {
									"Confirm" : function() {
										$(this).dialog("close");
										window.location.reload();
									}
								},
								open : function(event,ui) {
									$(this).html("");
									$(this).append(data);
								}
							});

						});
						$(this).dialog("close");
						$("#check-spartdb").dialog("close");
					},
					"Cancel" : function() {
						$(this).dialog("close");
					}
				}
			});
			$('#addsr').css("display", "block");
			addsr.dialog("open");
		});

		$("#remove").click(function() {
			var spa = get_table_checkboxs('spa_table');
			var rtdb = get_table_checkboxs('rtdb_table');
			//var spa = $('input[type="checkbox"][name="spa"]:checked');
			//var rtdb = $('input[type="checkbox"][name="rtdb"]:checked');
			var s = "";
			var r = "";
			if (spa.length == 0 && rtdb.length == 0) {
				$("#reminder").dialog(
				{
					open : function(event, ui) {
						$(this).html("");
						$(this).append("<p>RTDB or SPA was not be checked!</p>");
					}
				});
				return;
			}
			for (var i = 0; i < spa.length; i++) {
				s += spa[i];
				s += ",";
			}
			for (var i = 0; i < rtdb.length; i++) {
				r += rtdb[i];
				r += ",";
			}
			confirm("remove", "Confirm to remove this server?",s, r);
		});
		
	    var get_table_checkboxs = function(table_id){
            
            var checkboxs = [];
            var table_name = '#'+ table_id;
            var rows = $(table_name).bootstrapTable('getData');//行的数据
            for(var i=0;i<rows.length;i++){
    	        for(let td_key in rows[i])
    	        {
    	        	if ($('#'+rows[i][td_key]).is(':checked')) {
    	        		checkboxs.push(rows[i][td_key]);
    	        	}
    	        }
            } 
            return  checkboxs;    
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
        
		var spa_columns = [
			{
				field: 'spa_name#0',
                title: 'SPA',
                formatter: checkbox_format
			},
			{
                field: 'spa_name#1',
                title: '',
                formatter: checkbox_format
            },
            {
                field: 'spa_name#2',
                title: '',
                formatter: checkbox_format
            },
            {
                field: 'spa_name#3',
                title: '',
                formatter: checkbox_format
            }
		];
		
		var spa_datas = ${SPA};
		console.log(spa_datas);
		var spas_table = $('#spa_table')
        var spasTable = new ParaTableInit(spas_table, spa_columns, spa_datas);
		spasTable.Init();
		
		var rtdb_columns = [
            {
                field: 'rtdb_name#0',
                title: 'RTDB/NDB',
                formatter: checkbox_format
            },
            {
                field: 'rtdb_name#1',
                title: '',
                formatter: checkbox_format
            },
            {
                field: 'rtdb_name#2',
                title: '',
                formatter: checkbox_format
            },
            {
                field: 'rtdb_name#3',
                title: '',
                formatter: checkbox_format
            }
        ];
        
        var rtdb_datas = ${RTDB};
        var rtdbs_table = $('#rtdb_table')
        var rtdbsTable = new ParaTableInit(rtdbs_table, rtdb_columns, rtdb_datas);
        rtdbsTable.Init();
		
	});
</script>
</head>
<body style="background-color:#ECEFF3;">
    <div id="reminder"></div>
    <div class="panel-body" style="padding-bottom: 0px;">
        <div class="panel panel-default">
            <div class="panel-heading">SPA and DB Manage</div>
            <div class="panel-body" style="padding-bottom: 0px;">
                <div class="row" style="margin-left: 10px;margin-right: 13px;">
                    <table id="spa_table"  class="text-nowrap" style="background-color: #FBFCFC"></table>
                </div>
                <br/>
                <div class="row" style="margin-left: 10px;margin-right: 13px;">
                    <table id="rtdb_table"  class="text-nowrap" style="background-color: #FBFCFC"></table>
                </div>
                <br/>
                <div class="row" style="margin-left: 10px;margin-right: 13px;">
                         <div class="col-md-12 column text-right">
                             <button type="button" class="btn btn-primary" id="add">&nbsp;&nbsp;&nbsp;&nbsp;Add&nbsp;&nbsp;&nbsp;&nbsp;</button>
                             <button type="button" class="btn btn-primary" id="remove">&nbsp;&nbsp;&nbsp;&nbsp;Remove&nbsp;&nbsp;&nbsp;&nbsp;</button>
                         </div>
                         <div id="addsr" style="display: none">
                             <div class="form-group">
                                 <label for="addspa">SPA:</label> <input type="text"
                                     class="form-control" id="addspa"
                                     placeholder="Separated by commas">
                             </div>
                             <div class="form-group">
                                 <label for="addrtdb">RTDB:</label> <input type="text"
                                     class="form-control" id="addrtdb"
                                     placeholder="Separated by commas">
                             </div>
                         </div>
                   </div>
            </div>
        </div>
    </div>
    
</body>
</html>