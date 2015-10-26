package controllers;

import java.util.Date;
import java.util.List;
import java.util.Random;

import actor.VerifyCodeActor;
import actor.model.TemplateSMS;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.info.TOptions;
import models.info.TVerifyCode;
import models.info.Tsmssenduser;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Akka;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ActorHelper;
import utils.ConfigHelper;
import utils.Constants;

public class SMSController extends Controller {
	static WSClient ws = WS.client();
	static ActorRef verifyActor = Akka.system().actorOf(Props.create(VerifyCodeActor.class),"VerifyCodeActor");

	public static String rest_3part_smsparam = ConfigHelper
			.getString("tcp.actor.sms.param");
	
	public static String rest_3part_smstmpid = ConfigHelper.getString("tcp.actor.sms.verify.templateId");
	public static String rest_3part_smsresettmpid = ConfigHelper.getString("tcp.actor.sms.reset.templateId");
	public static String rest_3part_smscoupon1 = ConfigHelper.getString("tcp.actor.sms.counpon1.templateId");
	public static String rest_3part_smscoupon2 = ConfigHelper.getString("tcp.actor.sms.counpon2.templateId");


	//test remote actor
	public static Promise<Result> sendMessageToSMSActor(long phone,int passwd){
	
		if (passwd == 518898){
		    String phoneString = String.valueOf(phone);
			// 组合json bean
			TemplateSMS templateSMS = new TemplateSMS("",
					"test sms", rest_3part_smsresettmpid, phoneString);
			
			//myActor.tell(templateSMS, ActorRef.noSender());
			return Promise.wrap(ActorHelper.getInstant().askSMSFuture(templateSMS)).map(
                    new Function<Object, Result>() {
                        public Result apply(Object response) {
                        	  Logger.info("SMSActor=>message:"+response);
                             if( response instanceof TemplateSMS ) {
                            	 TemplateSMS message = ( TemplateSMS )response;
                            	 Logger.debug("SMSActor=>result:"+message.getResultCode());
                                  return ok(message.getResultCode());
                             }
                            return notFound( "Message is not of type MyMessage" );
                        }
                    }
                );
		}
		return Promise.pure(ok("send fail. password is incorrect"));
	}
	
	
	/**
	 * 重置密码
	 * @param phone
	 * @param type
	 * @return
	 */
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

		Logger.debug("-----rest_3part_smsresettmpid:"
				+ rest_3part_smsresettmpid);
		Logger.debug("-----rest_3part_smsparam:" + param);

		try {
			String phoneString = String.valueOf(phone);
			// 组合json bean
			TemplateSMS templateSMS = new TemplateSMS("",
					param, rest_3part_smsresettmpid, phoneString);
					
			ActorHelper.getInstant().sendSMSMessage(templateSMS);

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

		Logger.debug("-----rest_3part_smstmpid:" + rest_3part_smstmpid);
		Logger.debug("-----rest_3part_smsparam:" + param);

		try {
			// 组合json bean
			TemplateSMS templateSMS = new TemplateSMS("",
					param, rest_3part_smstmpid, phoneString);
			ActorHelper.getInstant().sendSMSMessage(templateSMS);
			
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

		Logger.debug("-----rest_3part_smscoupon1:" + rest_3part_smscoupon1);
		Logger.debug("-----rest_3part_smsparam:" + param);

		try {
			// 组合json bean
			TemplateSMS templateSMS = new TemplateSMS("",
					param, rest_3part_smscoupon1, phoneString);
			ActorHelper.getInstant().sendSMSMessage(templateSMS);
			
			return ok("优惠劵赠送短信发送");
			
		} catch (Exception e) {
			Logger.error("requestSMSVerify", e);
			return ok("优惠劵赠送短信发送失败");
		}
		}
		Logger.info("not open the sendsms option");
        return ok("系统错误");
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
				TemplateSMS templateSMS = new TemplateSMS("",
						param, rest_3part_smscoupon2, phoneString);
				ActorHelper.getInstant().sendSMSMessage(templateSMS);
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
						Logger.debug("-----rest_3part_smscoupon2:" + rest_3part_smscoupon2);
						continue;
					}
					else if(result==false){					
						Logger.info("sms push fail>>>>"+currentsenduser.telephone);
						Logger.debug("-----rest_3part_smscoupon2:" + rest_3part_smscoupon2);
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
	
	
	
	/**
	 * 定向发送优惠劵赠送短信
	 * 
	 * @return
	 */
	public static Result redirectsendcoupnsms(final long phone, final double price) {
		Logger.info("start to redirect :" + phone);
		
		//定向发送优惠券短信开关
		TOptions options = TOptions.findOption(13);
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

		Logger.debug("-----rest_3part_smscoupon1:" + rest_3part_smscoupon1);
		Logger.debug("-----rest_3part_smsparam:" + param);

		try {
			// 组合json bean
			TemplateSMS templateSMS = new TemplateSMS("",
					param, rest_3part_smscoupon1, phoneString);
			ActorHelper.getInstant().sendSMSMessage(templateSMS);
			
			return ok("定向发送优惠劵赠送短信请求中,请注意短信查收");
			
		} catch (Exception e) {
			Logger.error("requestSMSVerify", e);
			return ok("定向发送优惠劵赠送短信失败，请联系管理员.");
		}
		}
		Logger.info("not open the sendsms option");
        return ok("系统错误");
	}





}
