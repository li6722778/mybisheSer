package controllers;

import models.info.TuserInfo;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * login
 * 
 * @author woderchen
 *
 */
public class LoginController extends Controller {
	static Gson gsonBuilderWithExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	@BasicAuth
	public static Result loginUser(Long userphone) {
		Logger.info("start to login with" + userphone);
		TuserInfo userinfo = TuserInfo.findDataByPhoneId(userphone);
		String json = gsonBuilderWithExpose.toJson(userinfo);

		// added session
		if (userinfo != null) {
			session("userphone", "" + userinfo.userPhone);
			session("username", "" + userinfo.userName);
			session("userid", "" + userinfo.userid);
		}

		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		LogController.debug(userphone + " logined");
		return ok(jsonNode);
	}

	/**
	 * 
	 * @return
	 */
	public static Result logout() {
		session().clear();

		LogController.debug("logout");
		return ok("You've been logged out");
	}

}
