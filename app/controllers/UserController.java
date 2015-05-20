package controllers;

import models.info.TuserInfo;
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
	static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	/**
	 * 根据主键ID，得到数据
	 * @param userid
	 * @return
	 */
	@BasicAuth
	public static Result getDataById(Long userid) {
		Logger.info("start to get data");
		String json = gsonBuilderWithExpose.toJson(TuserInfo.findDataById(userid));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
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
		String json = gsonBuilderWithExpose.toJson(allData);
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
		
		TuserInfo user = gsonBuilderWithExpose.fromJson(request, TuserInfo.class);
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
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	
	/**
	 * 注册用户，不需要权限认证
	 * @return
	 */
	public static Result regUser() {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		TuserInfo user = gsonBuilderWithExpose.fromJson(request, TuserInfo.class);
		ComResponse<TuserInfo>  response = new ComResponse<TuserInfo>();
		try {
			TuserInfo.saveData(user);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(user);
			response.setExtendResponseContext("用户注册成功.");
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
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
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
}
