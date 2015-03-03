define(function(require, exports, module) {
	
	"use strict";
	
	var $ = require('jquery');
	require('jquery.validate');
	
	$.fn.cleanValidateStyle = function() {
		this.find('input').prop({title: ''})
			.parent()
			.removeClass('has-success')
			.removeClass('has-error')
			.find('.form-control-feedback')
			.remove();
	}
	
	var validateConfig = {
		onkeyup: false,
		onfocusin: false,
		focusCleanup: true,
		success: function(label, element) {
			$(element).prop({title: ''})
				.parent().removeClass('has-error')
				.addClass('has-success').addClass('has-feedback')
				.find('.form-control-feedback').remove().end()
				.append('<span class="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>');
		},
		errorPlacement: function(error, element) {
			if(!error.text()) {
				return;
			}
			$(element).prop({title: error.text()})
				.parent().removeClass('has-success')
				.addClass('has-error').addClass('has-feedback')
				.find('.form-control-feedback').remove().end()
				.append('<span class="glyphicon glyphicon-remove form-control-feedback" aria-hidden="true"></span>');
		},
		rules: {
			pageNo: {
				required: true,
				number: true,
				min: 1,
				remote: {
					url: CTX_PATH + '/pageInfo/checkDuplication',
					type: 'get',
					dataType: 'json',
					data: {
						id: function() {
							return $('#P_id').val();
						},
						pageNo: function() {
							return $("#P_pageNo").val();
						}
					}
				}
			},
			name: {
				required: true,
			}
		},
		messages: {
			pageNo: {
				required: '页面编号不能为空!',
				number: '页面编号必须为数字!',
				min: '页面编号不能小于{0}!',
				remote: '页面编号重复!'
			},
			name: {
				required: '页面名称不能为空!',
			}
		}
	};
	
	module.exports = {
		validate: function(form) {
			return $(form).validate(validateConfig);
		}
	};
	
});