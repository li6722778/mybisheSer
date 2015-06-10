package controllers;

import java.util.List;

import models.info.TParkInfoPro_Loc;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

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
}
