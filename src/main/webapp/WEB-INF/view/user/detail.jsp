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
			     					<td rowspan="4" style="width: 128px; border-right: 1px solid #ddd; padding: 0px;">
			     						<img src="${ctx_path}/image/avatar?type=large" style="width:128px;" />
			     					</td>
			     				</tr>
			     				<tr>
			     					<td  style="border-top-width: 0px;"><strong>用户名</strong></td>
			     					<td  style="border-top-width: 0px;"><shiro:principal/></td>
			     				</tr>
			     				<tr>
			     					<td><strong>昵称</strong></td>
			     					<td><%=AuthUtil.getScreenName()%></td>
			     				</tr>
			     				<tr>
			     					<td><strong>角色</strong></td>
			     					<td><%=AuthUtil.getCurrentRoleObj().getDisplayName()%></td>
			     				</tr>
			     				<tr>
			     					<td colspan="3">
			     						<button class="btn btn-primary" id="J_openUpdatePasswordModalBtn">修改密码</button>
			     						&nbsp;
			     						<button class="btn btn-primary" id="J_openUploadAvatarModalBtn">上传头像</button>
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
							<input type="password" class="form-control" id="PW_oldPassword" name="oldPassword" placeholder="请输入原密码" />
						</div>
					</div>
					<div class="form-group">
						<label for="PW_newPassword" class="col-sm-4 control-label">新密码：</label>
						<div class="col-sm-8">
							<input type="password" class="form-control" id="PW_newPassword" name="newPassword" placeholder="请输入新密码" />
						</div>
					</div>
					<div class="form-group">
						<label for="PW_newPasswordAgain" class="col-sm-4 control-label">确认新密码：</label>
						<div class="col-sm-8">
							<input type="password" class="form-control" id="PW_newPasswordAgain" name="newPasswordAgain" placeholder="请再次输入新密码" />
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

<!-- 上传头像的弹出层 -->
<div class="modal" id="J_uploadAvatarModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>上传头像</strong></h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" role="form">
					<div class="form-group upload-group">
						<label class="col-sm-3 control-label">头像文件：</label>
						<div class="col-sm-9">
							<input id="J_avatarPath" type="text" class="form-control" disabled="disabled"/>
						</div>
					</div>
					<div class="form-group tailor-container"></div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary btn-browse" >浏览</button>
				<button type="button" class="btn btn-primary btn-upload" style="margin-left:5px;">上传</button>
				<button type="button" class="btn btn-primary btn-submit" style="margin-left:5px;">提交</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<%@ include file="../include/includeJs.jsp" %>
<script>
seajs.use('app/user/detail', function(detail) {
	detail.init();
});
</script>
</body>
</html>

