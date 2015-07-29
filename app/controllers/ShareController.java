package controllers;

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
		Logger.info("start to query data");
		ComResponse<TShare> response = new ComResponse<TShare>();
		TShare share = TShare.findDataById(id);
	//不在分享表中，赠送优惠劵
		if(share==null||share.equals(""))
		{
			// 赠送优惠劵
			TOptions options = TOptions.findOption(3);
			String Counponcode = options.textObject;
			if (Counponcode != null&& !(Counponcode.toString().trim().equals(""))) {
				Counponcode = Counponcode.replace("，", ",");
				String[] counponcodes = Counponcode.split(",");
				if (counponcodes.length > 0) {
					for (int i = 0; i < counponcodes.length; i++) {
						CounponController.getcounpon(counponcodes[i], id);
					}
				}
			}
			TShare.saveshare(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
			
			
		}
		
		else {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
		}
		
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
		
	
	}
	
	
	
}
