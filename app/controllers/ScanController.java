package controllers;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import models.info.TCouponEntity;
import models.info.TOrder;
import models.info.TOrderHis;
import models.info.TParkInfoProd;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.ConfigHelper;
import utils.Constants;
import utils.ZXingUtil;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 扫码相关
 * @author woderchen
 *
 */
public class ScanController extends Controller{
	
	public static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	public static final String download_url = ConfigHelper.getString("chebole.download.url");
	
	public static final String OS_ANDROID="android";
	public static final String OS_IOS="ios";
		
	
	
	public static Result disCouponIdQRImage(String pidarray,int width,int height) {
		Logger.info("start to disCouponIdQRImage for :"+ pidarray);


		StringBuilder imgHtml = new StringBuilder("");
		
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					
					TCouponEntity prod = TCouponEntity.findDataById(pid);
					
					String encodeString = "";
					
					if(download_url!=null){
						encodeString = download_url;
					}
					

					
					encodeString=encodeString+"#"+prod.counponCode;

					try{
					String basePath = UploadController.image_store_path;
					String urlHeader = ConfigHelper.getString("image.url.header");
					
					String abbr = "/qr/cp_"+pid+".png";
					
					String imagePath = basePath+abbr;
					
					File file = new File(imagePath);
					if(!file.getParentFile().exists()){
						file.getParentFile().mkdirs();
					}
					
					Logger.info("QR image path:"+ imagePath);
					
					;
					
					if (ZXingUtil.encodeQRCodeImage(encodeString, null, imagePath, width, height, null)) {
						imgHtml.append("<div class=\"row-fluid\">").append("<h4>"+prod.counponCode+"<br/><small>"+prod.money+"元</small></h4>")
						.append("<a class=\"fancybox-button\" data-rel=\"fancybox-button\" title=\""+prod.money+"\" target=\"blank\" href=\""+urlHeader+abbr+"\">")
						.append("<div class=\"zoom\">").append("<img src=\""+urlHeader+abbr+"\"  /> ").append("</div>"
								+ "</a></div>");
					 }
					}catch(Exception e){
						Logger.error("disCouponIdQRImage",e);
					}
					
				} catch (Exception e) {
					Logger.error("disCouponIdQRImage:" + pidString, e);
				}
			}
		}
		

		return ok(imgHtml.toString());
		
	}
	

	/**
	 * 
	 * @param content
	 * @return
	 */
	public static Result disParkIdQRImage(String pidarray,int width,int height) {
		Logger.info("start to disQRImage for :"+ pidarray);


		StringBuilder imgHtml = new StringBuilder("");
		
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					
					String encodeString = "";
					
					if(download_url!=null){
						encodeString = download_url;
					}
					encodeString=encodeString+"#"+pid;

				try{
					String basePath = UploadController.image_store_path;
					String urlHeader = ConfigHelper.getString("image.url.header");
						
					String abbr = "/qr/pk_"+pid+".png";
					
					String imagePath = basePath+abbr;
					
					File file = new File(imagePath);
					if(!file.getParentFile().exists()){
						file.getParentFile().mkdirs();
					}
					
					Logger.info("QR image path:"+ imagePath);
					
					TParkInfoProd prod = TParkInfoProd.findDataById(pid);
					
					if (ZXingUtil.encodeQRCodeImage(encodeString, null, imagePath, width, height, null)) {
						imgHtml.append("<div class=\"row-fluid\">").append("<h4>"+prod.parkname+"<br/><small>"+prod.address+"</small></h4>")
						.append("<a class=\"fancybox-button\" data-rel=\"fancybox-button\" title=\""+prod.parkname+"\" target=\"blank\" href=\"/"+urlHeader+abbr+"\">")
						.append("<div class=\"zoom\">").append("<img src=\""+urlHeader+abbr+"\"  /> ").append("</div>"
								+ "</a></div>");
					 }
					}catch(Exception e){
						Logger.error("disParkIdQRImage",e);
					}
					
				} catch (Exception e) {
					Logger.error("disParkIdQRImage:" + pidString, e);
				}
			}
		}


		return ok(imgHtml.toString());
		
	}
	
		
	/**
	 * 解析二维码结果
	 * 前期我们认为应该是 http://-------/parkingmc.apk#123456;
	 * @param scanResult
	 * @return
	 */
	public static long decodeScan(String scanResult) throws Exception{
		
		Logger.info(">>>>>>scan result:"+scanResult);
		
		if(scanResult!=null&&!scanResult.trim().equals("")){
			if(scanResult.startsWith("http")){ 
				String[] scans = scanResult.split("\\#");
				if(scans.length>1){
					//第二个就是parkingId
					String parkStr = scans[1];
					try{
						long parking = Long.valueOf(parkStr);
						Logger.info(">>>>>>decode:"+parking);
						return parking;
					}catch(Exception e){
						throw new Exception("停车场ID解析出错");
					}
					
				}else{
					throw new Exception("停车场二维码图片解析出错");
				}
			}else{
				try{
					long parking = Long.valueOf(scanResult);
					Logger.info(">>>>>>decode:"+parking);
					return parking;
				}catch(Exception e){
					throw new Exception("停车场ID解析出错");
				}
			}
		}
		return 0;
	}
	
	
	/**
	 * 扫码进场
	 * @param parkingId
	 * @param orderId
	 * @return
	 */
	public static Result scanForIn(long orderId,String scanResult){
		Logger.info("start to scan from entrance for order:"+orderId);
		
			ComResponse<TOrder>  response = new ComResponse<TOrder>();
			try {
				
				TOrder order = TOrder.findDataById(orderId);
				if(order==null){
					throw new Exception("系统无法找到该订单:"+orderId);
				}
				
				long parkIdFromScan = decodeScan(scanResult);
				
				if(parkIdFromScan<=0){
					throw new Exception("不能识别停车场信息");
				}
				
				TParkInfoProd parkinfo = order.parkInfo;
				if(parkinfo==null){
					throw new Exception("该订单无有效停车场");
				}else if(parkinfo.parkId!=parkIdFromScan){
					throw new Exception("该订单不属于此停车场，请检查");
				}
				
				int feeType = parkinfo.feeType;
				
				order.startDate = new Date();
				
				if(feeType==1){//计时收费
					response.setExtendResponseContext("车辆进场扫码成功");
					TOrder.saveData(order);
				}else{//计次收费
					response.setExtendResponseContext("pass");
					order.orderStatus=Constants.PAYMENT_STATUS_FINISH;
					order.startDate=new Date();
					Set<String> options = new HashSet<String>();
					options.add("orderStatus");
					options.add("startDate");
					Ebean.update(order, options);
					//***********已经完成的订单需要移到历史表**************/
					TOrderHis.moveToHisFromOrder(orderId,Constants.ORDER_TYPE_FINISH);
				}
				
				
				
				response.setResponseStatus(ComResponse.STATUS_OK);
				response.setResponseEntity(order);
				
				
				LogController.debug("start to save calculated time for entrance date");
				
			} catch (Exception e) {
				response.setResponseStatus(ComResponse.STATUS_FAIL);
				response.setErrorMessage(e.getMessage());
				Logger.error("parkingOutForAdm", e);
			}
			String tempJsonString = gsonBuilderWithExpose.toJson(response);
			JsonNode json = Json.parse(tempJsonString);
			return ok(json);
			
		}
}
