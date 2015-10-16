package controllers;

import models.info.Tsign;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SignController extends Controller {

	public static Gson gsonBuilderWithExpose = new GsonBuilder()
	.excludeFieldsWithoutExposeAnnotation()
	.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	
	
	public static Result saveSign(Long userid) {
		Logger.info("start to save sign info");
		ComResponse<String> response = new ComResponse<String>();
		Tsign sign =Tsign.find.byId(userid);
		if(sign!=null)
		{
			Logger.info("update sign");
			Tsign.updateSign(userid);
			response.setResponseStatus(ComResponse.STATUS_OK);
		}
		else
		{
			Logger.info("save sign");
		     TuserInfo userInfo=TuserInfo.findDataById(userid);
			Tsign.savesign(userid, userInfo.userPhone);
			response.setResponseStatus(ComResponse.STATUS_OK);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		return ok(tempJsonString);
	}
	
	
	public static Result getsignRecord(Long userid) {
		Logger.info("start to query sign record for>>"+userid);
		String json = gsonBuilderWithExpose.toJson(Tsign.find.byId(userid));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}
}
