<%@page import="io.github.yangziwen.logreplay.util.ProductUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="typeUser" value="io.github.yangziwen.logreplay.bean.User" />
<nav class="navbar navbar-default navbar-fixed-top top-bar" role="navigation">
	<div class="container-fluit">
		<div class="collapse navbar-collapse">
			<div class="col-sm-4"></div>
			<div class="col-sm-4">
				<ul class="nav navbar-left text-center" style="margin-top:6px; width: 100%;">
					<li class="title"><strong>客户端日志埋点校验系统</strong></li>
				</ul>
			</div>
			<shiro:user>
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown">
						<a id="J_productDropdown" href="###" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" role="button">
							<span><%=ProductUtil.getCurrentProduct().getName()%></span>
							<span class="caret"></span>
						</a>
						<% request.setAttribute("navBarProductList", ProductUtil.getProductList()); %>
						<ul id="J_productMenu" class="dropdown-menu" role="menu" aria-labelledby="J_productDropdown">
							<c:forEach var="product" items="${navBarProductList}">
								<li>
									<a role="menuitem" tabindex="-1" data-product-id="${product.id}" href="javascript:void(0);">${product.name}</a>
								</li>
							</c:forEach>
						</ul>
					</li>
					<%-- <li><a href="${ctx_path}/home.htm">主页</a></li> --%>
					<li>
						<a href="${ctx_path}/user/detail.htm#avatar" target="_self" style="padding: 0px;">
							<img src="${ctx_path}/image/avatar/<shiro:principal property="id" type="${typeUser}"/>?type=small&version=<shiro:principal property="imageVersion" type="${typeUser}"/>"
								style="width: 32px; height: 32px; margin-top: 10px;"/>
						</a>
					</li>
					<li><a id="J_openProfileModalBtn" href="javascript:void(0);"><shiro:principal/></a></li>
					<li><a href="${ctx_path}/logout.htm">退出</a></li>
					<li style="width: 10px;"></li>
				</ul>
			</shiro:user>
		</div>
	</div>
</nav>

<!-- 新增/修改pageInfo的弹出层 -->
<div class="modal" id="J_profileModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>修改帐户信息</strong></h4>
			</div>
			<div class="modal-body">
					<form class="form-horizontal" role="form">
						<div class="form-group">
							<label for="TB_screenName" class="col-sm-4 control-label">昵称：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="TB_screenName" name="screenName" placeholder="请输入昵称" />
							</div>
						</div>
					</form>
				</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="TB_updateProfileBtn" >更新</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->