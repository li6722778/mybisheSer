package controllers;

import models.info.TParkInfo;
import models.info.TParkInfo_Img;
import models.info.TParkInfo_Loc;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.CommFindEntity;

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
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}


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


	public static Result deleteData(Long id) {
		Logger.info("start to delete data:" + id);
		ComResponse<TParkInfo>  response = new ComResponse<TParkInfo>();
		try {
			TParkInfo.deleteData(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setExtendResponseContext("删除数据成功.");
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
	
}
