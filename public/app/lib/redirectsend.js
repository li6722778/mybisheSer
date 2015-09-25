/**
 * redirect
 */
var redirectsend = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	
        	 $('#addphone').click(function(){	
        		 checkSubmitMobil();
  		   });
        	 
        	 
        	 $("#telephonelist").dblclick(function(){
        		 $("#telephonelist option:selected").remove();  
        		});
        	 
      
 
        	function checkSubmitMobil() { 
        	var tel = $("#telephone").val(); //获取手机号
        	var telReg = !!tel.match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/);
       	    App.scrollTop();
		    var pageContent = $('.page-content');
		    App.blockUI(pageContent, false);
        	//如果手机号码不能通过验证
        	if(telReg == false){ 
        	 	 App.unblockUI(pageContent);
	             var message = "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>错误:请输入正确的手机号码</strong></label></div>"
	       	     $("#remotemessage").html(message);
                 $('#telephone').attr("value","");
        	}
        	//手机验证通过，判断手机号是否重复
        	else{
        		
    		    var arr = new Array(); //数组定义标准形式，不要写成Array arr = new Array();
    	         $("#telephonelist option").each(function () {
    	             var val = $(this).val(); //获取单个value
    	             var node = val;
    	             arr.push(node);
    	         });
    		 var telephone=$('#telephone').val();
    		 var result=in_array(telephone, arr);
    		 if(result==true)
    			 {
    		 	   App.unblockUI(pageContent);
	               var message = "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>错误:请勿重复输入</strong></label></div>"
	       	       $("#remotemessage").html(message);
    			   $('#telephone').attr("value","");
    			 }
    		 else if(result==false)
    			 {
    	 	   	 App.scrollTop();  
    			  $("#telephonelist").append(" <option value='"+telephone+"'>"+telephone+"</option>");
    			  App.unblockUI(pageContent);
      	           var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:添加手机号成功</strong></label></div>"
      	           $("#remotemessage").html(message);
        		   $('#telephone').attr("value","");
      
    			 }
        	 }
        	}
        	
        	
        	function in_array(needle, haystack) {
        		  var i = 0, n = haystack.length;

        		  for (;i < n;++i)
        		    if (haystack[i] === needle)
        		      return true;

        		  return false;
        		}
        	
        	
        	$("#issendsmsCheckbox").change(function(e) { 		
        	    App.scrollTop();
                var pageContent = $('.page-content');
                App.blockUI(pageContent, false);
        		if($("#issendsmsCheckbox"	).prop("checked"))
        			{
        		       $.get("/w/changesmssendoption?t=1", function(data){
                           App.unblockUI(pageContent);
                           var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:开启短信推送</strong></label></div>"
                           $("#remotemessage").html(message);
                            });
        			}
        		else{
        		    $.get("/w/changesmssendoption?t=0", function(data){
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
            var arr = new Array(); //数组定义标准形式，不要写成Array arr = new Array();
	         $("#telephonelist option").each(function () {
	             var val = $(this).val(); //获取单个value
	             var node = val;
	             arr.push(node);
	         });
	         if(arr=="")
	        	 {
	        	   App.unblockUI(pageContent);
	               var message = "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>错误:手机号列表不能为空</strong></label></div>"
	       	       $("#remotemessage").html(message);
	        	 }
	         else{
	             $.get("/w/redirectsendphonelist?p="+arr, function(data){
	                 App.unblockUI(pageContent);
	                 var message = "<div class=\"alert alert-success\"><button class=\"close\" data-dismiss=\"alert\"></button><label class=\"control-label\" id=\"errorMessage\"><strong>成功:对指定用户发送优惠券</strong></label></div>"
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
	redirectsend.init();
});


