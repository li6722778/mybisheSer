/**
 * 停车场类
 */
var Index = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
        	 $('.page-sidebar .ajaxify.start').click();

        }

    };

}();

jQuery(document).ready(function() {    
	App.init();
	Index.init();
});
