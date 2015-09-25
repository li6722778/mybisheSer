package controllers;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import models.info.TOptions;
import models.info.TVerifyCode;
import models.info.Tsmssenduser;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Akka;
import play.libs.F.Function;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import utils.ConfigHelper;
import utils.Constants;
import utils.DateHelper;
import utils.EncryptUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

public class SMSController extends Controller {
	static WSClient ws = WS.client();

	public static String rest_3part_smsuri = ConfigHelper
			.getString("rest.3part.sms.uri");

	public static String rest_3part_accountsid = ConfigHelper
			.getString("rest.3part.sms.accountsid");
	public static String rest_3part_smsappid = ConfigHelper
			.getString("rest.3part.sms.appid");
	public static String rest_3part_smsparam = ConfigHelper
			.getString("rest.3part.sms.param");
	public static String rest_3part_smstmpid = ConfigHelper
			.getString("rest.3part.sms.templateId");
	public static String rest_3part_token = ConfigHelper
			.getString("rest.3part.sms.token");
	public static String rest_3part_noticeCounpon= ConfigHelper
			.getString("rest.3part.sms.noticeCounpon.templateId");

	public static String rest_3part_smsresettmpid = ConfigHelper
			.getString("rest.3part.sms.reset.templateId");
	public static String rest_3part_smspush = ConfigHelper
			.getString("rest.3part.sms.push");

	public static Result requestResetPasswd(long phone, int type) {
		Logger.info("start to request SMS reset password to:" + phone);
		TuserInfo usrinfo = TuserInfo.findDataByPhoneId(phone);
		if (usrinfo == null) {
			return ok("该手机号码没有注册，请使用APP客户端注册手机号码");
		}

		if (type == 0) {
			if (usrinfo.userType < Constants.USER_TYPE_MADMIN
					&& usrinfo.userType >= Constants.USER_TYPE_MADMIN + 10) {
				return ok("该手机号码不能登录后台系统");
			}
		}

		// 加密后这里应该先改密码？？？？？？？
		// 得到密码
		String param = usrinfo.passwd;

		if (rest_3part_smsparam != null
				&& !rest_3part_smsparam.trim().equals("")) {
			param += "," + rest_3part_smsparam;
		}

		Logger.debug("-----rest_3part_accountsid:" + rest_3part_accountsid);
		Logger.debug("-----rest_3part_smsuri:" + rest_3part_smsuri);
		Logger.debug("-----rest_3part_smsappid:" + rest_3part_smsappid);
		Logger.debug("-----rest_3part_smsresettmpid:"
				+ rest_3part_smsresettmpid);
		Logger.debug("-----rest_3part_smsparam:" + param);
		Logger.debug("-----rest_3part_token:" + rest_3part_token);

		try {
			String phoneString = String.valueOf(phone);
			// 组合json bean
			TemplateSMS templateSMS = new TemplateSMS(rest_3part_smsappid,
					param, rest_3part_smsresettmpid, phoneString);
			Gson gson = new Gson();
			String body = gson.toJson(templateSMS);
			body = "{\"templateSMS\":" + body + "}";

			Date currentDate = new Date();
			String timestamp = DateHelper.format(currentDate, "yyyyMMddHHmmss");

			String src = rest_3part_accountsid + ":" + timestamp;
			String auth = EncryptUtil.base64Encoder(src);

			String signature = getSignature(rest_3part_accountsid,
					rest_3part_token, timestamp);

			String realUrl = rest_3part_smsuri + "?sig=" + signature;

			Logger.debug("-----real url:" + realUrl);
			Logger.debug("-----real body:" + body);

			ws.url(realUrl)
					.setHeader("Content-Type",
							"application/json;;charset=utf-8")
					.setHeader("Accept", "application/json")
					.setHeader("Authorization", auth).setTimeout(5000)
					.post(body).map(new Function<WSResponse, Result>() {
						@Override
						public Result apply(WSResponse response) {
							JsonNode jsonString = response.asJson();

							Logger.info("SMS Response:" + jsonString);

							return ok();
						}
					});

			return ok("密码重置请求发送成功,请注意短信查收");
		} catch (Exception e) {
			Logger.error("requestSMSVerify", e);
			return ok("密码重置短信发送失败，请联系管理员.");
		}

	}

