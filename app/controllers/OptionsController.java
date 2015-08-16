package controllers;

import models.info.TOptions;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Constants;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OptionsController extends Controller {
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
	public static Result getOptions(int type) {
		Logger.info("start to get option:"+type);
		String json = gsonBuilderWithExpose.toJson(TOptions.findOption(type));
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("getOptions result:" + json);
		return ok(jsonNode);
	}


/**
 * 获取具体某个option
 * @param type
 * @return
 */
	public static Result getOption(int type) {
		Logger.info("start to get option:"+type);
		String json = gsonBuilderWithExpose.toJson(TOptions.findOption(type));
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("getOptions result:" + json);
		return ok(jsonNode);
	}

	
	
	
	
}
