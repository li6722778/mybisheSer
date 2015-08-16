package controllers;

import java.util.Date;

import models.info.TOptions;
import models.info.TParkInfo_Comment;
import models.info.TParkInfo_Comment_Keyword;
import models.info.TShare;
import models.info.TTakeCash;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.DateHelper;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ShareController extends Controller {

	public static Gson gsonBuilderWithExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	/**
	 * 查询分享记录
	 * 
	 * @return
	 */

	public static Result getDataById(Long id) {
		
		
		Logger.info("start to query share information");
		ComResponse<TShare> response = new ComResponse<TShare>();
		TShare share = TShare.findDataById(id);
		// 获取每日可分享次数
		TOptions option = TOptions.findOption(4);
		String times = option.textObject;
		if(times==null||times.trim().equals("")){
			times="0";
		}
		int timeint = 0;
		try{
			timeint = Integer.valueOf(times).intValue();
		}catch(Exception e){
			Logger.error("share:option.textObject",e);
		}
		
		if(timeint==0){ //如果没有设置
			
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			Logger.warn("没有设置分享次数");
		}else
		// 不在分享表中，赠送优惠劵
		if (share==null) {
			// 赠送优惠劵
			sendCounpon(id);
			TShare.saveshare(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
		}

		// 在分享列表中
		else if (share!= null) {
			// 比较日期 当前日期>列表分享记录日期 说明当天没参与过分享活动
			Date date = new Date();
			String nowdate =DateHelper.format(date,"yyyy-MM-dd 00:00:00");
			String dbdate =DateHelper.format(share.sharetDate,"yyyy-MM-dd 00:00:00");
			Date nowdate2 = DateHelper.getStringtoDate(nowdate, "yyyy-MM-dd 00:00:00");
			Date dbdate2= DateHelper.getStringtoDate(dbdate, "yyyy-MM-dd 00:00:00");
			if ((nowdate2.compareTo(dbdate2)) > 0) {
				// 赠送优惠劵
				sendCounpon(id);
				TShare.saveshare(id, 1);
				response.setResponseStatus(ComResponse.STATUS_OK);
			}else if ((nowdate2.compareTo(dbdate2))==0) {
				// 说明当前分享次数<限定设置分享次数
				if (share.share < timeint) {
					sendCounpon(id);
					TShare.saveshare(id, share.share + 1);
					response.setResponseStatus(ComResponse.STATUS_OK);
				}
				//说明当天分享次数已经用完
				else if (share.share>=timeint) {
					response.setResponseStatus(ComResponse.STATUS_FAIL);
				}


			} 
			
		

		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);

	}

	public static void sendCounpon(Long id) {

		// 赠送优惠劵
		TOptions options = TOptions.findOption(3);
		String Counponcode = options.textObject;
		if (Counponcode != null && !(Counponcode.toString().trim().equals(""))) {
			Counponcode = Counponcode.replace("，", ",");
			String[] counponcodes = Counponcode.split(",");
			if (counponcodes.length > 0) {
				for (int i = 0; i < counponcodes.length; i++) {
					CounponController.getsharecounpon(counponcodes[i], id);
				}
			}
		}
	}

}
