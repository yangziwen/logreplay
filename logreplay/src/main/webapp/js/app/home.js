define(function(require, exports, module) {
	
	var common = require('app/common');
	
	function init() {
		$('#J_alertTest').on('click', function() {
			common.alertMsg('测试一下alert提示框!');
		});
		$('#J_confirmTest').on('click', function() {
			common.confirmMsg('测试一下confirm提示框!').done(function(confirmed) {
				alert(confirmed? 'yes': 'no');
			});
		});
	}
	
	module.exports = {
		init: init
	}
});