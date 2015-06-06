/**
 * 停车场类
 */
var Parking = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	var handleTimePickers = function () {
                
                if (jQuery().timepicker) {
                    $('.timepicker-default').timepicker();
                    $('.timepicker-24').timepicker({
                        minuteStep: 1,
                        showSeconds: true,
                        showMeridian: false
                    });
                }
            }
        	
        	$('#parkingtitle .ajaxify').on('click', '', function (e) {
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
        	
        	
        	 $('#submitbutton').click(function(){
        		 App.scrollTop();
        		 var pageContent = $('.page-content');
        		 App.blockUI(pageContent, false);
  			   var options = {
  		       	        success: function (data) {
  		       	          App.unblockUI(pageContent);
  		       	           var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:</strong>"+data+"</label></div>"
  		       	           $("#remotemessage").html(message);
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
        	 
        	 $("#dialog_confirm" ).dialog({
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
        	      			App.scrollTop();
        	      			var pageContent = $('.page-content');
        	      			var imgId = $(this).data("imgId");
        	      			var warndialog = $(this);
        	      			 App.blockUI(pageContent, false);
        	      			 
        	      			 $.get("/a/image/delete/"+imgId,function(){
        	      				var imgItem = $('#item'+imgId);
        	      				imgItem.remove();
        	      				warndialog.dialog( "close" );
        	      				App.unblockUI(pageContent);
        	      			});

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
        	        
        	 handleTimePickers();
        }

    };

}();

function deleteRemoteImage(imgId){
    //confirm dialog
	$( "#dialog_confirm" ).data("imgId",imgId).dialog( "open" );
}

jQuery(document).ready(function() {    
	Parking.init();
	FormFileUpload.init();
});


