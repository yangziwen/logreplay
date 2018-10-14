<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>角色管理</title>
	<%@ include file="../include/includeCss.jsp" %>
	<style>
		.panel-heading {
			text-align: center;
		}
		.panel-heading > .title {
			font-size: 16px;
			font-family: 'Microsoft Yahei'
		}
		.panel tbody td {
			border-bottom: 1px solid #ddd;
			border-top-width: 0px !important;
			cursor: pointer;
		}
		.panel .table {
			margin-bottom: -1px !important;
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
						当前位置: <a data-toggle="collapse" href="#J_logConfigSubmenu">埋点信息管理</a>
						 &gt; <a href="${ctx_path}/role/list.htm">角色管理</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
			     <div class="col-sm-12">
			     	<div style="margin-top: 20px;">
				     	<table class="table table-bordered table-striped table-condensed table-hover ">
				     		<thead>
				     			<tr>
				     				<th style="width: 150px;">角色</th>
				     				<th style="width: 150px;">名称</th>
				     				<th>描述</th>
				     				<th style="width: 150px;">管理</th>
				     			</tr>
				     		</thead>
				     		<tbody id="J_roleTbody">
				     		</tbody>
							<script type="text/x-jquery-tmpl" id="J_roleTmpl">
								<tr data-id="${'${'}id}">
				     				<td>${'${'}name}</td>
				     				<td>${'${'}displayName}</td>
				     				<td>${'${'}comment}</td>
				     				<td>
				     					<button class="btn btn-primary btn-xs open-relate-permission-modal">关联权限</button>
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

<div class="modal" id="J_relatePermissionModal" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title"><strong>标题</strong></h4>
			</div>
			<div class="modal-body" style="padding-left: 40px; padding-right: 40px;">
				<div class="row">
					<div class="col-sm-5">
						<div class="panel panel-default">
							<div class="panel-heading"><strong class="title">已关联权限</strong></div>
							<div style="overflow-y: scroll; height: 250px;"style="overflow-y: scroll; height: 250px;">
							<table class="table table-condensed table-hover" style="margin-bottom: 0px;" >
								<tbody class="left-tbody">
								</tbody>
							</table>
							</div>
						</div>
					</div>
					<div class="col-sm-2" style="text-align: center; padding-top: 100px; height: 250px;">
						<button class="btn btn-primary left-to-right-btn" style="margin-top: 10px;">
							<span class="glyphicon glyphicon-chevron-right"></span>
						</button>
						<button class="btn btn-primary right-to-left-btn" style="margin-top: 10px;">
							<span class="glyphicon glyphicon-chevron-left"></span>
						</button>
					</div>
					<div class="col-sm-5">
						<div class="panel panel-default">
							<div class="panel-heading"><strong class="title">未关联权限</strong></div>
							<div style="overflow-y: scroll; height: 250px;"style="overflow-y: scroll; height: 250px;">
							<table class="table table-condensed table-hover" style="margin-bottom: 0px;" >
								<tbody class="right-tbody">
								</tbody>
							</table>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary update-btn">更新</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div>
	</div>
</div>

<%@ include file="../include/includeJs.jsp" %>
<script>
seajs.use('app/role/list', function(list) {
	list.init();
});
</script>
</body>
</html>

