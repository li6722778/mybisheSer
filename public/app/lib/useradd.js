/**
 * 停车场类
 */
var Parking = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	
        	 $('#submituserpasswdbutton').click(function(){
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
  			   

  				   $("#dataFormUserPasswd").ajaxSubmit(options);
  		   });
        	
        	 $('#submituserbutton').click(function(){
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
  			   

  				   $("#dataFormUser").ajaxSubmit(options);
  		   });
        	 
        	
        }

    };

}();



jQuery(document).ready(function() {    
	Parking.init();
});


