/**
 * 停车场类
 */
var ParkingList = function () {
    
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
        	
        	$('#packingcontent .ajaxify').on('click', '', function (e) {
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
        	
        	
        	//search
        	$('#searchParkingButton').click(function(){
        		App.scrollTop();
      		     var key = $("#key_search_parking").val();
      		     var value = $("#value_search_parking").val();
      		   
      		     var pageContent = $('.page-content');
      		     var pageContentBody = $('.page-content .page-content-body');
      		  
   		     App.blockUI(pageContent, false);
   		     
      		  $.post("/w/parking?k="+key+"&v="+value, {}, function (res) {
                
                 pageContentBody.html(res);
                 App.fixContentHeight(); // fix content height
                 App.initUniform(); // initialize uniform elements
                 App.unblockUI(pageContent);
                
             });
       		 
       	 });
        	
        	//delete data
        	 $('#button_delete').click(function(){
        		 var checked = "";
        		 $('input:checkbox:checked').each(function() {
        	            checked+=$(this).val()+",";
        	        });
        	      
        	
        		 $( "#dialog_confirm" ).data("parkingIdArray",checked).dialog( "open" );
        		 
        		
        	 });
        	 
        	 
        	 //approve data
       	      $('#button_verify').click(function(){
       	    	var checked = "";
       		     $('input:checkbox:checked').each(function() {
       	            checked+=$(this).val()+",";
       	        });
       		    $( "#dialog_confirm_approve" ).data("parkingIdArray",checked).dialog( "open" );
       		 
        	 });
        	 
        	 
        	
        	 $('#submitbutton').click(function(){
        		 App.scrollTop();
        		 var pageContent = $('.page-content');
        		 App.blockUI(pageContent, false);
  			   var options = {
  		       	        success: function (data) {
  		       	           var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:</strong>"+data+"</label></div>"
  		       	           $("#remotemessage").html(message);
  		       	           App.unblockUI(pageContent);
  		       	        },
  			   
  			            error:function (XmlHttpRequest, textStatus, errorThrown) {
  			               var message = "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>错误:</strong>"+XmlHttpRequest.responseText+"</label></div>"
   		       	           $("#remotemessage").html(message);
  			               App.unblockUI(pageContent);
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
        	      			var parkingIdArray = $(this).data("parkingIdArray");
        	      			var warndialog = $(this);
        	      			App.blockUI(pageContent, false);
        	      			window.console && console.log("uri:"+parkingIdArray)
        	      			 $.get("/w/parking/delete?p="+parkingIdArray,function(data){
        	      				window.console && console.log("delete total:"+data)
          	      				App.unblockUI(pageContent);
          	      			    warndialog.dialog( "close" );
          	      			    $('#parking').click();
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
        	 
        	 
        	 $("#dialog_confirm_approve" ).dialog({
       	      dialogClass: 'ui-dialog-green',
       	      autoOpen: false,
       	      resizable: false,
       	      height: 210,
       	      modal: true,
       	      buttons: [
       	      	{
       	      		'class' : 'btn red',	
       	      		"text" : "审批通过",
       	      		click: function() {
       	      		    App.scrollTop();
       	      			var pageContent = $('.page-content');
       	      			var parkingIdArray = $(this).data("parkingIdArray");
       	      			var warndialog = $(this);
       	      			App.blockUI(pageContent, false);
       	      			window.console && console.log("uri:"+parkingIdArray)
       	      			
       	      			 $.get("/w/parking/approve?p="+parkingIdArray,function(data){
       	      				window.console && console.log("approve total:"+data)
         	      				App.unblockUI(pageContent);
         	      			    warndialog.dialog( "close" );
         	      			    $('#parking').click();
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
        	        
        }

    };

}();


jQuery(document).ready(function() {    
	ParkingList.init();
});


