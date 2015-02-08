define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	require('bootstrap.pagebar');
	var $ = require('jquery'),
		common = require('app/common');
	
	var start = 0, limit = 30;	// 翻页信息
	
	function refreshTagActionOptions() {
		var url = CTX_PATH + '/tagAction/list';
		$.get(url, function(data) {
			if(!data || !data.response) {
				return;
			}
			$('#T_actionId').empty().append($.map(data.response, function(action) {
				return $('<option value="' + action.id + '">' + action.name + '</option>');
			}));
		});
	}
	
	function refreshTagTargetOptions() {
		var url = CTX_PATH + '/tagTarget/list';
		$.get(url, function(data) {
			if(!data || !data.response) {
				return;
			}
			$('#T_targetId').empty().append($.map(data.response, function(target) {
				return $('<option value="' + target.id + '">' + target.name + '</option>');
			}));
		});
	}
	
	function loadPageInfoResult(callback) {
		var params = {start: start, limit: limit};
		var url = CTX_PATH + '/pageInfo/list';
		$.get(url, params, function(data) {
			if(!data || !data.response || !data.response.list) {
				alert('failed!');
				return;
			}
			$.isFunction(callback) && callback(data);
		});
	}
	
	function renderPageInfoTbody(list) {
		$('#J_pageInfoTbody').empty().append($('#J_pageInfoTmpl').tmpl(list));
		initOpenUpdateModalBtn();
		initOpenCreateTagModalBtn();
	}
	
	function refreshPageInfoTbl() {
		loadPageInfoResult(function(data) {
			var result = data.response;
			common.buildPageBar('#J_pagebar', result.start, result.limit, result.count, function(i, pageNum) {
				start = (pageNum - 1) * limit;
				refreshPageInfoTbl();
			});
			renderPageInfoTbody(result.list);
		});
	}
	
	/** 新增pageInfo开始 **/
	function initOpenCreateModalBtn() {
		$("#J_openCreateModalBtn").on('click', function() {
			var $modal = $('#J_pageInfoModal');
			common.clearForm($modal.find('form'));
			$modal.find('.modal-title > strong').html('新增页面信息');
			$modal.find('.modal-dialog').css({
				width: 400,
				'margin-top': function() {
					return ( $(window).height() - $(this).height() ) / 3;
				}
			});
			$modal.find('button.create-page-info').show();
			$modal.find('button.update-page-info').hide();
			$modal.modal({
				backdrop: 'static'
			});
		});
	}
	
	function initCreatePageInfoBtn() {
		$('#J_createPageInfoBtn').on('click', function() {
			var params = {
				pageNo: $('#P_pageNo').val(),
				name: $('#P_name').val()
			};
			doCreatePageInfo(params);
		});
	}
	
	function doCreatePageInfo(params) {
		$.ajax({
			url: CTX_PATH + '/pageInfo/create',
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if(data.code !== 0) {
					common.alertMsg('创建失败!');
					return;
				} else {
					common.alertMsg('创建成功!').done(function() {
						$('#J_pageInfoModal').modal('hide');
					});
					refreshPageInfoTbl();
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	/** 新增pageInfo结束 **/
	
	/** 修改pageInfo开始 **/
	function initOpenUpdateModalBtn() {
		$('#J_pageInfoTbody button.open-update-modal').on('click', function() {
			var $this = $(this);
			var $tr = $this.parents('tr').eq(0),
				$tds = $tr.children();
			var id = $tr.data('id'),
				pageNo = $tds.eq(0).html(),
				name = $tds.eq(1).html();
			var $modal = $('#J_pageInfoModal');
			$modal.find('.modal-title > strong').html('修改页面信息');
			$modal.find('input[name=id]').val(id);
			$modal.find('input[name=pageNo]').val(pageNo);
			$modal.find('input[name=name]').val(name);
			$modal.find('.modal-dialog').css({
				width: 400,
				'margin-top': function() {
					return ( $(window).height() - $(this).height() ) / 3;
				}
			});
			$modal.find('button.create-page-info').hide();
			$modal.find('button.update-page-info').show();
			$modal.modal({
				backdrop: 'static'
			});
		});
	}
	
	function initUpdatePageInfoBtn() {
		$('#J_updatePageInfoBtn').on('click', function() {
			var params = {
				id: $('#P_id').val(),
				pageNo: $('#P_pageNo').val(),
				name: $('#P_name').val()
			};
			doUpdatePageInfo(params);
		});
	}
	
	function doUpdatePageInfo(params) {
		$.ajax({
			url: CTX_PATH + '/pageInfo/update/' + params['id'],
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if(data.code !== 0) {
					common.alertMsg('更新失败!');
					return;
				} else {
					common.alertMsg('更新成功!').done(function() {
						$('#J_pageInfoModal').modal('hide');
					});
					refreshPageInfoTbl();
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	/** 修改pageInfo结束 **/
	
	/** 新增tagInfo开始 **/
	function initOpenCreateTagModalBtn() {
		$("#J_pageInfoTbody .open-create-tag-modal").on('click', function() {
			var $tr = $(this).parents('tr').eq(0);
			var $tds = $tr.children();
			var pageInfoId = $tr.data('id'),
				pageNo = $tds.eq(0).html(),
				pageName = $tds.eq(1).html();
			var $modal = $('#J_tagInfoModal');
			common.clearForm($modal.find('form'));
			$modal.find('.modal-title > strong').html('新增操作信息');
			$modal.find('input[name=pageInfoId]').val(pageInfoId);
			$modal.find('input[name=pageNo]').val(pageNo).attr({disabled: true});
			$modal.find('input[name=pageName]').val(pageName).attr({disabled: true});
			$modal.find('.modal-dialog').css({
				width: 400
			});
			$modal.find('button.create-tag-info').show();
			$modal.find('button.update-tag-info').hide();
			$modal.modal({
				backdrop: 'static'
			});
		});
	}
	function initCreateTagInfoBtn() {
		$('#J_createTagInfoBtn').on('click', function() {
			var params = {
				pageNo: $('#T_pageNo').val(),
				pageInfoId: $('#T_pageInfoId').val(),
				tagNo: $('#T_tagNo').val(),
				name: $('#T_name').val(),
				actionId: $('#T_actionId').val(),
				targetId: $('#T_targetId').val(),
				comment: $('#T_comment').val()
			};
			doCreateTagInfo(params);
		});
	}
	
	function doCreateTagInfo(params) {
		$.ajax({
			url: CTX_PATH + '/tagInfo/create',
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if(data.code !== 0) {
					common.alertMsg('创建失败!');
					return;
				} else {
					common.alertMsg('创建成功!').done(function() {
						$('#J_tagInfoModal').modal('hide');
					});
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	/** 新增tagInfo结束 **/
	
	function init() {
		refreshTagActionOptions();
		refreshTagTargetOptions();
		refreshPageInfoTbl();
		initOpenCreateModalBtn();
		initCreatePageInfoBtn();
		initUpdatePageInfoBtn();
		initCreateTagInfoBtn();
	}
	
	module.exports = {init: init};
	
});