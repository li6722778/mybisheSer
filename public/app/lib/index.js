/**
 * 停车场类
 */
var Index = function () {
    
    return {
        //main function to initiate the module
        init: function () {

             $('#menuclick .ajaxify,notificationbar .ajaxify').on('click', '', function (e) {
              e.preventDefault();
              App.scrollTop();
              var url = $(this).attr("post");
              var pageContent = $('.page-content');
              var pageContentBody = $('.page-content .page-content-body');
              window.console && console.log("post url:"+url);
              App.blockUI(pageContent, false);

              $.post(url, {}, function (res) {
                      App.unblockUI(pageContent);
                      pageContentBody.html(res);
                      App.fixContentHeight(); // fix content height
                      App.initUniform(); // initialize uniform elements
                  });
             });
             
         	
        	 $('.page-sidebar .ajaxify.start').click();
        	 
        	 //get total of parking
        	 $.get("/w/parking/task",function(result){
        		 if(result){
        			//total
        			 $("#header_notification_bar .badge").text(result.length);
        			 
        			var taskHeader="<li><p>目前有"+result.length+"条采集记录等待审批</p></li>";
        			$("#header_notification_bar .notification").html(taskHeader);
        			
        			 $.each(result, function (index, entity) {  
        				 var taskmessage="<a class=\"ajaxify\"  href=\"javascript:$('#parking').click();\" post=\"/w/parking/"+entity.parkId+"\"><span class=\"label label-info\"><i class=\"icon-plus\"></i></span>"+entity.parkname+"" +
     					" <span class=\"time\">"+entity.createPerson+"</span></a>";
        				 
        				 $('<li/>').html(taskmessage).appendTo($("#header_notification_bar .notification"));
        			 });
        			 
        			 var footer = "<a class=\"ajaxify\"  href=\"javascript:$('#parking').click();\" post=\"/w/parking\">查看更多<i class=\"m-icon-swapright\"></i></a>";
        			 $('<li class=\"external\" />').html(footer).appendTo($("#header_notification_bar .notification"));
        			
        		 }
   			});
        	 
        }


    };

}();

jQuery(document).ready(function() {    
	App.init();
	Index.init();
});