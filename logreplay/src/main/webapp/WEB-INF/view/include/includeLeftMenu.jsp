<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div class="col-sm-2">
	<div class="sidebar-nav row">
			<ul class="nav nav-pills nav-stacked">
				<li class="active">
					<a href="${ctx_path}/home.htm"><i class="glyphicon glyphicon-home"></i><span> 主页</span></a>
				</li>
				<li class="active">
					<a class="has-submenu" data-toggle="collapse" href="#J_logConfigSubmenu">
						<i class="glyphicon glyphicon-plus"></i>
						<span> 日志项管理</span>
						<span class="pull-right glyphicon glyphicon-chevron-down"></span>
					</a>
					<c:set var="showConfigSubmenu" value="${fn:contains(requestURI, '/pageInfo') or fn:contains(requestURI, '/tagInfo')}"></c:set>
					<%-- <ul id="J_logConfigSubmenu" class="nav submenu ${showConfigSubmenu eq true? 'in': ''}" ${showConfigSubmenu eq false? 'style="height: 0px;"': ''}>  --%>
					<ul id="J_logConfigSubmenu" class="nav submenu in" >
						<li><a href="${ctx_path}/pageInfo/list.htm"><i class="glyphicon glyphicon-chevron-right"></i> 页面信息管理</a></li>
						<li><a href="${ctx_path}/tagInfo/list.htm"><i class="glyphicon glyphicon-chevron-right"></i> 操作项管理</a></li>
						<li><a href="${ctx_path}/tagInfo/commonList.htm"><i class="glyphicon glyphicon-chevron-right"></i> 公共操作项管理</a></li>
					</ul>
				</li>
				<li class="active">
					<a class="has-submenu" data-toggle="collapse" href="#J_logReplaySubmenu">
						<i class="glyphicon glyphicon-plus"></i>
						<span> 日志项校验</span>
						<span class="pull-right glyphicon glyphicon-chevron-down"></span>
					</a>
					<c:set var="showReplaySubmenu" value="${fn:contains(requestURI, '/replay') or fn:contains(requestURI, '/inspectionRecord')}"></c:set>
					<%-- <ul id="J_logReplaySubmenu" class="nav submenu ${showReplaySubmenu eq true? 'in': ''}" ${showReplaySubmenu eq false? 'style="height: 0px;"': ''}> --%>
					<ul id="J_logReplaySubmenu" class="nav submenu in">
						<li><a href="${ctx_path}/replay/realtime.htm"><i class="glyphicon glyphicon-chevron-right"></i> 实时校验</a></li>
						<%--<li><a href="${ctx_path}/replay/sequence.htm"><i class="glyphicon glyphicon-chevron-right"></i> 序列校验</a></li> --%>
						<li><a href="${ctx_path}/inspectionRecord/list.htm"><i class="glyphicon glyphicon-chevron-right"></i> 校验结果</a></li>
						<li><a href="${ctx_path}/inspectionRecord/inspectStatusList.htm"><i class="glyphicon glyphicon-chevron-right"></i> 校验状态查询</a></li>
					</ul>
				</li>
				<li class="active">
					<a class="has-submenu" data-toggle="collapse" href="#J_systemManageSubmenu">
						<i class="glyphicon glyphicon-plus"></i>
						<span> 系统管理</span>
						<span class="pull-right glyphicon glyphicon-chevron-down"></span>
					</a>
					<c:set var="showSystemManageSubmenu" value="${fn:contains(requestURI, '/admin') or fn:contains(requestURI, '/user/detail.htm')}"></c:set>
					<%-- <ul id="J_systemManageSubmenu" class="nav submenu ${showSystemManageSubmenu eq true? 'in': ''}" ${showSystemManageSubmenu eq false? 'style="height: 0px;"': ''}> --%>
					<ul id="J_systemManageSubmenu" class="nav submenu in" >
						<shiro:hasRole name="admin">
							<li><a href="${ctx_path}/admin/user/list.htm"><i class="glyphicon glyphicon-chevron-right"></i> 用户管理</a></li>
						</shiro:hasRole>
						<li><a href="${ctx_path}/user/detail.htm"><i class="glyphicon glyphicon-chevron-right"></i> 帐户管理</a></li>
					</ul>
				</li>
			</ul>
	</div>
</div>
