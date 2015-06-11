/**
 * 停车场类
 */
var ParkingProd = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1_prod .group-checkable').change(function () {
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
        	
    		$('#parkingprodcontent .ajaxify').on('click', '', function (e) {
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

    		//search
        	$('#searchParkingProdButton').click(function(){
        		App.scrollTop();
      		     var key = $("#key_search_parkingprod").val();
      		     var value = $("#value_search_parkingprod").val();
      		   
      		     var pageContent = $('.page-content');
      		     var pageContentBody = $('.page-content .page-content-body');
      		  
   		     App.blockUI(pageContent, false);
   		     
      		  $.post("/w/parkingprod?k="+key+"&v="+value, {}, function (res) {
                 App.unblockUI(pageContent);
                 pageContentBody.html(res);
                 App.fixContentHeight(); // fix content height
                 App.initUniform(); // initialize uniform elements
                
             });
       		 
       	 });
    		
        	 $('#button_retrieve').click(function(){
        		 var checked = "";
        		 $('input:checkbox:checked').each(function() {
        	            checked+=$(this).val()+",";
        	        });
        	      
        	
        		 $( "#dialog_confirm_retrieve" ).data("parkingIdArray",checked).dialog( "open" );

        	 });
        	 
        	 /**
        	  * 关闭打开停车场
        	  */
        	 $('#button_openclose').click(function(){
        		 var checked = "";
        		 $('input:checkbox:checked').each(function() {
        	            checked+=$(this).val()+",";
        	        });
        		 var p = $(this).attr("p");
        		 var s = $(this).attr("s");
        		 var o = $(this).attr("o");
        		 var k = $(this).attr("k");
        		 var v = $(this).attr("v");
        		 
        		 var pageContent = $('.page-content');
      		     var pageContentBody = $('.page-content .page-content-body');
      		     
      		   App.blockUI(pageContent, false);
        		 $.get("/w/parkingprod/open?p="+p+"&s="+s+"&o="+o+"&k="+k+"&v="+v+"&pid="+checked,function(data){
	      				App.unblockUI(pageContent);
	      				pageContentBody.html(data);
	                    App.fixContentHeight(); // fix content height
	                    App.initUniform(); // initialize uniform elements
	      			});
        	 });
        	
        	 $("#dialog_confirm_retrieve" ).dialog({
        	      dialogClass: 'ui-dialog-green',
        	      autoOpen: false,
        	      resizable: false,
        	      height: 210,
        	      modal: true,
        	      buttons: [
        	      	{
        	      		'class' : 'btn red',	
        	      		"text" : "退回",
        	      		click: function() {
        	      			App.scrollTop();
        	      			var pageContent = $('.page-content');
        	      			var parkingIdArray = $(this).data("parkingIdArray");
        	      			var warndialog = $(this);
        	      			App.blockUI(pageContent, false);
        	      			window.console && console.log("uri:"+parkingIdArray)
        	      			 $.get("/w/parking/retrieve?p="+parkingIdArray,function(data){
        	      				window.console && console.log("retrieve total:"+data)
          	      				App.unblockUI(pageContent);
          	      			    warndialog.dialog( "close" );
          	      			    $('#parkingprod').click();
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
	ParkingProd.init();
	
});


