/**
 * 停车场类
 */
var UserList = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1_order .group-checkable').change(function () {
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

        	$('#orderlist .ajaxify').on('click', '', function (e) {
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
        	
        	
        	
        	 $('#button_delete_order').click(function(){
        		 
        		 var checked = "";
       		     $('input:checkbox:checked').each(function() {
       	            checked+=$(this).val()+",";
       	        });
       		  $( "#dialog_confirm_deleteorder" ).data("idArray",checked).dialog( "open" );
        	 });
        	 
        	 
            $('#button_exption_order').click(function(){
        		 
        		 var checked = "";
       		     $('input:checkbox:checked').each(function() {
       	            checked+=$(this).val()+",";
       	        });
       		  $( "#dialog_confirm_order" ).data("idArray",checked).dialog( "open" );
        	 });
        	 
        	 
            
            //历史停车搜索
            $('#searchOrderButtonHis').click(function(){
            	App.scrollTop();
        		 var city = $("#city_search_order").val();
       		     var key = $("#key_search_order").val();
       		     var pageContent = $('.page-content');
       		     var pageContentBody = $('.page-content .page-content-body');
       		  
    		     App.blockUI(pageContent, false);
    		     
       		  $.post("/w/orderhis?c="+city+"&f="+key, {}, function (res) {
                  App.unblockUI(pageContent);
                  pageContentBody.html(res);
                  App.fixContentHeight(); // fix content height
                  App.initUniform(); // initialize uniform elements
                 
              });
        		 
        	 });
        	 
            $('#searchOrderButton').click(function(){
            	App.scrollTop();
        		 var city = $("#city_search_order").val();
       		     var key = $("#key_search_order").val();
       		     var pageContent = $('.page-content');
       		     var pageContentBody = $('.page-content .page-content-body');
       		  
    		     App.blockUI(pageContent, false);
    		     
       		  $.post("/w/order?c="+city+"&f="+key, {}, function (res) {
                  App.unblockUI(pageContent);
                  pageContentBody.html(res);
                  App.fixContentHeight(); // fix content height
                  App.initUniform(); // initialize uniform elements
                 
              });
        		 
        	 });
        	 
       	     
        	 
        
        	 
        	 $("#dialog_confirm_order" ).dialog({
        	      dialogClass: 'ui-dialog-green',
        	      autoOpen: false,
        	      resizable: false,
        	      height: 210,
        	      modal: true,
        	      buttons: [
        	      	{
        	      		'class' : 'btn red',	
        	      		"text" : "设置异常订单",
        	      		click: function() {
        	      			var checked = $(this).data("idArray");
        	      			var warndialog = $(this);
        	      			 if(checked.length>0){
        	      				 App.scrollTop();
        	       		    	 var pageContent = $('.page-content');
        	            		 App.blockUI(pageContent, false);
        		        		 $.get("/w/order/setexception?p="+checked, function (res) {
        		        			 window.console && console.log("setexception done."+res);
        		                     App.unblockUI(pageContent);
        		                     warndialog.dialog( "close" );
        		                     $('#index_order').click();
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
        	 
        	 
        	 $("#dialog_confirm_deleteorder" ).dialog({
       	      dialogClass: 'ui-dialog-green',
       	      autoOpen: false,
       	      resizable: false,
       	      height: 210,
       	      modal: true,
       	      buttons: [
       	      	{
       	      		'class' : 'btn red',	
       	      		"text" : "删除订单",
       	      		click: function() {
       	      			var checked = $(this).data("idArray");
       	      			var warndialog = $(this);
       	      			 if(checked.length>0){
       	      				 App.scrollTop();
       	       		    	 var pageContent = $('.page-content');
       	            		 App.blockUI(pageContent, false);
       		        		 $.get("/w/order/delete?p="+checked, function (res) {
       		        			 window.console && console.log("setexception done."+res);
       		                     App.unblockUI(pageContent);
       		                     warndialog.dialog( "close" );
       		                     $('#index_order').click();
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


