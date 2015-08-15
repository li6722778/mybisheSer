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
			       }else if($("#forceUpdateQz").is(':checked')){
			    	 $("#forceUpdate").val("1");
			       }else{
			    	  $("#forceUpdate").val("3");
			       }
  			   
	  			    var timers = "";
	   				$("#role_multi_select option:selected").each(function(){ 
	   					timers=timers+$(this).val()+",";
	   				});
	   				
	   				$("#downloadTarget").val(timers);
  			       
  				   $("#dataFormVersion").ajaxSubmit(options);
  		   });
        	 
        	 
        	 var handleMultiSelect = function () {
     	        $('#role_multi_select').multiSelect();        
     	    };
        	 
        	 
        	 $('#addbutton').click(function(){
        		 
        		 $('#addfile').append("<input type=\"file\" name='number' multiple>");
        		 
  		   });
        	 
        	 
     	    var initSelects = function(timers){
     	    	
                if(timers.length>0){
                	$.each(timers.split(","), function(i,e){
                		if(e.length>0){
                			//alert("selected:"+e);
                	       $("#role_multi_select option[value='" + e + "']").prop("selected", true);
                		}
                	});
                }
    	    }
    	    
     	    $('#cancelAll').click(function(){
     	    	var selectedIndexs = $("#downloadTarget").val();
     	    	
     	    	$('#role_multi_select').multiSelect("deselect_all");  
     	    	 
     	    	$("#downloadTarget").val("");
     	    });
     	    
    	    
    	    initSelects($("#downloadTarget").val());
    	    handleMultiSelect();
        }

    };

}();



jQuery(document).ready(function() {    
	Version.init();
});


