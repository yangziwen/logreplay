define(function(require, exports, module) {
	
	'use strict';
	
	var $ = require('jquery'),
		common = require('app/common');
	require('bootstrap.browsefilebtn');
	require('bootstrap.uploadfilebtn');
	require('jquery.imagetailor');
	
	function initOpenUploadAvatarModalBtn() {
		var $modal = $('#J_uploadAvatarModal');
		$('#J_openUploadAvatarModalBtn').on('click', function() {
			renderUploadAvatarModal($modal);
		});
		
		(function() {
			var $browseBtn = $modal.find('.btn-browse');
			$browseBtn.bootstrapBrowseFileBtn({
				pathTxt: '#J_avatarPath',
				browseBtn: $browseBtn,
				fileInputId: 'J_avatarFileInput',
				fileInputName: 'file'
			});
		})();
		
		(function() {
			$modal.find('.btn-upload').bootstrapUploadFileBtn({
				fileInput: '#J_avatarFileInput',
				url: CTX_PATH + '/image/upload?type=raw',
				validator: function() {
					var fileName = $('#J_avatarFileInput').val();
					if(!fileName){
						common.alertMsg('请先选择要上传的图片文件!');
						return false;
					}
					return true;
				},
				success: function(data, ev) {
					initImageTailor(data);
				}, 
				error: function() {
					common.alertMsg('上传失败!');
				}
			});
		})();
		
		function initImageTailor(data) {
			var image = data.response;
			var $container = $modal.find('.tailor-container').empty();
			$container.imageTailor({
				'img-src': CTX_PATH + '/image/' + image.id
			});
			var $submitBtn = $modal.find('.btn-submit').off('click').on('click', function(){
				var url = CTX_PATH + '/image/avatar';
				var info = $container.tailorInfo();
				$.post(url, {
					imageId: parseInt(image.id),
					left: parseInt(info.left),
					top: parseInt(info.top),
					width: info.width,
					height: info.height,
					imgWidth: info.imgWidth,
					imgHeight: info.imgHeight
				}, function(data) {
					if(data.code === 0) {
						common.alertMsg('更新成功!').done(function(){
							$modal.modal('hide');
							location.reload(true);
						});
					} else {
						common.alertMsg('更新失败!');
					}
				});
			});
			$modal.find('.btn-browse, .btn-upload').hide();
			$submitBtn.show();
			$modal.find('.upload-group').hide();
			$container.show();
		}
	}
	
	function renderUploadAvatarModal($modal) {
		$modal.find('.modal-dialog').css({
			width: 500,
			'margin-top': 150
		});
		$modal.find('.btn-browse, .btn-upload').show();
		$modal.find('.btn-submit').hide();
		$modal.find('.upload-group').show();
		$modal.find('.tailor-container').hide();
		$modal.modal({backdrop: 'static'});
	}
	
	function init() {
		initOpenUploadAvatarModalBtn();
	}
	
	return {init: init};
});