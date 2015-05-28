package controllers;

import models.info.TVersion;
import play.Logger;
import play.cache.Cached;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VersionController extends Controller {
	public static Gson gsonBuilderWithExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();


	/**
	 * 版本
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static Result getVersion() {
		Logger.info("start to get all data");
		String json = gsonBuilderWithExpose.toJson(TVersion.findVersion());
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}

	
	/**
	 * 新建或更新数据，ID不为空或大于0，则是更新数据；否则则是新建数据
	 * @return
	 */
	@BasicAuth
	public static Result saveData() {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		TVersion data = gsonBuilderWithExpose.fromJson(request, TVersion.class);
		ComResponse<TVersion>  response = new ComResponse<TVersion>();
		try {
			TVersion.saveData(data);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(data);
			response.setExtendResponseContext("更新数据成功.");
			LogController.info("save version:"+data.version);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	
}
