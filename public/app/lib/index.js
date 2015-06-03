/**
 * 停车场类
 */
var Index = function () {
    
    return {
        //main function to initiate the module
        init: function () {

        	    
             $('.ajaxify').on('click', '', function (e) {
              e.preventDefault();
              App.scrollTop();
              var url = $(this).attr("post");
              var pageContent = $('.page-content');
              var pageContentBody = $('.page-content .page-content-body');

              App.blockUI(pageContent, false);

              $.post(url, {}, function (res) {
                      App.unblockUI(pageContent);
                      pageContentBody.html(res);
                      App.fixContentHeight(); // fix content height
                      App.initUniform(); // initialize uniform elements
                  });
             });
             
         	
        	 $('.page-sidebar .ajaxify.start').click();
        	 

        }


    };

}();

jQuery(document).ready(function() {    
	App.init();
	Index.init();
});
