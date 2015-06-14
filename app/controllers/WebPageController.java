package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.info.ChartCityEntity;
import models.info.TLog;
import models.info.TOrder;
import models.info.TParkInfo;
import models.info.TParkInfoPro_Loc;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Img;
import models.info.TParkInfo_Loc;
import models.info.TParkInfo_Py;
import models.info.TParkInfo_adm;
import models.info.TVersion;
import models.info.TuserInfo;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.CommFindEntity;
import utils.Constants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.fasterxml.jackson.databind.JsonNode;

public class WebPageController extends Controller {

	/**
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoDashboard() {
		Logger.debug("goto gotoDashboard");

		int parkingProdCount = TParkInfoProd.findCount();

		int orderCount = TOrder.findDoneCount();

		int userCount = TuserInfo.findCount();

		double paymentCount = TParkInfo_Py.findDonePayment();

		Logger.debug("summary information,parkingProdCount->"
				+ parkingProdCount + ",orderCount->" + orderCount
				+ ",userCount->" + userCount + ",paymentCount->" + paymentCount);

		flash("parkingProdCount", "" + parkingProdCount);
		flash("orderCount", "" + orderCount);
		flash("userCount", "" + userCount);
		flash("paymentCount", "" + paymentCount);

		return ok(views.html.dashboard.render());
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoParking(int currentPage, int pageSize,
			String orderBy, String key, String searchObj) {
		Logger.debug("goto gotoParking");
		Page<TParkInfo> allData = TParkInfo.pageByFilter(currentPage, pageSize,
				orderBy, key, searchObj);
		return ok(views.html.parking.render(allData, currentPage, pageSize,
				orderBy, key, searchObj));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result getTaskOfParking(int currentPage, int pageSize,
			String orderBy) {
		Logger.debug("goto getTaskOfParking");
		CommFindEntity<TParkInfo> allData = TParkInfo.findData(currentPage,
				pageSize, orderBy);

		String json = ParkController.gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		return ok(jsonNode);

	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoParkingProd(int currentPage, int pageSize,
			String orderBy, String key, String searchObj) {
		Logger.debug("goto gotoParkingProd");
		Page<TParkInfoProd> allData = TParkInfoProd.pageByFilter(currentPage,
				pageSize, orderBy, key, searchObj);
		return ok(views.html.parkingprod.render(allData, currentPage, pageSize,
				orderBy, key, searchObj));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoParkingProdForPopup(int currentPage, int pageSize,
			String orderBy, String key, String searchObj) {
		Logger.debug("goto gotoParkingProdForPopup");
		Page<TParkInfoProd> allData = TParkInfoProd.pageByFilter(currentPage,
				pageSize, orderBy, key, searchObj);
		flash("onlyshow","false");
		return ok(views.html.popupparkprod.render(allData, currentPage,
				pageSize, orderBy, key, searchObj));
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result gotoParkingProdForPopupByAdmin(int currentPage, int pageSize,
			String orderBy, long userid) {
		Logger.debug("goto gotoParkingProdForPopupByAdmin");
		Page<TParkInfoProd> allData = TParkInfo_adm.findDataByUserId(currentPage,
				pageSize, orderBy, userid);
		flash("onlyshow","true");
		return ok(views.html.popupparkprod.render(allData, currentPage,
				pageSize, orderBy, "", ""));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoDetailParkingProd(long parking) {
		Logger.debug("goto gotoDetailParkingProd");
		TParkInfoProd allData = TParkInfoProd.findDataById(parking);

		String makerString = "";
		if (allData != null) {
			List<TParkInfoPro_Loc> locAarray = allData.latLngArray;
			if (locAarray != null) {

				for (TParkInfoPro_Loc loc : locAarray) {
					makerString += loc.longitude + "," + loc.latitude + "|";
				}

				if (makerString.length() > 0) {
					makerString = makerString.substring(0,
							makerString.length() - 2);
				}
			}
		}

		flash("makerString", makerString);

		return ok(views.html.parkingdetailprod.render(allData));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoDetailParking(long parking) {
		Logger.debug("goto gotoDetailParking");
		TParkInfo allData = TParkInfo.findDataById(parking);

		String makerString = "";
		if (allData != null) {
			List<TParkInfo_Loc> locAarray = allData.latLngArray;
			if (locAarray != null) {

				for (TParkInfo_Loc loc : locAarray) {
					makerString += loc.longitude + "," + loc.latitude + "|";
				}

				if (makerString.length() > 0) {
					makerString = makerString.substring(0,
							makerString.length() - 2);
				}
			}
		}

		flash("makerString", makerString);

		return ok(views.html.parkingdetail.render(allData));
	}

	/**
	 * 删除多个数据
	 * 
	 * @param parkingIdArray
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result deleteParking(String pidarray) {
		Logger.info("GOTO deleteParking,FOR" + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.info("try to delete pid:" + pid);
					TParkInfo.deleteData(pid);
					LogController.info("delete parking for id:"+pidarray);
				} catch (Exception e) {
					Logger.error("deleteParking:" + pidString, e);
				}
			}
			return ok("" + pids.length);
		}

		return ok("0");
	}

	@Security.Authenticated(SecurityController.class)
	public static Result approveParking(String pidarray) {
		Logger.info("GOTO approveParking,FOR" + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				if(!pidString.trim().equals("")){
					try {
						long pid = Long.parseLong(pidString);
						Logger.info("try to approve pid:" + pid);
						ParkProdController.copyData(pid);
						LogController.info("approve parking for submitted parking id:"+pidarray);
					} catch (Exception e) {
						Logger.error("deleteParking:" + pidString, e);
					}
				}
			}
			return ok("" + pids.length);
		}

		return ok("0");
	}

	@Security.Authenticated(SecurityController.class)
	public static Result retrieveParking(String pidarray) {
		Logger.info("GOTO retrieveParking,FOR" + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.info("try to copy2orin pid:" + pid);
					ParkProdController.copyDataToOringal(pid);
					LogController.info("move produce data to temp box for parking id:"+pidarray);
				} catch (Exception e) {
					Logger.error("deleteParking:" + pidString, e);
				}
			}
			return ok("" + pids.length);
		}

		return ok("0");
	}

	@Security.Authenticated(SecurityController.class)
	public static Result saveParkingData() {
		Logger.debug("goto saveParkingData");
		// DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TParkInfo> form = Form.form(TParkInfo.class).bindFromRequest();
		if (form.hasErrors()) {
			JsonNode node = form.errorsAsJson();
			Logger.error("###########getglobalError:" + node);
			return badRequest(node.toString());
		}
		TParkInfo parkinfo = form.get();
		if (parkinfo != null) {
			Logger.debug("###########get parkId:" + parkinfo.parkId);
			parkinfo.updatePerson=session("username");
			TParkInfo.saveData(parkinfo);
			LogController.info("save parking data for "+parkinfo.parkname);
		}

		return ok("提交成功.");
	}
	
	/**
	 * 打开关闭停车场
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @param key
	 * @param searchObj
	 * @param parkingId
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result updateParkingOpenClose(int currentPage, int pageSize,
			String orderBy, String key, String searchObj, String pidarray){
		Logger.debug("goto updateParkingOpenClose,pidarray:"+pidarray);
		
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.debug("update for parkingprod:"+pid);
					List<TParkInfoPro_Loc> result = TParkInfoPro_Loc.getLocationPointByParkingId(pid);
					if(result!=null&&result.size()>0){
						TParkInfoPro_Loc loc = result.get(0);
						//判断其中一个点是否开放
						int isopen = loc.isOpen;
						for(TParkInfoPro_Loc tmp : result){
							Logger.debug("update TParkInfoPro_Loc status:"+isopen+" to new status");
							if(isopen==0){
								tmp.isOpen=1;
								TParkInfoPro_Loc.saveData(tmp);
							}else if(isopen==1){
								tmp.isOpen=0;
								TParkInfoPro_Loc.saveData(tmp);
							}
						}
					}
					
					
				} catch (Exception e) {
					Logger.error("updateParkingOpenClose:" + pidString, e);
				}
			}
			
			LogController.debug("updated status open/close for "+pidarray);
		}
		
		
		
		return gotoParkingProd(currentPage, pageSize,
				orderBy, key, searchObj);
	}

	/**
	 * 为停车场上传图片
	 * @param parkingId
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result uploadImage(long parkingId) {
		Logger.debug("goto uploadImage");
		try {
			UploadController.uploadToParking(parkingId);

			CommFindEntity<TParkInfo_Img> imageArray = TParkInfo_Img
					.findData(parkingId);
			List<TParkInfo_Img> array = new ArrayList<TParkInfo_Img>();
			if (imageArray != null && imageArray.getResult() != null) {
				array = imageArray.getResult();
			}
			String json = ParkController.gsonBuilderWithExpose.toJson(array);
			JsonNode jsonNode = Json.parse(json);
			
			LogController.debug("upload image for parking id "+parkingId);
			return ok(jsonNode);

		} catch (IOException e) {
			Logger.error("uploadImage:", e);
			return badRequest();
		}

	}

	/**
	 * 打开用户页面
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @param type
	 * @param filter
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoUser(int currentPage, int pageSize,
			String orderBy, int type, String filter) {
		Logger.debug("goto gotoUser,type" + type);
		Page<TuserInfo> allData = TuserInfo.pageByTypeAndFilter(currentPage,
				pageSize, orderBy, type, filter);

		flash("type", "" + type);
		flash("filter", filter);
		if (allData != null) {
			Logger.debug("##########goto gotoUser,total:"
					+ allData.getTotalRowCount());
		}

		return ok(views.html.users.render(allData, currentPage, pageSize,
				orderBy, filter));
	}

	/**
	 * 打开新增用户页面
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoUserAdd() {
		Logger.debug("goto gotoUserAdd");
		return ok(views.html.useradd.render());
	}

	/**
	 * 保存用户
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result saveUserData() {
		Logger.debug("goto saveUserData");
		// DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TuserInfo> form = Form.form(TuserInfo.class).bindFromRequest();
		if (form.hasErrors()) {
			JsonNode node = form.errorsAsJson();
			Logger.error("###########getglobalError:" + node);
			return badRequest(node.toString());
		}
		TuserInfo info = form.get();
		if (info != null) {
			Logger.debug("###########get parkId:" + info.userName);
			Date current = new Date();
			String userName = session("username");
			info.updateDate = current;
			info.createPerson = userName;
			info.updatePerson = userName;
			TuserInfo.saveData(info);
			LogController.info("save user for "+info.userPhone);
		}

		return ok("提交成功.");
	}

	/**
	 * 打开用户密码页面
	 * @param userPhone
	 * @param type
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoPasswd(String userPhone, int type) {
		Logger.debug("goto gotoUserAdd");
		return ok(views.html.userpass.render(userPhone, type));
	}

	/**
	 * 打开用户修改页面
	 * @param idArray
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoMutipPasswd(String idArray) {
		Logger.debug("goto gotoMutipPasswd");
		return ok(views.html.userpass.render(idArray, 2));
	}

	/**
	 * 更新密码
	 * @param userPhone
	 * @param type
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result updatedUserPasswdData(String userPhone, int type) {
		Logger.debug("goto updatedUserPasswdData");
		DynamicForm dynamicForm = Form.form().bindFromRequest();

		String newpasswd = dynamicForm.get("newpasswd");
		String newpasswd2 = dynamicForm.get("newpasswd2");

		if (!newpasswd.equals(newpasswd2)) {
			return badRequest("二次新密码输入不一致,请重新输入.");
		}

		if (type == 1) {
			try {
				long userPhoneLong = Long.parseLong(userPhone);
				TuserInfo userinfo = TuserInfo.findDataByPhoneId(userPhoneLong);
				String currentpasswd = dynamicForm.get("passwd");
				if (userinfo != null) {
					if (!userinfo.passwd.equals(currentpasswd)) {
						return badRequest("当前密码输入错误，请重新输入.");
					}
				} else {
					return badRequest("当前用户不存在");
				}

				userinfo.passwd = newpasswd;
				TuserInfo.saveData(userinfo);
				LogController.info("change passsword for "+userPhone);
				return ok(type == 1 ? "密码修改成功" : "密码重置成功");
			} catch (Exception e) {
				Logger.error("updatedUserPasswdData", e);
				return badRequest(e.getMessage());
			}

		} else {
			String resetobject = dynamicForm.get("resetobject");

			if (resetobject != null) {
				String[] resetIds = resetobject.split(",");
				String succussString = "";
				for (String resetId : resetIds) {
					try {
						long id = Long.parseLong(resetId);
						TuserInfo userinfo = TuserInfo.findDataById(id);

						userinfo.passwd = newpasswd;
						TuserInfo.saveData(userinfo);
						succussString += userinfo.userName + " ";
					} catch (Exception e) {
						Logger.error("updatedUserPasswdData", e);
					}
				}
				LogController.info("reset passsword for "+userPhone);
				return ok(succussString + "密码重置成功");
			} else {
				return ok("没有获取到重置对象");
			}
		}

	}

	/**
	 * 删除用户
	 * @param pidarray
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result deleteUser(String pidarray) {
		Logger.info("GOTO deleteParking,FOR" + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.info("try to delete userid:" + pid);
					TuserInfo.deleteData(pid);
					LogController.info("delete user:"+pidarray);
				} catch (Exception e) {
					Logger.error("deleteUser:" + pidString, e);
				}
			}
			return ok("" + pids.length);
		}

		return ok("0");
	}

	/**
	 * 更新用户
	 * @param type
	 * @param pidarray
	 * @param admpark
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result updateUser(final int type, final String pidarray,final String admpark) {
		Logger.info("GOTO updateUser,FOR " + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (final String pidString : pids) {

				Ebean.execute(new TxRunnable() {
					public void run() {
						try {
							long pid = Long.parseLong(pidString);
							Logger.info("------------------------try to update userid:"
									+ pid);
							TuserInfo user = TuserInfo.findDataById(pid);
							user.userType = type;
							TuserInfo.saveData(user);

							// 是一个车位管理员
							if (type >= Constants.USER_TYPE_PADMIN
									&& type < (Constants.USER_TYPE_PADMIN * 10)) {
								if (admpark != null && admpark.length() > 0) {
									String[] parkIds = admpark.split(",");
									Logger.info("--------try to update admin for "
											+ admpark);
									for (String parid : parkIds) {
										
										TParkInfo_adm adm = TParkInfo_adm.findByUserAndPark(pid, Long.parseLong(parid));
										if(adm==null){
										  adm = new TParkInfo_adm();
										}
										adm.userInfo = user;
										TParkInfoProd parkprod = new TParkInfoProd();
										parkprod.parkId = Long.parseLong(parid);
										adm.parkInfo = parkprod;
										TParkInfo_adm.saveData(adm);
									}
								}
								
								
							}else{
								TParkInfo_adm.deleteDataByUser(pid);
							}
							LogController.info("updated user:"+pidString);
						} catch (Exception e) {
							Logger.error("updateUser:" + pidString, e);
						}
					}
				});
			}
			return ok("" + pids.length);
		}

		return ok("0");
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoOrder(int currentPage, int pageSize,
			String orderBy, String city, String filter) {
		Logger.debug("goto gotoOrder,city" + city);
		Page<TOrder> allData = TOrder.pageByFilter(currentPage, pageSize,
				orderBy, city, filter);

		if (allData != null) {
			Logger.debug("##########goto gotoUser,total:"
					+ allData.getTotalRowCount());
		}

		return ok(views.html.order.render(allData, currentPage, pageSize,
				orderBy, city, filter));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoDetailOrder(long orderId) {
		Logger.debug("goto gotoDetailOrder");
		TOrder allData = TOrder.findDataById(orderId);

		// String makerString = "";
		// if (allData != null) {
		// List<TParkInfo_Loc> locAarray = allData.latLngArray;
		// if (locAarray != null) {
		//
		// for (TParkInfo_Loc loc : locAarray) {
		// makerString += loc.longitude + "," + loc.latitude + "|";
		// }
		//
		// if (makerString.length() > 0) {
		// makerString = makerString.substring(0,
		// makerString.length() - 2);
		// }
		// }
		// }
		//
		// flash("makerString", makerString);

		return ok(views.html.orderdetail.render(allData));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result setExceptionOrder(String pidarray) {
		Logger.debug("goto setExceptionOrder");

		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.info("try to set exception for:" + pid);
					TOrder allData = TOrder.findDataById(pid);
					if (allData != null) {
						allData.orderStatus = Constants.ORDER_TYPE_EXCPTION;
						TOrder.saveData(allData);
						Logger.debug("done for set exception:" + pid);
						LogController.info("done for set exception:"+pid);
					}
				} catch (Exception e) {
					Logger.error("deleteUser:" + pidString, e);
				}
			}
			return ok("" + pids.length);
		}

		return ok("0");
	}

	/**
	 * 跳转到订单图
	 * 
	 * @return
	 */
	public static Result gotoOrderChart() {
		Logger.debug("goto gotoOrderChart");
		return ok(views.html.orderchart.render());
	}

