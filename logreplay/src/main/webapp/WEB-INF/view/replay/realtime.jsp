<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>页面信息管理</title>
	<%@ include file="../include/includeCss.jsp" %>
	<style>
		#J_replayArea {
			margin-top: 10px; height: 500px; overflow-y: auto; border-top: 1px solid #eee; padding: 0px;
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
						当前位置: <a data-toggle="collapse" href="#J_logReplaySubmenu">日志项校验</a>
						 &gt; <a href="${ctx_path}/replay/realtime.htm">即时校验</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
			    <div class="col-sm-12">
			     	<div id="J_queryArea" class="row" style="text-align: center;">
			     		<input type="hidden" name="limit" value="3"/>
			     		<form class="form-horizontal col-md-offset-2 col-md-8" role="form">
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
							<div class="form-group" style="margin-bottom: 0px;">
								<div class="col-sm-6 col-sm-offset-3" style="margin-top: 10px;">
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
				     				<th style="width: 125px;">平台</th>
				     				<th style="width: 100px;">日志编号</th>
				     				<th>日志描述</th>
				     				<th style="width: 250px;">日志内容</th>
				     				<th style="width: 125px;">操作</th>
				     			</tr>
				     		</thead>
				     		<tbody id="J_replayTbody" style="max-height: 500px;" >
				     		</tbody>
				     		<script type="text/x-jquery-tmpl" id="J_replayTmpl">
								<tr class="${'${'}$item.bgClass()}">
				     				<td>${'${'}os}</td>
				     				<td style="text-align: left">&nbsp;p[${'${'}pageNo}] t[${'${'}tagNo}]</td>
				     				<td>${'${'}$item.describe($data)}</td>
				     				<td title="${'${'}params}" style="max-width: 250px; overflow-x: hidden; text-align: left;">
										${'${'}params}
									</td>
				     				<td></td>
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
seajs.use('app/replay/realtime', function(realtime) {
	realtime.init();
});
</script>
</body>
</html>

