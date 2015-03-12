define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	require('bootstrap.pagebar');
	var $ = require('jquery'),
		common = require('app/common');
	
	var start = 0, limit = 30;	// 翻页信息
	
	var tagActionDict = {}, tagTargetDict = {};
	
	function refreshTagActionOptions() {
		var url = CTX_PATH + '/tagAction/list';
		return $.get(url, function(data) {
			if(!data || !data.response) {
				return;
			}
			tagActionDict = {};
			$('#T_actionId').empty().append($.map(data.response, function(action) {
				tagActionDict[action.id] = action.name;
				return $('<option value="' + action.id + '">' + action.name + '</option>');
			}));
		});
	}
	
	function refreshTagTargetOptions() {
		var url = CTX_PATH + '/tagTarget/list';
		return $.get(url, function(data) {
			if(!data || !data.response) {
				return;
			}
			$('#T_targetId').empty().append($.map(data.response, function(target) {
				tagTargetDict[target.id] = target.name;
				return $('<option value="' + target.id + '">' + target.name + '</option>');
			}));
		});
	}
	
	function loadTagInfoResult(callback) {
		var params = $.extend({
			start: start, limit: limit, isCommonTag: true
		}, common.collectParams('#J_queryArea input[type!=button][type!=submit][type!=reset]'));
		var url = CTX_PATH + '/tagInfo/list';
		$.get(url, params, function(data) {
			if(!data || !data.response || !data.response.list) {
				alert('failed!');
				return;
			}
			$.isFunction(callback) && callback(data);
		});
	}
	
	function renderTagInfoTbody(list) {
		$('#J_tagInfoTbody').empty().append($('#J_tagInfoTmpl').tmpl(list, {
			getActionName: function(actionId) {
				return tagActionDict[actionId] || '--';
			},
			getTargetName: function(targetId) {
				return tagTargetDict[targetId] || '--';
			},
			displayOriginVersion: function(originVersion) {
				return common.formatAppVersion(originVersion) || '--';
			}
		}));
	}
	
	function refreshTagInfoTbl() {
		loadTagInfoResult(function(data) {
			var result = data.response;
			common.buildPageBar('#J_pagebar', result.start, result.limit, result.count, function(i, pageNum) {
				start = (pageNum - 1) * limit;
				refreshTagInfoTbl();
			});
			renderTagInfoTbody(result.list);
		});
	}
	
	/** 新增tagInfo开始 **/
	function initOpenCreateTagModalBtn() {
		$("#J_openCreateTagModalBtn").on('click', function() {
			var $modal = $('#J_tagInfoModal');
			common.clearForm($modal.find('form'));
			$modal.find('.modal-title > strong').html('新增公共操作项');
			$modal.find('select option:first-child').prop({selected: true});
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
			var params = {
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
						refreshTagInfoTbl();
					});
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	/** 新增tagInfo结束 **/
	
	/** 修改tagInfo开始 **/
	function initOpenUpdateTagModalBtn() {
		$("#J_tagInfoTbody").on('click', '.open-update-tag-modal', function() {
			var $tr = $(this).parents('tr').eq(0);
			var id = $tr.data('id');
			var $modal = $('#J_tagInfoModal');
			common.clearForm($modal.find('form'));
			var url = CTX_PATH + '/tagInfo/detail/' + id;
			$.get(url, function(data) {
				var tagInfo = data.response;
				$modal.find('.modal-title > strong').html('修改操作项');
				$modal.find('input[name=id]').val(tagInfo.id);
				$modal.find('input[name=tagNo]').val(tagInfo.tagNo);
				$modal.find('input[name=name]').val(tagInfo.name);
				$modal.find('select[name=actionId]').val(tagInfo.actionId);
				$modal.find('select[name=targetId]').val(tagInfo.targetId);
				$modal.find('input[name=originVersion]').val(tagInfo.originVersion);
				$modal.find('textarea[name=comment]').val(tagInfo.comment);
				$modal.find('.modal-dialog').css({
					width: 400,
					'margin-top': 100
				});
				$modal.find('button.create-tag-info').hide();
				$modal.find('button.update-tag-info').show();
				$modal.modal({
					backdrop: 'static'
				});
			});
		});
	}
	function initUpdateTagInfoBtn() {
		$('#J_updateTagInfoBtn').on('click', function() {
			var params = {
				id: $('#T_id').val(),
				tagNo: $('#T_tagNo').val(),
				name: $('#T_name').val(),
				actionId: $('#T_actionId').val(),
				targetId: $('#T_targetId').val(),
				originVersion: common.parseAppVersion($('#T_originVersion').val()),
				comment: $('#T_comment').val()
			};
			doUpdateTagInfo(params);
		});
	}
	
	function doUpdateTagInfo(params) {
		$.ajax({
			url: CTX_PATH + '/tagInfo/update',
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if(data.code !== 0) {
					common.alertMsg('更新失败!');
					return;
				} else {
					common.alertMsg('更新成功!').done(function() {
						refreshTagInfoTbl();
						$('#J_tagInfoModal').modal('hide');
					});
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	/** 修改tagInfo结束 **/
	
	
	function initQueryBtn() {
		var $queryBtn = $('#J_queryBtn');
		$('#J_queryArea').on('keyup', 'input[type!=button][type!=submit][type!=reset]', function(ev) {
			if(ev.which == 13) {
				$queryBtn.trigger('click');
			}
		});
		$queryBtn.on('click', function() {
			start = 0;
			refreshTagInfoTbl();
		});
	}
	
	function initClearBtn() {
		$('#J_clearBtn').on('click', function() {
			start = 0;
			common.clearForm($('#J_queryArea form'));
			refreshTagInfoTbl();
		});
	}
	
	function init() {
		$.when(refreshTagActionOptions(), refreshTagTargetOptions())
		.done(function() {
			refreshTagInfoTbl();
		});
		initOpenCreateTagModalBtn();
		initCreateTagInfoBtn();
		initOpenUpdateTagModalBtn();
		initUpdateTagInfoBtn();
		initQueryBtn();
		initClearBtn();
	}
	
	module.exports = {init: init};
	
});