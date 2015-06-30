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
        	
       	 /**
       	  * 生成二维码
       	  */
          	 $('#button_qrimage_coupon').click(function(){
          		$("#site_activities_loading_coupon").css("display","");
          	    $("#qrcouponpreview").html("");
       		 var checked = "";
       		 $('input:checkbox:checked').each(function() {
       	            checked+=$(this).val()+",";
       	        });
	       		 var w = $("#qrcwidth").val();
	       		 var h = $("#qrcheight").val();

       		 $.get("/w/scan/gencouponqr?p="+checked+"&w="+w+"&h="+h,function(data){
		       			 $("#qrcouponpreview").html(data);
		       			 $("#site_activities_loading_coupon").css("display","none");
	      			});
       	 });
          	 
        	 $('#button_openclose_coupon').click(function(){
        		 var checked = "";
        		 $('input:checkbox:checked').each(function() {
        	            checked+=$(this).val()+",";
        	        });
        		 var p = $(this).attr("p");
        		 var s = $(this).attr("s");
        		 var o = $(this).attr("o");
        		 var f = $(this).attr("f");
        		 
        		 var pageContent = $('.page-content');
      		     var pageContentBody = $('.page-content .page-content-body');
      		     
      		   App.blockUI(pageContent, false);
        		 $.get("/w/coupon/open?p="+p+"&s="+s+"&o="+o+"&f="+f+"&pid="+checked,function(data){
	      				
	      				pageContentBody.html(data);
	                    App.fixContentHeight(); // fix content height
	                    App.initUniform(); // initialize uniform elements
	                    App.unblockUI(pageContent);
	      			});
        	 });
          	 
        	
        	 $('#button_delete_coupon').click(function(){
        		 
        		 var checked = "";
        		 $("input[name='couponselect']:checked").each(function() {
       	            checked+=$(this).val()+",";
       	        });
       		  $( "#dialog_confirm_coupon" ).data("idArray",checked).dialog( "open" );
        	 });
        	 

        	 
            $('#searchCouponButton').click(function(){
            	App.scrollTop();
       		     var key = $("#key_search_coupon").val();
       		     var pageContent = $('.page-content');
       		     var pageContentBody = $('.page-content .page-content-body');
       		  
    		     App.blockUI(pageContent, false);
    		     
       		  $.post("/w/coupon?f="+key, {}, function (res) {
                 
                  pageContentBody.html(res);
                  App.fixContentHeight(); // fix content height
                  App.initUniform(); // initialize uniform elements
                  App.unblockUI(pageContent);
                 
              });
        		 
        	 });

        	 $("#dialog_confirm_coupon" ).dialog({
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
        		        		 $.post("/w/coupon/delete?p="+checked, {}, function (res) {
        		                     App.unblockUI(pageContent);
        		                    
        		                     warndialog.dialog( "close" );
        		                     
        		                      $('#index_coupon').click();
        		                     
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
	CouponList.init();
});


