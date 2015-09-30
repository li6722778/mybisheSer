/**
 * 停车场类
 */
var UserList = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1_order .group-checkable').change(function () {
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

        	$('#orderlist .ajaxify').on('click', '', function (e) {
                e.preventDefault();
                App.scrollTop();
                var url = $(this).attr("post");
                var pageContent = $('.page-content');
                var pageContentBody = $('.page-content .page-content-body');

                App.blockUI(pageContent, false);
                window.console && console.log("post url:"+url);
                $.post(url, {}, function (res) {
                       
                        pageContentBody.html(res);
                        App.fixContentHeight(); // fix content height
                        App.initUniform(); // initialize uniform elements
                        App.unblockUI(pageContent);
                    });
               });
        	
        	
        	
        	 $('#button_delete_order').click(function(){
        		 
        		 var checked = "";
       		     $('input:checkbox:checked').each(function() {
       	            checked+=$(this).val()+",";
       	        });
       		  $( "#dialog_confirm_deleteorder" ).data("idArray",checked).dialog( "open" );
        	 });
        	 
        	 
            $('#button_exption_order').click(function(){
        		 
        		 var checked = "";
       		     $('input:checkbox:checked').each(function() {
       	            checked+=$(this).val()+",";
       	        });
       		  $( "#dialog_confirm_order" ).data("idArray",checked).dialog( "open" );
        	 });
        	 
        	 
            
            //历史停车搜索
            $('#searchOrderButtonHis').click(function(){
            	App.scrollTop();
        		 var city = $("#city_search_order").val();
       		     var key = $("#key_search_order").val();
       		     var pageContent = $('.page-content');
       		     var pageContentBody = $('.page-content .page-content-body');
       		  
    		     App.blockUI(pageContent, false);
    		     
       		  $.post("/w/orderhis?c="+city+"&f="+key, {}, function (res) {
                 
                  pageContentBody.html(res);
                  App.fixContentHeight(); // fix content height
                  App.initUniform(); // initialize uniform elements
                  App.unblockUI(pageContent);
                 
              });
        		 
        	 });
        	 
            $('#searchOrderButton').click(function(){
            	App.scrollTop();
        		 var city = $("#city_search_order").val();
       		     var key = $("#key_search_order").val();
       		     var pageContent = $('.page-content');
       		     var pageContentBody = $('.page-content .page-content-body');
       		  
    		     App.blockUI(pageContent, false);
    		     
       		  $.post("/w/order?c="+city+"&f="+key, {}, function (res) {
                  
                  pageContentBody.html(res);
                  App.fixContentHeight(); // fix content height
                  App.initUniform(); // initialize uniform elements
                  App.unblockUI(pageContent);
                 
              });
        		 
        	 });
        	 
       	     
        	 
        
        	 
        	 $("#dialog_confirm_order" ).dialog({
        	      dialogClass: 'ui-dialog-green',
        	      autoOpen: false,
        	      resizable: false,
        	      height: 210,
        	      modal: true,
        	      buttons: [
        	      	{
        	      		'class' : 'btn red',	
        	      		"text" : "设置异常订单",
        	      		click: function() {
        	      			var checked = $(this).data("idArray");
        	      			var warndialog = $(this);
        	      			 if(checked.length>0){
        	      				 App.scrollTop();
        	       		    	 var pageContent = $('.page-content');
        	            		 App.blockUI(pageContent, false);
        		        		 $.get("/w/order/setexception?p="+checked, function (res) {
        		        			 window.console && console.log("setexception done."+res);
        		                     App.unblockUI(pageContent);
        		                     warndialog.dialog( "close" );
        		                     $('#index_order').click();
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
        	 
        	 
        	 $("#dialog_confirm_deleteorder" ).dialog({
       	      dialogClass: 'ui-dialog-green',
       	      autoOpen: false,
       	      resizable: false,
       	      height: 210,
       	      modal: true,
       	      buttons: [
       	      	{
       	      		'class' : 'btn red',	
       	      		"text" : "删除订单",
       	      		click: function() {
       	      			var checked = $(this).data("idArray");
       	      			var warndialog = $(this);
       	      			 if(checked.length>0){
       	      				 App.scrollTop();
       	       		    	 var pageContent = $('.page-content');
       	            		 App.blockUI(pageContent, false);
       		        		 $.get("/w/order/delete?p="+checked, function (res) {
       		        			 window.console && console.log("setexception done."+res);
       		                     App.unblockUI(pageContent);
       		                     warndialog.dialog( "close" );
       		                     $('#index_order').click();
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
        	 
        	 
        	 //导出数据
             //获取备份数据并且刷新界面
             var fetchbackupList = function(){
             	
             	$("#button_export_order").attr("disabled",false); 
             	$("#button_export_allorder").attr("disabled",false); 
             	
             	$("#form_modal_orderbackup .orderbacklist").html("请等待");
             	 $.get("/w/getbackupList?typeName=.order.csv",function(result){
                		 if(result){
                			$("#form_modal_orderbackup .orderbacklist").html("");
                			 $.each(result, function(index, value) {
                				$("<tr class=\"odd gradeX\">" +
                    					"<td class=\"hidden-480\">"+value.createDate+"</td>" +
                    					"<td >"+value.fileName+"</td>" +
                    					"<td class=\"hidden-480\">"+value.fileSize+"k</td>" +
                    					"<td class=\"hidden-480\"><a href=\"/w/downloaduser?file="+value.fileName+"\" target=\"_parent\"><i class=\"icon-cloud-download\"></i></a></td></tr>").appendTo("#form_modal_orderbackup .orderbacklist");
            			     });
                		 }
           			});
             };
//                         
            //导出数据，并且刷新界面
            var startExport = function(fullbackup){
         	   $("#exportmessage").html("")
            	$("#button_export_order").attr("disabled",true); 
         	    $("#button_export_allorder").attr("disabled",true); 
         		var orderasc = 0;
             	if($("#orderasc").is(':checked')){
             		orderasc = 0;
             	}else{
             		orderasc = 1;
             	}
             	$.get("/w/export/order?fullbackup="+fullbackup+"&asc="+orderasc, function(result){
             		$("#exportmessage").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size=3 color=red>"+result+"</font>")
             		if (result == "任务完成"){
             			//alert("任务完成,开始刷新列表")
             			fetchbackupList();
             		}
             	});
            };
            
            //点击了导出数据menu
            $('.button_export_order_menu').click(function(){
   	    	    $("#exportmessage").html("")
   	    	    //get total of parking
   	    	    fetchbackupList();
   	    	
    	        });
             
             //增量备份
             $('#button_export_order').click(function(){
             	startExport(0);
        	    });
             
             //所有备份
             $('#button_export_allorder').click(function(){
             	startExport(1);
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


