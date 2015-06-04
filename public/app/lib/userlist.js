/**
 * 停车场类
 */
var UserList = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1 .group-checkable').change(function () {
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

        	$('.ajaxify').on('click', '', function (e) {
                e.preventDefault();
                App.scrollTop();
                var url = $(this).attr("post");
                var pageContent = $('.page-content');
                var pageContentBody = $('.page-content .page-content-body');

                App.blockUI(pageContent, false);

                $.post(url, {}, function (res) {
                        App.unblockUI(pageContent);
                        pageContentBody.html(res);
                        App.fixContentHeight(); // fix content height
                        App.initUniform(); // initialize uniform elements
                    });
               });
        	
        	
        	
        	 $('#button_delete').click(function(){
        		 alert("test delete!!!");
        	 });
        	 
       	      $('#button_verify').click(function(){
        		 
        	 });
        	 
        	 
        
        	 
        	 $("#dialog_confirm" ).dialog({
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
        	      			var imgId = $(this).data("imgId");
        	      			var warndialog = $(this);
        	      			 App.blockUI(pageContent, false);
        	      			 
        	      			 $.get("/a/image/delete/"+imgId,function(){
        	      				var imgItem = $('#item'+imgId);
        	      				imgItem.remove();
        	      				warndialog.dialog( "close" );
        	      				App.unblockUI(pageContent);
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

function deleteRemoteImage(imgId){
    //confirm dialog
	$( "#dialog_confirm" ).data("imgId",imgId).dialog( "open" );
}

jQuery(document).ready(function() {    
	UserList.init();
});