	public static Result gotoParkingChart() {
		Logger.debug("goto gotoParkingChart");
		return ok(views.html.chartparking.render());
	}

	
	/**
	 * 跳转版本界面
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoVersion() {
		Logger.debug("goto gotoVersion");
		
		TVersion tversion = TVersion.findVersion();
		
		return ok(views.html.version.render(tversion));
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result saveVersionData() {
		Logger.debug("goto saveVersionData");
		// DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TVersion> form = Form.form(TVersion.class).bindFromRequest();
		if (form.hasErrors()) {
			JsonNode node = form.errorsAsJson();
			Logger.error("###########getglobalError:" + node);
			return badRequest(node.toString());
		}
		TVersion info = form.get();
		if (info != null) {
			if(info.versionId==null||info.versionId<=0){
			   TVersion.saveData(info);
			}else{
			   TVersion.updateData(info);
			}
			LogController.info("version delivery:"+info.version);
		}

		return ok("提交成功.");
	}
	
	/**
	 * 返回json的城市订单数据
	 * 
	 * @param city
	 * @return
	 */
	public static Result getCityOrderChart(String city) {
		Logger.debug("goto getCityOrderChart:" + city);
		HashMap<String, List<ChartCityEntity>> map = ChartCityEntity
				.getTop30OrderForEachCity(city);

		String json = OrderController.gsonBuilderWithExpose.toJson(map);
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("return json:" + json);
		return ok(jsonNode);
	}

	/**
	 * 返回json的城市订单数据
	 * 
	 * @param city
	 * @return
	 */
	public static Result getPersonOrderChart(String createPerson) {
		Logger.debug("goto getPersonOrderChart:" + createPerson);
		HashMap<String, List<ChartCityEntity>> map = ChartCityEntity
				.getTop30OrderForEachCaiji(createPerson);

		String json = OrderController.gsonBuilderWithExpose.toJson(map);
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("return json:" + json);
		return ok(jsonNode);
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result gotoLog(int currentPage, int pageSize,
			String orderBy, String filter) {
		Logger.debug("goto gotoLog");
		Page<TLog> allData = TLog.findWebLog(currentPage,
				pageSize, orderBy, filter);
		return ok(views.html.log.render(allData, currentPage, pageSize,
				orderBy, filter));
	}

}
