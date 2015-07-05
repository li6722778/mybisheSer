/**
 * 停车场账户
 */
var ParkingAccount = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	

        	
    		//search
        	$('#searchParkingProdButton').click(function(){
        		App.scrollTop();
      		     var key = $("#key_search_parkingprod").val();
      		     var value = $("#value_search_parkingprod").val();
      		     var status = $("#status_search_parkingprod").val();
      		   
      		     var pageContent = $('.page-content');
      		     var pageContentBody = $('.page-content .page-content-body');
      		  
   		     App.blockUI(pageContent, false);
   		     
      		  $.post("/w/parkaccount?k="+key+"&v="+value+"&open="+status, {}, function (res) {
                 App.unblockUI(pageContent);
                 pageContentBody.html(res);
                 App.fixContentHeight(); // fix content height
                 App.initUniform(); // initialize uniform elements
                
             });
       		 
       	 });


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
        	 

    		$('.accountSetting').on('click', '', function (e) {
    			
    			var parkId = jQuery(this).attr("parkId");
      	    	var parkname = jQuery(this).attr("parkname");
      	    	var address = jQuery(this).attr("address");
      	    	var venderBankName = jQuery(this).attr("venderBankName");
      	    	var venderBankNumber = jQuery(this).attr("venderBankNumber");
      	    	


      	    	$("#parkId").val(parkId);
      	    	$("#parkname").val(parkname);
      	    	$("#address").val(address);
      	    	$("#venderBankName").val(venderBankName);
      	    	$("#venderBankNumber").val(venderBankNumber);
      	    	
    		});
    		
    		
    		$('#button_update_account').on('click', '', function (e) {
      	    	var currentPage = jQuery(this).attr("currentPage");
      	    	var key = jQuery(this).attr("key");
      	    	var searchObj = jQuery(this).attr("searchObj");
      	    	var status = jQuery(this).attr("status");
      	    	
      	    	var parkId = $("#parkId").val();
      	    	var venderBankName = $("#venderBankName").val();
      	    	var venderBankNumber = $("#venderBankNumber").val();
      	    	
      	    	var pageContent = $('.page-content');
                var pageContentBody = $('.page-content .page-content-body');

               // alert("parkid:"+parkId+",venderBankName:"+venderBankName+",venderBankNumber:"+venderBankNumber)
                
                App.blockUI(pageContent, false);
                
      	    	$.post("/w/parkaccount/update/"+parkId+"?p="+currentPage+"&open="+status+"&k="+key+"&v="+searchObj+"&bn="+venderBankName+"&bc="+venderBankNumber, {}, function (res) {
                    App.unblockUI(pageContent);
                    pageContentBody.html(res);
                    App.fixContentHeight(); // fix content height
                    App.initUniform(); // initialize uniform elements
                   
                });
    		});
        	

        }

    };

}();


jQuery(document).ready(function() {    
	ParkingAccount.init();
});


