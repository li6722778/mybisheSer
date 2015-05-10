package controllers;

import models.TuserInfo;
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

public class UserController extends Controller {
	public static Gson gsonBuilder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	/**
	 * 根据主键ID，得到数据
	 * @param userid
	 * @return
	 */
	@BasicAuth
	public static Result getDataById(Long userid) {
		Logger.info("start to get data");
		JsonNode json = Json.toJson(TuserInfo.findDataById(userid));
		String jsonString = Json.stringify(json);
		Logger.debug("got Data:" + jsonString);
		return ok(json);
	}

	/**
	 * 得到所有的数据，这里是查询出所有的数据，如果有其他条件，需要仿照TuserInfo.findData写一些方法
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	@BasicAuth
	public static Result getAllData(int currentPage, int pageSize,
			String orderBy) {
		Logger.info("start to all data");
		CommFindEntity<TuserInfo> allData = TuserInfo.findData(currentPage,
				pageSize, orderBy);
		JsonNode json = Json.toJson(allData);
		// String jsonString = Json.stringify(json);
		Logger.debug("Got Data Count:" + allData.getRowCount());
		return ok(json);
	}

	/**
	 * 新建或更新数据，ID不为空或大于0，则是更新数据；否则则是新建数据
	 * @return
	 */
	@BasicAuth
	public static Result saveData() {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		TuserInfo user = gsonBuilder.fromJson(request, TuserInfo.class);
		ComResponse<TuserInfo>  response = new ComResponse<TuserInfo>();
		try {
			TuserInfo.saveData(user);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(user);
			response.setExtendResponseContext("更新数据成功.");
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		JsonNode json = Json.toJson(response);
		return ok(json);
	}
	
	
	/**
	 * 根据ID删除数据，如果有其他条件，就需要仿照TuserInfo.deleteData，写类似的方法
	 * @param id
	 * @return
	 */
	@BasicAuth
	public static Result deleteData(long id){
		Logger.info("start to delete data:" + id);
		ComResponse<TuserInfo>  response = new ComResponse<TuserInfo>();
		try {
			TuserInfo.deleteData(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setExtendResponseContext("删除数据成功.");
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		JsonNode json = Json.toJson(response);
		return ok(json);
	}
}
