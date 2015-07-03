package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.info.TParkInfo_adm;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ConfigHelper;
import utils.DateHelper;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;

public class PushController extends Controller{

	static String appId = ConfigHelper.getString("push.getui.appId");;
	static String appkey = ConfigHelper.getString("push.getui.appkey");;
	static String master = ConfigHelper.getString("push.getui.master");;
	static String host = ConfigHelper.getString("push.getui.host");;
	//static String Alias = "";
	
	public static HashMap<Long,String> clientMap = new HashMap<Long,String>();
	
	/**
	 * 注册一个用户的clientId
	 * @param userid
	 * @param clientId
	 */
	public static Result registerAdmUser(long userid,String clientId){
		Logger.debug("###register user:"+userid+", client id: "+clientId+"#######");
		if(userid>0&&clientId!=null&&!clientId.trim().equals("")){
			clientMap.put(userid, clientId);
			return ok("ok");
		}
		return ok("no action");
	}
	
	
	public static void pushToParkAdmin(final long parkId,final String phone,final String orderName) {
		Logger.info("########plan to push to administrator of park:"+parkId+" for "+phone+"#######");
	
		try{
			final IGtPush push = new IGtPush(host, appkey, master);
			// LinkTemplate template = linkTemplateDemo();
	//		 TransmissionTemplate template = TransmissionTemplateDemo();
	//		 LinkTemplate template = linkTemplateDemo();
			NotificationTemplate template = NotificationTemplateDemo(phone,orderName);
			// NotyPopLoadTemplate template = NotyPopLoadTemplateDemo();
	
			ListMessage message = new ListMessage();
			message.setData(template);
			message.setOffline(false);
			message.setOfflineExpireTime(1000*3600*24);
			// message.setPushNetWorkType(1);
	
			List<Target> targets = new ArrayList<Target>();
			if(clientMap!=null){
				
				List<TParkInfo_adm>  adms = TParkInfo_adm.findAdmPartInfoByParkId(parkId);
				
				if(adms!=null){
					for(TParkInfo_adm adm:adms){
						String clientId = clientMap.get(adm.userInfo.userid);
						if(clientId!=null){
							Target target = new Target();
							target.setAppId(appId);
							target.setClientId(clientId);
							Logger.debug("###try to push to user:"+clientId);
							//target.setAlias(""+adm.userInfo.userid);
							targets.add(target);
						}
					}
				}
			}

			String taskId = push.getContentId(message);
			IPushResult ret = push.pushMessageToList(taskId, targets);
			Logger.info("#####push result:"+ret.getResponse()+"#######");
		}catch(Exception e){
			Logger.error("pushToParkAdmin",e);
		}
		
	}
	
	private static NotificationTemplate NotificationTemplateDemo(String phone,String orderName)
			throws Exception {
		NotificationTemplate template = new NotificationTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTitle("收到"+phone+"的车位订单");
		template.setText(phone+"已经成功下单["+orderName+"]-"+DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		template.setLogo("ic_launcher.png");
		template.setLogoUrl("");
		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		template.setTransmissionType(1);
		template.setTransmissionContent(orderName+"已经成功下单。");
//		template.setPushInfo("actionLocKey", 2, "message", "sound", "payload",
//				"locKey", "locArgs", "launchImage");
		return template;
	}
}
