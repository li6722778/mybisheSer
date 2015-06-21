package controllers;

import java.util.Date;

import models.info.TOrder;
import models.info.TParkInfoProd;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.ConfigHelper;

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
		
		
	/**
	 * 解析二维码结果
	 * 前期我们认为应该是 http://-------/ddddd?parkid=123456;
	 * @param scanResult
	 * @return
	 */
	public static long decodeScan(String scanResult) throws Exception{
		
		Logger.info(">>>>>>scan result:"+scanResult);
		
		if(scanResult!=null&&!scanResult.trim().equals("")){
			if(scanResult.startsWith("http")){ 
				String[] scans = scanResult.split("?");
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
				
				if(feeType==1){//计时收费
					
				}else{//计次收费
					
				}
				
				order.startDate = new Date();
				TOrder.saveData(order);
				
				response.setResponseStatus(ComResponse.STATUS_OK);
				response.setResponseEntity(order);
				response.setExtendResponseContext("车辆进场扫码成功");
				
				LogController.debug("start to save caculate time for start date");
				
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
