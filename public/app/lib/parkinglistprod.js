/**
 * 停车场类
 */
var ParkingProd = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	
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

        	 $('#button_retrieve').click(function(){
        		 var checked = "";
        		 $('input:checkbox:checked').each(function() {
        	            checked+=$(this).val()+",";
        	        });
        	      
        	
        		 $( "#dialog_confirm_retrieve" ).data("parkingIdArray",checked).dialog( "open" );

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


