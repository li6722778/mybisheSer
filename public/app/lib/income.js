/**
 * 停车场类
 */
var Income = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	$('#searchIncomeButton').click(function(){
        		App.scrollTop();
        		 var type = jQuery(this).attr("type");
        		     var key = $("#key_search_income").val();
        		     var pageContent = $('.page-content');
        		     var pageContentBody = $('.page-content .page-content-body');
        		  
        	     App.blockUI(pageContent, false);
        	     
        		  $.post("/w/income?f="+key, {}, function (res) {
        	     
        	      pageContentBody.html(res);
        	      App.fixContentHeight(); // fix content height
        	      App.initUniform(); // initialize uniform elements
        	      App.unblockUI(pageContent);
        	     
        	  });
        		 
        	 });
        	
        	$('#incomereload').on('click', '', function (e) {
        	    e.preventDefault();
        	    var url = $(this).attr("post");
        	    window.console && console.log("post url:"+url);
        	    $.post(url, {}, function (res) {
        	            pageContentBody.html(res);
        	            App.fixContentHeight(); // fix content height
        	            App.initUniform(); // initialize uniform elements
        	        });
        	   });
        	
        	 
        	$('#incomedetail .ajaxify,#incomepagination .ajaxify,#incomebreadcrumb .ajaxify,#incomelist .ajaxify').on('click', '', function (e) {
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
        	        
        }

    };

}();

function deleteRemoteImage(imgId){
    //confirm dialog
	$( "#dialog_confirm" ).data("imgId",imgId).dialog( "open" );
}

jQuery(document).ready(function() {    
	Income.init();
});


