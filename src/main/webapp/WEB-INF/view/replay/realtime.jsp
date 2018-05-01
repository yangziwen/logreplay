<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>实时校验</title>
	<%@ include file="../include/includeCss.jsp" %>
	<style>
		#J_replayArea {
			margin-top: 10px; height: 800px; overflow-y: auto; border-top: 1px solid #eee; padding: 0px;
		}
		#J_replayTbl td {
			white-space: normal;
			word-break: break-all;
		}
		.table > tbody > tr.danger > td {
			background-color: #e2b3b3;
		}
		.table > tbody > tr.danger:hover > td {
			background-color: #dca2a2;
		}
		.table > tbody > tr.warning > td {
			background-color: #f8edb8;
		}
		.table > tbody > tr.warning:hover > td {
			background-color: #f6e7a2;
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
						当前位置: <a data-toggle="collapse" href="#J_logReplaySubmenu">埋点信息校验</a>
						 &gt; <a href="${ctx_path}/replay/realtime.htm">实时校验</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
			    <div class="col-sm-12">
			     	<div id="J_queryArea" class="row" style="text-align: center;">
			     		<input type="hidden" name="limit" value="10"/>
			     		<form class="form-horizontal col-md-offset-1 col-md-10" role="form">
							<div class="form-group">
								<label for="J_deviceId" class="col-sm-2 control-label">设备id：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_deviceId" name="deviceId" placeholder="请输入设备id" />
								</div>
								<label for="J_uvid" class="col-sm-2 control-label">用户id：</label>
								<div class="col-sm-4">
									<input type="text" class="form-control" id="J_uvid" name="uvid" placeholder="请输入用户id" />
								</div>
							</div>
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
									<button id="J_replaySwitchBtn" type="button" class="btn btn-primary btn-lg-font">开始校验</button>
									<button id="J_lockScrollBtn" type="button"  class="btn btn-primary btn-lg-font" style="width: 90px;">锁定滚动</button>
									<button id="J_clearBtn" type="button"  class="btn btn-primary btn-lg-font" style="width: 90px;">清&nbsp;&nbsp;除</button>
								</div>
							</div>
						</form>
					</div>
				</div>
				<div class="col-sm-12" style="overflow-x: hidden">
					<hr>
				</div>
				<div class="col-sm-12">
			     	<div id="J_replayArea">
				     	<table id="J_replayTbl" class="table table-bordered table-striped table-condensed table-hover" >
				     		<thead>
				     			<tr>
				     				<th style="width: 100px;">日志编号</th>
				     				<th>日志描述</th>
				     				<th style="width: 220px;">日志内容</th>
				     				<th style="width:85px">自测结果</th>
				     				<th style="width:85px">测试结果</th>
				     				<shiro:hasPermission name="operation_record:modify">
				     					<th style="width: 90px;">管理</th>
				     				</shiro:hasPermission>
				     			</tr>
				     		</thead>
				     		<tbody id="J_replayTbody" style="max-height: 800px;" >
				     		</tbody>
				     		<script type="text/x-jquery-tmpl" id="J_replayTmpl">
								<tr class="${'${'}$item.bgClass($data)}" data-id="${'${'}id}" 
										data-page-no="${'${'}pageNo}" data-tag-no="${'${'}tagNo}"
										data-page-name="${'${'}pageName}" data-tag-name="${'${'}tagName}">
				     				<td style="text-align: left">&nbsp;p[${'${'}pageNo}] t[${'${'}tagNo}]</td>
				     				<td>{{html $item.describe($data) }}</td>
				     				<td title="${'${'}params}" style="max-width: 250px; overflow-x: hidden; text-align: left;">
										${'${'}params}
									</td>
									<td>{{html $item.displayInspectStatus(devInspectStatus)}}</td>
									<td>{{html $item.displayInspectStatus(inspectStatus)}}</td>
									<shiro:hasPermission name="operation_record:modify">
				     				<td>
										<button class="btn btn-success btn-xs submit-success-btn"><span class="glyphicon glyphicon-ok"></span></button>
										&nbsp;
										<button class="btn btn-danger btn-xs submit-error-btn"><span class="glyphicon glyphicon-remove"></span></button>
									</td>
									</shiro:hasPermission>
				     			</tr>
							</script>
				     	</table>
			     	</div>
			    </div>
			</div><!--/row2-->
		</div>
	</div><!-- /row1 -->
</div>

<!-- 提交错误信息的弹出层 -->
<div class="modal" id="J_submitErrorModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>提交校验错误的结果</strong></h4>
			</div>
			<div class="modal-body">
				<div class="panel panel-default">
					<form>
					<table class="table table-bordered">
						<tbody>
						</tbody>
						<tr>
							<td style="width: 150px;"><strong>页面编号:</strong></td>
							<td style="width: 250px;">
								<div class="form-group">
									<input type="text" class="form-control" id="S_pageNo" name="pageNo"/>
								</div>
							</td>
							<td style="width: 150px;"><strong>页面名称:</strong></td>
							<td style="width: 250px;" id="S_pageName"></td>
						</tr>
						<tr>
							<td><strong>操作编号:</strong></td>
							<td>
								<div class="form-group">
									<input type="text" class="form-control" id="S_tagNo" name="tagNo"/> 
								</div>
							</td>
							<td><strong>操作名称:</strong></td>
							<td id="S_tagName"></td>
						</tr>
						<tr>
							<td><strong>备注:</strong></td>
							<td colspan="3">
							<textarea class="form-control" id="S_comment" style="height: 150px;"></textarea>
							</td>
						</tr>
					</table>
					</form>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="J_submitErrorBtn">提交</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<%@ include file="../include/includeJs.jsp" %>

<!-- It's hard to import sockjs via sea.js due to the complex dependencies -->
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script>
seajs.use('app/replay/realtime', function(realtime) {
	realtime.init();
});
</script>
</body>
</html>

