package controllers;

import java.util.Date;

import models.info.TCouponEntity;
import models.info.TCouponHis;
import models.info.TUseCouponEntity;
import models.info.TUseCouponHis;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.CommFindEntity;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CounponController extends Controller{
	public static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	
	
	public static Result getAllDataByUser(int currentPage, int pageSize, String orderBy,long user) {
		Logger.info("start to get all data by user");
		CommFindEntity<TUseCouponEntity> allData = TUseCouponEntity.findValidPageDataByuserid(currentPage,
				pageSize, orderBy,user);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.info("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	
	
	@BasicAuth
	public static Result getcounpon(String counponcode,Long userid)
	{
		
		ComResponse<TCouponEntity>  response = new ComResponse<TCouponEntity>();
		TCouponEntity counponbean=TCouponEntity.findentityByCode(counponcode);
		TuserInfo useinfo;
		if(counponbean==null||(counponbean.count>0&&counponbean.scancount>=counponbean.count)||counponbean.isable==0||counponbean.isable==2)
		{
			Logger.debug("not find TCouponEntity");
			if(counponbean!=null&&counponbean.scancount>=counponbean.count&&!TUseCouponEntity.findExistCouponByCouponId(counponbean.counponId))
			{
				
				TCouponHis.moveToHis(counponbean);
				
			}
			return ok();
			
		}else
		{
			Date startDate = counponbean.startDate;
			Date endDate = counponbean.endDate;
			Date currentDate = new Date();
			if(startDate!=null){ //如果还没有到
				if(startDate.after(currentDate)){
					Logger.debug("not find coupon as start Date after current Date");
					return ok();
				}
				
			}else{//优惠券没有开始时间
				if(endDate!=null){//失效了
					if(endDate.before(currentDate)){
						if(!TUseCouponEntity.findExistCouponByCouponId(counponbean.counponId))
						{
							TCouponHis.moveToHis(counponbean);
						}
						Logger.debug("not find coupon as end Date before current Date");
						return ok();
					}
				}
				
			}
			
			if(endDate!=null){//失效了
				if(endDate.before(currentDate)){
					if(startDate.before(currentDate)){
						if(!TUseCouponEntity.findExistCouponByCouponId(counponbean.counponId))
						{
							TCouponHis.moveToHis(counponbean);
						}
						Logger.debug("not find coupon as end Date before current Date");
						return ok();
					}
				}
			}
			
			//判断是否已经有优惠券了
			if(TUseCouponEntity.findExistCouponByUserIdAndId(counponbean.counponId, userid)||TUseCouponHis.findExistCouponByUserIdAndId(counponbean.counponId, userid)){
				Logger.debug("existing coupon!");
				return ok();
			}
			
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
					databean.counponId=counponbean.counponId;
					databean.isable = 1;
					TUseCouponEntity.saveData(databean);
					//更新优惠信息表
					counponbean.scancount=counponbean.scancount+1;
					TCouponEntity.saveData(counponbean);
					
					response.setResponseStatus(ComResponse.STATUS_OK);
					response.setResponseEntity(counponbean);
					response.setExtendResponseContext("更新数据成功.");
					LogController.info("save coupon data:"+counponbean.counponCode);
				} catch (Exception e) {
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
		
	
	@BasicAuth
	public static Result getsharecounpon(String counponcode,Long userid)
	{
		
		
		ComResponse<TCouponEntity>  response = new ComResponse<TCouponEntity>();
		TCouponEntity counponbean=TCouponEntity.findentityByCode(counponcode);
		TuserInfo useinfo;
		if(counponbean==null||(counponbean.count>0&&counponbean.scancount>=counponbean.count)||counponbean.isable==0||counponbean.isable==2)
		{
			Logger.debug("not find TCouponEntity");
			return ok();
			
		}else
		{
			Date startDate = counponbean.startDate;
			Date endDate = counponbean.endDate;
			Date currentDate = new Date();
			if(startDate!=null){ //如果还没有到
				if(startDate.after(currentDate)){
					Logger.debug("not find coupon as start Date after current Date");
					return ok();
				}
				
			}else{//优惠券没有开始时间
				if(endDate!=null){//失效了
					if(endDate.before(currentDate)){
						Logger.debug("not find coupon as end Date before current Date");
						return ok();
					}
				}
				
			}
			
			if(endDate!=null){//失效了
				if(endDate.before(currentDate)){
					if(startDate.before(currentDate)){
						Logger.debug("not find coupon as end Date before current Date");
						return ok();
					}
				}
			}
			
			
			useinfo=TuserInfo.findDataById(userid);
			if(useinfo==null)
			{
				Logger.debug("not find useinfo");
				return ok();
			}else
			{
				
				try {
					//更新用户优惠表 lwei
					TUseCouponEntity databean=new TUseCouponEntity();
					databean.scanDate=new Date();
					databean.Id=null;
					databean.userInfo=useinfo;
					databean.counponentity=counponbean;
					databean.counponId=counponbean.counponId;
					databean.isable = 1;
					databean.type=1;
					TUseCouponEntity.saveData(databean);
					//更新优惠信息表
					counponbean.scancount=counponbean.scancount+1;
					TCouponEntity.saveData(counponbean);
					
					response.setResponseStatus(ComResponse.STATUS_OK);
					response.setResponseEntity(counponbean);
					response.setExtendResponseContext("更新数据成功.");
					LogController.info("save coupon data:"+counponbean.counponCode);
				} catch (Exception e) {
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
	
	
}
