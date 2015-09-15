package controllers;

import java.util.Date;
import java.util.Random;

import models.info.TOptions;
import models.info.TuserInfo;
import models.info.Twxuserinfo;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;

/**
 * @author mxs E-mail:308348194@qq.com
 * @version 创建时间：2015年9月10日 下午2:02:40
 */
public class WXuserinfoController extends Controller {
	
	public static Gson gsonBuilderWithExpose = new GsonBuilder()
	.excludeFieldsWithoutExposeAnnotation()
	.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	

	
	/**
	 * 根据url，得到用户列表
	 * 
	 * @param url
	 * @return
	 */
	public static Result getuserinfolist(String url) {
		Logger.info("start to get userinfolist");
		String json = gsonBuilderWithExpose.toJson(Twxuserinfo.getwxuserinfos(url));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}
	
	
	public static Result saveuserinfo ( String openid, String nickname, String headimgurl,int getcoupnprice, String uniqueurl, String userphone)
	{
		
		Logger.info("start to save Wxshare userinfo");
		ComResponse<String> response = new ComResponse<String>();
	
		Twxuserinfo twxuserinfo =Twxuserinfo.getuser(uniqueurl, openid);
		//存在数据，更新手机号码
		if(twxuserinfo!=null)
		{
			Twxuserinfo.updateuserinfo(uniqueurl, userphone, openid);
			response.setResponseStatus(ComResponse.STATUS_OK);
			LogController.info("update wxshare  userinfo success:");
		}
		else {
			try {
				String shareword = getRandomshareword();
				if(shareword!=null&&(!shareword.equals(""))){
					Twxuserinfo.saveuserinfo(openid, nickname, headimgurl,shareword, getcoupnprice, uniqueurl, userphone);
					response.setResponseStatus(ComResponse.STATUS_OK);
					LogController.info("save wxshare  userinfo success:");	
				}
				
				
			} catch (Exception e) {
				response.setResponseStatus(ComResponse.STATUS_FAIL);
			}
			
		}
		
		
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		return ok("success_jsonpCallback(" + tempJsonString + ")");
	}
	
	

	
	
	public static String getRandomshareword()
	{
		// 获取设置的领取优惠劵分享语
	  TOptions option = TOptions.findOption(10);
	  String[] sharewords = null;
	  String shareword = option.textObject;
		if (shareword != null && !(shareword.toString().trim().equals(""))) {
			shareword = shareword.replace("，", ",");
            sharewords = shareword.split(",");
		}
		if(sharewords!=null){
	   int max=sharewords.length-1;
       int min=1;
       Random random = new Random();
       int s = random.nextInt(max)%(max-min+1) + min;
       Logger.info("random s:"+s);
       return sharewords[s-1];

	 }

		return null;
		
	}
	
	public static Result getcurrentuserinfo(String openid) {
		Logger.info("start to get userinfolist");
		String json = gsonBuilderWithExpose.toJson(Twxuserinfo.getwxuserinfo(openid));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}
	

}
