define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	require('bootstrap.browsefilebtn');
	require('bootstrap.uploadfilebtn');
	require('bootstrap.pagebar');
	var $ = require('jquery'),
		common = require('app/common'),
		tagInfoValidator = require('app/tagInfo/validator').validate('#J_tagInfoModal form', function(options) {
			options.rules.tagNo.min = 10001;
			delete options.rules.tagNo.remote.data.pageInfoId;
		});
		
	$('#J_tagInfoModal').on('hide.bs.modal', function() {
		$('#J_tagInfoModal form').cleanValidateStyle();
	});
	
	var start = 0, limit = 30;	// 翻页信息
	
	var tagActionDict = {}, tagTargetDict = {};
	
	function refreshTagActionOptions() {
		var url = CTX_PATH + '/tagAction/list';
		return $.get(url, function(data) {
			if (!data || !data.response) {
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
			if (!data || !data.response) {
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
		params['originVersionSince'] = common.parseAppVersion(params['originVersionSince']);
		params['originVersionUntil'] = common.parseAppVersion(params['originVersionUntil']);
		var url = CTX_PATH + '/tagInfo/list';
		$.get(url, params, function(data) {
			if (!data || !data.response || !data.response.list) {
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
			},
			getParamBtnClass: function(hasParams) {
				return hasParams === true? ' btn-success ': ' btn-primary ';
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
			if (!tagInfoValidator.form()) {
				return;
			}
			var params = {
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
			if (!tagInfoValidator.form()) {
				return;
			}
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
				if (data.code !== 0) {
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
	
	/** 删除tagInfo开始 **/
	function initDeleteTagInfoBtn() {
		$("#J_tagInfoTbody").on('click', '.delete-tag-btn', function() {
			var $tr = $(this).parents('tr').eq(0);
			var $tds = $tr.children('td'),
				id = $tr.data('id');
			var tagNo = $tds.eq(0).text(),
				tagName = $tds.eq(1).text();
			common.confirmMsg('确定要删除如下操作项? <br/> [t' + tagNo + '] [' + tagName + ']').done(function(result) {
				if (!result) {
					return;
				}
				$.post(CTX_PATH + '/tagInfo/delete', {id: id}, function(data) {
					if (data && data.code === 0) {
						common.alertMsg('删除成功!');
						refreshTagInfoTbl();
					} else {
						common.alertMsg('删除失败!');
					}
				});
			});
		});
	}
	/** 删除tagInfo结束 **/
	
	/** 更新tagParam开始 **/
	var paramNameList = ['num', 'idx', 'type', 'key', 'color', 'cont', 'mode', 'sum', 'choose', 'subway', 'info', 'value'];
	
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
			$modal.find('input[name=tagInfoId]').val($tr.data('id'));
			$modal.find('input[name=tagNo]').val($tds.eq(0).html()).attr({disabled: true});
			$modal.find('input[name=tagName]').val($tds.eq(1).html()).attr({disabled: true});
			
			var url = CTX_PATH + '/tagParam/detail';
			$.get(url, {tagInfoId: tagInfoId}, function(data) {
				var tagParam = data.response,
					paramInfoList = tagParam? tagParam.paramInfoList: [];
				$modal.find('textarea[name=comment]').val(tagParam? tagParam.comment: '');
				/* if ($.isEmptyObject(paramInfoList)) {
					paramInfoList = [{}];
				} */
				$('#TP_paramInfoTbody').empty().append($('#TP_paramInfoTmpl').tmpl(paramInfoList, {
					renderParamNameOptions: function(selectedName) {
						return $.map(paramNameList, function(name, i) {
							return '<option ' + (name == selectedName? 'selected="selected"': '') + '>' + name + '</option>';
						}).join('');
					}
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
			var $tr = $('#TP_paramInfoTmpl').tmpl({}, {
				renderParamNameOptions: function(selectedName) {
					return $.map(paramNameList, function(name, i) {
						return '<option ' + (name == selectedName? 'selected="selected"': '') + '>' + name + '</option>';
					}).join('');
				}
			});
			$tr.appendTo('#TP_paramInfoTbody');
			var $backdrop = $('#J_tagParamModal .modal-backdrop.in');
			$backdrop.height($backdrop.height() + $tr.height());
		});
	}
	function initUpdateTagParamBtn() {
		$('#J_updateTagParamBtn').on('click', function() {
			var tagInfoId = $('#TP_tagInfoId').val();
			var paramInfoList = $('#TP_paramInfoTbody tr').map(function(i, paramInfoTr) {
				var $tr = $(paramInfoTr);
				return {
					id: $tr.data('param-info-id'),
					name: $tr.find('.param-info-name').val(),
					value: $tr.find('.param-info-value').val(),
					description: $tr.find('.param-info-description').val()
				};
			}).toArray();
			var params = {
				tagInfoId: tagInfoId,
				comment: $('#TP_comment').val(),
				paramInfoList: JSON.stringify(paramInfoList)
			};
			doUpdateTagParam(params);
		});
	}
	function doUpdateTagParam(params) {
		$.ajax({
			url: CTX_PATH + '/tagParam/update',
			type: 'POST',
			dataType: 'json',
			data: params,
			success: function(data) {
				if (data.code !== 0) {
					common.alertMsg('更新失败!');
					return;
				} else {
					common.alertMsg('更新成功!').done(function() {
						refreshTagInfoTbl();
						$('#J_tagParamModal').modal('hide');
					});
				}
			},
			error: function() {
				common.alertMsg('请求失败!');
			}
		});
	}
	/** 更新tagParam结束 **/
	
	
	function initQueryBtn() {
		var $queryBtn = $('#J_queryBtn');
		$('#J_queryArea').on('keyup', 'input[type!=button][type!=submit][type!=reset]', function(ev) {
			if (ev.which == 13) {
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
	
	/** 导出excel **/
	function initExportTagInfoBtn() {
		$('#J_exportTagInfoBtn').on('click', function() {
			var params = common.collectParams('#J_queryArea input[type!=button][type!=submit][type!=reset]');
			params['originVersionSince'] = common.parseAppVersion(params['originVersionSince']);
			params['originVersionUntil'] = common.parseAppVersion(params['originVersionUntil']);
			params['isCommonTag'] = true;
			var url = common.buildUrlByParams(CTX_PATH + '/tagInfo/export', params, true);
			window.open(url, 'exportFrame');
		});
	}
	
	/** excel导入 **/
	function initOpenUploadExcelModalBtn() {
		var $modal = $('#J_uploadExcelModal');
		$('#J_openUploadExcelModalBtn').on('click', function() {
			$modal.find('.modal-dialog').css({
				width: 500,
				'margin-top': function() {
					return ( $(window).height() - $(this).height() ) / 3;
				}
			});
			$modal.modal({
				backdrop: 'static'
			});
		});
		$modal.on('hide.bs.modal', function() {
			$('#J_uploadExcelPath').val('');
		});
	}
	
	function initBrowseExcelBtn() {
		$('#J_browseExcelBtn').bootstrapBrowseFileBtn({
			pathTxt: '#J_uploadExcelPath',
			browseBtn: '#J_browseExcelBtn',
			fileInputId: 'J_uploadExcelInput',
			fileInputName: 'file'
		});
	}

	function initUploadExcelBtn() {
		$('#J_uploadExcelBtn').bootstrapUploadFileBtn({
			progressBar: '#J_uploadProgressBar',
			fileInput: '#J_uploadExcelInput',
			url: CTX_PATH + '/tagInfo/import',
			validator: function() {
				var fileName = $('#J_uploadExcelInput').val();
				if (!fileName){
					common.alertMsg('请先选择要上传的Excel文件!');
					return false;
				}
				return true;
			},
			success: function(data,ev) {
				common.alertMsg('导入[' + data.response.count + ']条数据');
				$('#J_uploadExcelModal').modal('hide');
				refreshTagInfoTbl();
			},
			error: function(status, ev) {common.alertMsg('导入失败');},
			unsupport: function() {common.alertMsg('不支持当前浏览器');}
		});
	}
	
	function initUploadExcel() {
		initOpenUploadExcelModalBtn();
		initBrowseExcelBtn();
		initUploadExcelBtn();
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
		initOpenUpdateTagParamModalBtn();
		initAddNewTagParamBtn();
		initDeleteTagInfoBtn();
		initUpdateTagParamBtn();
		initQueryBtn();
		initClearBtn();
		initExportTagInfoBtn();
		initUploadExcel();
	}
	
	module.exports = {init: init};
	
});
