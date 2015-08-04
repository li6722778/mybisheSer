/**
 * 停车场类
 */
var CouponList = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1_couponlist .group-checkable').change(function () {
                    var set = jQuery(this).attr("data-set");
                    var checked = jQuery(this).is(":checked");
                    jQuery(set).each(function () {
                        if (checked) {
                            $(this).attr("checked", true);
                        } else {
                            $(this).attr("checked", false);
                        }
                    });
                    jQuery.uniform.update(set);
                });
            }

        	$('#couponlist .ajaxify').on('click', '', function (e) {
                e.preventDefault();
                App.scrollTop();
                var url = $(this).attr("post");
                var pageContent = $('.page-content');
                var pageContentBody = $('.page-content .page-content-body');

                App.blockUI(pageContent, false);
                window.console && console.log("post url:"+url);
                $.post(url, {}, function (res) {
                        
                        pageContentBody.html(res);
                        App.fixContentHeight(); // fix content height
                        App.initUniform(); // initialize uniform elements
                        App.unblockUI(pageContent);
                    });
               });
 	 
            $('#searchCouponButton').click(function(){
            	App.scrollTop();
       		     var key = $("#key_search_coupon").val();
       		     var pageContent = $('.page-content');
       		     var pageContentBody = $('.page-content .page-content-body');
       		  
    		     App.blockUI(pageContent, false);
    		     
       		  $.post("/w/coupon/used?f="+key, {}, function (res) {
                 
                  pageContentBody.html(res);
                  App.fixContentHeight(); // fix content height
                  App.initUniform(); // initialize uniform elements
                  App.unblockUI(pageContent);
                 
              });
        		 
        	 });

        	     
        }

    };

}();


jQuery(document).ready(function() {    
	CouponList.init();
});


