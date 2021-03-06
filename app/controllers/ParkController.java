package controllers;

import java.io.File;
import java.util.List;

import models.info.TParkInfo;
import models.info.TParkInfo_Img;
import models.info.TParkInfo_Loc;
import play.Logger;
import play.cache.Cached;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.CommFindEntity;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 停车场相关,这些信息都是未经审批的
 * @author woderchen
 *
 */
public class ParkController extends Controller{
	public static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	public static Result getDataById(Long id) {
		Logger.info("start to get data");
		
		String json = gsonBuilderWithExpose.toJson(TParkInfo.findDataById(id));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}

	public static Result getAllData(int currentPage, int pageSize, String orderBy) {
		Logger.info("start to get all data");
		CommFindEntity<TParkInfo> allData = TParkInfo.findData(currentPage,
				pageSize, orderBy);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.info("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	
	
	public static Result getAllDataByUser(int currentPage, int pageSize, String orderBy,long user) {
		Logger.info("start to get all data by user");
		CommFindEntity<TParkInfo> allData = TParkInfo.findData(currentPage,
				pageSize, orderBy,user);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.info("CommFindEntity result:" + json);
		return ok(jsonNode);
	}


	@BasicAuth
	public static Result saveData() {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		TParkInfo info = gsonBuilderWithExpose.fromJson(request, TParkInfo.class);
		ComResponse<TParkInfo>  response = new ComResponse<TParkInfo>();
		try {
			TParkInfo.saveData(info);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(info);
			response.setExtendResponseContext("更新数据成功.");
			LogController.info("save park data:"+info.parkname);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String json = gsonBuilderWithExpose.toJson(response);
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("ComResponse result:"+json);
		
		return ok(jsonNode);
	}


	@BasicAuth
	public static Result deleteData(Long id) {
		Logger.info("start to delete data:" + id);
		ComResponse<TParkInfo>  response = new ComResponse<TParkInfo>();
		try {
			TParkInfo.deleteData(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setExtendResponseContext("删除数据成功.");
			LogController.info("delete park data:"+id);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		
		return ok(json);
	}
	
	
	/**
	 * 得到位置的停车信息
	 * @param parkId
	 * @param type
	 * @param status
	 * @return
	 */
	public static Result getLocationDataById(Long parkId,int type,int status) {
		Logger.info("start to get data for getLocationDataById");

		CommFindEntity<TParkInfo_Loc> allData = TParkInfo_Loc.findData(parkId,type,status);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}
	
	/**
	 * 得到相关的图片信息
	 * @param parkId
	 * @param type
	 * @param status
	 * @return
	 */
	public static Result getImgDataById(Long parkId) {
		Logger.info("start to get data for getLocationDataById");

		CommFindEntity<TParkInfo_Img> allData = TParkInfo_Img.findData(parkId);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}
	
	/**
	 * 删除经纬度
	 * @param id
	 * @return
	 */
	@BasicAuth
	public static Result deleteLocData(Long id) {
		Logger.info("start to delete location data:" + id);
		ComResponse<TParkInfo_Loc>  response = new ComResponse<TParkInfo_Loc>();
		try {
			TParkInfo_Loc.deleteData(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setExtendResponseContext("删除数据成功.");
			LogController.info("delete park location data:"+id);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		
		return ok(json);
	}

}
