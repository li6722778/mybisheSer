var FormFileUpload = function () {


    return {
        //main function to initiate the module
        init: function () {

        	var formurl = $('#fileupload').attr("action");
            // Initialize the jQuery File Upload widget:
            $('#fileupload').fileupload({
                // Uncomment the following to send cross-domain cookies:
                //xhrFields: {withCredentials: true},
                url: formurl,
                done: function (e, data) { 
                	
                	if(data.result){
	                	if(data.result.length>0){
	                		$("#imagepreview").empty();
		                    $.each(data.result, function (index, file) {  
		                    	var imghtml = "<div class=item id=item"+file.parkImgId+">" +
		            			"<a class=fancybox-button data-rel=fancybox-button title=parking href="+file.imgUrlHeader+file.imgUrlPath+">" +
		            					"<div class=zoom>" +
		            							"<img src="+file.imgUrlHeader+file.imgUrlPath+" alt=parking />" +
		            									"<div class=zoom-icon></div></div></a><div class=details>" +
		            											"<a href=\"javascript:deleteRemoteImage("+file.parkImgId+");\" class=icon>" +
		            													"<i class=icon-remove></i></a></div></div></div>";
		                    	$('.template-upload').css("display","none");
		                    	$('<div class=span3 />').html(imghtml).appendTo($("#imagepreview"));
		                    }); 
	                	}
                	}
                }  
            });

            // Load existing files:
            // Demo settings:
//            $.ajax({
//                url: $('#fileupload').fileupload('option', 'url'),
//                dataType: 'json',
//                context: $('#fileupload')[0],
//                maxFileSize: 5000000,
//                acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
//                process: [{
//                        action: 'load',
//                        fileTypes: /^image\/(gif|jpeg|png)$/,
//                        maxFileSize: 2000000 // 20MB
//                    }, {
//                        action: 'resize',
//                        maxWidth: 1440,
//                        maxHeight: 900
//                    }, {
//                        action: 'save'
//                    }
//                ]
//            }).done(function (result) {
//            	alert("upload done!!!!!!!");
//                $(this).fileupload('option', 'done')
//                    .call(this, null, {
//                    result: result
//                });
//            });

            // Upload server status check for browsers with CORS support:
            if ($.support.cors) {
                $.ajax({
                    url: '/a/version',
                    type: 'HEAD'
                }).fail(function () {
                    $('<span class="alert alert-error"/>')
                        .text('当前图片服务器已经停止工作 ')
                        .appendTo('#fileupload');
                });
            }

            // initialize uniform checkboxes  
            App.initUniform('.fileupload-toggle-checkbox');
        }

    };

}();