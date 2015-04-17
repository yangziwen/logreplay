<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>公用操作项管理</title>
	<%@ include file="../include/includeCss.jsp" %>
	<style>
		table.inner-table {
			width: 100%;
		}
		table.inner-table tr > th, table.inner-table tr > td {
			text-align: center;
		}
		table.inner-table td {
			padding: 5px;
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
						当前位置: <a data-toggle="collapse" href="#J_logConfigSubmenu">日志项管理</a> 
						&gt; <a href="${ctx_path}/tagInfo/list.htm">公用操作项管理</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
			    <div class="col-md-12">
			     	<div id="J_queryArea" style="text-align: center;">
			     		<form class="form-horizontal col-md-offset-1 col-md-10" role="form">
							<div class="form-group">
								<label for="J_tagNo" class="col-sm-2 control-label">操作编号：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_tagNo" name="tagNo" placeholder="请输入操作编号" />
								</div>
								<label for="J_tagName" class="col-sm-2 control-label">操作名称：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_tagName" name="tagName" placeholder="请输入操作名称" />
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
			     <div class="col-sm-12"><hr /></div>
			     <div class="col-sm-12">
			     	<div class="row">
			     		<div class="col-sm-2">
			     			<button class="btn btn-primary btn-lg-font" id="J_openCreateTagModalBtn">新增</button>
			     		</div>
			     		<div id="J_pagebar" class="col-sm-10"></div>
			     	</div>
			     	<div style="margin-top: 20px;">
				     	<table class="table table-bordered table-striped table-condensed table-hover ">
				     		<thead>
				     			<tr>
				     				<th style="width: 100px;">操作编号</th>
				     				<th>操作名称</th>
				     				<th style="width: 100px;">操作动作</th>
				     				<th style="width: 100px;">操作目标</th>
				     				<th style="width: 100px;">初始版本</th>
				     				<shiro:hasRole name="admin">
				     				<th style="width: 100px;">管理</th>
				     				</shiro:hasRole>
				     			</tr>
				     		</thead>
				     		<tbody id="J_tagInfoTbody">
				     		</tbody>
							<script type="text/x-jquery-tmpl" id="J_tagInfoTmpl">
								<tr data-id="${'${'}id}" title="${'${'}comment}">
				     				<td>${'${'}tagNo}</td>
				     				<td>${'${'}name}</td>
				     				<td>${'${'}$item.getActionName(actionId)}</td>
				     				<td>${'${'}$item.getTargetName(targetId)}</td>
									<td>${'${'}$item.displayOriginVersion(originVersion)}</td>
									<shiro:hasRole name="admin">
				     				<td>
				     					<button class="btn btn-primary btn-xs open-update-tag-modal">修改</button>
				     					<button class="btn btn-xs open-update-tag-param-modal ${'${'}$item.getParamBtnClass(hasParams)}">参数</button>
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

<!-- 修改tagInfo的弹出层 -->
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

<!-- 修改tagParam的弹出层 -->
<div class="modal" id="J_tagParamModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>更新参数</strong></h4>
			</div>
			<div class="modal-body">
					<form class="form-horizontal" role="form">
						<!-- <input type="hidden" id="TP_id" name="id" /> -->
						<input type="hidden" id="TP_tagInfoId" name="tagInfoId" />
						<div class="form-group">
							<label for="TP_tagNo" class="col-sm-2 control-label">操作编号：</label>
							<div class="col-sm-4">
								<input type="text" class="form-control" id="TP_tagNo" name="tagNo" placeholder="请输入操作编号" />
							</div>
							<label for="TP_tagName" class="col-sm-2 control-label">操作名称：</label>
							<div class="col-sm-4">
								<input type="text" class="form-control" id="TP_tagName" name="tagName" placeholder="请输入操作名称" />
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">参数设置：</label>
							<div class="col-sm-10">
								<table class="inner-table">
									<thead>
										<tr>
											<th width="20%">参数名</th>
											<th width="20%">参数值</th>
											<th width="50%">描述</th>
											<th width="10%" style="line-height: 0px;">
												<a id="TP_addNewTagParam" href="###" style="font-size: 20px;" title="添加参数">
													<i class="glyphicon glyphicon-plus-sign"></i>
												</a>
											</th>
										</tr>
									</thead>
									<tbody id="TP_paramInfoTbody">
									</tbody>
									<script type="text/x-jquery-tmpl" id="TP_paramInfoTmpl">
										<tr data-param-info-id="${'${'}id}">
				     						<td>
												<select class="form-control input-sm param-info-name">
													{{html $item.renderParamNameOptions(name) }}
												</select>
											</td>
				     						<td><input type="text" class="input-sm form-control param-info-value" value="${'${'}value}" /></td>
				     						<td><input type="text" class="input-sm form-control param-info-description" value="${'${'}description}" /></td>
				     						<td style="line-height: 0px;">
												<a tabindex="-1" href="###" style="font-size:20px;" title="删除参数" class="remove-param-info-btn">
													<i class="glyphicon glyphicon-minus-sign"></i>
												</a>
											</td>
				     					</tr>
									</script>
								</table>
							</div>
						</div>
						<div class="form-group">
							<label for="TP_comment" class="col-sm-2 control-label">备注：</label>
							<div class="col-sm-10">
								<textarea id="TP_comment" name="comment" class="form-control" rows="5" placeholder="请输入备注，100字以内"></textarea>
							</div>
						</div>
					</form>
				</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary update-tag-param" id="J_updateTagParamBtn" >更新</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<%@ include file="../include/includeJs.jsp" %>
<script>
seajs.use('app/tagInfo/commonList', function(list) {
	list.init();
});
</script>
</body>
</html>

