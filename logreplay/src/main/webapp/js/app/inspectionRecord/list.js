define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	require('bootstrap.pagebar');
	require('bootstrap.datetimepicker');
	var $ = require('jquery'),
		common = require('app/common');
	
	var start = 0, limit = 30;	// 翻页信息
	
	function loadInspectionRecordResult(callback) {
		var params = $.extend({
			start: start, limit: limit
		}, common.collectParams('#J_queryArea input[type=text], #J_queryArea select'));
		var url = CTX_PATH + '/inspectionRecord/list';
		$.get(url, params, function(data) {
			if(!data || !data.response || !data.response.list) {
				alert('failed!');
				return;
			}
			$.isFunction(callback) && callback(data);
		});
	}
	
	function renderInspectionRecordTbody(list) {
		$('#J_inspectionRecordTbody').empty().append($('#J_inspectionRecordTmpl').tmpl(list, {
			displayValidStatus: function(record) {
				if(record.valid === true) {
					return '<span class="label label-success">正确</span>'
				} else if (!record.valid && record.solved === true) {
					return '<span class="label label-default">错误</span>';
				} else {
					return '<span class="label label-danger">错误</span>'
				}
			},
			displaySolvedStatus: function(record) {
				if(record.valid === true) {
					return '--';
				}
				if(record.solved === true) {
					return '<span class="label label-info">已处理</span>';
				} else {
					return '<span class="label label-warning">未处理</span>';
				}
			}
		}));
	}
	
	function refreshInspectionRecordTbl() {
		loadInspectionRecordResult(function(data) {
			var result = data.response;
			common.buildPageBar('#J_pagebar', result.start, result.limit, result.count, function(i, pageNum) {
				start = (pageNum - 1) * limit;
				refreshInspectionRecordTbl();
			});
			renderInspectionRecordTbody(result.list);
		});
	}
	
	function initDateTimePicker() {
		 $('.form_datetime').datetimepicker({
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			forceParse : 0,
			showMeridian : 1,
			pickerPosition: "bottom-left"
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
			refreshInspectionRecordTbl();
		});
	}
	
	function initClearBtn() {
		$('#J_clearBtn').on('click', function() {
			start = 0;
			common.clearForm($('#J_queryArea form'));
			refreshInspectionRecordTbl();
		});
	}
	
	function init() {
		initDateTimePicker();
		refreshInspectionRecordTbl();
		initQueryBtn();
		initClearBtn();
	}
	
	module.exports = {init: init};
	
});