define(function(require, exports, module) {
	
	'use strict';
	
	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common');
	
	function refreshRoleTbl() {
		var url = CTX_PATH + '/role/list';
		$.get(url, function(data) {
			if(!data || !data.response) {
				common.alertMsg('加载失败!');
				return;
			}
			var list = data.response;
			$('#J_roleTbody').empty().append($("#J_roleTmpl").tmpl(list));
		});
	}
	
	function init() {
		refreshRoleTbl();
	}
	
	return {init: init};
	
});