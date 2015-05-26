package controllers;

import models.info.TLog;
import models.info.TParkInfo_Comment;
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

public class LogController extends Controller{
	public static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	/**
	 * 得到所有的数据，这里是查询出所有的数据，如果有其他条件，需要仿照TParkInfo_Comment.findData写一些方法
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	@BasicAuth
	public static Result getAllData(int currentPage, int pageSize,
			String orderBy) {
		Logger.info("start to get all data");
		CommFindEntity<TLog> allData = TLog.findPageData(currentPage,
				pageSize, orderBy);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	
	@BasicAuth
	public static void saveData(String logcontent,String extLog) {
		TLog log = new TLog();
		log.content = logcontent;
		log.extraString = extLog;
		log.operateName ="";
		TLog.saveData(log);
	}
}
