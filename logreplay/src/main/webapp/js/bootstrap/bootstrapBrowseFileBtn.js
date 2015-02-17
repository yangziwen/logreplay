/**
 * 基于bootstrap样式的上传插件
 * 调用方式如下
$(function(){
	$('#test01').bootstrapBrowseFileBtn({
		pathTxt: '#J_uploadLogTxt',				// 文字框
		browseBtn: '#J_explorerFileBtn',		// 按钮
		btnWidth: 54,
		btnHeight: 34,
		fileInputId: 'J_uploadLogFile',			// 赋予内置的input[type="file"]一个id，以供外部使用
		fileInputName: 'file'					// 赋予内置的input[type="file"]一个name，供表单提交使用
	});
});
 * 
 * @author: zyang
 */
define(function(require){
	var jQuery = require('jquery');

	(function($){
		'use strict';
		$.fn.extend({
			bootstrapBrowseFileBtn: function(options){
				var opts = $.extend({}, $.fn.bootstrapBrowseFileBtn.defaults, options);
				var $this = this,
					$browseBtn = $(options.browseBtn),
					$pathTxt = $(options.pathTxt).attr({disabled: true});
				// webkit内核浏览器无法触发file类型的input的click事件，所以只能用透明度为0的方式叠在按钮上
				var isWebkit = /(webkit)[ \/]([\w.]+)/i.test(navigator.userAgent);
				var $btnWrapper = $('<div>').css({
					display: 'inline',
					width: opts.btnWidth,
					height: opts.btnHeight,
					position: 'relative',
					overflow: 'hidden',
				});
				$browseBtn.before($btnWrapper);
				$btnWrapper.append($browseBtn);
				buildFileInput($btnWrapper, opts);
				if(!isWebkit) {
					$browseBtn.on('click', function(){
						$(this).siblings('input[type=file]').click();
					});
				}
				
				function buildFileInput($wrapper, opts) {
					$wrapper.children('[type=file]').remove();
					var $fileInput = $('<input type="file"/>');
					$fileInput.css({
						'width': opts.btnWidth,
						'height': opts.btnHeight,
						'position': 'absolute',
						'opacity': 0,
					}).attr({
						id: opts.fileInputId,
						name: opts.fileInputName
					});
					if(isWebkit) {
						$fileInput.css({left: -10, top: -8});
					} else {
						$fileInput.css({
							top: 0,
							left: 0,
							width: opts.btnWidth,
							height: opts.btnHeight,
							'z-index': -1
						});
					}
					$wrapper.append($fileInput);
					$fileInput.on('change', function(){
						$this.attr('data-path', this.value);
						$pathTxt.val(this.value);
					});
					return $fileInput;
				}
			}
		});
		$.fn.bootstrapBrowseFileBtn.defaults = {
			btnWidth: 54,
			btnHeight: 34,
			fileInputId: '',
			fileInputName: ''
		};
		
	})(jQuery);

});