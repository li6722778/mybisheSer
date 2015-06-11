package controllers;

import java.util.Date;
import java.util.Random;

import javax.inject.Inject;

import models.info.TVerifyCode;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ConfigHelper;

import com.fasterxml.jackson.databind.JsonNode;

public class SMSController extends Controller{
	static @Inject WSClient ws;
	
	public static String rest_3part_smsuri = ConfigHelper
			.getString("rest.3part.sms.uri");
	
	public static String rest_3part_smsappid = ConfigHelper
			.getString("rest.3part.sms.appid");
	public static String rest_3part_smsparam = ConfigHelper
			.getString("rest.3part.sms.param");
	public static String rest_3part_smstmpid = ConfigHelper
			.getString("rest.3part.sms.templateId");
	/**
	 * 请求验证码
	 * @return
	 */
	public static Result requestSMSVerify(long phone){
		Logger.info("start to request SMS verification to:"+phone);

		
		TuserInfo usrinfo = TuserInfo.findDataByPhoneId(phone);
		if(usrinfo!=null){
			Logger.info("there is existing user phone, can not register again!");
			return ok("该手机号码已经注册");
		}
		
		
		String phoneString = String.valueOf(phone);
		
		//得到验证码
		String param = getRandomChar();
		
		if(param.trim().equals("")){
			param="4677";
		}
		
		TVerifyCode verficode = new TVerifyCode();
		verficode.createDate=new Date();
		verficode.phone = phone;
		verficode.verifycode = param;
		TVerifyCode.saveData(verficode);
		
		if(rest_3part_smsparam!=null&&!rest_3part_smsparam.trim().equals("")){
			param+=","+rest_3part_smsparam;
		}
		
		
		
		Logger.debug("-----rest_3part_smsuri:"+rest_3part_smsuri);
		Logger.debug("-----rest_3part_smsappid:"+rest_3part_smsappid);
		Logger.debug("-----rest_3part_smsparam:"+param);
		
		TemplateSMS sms = new TemplateSMS(rest_3part_smsappid,param,rest_3part_smstmpid,phoneString);
		JsonNode smsjson =  Json.toJson(sms);
       // ws.url(rest_3part_smsuri).post(smsjson);
          
		return ok("验证码请求中,请注意短信查收");
	}
	
	
	private static String getRandomChar(){
		Random r=new Random();
        int tag[]={0,0,0,0,0,0,0,0,0,0};
        String four="";
        int temp=0;
        while(four.length()!=4){
                temp=r.nextInt(10);//随机获取0~9的数字
                if(tag[temp]==0){
                      four+=temp;
                     tag[temp]=1;
                }
        }
       return four;
	}
	
	/**
	 * 这里来自第三方ucpaas.com
	 * @author woderchen
	 *
	 */
	static class TemplateSMS{
		public String appId;
		public String param;
		public String templateId;
		public String to;
		public TemplateSMS(String appId, String param, String templateId,
				String to) {
			super();
			this.appId = appId;
			this.param = param;
			this.templateId = templateId;
			this.to = to;
		}
		
		
	}
}
