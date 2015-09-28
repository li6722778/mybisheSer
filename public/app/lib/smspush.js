/**
 * smspush
 */
var smspush = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	
        	
        	 $('#addmodel').click(function(){	
        		 App.scrollTop();
                 var pageContent = $('.page-content');
                 App.blockUI(pageContent, false);
        		 var modelcontext=$('#model_context').val();
        		 var modelcx=$.trim(modelcontext);
        		 var modelid=$('#model_id').val();	 
        	      $.get("/w/savesmspushmodel?i="+modelid+"&c="+modelcx, function(data){
        	    	  if(data=="false")
        	    		  {
        	    		     App.unblockUI(pageContent);
        		             var message = "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>错误:短信模板已存在</strong></label></div>"
        		       	     $("#remotemessage").html(message);
        	    		  }
        	    	  else{
        	    		     App.unblockUI(pageContent);
                             var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:添加新模板请刷新查看</strong></label></div>"
                             $("#remotemessage").html(message);
        	    	  }
                 
                       });
  		   });

        	
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
    	    var modelvalue=$('input:radio[name="model"]:checked').val();
            var pageContent = $('.page-content');
            App.blockUI(pageContent, false);
    		       $.get("/w/smspush?m="+modelvalue, function(data){
                       App.unblockUI(pageContent);
                       var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:短信推送发送中...</strong></label></div>"
                       $("#remotemessage").html(message);
                        });
  		   });

        
        $(document).ready(function(){  
        	$("input:radio[name=model]:first").prop("checked", "checked");
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


