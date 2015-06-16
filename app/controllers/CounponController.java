package controllers;

import java.util.Date;

import models.info.TCouponEntity;
import models.info.TUseCouponEntity;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.CommFindEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CounponController extends Controller{
	public static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	
	
	public static Result getAllDataByUser(int currentPage, int pageSize, String orderBy,long user) {
		Logger.info("start to get all data by user");
		CommFindEntity<TUseCouponEntity> allData = TUseCouponEntity.findPageDataByuserid(currentPage,
				pageSize, orderBy,user);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.info("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	
	
	/*public static Result setcounpon()
	{
		
		TCouponEntity counponbean=new TCouponEntity();
		counponbean.isable=0;
		counponbean.count=100;
		counponbean.createName="wlw";
		counponbean.money=100d;
		counponbean.createDate=new Date();
		counponbean.scancount=0;
		counponbean.counponCode="bbb";
		TCouponEntity.saveData(counponbean);
		return ok("haha");
		
		
	}
	*/
	
	
	
	public static Result getcounpon(String counponcode,Long userid)
	{
		ComResponse<TCouponEntity>  response = new ComResponse<TCouponEntity>();
		TCouponEntity counponbean=TCouponEntity.findentityByCode(counponcode);
		TuserInfo useinfo;
		if(counponbean==null||counponbean.scancount>counponbean.count)
		{
			Logger.debug("not find TCouponEntity");
			return ok();
			
		}else
		{
			useinfo=TuserInfo.findDataById(userid);
			if(useinfo==null)
			{
				Logger.debug("not find useinfo");
				return ok();
			}else
			{
				
				try {
					//更新用户优惠表
					TUseCouponEntity databean=new TUseCouponEntity();
					databean.scanDate=new Date();
					databean.Id=null;
					databean.userInfo=useinfo;
					databean.counponentity=counponbean;
					TUseCouponEntity.saveData(databean);
					//更新优惠信息表
					counponbean.scancount=counponbean.scancount+1;
					TCouponEntity.saveData(counponbean);
					
					response.setResponseStatus(ComResponse.STATUS_OK);
					response.setResponseEntity(counponbean);
					response.setExtendResponseContext("更新数据成功.");
					LogController.info("save comments data:"+counponbean.scancount);
				} catch (Exception e) {
					response.setResponseStatus(ComResponse.STATUS_FAIL);
					response.setErrorMessage(e.getMessage());
					Logger.error("", e);
				}
				String tempJsonString = gsonBuilderWithExpose.toJson(response);
				JsonNode json = Json.parse(tempJsonString);
				Logger.info("getcounpon result@@@@@@@:" + tempJsonString);
				return ok(json);
			}
			
		
		}
		
		
		
	}
	
	public static Result usecounpon(Long id,Long userid)
	{
		ComResponse<TUseCouponEntity>  response = new ComResponse<TUseCouponEntity>();
		TUseCouponEntity usecounponbean=TUseCouponEntity.findDataById(id);
		if(usecounponbean.userInfo.userid!=userid||usecounponbean.isable!=0)
		{
			return ok();
			
			
		}else
		{
			try{
			usecounponbean.isable=1;
			TUseCouponEntity.saveData(usecounponbean);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(usecounponbean);
			response.setExtendResponseContext("更新数据成功.");
			}catch (Exception e) {
				response.setResponseStatus(ComResponse.STATUS_FAIL);
				response.setErrorMessage(e.getMessage());
				Logger.error("", e);
			}
			String tempJsonString = gsonBuilderWithExpose.toJson(response);
			JsonNode json = Json.parse(tempJsonString);
			return ok(json);
		}
		
		
		
		
	}
	
	
	
	
	
}
