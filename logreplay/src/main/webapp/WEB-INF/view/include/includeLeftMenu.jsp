<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="col-sm-2 col-lg-2">
	<div class="sidebar-nav row">
			<ul class="nav nav-pills nav-stacked">
				<li class="active">
					<a href="${ctx_path}/home.htm"><i class="glyphicon glyphicon-home"></i><span> 主页</span></a>
				</li>
				<li class="active">
					<a data-toggle="collapse" href="#J_logConfigSubmenu">
						<i class="glyphicon glyphicon-plus"></i>
						<span> 日志项管理</span>
						<span class="pull-right glyphicon glyphicon-chevron-down"></span>
					</a>
					<c:set var="showSubmenu" value="${fn:contains(requestURI, 'pageInfo') or fn:contains(requestURI, 'tagInfo')}"></c:set>
					<ul id="J_logConfigSubmenu" class="nav submenu ${showSubmenu eq true? 'in': ''}" ${showSubmenu eq false? 'style="height: 0px;"': ''}>
						<li><a href="${ctx_path}/pageInfo/list.htm"><i class="glyphicon glyphicon-chevron-right"></i> 页面信息管理</a></li>
						<li><a href="${ctx_path}/tagInfo/list.htm"><i class="glyphicon glyphicon-chevron-right"></i> 操作项管理</a></li>
					</ul>
				</li>
				<li class="active">
					<a data-toggle="collapse" href="#J_logCheckSubmenu">
						<i class="glyphicon glyphicon-plus"></i>
						<span> 日志项校验</span>
						<span class="pull-right glyphicon glyphicon-chevron-down"></span>
					</a>
					<ul id="J_logCheckSubmenu" class="nav submenu" style="height: 0px;">
						<li><a href="###"><i class="glyphicon glyphicon-chevron-right"></i> 即时校验</a></li>
						<li><a href="###"><i class="glyphicon glyphicon-chevron-right"></i> 序列校验</a></li>
					</ul>
				</li>
				<li class="active">
					<a href="###"><i class="glyphicon glyphicon-plus"></i><span> 系统管理</span></a>
				</li>
			</ul>
	</div>
</div>
