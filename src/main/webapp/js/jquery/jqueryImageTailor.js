/**
 * 上传图片时对图片进行剪裁的组件
 * 可获取用户剪裁后，图片中保留区域相对于原图的left和top
 * 以及保留区域的width和height
 */

define(function(require){
	
	var jQuery = require('jquery');

	(function($){
		$.fn.extend({
			imageTailor: function(options) {
				var ops = $.extend({}, $.fn.imageTailor.defaults, options);
				var eles = buildTailor(this, ops);
				this.tailorInfo = function() {
					var $mask = eles.mask,
						$img = eles.img;
					var maskPos = $mask.position(),
						imgPos = $img.position();
					return {
						left: maskPos.left - imgPos.left,
						top: maskPos.top - imgPos.top,
						width: $mask.width(),
						height: $mask.height(),
						imgWidth: $img.width(),
						imgHeight: $img.height()
					};
				}
				return this;
			}
		});
		
		$.fn.imageTailor.defaults = {
			'img-max-width': 500,
			'img-max-height': 400,
			'mask-width': 200,			// 剪裁遮罩的默认宽度
			'mask-height': 200,			// 剪裁遮罩的默认高度
			'img-background-opacity': 0.6,
			'img-src': '',
			'mask-title': '通过拖拽进行剪裁'
		};
		
		function buildTailor($tailor, ops) {
			!$.isPlainObject(ops.css) && (ops.css = {});
			$tailor.css($.extend(ops.css, {
				'position': 'relative', 
				'text-align': 'center',
				'vertical-align': 'middle'
			}));
			var $img = $('<img/>').css({
				'max-width': ops['img-max-width'],
				'max-height': ops['img-max-height'],
				'opacity': ops['img-background-opacity'],
				'user-select': 'none',
				'border': '2px solid #ddd',
				'margin-top': -2
			}).attr('src', ops['img-src']);
			var $maskImg = $('<img/>').css({
				'max-width': ops['img-max-width'],
				'max-height': ops['img-max-height'],
				'position': 'absolute',
				'user-select': 'none'
			}).attr('src', ops['img-src']);
			var $mask = $('<div/>').css({
				width: ops['mask-width'],
				height: ops['mask-height'],
				position: 'absolute',
				border: '2px dashed #666',
				overflow: 'hidden'
			}).addClass('img-mask').attr('title', ops['mask-title']).hide();
			$mask.append($maskImg)
				.append('<div style="width: 100%; height: 100%;" />')
				.append('<div class="handler left-handler top-handler" />')
				.append('<div class="handler left-handler bottom-handler" />')
				.append('<div class="handler right-handler top-handler" />')
				.append('<div class="handler right-handler bottom-handler" />');
			$tailor.append($img).append('<div style="width: 100%; height:100%" />').append($mask);
			$img.on('load', function() {
				$mask.css({
					left: ($tailor.width() - $mask.width()) / 2,
					top: ($tailor.height() - $mask.height()) / 2
				}).show();
				bindEvents($tailor, $img, $mask, $maskImg);
			});
			return {
				tailor: $tailor,
				img: $img,
				mask: $mask
			}
		}
		
		function bindEvents($tailor, $img, $mask, $maskImg) {
			
			var maskActive = false,
				$activeHandler = null;
			var imgInfo = $.extend({
					width: $img.width(),
					height: $img.height()
				}, $img.position());
			$.extend(imgInfo, {
				right: imgInfo.left + imgInfo.width,
				bottom: imgInfo.top + imgInfo.height
			});
			var maskInfo = $.extend({
				width: $mask.width(),
				height: $mask.height()
			}, $mask.position());
			
			$maskImg.css({
				left: imgInfo.left - maskInfo.left - 1,
				top: imgInfo.top - maskInfo.top - 3
			});
			
			var x0 = 0, y0 = 0;
			
			$tailor.find('img').on('mousedown', function(ev) {
				ev.preventDefault();
			}).on('mousemove', function(ev) {
				ev.preventDefault();
			}).on('mouseup', function(ev) {
				ev.preventDefault();
			});
			
			$mask.on('mousedown', function(ev) {
				maskInfo = $.extend({
					width: $mask.width(),
					height: $mask.height()
				}, $mask.position());
				$.extend(maskInfo, {
					right: maskInfo.left + maskInfo.width,
					bottom: maskInfo.top + maskInfo.height
				});
				x0 = ev.pageX, y0 = ev.pageY;
				var $target = $(ev.target);
				if($target.is('.handler')) {
					$activeHandler = $target;
				}
				maskActive = true;
			});
			
			$(document).on('mousemove', function(ev) {
				if(!maskActive) return;
				var x = ev.pageX - x0,
					y = ev.pageY - y0;
				var left = 0, top = 0;
				if($activeHandler != null) {
					var h = $activeHandler.is('.left-handler')? -1: 1,
						v = $activeHandler.is('.top-handler')? -1: 1,
						minLength = 20;
					var infos = {
						'-1': imgInfo,
						'1': maskInfo
					};
					var width = Math.max(maskInfo.width + h * x , minLength),
						height = Math.max(maskInfo.height + v * y, minLength);
					var length = Math.max(width, height);
					if(length + infos[h].left > infos[-h].right) {
						length = infos[-h].right - infos[h].left;
					}
					if(length + infos[v].top > infos[-v].bottom) {
						length = infos[-v].bottom - infos[v].top;
					}
					left = h < 0? infos[1].right - length: infos[1].left;
					top = v < 0? infos[1].bottom - length: infos[1].top;
					$mask.css({width: length, height: length});
				} else {
					var width = $mask.width(),
						height = $mask.height();
					left = Math.max(maskInfo.left + x, imgInfo.left);
					top = Math.max(maskInfo.top + y, imgInfo.top);
					left + width > imgInfo.right && (left = imgInfo.right - width);
					top + height > imgInfo.bottom && (top = imgInfo.bottom - height);
				}
				$mask.css({left: left, top: top});
				$maskImg.css({
					left: imgInfo.left - left - 1,
					top: imgInfo.top - top - 3 
				});
			});
			
			$(document).on('mouseup', function() {
				maskActive = false;
				$activeHandler = null;
			});
				
		}
		
	})(jQuery);	

});