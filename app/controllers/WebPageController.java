package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import models.info.ChartCityEntity;
import models.info.TAllowance;
import models.info.TAllowanceOffer;
import models.info.TCouponEntity;
import models.info.TCouponHis;
import models.info.TIncome;
import models.info.TLog;
import models.info.TOptions;
import models.info.TOrder;
import models.info.TOrderHis;
import models.info.TParkInfo;
import models.info.TParkInfoPro_Loc;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Comment;
import models.info.TParkInfo_Comment_Keyword;
import models.info.TParkInfo_Img;
import models.info.TParkInfo_Loc;
import models.info.TParkInfo_adm;
import models.info.TTakeCash;
import models.info.TUseCouponEntity;
import models.info.TVersion;
import models.info.TuserInfo;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Crypto;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import utils.CommFindEntity;
import utils.ConfigHelper;
import utils.Constants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.fasterxml.jackson.databind.JsonNode;

public class WebPageController extends Controller {
	
	
	// 图片存储路径
	public static String image_store_guide_path = ConfigHelper
			.getString("image.store.guide.path");
	public static String image_url_header = ConfigHelper
			.getString("image.url.header");

	/**
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoDashboard() {
		Logger.debug("goto gotoDashboard");

		int parkingProdCount = TParkInfoProd.findCount();

		int orderCount = TOrder.findAllCount() + TOrderHis.findAllCount();

		int userCount = TuserInfo.findCount();

		double paymentCount = TIncome.findDonePayment();

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
			String orderBy, String key, String searchObj, int isopen) {
		Logger.debug("goto gotoParkingProd");
		Page<TParkInfoProd> allData = TParkInfoProd.pageByFilter(currentPage,
				pageSize, orderBy, key, searchObj, isopen);
		return ok(views.html.parkingprod.render(allData, currentPage, pageSize,
				orderBy, key, searchObj, isopen));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoparkcomment(int currentPage, int pageSize,
			String orderBy, String key, String searchObj) {
		Logger.debug("goto gotoParkingProd");
		Page<TParkInfo_Comment> allData = TParkInfo_Comment.pageByFilter(
				currentPage, pageSize, orderBy, key, searchObj);
		return ok(views.html.parkcomment.render(allData, currentPage, pageSize,
				orderBy, key, searchObj));
	}

	/**
	 * 删除评论
	 * 
	 * @param pidarray
	 * @return
	 */

