package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import actor.model.PushMessage;
import actor.model.PushTarget;
import models.info.TParkInfo_adm;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ActorHelper;
import utils.DateHelper;

/**
 * 推送
 * 
 * @author woderchen
 *
 */
public class PushController extends Controller {

	static String appId = "push";
	// static String Alias = "";

	public static HashMap<Long, String> clientMap = new HashMap<Long, String>();

	public static HashMap<Long, String> adminPushToClientMap = new HashMap<Long, String>();
	
	public static HashMap<Long, String> adminPushToClientMapForIn = new HashMap<Long, String>();

	/**
	 * 注册一个用户的clientId
	 * 
	 * @param userid
	 * @param clientId
	 */
	public static Result registerAdmUser(long userid, String clientId) {
		if (userid > 0 && clientId != null && !clientId.trim().equals("")) {
			Logger.info("###add to clientMap:" + userid + ", client id: "
					+ clientId + "#######");
			clientMap.put(userid, clientId);
			return ok("ok");
		}
		return ok("no action");
	}

	/**
	 * 注册一个消息订单id和client的绑定消息，一般用于结账放行，管理员推送消息给客户
	 * 
	 * @param orderId
	 * @param clientId
	 */
	public static void registerClientUser(long orderId, String clientId) {
		if (orderId > 0 && clientId != null && !clientId.trim().equals("")) {
			Logger.info("###add to adminPushToClientMap:" + orderId
					+ ", client id: " + clientId + "#######");
			adminPushToClientMap.put(orderId, clientId);
		}

	}

	/**
	 * 移除一个待通知消息
	 * 
	 * @param orderId
	 */
	public static void remove(long orderId) {
		adminPushToClientMap.remove(orderId);
	}
	
	/**
	 * 注册一个消息订单id和client的绑定消息，一般用于结账放行，管理员推送消息给客户
	 * 
	 * @param orderId
	 * @param clientId
	 */
	public static void registerClientUserForIn(long orderId, String clientId) {
		if (orderId > 0 && clientId != null && !clientId.trim().equals("")) {
			Logger.info("###add to adminPushToClientMapForIn:" + orderId
					+ ", client id: " + clientId + "#######");
			adminPushToClientMapForIn.put(orderId, clientId);
		}

	}

	/**
	 * 移除一个待通知消息
	 * 
	 * @param orderId
	 */
	public static void removeForIn(long orderId) {
		adminPushToClientMapForIn.remove(orderId);
	}

	public static void pushToParkAdmin(final long parkId, final String phone,
			final String orderName, long orderid) {
		Logger.info("########plan to push to administrator of park:" + parkId
				+ " for " + phone + "#######");

		ArrayList<PushTarget> targets = new ArrayList<PushTarget>();
		if (clientMap != null) {

			List<TParkInfo_adm> adms = TParkInfo_adm
					.findAdmPartInfoByParkId(parkId);

			if (adms != null) {
				for (TParkInfo_adm adm : adms) {
					String clientId = clientMap.get(adm.userInfo.userid);
					if (clientId != null) {
						PushTarget target = new PushTarget();
						target.setAppId(appId);
						target.setClientId(clientId);
						Logger.debug("###try to push to user:" + clientId);
						// target.setAlias(""+adm.userInfo.userid);
						targets.add(target);
					}
				}
			}
		}

		PushMessage message = new PushMessage();
		message.type = "ORDER_START";
		message.title = "【车泊乐】收到新订单[" + orderid + "]";
		message.message = "用户:" + phone + " 已经成功下单[" + orderName + "]";
		message.sender = phone;
		message.date = DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		// message.ext1 = ""+TOrder.findnotcomeincount(parkId);;

		pushToClient(message, targets);

	}

