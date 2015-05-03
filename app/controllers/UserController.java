package controllers;

import models.UserInfo;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;

public class UserController extends Controller {
	
	@BasicAuth
	public static Result getUsers(Long userid) {
		Logger.info("start to get users");
		JsonNode json = Json.toJson(UserInfo.findUser(userid));
		String jsonString = Json.stringify(json);
		Logger.debug("got Users:"+jsonString);
		return ok(json);
    }
}
