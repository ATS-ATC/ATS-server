<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.alucn.casemanager.server.common.model.ServerSort" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- <meta http-equiv="refresh" content="5">页面每5秒刷新一次 -->
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<link href="${pageContext.request.contextPath}/css/bootstrap-treeview.min.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/bootstrap-treeview.min.js"></script>
<link href="./css/style.css" rel="stylesheet" />
<script type="text/javascript">
$(function() {
	function getTree() {
		var tree = '${jsonTree}';
	  return tree;
	}
	$('#tree').treeview({data: getTree()});
});	
</script>
<title>Server Info</title>
</head>
<body style="background-color:#ECEFF3;">
<div class="panel panel-primary" style="margin: 10px;">
    <div class="panel-heading">
        <h3 class="panel-title">Lab Management</h3>
    </div>
    <div class="panel-body">
       <div id="tree"></div>
    </div>
</div>
</body>
</html>