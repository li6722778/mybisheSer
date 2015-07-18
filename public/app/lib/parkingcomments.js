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
    		
        	
       	 $('#button_delete_comments').click(function(){
       		 
       		 var checked = "";
       		 $("input[name='commentsselect']:checked").each(function() {
      	            checked+=$(this).val()+",";
      	        });
      		  $( "#dialog_confirm_comments" ).data("idArray",checked).dialog( "open" );
       	 });
       	 

       	 


       	 $("#dialog_confirm_comments" ).dialog({
       	      dialogClass: 'ui-dialog-green',
       	      autoOpen: false,
       	      resizable: false,
       	      height: 210,
       	      modal: true,
       	      buttons: [
       	      	{
       	      		'class' : 'btn red',	
       	      		"text" : "删除",
       	      		click: function() {
       	      			
       	      			var pageContent = $('.page-content');
       	      			var checked = $(this).data("idArray");
       	      			var warndialog = $(this);
       	      			 if(checked.length>0){
       	      				 App.scrollTop();
       	       		    	 var pageContent = $('.page-content');
       	            		 App.blockUI(pageContent, false);
       		        		 $.post("/w/comment/delete?p="+checked, {}, function (res) {
       		                     App.unblockUI(pageContent);     		                    
       		                     warndialog.dialog( "close" );
       		                     
       		                      $('#index_comment').click();
       		                     
       		                 });
       	        	     }

       	  			}
       	      	},
       	      	{
       	      		'class' : 'btn',
       	      		"text" : "取消",
       	      		click: function() {
       	    			$(this).dialog( "close" );
       	  			}
       	      	}
       	      ]
       	    });
        	 
        	 
        	 
        	 

        	 

        	

        }

    };

}();


jQuery(document).ready(function() {    
	ParkingComments.init();
});


