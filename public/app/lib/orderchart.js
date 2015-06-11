/**
 * 停车场类
 */
var OrderChartList = function () {
    
    return {
        //main function to initiate the module
        init: function () {

             function chart2(jsonData) {
           
                 var plot = $.plot($("#chart_2"), jsonData, {
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
                 $("#chart_2").bind("plothover", function (event, pos, item) {
                     $("#x").text(pos.x.toFixed(2));
                     $("#y").text(pos.y.toFixed(2));

                     if (item) {
                         if (previousPoint != item.dataIndex) {
                             previousPoint = item.dataIndex;

                             $("#tooltip").remove();
                             var x = $.datepicker.formatDate('yy/mm/dd', new Date(item.datapoint[0])),
                                 y = item.datapoint[1];

                             showTooltip(item.pageX, item.pageY, item.series.label+ y + "订单[" + x + "]");
                         }
                     } else {
                         $("#tooltip").remove();
                         previousPoint = null;
                     }
                 });
             };
             
             $.get("/w/chart/order",function(result){
        		 if(result){
        			 
        			 var plotData = [];
        			 $.each(result, function(key, value) {
        				 var chartData=$.map(value, function(obj,i){
        			           return [[ new Date(obj.dateString), obj.countOrder]];                            
        			      });
        				 var jsonTextF={
        						 data:chartData,
        						 label:key
        				 }
        				 //alert("json:"+chartData);
        				 plotData.push(jsonTextF);
        			 });
        	
        			 chart2(plotData); 
        			 
        		}
        	});
             
        }

    };

}();


jQuery(document).ready(function() {    
	OrderChartList.init();
});


