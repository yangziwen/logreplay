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
			start: start, limit: limit
		}, common.collectParams('#J_queryArea input[type=text], #J_queryArea select'));
		params['originVersionSince'] = common.parseAppVersion(params['originVersionSince']);
		params['originVersionUntil'] = common.parseAppVersion(params['originVersionUntil']);
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
			},
			displayInspectStatus: function(inspectStatus) {
				switch(inspectStatus) {
					case 0: return '<span class="label label-default">未校验</span>';
					case 1: return '<span class="label label-success">校验正确</span>';
					case 2: return '<span class="label label-danger">校验错误</span>';
					default: return '--';
				}
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
		initQueryBtn();
		initClearBtn();
	}
	
	module.exports = {init: init};
	
});