/*C O P Y R I G H T    N O T I C E
 (c) Copyright 2013 GE Healthcare

 All Rights Reserved.
 No portions of this source code or the resulting compiled program may be
 used without express written consent and licensing by GE Healthcare, Inc. */

/*******************************************************************************
 * FILE QueryManager.js
 * 
 * DESCRIPTION This JS provides common API for Ajax queries.
 * 
 * DESIGN DOCUMENTS
 * 
 * 
 * REV DATE BY SPR & REMARKS ---- -------------- ---------------
 * --------------------------------------- 1.0 Dec 27,2013 fz031403 Original
 * --------------------------------------- 2.0 Jan 13,2015 Youlu    N/A Added ajax call for bandwidth calculation.
 ******************************************************************************/
define([ "log4javascript"], function() {
	var networkConnectionStatus = 0;
	var tempArray = new Array();

	var sleep = function() {
		window.console && console.log("In SLeep Query Manager");
		setTimeout(function()
				{ 
			window.console && console.log("In Sleep in Query Manager After creating sockets");
			MainController.hideNetworkNotAvailablePopUp();
				}
		, MainController.getSleepTimeDuringSocketConnection());
	};

	QueryManager = {
			ajaxAsyncGet : function(url, callback, callbackParam) {
				$.ajax(url, {
					'type' : 'GET',
					'processData' : false
				}).done(
						function(data) {
							callback(data, callbackParam);
						})
						.fail(function(data, textStatus, jqXHR) {
							window.console && console.log("Error Code ajaxAsyncGet  " + data.status);
		
						});
			},

			ajaxSyncGet : function(url, callback, callbackParam) {
				$.ajax(url, {
					'type' : 'GET',
					'async' : false,
					'processData' : false
				}).done(
						function(data) {
							callback(data, callbackParam);
						})
						.fail(function(data, textStatus, jqXHR) {
							window.console && console.log("Error Code  ajaxSyncGet  " + data.status);
	
						});
			},

			ajaxAsyncPost : function(url, data, callback) {
				$.ajax(url, {
					'data' : data,
					'type' : 'POST',
					'processData' : false,
					'contentType' : 'application/json'
				}).done(
						function(data) {
							callback(data);
						})
						.fail(function(data, textStatus, jqXHR) {
							window.console && console.log("Error Code  ajaxAsyncPost  " + data.status);

						});
			},

			ajaxSyncPost : function(url, data, callback, param) {

				$.ajax(url, {
					'data' : data,
					'type' : 'POST',
					'async' : false,
					'processData' : false,
					'contentType' : 'application/json'
				}).done(
						function(data) {
							callback(data,param);
						})
						.fail(function(data, textStatus, jqXHR) {
							window.console && console.log("Error Code ajaxSyncPost  " + data.status);

						});
			},

			ajaxSyncPostHeartBeat : function(url, data, callback) {
				$.ajax(url, {
					'data' : data,
					'type' : 'POST',
					'async' : false,
					'timeout' : 2000,
					'processData' : false,
					'contentType' : 'application/json'
				}).done(
						function(data, textStatus, jqXHR) {
							
							//window.console && console.log("Ajax call success");
							if (networkConnectionStatus != 0) {
								networkConnectionStatus = 0;
								setSockets();
								for (var i =0; i< tempArray.length; i++) {
									wsUri = tempArray[i].wsUri;
									update = tempArray[i].update;
									
									createSocketAndListener(wsUri, update);
								}
								sleep();
								window.console && console.log(" Sockets created     " + tempArray.length);
							}
							callback(data);
						})
						.fail(function(data, textStatus, jqXHR) {
							
							//window.console && console.log("Error Code in Heartbeat  " + data.status);
							tempArray = getSockets();
							if (networkConnectionStatus == 0) {
								MainController.showNetworkNotAvailablePopUp();
							}
							if (data.status == 0) {
								LOGGER.debug("Network not available so retrying till network available");
								MainController.sleep();
								networkConnectionStatus++;
							} else if (data.status == 404) {
								LOGGER.debug("Network not available so retrying till network available");
								MainController.sleep();
								networkConnectionStatus++;
							}
						});	
			},

			ajaxSyncPostBandwidth : function(url, data, callback) {

				$.ajax(url, {
					'data' : data,
					'type' : 'POST',
					'async' : false,
					'timeout' : 2000,
					'processData' : false,
					'contentType' : 'application/json'
				}).done(
						function(data) {
							callback(data);
						})
						.fail(function(data, textStatus, jqXHR) {
							window.console && console.log("Error Code ajaxSyncPost  " + data.status);

						});
			}

	};
	return QueryManager;
});
