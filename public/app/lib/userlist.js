/**
 * 停车场类
 */
var UserList = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1 .group-checkable').change(function () {
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

        	$('#userlist .ajaxify').on('click', '', function (e) {
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
        	
        	
        	
        	 $('#button_delete_user').click(function(){
        		 
        		 var checked = "";
        		 var type = jQuery(this).attr("type");
       		     $('input:checkbox:checked').each(function() {
       	            checked+=$(this).val()+",";
       	        });
       		     
        		 
       		  $( "#dialog_confirm_user" ).data("idArray",checked).data("type",type).dialog( "open" );
       		     
       		    
        		 
        	 });
        	 
            $('#searchUserButton').click(function(){
        		 
        		 var type = jQuery(this).attr("type");
       		     var key = $("#key_search_user").val();
       		     var pageContent = $('.page-content');
       		     var pageContentBody = $('.page-content .page-content-body');
       		  
    		     App.blockUI(pageContent, false);
    		     
       		  $.post("/w/user?t="+type+"&f="+key, {}, function (res) {
                  App.unblockUI(pageContent);
                  pageContentBody.html(res);
                  App.fixContentHeight(); // fix content height
                  App.initUniform(); // initialize uniform elements
                 
              });
        		 
        	 });
        	 
       	      $('.button_update_user').click(function(){
       	    	 var type = jQuery(this).attr("type");
       	    	 var checked = "";
      		     $('input:checkbox:checked').each(function() {
      	            checked+=$(this).val()+",";
      	        });
       		 
      		     if(checked.length>0){
      		    	 var pageContent = $('.page-content');
           		     App.blockUI(pageContent, false);
           		 
	        		 $.post("/w/user/update?t="+type+"&p="+checked, {}, function (res) {
	                     App.unblockUI(pageContent);
	                     if(type>=20&&type<30){
	                    	 $('#userlist20').click();
	                     }else if(type>=30&&type<40){
	                    	 $('#userlist30').click();
	                     } else{
	                    	 $('#userlist10').click();
	                     }
	                    
	                 });
       	     }
       	    	
        	 });
        	 
        	 
        
        	 
        	 $("#dialog_confirm_user" ).dialog({
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
        	      			var type = $(this).data("type");
        	      			var warndialog = $(this);
        	      			 App.blockUI(pageContent, false);
        	      			
        	      			 if(checked.length>0){
        	       		    	 var pageContent = $('.page-content');
        	            		 App.blockUI(pageContent, false);
        		        		 $.post("/w/user/delete?p="+checked, {}, function (res) {
        		                     App.unblockUI(pageContent);
        		                    
        		                     warndialog.dialog( "close" );
        		                     if(type>=20&&type<30){
        		                    	 $('#userlist20').click();
        		                     }else if(type>=30&&type<40){
        		                    	 $('#userlist30').click();
        		                     } else{
        		                    	 $('#userlist10').click();
        		                     }
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

function deleteRemoteImage(imgId){
    //confirm dialog
	$( "#dialog_confirm" ).data("imgId",imgId).dialog( "open" );
}

jQuery(document).ready(function() {    
	UserList.init();
});


