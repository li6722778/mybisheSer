package controllers;

import java.util.Date;

import models.info.TOptions;
import models.info.TShare;
import models.info.Tuniqueurl;
import models.info.Tunregisteruser;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.DateHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ShareController extends Controller {

	public static Gson gsonBuilderWithExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	/**
	 * 查询分享记录,获得优惠劵
	 * @return
	 */

	public static Result getDataById(Long id, String url) {

		Logger.info("start to query share information");
		ComResponse<String> response = new ComResponse<String>();

		if (url!= null) {// 保存唯一的url
			//查询该url是否在Tunqueurl表中
			Tuniqueurl  uniqueurl = Tuniqueurl.findDataById(url);
			if(uniqueurl==null)
			{
		   Logger.info("right  not exist");
			saveuniqueurl(url);
			}
		}
		TShare share = TShare.findDataById(id);
		// 获取每日可分享次数
		TOptions option = TOptions.findOption(4);
		String times = option.textObject;
		if (times == null || times.trim().equals("")) {
			times = "0";
		}
		int timeint = 0;
		try {
			timeint = Integer.valueOf(times).intValue();
		} catch (Exception e) {
			Logger.error("share:option.textObject", e);
		}

		if (timeint == 0) { // 如果没有设置

			response.setResponseStatus(ComResponse.STATUS_FAIL);
			Logger.warn("没有设置分享次数");
		} else
		// 不在分享表中，赠送优惠劵
		if (share == null) {
			// 赠送优惠劵
			sendCounpon(id);
			TShare.saveshare(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
		}

		// 在分享列表中
		else if (share != null) {
			Date date = new Date();
			Date lastsharedate = share.sharetDate;

			// 获取上次插入的时间和现在的时间差
			Long result = (date.getTime()) - (lastsharedate.getTime());

			// 最近3秒内没有插入数据
			if (result > 3000) {

				// 比较日期 当前日期>列表分享记录日期 说明当天没参与过分享活动
				String nowdate = DateHelper.format(date, "yyyy-MM-dd 00:00:00");
				String dbdate = DateHelper.format(share.sharetDate,
						"yyyy-MM-dd 00:00:00");
				Date nowdate2 = DateHelper.getStringtoDate(nowdate,
						"yyyy-MM-dd 00:00:00");
				Date dbdate2 = DateHelper.getStringtoDate(dbdate,
						"yyyy-MM-dd 00:00:00");
				if ((nowdate2.compareTo(dbdate2)) > 0) {
					// 赠送优惠劵
					sendCounpon(id);
					TShare.saveshare(id, 1);

					response.setResponseStatus(ComResponse.STATUS_OK);
				} else if ((nowdate2.compareTo(dbdate2)) == 0) {
					// 说明当前分享次数<限定设置分享次数
					if (share.share < timeint) {
						sendCounpon(id);

						TShare.saveshare(id, share.share + 1);
						response.setResponseStatus(ComResponse.STATUS_OK);
					}
					// 说明当天分享次数已经用完
					else if (share.share >= timeint) {
						response.setResponseStatus(ComResponse.STATUS_FAIL);
					}

				}

			}
			// 最近3秒内插入过数据
			else {
				response.setResponseStatus(ComResponse.STATUS_FAIL);
			}
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);

	}

	
	//查询分享记录,获得优惠劵,这里只是针对2015081601的版本用户
	public static Result getDataByIds(Long id) {
		return getDataById(id,null);
	}
	public static void sendCounpon(Long id) {

		// 赠送优惠劵
		TOptions options = TOptions.findOption(3);
		String Counponcode = options.textObject;
		if (Counponcode != null && !(Counponcode.toString().trim().equals(""))) {
			Counponcode = Counponcode.replace("，", ",");
			String[] counponcodes = Counponcode.split(",");
			if (counponcodes.length > 0) {
				for (int i = 0; i < counponcodes.length; i++) {
					CounponController.getsharecounpon(counponcodes[i], id);
				}
			}
		}
	}

	/**
	 * 输入手机号赠送优惠劵
	 */

	public static Result sendShareById(final Long telephonenumber, String url) {

		Logger.info("start to query sendshare information");
		ComResponse<String> response = new ComResponse<String>();
		//定时任务
		if(url.equals("xxxxx"))
		{
			
			Tuniqueurl uniqueurl = Tuniqueurl.findDataById(url);
			Date  date =new  Date();
			Date  sqlDate =uniqueurl.sharetDate;
			long result = (date.getTime()-sqlDate.getTime())/ (24 * 60 * 60 * 1000);
			if(result>=1)
			{
				Tuniqueurl.ResetOldeditionURL();
			}
		}
		
		
		// 获取url的已分享次数
		final Tuniqueurl uniqueurl = Tuniqueurl.findDataById(url);
		// 获取后台设置的每个url可用次数
		TOptions options = TOptions.findOption(5);
		int times = 0;
		String[] counponcodes = null;
		String Counponcode = options.textObject;
		if (Counponcode != null && !(Counponcode.toString().trim().equals(""))) {
			Counponcode = Counponcode.replace("，", ",");
			counponcodes = Counponcode.split(",");
			if (counponcodes.length > 0 && counponcodes.length == 5) {
				// 获取option的第5个参数 为一个链接的可用次数
				times = Integer.parseInt(counponcodes[4]);
			}
		}
		if (times!= 0 && uniqueurl!= null) {
			

			if (uniqueurl.sharetime >= times) {
				// 分享次数已经用完
				// 判断手机号是否在分享列表中
				String phoneobjects = uniqueurl.userphoneObject;
				String[] phoneobject = phoneobjects.split(",");
				boolean result = false;
				String telephone = Long.toString(telephonenumber);
				for (String temptelephone : phoneobject) {
					if (temptelephone.equals(telephone)) {
						result = true;
						break;
					}
				}

				// 在分享列表中
				if (result == true) {
					response.setResponseEntity("4");
					Logger.info("已经领取过该url链接分享的优惠劵");
					response.setResponseStatus(ComResponse.STATUS_FAIL);
				}

				else if (result == false) {
					response.setResponseEntity("5");
					Logger.info("该url的分享次数已经用完");
					response.setResponseStatus(ComResponse.STATUS_FAIL);
				}

			}

			else {
				
				Logger.info("---"+uniqueurl.url);
				// 获取该url已经被那些手机号领取,如果已经领取过 则返回
				String phoneobjects = uniqueurl.userphoneObject;
				Logger.info("---"+uniqueurl.userphoneObject);
				// 之前没有用户使用过该url链接
				if (phoneobjects == null && counponcodes != null) {
					int sendresult = sendtouser(telephonenumber, uniqueurl,
							times, counponcodes);
					if (sendresult == 0) {
						response.setResponseEntity("0");
						response.setResponseStatus(ComResponse.STATUS_FAIL);
					}
					// 新用户返回4
					if (sendresult ==4) {
						
						SMSController.requestSMSmessage(telephonenumber, 10);
						Logger.info("分享成功，获取10元优惠劵");
						response.setResponseEntity("10");
						response.setResponseStatus(ComResponse.STATUS_OK);
					}
					// 老用户返回的获取优惠劵金额
					else {
						
						if(sendresult==3){
							Logger.info("分享成功，获取5元优惠劵");
							SMSController.requestSMSmessage(telephonenumber, sendresult+2);
						}
						else {
							Logger.info("分享成功，获取优惠劵:"+sendresult+"元");
							SMSController.requestSMSmessage(telephonenumber, sendresult+1);
						}
						
						response.setResponseEntity(sendresult + "");
						response.setResponseStatus(ComResponse.STATUS_OK);
					}
				}
				// 该url链接已经被使用，查询该手机号是否在该url中记录过
				else if (phoneobjects != null
						&& !(phoneobjects.toString().trim().equals(""))) {
					String[] phoneobject = phoneobjects.split(",");
					boolean result = false;
					String telephone = Long.toString(telephonenumber);
					for (String temptelephone : phoneobject) {
						if (temptelephone.equals(telephone)) {
							result = true;
							break;
						}
					}

					// 手机号不在该url的分享记录中
					if (result == false) {
						int sendreuslt = sendtouser(telephonenumber, uniqueurl,
								times, counponcodes);

						if (sendreuslt == 0) {
						Logger.info("分享失败，");
							response.setResponseEntity("0");
							response.setResponseStatus(ComResponse.STATUS_FAIL);
						}
						// 新用户返回4
						if (sendreuslt == 4) {
							
							Logger.info("分享成功，获取优惠劵:10元");
							SMSController.requestSMSmessage(telephonenumber, 10);
							response.setResponseEntity("10");
							response.setResponseStatus(ComResponse.STATUS_OK);
						}
						
				       // 老用户返回的获取优惠劵金额 返回2，3，5
						
						if(sendreuslt==3){
							Logger.info("分享成功，获取5元优惠劵");
							SMSController.requestSMSmessage(telephonenumber, sendreuslt+2);
						}
						else {
							Logger.info("分享成功，获取优惠劵:"+sendreuslt+"元");
							SMSController.requestSMSmessage(telephonenumber, sendreuslt+1);
						}
						
						response.setResponseEntity(sendreuslt + "");
						response.setResponseStatus(ComResponse.STATUS_OK);
					

					}
					// 该用户已经通过该url领取过优惠劵
					else if (result == true) {
						response.setResponseEntity("4");
						Logger.info("已经领取过该url链接分享的优惠劵");
						response.setResponseStatus(ComResponse.STATUS_FAIL);
					}
				}
			}
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		return ok("success_jsonpCallback(" + tempJsonString + ")");

	}

	public static int sendrandomshareCounpon(String[] counponcodes, Long id) {
		// 参数设置的 第2.3.4 为给老用户发放的优惠劵
		int max = 3;
		int min = 1;
		int random = 0;
		if (counponcodes.length > 0) {
			// 获取随机优惠劵编号
			random = (int) Math.round(Math.random() * (max - min) + min);
			CounponController.getsharecounpon(counponcodes[random-1], id);
		}
		return random;
	}

	/**
	 * 分享后为每个链接生成的唯一标识记录次数
	 * 
	 * @param url
	 * @return
	 */
	public static boolean saveuniqueurl(String url) {
		Logger.info("start to query sendshare information");
		try {
			Tuniqueurl.saveTuniqueurl(url);
			return true;
		} catch (Exception e) {
			Logger.error("saveeeeorr" + e);
		}
		return false;
	}

	public static void sendunregistshareCounpon(Long id) {

		// 赠送优惠劵
		TOptions options = TOptions.findOption(5);
		String Counponcode = options.textObject;
		if (Counponcode != null && !(Counponcode.toString().trim().equals(""))) {
			Counponcode = Counponcode.replace("，", ",");
			String[] counponcodes = Counponcode.split(",");
			if (counponcodes.length > 0) {
				CounponController.getsharecounpon(counponcodes[0], id);
			}
		}

	}

	/**
	 * 给网页端输入的手机号发送优惠劵
	 * 
	 * @param telephonenumber
	 * @param uniqueurl
	 */
	public static int sendtouser(Long telephonenumber, Tuniqueurl uniqueurl,
			int times, String[] counponcodes) {
		// 返回标识
		int result;
		// 证明该链接的分享获取优惠劵次数未用完

		if (uniqueurl.sharetime < times) {
			// 判断是老用户还是新用户
			TuserInfo userInfo = TuserInfo.findDataByPhoneId(telephonenumber);

			// 老用户
			if (userInfo != null) {
				// 随机赠送优惠劵
				result = sendrandomshareCounpon(counponcodes, userInfo.userid);
				// url的分享次数加一
				Tuniqueurl.updateTuniqueurl(uniqueurl.url,
						uniqueurl.sharetime + 1, telephonenumber.toString());
				return result;
			}
			// 新用户,记录用户信息，注册后赠送优惠劵
			else if (userInfo == null) {
				Tunregisteruser unregisteruser = Tunregisteruser
						.findDataById(telephonenumber);
				// 不在未注册用户列表中
				if (unregisteruser == null) {
					Tunregisteruser.saveunregistershare(telephonenumber);
					Tuniqueurl
							.updateTuniqueurl(uniqueurl.url,
									uniqueurl.sharetime + 1,
									telephonenumber.toString());
					return 4;
				} else if (unregisteruser != null) {
					Tunregisteruser.updateunregistershare(telephonenumber,
							unregisteruser.sharetime + 1);
					Tuniqueurl
							.updateTuniqueurl(uniqueurl.url,
									uniqueurl.sharetime + 1,
									telephonenumber.toString());
					return 4;
				}

			}
		}
		return 0;
	}
	
	//点击分享按钮之后直接保存url信息（解决留在微信后链接无效问题）
	public static Result saveurl(String url){
		Logger.info("start to save url");
		ComResponse<String> response = new ComResponse<String>();
		//查询是否有记录
		Tuniqueurl tuniqueurl =Tuniqueurl.findDataById(url);
		if(tuniqueurl==null){
		try {
			Tuniqueurl.saveTuniqueurl(url);
			response.setResponseStatus(ComResponse.STATUS_OK);
		} catch (Exception e) {
			Logger.error("saveeeeorr" + e);
			response.setResponseStatus(ComResponse.STATUS_FAIL);
		}
		}
		else {
			Logger.info("url is exist");
			response.setResponseStatus(ComResponse.STATUS_OK);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		return ok(tempJsonString);
		
	}
}
