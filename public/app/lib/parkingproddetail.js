/**
 * 停车场类
 */
var ParkingProdDetail = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	var handleTimePickers = function () {
                
                if (jQuery().timepicker) {
                    $('.timepicker-default').timepicker();
                  
                }
            }
        	
			$('#parkingdetailprod .ajaxify').on('click', '', function (e) {
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
        	
        	
        	 $('#submitpricebutton').click(function(){
        		 App.scrollTop();
        		 var pageContent = $('.page-content');
        		 var pageContentBody = $('.page-content .page-content-body');
        		 App.blockUI(pageContent, false);
        	
  			     var options = {
  		       	        success: function (data) {
  		       	          App.unblockUI(pageContent);
  		       	           var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:</strong>"+data+"</label></div>"
  		       	           $("#remotemessage").html(message);
  		       	 	        
  		       	           var pid = $("#parkId").val();
			  		       	 $.post("/w/parkingprod/"+pid, {}, function (res) {
				                    pageContentBody.html(res);
				                    App.fixContentHeight(); // fix content height
				                    App.initUniform(); // initialize uniform elements
				                });
  		       	        },
  			   
  			            error:function (XmlHttpRequest, textStatus, errorThrown) {
  			            	 App.unblockUI(pageContent);
  			               var message = "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>错误:</strong>"+XmlHttpRequest.responseText+"</label></div>"
   		       	           $("#remotemessage").html(message);
   		       	        }
  		       	    };
  			   
  			       if($("#feeTypeSec").is(':checked')){
  			    	 $("#feeType").val("1");
  			       }else{
  			    	 $("#feeType").val("0");
  			       }
  			       
  			       if($("#isDiscountAllday").is(':checked')){
  			    	 $("#isDiscountAllday").val("1");
  			       }else{
  			    	 $("#isDiscountAllday").val("0");
  			       }
  			       
			       if($("#isDiscountSec").is(':checked')){
	  			    	 $("#isDiscountSec").val("1");
	  			   }else{
	  			    	 $("#isDiscountSec").val("0");
	  			   }

  				   $("#dataForm").ajaxSubmit(options);
  		   });
        	   	        
        	 handleTimePickers();
        }

    };

}();


jQuery(document).ready(function() {    
	ParkingProdDetail.init();
});


