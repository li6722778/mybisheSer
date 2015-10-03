package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.info.TOptions;
import play.Logger;

/**
 * 这个类主要是付款帮助类，目前完成的功能有
 * 1.检查恶意刷卡订单
 *   1.1 管理员的手机参与下订单
 *   1.2 同一个手机不能在＊个小时内，用不同的手机号（超过3个？）用优惠卷交易
 * @author woderchen
 *
 */
public class PayHelper {

	public static HashMap<String, List<String>> monitorClientMapForOrder= new HashMap<String, List<String>>();
	
	public final static int MAX_SWICH_NUM = 2;
	public final static int MONITOR_SPITE_ORDER=99;
	
	/**
	 * clientid：用户的手机端唯一标示码。注意这个有个推第三方提供
	 *    ClientID在哪些情况下会改变？
     *    用户超过三个月未登录，之后再登录会重新生成一个CID。
     *    双清：即卸载应用，清除Sdcard下libs文件夹，然后重新安装（只适用与Android客户端）。
     *    Android：应用的包名修改；iOS：bundleID的修改（越狱手机卸载安装也有可能会变）。
	 * userid：用户的在车泊乐系统中的唯一id
	 * @param userClientId
	 * @param userId
	 * @throws Exception
	 */
	public static void checkSpiteOrder(String userClientId,String userPhone,String parkName) throws Exception{
		Logger.info("#####start to check spite order for device:"+userClientId+"#####");
		if (userClientId != null && userClientId.trim().length()>0){
			
			TOptions option = TOptions.findOption(MONITOR_SPITE_ORDER);
			if (option==null||option.textObject==null||option.textObject.trim().equals("0")){
				Logger.info("#####since the check flag is off,end check#####");
				return;
			}
//			
//			Logger.info("check device id from park administrator pool");
//			
			
			Logger.info("check device id from monitor client pool");
			List<String> phoneCountArray = monitorClientMapForOrder.get(userClientId);
			
			if (phoneCountArray != null && phoneCountArray.size()>0){
				
				if (!phoneCountArray.contains(userPhone)){
					int currentCount = phoneCountArray.size();
					
					if (currentCount>=MAX_SWICH_NUM){//当前手机已经切换了N次手机号
						LogController.info("##可疑行为##diffirent phone number["+userPhone+"] with same device["+userClientId+"]->{"+phoneCountArray+"} for "+parkName+",max pool:"+currentCount);
						throw new Exception("系统检测到可疑行为，请稍后再试。");
					}

					Logger.warn("##add phone："+userPhone+" to "+userClientId+",current size:"+currentCount);
					phoneCountArray.add(userPhone);
				}else{
					Logger.warn("##same phone number with same device for "+parkName);
				}
				
			}else{
				Logger.info("add to monitor client pool");
				List<String> ls = new ArrayList<String>();
				ls.add(userPhone);
				monitorClientMapForOrder.put(userClientId, ls);
			}
		}
		Logger.info("######end check spite order#####");
	}
	
}