	@Security.Authenticated(SecurityController.class)
	public static Result deletecomments(String pidarray, int currentPage,
			int pageSize, String orderBy, String key, String searchObj) {

		Logger.info("GOTO deletecomments,FOR:" + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.info("try to delete orderid:" + pid);
					TParkInfo_Comment.deleteData(pid);
					LogController.info("delete order:" + pidString);
				} catch (Exception e) {
					Logger.error("deleteOrder:" + pidString, e);
				}
			}
			return gotoparkcomment(currentPage, pageSize, orderBy, key,
					searchObj);
		}

		return ok("0");
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

	// 关键字搜索

	@Security.Authenticated(SecurityController.class)
	public static Result gotokeywordpage(int currentPage, int pageSize,
			String orderBy, String searchObj) {
		Logger.debug("goto keywordpage");
		Page<TParkInfo_Comment_Keyword> allData = TParkInfo_Comment_Keyword
				.pageByFilter(currentPage, pageSize, orderBy, searchObj);
		return ok(views.html.commentskeyword.render(allData, currentPage,
				pageSize, orderBy, searchObj));
	}

	// 删除关键字
	@Security.Authenticated(SecurityController.class)
	public static Result deletekeyword(String pidarray, int currentPage,
			int pageSize, String orderBy, String searchObj) {

		Logger.info("GOTO deletekeyword,FOR:" + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.info("try to delete orderid:" + pid);
					TParkInfo_Comment_Keyword.deleteData(pid);
					LogController.info("delete order:" + pidString);
				} catch (Exception e) {
					Logger.error("deleteOrder:" + pidString, e);
				}
			}
			return gotokeywordpage(currentPage, pageSize, orderBy, searchObj);
		}
		return ok("0");
	}

	// 添加关键字
	@Security.Authenticated(SecurityController.class)
	public static Result addkeyword(String keyword, int currentPage,
			int pageSize, String orderBy, String searchObj) {
		Logger.info("GOTO addkeyword,FOR:" + keyword);
		if (keyword != null && keyword.toString().trim() != "") {
			TParkInfo_Comment_Keyword.saveData(keyword);
		}
		return gotokeywordpage(currentPage, pageSize, orderBy, searchObj);
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoParkAccount(int currentPage, int pageSize,
			String orderBy, String key, String searchObj, int isopen) {
		Logger.debug("goto gotoParkAccount");
		Page<TParkInfoProd> allData = TParkInfoProd.pageByFilter(currentPage,
				pageSize, orderBy, key, searchObj, isopen);
		return ok(views.html.parkaccount.render(allData, currentPage, pageSize,
				orderBy, key, searchObj, isopen));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result saveParkingAcctounyData(int currentPage, int pageSize,
			String orderBy, String key, String searchObj, int isopen,
			long parkId, String bankName, String bankAccount) {
		Logger.debug("goto saveParkingAcctounyData");

		try {
			String userName = session("username");
			TParkInfoProd prod = TParkInfoProd.findDataById(parkId);
			prod.venderBankName = bankName;
			prod.venderBankNumber = bankAccount;
			prod.updateDate = new Date();
			prod.updatePerson = userName;

			Set<String> options = new HashSet<String>();
			options.add("venderBankName");
			options.add("venderBankNumber");
			options.add("updateDate");
			options.add("updatePerson");
			Ebean.update(prod, options);

			LogController.info("set bank account for parkid" + parkId + " to "
					+ bankAccount);
		} catch (Exception e) {
			Logger.error("saveParkingAcctounyData", e);
		}

		return gotoParkAccount(currentPage, pageSize, orderBy, key, searchObj,
				isopen);
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoTakeCash(int currentPage, int pageSize,
			String orderBy) {
		Logger.debug("goto gotoTakeCash");
		Page<TTakeCash> allData = TTakeCash.findPageDataForWeb(currentPage,
				pageSize, orderBy);
		return ok(views.html.takecash.render(allData, currentPage, pageSize,
				orderBy));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoParkingProdForPopup(int currentPage, int pageSize,
			String orderBy, String key, String searchObj) {
		Logger.debug("goto gotoParkingProdForPopup");
		Page<TParkInfoProd> allData = TParkInfoProd.pageByFilter(currentPage,
				pageSize, orderBy, key, searchObj, -1);
		flash("onlyshow", "false");
		return ok(views.html.popupparkprod.render(allData, currentPage,
				pageSize, orderBy, key, searchObj));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoParkingProdForPopupAdd(int currentPage,
			int pageSize, String orderBy, String key, String searchObj) {
		Logger.debug("goto gotoParkingProdForPopupAdd");
		Page<TParkInfoProd> allData = TParkInfoProd.pageByFilter(currentPage,
				pageSize, orderBy, key, searchObj, -1);
		return ok(views.html.popupAddparkprod.render(allData, currentPage,
				pageSize, orderBy, key, searchObj));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoParkingProdForPopupByAdmin(int currentPage,
			int pageSize, String orderBy, long userid) {
		Logger.debug("goto gotoParkingProdForPopupByAdmin");
		Page<TParkInfoProd> allData = TParkInfo_adm.findDataByUserId(
				currentPage, pageSize, orderBy, userid);
		flash("onlyshow", "true");
		return ok(views.html.popupparkprod.render(allData, currentPage,
				pageSize, orderBy, "", ""));
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
					LogController.info("delete parking for id:" + pidarray);
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
			LogController.info("approve parking for submitted parking id:"
					+ pidarray);
			for (String pidString : pids) {
				if (!pidString.trim().equals("")) {
					try {
						long pid = Long.parseLong(pidString);
						Logger.info("try to approve pid:" + pid);
						ParkProdController.copyData(pid);
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
					LogController
							.info("move produce data to temp box for parking id:"
									+ pidarray);
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
			parkinfo.updatePerson = session("username");
			TParkInfo.saveData(parkinfo);
			LogController.info("save parking data for " + parkinfo.parkname);
		}

		return ok("提交成功.");
	}

	/**
	 * 调价
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result saveParkingProdData() {
		Logger.debug("goto saveParkingProdData");
		// DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TParkInfoProd> form = Form.form(TParkInfoProd.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			JsonNode node = form.errorsAsJson();
			Logger.error("###########getglobalError:" + node);
			return badRequest(node.toString());
		}
		TParkInfoProd parkinfo = form.get();
		if (parkinfo != null) {
			Logger.debug("###########get produce parkId:" + parkinfo.parkId);
			parkinfo.updatePerson = session("username");
			TParkInfoProd.saveData(parkinfo);
			LogController.info("save parking produce data for "
					+ parkinfo.parkname);
		}

		return ok("提交成功.");
	}

	/**
	 * 打开关闭停车场
	 * 
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
			String orderBy, String key, String searchObj, String pidarray,
			int isopen) {
		Logger.debug("goto updateParkingOpenClose,pidarray:" + pidarray);

		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			String userName = session("username");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.debug("update for parkingprod:" + pid);
					TParkInfoPro_Loc.saveOpenCloseStatus(pid, userName);
				} catch (Exception e) {
					Logger.error("updateParkingOpenClose:" + pidString, e);
				}
			}

			LogController.debug("updated status open/close for " + pidarray);
		}

		return gotoParkingProd(currentPage, pageSize, orderBy, key, searchObj,
				isopen);
	}

	/**
	 * 为停车场上传图片
	 * 
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

			LogController.debug("upload image for parking id " + parkingId);
			return ok(jsonNode);

		} catch (IOException e) {
			Logger.error("uploadImage:", e);
			return badRequest();
		}

	}


	/**
	 * 打开用户页面
	 * 
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
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoUserAdd() {
		Logger.debug("goto gotoUserAdd");
		return ok(views.html.useradd.render());
	}

	/**
	 * 保存用户
	 * 
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
			LogController.info("save user for " + info.userPhone);
		}

		return ok("提交成功.");
	}

	/**
	 * 打开用户密码页面
	 * 
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
	 * 
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
	 * 
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
					try {
						if (!Crypto.decryptAES(userinfo.passwd).equals(
								currentpasswd)) {
							// if (!userinfo.passwd.equals(currentpasswd)) {
							return badRequest("当前密码输入错误，请重新输入.");
						}
					} catch (Exception e) {
						if (!userinfo.passwd.equals(currentpasswd)) {
							// if (!userinfo.passwd.equals(currentpasswd)) {
							return badRequest("当前密码输入错误，请重新输入.");
						}
					}
				} else {
					return badRequest("当前用户不存在");
				}

				userinfo.passwd = Crypto.encryptAES(newpasswd);
				;
				TuserInfo.saveData(userinfo);
				LogController.info("change passsword for " + userPhone);
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
						Logger.debug("target>>>>>>>>>>>>>>>>>" + newpasswd);
						long id = Long.parseLong(resetId);
						TuserInfo userinfo = TuserInfo.findDataById(id);

						userinfo.passwd = Crypto.encryptAES(newpasswd);
						TuserInfo.saveData(userinfo);
						succussString += userinfo.userName + " ";
					} catch (Exception e) {
						Logger.error("updatedUserPasswdData", e);
					}
				}
				LogController.info("reset passsword for " + userPhone);
				return ok(succussString + "密码重置成功");
			} else {
				return ok("没有获取到重置对象");
			}
		}

	}

	/**
	 * 删除用户
	 * 
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
					LogController.info("delete user:" + pidarray);
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
	 * 
	 * @param type
	 * @param pidarray
	 * @param admpark
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result updateUser(final int type, final String pidarray,
			final String admpark) {
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
							user.updateDate = new Date();
							user.updatePerson = session("username");

							Set<String> options = new HashSet<String>();
							options.add("userType");
							options.add("updateDate");
							options.add("updatePerson");

							Ebean.update(user, options);
							// TuserInfo.saveData(user,options);

							// 是一个车位管理员
							if (type >= Constants.USER_TYPE_PADMIN
									&& type < (Constants.USER_TYPE_PADMIN * 10)) {
								if (admpark != null && admpark.length() > 0) {
									String[] parkIds = admpark.split(",");
									Logger.info("--------try to update admin for "
											+ admpark);
									for (String parid : parkIds) {

										TParkInfo_adm adm = TParkInfo_adm
												.findByUserAndPark(pid,
														Long.parseLong(parid));
										if (adm == null) {
											adm = new TParkInfo_adm();
										}
										adm.userInfo = user;
										TParkInfoProd parkprod = new TParkInfoProd();
										parkprod.parkId = Long.parseLong(parid);
										adm.parkInfo = parkprod;
										TParkInfo_adm.saveData(adm);
									}
								}

							} else {
								TParkInfo_adm.deleteDataByUser(pid);
							}
							LogController.info("updated user:" + pidString);
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
	public static Result deleteUserAdm(final String pidarray,
			final String admpark) {
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

							if (admpark != null && admpark.length() > 0) {
								String[] parkIds = admpark.split(",");
								Logger.info("--------try to delete admin for "
										+ admpark);
								for (String parid : parkIds) {
									TParkInfo_adm.deleteDataByUserAndParkid(
											pid, Long.parseLong(parid));
								}
							}

							LogController
									.info("updated user["
											+ pid
											+ "], delete authorized administrator for park:"
											+ admpark);
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
	public static Result addUserAdm(final String pidarray, final String admpark) {
		Logger.info("GOTO updateUser,FOR " + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (final String pidString : pids) {

				Ebean.execute(new TxRunnable() {
					public void run() {
						try {
							long pid = Long.parseLong(pidString);
							// Logger.info("------------------------try to update userid:"
							// + pid);

							if (admpark != null && admpark.length() > 0) {
								String[] parkIds = admpark.split(",");
								Logger.info("--------try to add authorized administrator for "
										+ admpark + ",for userid:" + pid);
								for (String parid : parkIds) {

									if (TParkInfo_adm.findByUserAndParkCount(
											pid, Long.parseLong(parid)) <= 0) {
										TParkInfo_adm adm = new TParkInfo_adm();
										TuserInfo userInfo = new TuserInfo();
										userInfo.userid = pid;
										adm.userInfo = userInfo;

										TParkInfoProd prod = new TParkInfoProd();
										prod.parkId = Long.parseLong(parid);
										adm.parkInfo = prod;

										TParkInfo_adm.saveData(adm);
									} else {
										Logger.warn("userid:"
												+ pid
												+ ",parkid:"
												+ parid
												+ ",existing.not save this time.");
									}

								}
							}

							LogController
									.info("updated user["
											+ pid
											+ "], delete authorized administrator for park:"
											+ admpark);
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
	public static Result gotoOrderHis(int currentPage, int pageSize,
			String orderBy, String city, String filter) {
		Logger.debug("goto gotoOrderHis,city" + city);
		Page<TOrderHis> allData = TOrderHis.pageByFilter(currentPage, pageSize,
				orderBy, city, filter);

		if (allData != null) {
			Logger.debug("##########goto gotoUser,total:"
					+ allData.getTotalRowCount());
		}

		return ok(views.html.orderHis.render(allData, currentPage, pageSize,
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
	public static Result gotoDetailOrderHis(long orderId, String from) {
		Logger.debug("goto gotoDetailOrder");
		TOrderHis allData = TOrderHis.findDataById(orderId);

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

		flash("from", from);

		return ok(views.html.orderdetailhis.render(allData));
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
						TOrderHis.moveToHisFromOrder(pid,
								Constants.ORDER_TYPE_EXCPTION);
						Logger.debug("done for set exception:" + pid);
						LogController.info("done for set exception:" + pid);
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
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoVersion() {
		Logger.debug("goto gotoVersion");
		if (image_store_guide_path == null
				|| image_store_guide_path.length() <= 0) {
			image_store_guide_path = "/temp/guide";
		}
		List<String> path = new ArrayList<String>();
		TVersion tversion = TVersion.findVersion();
		File root = new File(image_store_guide_path);
		try {
			 path = VersionController.showAllFiles(root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> truepaths =new ArrayList<String>();
		for(int i =0;i<path.size();i++)
		{
		    String truepath = image_url_header+path.get(i);
		    truepaths.add(truepath);
		    truepath=null;
		     
		}
		
		Logger.debug("goto gotoVersion"+truepaths);
		return ok(views.html.version.render(tversion,truepaths));
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
	     try {
	    	 VersionController.upload();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (info != null) {
			if (info.versionId == null || info.versionId <= 0) {
				TVersion.saveData(info);
			} else {
				TVersion.updateData(info);
			}
			LogController.info("version delivery:" + info.version);
		}

		return ok("提交成功.");
	}

	/**
	 * 返回json的城市订单数据
	 * 
	 * @param city
	 * @return
	 */
	public static Result getCityOrderChart(int days) {
		Logger.debug("goto getCityOrderChart for days:" + days);
		HashMap<String, List<ChartCityEntity>> map = ChartCityEntity
				.getTop30OrderForEachCity(days);

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
	public static Result getPersonOrderChart(int days) {
		Logger.debug("goto getPersonOrderChart for days:" + days);
		HashMap<String, List<ChartCityEntity>> map = ChartCityEntity
				.getTop30OrderForEachCaiji(days);

		String json = OrderController.gsonBuilderWithExpose.toJson(map);
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("return json:" + json);
		return ok(jsonNode);
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoLog(int currentPage, int pageSize, String orderBy,
			String filter) {
		Logger.debug("goto gotoLog");
		Page<TLog> allData = TLog.findWebLog(currentPage, pageSize, orderBy,
				filter);
		return ok(views.html.log.render(allData, currentPage, pageSize,
				orderBy, filter));
	}

	/**
	 * 删除用户
	 * 
	 * @param pidarray
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result deleteOrder(String pidarray) {
		Logger.info("GOTO deleteOrder,FOR:" + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.info("try to delete orderid:" + pid);
					TOrder.deleteData(pid);
					LogController.info("delete order:" + pidString);
				} catch (Exception e) {
					Logger.error("deleteOrder:" + pidString, e);
				}
			}
			return ok("" + pids.length);
		}

		return ok("0");
	}

	/**
	 * 打开优惠劵
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @param type
	 * @param filter
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoCoupon(int currentPage, int pageSize,
			String orderBy, String filter) {
		Logger.debug("goto gotoCoupon,filter" + filter);
		Page<TCouponEntity> allData = TCouponEntity.pageByTypeAndFilter(
				currentPage, pageSize, orderBy, filter);

		flash("filter", filter);
		if (allData != null) {
			Logger.debug("##########goto gotoCoupon,total:"
					+ allData.getTotalRowCount());
		}

		return ok(views.html.coupon.render(allData, currentPage, pageSize,
				orderBy, filter));
	}
	
	
	
	/**
	 * 打开优使用过的惠劵
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @param type
	 * @param filter
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoCouponinfohis(int currentPage, int pageSize,
			String orderBy, String filter) {
		Logger.debug("goto gotoCoupon,filter" + filter);
		Page<TCouponHis> allData = TCouponHis.pageByTypeAndFilter(
				currentPage, pageSize, orderBy, filter);

		flash("filter", filter);
		if (allData != null) {
			Logger.debug("##########goto gotoCoupon,total:"
					+ allData.getTotalRowCount());
		}

		return ok(views.html.couponinfousedlist.render(allData, currentPage, pageSize,
				orderBy, filter));
	}
	

	/**
	 * 打开使用过优惠劵界面
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @param type
	 * @param filter
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoCouponused(int currentPage, int pageSize,
			String orderBy, String filter) {
		Logger.debug("goto gotoCouponused,filter" + filter);
		Page<TOrderHis> allData = TOrderHis.pageByTypeAndFilter(currentPage,
				pageSize, orderBy, filter);

		flash("filter", filter);
		if (allData != null) {
			Logger.debug("##########goto gotoCoupon,total:"
					+ allData.getTotalRowCount());
		}

		return ok(views.html.couponused.render(allData, currentPage, pageSize,
				orderBy, filter));
	}

	/**
	 * 打开新增用户页面
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoCouponAdd() {
		Logger.debug("goto gotoCouponAdd");
		flash("newcouponcode", UUID.randomUUID().toString());

		return ok(views.html.couponadd.render());
	}

	/**
	 * 保存用户
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result saveCouponData() {
		Logger.debug("goto saveCouponData");
		// DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TCouponEntity> form = Form.form(TCouponEntity.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			JsonNode node = form.errorsAsJson();
			Logger.error("###########getglobalError:" + node);
			return badRequest(node.toString());
		}
		TCouponEntity info = form.get();
		if (info != null) {
			Logger.debug("###########get coupon:" + info.counponCode);
			Date current = new Date();
			String userName = session("username");
			info.createName = userName;
			TCouponEntity.saveData(info);
			LogController.info("save coupon for " + info.counponCode);
		}

		return ok("提交成功.");
	}

	/**
	 * 删除用户
	 * 
	 * @param pidarray
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result deleteCoupon(String pidarray) {
		Logger.info("GOTO deleteCoupon,FOR" + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.info("try to delete coupon:" + pid);
					TCouponEntity.deleteData(pid);
					LogController.info("delete coupon:" + pidarray);
				} catch (Exception e) {
					Logger.error("deleteCoupon:" + pidString, e);
				}
			}
			return ok("" + pids.length);
		}

		return ok("0");
	}

	@Security.Authenticated(SecurityController.class)
	public static Result updateCouponOpenClose(int currentPage, int pageSize,
			String orderBy, String searchObj, String pidarray) {
		Logger.debug("goto updateCouponOpenClose,pidarray:" + pidarray);

		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);
					Logger.debug("update for coupon:" + pid);
					TCouponEntity result = TCouponEntity.findDataById(pid);
					if (result.isable == 1) {
						result.isable = 0;
						TCouponEntity.updateUseable(result);
					} else {
						result.isable = 1;
						TCouponEntity.updateUseable(result);
					}

				} catch (Exception e) {
					Logger.error("updateCouponOpenClose:" + pidString, e);
				}
			}

			LogController.debug("updated coupon open/close for " + pidarray);
		}

		return gotoCoupon(currentPage, pageSize, orderBy, searchObj);
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoIncome(int currentPage, int pageSize,
			String orderBy, String filter) {
		Logger.debug("goto gotoIncome,filter" + filter);
		Page<TIncome> allData = TIncome.pageByTypeAndFilter(currentPage,
				pageSize, orderBy, filter);

		flash("filter", filter);
		if (allData != null) {
			Logger.debug("##########goto gotoUser,total:"
					+ allData.getTotalRowCount());
		}

		return ok(views.html.income.render(allData, currentPage, pageSize,
				orderBy, filter));
	}

	/**
	 * 初始化所有停车场收益
	 * 
	 * @param pageSize
	 * @param orderBy
	 * @param filter
	 * @return
	 */
	public static Result gotoInitIncome(int currentPage, int pageSize,
			String orderBy, String filter) {

		Logger.debug("gotoInitIncome");

		TIncome.initIncome();

		return gotoIncome(currentPage, pageSize, orderBy, filter);
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoIncomeDetailOrderHis(int currentPage,
			int pageSize, String orderBy, long parkId, String filter) {
		Logger.debug("goto gotoIncomeDetailOrderHis,parkingid" + parkId);
		Page<TOrderHis> allData = TOrderHis.pageByFilterForPark(currentPage,
				pageSize, orderBy, parkId, filter);

		if (allData != null) {
			Logger.debug("##########goto gotoUser,total:"
					+ allData.getTotalRowCount());
			if (allData.getList() != null && allData.getList().size() > 0) {
				TParkInfoProd park = allData.getList().get(0).parkInfo;
				flash("parkname", park.parkname);
				flash("parkaddress", park.address);
			} else {
				flash("parkname", "未知ID:" + parkId);
				flash("parkaddress", "");
			}

		}

		return ok(views.html.incomeDetail.render(allData, currentPage,
				pageSize, orderBy, parkId, filter));
	}

	/**
	 * 提现状态变化
	 * 
	 * @param pidarray
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result updateTakeCash(int currentPage, int pageSize,
			String orderBy, int status, String pidarray) {
		Logger.info("GOTO updateTakeCash,FOR" + pidarray);
		if (pidarray != null && pidarray.length() > 0) {
			String[] pids = pidarray.split(",");
			for (String pidString : pids) {
				try {
					long pid = Long.parseLong(pidString);

					TTakeCash cash = TTakeCash.findDataById(pid);
					cash.status = status;

					Set<String> setStr = new HashSet<String>();
					setStr.add("status");

					Ebean.update(cash, setStr);

					LogController.info("update status for apply for cash to "
							+ status + ", for " + pidarray);
				} catch (Exception e) {
					Logger.error("updateTakeCash:" + pidString, e);
				}
			}
		}

		return gotoTakeCash(currentPage, pageSize, orderBy);
	}

	/**
	 * 跳转版本界面
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoOptions() {
		Logger.debug("goto gotoOptions");
		TOptions options = new TOptions();
		if (options.longTextObject != null) {
			options.longTextObject = options.longTextObject.trim();
		}
		return ok(views.html.options.render(options));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoOptionsByType(int type) {
		Logger.debug("goto gotoOptionsByType");
		TOptions options = TOptions.findOption(type);
		options.optionType = type;
		if (options.textObject != null) {
			options.textObject = options.textObject.trim();
		}
		if (options.longTextObject != null) {
			options.longTextObject = options.longTextObject.trim();
		}
		return ok(views.html.options.render(options));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result saveOptionData() {
		Logger.debug("goto saveOptionData");
		// DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TOptions> form = Form.form(TOptions.class).bindFromRequest();
		if (form.hasErrors()) {
			JsonNode node = form.errorsAsJson();
			Logger.error("###########getglobalError:" + node);
			return badRequest(node.toString());
		}
		TOptions info = form.get();
		if (info != null) {
			if (info.textObject != null) {
				info.textObject = info.textObject.trim();
			}
			if (info.longTextObject != null) {
				info.longTextObject = info.longTextObject.trim();
			}
			if (info.optionId == null || info.optionId <= 0) {
				info.updatePerson = session("username");
				TOptions.saveData(info);
			} else {
				info.updatePerson = session("username");
				TOptions.updateData(info);
			}
			LogController.info("save options, type:" + info.optionType);
		}

		return ok("提交成功.");
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoAllowance() {
		Logger.debug("goto gotoAllowance");

		TAllowance allowance = TAllowance.findAllowance();

		return ok(views.html.allowance.render(allowance));
	}

	@Security.Authenticated(SecurityController.class)
	public static Result saveAllowanceData() {
		Logger.debug("goto saveAllowanceData");
		// DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TAllowance> form = Form.form(TAllowance.class).bindFromRequest();
		if (form.hasErrors()) {
			JsonNode node = form.errorsAsJson();
			Logger.error("###########getglobalError:" + node);
			return badRequest(node.toString());
		}
		TAllowance info = form.get();
		if (info != null) {
			info.updateName = session("username");
			if (info.allowanceId <= 0) {
				TAllowance.saveData(info);
			} else {
				TAllowance.updateData(info);
			}

			LogController.info("allowance was changed by " + info.updateName);
		}

		return ok("保存成功");
	}

	@Security.Authenticated(SecurityController.class)
	public static Result gotoAllowanceOffer(int currentPage, int pageSize,
			String orderBy, long filter) {
		Logger.debug("goto gotoAllowanceOffer");
		Page<TAllowanceOffer> allData = TAllowanceOffer.findAllowanceOffer(
				currentPage, pageSize, orderBy, filter);
		return ok(views.html.allowanceoffer.render(allData, currentPage,
				pageSize, orderBy, filter));
	}
}