	/**
	 * 请求验证码
	 * 
	 * @return
	 */
	public static Result requestSMSVerify(final long phone) {
		Logger.info("start to request SMS verification to:" + phone);

		TuserInfo usrinfo = TuserInfo.findDataByPhoneId(phone);
		if (usrinfo != null) {
			Logger.info("there is existing user phone, can not register again!");
			return ok("该手机号码已经注册");
		}

		String phoneString = String.valueOf(phone);

		// 得到验证码
		String param = getRandomChar();

		if (param.trim().equals("")) {
			param = "4677";
		}

		TVerifyCode verficode = new TVerifyCode();
		verficode.createDate = new Date();
		verficode.phone = phone;
		verficode.verifycode = param;
		TVerifyCode.saveData(verficode);

		if (rest_3part_smsparam != null
				&& !rest_3part_smsparam.trim().equals("")) {
			param += "," + rest_3part_smsparam;
		}

		Logger.debug("-----rest_3part_accountsid:" + rest_3part_accountsid);
		Logger.debug("-----rest_3part_smsuri:" + rest_3part_smsuri);
		Logger.debug("-----rest_3part_smsappid:" + rest_3part_smsappid);
		Logger.debug("-----rest_3part_smstmpid:" + rest_3part_smstmpid);
		Logger.debug("-----rest_3part_smsparam:" + param);
		Logger.debug("-----rest_3part_token:" + rest_3part_token);

		try {
			// 组合json bean
			TemplateSMS templateSMS = new TemplateSMS(rest_3part_smsappid,
					param, rest_3part_smstmpid, phoneString);
			Gson gson = new Gson();
			String body = gson.toJson(templateSMS);
			body = "{\"templateSMS\":" + body + "}";

			Date currentDate = new Date();
			String timestamp = DateHelper.format(currentDate, "yyyyMMddHHmmss");

			String src = rest_3part_accountsid + ":" + timestamp;
			String auth = EncryptUtil.base64Encoder(src);

			String signature = getSignature(rest_3part_accountsid,
					rest_3part_token, timestamp);

			String realUrl = rest_3part_smsuri + "?sig=" + signature;

			Logger.debug("-----real url:" + realUrl);
			Logger.debug("-----real body:" + body);

			ws.url(realUrl)
					.setHeader("Content-Type",
							"application/json;;charset=utf-8")
					.setHeader("Accept", "application/json")
					.setHeader("Authorization", auth).setTimeout(5000)
					.post(body).map(new Function<WSResponse, Result>() {
						@Override
						public Result apply(WSResponse response) {
							JsonNode jsonString = response.asJson();

							Logger.info("SMS Response:" + jsonString);

							Akka.system()
									.scheduler()
									.scheduleOnce(
											Duration.create(
													Constants.SCHEDULE_TIME_DELETE_VERIFYCODE,
													TimeUnit.SECONDS),
											new Runnable() {
												public void run() {
													Logger.debug("#######AKKA schedule start>> TVerifyCode.deletePhone:"
															+ phone
															+ "#########");
													TVerifyCode
															.deletePhone(phone);
												}
											}, Akka.system().dispatcher());

							return ok();
						}
					});

			return ok("验证码请求中,请注意短信查收");
		} catch (Exception e) {
			Logger.error("requestSMSVerify", e);
			return ok("验证码短信发送失败，请联系管理员.");
		}

	}

