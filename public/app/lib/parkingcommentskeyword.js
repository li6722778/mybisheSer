/**
 * 关键字类
 */
var ParkingCommentskeyword = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1_prod .group-checkable').change(function () {
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
        	
    		$('#pagination .ajaxify').on('click', '', function (e) {
                e.preventDefault();
                App.scrollTop();
                var url = $(this).attr("post");
                var pageContent = $('.page-content');
                var pageContentBody = $('.page-content .page-content-body');
                App.blockUI(pageContent, false);

                window.console && console.log("post url:"+url);
                
                $.post(url, {}, function (res) {
                        App.unblockUI(pageContent);
                        pageContentBody.html(res);
                        App.fixContentHeight(); // fix content height
                        App.initUniform(); // initialize uniform elements
                    });
               });

    		//search
        	$('#searchKeywordButton').click(function(){
        		App.scrollTop();
      		     var value = $("#value_search_keyword").val();
      		     var pageContent = $('.page-content');
      		     var pageContentBody = $('.page-content .page-content-body');	  
   		     App.blockUI(pageContent, false);
   		     
      		  $.post("/w/commentskeyword?v="+value, {}, function (res) {
                 App.unblockUI(pageContent);
                 pageContentBody.html(res);
                 App.fixContentHeight(); // fix content height
                 App.initUniform(); // initialize uniform elements
                
             });
       		 
       	 });
    		
        	
        	
        	
        	
    		//add
        	$('#addKeywordButton').click(function(){
        		App.scrollTop();
      		     var value = $("#value_add_keywords").val();
      		     var pageContent = $('.page-content');
      		     var pageContentBody = $('.page-content .page-content-body');	  
   		     App.blockUI(pageContent, false);
   		     
      		  $.post("/w/commentskeyword/add?k="+value, {}, function (res) {
                 App.unblockUI(pageContent);
                 pageContentBody.html(res);
                 App.fixContentHeight(); // fix content height
                 App.initUniform(); // initialize uniform elements
                
             });
       		 
       	 });

        //delete
       	 
      	 $('#button_delete_keyword').click(function(){
    		 var checked = "";
    		 $('input:checkbox:checked').each(function() {
    	            checked+=$(this).val()+",";
    	        });
    		 
    		 if(checked.length>0)
    		 {
    			  
    			 var p = $(this).attr("p");
        		 var s = $(this).attr("s");
        		 var o = $(this).attr("o");
        		 var v = $(this).attr("v");
        		 
        		 var pageContent = $('.page-content');
      		     var pageContentBody = $('.page-content .page-content-body');
      		     
      		   App.blockUI(pageContent, false);
        		 $.get("/w/commentskeyword/deletes?i="+checked+"&p="+p+"&s="+s+"&o="+o+"&v="+v,function(data){    				
          				pageContentBody.html(data);
                        App.fixContentHeight(); // fix content height
                        App.initUniform(); // initialize uniform elements
                        App.unblockUI(pageContent);
          			});
    		 }
    	
    	 });
        	 
        	 
      	 

        	 

        	

        }

    };

}();


jQuery(document).ready(function() {    
	ParkingCommentskeyword.init();
});


