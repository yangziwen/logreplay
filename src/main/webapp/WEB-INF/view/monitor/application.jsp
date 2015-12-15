<%@page import="com.sogou.map.logreplay.util.AuthUtil"%>
<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>应用状态</title>
	<%@ include file="../include/includeCss.jsp" %>
	<style>
		.panel-heading > .title {
			font-size: 18px;
			font-family: 'Microsoft Yahei'
		}
		#J_systemInfoTbody > tr > td:nth-child(2) {
			text-align: left;
			word-wrap: break-word;
			word-break: break-all;	
		}
	</style>
</head>
<body>

<%@ include file="../include/includeTopBar.jsp" %>

<div class="container" style="margin-bottom: 50px;">
	<div class="row"><!-- row1 -->
		<%@ include file="../include/includeLeftMenu.jsp" %>
		<div class="col-sm-10">
			<div>
			    <ul class="breadcrumb">
			        <li>
						当前位置: <a data-toggle="collapse" href="#J_systemManageSubmenu">系统监控</a>
						 &gt; <a href="${ctx_path}/monitor/application.htm">应用状态</a>
			        </li> 
			    </ul>
			</div>
			<div class="row">
				<div class="col-sm-6">
					<div id="J_threadChart" style="height:300px;"></div>
				</div>
				<div class="col-sm-6">
					<div id="J_httpChart" style="height: 300px;"></div>
				</div>
			</div>
			<div class="row"><!-- row2 -->
				<div class="col-sm-6">
					<div id="J_classChart" style="height:300px;"></div>
				</div>
				<div class="col-sm-6">
					<div id="J_sqlChart" style="height: 300px;"></div>
				</div>
			</div>
		</div>
	</div><!-- /row1 -->
</div>

<%@ include file="../include/includeJs.jsp" %>
<script src="${static_path}/js/echarts/echarts-all.js"></script>
<script>
seajs.use('app/monitor/application', function(application) {
	application.init();
});
</script>
</body>
</html>