	/**
	 * 发送优惠劵赠送短信
	 * 
	 * @return
	 */
	public static Result requestSMSmessage(final long phone, final int price) {
		Logger.info("start to request SMS verification to:" + phone);
		
		//查看option 发送优惠劵赠送短信是否打开
		
		TOptions options = TOptions.findOption(7);
		if(options.textObject!=null&&options.textObject.toString().trim().equals("1"))
		{
		// 得到手机号
		String phoneString = String.valueOf(phone);

		TOptions optionsday=TOptions.findOption(8);
		int day=0;
		if(optionsday!=null&&optionsday.textObject!=null)
		  day=Integer.valueOf(optionsday.textObject.toString().trim());
		// 得到优惠劵价格
		
		String prices =String.valueOf(price);
		String param = prices+"元"+","+day+"天";

		if (rest_3part_smsparam != null
				&& !rest_3part_smsparam.trim().equals("")) {
			param += "," + rest_3part_smsparam;
		}

		Logger.debug("-----rest_3part_accountsid:" + rest_3part_accountsid);
		Logger.debug("-----rest_3part_smsuri:" + rest_3part_smsuri);
		Logger.debug("-----rest_3part_smsappid:" + rest_3part_smsappid);
		Logger.debug("-----rest_3part_smstmpid:" + rest_3part_noticeCounpon);
		Logger.debug("-----rest_3part_smsparam:" + param);
		Logger.debug("-----rest_3part_token:" + rest_3part_token);

		try {
			// 组合json bean
			TemplateSMS templateSMS = new TemplateSMS(rest_3part_smsappid,
					param, "12676", phoneString);
			Gson gson = new Gson();
			String body = gson.toJson(templateSMS);
			body = "{\"templateSMS\":" + body + "}";

			Date currentDate = new Date();
			String timestamp = DateHelper.format(currentDate, "yyyyMMddHHmmss");
			String src = rest_3part_accountsid + ":" + timestamp;
			String auth = EncryptUtil.base64Encoder(src);

			String signature = getSignature(rest_3part_accountsid,
					rest_3part_token, timestamp);

			String realUrl = rest_3part_smsuri + "?sig=" + signature;
			Logger.debug("-----real url:" + realUrl);
			Logger.debug("-----real body:" + body);

			ws.url(realUrl)
					.setHeader("Content-Type",
							"application/json;;charset=utf-8")
					.setHeader("Accept", "application/json")
					.setHeader("Authorization", auth).setTimeout(5000)
					.post(body).map(new Function<WSResponse, Result>() {
						@Override
						public Result apply(WSResponse response) {
							JsonNode jsonString = response.asJson();

							Logger.info("SMS Response:" + jsonString);

							return ok();
						}
					});

			return ok("验证码请求中,请注意短信查收");
			
		} catch (Exception e) {
			Logger.error("requestSMSVerify", e);
			return ok("验证码短信发送失败，请联系管理员.");
		}
		}
		Logger.info("not open the sendsms option");
        return ok("系统错误");
	}

	private static String getSignature(String accountSid, String authToken,
			String timestamp) throws Exception {
		String sig = accountSid + authToken + timestamp;
		String signature = EncryptUtil.md5Digest(sig);
		return signature;
	}

