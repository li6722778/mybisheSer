/**
 * 用户赠涨类
 */
var UserChartList = function () {
    
    return {
        //main function to initiate the module
        init: function () {

             function chart2(jsonData) {
           
                 var plot = $.plot($("#chart_user"), jsonData, {
                         series: {
                             lines: {
                                 show: true,
                                 lineWidth: 2,
                                 fill: true,
                                 fillColor: {
                                     colors: [{
                                             opacity: 0.05
                                         }, {
                                             opacity: 0.01
                                         }
                                     ]
                                 }
                             },
                             points: {
                                 show: true
                             },
                             shadowSize: 2
                         },
                         grid: {
                             hoverable: true,
                             clickable: true,
                             tickColor: "#eee",
                             borderWidth: 0
                         },
                         colors: ["#d12610", "#37b7f3", "#52e136"],
                         xaxis: {
                        	 mode: "time",
                             timeformat: "%m/%d"
                            
                         },
                         yaxis: {
                             ticks: 11,
                             tickDecimals: 0
                         }

                     });


                 function showTooltip(x, y, contents) {
                     $('<div id="tooltip">' + contents + '</div>').css({
                             position: 'absolute',
                             display: 'none',
                             top: y + 5,
                             left: x + 15,
                             border: '1px solid #333',
                             padding: '4px',
                             color: '#fff',
                             'border-radius': '3px',
                             'background-color': '#333',
                             opacity: 0.80
                         }).appendTo("body").fadeIn(200);
                 }

                 var previousPoint = null;
                 $("#chart_user").bind("plothover", function (event, pos, item) {
                     $("#x").text(pos.x.toFixed(2));
                     $("#y").text(pos.y.toFixed(2));

                     if (item) {
                         if (previousPoint != item.dataIndex) {
                             previousPoint = item.dataIndex;

                             $("#tooltip").remove();
                             var x = $.datepicker.formatDate('yy/mm/dd', new Date(item.datapoint[0])),
                                 y = item.datapoint[1];

                             showTooltip(item.pageX, item.pageY, item.series.label+ y + "[" + x + "]");
                         }
                     } else {
                         $("#tooltip").remove();
                         previousPoint = null;
                     }
                 });
             };
             

             
             var jumpto = function(days, hasloading){
            	 var pageContent = $('.page-content');
            	 if(hasloading==1){
            	    App.blockUI(pageContent, false);
            	 }
            	 $.get("/w/chart/user?p="+days,function(result){
            		 if(result){
            			 
            			 var plotData = [];
            			 $.each(result, function(key, value) {
            				 var chartData=$.map(value, function(obj,i){
            			           return [[ new Date(obj.dateString).getTime(), obj.countOrder]];                            
            			      });

            				 var jsonTextF={
            						 data:chartData,
            						 label:key
            				 }
            				 plotData.push(jsonTextF);
            			 });
            	
            			 chart2(plotData); 
            			 if(hasloading==1){
            			   App.unblockUI(pageContent);
            			 }
            		}
            	});
            	 
            };
             
            $('#data30').click(function(){
            	jumpto(30,1);
            	$('#label_days').html("30天");
        	 });
             
            $('#data90').click(function(){
            	jumpto(90,1);
            	$('#label_days').html("90天");
       	    });
            
            $('#data180').click(function(){
            	jumpto(180,1);
            	$('#label_days').html("180天");
       	    });
             
            $('.popup_export').click(function(){
   	    	 var type = jQuery(this).attr("type");
   	    	 
   	    	 $("#form_modal_userbackup .userbacklist").html("请等待");
   	    	 //get total of parking
           	 $.get("/w/getbackupuserList",function(result){
           		 if(result){
           			$("#form_modal_userbackup .userbacklist").html("");
           			 $.each(result, function(index, value) {
           				$("<tr class=\"odd gradeX\">" +
               					"<td class=\"hidden-480\">"+value.createDate+"</td>" +
               					"<td >"+value.fileName+"</td>" +
               					"<td class=\"hidden-480\">"+value.fileSize+"k</td>" +
               					"<td class=\"hidden-480\"><a href=\"/w/downloaduser?file="+value.fileName+"\" target=\"_parent\"><i class=\"icon-cloud-download\"></i></a></td></tr>").appendTo("#form_modal_userbackup .userbacklist");
       			     });
           		 }
      			});
   	    	
    	 });
            
            //增量备份
            $('#button_export_user').click(function(){
               
            	
            	
            	
            	$("#form_modal_userbackup .userbacklist").html("请等待");
      	    	 //get total of parking
              	 $.get("/w/getbackupuserList",function(result){
              		 if(result){
              			$("#form_modal_userbackup .userbacklist").html("");
              			 $.each(result, function(index, value) {
              				$("<tr class=\"odd gradeX\">" +
                  					"<td class=\"hidden-480\">"+value.createDate+"</td>" +
                  					"<td >"+value.fileName+"</td>" +
                  					"<td class=\"hidden-480\">"+value.fileSize+"k</td>" +
                  					"<td class=\"hidden-480\"><a href=\"/w/downloaduser?file="+value.fileName+"\" target=\"_parent\"><i class=\"icon-cloud-download\"></i></a></td></tr>").appendTo("#form_modal_userbackup .userbacklist");
          			     });
              		 }
         			});
      	    	
       	    });
            
            
             jumpto(30,0);
             
        }

    };

}();




jQuery(document).ready(function() {    
	UserChartList.init();
});


