define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common');

	var replaying = false, lockScroll = false, logNum = 0;
	
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
			$.map(data.response, function(i, target) {
				tagTargetDict[target.id] = target.name;
			});
		});
	}
	
	var $replaySwitchBtn = $('#J_replaySwitchBtn'),
		$clearBtn = $('#J_clearBtn'),
		$replayArea = $('#J_replayArea'),
		$replayTmpl = $('#J_replayTmpl');
	
	function initReplaySwitchBtn() {
		$replaySwitchBtn.on('click', function() {
			replaying = !replaying;
			switchButtonStatus(replaying);
		});
	}
	
	function switchButtonStatus(replaying) {
		if(replaying) {
			$replaySwitchBtn.html('停止回放');
			$clearBtn.attr({disabled: true});
			var params = common.collectParams('#J_queryArea input[type!=button]');
			params.since = $.now();
			doReplay(params, 1000);
		} else {
			$replaySwitchBtn.html('开始回放');
			$clearBtn.attr({disabled: false});
		}
	}
	
	function doReplay(params, queryInterval) {
		queryOperationRecords(params).done(function(data) {
			var recordList = data.response;
			if(recordList && recordList.length > 0) {
				var record = recordList[recordList.length - 1];
				record && (params.since = record.timestamp);
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
				$replayArea.append($replayTmpl.tmpl(recordList, {
					formatTime: function(t) {
						if(!t) {
							return '--';
						}
						var ts = t + '';
						return new Date(t).format('yyyy-MM-dd hh:mm:ss') + '.' + ts.substring(ts.length - 3, ts.length);
					}, 
					toActionName: function(actionId) {
						return tagActionDict[actionId] || '';
					},
					toTargetName: function(targetId) {
						return tagTargetDict[targetId] || '';
					},
					bgClass: function() {
						return (logNum ++  % 2)? 'bg-warning': 'bg-info';
					}
				}));
				if(!lockScroll) {
					$replayArea.scrollTop($replayArea[0].scrollHeight - $replayArea.height());
				}
			}
		});
	}
	
	function initClearBtn() {
		$('#J_clearBtn').on('click', function() {
			$replayArea.empty();
			logNum = 0;
		});
	}
	
	function initLockScrollBtn() {
		$('#J_lockScrollBtn').on('click', function() {
			lockScroll = !lockScroll;
			var $this = $(this);
			if(lockScroll) {
				$this.html('解锁滚动')
			} else {
				$this.html('锁定滚动')
			}
		});
	}
	
	function init() {
		refreshTagActionDict();
		refreshTagTargetDict();
		initReplaySwitchBtn();
		initClearBtn();
		initLockScrollBtn();
	}
	
	module.exports = {init: init};
	
});