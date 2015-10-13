/**
 * 停车场类
 */
var Index = function () {
    
    return {
        //main function to initiate the module
        init: function () {

             $('#menuclick .ajaxify,#notificationbar .ajaxify,#passwd').on('click', '', function (e) {
              e.preventDefault();
              App.scrollTop();
              var url = $(this).attr("post");
              var pageContent = $('.page-content');
              var pageContentBody = $('.page-content .page-content-body');
              window.console && console.log("index post url:"+url);
              App.blockUI(pageContent, false);
              $.post(url, {}, function (res) {
                     
                      pageContentBody.html(res);
                      App.fixContentHeight(); // fix content height
                      App.initUniform(); // initialize uniform elements
                      App.unblockUI(pageContent);
                  });
             });
             
         	
        	 $('.page-sidebar .ajaxify.start').click();
        	 
        	 //get total of parking
        	 $.get("/w/parking/task",function(result){
        		 if(result){
        			//total
        			 $("#header_notification_bar .badge").text(result.rowCount);
        			 
        			var taskHeader="<li><p>目前有"+result.rowCount+"条采集记录等待审批</p></li>";
        			$("#header_notification_bar .notification").html(taskHeader);
        			
        			if(result.result){
        			 $.each(result.result, function (index, entity) {  
        				 var taskmessage="<a class=\"ajaxify\"  href=\"javascript:$('#parking').click();\" post=\"/w/parking/"+entity.parkId+"\"><span class=\"label label-info\"><i class=\"icon-plus\"></i></span>"+entity.parkname+"" +
     					" <span class=\"time\">"+entity.createPerson+"</span></a>";
        				 
        				 $('<li/>').html(taskmessage).appendTo($("#header_notification_bar .notification"));
        			 });
        			}
        			 var footer = "<a class=\"ajaxify\"  href=\"javascript:$('#parking').click();\" post=\"/w/parking\">查看更多<i class=\"m-icon-swapright\"></i></a>";
        			 $('<li class=\"external\" />').html(footer).appendTo($("#header_notification_bar .notification"));
        			
        		 }
   			});
        	 
        	//get total of takecash
        	 $.get("/w/takecash/task",function(result){
        		 if(result){
        			//total
        			 $("#header_inbox_bar .badge").text(result.rowCount);
        			 
        			var taskHeader="<li><p>新来"+result.rowCount+"条停车场提现请求</p></li>";
        			$("#header_inbox_bar .inbox").html(taskHeader);
        			
        			if(result.result){
        			 $.each(result.result, function (index, entity) {  
        				 var taskmessage="<a class=\"ajaxify\"  href=\"javascript:$('#index_takecash').click();\" post=\"/w/takecash\"><span class=\"photo\"><img src=\""+entity.parkprod.imgUrlArray[0].imgUrlHeader+entity.parkprod.imgUrlArray[0].imgUrlPath+"\"/></span>" +
        				 "<span class=\"subject\"><span class=\"from\">"+entity.parkprod.parkname+"</span>" +
        				 "<span class=\"time\">取现"+entity.takemoney+"元</span></span><span class=\"message\">"+entity.askdata+"</span></a>"
        				 
        				 $('<li/>').html(taskmessage).appendTo($("#header_inbox_bar .inbox"));
        			 });
        			}
        			 var footer = "<a class=\"ajaxify\"  href=\"javascript:$('#index_takecash').click();\" post=\"/w/takecash\">查看更多<i class=\"m-icon-swapright\"></i></a>";
        			 $('<li class=\"external\" />').html(footer).appendTo($("#header_inbox_bar .inbox"));
        			
        		 }
   			});
        	 
        	 //禁止backspace键
        	 $( document ).keydown( function( event ) {
                 if ( event.keyCode == 8 && !$("input").is(":focus")&& !$("textarea").is(":focus")) {
                     event.preventDefault();
                 }
             } );
        	 
        }


    };

}();

jQuery(document).ready(function() {    
	App.init();
	Index.init();

});
