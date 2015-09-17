package controllers;

import models.info.Tredirecturl;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DirectController extends Controller {
	
	
	public static Gson gsonBuilderWithExpose = new GsonBuilder()
	.excludeFieldsWithoutExposeAnnotation()
	.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	
	
	
	public static Result saveredicturl(String uniqueurl, String redicturl)
	{
		
		Logger.info("start to save redicturl:"+redicturl);
		ComResponse<String> response = new ComResponse<String>();
	
		Tredirecturl tredicturl =Tredirecturl.find.byId(uniqueurl);
		//存在数据，更新手机号码
		if(tredicturl!=null)
		{
			response.setResponseStatus(ComResponse.STATUS_OK);
			LogController.info("redicturl is exist:");
		}
		else {
			try {
				Tredirecturl.saveredicturl(uniqueurl, redicturl);
					response.setResponseStatus(ComResponse.STATUS_OK);
					LogController.info("save redicturl success:");	
			} catch (Exception e) {
				response.setResponseStatus(ComResponse.STATUS_FAIL);
			}
			
		}
		
		
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		return ok(tempJsonString);
	}
	
	
	
	public static Result getdirecturl(String uniqueurl) {
		Logger.info("start to find the redicturl");
		String json = gsonBuilderWithExpose.toJson(Tredirecturl.find.byId(uniqueurl));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}
	
	
	
	@BasicAuth
	public static Result saveData() {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);

		Tredirecturl redicturl = gsonBuilderWithExpose.fromJson(request,
				Tredirecturl.class);
		ComResponse<Tredirecturl> response = new ComResponse<Tredirecturl>();
		
		Tredirecturl sqltredicturl =Tredirecturl.find.byId(redicturl.uniqueurl);
		//通过post过来的重定向地址在数据库为空
		if(sqltredicturl==null){
		try {
			Tredirecturl.saveredicturl(redicturl.uniqueurl, redicturl.redicturl);
			response.setResponseStatus(ComResponse.STATUS_OK);
			LogController.info("save redicturl success data:" + redicturl.redicturl);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		}
		else {
			response.setResponseStatus(ComResponse.STATUS_OK);
			LogController.info("redicturl in sql:" + redicturl.redicturl);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}

}
