/**
 * smspush
 */
var smspush = function () {
    
    return {
        //main function to initiate the module
        init: function () {

        	
        	$("#issendsmsCheckbox").change(function(e) { 		
        	    App.scrollTop();
                var pageContent = $('.page-content');
                App.blockUI(pageContent, false);
        		if($("#issendsmsCheckbox"	).prop("checked"))
        			{
        		       $.get("/w/changesmspushoption?t=1", function(data){
                           App.unblockUI(pageContent);
                           var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:开启短信推送</strong></label></div>"
                           $("#remotemessage").html(message);
                            });
        			}
        		else{
        		       $.get("/w/changesmspushoption?t=0", function(data){
                        App.unblockUI(pageContent);
                            var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:关闭短信推送</strong></label></div>"
                            $("#remotemessage").html(message);
                           
                         });
        		}
        		
        	});
        	
        	
        	
        $('#submitOptionsbutton').click(function(){
     	    App.scrollTop();
            var pageContent = $('.page-content');
            App.blockUI(pageContent, false);
    		if($("#issendsmsCheckbox"	).prop("checked"))
    			{
    		       $.get("/w/smspush?m=1", function(data){
                       App.unblockUI(pageContent);
                       var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:短信推送发送中</strong></label></div>"
                       $("#remotemessage").html(message);
                        });
    			}
    		else{
    		       $.get("/w/smspush?m=0", function(data){
                    App.unblockUI(pageContent);
                        var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:短信推送发送失败</strong></label></div>"
                        $("#remotemessage").html(message);
                       
                     });
    		}
  		   });
        	
        	
        	  $('.success-toggle-button').toggleButtons({
                  style: {
                      enabled: "success",
                      disabled: "info"
                  }
              });
      	    
        }

    };

}();



jQuery(document).ready(function() {    
	smspush.init();
});


