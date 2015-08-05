/**
 * version
 */
var Version = function () {
    
    return {
        // main function to initiate the module
        init: function () {
        	
        	 $('#submitversionbutton').click(function(){
        		 App.scrollTop();
        		 var pageContent = $('.page-content');
        		 App.blockUI(pageContent, false);
  			     var options = {
  		       	        success: function (data) {
  		       	          App.unblockUI(pageContent);
  		       	           var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:</strong>"+data+"</label></div>"
  		       	           $("#remotemessage").html(message);
  		       	           
  		       	           $('#userlist').click();
  		       	           
  		       	        },
  			   
  			            error:function (XmlHttpRequest, textStatus, errorThrown) {
  			            	 App.unblockUI(pageContent);
  			               var message = "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>错误:</strong>"+XmlHttpRequest.responseText+"</label></div>"
   		       	           $("#remotemessage").html(message);
   		       	        }
  		       	    };
  			  
  			       if($("#forceUpdateTs").is(':checked')){
			    	 $("#forceUpdate").val("0");
			       }else{
			    	 $("#forceUpdate").val("1");
			       }
  			   
  				   $("#dataFormVersion").ajaxSubmit(options);
  		   });
        	 
        	 
        	 
        	 $('#addbutton').click(function(){
        		 
        		 $('#addfile').append("<input type=\"file\" name='number' multiple>");
        		 
  		   });
        	 
        	 
        	 
        	 
        	 
        	 
        	 
        	
        }

    };

}();



jQuery(document).ready(function() {    
	Version.init();
});


