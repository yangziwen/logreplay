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
	
	/** 打开详情弹出框 开始 **/
	function initOpenDetailModalBtn() {
		var $resolveBtn = $('#J_resolveBtn');
		var $modal = $('#J_detailModal');
		$("#J_inspectionRecordTbody").on('click', 'button.open-detail-modal', function() {
			var $this = $(this);
			var $tr = $this.parents('tr').eq(0),
				id = $tr.data('id');
			var url = CTX_PATH + '/inspectionRecord/detail/' + id;
			$.get(url).then(function(data, result) {
				if(result !== 'success' || !data || !data.response) {
					common.alergMsg('请求失败!');
					return;
				}
				var record = data.response;
				$modal.find('.modal-dialog').css({
					width: 700,
					'margin-top': function() {
						return ( $(window).height() - $(this).height() ) / 4;
					}
				});
				if(record.valid === true || record.solved === true) {
					$resolveBtn.hide();
				} else {
					$resolveBtn.show();
				}
				$modal.find('tbody').data('id', record.id).empty().append($('#J_inspectionRecordDetailTmpl').tmpl(record, {
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
					},
					displaySubmitterName: function(record){
						return record.submitterScreenName || record.submitterUsername || '--';
					},
					displaySolverName: function(record) {
						return record.solverScreenName || record.solverUsername || '--';
					}
				})); 
				$modal.modal({
					backdrop: 'static'
				});
			});
		});
	}
	/** 打开详情弹出框 结束 **/
	
	/** 将记录标记为已解决 开始 **/
	function initSolveBtn() {
		var $modal = $('#J_detailModal');
		$('#J_resolveBtn').on('click', function() {
			common.confirmMsg('请确认是否将本条记录标记为<strong>“已处理”</strong>?').then(function(result) {
				if(result !== true) {
					return;
				}
				var id = $modal.find('tbody').data('id');
				$.post(CTX_PATH + '/inspectionRecord/resolve/' + id).then(function(data) {
					if(!data || data.code === 0) {
						common.alertMsg('更新成功!');
						refreshInspectionRecordTbl();
						$modal.modal('hide');
					} else {
						common.alertMsg('更新失败!');
					}
				});
			});
		});
	}
	/** 将记录标记为已解决 结束 **/
	
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
		initOpenDetailModalBtn();
		initSolveBtn();
		initQueryBtn();
		initClearBtn();
	}
	
	module.exports = {init: init};
	
});