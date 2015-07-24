package controllers;

import models.info.TOptions;
import models.info.TParkInfo_adm;
import models.info.TVerifyCode;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Crypto;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.CommFindEntity;
import utils.RoleConstants;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserController extends Controller {
	public static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	/**
	 * 根据主键ID，得到数据
	 * @param userid
	 * @return
	 */
	@BasicAuth
	public static Result getDataById(Long userid) {
		Logger.info("start to get data");
		String json = gsonBuilderWithExpose.toJson(TuserInfo.findDataById(userid));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}

	@BasicAuth
	public static Result findDataByPhoneId(Long userphone) {
		Logger.info("start to get data");
		TuserInfo userinfo = TuserInfo.findDataByPhoneId(userphone);
		String json = gsonBuilderWithExpose.toJson(userinfo);
		
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}
	
	/**
	 * 得到所有的数据，这里是查询出所有的数据，如果有其他条件，需要仿照TuserInfo.findData写一些方法
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	@BasicAuth
	public static Result getAllData(int currentPage, int pageSize,
			String orderBy) {
		Logger.info("start to all data");
		CommFindEntity<TuserInfo> allData = TuserInfo.findData(currentPage,
				pageSize, orderBy);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}

	/**
	 * 新建或更新数据，ID不为空或大于0，则是更新数据；否则则是新建数据
	 * @return
	 */
	@BasicAuth
	public static Result saveData() {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		TuserInfo user = gsonBuilderWithExpose.fromJson(request, TuserInfo.class);
		ComResponse<TuserInfo>  response = new ComResponse<TuserInfo>();
		try {
			TuserInfo.saveData(user);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(user);
			response.setExtendResponseContext("更新数据成功.");
			LogController.info("save user data:"+user.userName);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	
	/**
	 * 更新密码
	 * @return
	 */
	@BasicAuth
	public static Result changePasswdData() {
		
		Logger.info("start to changePasswdData:");
		String request = request().body().asJson().toString();
		
		
		TuserInfo user = gsonBuilderWithExpose.fromJson(request, TuserInfo.class);
		ComResponse<TuserInfo>  response = new ComResponse<TuserInfo>();
		try {
			
			TuserInfo currentUser = TuserInfo.findDataById(user.userid);
			
			//user.updatePerson 这里只是借用一下这个字段存用户传过来的当前密码
			try{
			if(!Crypto.decryptAES(user.updatePerson).equals(currentUser.passwd)){
				throw new Exception("用户当前密码输入错误");
			}
			}catch(Exception e){
				if(!user.updatePerson.equals(currentUser.passwd)){
					throw new Exception("用户当前密码输入错误");
				}
			}
			
			user.passwd = Crypto.encryptAES(user.passwd);
			TuserInfo.saveData(user);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(user);
			response.setExtendResponseContext("更新数据成功.");
			LogController.info("save user data:"+user.userName);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	
	/**
	 * 注册用户，不需要权限认证
	 * @return
	 */
	public static Result regUser(String code) {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		
		
		TuserInfo user = gsonBuilderWithExpose.fromJson(request, TuserInfo.class);

		ComResponse<TuserInfo>  response = new ComResponse<TuserInfo>();
		try {
			
			//这里首先需要检查SMS verification
			TVerifyCode verficode =  TVerifyCode.getCode(user.userPhone);
			if(verficode==null||verficode.verifycode==null||!verficode.verifycode.trim().equals(code.trim())){
				throw new Exception("注册验证码有误,请重新获取");
			}else{
				//有效的情况下
				TVerifyCode.deletePhone(user.userPhone);
				//三分钟有效用存储过程来删除？？
				Logger.info("verified code successfully,delete verification code " + verficode.verifycode);
			}
			//检查完毕
			
			user.userType=RoleConstants.USER_TYPE_NORMAL;
			TuserInfo.saveData(user);
			user.passwd = Crypto.decryptAES(user.passwd);
			
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(user);
			response.setExtendResponseContext("用户注册成功.");
			
			//判断是否赠送优惠劵
			
			TOptions options  = TOptions.findOption(2);
			String  Counponcode = options.textObject;
			if(Counponcode!=null&&!(Counponcode.toString().trim().equals("")))
			{
				Counponcode=Counponcode.replace("，", ",");
				String[] counponcodes=Counponcode.split(",");	
			if(user!=null&&user.userid!=null&&counponcodes.length>0)
			{
				for(int i=0;i<counponcodes.length;i++){
			CounponController.getcounpon(counponcodes[i], user.userid);
				}
			}
			}
			
				
			
			
			
			LogController.info("register user data:"+user.userPhone);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	
	/**
	 * 更新用户权限
	 * @param id
	 * @return
	 */
	@BasicAuth
	public static Result updateRole(long id, int role){
		Logger.info("start to delete data:" + id);
		
		ComResponse<TuserInfo>  response = new ComResponse<TuserInfo>();
		try {
			TuserInfo userinfo = TuserInfo.updateRole(id, role);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(userinfo);
			response.setExtendResponseContext("用户权限更新成功.");
			LogController.info("update user data:"+userinfo.userPhone);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		
		return ok(json);
	}
	
	
	/**
	 * 根据ID删除数据，如果有其他条件，就需要仿照TuserInfo.deleteData，写类似的方法
	 * @param id
	 * @return
	 */
	@BasicAuth
	public static Result deleteData(long id){
		Logger.info("start to delete data:" + id);
		ComResponse<TuserInfo>  response = new ComResponse<TuserInfo>();
		try {
			TuserInfo.deleteData(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setExtendResponseContext("删除数据成功.");
			LogController.info("delete user data:"+id);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	
	@BasicAuth
	public static Result getAdmDataById(Long userid) {
		Logger.info("start to get data");
		String json = gsonBuilderWithExpose.toJson(TParkInfo_adm.findAdmPartInfo(userid));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}
	
	/**
	 * 请求验证码
	 * @return
	 */
	public static Result requestSMSVerify(Long phone){
		Logger.info("start to request SMS verification");
		//这里发送短信
		return ok("1");
	}
}
