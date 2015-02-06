<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
    <title>home</title>
	<%@ include file="../include/includeCss.jsp" %>
	<style>
		table.table {
			text-align: center;
		}
		table.table tr>th {
			text-align: center;
			background-color: #eee;
		}
	</style>
</head>
<body>

<%@ include file="../include/includeTopBar.jsp" %>

<div class="container" style="margin-bottom: 50px;">
	<div class="row"><!-- row1 -->
		<%@ include file="../include/includeMenuBar.jsp" %>
		<div class="col-lg-10 col-sm-10">
			<div>
			    <ul class="breadcrumb">
			        <li>
						当前位置: <a>日志项管理</a> > <a>页面管理</a>
			        </li> 
			    </ul>
			</div>
			<div class="row"><!-- row2 -->
			    <div class="col-md-12">
			     	<div id="J_queryArea" style="height: 100px; border: 1px solid #ccc; text-align: center;">
			     		<h2 style="line-height: 50px;">查询区占位</h2>
			     	</div>
			     	<hr>
			     	<div>
			     		<button class="btn btn-primary btn-lg-font" id="J_openCreateModalBtn">新增</button>
			     	</div>
			     	<div style="margin-top: 20px;">
				     	<table class="table table-bordered table-striped table-condensed table-hover ">
				     		<thead>
				     			<tr>
				     				<th style="width: 100px;">页面编号</th>
				     				<th>页面名称</th>
				     				<th style="width: 150px;">创建时间</th>
				     				<th style="width: 150px;">修改时间</th>
				     				<th style="width: 200px;">操作</th>
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
				     				<td>
				     					<button class="btn btn-primary btn-sm open-update-modal">修改</button>
				     					<button class="btn btn-primary btn-sm">标签</a>
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

<div class="modal" id="J_pageInfoModal" tabindex="-1">
    <div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title"><strong>标题</strong></h4>
			</div>
			<div class="modal-body">
					<form class="form-horizontal" role="form">
						<input type="hidden" id="J_id" name="id" />
						<div class="form-group">
							<label for="J_pageNo" class="col-sm-4 control-label">页面编号：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="J_pageNo" name="pageNo" placeholder="请输入页面编号" />
							</div>
						</div>
						<div class="form-group">
							<label for="J_name" class="col-sm-4 control-label">页面名称：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="J_name" name="name" placeholder="请输入页面名称" />
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
<%@ include file="../include/includeJs.jsp" %>
<script>
seajs.use('app/pageInfo/list', function(list) {
	list.init();
});
</script>
</body>
</html>