	private static String getRandomChar() {
		Random r = new Random();
		int tag[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		String four = "";
		int temp = 0;
		while (four.length() != 4) {
			temp = r.nextInt(10);// 随机获取0~9的数字
			if (tag[temp] == 0) {
				four += temp;
				tag[temp] = 1;
			}
		}
		return four;
	}

	/**
	 * 这里来自第三方ucpaas.com
	 * 
	 * @author woderchen
	 *
	 */
	static class TemplateSMS {
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
		
		public TemplateSMS(String appId, String templateId,
				String to) {
			super();
			this.appId = appId;
			this.templateId = templateId;
			this.to = to;
		}
	}

	public static boolean smspush (String phoneString)
	{
		
		TOptions options =TOptions.findOption(15);
		if(options.textObject==""||options.textObject==null||options.textObject.equals(""))
		{
			Logger.debug("param is  null");
			return false;
		}
		else {
			String param = options.textObject;
			try {
				

				if (rest_3part_smsparam != null
						&& !rest_3part_smsparam.trim().equals("")) {
					param += "," + rest_3part_smsparam;
				}
				// 组合json bean
				//TemplateSMS templateSMS = new TemplateSMS(rest_3part_smsappid, "12676", phoneString);
				TemplateSMS templateSMS = new TemplateSMS(rest_3part_smsappid,
						param, "13688", phoneString);
				Gson gson = new Gson();
				String body = gson.toJson(templateSMS);
				body = "{\"templateSMS\":" + body + "}";
				Date currentDate = new Date();
				String timestamp = DateHelper.format(currentDate, "yyyyMMddHHmmss");
				String src = rest_3part_accountsid + ":" + timestamp;
				String auth = EncryptUtil.base64Encoder(src);
				String signature = getSignature(rest_3part_accountsid,
						rest_3part_token, timestamp);

				String realUrl = rest_3part_smsuri + "?sig=" + signature;
				Logger.debug("-----real url:" + realUrl);
				Logger.debug("-----real body:" + body);

				ws.url(realUrl)
						.setHeader("Content-Type",
								"application/json;;charset=utf-8")
						.setHeader("Accept", "application/json")
						.setHeader("Authorization", auth).setTimeout(5000)
						.post(body).map(new Function<WSResponse, Result>() {
							@Override
							public Result apply(WSResponse response) {
								JsonNode jsonString = response.asJson();

								Logger.info("SMS Response:" + jsonString);

								return ok();
							}
						});
				return true;
				
				
			} catch (Exception e) {
				Logger.error("requestSMSVerify", e);
				return false;
				
			}
			
		}
		
	
		
	}
	
	
	public static Result requestSMSmessageModel() {
		Logger.info("start to send sms to smssenduser table");
		
		//查看option 发送优惠劵赠送短信是否打开
		
		TOptions options = TOptions.findOption(14);
		if(options.textObject!=null&&options.textObject.toString().trim().equals("1"))
		{
	      //遍历smssenduser表
			List<Tsmssenduser> listsmssenduser=Tsmssenduser.findalluser();		
			if(listsmssenduser.size()>0&&listsmssenduser!=null)
			{
				Logger.info("listsmssenduser.size>>"+listsmssenduser.size());
				for(int i=0;i<listsmssenduser.size();i++)
				{
					Tsmssenduser currentsenduser= listsmssenduser.get(i);
					boolean result=smspush(currentsenduser.telephone);
					if(result==true)
					{
						Logger.info("sms push suceess>>>>"+currentsenduser.telephone);
						Logger.debug("-----rest_3part_accountsid:" + rest_3part_accountsid);
						Logger.debug("-----rest_3part_smsuri:" + rest_3part_smsuri);
						Logger.debug("-----rest_3part_smsappid:" + rest_3part_smsappid);
						Logger.debug("-----rest_3part_smstmpid:" + rest_3part_smspush);
						Logger.debug("-----rest_3part_token:" + rest_3part_token);
						continue;
					}
					else if(result==false){					
						Logger.info("sms push fail>>>>"+currentsenduser.telephone);
						Logger.debug("-----rest_3part_accountsid:" + rest_3part_accountsid);
						Logger.debug("-----rest_3part_smsuri:" + rest_3part_smsuri);
						Logger.debug("-----rest_3part_smsappid:" + rest_3part_smsappid);
						Logger.debug("-----rest_3part_smstmpid:" + rest_3part_smspush);
						Logger.debug("-----rest_3part_token:" + rest_3part_token);
						continue;	
					}
				}

			}
			else {
				Logger.info("smssenduser table is null");
			}
			
		}
		else {
			Logger.info("not open the smspush option");
	        return ok("系统错误");
		}
		 return ok("系统错误");
		
	}





}
