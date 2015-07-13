/**
 * options
 */
var Option = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	$('#selOption').change(function(){ 
        		var selectValue = $("#selOption").val();
        		//alert("selected:"+selectValue);
        		
 	            var url = "/w/options/"+selectValue;
 	            var pageContent = $('.page-content');
 	            var pageContentBody = $('.page-content .page-content-body');

 	            App.blockUI(pageContent, false);
 	            window.console && console.log("get url:"+url);
 	            $.get(url, function (res) {
 	                    pageContentBody.html(res);
 	                    App.fixContentHeight(); // fix content height
 	                    App.initUniform(); // initialize uniform elements
 	                    App.unblockUI(pageContent);
 	                });
 	            
        	});
        	
     
        	
        	 $('#submitOptionsbutton').click(function(){
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
  			     
  			       var cont = $("#option_context").val();
  			       var selectValue = $("#selOption").val();
  			   
  			       if($("#typecomm").is(':checked')){
			    	 $("#longTextObject").val("");
			    	 $("#textObject").val(cont);
			       }else{
			    	   $("#textObject").val("");
			    	   $("#longTextObject").val(cont);
			       }
  			       
  			     $("#optionType").val(selectValue);
  			   
  				 $("#dataFormOptions").ajaxSubmit(options);
  		   });
        	 
        	
        }

    };

}();



jQuery(document).ready(function() {    
	Option.init();
});


