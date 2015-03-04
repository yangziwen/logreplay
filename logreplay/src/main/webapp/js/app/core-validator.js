define(function(require, exports, module) {
	
	"use strict";
	
	var $ = require('jquery');
	require('jquery.validate');
	
	$.fn.cleanValidateStyle = function() {
		var validator = $.data(this[0], "validator");
		validator && validator.resetForm();
		this.find('input, textarea').prop({title: ''})
			.parent()
			.removeClass('has-success')
			.removeClass('has-error')
			.find('.form-control-feedback')
			.remove();
	}
	
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