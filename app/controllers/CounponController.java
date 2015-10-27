package controllers;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import action.BasicAuth;
import actor.model.ScanCounponModel;
import actor.model.TemplateSMS;
import models.info.TCouponEntity;
import models.info.TUseCouponEntity;
import models.info.TuserInfo;
import play.Logger;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ActorHelper;
import utils.ComResponse;
import utils.CommFindEntity;

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
	
	
//	@BasicAuth
//	public static Result getcounpon(String counponcode,Long userid)
//	{
//		
//		ComResponse<TCouponEntity>  response = new ComResponse<TCouponEntity>();
//		
//		
//		
//		
//		String tempJsonString = gsonBuilderWithExpose.toJson(response);
//		JsonNode json = Json.parse(tempJsonString);
//		return ok(json);
//	}
	
	/**
	 * 扫描优惠卷，需要等到线程回答是否扫码成功
	 * @param counponcode
	 * @param userid
	 * @return
	 */
	@BasicAuth
	public static Promise<Result> getcounpon(String counponcode,Long userid)
	{
		ScanCounponModel model = new ScanCounponModel();
		model.counponcode = counponcode;
		model.userid = userid;
		model.type = ScanCounponModel.TYPE_DEFAULT;
		
		return Promise.wrap(ActorHelper.getInstant().askCouponFuture(model)).map(
                new Function<Object, Result>() {
                    public Result apply(Object response) {
                    	  Logger.info("ScanCouponActor=>message:"+response);
                         if( response instanceof ScanCounponModel ) {
                        	 ScanCounponModel message = ( ScanCounponModel )response;
                        	 
                        	ComResponse<TCouponEntity>  responseCoupon = new ComResponse<TCouponEntity>();
                     		if (message.responseResult == 0){
                        	     responseCoupon.setResponseStatus(ComResponse.STATUS_OK);
                     		}else{
                     		     responseCoupon.setResponseStatus(ComResponse.STATUS_FAIL);
                     		}
                     		String tempJsonString = gsonBuilderWithExpose.toJson(responseCoupon);
                     		JsonNode json = Json.parse(tempJsonString);
                        	 
                        	 Logger.debug("ScanCouponActor=>result:"+message.responseResult);
                              return ok(json);
                         }
                        return notFound( "Message is not of type MyMessage" );
                    }
                }
            );
	}
		
	/**
	 * 分享优惠卷
	 * @param counponcode
	 * @param userid
	 */
	public static void getsharecounpon(String counponcode,Long userid)
	{
		ScanCounponModel model = new ScanCounponModel();
		model.counponcode = counponcode;
		model.userid = userid;
		model.type = ScanCounponModel.TYPE_SHARE;
		
		ActorHelper.getInstant().sendCouponMessage(model);
	}
}
