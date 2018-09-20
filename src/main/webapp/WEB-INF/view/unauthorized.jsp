<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="./include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>没有权限</title>
	<%@ include file="./include/includeCss.jsp" %>
</head>
<body>
<%@ include file="./include/includeTopBar.jsp" %>

<div class="container">
	<div style="text-align: center; font-family: 'Microsoft Yahei'; margin-top: 200px;">
		<h2><strong>对不起，当前用户没有权限! <a href="javascript: history.back();">请返回</a></strong></h2>
	</div>
</div>
</body>
</html>

