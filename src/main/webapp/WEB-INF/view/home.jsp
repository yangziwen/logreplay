<%@page import="io.github.yangziwen.logreplay.util.AuthUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="./include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>主页</title>
	<%@ include file="./include/includeCss.jsp" %>
</head>
<body>
<%@ include file="./include/includeTopBar.jsp" %>

<div class="container">
	<div class="row">
	
		<%@ include file="./include/includeLeftMenu.jsp" %>
		
		<div class="col-sm-10">
			<div>
			    <ul class="breadcrumb">
			        <li>
						当前位置: <a href="${ctx_path}/home.htm">主页</a>
			        </li> 
			    </ul>
			</div>
			
			<div class="row">
			    <div class="box col-md-12">
			    	<div style="font-family: 'Microsoft Yahei'; text-align: center; margin-top: 50px;">
				    	<h2><strong>你好，<%=AuthUtil.getScreenName()%>！</strong></h2>
			    	</div>
			    </div>
			</div><!--/row-->
			
		</div>
	</div>
</div>
<%@ include file="./include/includeJs.jsp" %>
<script>
seajs.use('app/home', function(home) {
	home.init();
});
</script>
</body>
</html>