	public static void pushToParkAdminForOut(final long parkId,
			final String phone, final String orderName, long orderid) {
		Logger.info("########plan to push to administrator for out:" + parkId
				+ " for " + phone + "#######");

		// NotyPopLoadTemplate template = NotyPopLoadTemplateDemo();

		// message.setPushNetWorkType(1);

		ArrayList<PushTarget> targets = new ArrayList<PushTarget>();
		if (clientMap != null) {

			List<TParkInfo_adm> adms = TParkInfo_adm
					.findAdmPartInfoByParkId(parkId);

			if (adms != null) {
				for (TParkInfo_adm adm : adms) {
					String clientId = clientMap.get(adm.userInfo.userid);
					if (clientId != null) {
						PushTarget target = new PushTarget();
						target.setAppId(appId);
						target.setClientId(clientId);
						Logger.debug("###try to push to user:" + clientId);
						// target.setAlias(""+adm.userInfo.userid);
						targets.add(target);
					}
				}
			}
		}

		PushMessage message = new PushMessage();
		message.type = "ORDER_DONE";
		message.message = "订单["+orderid+"]用户[" + phone + "]已经完成订单[" + orderName + "]";
		message.title = "【车泊乐】订单[" + orderid + "]已完成";
		message.sender = phone;
		message.date = DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		// message.ext1 = ""+TOrder.findnotcomeincount(parkId);;

		pushToClient(message, targets);

	}

	public static void pushToParkAdminForRequestPay(final long parkId,
			final String phone, final double payment, long orderid) {
		Logger.info("########plan to push to administrator for pay request:"
				+ payment + " for " + phone + "#######");

		ArrayList<PushTarget> targets = new ArrayList<PushTarget>();
		if (clientMap != null) {
			List<TParkInfo_adm> adms = TParkInfo_adm
					.findAdmPartInfoByParkId(parkId);
			if (adms != null) {
				for (TParkInfo_adm adm : adms) {
					String clientId = clientMap.get(adm.userInfo.userid);
					if (clientId != null) {
						PushTarget target = new PushTarget();
						target.setAppId(appId);
						target.setClientId(clientId);
						Logger.debug("###try to push to user:" + clientId);
						// target.setAlias(""+adm.userInfo.userid);
						targets.add(target);
					}
				}
			}
		}
		PushMessage message = new PushMessage();
		message.type = "ORDER_REQUEST_PAY";
		message.message = "订单["+orderid+"]收到[" + phone + "]的付款请求:" + payment + "元.";
		message.title = "【车泊乐】订单[" + orderid + "]付款请求";
		message.sender = phone;
		message.date = DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		pushToClient(message, targets);
	}

	/**
	 * 管理员结账放行
	 * @param orderId
	 * @param pay
	 */
	public static void pushToClientForOrderDone(long orderId,double pay,String parkName) {
		Logger.info("########plan to ClientForOrderDone,orderid"
				+ orderId);

		String clientId = adminPushToClientMap.get(orderId);

		if(clientId!=null){
			PushTarget target = new PushTarget();
			String userId =adminPushToClientMap.get(orderId);
			target.setAppId(appId);
			target.setClientId(userId);
			Logger.debug("###try to push to user:" + clientId);
			
			PushMessage message = new PushMessage();
			message.type = "ADMIN_ORDER_DONE";
			message.title = "【车泊乐】订单["+orderId+"]完成";
			message.message = "订单["+orderId+"]["+parkName+"]管理员已结账放行,收费:"+pay+"元";
			message.sender = "admin";
			message.date = DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		// message.ext1 = ""+TOrder.findnotcomeincount(parkId);;

		   pushToClient(message, target);
		}

	}
	
