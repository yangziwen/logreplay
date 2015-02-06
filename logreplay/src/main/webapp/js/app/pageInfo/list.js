define(function(require, exports, module) {
	
	"use strict";

	require('jquery.tmpl');
	var $ = require('jquery'),
		common = require('app/common');
	
	function loadPageInfoResult(params, callback) {
		var url = CTX_PATH + '/pageInfo/list';
		$.get(url, params, function(data) {
			if(!data || !data.response || !data.response.list) {
				alert('failed!');
				return;
			}
			$.isFunction(callback) && callback(data);
		});
	}
	
	function renderPageInfoTbody(list) {
		$('#J_pageInfoTbody').empty().append($('#J_pageInfoTmpl').tmpl(list));
	}
	
	function init() {
		loadPageInfoResult({}, function(data) {
			renderPageInfoTbody(data.response.list);
		});
	}
	
	module.exports = {init: init};
	
});