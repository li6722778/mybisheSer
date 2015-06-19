package controllers;

import models.info.TLog;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.CommFindEntity;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LogController extends Controller {
	public static Gson gsonBuilderWithExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	public static final int DEBUG=2;
	public static final int INFO=1;
	public static final int WARN=3;
	public static final int ERROR=4;
	/**
	 * 得到所有的数据，这里是查询出所有的数据，如果有其他条件，需要仿照TParkInfo_Comment.findData写一些方法
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static Result getAllData(int currentPage, int pageSize,
			String orderBy) {
		Logger.info("start to get all data");
		CommFindEntity<TLog> allData = TLog.findPageData(currentPage, pageSize,
				orderBy);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}

	public static void debug(String logcontent) {
		saveData(LogController.DEBUG,logcontent, "");
	}
	
	public static void info(String logcontent) {
		saveData(LogController.INFO,logcontent, "");
	}
	
	public static void info(String logcontent,String username) {
		saveData(LogController.INFO,logcontent, "",username);
	}

	public static void warn(String logcontent) {
		saveData(LogController.WARN,logcontent, "");
	}
	
	public static void error(String logcontent) {
		saveData(LogController.ERROR,logcontent, "");
	}

	public static void saveData(int level ,String logcontent, String extLog) {
		try {
			TLog log = new TLog();
			log.content = logcontent;
			log.extraString = extLog;
			log.level = level;
			String userphone = flash("userphone");
			String username = flash("username");
			String userid = flash("userid");
			
			if(userphone==null){
				userphone = session("userphone");
				username = session("username");
				userid = session("usertype");
			}
			
			log.operateName = username + "[" + userid + "," + userphone + "]";
			TLog.saveData(log);
		} catch (Exception e) {
			Logger.warn("save log", e);
		}
	}
	
	public static void saveData(int level ,String logcontent, String extLog, String username) {
		try {
			TLog log = new TLog();
			log.content = logcontent;
			log.extraString = extLog;
			log.level = level;

			log.operateName = username ;
			TLog.saveData(log);
		} catch (Exception e) {
			Logger.warn("save log", e);
		}
	}
}
