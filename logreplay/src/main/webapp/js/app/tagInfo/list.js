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
		var params = {start: start, limit: limit};
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
				$modal.find('input[name=pageInfoId]').val(tagInfo.pageInfoId);
				$modal.find('input[name=pageNo]').val(tagInfo.pageInfo.pageNo).attr({disabled: true});
				$modal.find('input[name=pageName]').val(tagInfo.pageInfo.name).attr({disabled: true});
				$modal.find('input[name=tagNo]').val(tagInfo.tagNo);
				$modal.find('input[name=name]').val(tagInfo.name);
				$modal.find('select[name=actionId]').val(tagInfo.actionId);
				$modal.find('select[name=targetId]').val(tagInfo.targetId);
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
				pageInfoId: $('#T_pageInfoId').val(),
				tagNo: $('#T_tagNo').val(),
				name: $('#T_name').val(),
				actionId: $('#T_actionId').val(),
				targetId: $('#T_targetId').val(),
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
	/** 新增tagInfo结束 **/
	
	/** 更新tagParam开始 **/
	function initOpenUpdateTagParamModalBtn() {
		
		$('#TP_paramInfoTbody').on('click', '.remove-param-info-btn', function(ev) {
			var $tr = $(this).parents('tr').eq(0);
			$tr.remove();
		});
		
		$('#J_tagInfoTbody').on('click', '.open-update-tag-param-modal', function() {
			var $tr = $(this).parents('tr').eq(0),
				$tds = $tr.children();
			var tagInfoId = $tr.data('id');
			var $modal = $('#J_tagParamModal');
			common.clearForm($modal.find('form'));
			//var url = CTX_PATH + '/tagInfo/detail/' + id;
			$modal.find('input[name=pageNo]').val($tds.eq(0).html()).attr({disabled: true});
			$modal.find('input[name=pageName]').val($tds.eq(1).html()).attr({disabled: true});
			$modal.find('input[name=tagNo]').val($tds.eq(2).html()).attr({disabled: true});
			$modal.find('input[name=tagName]').val($tds.eq(3).html()).attr({disabled: true});
			
			var url = CTX_PATH + '/tagParam/detail';
			$.get(url, {tagInfoId: tagInfoId}, function(data) {
				var tagParam = data.response,
					paramInfoList = tagParam? tagParam.paramInfoList: [];
				$modal.find('textarea[name=comment]').val(tagParam? tagParam.comment: '');
				if($.isEmptyObject(paramInfoList)) {
					paramInfoList = [{}];
				}
				$('#TP_paramInfoTbody').empty().append($('#TP_paramInfoTmpl').tmpl(paramInfoList, {
					
				}).appendTo('#TP_paramInfoTbody'));
				$modal.find('.modal-dialog').css({
					width: 650,
					'margin-top': 100
				});
				$modal.find('button.update-tag-param').show();
				$modal.modal({
					backdrop: 'static'
				});
			});
		});
	}
	function initAddNewTagParamBtn() {
		$('#TP_addNewTagParam').on('click', function() {
			var $tr = $('#TP_paramInfoTmpl').tmpl({});
			$tr.appendTo('#TP_paramInfoTbody');
			var $backdrop = $('#J_tagParamModal .modal-backdrop.in');
			$backdrop.height($backdrop.height() + $tr.height());
		});
	}
	/** 更新tagParam结束 **/
	
	function init() {
		$.when(refreshTagActionOptions(),refreshTagTargetOptions())
		.done(function() {
			refreshTagInfoTbl();
		});
		initOpenUpdateTagModalBtn();
		initOpenUpdateTagParamModalBtn();
		initUpdateTagInfoBtn();
		initAddNewTagParamBtn();
	}
	
	module.exports = {init: init};
	
});