/**
 * 停车场类
 */
var Income = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	$('#searchIncomeButton').click(function(){
        		App.scrollTop();
        		 var type = jQuery(this).attr("type");
        		     var key = $("#key_search_income").val();
        		     var pageContent = $('.page-content');
        		     var pageContentBody = $('.page-content .page-content-body');
        		  
        	     App.blockUI(pageContent, false);
        	     
        		  $.post("/w/income?f="+key, {}, function (res) {
        	     
        	      pageContentBody.html(res);
        	      App.fixContentHeight(); // fix content height
        	      App.initUniform(); // initialize uniform elements
        	      App.unblockUI(pageContent);
        	     
        	  });
        		 
        	 });
        	
        	$('#incomereload').on('click', '', function (e) {
        	    e.preventDefault();
        	    var url = $(this).attr("post");
        	    window.console && console.log("post url:"+url);
        	    $.post(url, {}, function (res) {
        	            pageContentBody.html(res);
        	            App.fixContentHeight(); // fix content height
        	            App.initUniform(); // initialize uniform elements
        	        });
        	   });
        	
        	 
        	$('#incomedetail .ajaxify,#incomepagination .ajaxify,#incomebreadcrumb .ajaxify,#incomelist .ajaxify').on('click', '', function (e) {
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
        	      
        	
         	 //导出数据
            //获取备份数据并且刷新界面
            var fetchbackupList = function(){
            	
            	$("#button_export_income").attr("disabled",false); 
            	$("#button_export_allincome").attr("disabled",false); 
            	
            	$("#form_modal_incomebackup .incomebacklist").html("请等待");
            	 $.get("/w/getbackupList?typeName=.income.csv",function(result){
               		 if(result){
               			$("#form_modal_incomebackup .incomebacklist").html("");
               			 $.each(result, function(index, value) {
               				$("<tr class=\"odd gradeX\">" +
                   					"<td class=\"hidden-480\">"+value.createDate+"</td>" +
                   					"<td >"+value.fileName+"</td>" +
                   					"<td class=\"hidden-480\">"+value.fileSize+"k</td>" +
                   					"<td class=\"hidden-480\"><a href=\"/w/downloaduser?file="+value.fileName+"\" target=\"_parent\"><i class=\"icon-cloud-download\"></i></a></td></tr>").appendTo("#form_modal_incomebackup .incomebacklist");
           			     });
               		 }
          			});
            };
//                        
           //导出数据，并且刷新界面
           var startExport = function(fullbackup){
        	   $("#exportmessage").html("")
             	$("#button_export_income").attr("disabled",true); 
        	    $("#button_export_allincome").attr("disabled",true); 
        		var orderasc = 0;
            	if($("#orderasc").is(':checked')){
            		orderasc = 0;
            	}else{
            		orderasc = 1;
            	}
            	$.get("/w/export/income?fullbackup="+fullbackup+"&asc="+orderasc, function(result){
            		$("#exportmessage").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size=3 color=red>"+result+"</font>")
            		if (result == "任务完成"){
            			//alert("任务完成,开始刷新列表")
            			fetchbackupList();
            		}
            	});
           };
           
           //点击了导出数据menu
           $('.button_export_income_menu').click(function(){
  	    	    $("#exportmessage").html("")
  	    	    //get total of parking
  	    	    fetchbackupList();
  	    	
   	        });
            
            //增量备份
            $('#button_export_income').click(function(){
            	startExport(0);
       	    });
            
            //所有备份
            $('#button_export_allincome').click(function(){
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
	Income.init();
});


