/**
 * 带上传进度条的文件上传按钮
 * 调用方式如下所示
$('#J_uploadStaticBtn').bootstrapUploadFileBtn({
	progressBar: '#J_uploadStaticProgressBar',
	fileInput: '#J_deployItemField',
	url: '${ctx_path}/test/uploadTest',
	data: {originalFilename: 'test'},
	validator: function() {return true;},
	success: function(data,ev){alert('上传成功');},
	error: function(status, ev){alert('上传失败');},
	unsupport: function() {alert('不支持当前浏览器')}
});
 */
define(function(require){
	var jQuery = require('jquery');
	
	(function($){
		'use strict';
		$.fn.extend({
			bootstrapUploadFileBtn: function(options) {
				var opts = $.extend({}, options),
					$uploadBtn = $(this),
					originHtml = $uploadBtn.html(),
					$progressBar = $(opts.progressBar),
					data = opts.data || {},
					validator = $.isFunction(opts.validator) && opts.validator || function(){return true;},
					uploadUrl = opts.url,
					success = opts.success || $.noop,
					error = opts.error || $.noop,
					unsupport = opts.unsupport || $.noop;
				$uploadBtn.on('click', function(){
					if(!$.isFunction(window.FormData)) {
						unsupport.call(options);
						return;
					}
					if(!validator.call(opts)) {
						return;
					}
					var $fileInput = $(opts.fileInput);
					$progressBar.empty().append('<div class="bar" style="width: 0%;"></div>');
					var $completedBar = $progressBar.children('.bar');
					var fd = new FormData(),
						file = $fileInput[0].files[0];
					var params = $.isFunction(data)? data(): data;
					for(var key in params) {
						fd.append(key, params[key]);
					}
					fd.append($fileInput.attr('name'), file);
					var xhr = new XMLHttpRequest();
					xhr.upload.addEventListener("progress", function(ev){
						if(!ev.lengthComputable) {
							return;
						}
						var completedPercent = Math.round(ev.loaded * 100 / ev.total);
						$completedBar.css('width', completedPercent + '%');
					}, false);
					xhr.addEventListener('readystatechange', function(ev){
						if(ev.target.readyState != 4) {
							return;
						}
						if(ev.target.status == 200 || ev.target.status == 304) {
							success.call(opts, $.parseJSON(ev.target.responseText), ev.target.status, ev);
						} else {
							error.call(opts, {}, ev.target.status, ev);
						}
						setTimeout(function(){
							$completedBar.css('width', '100%');
							$uploadBtn.html(originHtml).attr({disabled: false});
						}, 0);
					}, false);
					xhr.open("POST", uploadUrl);
					xhr.send(fd);
					$uploadBtn.html('上传中').attr({disabled: true});
				});
			}
		});
		
	})(jQuery);

});