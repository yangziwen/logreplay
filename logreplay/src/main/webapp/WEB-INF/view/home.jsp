<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="./include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>home</title>
	<%@ include file="./include/includeCss.jsp" %>
</head>
<body style="height: 3000px;">
<%@ include file="./include/includeTopBar.jsp" %>

<div class="container" style="_width: 1200px;">
	<div class="row">
	
		<%@ include file="./include/includeMenuBar.jsp" %>
		
		<div class="col-lg-10 col-sm-10">
			<div>
			    <ul class="breadcrumb">
			        <li>
			            	当前位置: <a href="${ctx_path}/home.htm">主页</a>
			        </li> 
			    </ul>
			</div>
			
			<div class="row">
			    <div class="box col-md-12">
			        <div class="box-inner"> 
			            <div class="box-content" style="height: 500px;">
			                <!-- put your content here -->
			                <p>Welcome <shiro:principal />!</p>
			            </div>
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

