/**
 * version
 */
var Allowance = function () {
    
    return {
        //main function to initiate the module
        init: function () {
       	
        	 $('#submitallowancebutton').click(function(){
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
  			       
  			       var allowanceTypeValue = 0;
  			       if($("#allowanceTypeXiandan").is(':checked')){
			    	 $("#allowanceType").val("1");
			    	 allowanceTypeValue = $("#allowanceTypeValueXiandan").val();
			       }else{
			    	 $("#allowanceType").val("2");
			    	 allowanceTypeValue = $("#allowanceTypeValueJiaoyiliang").val();
			       }
  			       
  			       $("#allowanceTypeValue").val(allowanceTypeValue);
  			   
    				 $("#isopen").val("0");
      				 $("input[name='isopenCheckbox']:checked").each(function() {
      					$("#isopen").val("1");
            	        });
  			       
      				var timers = "";
      				$("#my_multi_select2 option:selected").each(function(){ 
      					timers=timers+$(this).val()+",";
      				});
      				//alert("timers:"+timers);
      				$("#allowanceTimer").val();
      				 
  				   $("#dataFormAllowance").ajaxSubmit(options);
  				  

  				     				   
  		      });
        	 
        	    var handleMultiSelect = function () {
        	        $('#my_multi_select2').multiSelect({
        	            selectableOptgroup: true
        	        });        
        	    };

        	    
        	    $('.success-toggle-button').toggleButtons({
                    style: {
                        enabled: "success",
                        disabled: "info"
                    }
                });
        	    

        	    var initSelects = function(timers){
                    if(timers.length>0){
                    	$.each(timers.split(","), function(i,e){
                    		if(e.length>0){
                    	       $("#my_multi_select2 option[value='" + e + "']").prop("selected", true);
                    		}
                    	});
                    }
        	    }
        	    
        	    
        	    initSelects($("#allowanceTimer").val());
        	    
        	    handleMultiSelect();
        }

    };

}();


jQuery(document).ready(function() {    
	Allowance.init();
});


