<%@page import="com.sogou.map.logreplay.util.AuthUtil"%>
<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>系统状态</title>
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
						 &gt; <a href="${ctx_path}/user/detail.htm">系统状态</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
				<div class="col-sm-6">
					<div id="J_jvmMemoryChart" style="height:300px;"></div>
				</div>
				<div class="col-sm-6">
					<div id="J_systemMemoryChart" style="height: 300px;"></div>
				</div>
			</div>
			<div class="row"><!-- row3 -->
			    <div class="col-sm-12">
			     	<div class="panel panel-default">
			     		<table class="table table-bordered table-condensed">
			     			<tbody id="J_systemInfoTbody">
			     			</tbody>
			     			<script type="text/x-jquery-tmpl" id="J_systemInfoTmpl">
								<tr>
			     					<td style="width: 110px;"><strong>操作系统</strong></td>
			     					<td>
										<img src="${ctx_path}/monitoring?resource=servers/${'${'}osIcon}" />
										${'${'}osInfo} (${'${'}availableProcessors}核心)
									</td>
			     				</tr>
			     				<tr>
			     					<td><strong>Java版本</strong></td>
			     					<td>${'${'}javaVersion}</td>
			     				</tr>
			     				<tr>
			     					<td><strong>JVM版本</strong></td>
			     					<td>${'${'}jvmVersion}</td>
			     				</tr>
			     				<tr>
			     					<td><strong>PID</strong></td>
			     					<td>${'${'}pid}</td>
			     				</tr>
			     				<tr>
			     					<td><strong>服务器</strong></td>
			     					<td>
										<img src="${ctx_path}/monitoring?resource=servers/${'${'}serverIcon}" />
										${'${'}serverInfo}
									</td>
			     				</tr>
			     				<tr>
			     					<td><strong>启动时间</strong></td>
			     					<td>${'${'}startTime}</td>
			     				</tr>
			     				<tr>
			     					<td><strong>JVM参数</strong></td>
			     					<td>${'${'}jvmArguments}</td>
			     				</tr>
							</script>
			     		</table>
			     	</div>
				</div>
			</div><!--/row3-->
		</div>
	</div><!-- /row1 -->
</div>

<%@ include file="../include/includeJs.jsp" %>
<script src="${static_path}/js/echarts/echarts-all.js"></script>
<script>
seajs.use('app/monitor/system', function(system) {
	system.init();
});
</script>
</body>
</html>

