<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>用户管理</title>
	<%@ include file="../../include/includeCss.jsp" %>
</head>
<body>

<%@ include file="../../include/includeTopBar.jsp" %>

<div class="container" style="margin-bottom: 50px;">
	<div class="row"><!-- row1 -->
		<%@ include file="../../include/includeLeftMenu.jsp" %>
		<div class="col-sm-10">
			<div>
			    <ul class="breadcrumb">
			        <li>
						当前位置: <a data-toggle="collapse" href="#J_systemManageSubmenu">系统管理</a>
						 &gt; <a href="${ctx_path}/admin/user/list.htm">用户管理</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
			    <div class="col-sm-12">
			     	<div id="J_queryArea" style="text-align: center;">
			     		<form class="form-horizontal col-md-offset-1 col-md-10" role="form">
							<div class="form-group">
								<label for="J_username" class="col-sm-2 control-label">用户名：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_username" name="username" placeholder="请输入用户名" />
								</div>
								<label for="J_screenName" class="col-sm-2 control-label">昵称：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_screenName" name="screenName" placeholder="请输入昵称" />
								</div>
							</div>
							<div class="form-group">
								<label for="J_roleNames" class="col-sm-2 control-label">角色：</label>
								<div class="col-sm-4">
									<select id="J_roleNames" name="roleNames" class="form-control">
									</select>
								</div>
								<label for="J_enabled" class="col-sm-2 control-label">状态：</label>
								<div class="col-sm-4">
									<select id="J_enabled" name="enabled" class="form-control">
										<option value="">全部</option>
										<option value="true">正常</option>
										<option value="false">禁用</option>
									</select>
								</div>
							</div>
							<div class="form-group" style="margin-bottom: 0px;">
								<div class="col-sm-12" style="margin-top: 10px;">
									<button id="J_queryBtn" type="button" class="btn btn-primary btn-lg-font" style="width: 90px;">查&nbsp;&nbsp;询</button>
									<button id="J_clearBtn" type="button"  class="btn btn-primary btn-lg-font" style="width: 90px;">清除条件</button>
								</div>
							</div>
						</form>
			     	</div>
			     </div>
			     <div class="col-sm-12"><hr/></div>
			     <div class="col-sm-12">
			     	<div class="row">
			     		<div class="col-sm-2">
			     			<button class="btn btn-primary btn-lg-font" id="J_openCreateModalBtn">新增</button>
			     		</div>
			     		<div id="J_pagebar" class="col-sm-10">
			     			
			     		</div>
			     	</div>
			     	<div style="margin-top: 20px;">
				     	<table class="table table-bordered table-striped table-condensed table-hover ">
				     		<thead>
				     			<tr>
				     				<th style="width: 200px;">用户名</th>
				     				<th>昵称</th>
				     				<th style="width: 150px;">角色</th>
				     				<th style="width: 120px">状态</th>
				     				<th style="width: 150px;">管理</th>
				     			</tr>
				     		</thead>
				     		<tbody id="J_userTbody">
				     		</tbody>
							<script type="text/x-jquery-tmpl" id="J_userTmpl">
								<tr data-id="${'${'}id}">
				     				<td>${'${'}username}</td>
				     				<td>${'${'}screenName || '--'}</td>
									<td>${'${'}roles ? roles[0].name : '--'}</td>
									<td>${'${'}enabled === true? '正常': '禁用'}</td>
				     				<td>
				     					<button class="btn btn-primary btn-xs open-update-modal">修改</button>
				     					<button class="btn btn-primary btn-xs open-update-password-modal">重置密码</button>
				     				</td>
				     			</tr>
							</script>
				     	</table>
			     	</div>
			    </div>
			</div><!--/row2-->
		</div>
	</div><!-- /row1 -->
</div>

<!-- 新增/修改user的弹出层 -->
<div class="modal" id="J_userModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>标题</strong></h4>
			</div>
			<div class="modal-body">
					<form class="form-horizontal" role="form">
						<input type="hidden" id="U_id" name="id" />
						<div class="form-group">
							<label for="U_username" class="col-sm-4 control-label">用户名：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="U_username" name="username" placeholder="请输入用户名" />
							</div>
						</div>
						<div class="form-group">
							<label for="U_screenName" class="col-sm-4 control-label">昵称：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="U_screenName" name="screenName" placeholder="请输入昵称" />
							</div>
						</div>
						<div class="form-group">
							<label for="U_roleNames" class="col-sm-4 control-label">角色：</label>
							<div class="col-sm-8">
								<select id="U_roleNames" name="roleNames" class="form-control">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="U_enabled" class="col-sm-4 control-label">状态：</label>
							<div class="col-sm-8">
								<select id="U_enabled" name="enabled" class="form-control">
									<option value="true">正常</option>
									<option value="false">禁用</option>
								</select>
							</div>
						</div>
					</form>
				</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary create-user-info" id="J_createUserBtn" >创建</button>
				<button type="button" class="btn btn-primary update-user-info" id="J_updateUserBtn" >更新</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- 重置用户密码的弹出层 -->
<div class="modal" id="J_passwordModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>标题</strong></h4>
			</div>
			<div class="modal-body">
					<form class="form-horizontal" role="form">
						<input type="hidden" id="PW_id" name="id" />
						<div class="form-group">
							<label for="PW_password" class="col-sm-4 control-label">密码：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="PW_password" name="password" placeholder="请输入密码" />
							</div>
						</div>
					</form>
				</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary update-user-password" id="J_updatePasswordBtn" >重置</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<%@ include file="../../include/includeJs.jsp" %>
<script>
seajs.use('app/admin/user/list', function(list) {
	list.init();
});
</script>
</body>
</html>

