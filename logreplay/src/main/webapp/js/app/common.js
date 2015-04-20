define(function(require, exports, module){
	
	"use strict";
	
	var $ = require('jquery');
	require('jquery.cookie');
	require('bootstrap');
	
	String.prototype.encodeUnicode = function() {
		if(!this) return this;
		var chars = [];
		for(var i=0, l=this.length; i<l; i++) chars[i] = this.charCodeAt(i);
		return "&#" + chars.join(";&#") + ";";  
	};
	
	String.prototype.decodeUnicode = function() {
		if(!this) return this;
		return this.replace(/&#(x)?([^&;]{1,5});?/g, function (a, b, c) {    
	        return String.fromCharCode(parseInt(c, b ? 16 : 10));    
	    });
	};
	
	$.ajaxSetup({
		beforeSend: function(xhr) {
			xhr.setRequestHeader('isAjax', 'true');
		}
	});
	
	/**
	 * 调用bootstrap样式的弹出框
	 */
	var alertMsgTmpl = [
		'<div class="modal" tabindex="-1" id="J_alertModal">',
			'<div class="modal-dialog">',
				'<div class="modal-content">',
					'<div class="modal-header">',
						'<button type="button" class="close" data-dismiss="modal">',
							'<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>',
						'</button>',
						'<h4 class="modal-title"><strong>提示<strong></h4>',
					'</div>',
					'<div class="modal-body">',
						'<p style="font-size: 16px; text-align: center;"></p>',
					'</div>',
					'<div class="modal-footer">',
						'<button class="btn btn-primary" data-dismiss="modal">确定</button>',
					'</div>',
				'</div>',
			'</div>',
		'</div>'
	].join('');
	function alertMsg (msg) {
		var deferred = $.Deferred();
		var width = 350;
		if($.isPlainObject(msg)) {
			width = msg.width || width;
			msg = msg.message;
		}
		var $modal = $('#J_alertModal');
		if($modal.size() == 0) {
			$modal = $(alertMsgTmpl).appendTo('body');
//			alert(msg);
//			return deferred.resolve().promise();
		}
		msg = ('' + msg).replace(/\n/g, '<br/>');
		$modal.find('.modal-body p').html(msg);
		$modal.find('.modal-dialog').css({
			width: width,
			'margin-top': function() {
				return ( $(window).height() - $(this).height() ) / 3;
			}
		});
		$modal.modal({
			backdrop: 'static',
			keyboard: false
		});
		$modal.on('hidden.bs.modal', function(){
			$(this).off('hidden.bs.modal');
			deferred.resolve();
		});
		return deferred.promise();
	};

	/**
	 * 调用bootstrap样式的确认框
	 */
	var confirmMsgTmpl = [
		'<div class="modal" tabindex="-1" id="J_confirmModal">',
			'<div class="modal-dialog">',
				'<div class="modal-content">',
					'<div class="modal-header">',
						'<button type="button" class="close" data-dismiss="modal">',
							'<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>',
						'</button>',
						'<h4 class="modal-title"><strong>确认<strong></h4>',
					'</div>',
					'<div class="modal-body">',
						'<p style="font-size: 16px; text-align: center;"></p>',
					'</div>',
					'<div class="modal-footer">',
						'<button class="btn btn-primary confirm" data-dismiss="modal">确定</button>',
						'<button class="btn btn-default cancel" data-dismiss="modal">取消</button>',
					'</div>',
				'</div>',
			'</div>',
		'</div>'
	].join('');
	function confirmMsg (msg) {
		var deferred = $.Deferred();
		var width = 350;
		if($.isPlainObject(msg)) {
			width = msg.width || width;
			msg = msg.message;
		}
		var $modal = $('#J_confirmModal');
		if($modal.size() == 0) {
			$modal = $(confirmMsgTmpl).appendTo('body');
//			return deferred.resolve(confirm(msg)).promise();
		}
		msg = ('' + msg).replace(/\n/g, '<br/>');
		$modal.find('.modal-body p').html(msg);
		$modal.find('.modal-dialog').css({
			width: width,
			'margin-top': function() {
				return ( $(window).height() - $(this).height() ) / 3;
			}
		});
		$modal.modal({
			backdrop: 'static',
			keyboard: 'false'
		});
		$modal.on('click', '.modal-footer .confirm', function(){
			$modal.off('click');
			$modal.modal('hide');
			deferred.resolve(true);
		});
		$modal.on('click', '.modal-footer .cancel, .modal-header .close', function(){
			$modal.off('click');
			$modal.modal('hide');
			deferred.resolve(false);
		});
		return deferred.promise();
	};
	
	function collectParams(selector) {
		var params = {};
		if(!selector) {
			return params;
		}
		$(selector).each(function(i, input){
			var $input = $(input);
			var key = $input.attr('name'),
				value = $input.val();
			key && (params[key] = value);
		});
		return params;
	}
	
	function buildUrlByParams(prefix, params, ignoreEmptyParams) {
		ignoreEmptyParams = !!ignoreEmptyParams;
		var arr = [];
		for(var key in params) {
			var value = params[key];
			if(ignoreEmptyParams && !value && value !== 0) {
				continue;
			}
			arr.push(key + "=" + encodeURIComponent(value));
		}
		if(arr.length == 0) {
			return prefix;
		} else {
			return prefix + (prefix.indexOf('?') >= 0? '&': '?') + arr.join('&');
		}
	}
	
	function discardEmptyParams(url) {
		url = url.replace(/(?:\?|&)([^\/\?&]+?=)(?=&|$)/g, '');
		if(url.indexOf('&') >= 0 && url.indexOf('?') == -1) {
			url = url.replace('&', '?');
		}
		return url;
	}
	
	/**
	 * @deprecated
	 */
	function submitForm(form, ignoreEmptyParams) {
		if(!form) {
			return;
		}
		var params = collectParams($(form).find('input[type!=button][type!=submit][type!=reset], select'));
		var url = buildUrlByParams($(form).attr('action'), params, ignoreEmptyParams);
	}
	
	function clearForm(form) {
		if(!form) {
			return;
		}
		$(form).find('input[type!=button][type!=submit][type!=reset], select, textarea').val('');
	}
	
	function openWin(options) {
		options = options || {};
		var width = options.width || 420,
			height = options.height || 300;
		var screenWidth = window.screen.availWidth,
			screenHeight = window.screen.availHeight,
			left = (screenWidth - width) / 2,
			top = (screenHeight - height) / 2;
		var winConfig = [
			'width=' + width,
			'height=' + height,
			'left=' + left,
			'top=' + top
		].join(',');
		var url = options.url;
		window.open(url, '_blank', winConfig);
	}
	
	function buildPageBar(pageBarEl, start, limit, totalCount, clickFn){
		pageBarEl = $(pageBarEl);
		if(pageBarEl.size() == 0) {
			return;
		}
		var curPage = Math.floor(start / limit) + 1;
		var totalPage = Math.floor(totalCount / limit) + (totalCount % limit > 0? 1: 0);
		pageBarEl.empty();
		pageBarEl.bootstrapPageBar({
			curPageNum: curPage,
			totalPageNum: totalPage,
			maxBtnNum: 10,
			pageSize: limit,
			siblingBtnNum: 2,
			paginationCls: 'pull-right',
			paginationCss: {'margin-top': 0, 'margin-bottom': 0},
			click: clickFn || $.noop
		});
	}
	
	var majorUnit = 10000000,
		minorUnit = 100000,
		revisionUnit = 1000;
	
	function parseAppVersion(version) {
		if(!version) {
			return 0;
		}
		if($.isNumeric(version) && parseInt(version) > majorUnit) {
			return parseInt(version);
		}
		var arr = version.split('.');
		for(var i = 0, l = arr.length; i < l; i++) {
			if(!$.isNumeric(arr[i])) {
				return 0;
			}
		}
		var major = parseInt(arr[0]),
			minor = parseInt(arr[1]),
			revision = parseInt(arr[2]);
		isNaN(revision) && (revision = 0);
		var result = major * majorUnit + minor * minorUnit + revision * revisionUnit;
		return isNaN(result)? 0: result;
	}
	
	function formatAppVersion(version) {
		if(!$.isNumeric(version)) {
			return '';
		}
		version = parseInt(version);
		if(version < majorUnit) {
			return '';
		}
		var major = Math.floor(version / majorUnit),
			minor = Math.floor((version % majorUnit) / minorUnit),
			revision = Math.floor((version % minorUnit) / revisionUnit);
		var arr = [major, minor];
		if(revision > 0) {
			arr.push(revision);
		}
		return arr.join('.');
	}
	
	/**
	 * 页头更新产品类型
	 */
	(function() {
		$('#J_productMenu li>a').on('click', function() {
			$.cookie('product_id', $(this).data('product-id'), {
				path: CTX_PATH,
				expires: new Date($.now() + 1000 * 3600 * 24 * 365)
			});
			setTimeout(function() {
				location.reload(true);
			}, 0);
		});
	})();
	
	/**
	 * 页头更新昵称
	 */
	(function() {
		function initOpenProfileModalBtn() {
			$('#J_openProfileModalBtn').on('click', function() {
				var url = CTX_PATH + '/user/detail';
				$.get(url, function(data) {
					if(!data || !data.response) {
						alertMsg('用户信息加载失败!');
						return;
					}
					var profile = data.response;
					$('#TB_screenName').val(profile.screenName);
					var $modal = $('#J_profileModal');
					$modal.find('.modal-dialog').css({
						width: 400,
						'margin-top': function() {
							return ( $(window).height() - $(this).height() ) / 3;
						}
					});
					$modal.modal({
						backdrop: 'static'
					});
				});
			});
		}
		function initUpdateProfileBtn() {
			$('#TB_updateProfileBtn').on('click', function() {
				$.ajax({
					url: CTX_PATH + '/user/profile/update',
					type: 'POST',
					dataType: 'json',
					data: {
						screenName: $('#TB_screenName').val()
					},
					success: function(data) {
						if(data.code !== 0) {
							alertMsg('更新失败!');
							return;
						} else {
							alertMsg('更新成功!').done(function() {
								$('#J_profileModal').modal('hide');
							});
						}
					},
					error: function() {
						alertMsg('请求失败!');
					}
				});
			});
		}
		initOpenProfileModalBtn();
		initUpdateProfileBtn();
	})();
	
	/**
	 * 二级菜单收起后，一级菜单下端显示圆角
	 */
	(function() {
		$('a.has-submenu').on('click', function() {
			var $this = $(this),
				$ul = $this.next('ul');
			var collapsed = $ul.height() == 0,
				radius = collapsed? 0: 5;
			setBottomBorderRadius($this, radius);
		});
		
		function setBottomBorderRadius(ele, radius) {
			$(ele).css({
				'border-bottom-left-radius': radius,
				'border-bottom-right-radius': radius
			});
		}
	})();
	
	module.exports = {
		alertMsg: alertMsg,
		confirmMsg: confirmMsg,
		collectParams: collectParams,
		buildUrlByParams: buildUrlByParams,
		discardEmptyParams: discardEmptyParams,
		submitForm: submitForm,
		clearForm: clearForm,
		buildPageBar: buildPageBar,
		openWin: openWin,
		parseAppVersion: parseAppVersion,
		formatAppVersion: formatAppVersion
	};
});