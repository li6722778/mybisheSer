package controllers;

import java.util.List;

import models.info.MapMakers;
import models.info.TParkInfoPro_Loc;
import models.info.TTakeCash;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.CommFindEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 找点
 * @author woderchen
 *
 */
public class MapFindController extends Controller{
	public static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	
	public static Result findNearbyParking(double myLat,double myLng, float scope) {
		Logger.debug("start to find Nearby Parking,myLat:"+myLat+",myLng:"+myLng+",scope");
		List<TParkInfoPro_Loc> allData = TParkInfoPro_Loc.findNearbyParking(myLat,myLng, scope);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("List result:" + json);
		return ok(jsonNode);
	}
	
	
	/**
	 * 查询地图列表数据
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static Result getNearbyParkingForPreview(int currentPage, int pageSize,
			String orderBy,double myLat,double myLng, float scope) {
		Logger.info("getParkCommentsAllData");
		CommFindEntity<TParkInfoPro_Loc> allData = TParkInfoPro_Loc.findPageData(currentPage,
				pageSize, orderBy,myLat,myLng,scope);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	
	public static Result findParkingEntrance(long parkLocId) {
		Logger.debug("start to find Entrance by"+parkLocId);
		TParkInfoPro_Loc locData = TParkInfoPro_Loc.getLocationPointByKey(parkLocId);
		String json = gsonBuilderWithExpose.toJson(locData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("loc data:" + json);
		return ok(jsonNode);
	}
	
	public static Result findNearbyParkingSimpleMode(double myLat,double myLng, float scope) {
		Logger.debug("start to find Nearby Parking for simple mode,myLat:"+myLat+",myLng:"+myLng+",scope");
		List<MapMakers> allData = MapMakers.findNearbyParkingSimple(myLat,myLng, scope);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("List result:" + json);
		return ok(jsonNode);
	}
}
