/**
 * 评论类
 */
var ParkingComments = function () {
    
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
        	
    		$('#parkingprodcontent .ajaxify').on('click', '', function (e) {
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
        	$('#searchParkingcommentsButton').click(function(){
        		App.scrollTop();
      		     var key = $("#key_search_parkingcomments").val();
      		     var value = $("#value_search_parkingcomments").val();
      		     var pageContent = $('.page-content');
      		     var pageContentBody = $('.page-content .page-content-body');
      		  
   		     App.blockUI(pageContent, false);
   		     
      		  $.post("/w/comment?k="+key+"&v="+value, {}, function (res) {
                 App.unblockUI(pageContent);
                 pageContentBody.html(res);
                 App.fixContentHeight(); // fix content height
                 App.initUniform(); // initialize uniform elements
                
             });
       		 
       	 });
    		

       	 

        //delete
       	 
      	 $('#button_delete_comment').click(function(){
    		 var checked = "";
    		 $('input:checkbox:checked').each(function() {
    	            checked+=$(this).val()+",";
    	        });
    		 var p = $(this).attr("p");
    		 var s = $(this).attr("s");
    		 var o = $(this).attr("o");
    		 var k = $(this).attr("k");
    		 var v = $(this).attr("v");
    		 
    		 var pageContent = $('.page-content');
  		     var pageContentBody = $('.page-content .page-content-body');
  		     
  		   App.blockUI(pageContent, false);
    		 $.get("/w/comment/deletes?i="+checked+"&p="+p+"&s="+s+"&o="+o+"&k="+k+"&v="+v,function(data){
      				
      				pageContentBody.html(data);
                    App.fixContentHeight(); // fix content height
                    App.initUniform(); // initialize uniform elements
                    App.unblockUI(pageContent);
      			});
    	 });
        	 
        	 
        	 

        	 

        	

        }

    };

}();


jQuery(document).ready(function() {    
	ParkingComments.init();
});


