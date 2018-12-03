define(function(require){
	var jQuery = require('jquery');

(function($){
	'use strict';
	$.fn.extend({
		bootstrapPageBar: function(options){
			var opts = $.extend({}, $.fn.bootstrapPageBar.defaults, options);
			var $this = this;
			var pageNumArr = buildPageNumArr(opts);
			var $ul = buildPageBtnUl(pageNumArr, opts);
			$ul.addClass('pagination ' + (opts.paginationCls || ''));
			$ul.css(opts.paginationCss || {});
			$this.append($ul);
		}
	});
	$.fn.bootstrapPageBar.defaults = {
		curPageNum: 1,
		totalPageNum: 1,
		pageSize: 20,	// 没有用了
		maxBtnNum: 15,
		siblingBtnNum: 2,
		paginationCls: '',
		click: function(idx, pageNum){
			return false;
		}
	};
	
	function buildPageBtnUl(pageNumArr, opts){
		var $ul = $('<ul></ul>');
		
		for (var i=0, l=pageNumArr.length; i<l; i++) {
			if (i > 0 && pageNumArr[i] - pageNumArr[i-1] > 1){
				var $li = $('<li class="disabled"><a href="javascript:void(0);">...</a></li>');
				$ul.append($li);
			}
			var $li = buildPageBtnLi(i, pageNumArr[i], opts.curPageNum == pageNumArr[i], opts.click);
			$ul.append($li);
		}
		
		$ul.prepend(buildPageBtnLi(-1, opts.curPageNum-1, opts.curPageNum <= 1, opts.click, '&lt;'));
		$ul.prepend(buildPageBtnLi(-2, 1, opts.curPageNum <= 1, opts.click, '&lt;&lt;'));
		$ul.append(buildPageBtnLi(-1, opts.curPageNum + 1, opts.curPageNum >= opts.totalPageNum, opts.click, '&gt;'));
		$ul.append(buildPageBtnLi(-2, opts.totalPageNum, opts.curPageNum >= opts.totalPageNum, opts.click, '&gt;&gt;'));
		
		return $ul;
	}
	
	function buildPageBtnLi(i, pageNum, disabled, callbackFn, content) {
		var $li = $('<li></li>');
		$li.append('<a href="javascript:void(0);">' + (content ||pageNum) + '</a>');
		if (disabled){
			$li.addClass('disabled');
		} else {
			$li.click(function(){
				callbackFn(i, pageNum);
			});
		}
		return $li;
	}
	
	function buildPageNumArr(opts){
		if (opts.totalPageNum < 1) {
			return [];
		}
		opts.curPageNum < 1 && (opts.curPageNum = 1);
		opts.curPageNum > opts.totalPageNum && (opts.curPageNum = opts.totalPageNum);
		
		var curPageNum = opts.curPageNum,
			totalPageNum = opts.totalPageNum,
			maxBtnNum = opts.maxBtnNum,
			siblingBtnNum = opts.siblingBtnNum;
		if (siblingBtnNum * 2 > maxBtnNum) {
			siblingBtnNum = Math.max(Math.round(maxBtnNum / 2) - 1, 1);
		}
		
		var pageNumArr = [];
		
		if (totalPageNum > maxBtnNum) {
			var centerLeftPageNum = curPageNum > siblingBtnNum
					? curPageNum - siblingBtnNum: 1;
			var centerRightPageNum = curPageNum + siblingBtnNum < totalPageNum
					? curPageNum + siblingBtnNum: totalPageNum;
			
			var centerBtnNum = centerRightPageNum - centerLeftPageNum + 1;
			
			for (var i = centerLeftPageNum; i<= centerRightPageNum; i++) {
				pageNumArr.push(i);
			}
			
			var leftBtnNum = 0, rightBtnNum = 0;	// 最左侧的按钮， 最右侧的按钮
			var remainedBtnNum = Math.max(maxBtnNum - centerBtnNum, 0);
			
			if (remainedBtnNum > 0){
				leftBtnNum = rightBtnNum = Math.floor(remainedBtnNum / 2);
				// 有可能比maxBtnNum少1， 无所谓了
				var leftOffset = Math.min(centerLeftPageNum - 1, leftBtnNum);
				for (var i=1; i<=leftBtnNum; i++){
					pageNumArr.push(i<centerLeftPageNum? i: i+centerRightPageNum - leftOffset);
				}
				var rightOffset = Math.max(totalPageNum - rightBtnNum + 1, centerRightPageNum + 1);
				for (var i = totalPageNum; i> totalPageNum - rightBtnNum; i--) {
					pageNumArr.push(i > centerRightPageNum? i: i - rightOffset + centerLeftPageNum);
				}
			}
		}else {
			for (var i=1; i<=totalPageNum; i++){
				pageNumArr.push(i);
			}
		}
		pageNumArr.sort(function(v1, v2){
			return v1 - v2;
		});
		if (pageNumArr[0] > 1) {
			pageNumArr = [1].concat(pageNumArr);
		}
		if (pageNumArr[pageNumArr.length - 1] < totalPageNum) {
			pageNumArr.push(totalPageNum);
		}
		return pageNumArr;
	}
})(jQuery);

});
