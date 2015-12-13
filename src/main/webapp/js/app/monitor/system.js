define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common');
	
	function initSystemInfo() {
		return $.get(CTX_PATH + '/monitor/systemInfo', function(data) {
			if(data.code !== 0 || !data.response) {
				return;
			}
			$('#J_systemInfoTmpl').tmpl(data.response)
				.appendTo('#J_systemInfoTbody')
		});
	}
	
	function init() {
		initSystemInfo();
	}
	
	module.exports = {init: init};
	
});