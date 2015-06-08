/**
 * 停车场类
 */
var UserList = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1_userlist .group-checkable').change(function () {
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

        	$('#userlist .ajaxify').on('click', '', function (e) {
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
        	
        	
        	
        	 $('#button_delete_user').click(function(){
        		 
        		 var checked = "";
        		 var type = jQuery(this).attr("type");
        		 $("input[name='userselect']:checked").each(function() {
       	            checked+=$(this).val()+",";
       	        });
       		  $( "#dialog_confirm_user" ).data("idArray",checked).data("type",type).dialog( "open" );
        	 });
        	 
        	 
        	 $('#button_reset_passwd').click(function(){
        		 var checked = "";
        		 $("input[name='userselect']:checked").each(function() {
      	            checked+=$(this).val()+",";
      	        });
      		     
      		   $.post("/w/user/gotomulitpasswd?p="+checked, {}, function (res) {
      			 var pageContent = $('.page-content');
       		     var pageContentBody = $('.page-content .page-content-body');
                   App.unblockUI(pageContent);
                   pageContentBody.html(res);
                   App.fixContentHeight(); // fix content height
                   App.initUniform(); // initialize uniform elements
                  
               });
        	 });
        	 
            $('#searchUserButton').click(function(){
            	App.scrollTop();
        		 var type = jQuery(this).attr("type");
       		     var key = $("#key_search_user").val();
       		     var pageContent = $('.page-content');
       		     var pageContentBody = $('.page-content .page-content-body');
       		  
    		     App.blockUI(pageContent, false);
    		     
       		  $.post("/w/user?t="+type+"&f="+key, {}, function (res) {
                  App.unblockUI(pageContent);
                  pageContentBody.html(res);
                  App.fixContentHeight(); // fix content height
                  App.initUniform(); // initialize uniform elements
                 
              });
        		 
        	 });
        	 
            //弹出popup设值
            $('#button_update_user_selected').click(function(){
       		  var checked = "";
     		    $("input[name='parkingselect']:checked").each(function() {
     	            checked+=$(this).val()+",";
     	        });
     		    
     		   var person = "";
    		     $("input[name='userselect']:checked").each(function() {
    		    	 person+=$(this).val()+",";
    	        });
     		    
     		   var type = $('#popuptype').val();
     		   
     		   // alert("select:"+checked+",type:"+type+"||person:"+person);
     		    
     		   if(checked.length>0&&person.length>0){
     		    	 App.scrollTop();
     		    	 var pageContent = $('.page-content');
          		     App.blockUI(pageContent, false);
          		 
	        		 $.post("/w/user/update?t="+type+"&p="+person+"&admpark="+checked, {}, function (res) {
	                     App.unblockUI(pageContent);
	                     if(type>=20&&type<30){
	                    	 $('#userlist20').click();
	                     }else if(type>=30&&type<40){
	                    	 $('#userlist30').click();
	                     } else{
	                    	 $('#userlist10').click();
	                     }
	                    
	                 });
      	     }
     		   
            });
            
       	      $('.button_update_user').click(function(){
       	    	 var type = jQuery(this).attr("type");
       	    	 var checked = "";
      		     $("input[name='userselect']:checked").each(function() {
      	            checked+=$(this).val()+",";
      	        });
       		 
      		     if(checked.length>0){
      		    	App.scrollTop();
      		    	 var pageContent = $('.page-content');
           		     App.blockUI(pageContent, false);
           		 
	        		 $.post("/w/user/update?t="+type+"&p="+checked, {}, function (res) {
	                     App.unblockUI(pageContent);
	                     if(type>=20&&type<30){
	                    	 $('#userlist20').click();
	                     }else if(type>=30&&type<40){
	                    	 $('#userlist30').click();
	                     } else{
	                    	 $('#userlist10').click();
	                     }
	                    
	                 });
       	     }
       	    	
        	 });
       	      
       	      
       	   $('.popup_show_parking').click(function(){
  	    	 var userid = jQuery(this).attr("usertype");
  	    	 var p = jQuery(this).attr("p");
  	    	 var k = jQuery(this).attr("k");
  	    	 var v = jQuery(this).attr("v");
  	    	 //get total of parking
          	 $.get("/w/adm/park/"+userid+"?p="+p,function(result){
          		 if(result){
          			$("#form_modal3_adm .modal-body").html(result);
          			$("<input  type=hidden value="+userid+" id=usertype />").appendTo("#form_modal3_adm .modal-body");
          		 }
     			});
  	    	
   	        });
       	      
       	      $('.popup_select_parking').click(function(){
        	    	 var type = jQuery(this).attr("type");
        	    	 var p = jQuery(this).attr("p");
        	    	 var k = jQuery(this).attr("k");
        	    	 var v = jQuery(this).attr("v");
        	    	 //get total of parking
                	 $.get("/w/parkingprodpopup?p="+p+"&k="+k+"&v="+v,function(result){
                		 if(result){
                			$("#form_modal3 .modal-body").html(result);
                			$("<input  type=hidden value="+type+" id=popuptype />").appendTo("#form_modal3 .modal-body");
                		 }
           			});
        	    	
         	 });
       	      
        	 
        	 $("#dialog_confirm_user" ).dialog({
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
        	      			
        	      			var pageContent = $('.page-content');
        	      			var checked = $(this).data("idArray");
        	      			var type = $(this).data("type");
        	      			var warndialog = $(this);
        	      			 if(checked.length>0){
        	      				 App.scrollTop();
        	       		    	 var pageContent = $('.page-content');
        	            		 App.blockUI(pageContent, false);
        		        		 $.post("/w/user/delete?p="+checked, {}, function (res) {
        		                     App.unblockUI(pageContent);
        		                    
        		                     warndialog.dialog( "close" );
        		                     if(type>=20&&type<30){
        		                    	 $('#userlist20').click();
        		                     }else if(type>=30&&type<40){
        		                    	 $('#userlist30').click();
        		                     } else{
        		                    	 $('#userlist10').click();
        		                     }
        		                 });
        	        	     }

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

function deleteRemoteImage(imgId){
    //confirm dialog
	$( "#dialog_confirm" ).data("imgId",imgId).dialog( "open" );
}

jQuery(document).ready(function() {    
	UserList.init();
});