	/**
	 * 订单过期消息
	 * @param orderId
	 * @param pay
	 */
	public static void pushToClientForOrderExpire(long orderId,String parkName,float overtime,String additionalmessage) {
		Logger.info("########plan to ClientForOrderDone,orderid"
				+ orderId);

		String clientId = adminPushToClientMapForIn.get(orderId);

		if(clientId!=null){
			String userId = adminPushToClientMapForIn.get(orderId);
			PushTarget target = new PushTarget();
			target.setAppId(appId);
			target.setClientId(userId);
			Logger.debug("###try to push to user:" + clientId);
			
			PushMessage message = new PushMessage();
			message.type = "ORDER_WILL_EXPIRE";
			if(overtime<=0){
				message.title = "【车泊乐】订单["+orderId+"]已经"+additionalmessage;
				message.message = "注意！订单["+orderId+"]["+parkName+"]已经"+additionalmessage;
			}else{
				message.title = "【车泊乐】订单["+orderId+"]将在"+overtime+"后"+additionalmessage;
				message.message = "注意！订单["+orderId+"]["+parkName+"]将在"+overtime+"分钟后"+additionalmessage;
			}
			
			message.sender = "admin";
			message.date = DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		// message.ext1 = ""+TOrder.findnotcomeincount(parkId);;

		   pushToClient(message, target);
		}

	}
	
	
	/**
	 * 
	 * @param orderId
	 * @param pay
	 * @param parkName
	 */
	public static void pushToAdmForIn(long orderId,long phone,String parkName,long parkId) {
		Logger.info("########push to administrator for in,orderid"
				+ orderId);

		
		ArrayList<PushTarget> targets = new ArrayList<PushTarget>();
		if (clientMap != null) {
			List<TParkInfo_adm> adms = TParkInfo_adm.findAdmPartInfoByParkId(parkId);
			if (adms != null) {
				for (TParkInfo_adm adm : adms) {
					String clientId = clientMap.get(adm.userInfo.userid);
					if (clientId != null) {
						PushTarget target = new PushTarget();
						target.setAppId(appId);
						target.setClientId(clientId);
						Logger.debug("###try to push to user:" + clientId);
						// target.setAlias(""+adm.userInfo.userid);
						targets.add(target);
					}
				}
			}
		}
		
		PushMessage message = new PushMessage();
		message.type = "ORDER_IN";
		message.title = "【车泊乐】订单["+orderId+"]已进场";
		message.message = "订单["+orderId+"]用户["+phone+"]已经进场"+"["+parkName+"]";
		message.sender = "admin";
		message.date = DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	// message.ext1 = ""+TOrder.findnotcomeincount(parkId);;

	   pushToClient(message, targets);

	}
	
	/**
	 * 
	 * @param orderId
	 * @return
	 */
	public static boolean existInClientRequest(long orderId){
		Logger.debug("adminPushToClientMap:"+adminPushToClientMap);
		return adminPushToClientMap.containsKey(orderId);
	}

	/**
	 * 推送主要方法
	 * 
	 * @param template
	 * @param targets
	 */
	private static void pushToClient(PushMessage pushMessage,
			ArrayList<PushTarget> targets) {

		pushMessage.targets = targets;
		ActorHelper.getInstant().sendPushMessage(pushMessage);
	}

	/**
	 * 推送主要方法
	 * 
	 * @param template
	 * @param targets
	 */
	private static void pushToClient(PushMessage pushMessage,PushTarget targets ){

		ArrayList<PushTarget> targetArray = new ArrayList<PushTarget>();
		targetArray.add(targets);
		pushMessage.targets = targetArray;
		ActorHelper.getInstant().sendPushMessage(pushMessage);
	}
	/*
	 * private static NotificationTemplate NotificationTemplateDemo(String
	 * phone, String orderName) throws Exception { NotificationTemplate template
	 * = new NotificationTemplate(); template.setAppId(appId);
	 * template.setAppkey(appkey); template.setTitle("收到" + phone + "的车位订单");
	 * template.setText(phone + "已经成功下单[" + orderName + "]-" +
	 * DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
	 * template.setLogo("ic_launcher.png"); template.setLogoUrl("");
	 * template.setIsRing(true); template.setIsVibrate(true);
	 * template.setIsClearable(true); template.setTransmissionType(1);
	 * template.setTransmissionContent(orderName + "已经成功下单。"); //
	 * template.setPushInfo("actionLocKey", 2, "message", "sound", // "payload",
	 * // "locKey", "locArgs", "launchImage"); return template; }
	 */
}
