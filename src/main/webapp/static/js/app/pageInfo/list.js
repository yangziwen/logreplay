define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	require('bootstrap.pagebar');
	var $ = require('jquery'),
		common = require('app/common');
	var pageInfoValidator = require('app/pageInfo/validator').validate($('#J_pageInfoModal form')),
		tagInfoValidator = require('app/tagInfo/validator').validate('#J_tagInfoModal form');
	
	$('#J_pageInfoModal').on('hide.bs.modal', function() {
		$('#J_pageInfoModal form').cleanValidateStyle();
	});
	$('#J_tagInfoModal').on('hide.bs.modal', function() {
		$('#J_tagInfoModal form').cleanValidateStyle();
	});
	
	var start = 0, limit = 30;	// 翻页信息
	
	function refreshTagActionOptions() {
		var url = CTX_PATH + '/tagAction/list';
		$.get(url, function(data) {
			if (!data || !data.response) {
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
			if (!data || !data.response) {
				return;
			}
			$('#T_targetId').empty().append($.map(data.response, function(target) {
				return $('<option value="' + target.id + '">' + target.name + '</option>');
			}));
		});
	}
	
	function loadPageInfoResult(callback) {
		var params = $.extend({
			start: start, limit: limit
		}, common.collectParams('#J_queryArea input[type!=button][type!=submit][type!=reset]'));
		var url = CTX_PATH + '/pageInfo/list';
		$.get(url, params, function(data) {
			if (!data || !data.response || !data.response.list) {
				alert(data.errorMsg || 'failed!');
				return;
			}
			$.isFunction(callback) && callback(data);
		});
	}
	
	function renderPageInfoTbody(list) {
		$('#J_pageInfoTbody').empty().append($('#J_pageInfoTmpl').tmpl(list));
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
			$modal.find('input[name=id]').val('');
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
			if (!pageInfoValidator.form()) {
				//common.alertMsg('参数有误，请检查!');
				return;
			}
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
				if (data.code !== 0) {
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
		$('#J_pageInfoTbody').on('click', 'button.open-update-modal', function() {
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
			if (!pageInfoValidator.form()) {
				//common.alertMsg('参数有误，请检查!');
				return;
			}
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
				if (data.code !== 0) {
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
		$("#J_pageInfoTbody").on('click', '.open-create-tag-modal', function() {
			var $tr = $(this).parents('tr').eq(0);
			var $tds = $tr.children();
			var pageInfoId = $tr.data('id'),
				pageNo = $tds.eq(0).html(),
				pageName = $tds.eq(1).html();
			var $modal = $('#J_tagInfoModal');
			common.clearForm($modal.find('form'));
			$('#T_actionId').children(':first-child').prop('selected', true);
			$('#T_targetId').children(':first-child').prop('selected', true);
			$modal.find('.modal-title > strong').html('新增操作项');
			$modal.find('input[name=pageInfoId]').val(pageInfoId);
			$modal.find('input[name=pageNo]').val(pageNo).attr({disabled: true});
			$modal.find('input[name=pageName]').val(pageName).attr({disabled: true});
			$modal.find('.modal-dialog').css({
				width: 400,
				'margin-top': 100
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
			if (!tagInfoValidator.form()) {
				//common.alertMsg('参数有误，请检查!');
				return;
			}
			var params = {
				pageNo: $('#T_pageNo').val(),
				pageInfoId: $('#T_pageInfoId').val(),
				tagNo: $('#T_tagNo').val(),
				name: $('#T_name').val(),
				actionId: $('#T_actionId').val(),
				targetId: $('#T_targetId').val(),
				originVersion: common.parseAppVersion($('#T_originVersion').val()),
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
				if (data.code !== 0) {
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
	
	function initQueryBtn() {
		var $queryBtn = $('#J_queryBtn');
		$('#J_queryArea').on('keyup', 'input[type!=button][type!=submit][type!=reset]', function(ev) {
			if (ev.which == 13) {
				$queryBtn.trigger('click');
			}
		});
		$queryBtn.on('click', function() {
			start = 0;
			refreshPageInfoTbl();
		});
	}
	
	function initClearBtn() {
		$('#J_clearBtn').on('click', function() {
			start = 0;
			common.clearForm($('#J_queryArea form'));
			refreshPageInfoTbl();
		});
	}
	
	/** 导出excel **/
	function initExportPageInfoBtn() {
		$('#J_exportPageInfoBtn').on('click', function() {
			var params = common.collectParams('#J_queryArea input[type!=button][type!=submit][type!=reset]');
			var url = common.buildUrlByParams(CTX_PATH + '/pageInfo/export', params, true);
			window.open(url, 'exportFrame');
		});
	}
	
	function init() {
		refreshTagActionOptions();
		refreshTagTargetOptions();
		refreshPageInfoTbl();
		initOpenCreateModalBtn();
		initOpenUpdateModalBtn();
		initOpenCreateTagModalBtn();
		initCreatePageInfoBtn();
		initUpdatePageInfoBtn();
		initCreateTagInfoBtn();
		initQueryBtn();
		initClearBtn();
		initExportPageInfoBtn();
	}
	
	module.exports = {init: init};
	
});
