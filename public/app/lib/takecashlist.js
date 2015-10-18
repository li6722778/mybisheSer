/**
 * 停车场类
 */
var UserList = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	if (jQuery().dataTable) {
        		jQuery('#sample_1_takecash .group-checkable').change(function () {
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

            var handlePopovers = function () {
                //jQuery('.popovers').popover();
                $('.popovers').popover({"trigger": "manual", "html":"true"});
            }
        	$('#takecashlist .ajaxify').on('click', '', function (e) {
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
        	
        	
        	 $('.button_update_cash_status').click(function(){
       	    	 var type = jQuery(this).attr("type");
       	    	 
       	    	 var currentPage = jQuery(this).attr("currentPage");
       	    	 var selectedStatus = jQuery(this).attr("selectedStatus");
       	    	
       	    	 
       	    	 var checked = "";
      		     $("input[name='cashselect']:checked").each(function() {
      	            checked+=$(this).val()+",";
      	        });

      		   if (type == null) { 
     			   type = 1;
     		   }
     		   
      		     if(checked.length>0){
      		    	App.scrollTop();
      		    	 var pageContent = $('.page-content');
      		    	var pageContentBody = $('.page-content .page-content-body');
           		     App.blockUI(pageContent, false);
           		 
	        		 $.post("/w/takecash/update/"+type+"?p="+currentPage+"&pid="+checked, {}, function (res) {
	                     pageContentBody.html(res);
	                        App.fixContentHeight(); // fix content height
	                        App.initUniform(); // initialize uniform elements
	                        App.unblockUI(pageContent);
	                    
	                 });
       	     }
       	    	
        	 });
        	 
        	 $('#sample_1_takecash .popovers').mouseout(function(){
        		 $(this).popover('hide');
        		});
        	 
        	 $('#sample_1_takecash .popovers').on('click', '', function (e) {
        		 var parkid = $(this).attr("parkid");
        		 var currentObj = $(this);
        		 $.get("/a/income/"+parkid, function (res) {
        			 var html = "<div class=\"row-fluid portfolio-block\"><div>" +
        			 		"<div class=\"portfolio-info\">今日收益:<span>¥"+res.incometoday+"</span></div>" +
        			 		"<div class=\"portfolio-info\">全部收益:<span >¥"+res.incometotal+"</span></div>" +
        			 		"<div class=\"portfolio-info\">可提余额:<span class=\"label label-important\">¥"+res.feeTakecashInBalance+"</span></div>" +
        			 		"<div class=\"portfolio-info\">已申请金额:<span class=\"label label-warning\">¥"+res.takeCashTotal+"</span></div>" +
        			 		"<div class=\"portfolio-info\">网上收益:<span >¥"+res.feeWeb+"</span></div>" +
        			 		"<div class=\"portfolio-info\">已补贴:<span >¥"+res.allowance+"</span></div>" +
        			 		"<div class=\"portfolio-info\">优惠卷:<span >¥"+res.counpontotal+"</span></div>" +
        			 		"<div class=\"portfolio-info\">现金收益:<span >¥"+res.cashtotal+"</span></div>" +
        			 		"<div class=\"portfolio-info\">已完成订单数:<span >"+res.finishedOrder+"</span></div></div>";
        			 currentObj.attr('data-content',html);
        			 currentObj.popover('show');
                 });
        	 })
       
        	 handlePopovers();       
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


