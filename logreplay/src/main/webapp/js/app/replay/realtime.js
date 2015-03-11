define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common');

	var replaying = false, lockScroll = false;
	
	var tagActionDict = {}, tagTargetDict = {};
	
	function refreshTagActionDict() {
		var url = CTX_PATH + '/tagAction/list';
		return $.get(url, function(data) {
			if(!data || !data.response) {
				return;
			}
			tagActionDict = {};
			$.each(data.response, function(i, action) {
				tagActionDict[action.id] = action.name;
			});
		});
	}
	
	function refreshTagTargetDict() {
		var url = CTX_PATH + '/tagTarget/list';
		return $.get(url, function(data) {
			if(!data || !data.response) {
				return;
			}
			$.each(data.response, function(i, target) {
				tagTargetDict[target.id] = target.name;
			});
		});
	}
	
	var $replaySwitchBtn = $('#J_replaySwitchBtn'),
		$clearBtn = $('#J_clearBtn'),
		$replayTmpl = $('#J_replayTmpl'),
		$replayArea = $('#J_replayArea'),
		$replayTbody = $('#J_replayTbody');
	
	function initReplaySwitchBtn() {
		$replaySwitchBtn.on('click', function() {
			replaying = !replaying;
			switchButtonStatus(replaying);
		});
	}
	
	function switchButtonStatus(replaying) {
		if(replaying) {
			$replaySwitchBtn.html('停止校验');
//			$clearBtn.attr({disabled: true});
			var params = common.collectParams('#J_queryArea input[type!=button]');
			//params.since = $.now();
			params.since = 1426047917529; 	// todo
			doReplay(params, 1000);
		} else {
			$replaySwitchBtn.html('开始校验');
//			$clearBtn.attr({disabled: false});
		}
	}
	
	function doReplay(params, queryInterval) {
		queryOperationRecords(params).done(function(data) {
			var recordList = data.response;
			if(recordList && recordList.length > 0) {
				var record = recordList[recordList.length - 1];
				record && (params.idSince = record.id) || (params.since = record.timestamp);
			}
			setTimeout(function() {
				if(replaying) {
					doReplay(params, queryInterval);
				}
			}, queryInterval);
		});
	}
	
	function queryOperationRecords(params) {
		var url = CTX_PATH + '/operationRecord/query';
		return $.get(url, params, function(data) {
			if(!data || data.code !== 0) {
				return;
			}
			var recordList = data.response;
			if(recordList && recordList.length > 0) {
				$replayTbody.append($replayTmpl.tmpl(recordList, {
					formatTime: function(t) {
						if(!t) {
							return '--';
						}
						var ts = t + '';
						return new Date(t).format('yyyy-MM-dd hh:mm:ss') + '.' + ts.substring(ts.length - 3, ts.length);
					}, 
					bgClass: function(record) {
						return (!record.pageName || !record.tagName )? 'danger': '';
					},
					describe: function(record) {
//						return [record.pageName, record.tagName, tagTargetDict[record.targetId], tagActionDict[record.actionId]].join(' => ');
						return [record.pageName, record.tagName].join(' => ');
					}
				}));
				if(!lockScroll) {
					$replayArea.scrollTop($replayArea[0].scrollHeight - $replayArea.height());
				}
			}
		});
	}
	
	/** 提交校验正确结果 开始 **/
	function initSubmitSuccessResultBtn() {
		$('#J_replayTbody').on('click', 'button.submit-success-btn', function() {
			var $btn = $(this);
			common.confirmMsg('请确认将此条记录的校验结果标记为<strong>“正确”<strong>?')
			.then(function(result) {
				if(result !== true) {
					return;
				}
				var $tr = $btn.parents('tr').eq(0);
				var pageNo = $tr.data('pageNo'),
					tagNo = $tr.data('tagNo');
				if(!pageNo) {
					common.alertMsg('页面编号有误!');
					return;
				}
				if(!tagNo) {
					common.alertMsg('操作编号有误!');
					return;
				}
				$.post(CTX_PATH + '/inspectionRecord/submit', {
					pageNo: pageNo,
					tagNo: tagNo,
					valid: true
				}).then(function(data) {
					if(data && data.code === 0) {
						common.alertMsg('提交成功!');
						$tr.removeClass('danger').addClass('success');
						$btn.parent().empty();
					} else {
						common.alertMsg('提交失败!');
					}
				});
			});
		});
	}
	/** 提交校验正确结果 结束 **/
	
	function initClearBtn() {
		$('#J_clearBtn').on('click', function() {
			$replayTbody.empty();
		});
	}
	
	function initLockScrollBtn() {
		$('#J_lockScrollBtn').on('click', function() {
			lockScroll = !lockScroll;
			var $this = $(this);
			if(lockScroll) {
				$this.html('解锁滚动');
			} else {
				$this.html('锁定滚动');
			}
		});
	}
	
	function init() {
		refreshTagActionDict();
		refreshTagTargetDict();
		initReplaySwitchBtn();
		initClearBtn();
		initLockScrollBtn();
		initSubmitSuccessResultBtn();
	}
	
	module.exports = {init: init};
	
});