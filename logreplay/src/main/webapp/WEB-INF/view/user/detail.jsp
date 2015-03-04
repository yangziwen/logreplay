<%@page import="com.sogou.map.logreplay.util.AuthUtil"%>
<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>帐户管理</title>
	<%@ include file="../include/includeCss.jsp" %>
	<style>
		.panel-heading {
			text-align: center;
		}
		.panel-heading > .title {
			font-size: 18px;
			font-family: 'Microsoft Yahei'
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
						当前位置: <a data-toggle="collapse" href="#J_systemManageSubmenu">系统管理</a>
						 &gt; <a href="${ctx_path}/user/detail.htm">帐户管理</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
			    <div class="col-sm-6 col-sm-offset-3">
			     	<div class="panel panel-default">
			     		<div class="panel-heading"><strong class="title">帐户信息</strong></div>
			     		<table class="table table-bordered">
			     			<tbody>
			     				<tr>
			     					<td><strong>用户名</strong></td>
			     					<td><shiro:principal/></td>
			     				</tr>
			     				<tr>
			     					<td><strong>昵称</strong></td>
			     					<td><%=AuthUtil.getScreenName()%></td>
			     				</tr>
			     				<tr>
			     					<td><strong>角色</strong></td>
			     					<td><%=AuthUtil.getRoles()%></td>
			     				</tr>
			     				<tr>
			     					<td colspan="2">
			     						<button class="btn btn-primary">修改密码</button>
			     					</td>
			     				</tr>
			     			</tbody>
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

<!-- 修改密码的弹出层 -->
<div class="modal" id="J_passwordModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>标题</strong></h4>
			</div>
			<div class="modal-body">
					<form class="form-horizontal" role="form">
						<div class="form-group">
							<label for="PW_oldPassword" class="col-sm-4 control-label">原密码：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="PW_oldPassword" name="oldPassword" placeholder="请输入原密码" />
							</div>
							<label for="PW_newPassword" class="col-sm-4 control-label">新密码：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="PW_newPassword" name="newPassword" placeholder="请输入新密码" />
							</div>
							<label for="PW_newPasswordAgain" class="col-sm-4 control-label">确认新密码：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="PW_newPasswordAgain" name="newPasswordAgain" placeholder="请再次输入新密码" />
							</div>
						</div>
					</form>
				</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary update-user-password" id="J_updatePasswordBtn" >提交</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<%@ include file="../include/includeJs.jsp" %>
<script>
/* seajs.use('app/admin/user/list', function(list) {
	list.init();
}); */
</script>
</body>
</html>

