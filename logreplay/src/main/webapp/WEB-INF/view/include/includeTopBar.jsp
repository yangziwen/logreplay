<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<nav class="navbar navbar-default navbar-fixed-top top-bar" role="navigation">
	<div class="container-fluit">
		<div class="collapse navbar-collapse">
			<div class="col-sm-3"></div>
			<div class="col-sm-6">
				<ul class="nav navbar-left text-center" style="margin-top:6px; width: 100%;">
					<li class="title"><strong>Sogou客户端日志埋点回放系统</strong></li>
				</ul>
			</div>
			<shiro:user>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="${ctx_path}/home.htm">主页</a></li>
					<li><a href="###"><shiro:principal/></a></li>
					<li><a href="${ctx_path}/logout.htm">退出</a></li>
					<li style="width: 10px;"></li>
				</ul>
			</shiro:user>
		</div>
	</div>
</nav>
