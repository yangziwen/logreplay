define(function(require, exports, module) {
	
	"use strict";
	
	require('jquery.validate');
	var $ = require('jquery'),
		common = require('app/common');
	
	$.fn.cleanValidateStyle = function() {
		var validator = $.data(this[0], "validator");
		validator && validator.resetForm();
		this.find('input, textarea').prop({title: ''})
			.parent()
			.removeClass('has-success')
			.removeClass('has-error')
			.find('.form-control-feedback')
			.remove();
	};
	
	$.validator.addMethod('appVersion', function(value, element, param) {
		var appVersion = common.parseAppVersion(value);
		return this.optional(element) || appVersion > 10000000;
	}, '应用版本号有误!');
	
	var coreOptions = {
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
	};
	
	module.exports = {
		validate: function(form, options) {
			return $(form).validate($.extend({}, coreOptions, options));
		}
	};
	
});