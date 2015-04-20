<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>页面信息管理</title>
	<%@ include file="../include/includeCss.jsp" %>
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
						当前位置: <a data-toggle="collapse" href="#J_logConfigSubmenu">日志项管理</a>
						 &gt; <a href="${ctx_path}/pageInfo/list.htm">页面信息管理</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
			    <div class="col-sm-12">
			     	<div id="J_queryArea" style="text-align: center;">
			     		<form class="form-horizontal col-md-offset-1 col-md-10" role="form">
							<div class="form-group">
								<label for="J_pageNo" class="col-sm-2 control-label">页面编号：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_pageNo" name="pageNo" placeholder="请输入页面编号" />
								</div>
								<label for="J_pageName" class="col-sm-2 control-label">页面名称：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_pageName" name="pageName" placeholder="请输入页面名称" />
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
			     		<div class="col-sm-3">
			     			<shiro:hasRole name="admin">
			     				<button class="btn btn-primary btn-lg-font" id="J_openCreateModalBtn" title="新增页面信息">新增</button>
			     			</shiro:hasRole>
		     				<button class="btn btn-primary btn-lg-font" id="J_exportPageInfoBtn" title="导出excel">导出</button>
			     		</div>
			     		<div id="J_pagebar" class="col-sm-9">
			     			
			     		</div>
			     	</div>
			     	<div style="margin-top: 20px;">
				     	<table class="table table-bordered table-striped table-condensed table-hover ">
				     		<thead>
				     			<tr>
				     				<th style="width: 100px;">页面编号</th>
				     				<th>页面名称</th>
				     				<th style="width: 150px;">创建时间</th>
				     				<th style="width: 150px;">修改时间</th>
				     				<shiro:hasRole name="admin">
				     				<th style="width: 200px;">管理</th>
				     				</shiro:hasRole>
				     			</tr>
				     		</thead>
				     		<tbody id="J_pageInfoTbody">
				     		</tbody>
							<script type="text/x-jquery-tmpl" id="J_pageInfoTmpl">
								<tr data-id="${'${'}id}">
				     				<td>${'${'}pageNo}</td>
				     				<td>${'${'}name}</td>
				     				<td>${'${'}createTime? new Date(createTime).format('yyyy-MM-dd hh:mm:ss'): '--'}</td>
				     				<td>${'${'}updateTime? new Date(updateTime).format('yyyy-MM-dd hh:mm:ss'): '--'}</td>
									<shiro:hasRole name="admin">
				     				<td>
				     					<button class="btn btn-primary btn-xs open-update-modal">修改</button>
				     					<button class="btn btn-primary btn-xs open-create-tag-modal">操作项</button>
				     				</td>
									</shiro:hasRole>
				     			</tr>
							</script>
				     	</table>
			     	</div>
			    </div>
			</div><!--/row2-->
		</div>
	</div><!-- /row1 -->
</div>

<!-- 新增/修改pageInfo的弹出层 -->
<div class="modal" id="J_pageInfoModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>标题</strong></h4>
			</div>
			<div class="modal-body">
					<form class="form-horizontal" role="form">
						<input type="hidden" id="P_id" name="id" />
						<div class="form-group">
							<label for="P_pageNo" class="col-sm-4 control-label">页面编号：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="P_pageNo" name="pageNo" placeholder="请输入页面编号" />
							</div>
						</div>
						<div class="form-group">
							<label for="P_name" class="col-sm-4 control-label">页面名称：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="P_name" name="name" placeholder="请输入页面名称" />
							</div>
						</div>
					</form>
				</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary create-page-info" id="J_createPageInfoBtn" >创建</button>
				<button type="button" class="btn btn-primary update-page-info" id="J_updatePageInfoBtn" >更新</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- 新增tagInfo的弹出层 -->
<div class="modal" id="J_tagInfoModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>标题</strong></h4>
			</div>
			<div class="modal-body">
					<form class="form-horizontal" role="form">
						<input type="hidden" id="T_id" name="id" />
						<input type="hidden" id="T_pageInfoId" name="pageInfoId" />
						<div class="form-group">
							<label for="T_pageNo" class="col-sm-4 control-label">页面编号：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="T_pageNo" name="pageNo" placeholder="请输入页面编号" />
							</div>
						</div>
						<div class="form-group">
							<label for="T_pageName" class="col-sm-4 control-label">页面名称：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="T_pageName" name="pageName" placeholder="请输入页面名称" />
							</div>
						</div>
						<div class="form-group">
							<label for="T_tagNo" class="col-sm-4 control-label">操作编号：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="T_tagNo" name="tagNo" placeholder="请输入操作编号" />
							</div>
						</div>
						<div class="form-group">
							<label for="T_name" class="col-sm-4 control-label">操作名称：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="T_name" name="name" placeholder="请输入操作名称" />
							</div>
						</div>
						<div class="form-group">
							<label for="T_actionId" class="col-sm-4 control-label">操作动作：</label>
							<div class="col-sm-8">
								<select id="T_actionId" name="actionId" class="form-control">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="T_targetId" class="col-sm-4 control-label">操作目标：</label>
							<div class="col-sm-8">
								<select id="T_targetId" name="targetId" class="form-control">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="T_originVersion" class="col-sm-4 control-label">初始版本：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="T_originVersion" name="originVersion" placeholder="请输入初始版本" />
							</div>
						</div>
						<div class="form-group hide">
							<label for="T_comment" class="col-sm-4 control-label">备注：</label>
							<div class="col-sm-8">
								<textarea id="T_comment" name="comment" class="form-control" rows="5" placeholder="请输入备注，100字以内"></textarea>
							</div>
						</div>
					</form>
				</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary create-tag-info" id="J_createTagInfoBtn" >创建</button>
				<button type="button" class="btn btn-primary update-tag-info" id="J_updateTagInfoBtn" >更新</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="hide">
	<iframe name="exportFrame"></iframe>
</div>

<%@ include file="../include/includeJs.jsp" %>
<script>
seajs.use('app/pageInfo/list', function(list) {
	list.init();
});
</script>
</body>
</html>

