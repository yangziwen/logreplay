define(function(require, exports, module) {
	
	require('bootstrap.pagebar');
	var common = require('app/common'),
		$ = require('jquery');
	
	function init() {
		$('#J_alertTest').on('click', function() {
			common.alertMsg('测试一下alert提示框!');
		});
		$('#J_confirmTest').on('click', function() {
			common.confirmMsg('测试一下confirm提示框!').done(function(confirmed) {
				alert(confirmed? 'yes': 'no');
			});
		});
		common.buildPageBar('#J_pagebar', 0, 10, 100);
	}
	
	module.exports = {
		init: init
	};
});