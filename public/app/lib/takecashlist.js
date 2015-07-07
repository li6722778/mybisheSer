/**
 * 停车场类
 */
var UserList = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1_takecash .group-checkable').change(function () {
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

        	$('#takecashlist .ajaxify').on('click', '', function (e) {
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
        	
        	
        	 $('.button_update_cash_status').click(function(){
       	    	 var type = jQuery(this).attr("type");
       	    	 
       	    	 var currentPage = jQuery(this).attr("currentPage");
       	    	 
       	    	 var checked = "";
      		     $("input[name='cashselect']:checked").each(function() {
      	            checked+=$(this).val()+",";
      	        });

      		   if (type == null) { 
     			   type = 1;
     		   }
     		   
      		     if(checked.length>0){
      		    	App.scrollTop();
      		    	 var pageContent = $('.page-content');
      		    	var pageContentBody = $('.page-content .page-content-body');
           		     App.blockUI(pageContent, false);
           		 
	        		 $.post("/w/takecash/update/"+type+"?p="+currentPage+"&pid="+checked, {}, function (res) {
	                     pageContentBody.html(res);
	                        App.fixContentHeight(); // fix content height
	                        App.initUniform(); // initialize uniform elements
	                        App.unblockUI(pageContent);
	                    
	                 });
       	     }
       	    	
        	 });
       
        	        
        }

    };

}();

function deleteRemoteImage(imgId){
    //confirm dialog
	$( "#dialog_confirm" ).data("imgId",imgId).dialog( "open" );
}

jQuery(document).ready(function() {    
	UserList.init();
});


