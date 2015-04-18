<%@page import="com.sogou.map.logreplay.util.AuthUtil"%>
<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>校验状态查询</title>
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

<input type="hidden" id="J_currentRole" value="<%=AuthUtil.getCurrentRoleObj().getName()%>" />

<div class="container" style="margin-bottom: 50px;">
	<div class="row"><!-- row1 -->
		<%@ include file="../include/includeLeftMenu.jsp" %>
		<div class="col-sm-10">
			<div>
			    <ul class="breadcrumb">
			        <li>
						当前位置: <a data-toggle="collapse" href="#J_logReplaySubmenu">日志项校验</a> 
						&gt; <a href="${ctx_path}/inspectionRecord/inspectStatusList.htm">校验状态查询</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
			    <div class="col-md-12">
			     	<div id="J_queryArea" style="text-align: center;">
			     		<form class="form-horizontal col-md-offset-1 col-md-10" role="form">
							<div class="form-group">
								<label for="J_pageNo" class="col-sm-2 control-label">页面编号：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_pageNo" name="pageNo" placeholder="请输入页面编号" />
								</div>
								<label for="J_tagNo" class="col-sm-2 control-label">操作编号：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_tagNo" name="tagNo" placeholder="请输入操作编号" />
								</div>
							</div>
							<div class="form-group">
								<label for="J_pageName" class="col-sm-2 control-label">页面名称：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_pageName" name="pageName" placeholder="请输入页面名称" />
								</div>
								<label for="J_tagName" class="col-sm-2 control-label">操作名称：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_tagName" name="tagName" placeholder="请输入操作名称" />
								</div>
							</div>
							<div class="form-group">
								<label for="J_inspectMode" class="col-sm-2 control-label">校验模式：</label>
								<div class="col-sm-4">
									<select id="J_inspectMode" name="inspectMode" class="form-control">
										<option value="dev" <shiro:hasRole name="dev">selected</shiro:hasRole>>开发模式</option>
										<option value="test" <shiro:lacksRole name="dev">selected</shiro:lacksRole>>测试模式</option>
									</select>
								</div>
								<label for="J_inspectStatus" class="col-sm-2 control-label">校验状态：</label>
								<div class="col-sm-4">
									<select id="J_inspectStatus" name="inspectStatus" class="form-control">
										<option value="">全部</option>
										<option value="0">未校验</option>
										<option value="1">校验正确</option>
										<option value="2">校验错误</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="J_originVersionSince" class="col-sm-2 control-label">起始版本：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_originVersionSince" name="originVersionSince" placeholder="请输入起始版本" />
								</div>
								<label for="J_originVersionUntil" class="col-sm-2 control-label">终止版本：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_originVersionUntil" name="originVersionUntil" placeholder="请输入终止版本" />
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
			     		<div id="J_pagebar" class="col-sm-12"></div>
			     	</div>
			     	<div style="margin-top: 20px;">
				     	<table class="table table-bordered table-striped table-condensed table-hover ">
				     		<thead>
				     			<tr>
				     				<th style="width: 100px;">页面编号</th>
				     				<th>页面名称</th>
				     				<th style="width: 100px;">操作编号</th>
				     				<th>操作名称</th>
				     				<th style="width: 100px;">操作动作</th>
				     				<th style="width: 100px;">操作目标</th>
				     				<th style="width: 100px;">初始版本</th>
				     				<th style="width: 100px;">校验状态</th>
				     			</tr>
				     		</thead>
				     		<tbody id="J_tagInfoTbody">
				     		</tbody>
							<script type="text/x-jquery-tmpl" id="J_tagInfoTmpl">
								<tr data-id="${'${'}id}" title="${'${'}comment}">
				     				<td>${'${'}pageInfo.pageNo || '--'}</td>
				     				<td>${'${'}pageInfo.name || '--'}</td>
				     				<td>${'${'}tagNo}</td>
				     				<td>${'${'}name}</td>
				     				<td>${'${'}$item.getActionName(actionId)}</td>
				     				<td>${'${'}$item.getTargetName(targetId)}</td>
									<td>${'${'}$item.displayOriginVersion(originVersion)}</td>
				     				<td>{{html $item.displayInspectStatus($data)}}</td>
				     			</tr>
							</script>
				     	</table>
			     	</div>
			    </div>
			</div><!--/row2-->
		</div>
	</div><!-- /row1 -->
</div>
<%@ include file="../include/includeJs.jsp" %>
<script>
seajs.use('app/inspectionRecord/inspectStatusList', function(list) {
	list.init();
});
</script>
</body>
</html>

