package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.info.TParkInfo;
import models.info.TParkInfoPro_Img;
import models.info.TParkInfoPro_Loc;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Img;
import models.info.TParkInfo_Loc;
import net.sf.cglib.beans.BeanCopier;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.ComResponse;
import utils.CommFindEntity;
import utils.ZXingUtil;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 停车场相关
 * 
 * @author woderchen
 *
 */
public class ParkProdController extends Controller {
	public static Gson gsonBuilderWithExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static BeanCopier copier = BeanCopier.create(TParkInfo.class,
			TParkInfoProd.class, false);
	public static BeanCopier copierImg = BeanCopier.create(TParkInfo_Img.class,
			TParkInfoPro_Img.class, false);
	public static BeanCopier copierLoc = BeanCopier.create(TParkInfo_Loc.class,
			TParkInfoPro_Loc.class, false);

	public static BeanCopier copierOrin = BeanCopier.create(TParkInfoProd.class,
			TParkInfo.class, false);
	public static BeanCopier copierImgOrin = BeanCopier.create(TParkInfoPro_Img.class,
			TParkInfo_Img.class, false);
	public static BeanCopier copierLocOrin = BeanCopier.create(TParkInfoPro_Loc.class,
			TParkInfo_Loc.class, false);
	
	@BasicAuth
	public static Result getDataById(Long id) {
		Logger.info("start to get data");

		String json = gsonBuilderWithExpose.toJson(TParkInfoProd
				.findDataById(id));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}

	@BasicAuth
	public static Result getAllData(int currentPage, int pageSize,
			String orderBy) {
		Logger.info("start to get all data");
		CommFindEntity<TParkInfoProd> allData = TParkInfoProd.findData(
				currentPage, pageSize, orderBy);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}

	@BasicAuth
	public static Result getAllDataByUser(int currentPage, int pageSize,
			String orderBy, long user) {
		Logger.info("start to get all data by user");
		CommFindEntity<TParkInfoProd> allData = TParkInfoProd.findData(
				currentPage, pageSize, orderBy, user);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}

	@BasicAuth
	public static Result saveData() {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);

		TParkInfoProd info = gsonBuilderWithExpose.fromJson(request,
				TParkInfoProd.class);
		ComResponse<TParkInfoProd> response = new ComResponse<TParkInfoProd>();
		try {
			TParkInfoProd.saveData(info);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(info);
			response.setExtendResponseContext("更新数据成功.");
			LogController.info("save prod park data:"+info.parkname);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String json = gsonBuilderWithExpose.toJson(response);
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("ComResponse result:" + json);
		return ok(jsonNode);
	}

	@Security.Authenticated(SecurityController.class)
	public static Result copyData(Long parkId) {
		Logger.info("start to copy data based on approved:" + parkId);

		ComResponse<TParkInfoProd> response = new ComResponse<TParkInfoProd>();
		try {
			// 从未审批表中取出数据
			TParkInfo parkInfo = TParkInfo.findDataById(parkId);

			// copy到已经审批表中
			TParkInfoProd parkProdInfo = new TParkInfoProd();
			Logger.debug(">>>>park bean copy start");
			copier.copy(parkInfo, parkProdInfo, null);
			Logger.debug(">>>>park bean copy end");
			List<TParkInfoPro_Img> imgArray = new ArrayList<TParkInfoPro_Img>();
			List<TParkInfoPro_Loc> locArray = new ArrayList<TParkInfoPro_Loc>();
			parkProdInfo.imgUrlArray = imgArray;
			parkProdInfo.latLngArray = locArray;
			Logger.debug(">>>>img bean copy start");
			for (TParkInfo_Img oldImgArray : parkInfo.imgUrlArray) {
				TParkInfoPro_Img img = new TParkInfoPro_Img();
				copierImg.copy(oldImgArray, img, null);
				imgArray.add(img);
			}
			Logger.debug(">>>>img bean copy end");
			Logger.debug(">>>>location bean copy start");
			for (TParkInfo_Loc oldLocArray : parkInfo.latLngArray) {
				TParkInfoPro_Loc loc = new TParkInfoPro_Loc();
				copierLoc.copy(oldLocArray, loc, null);
				locArray.add(loc);
			}
			Logger.debug(">>>>location bean copy end");

			parkProdInfo.approveDate = new Date();
			parkProdInfo.approvePerson = session("username");
			TParkInfoProd.approveDataWithoutIDPolicy(parkProdInfo);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(parkProdInfo);
			response.setExtendResponseContext("更新数据成功.");
			LogController.info("approved. copy data from submitted park:"+parkInfo.parkname);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String json = gsonBuilderWithExpose.toJson(response);
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("ComResponse result:" + json);
		return ok(jsonNode);
	}

	@Security.Authenticated(SecurityController.class)
	public static Result copyDataToOringal(Long parkProdId) {
		Logger.info("start to copy data to oringal creator,park prod id:" + parkProdId);

		ComResponse<TParkInfo> response = new ComResponse<TParkInfo>();
		try {
			// 从正式表中取出数据
			TParkInfoProd parkProdInfo = TParkInfoProd.findDataById(parkProdId);

			// copy到已经审批表中
			TParkInfo parkInfo = new TParkInfo();
			Logger.debug(">>>>park bean copy start");
			copierOrin.copy(parkProdInfo,parkInfo, null);
			Logger.debug(">>>>park bean copy end");
			List<TParkInfo_Img> imgArray = new ArrayList<TParkInfo_Img>();
			List<TParkInfo_Loc> locArray = new ArrayList<TParkInfo_Loc>();
			parkInfo.imgUrlArray = imgArray;
			parkInfo.latLngArray = locArray;
			Logger.debug(">>>>img bean copy start");
			for (TParkInfoPro_Img prodImgArray : parkProdInfo.imgUrlArray) {
				TParkInfo_Img img = new TParkInfo_Img();
				copierImgOrin.copy(prodImgArray, img, null);
				imgArray.add(img);
			}
			Logger.debug(">>>>img bean copy end");
			Logger.debug(">>>>location bean copy start");
			for (TParkInfoPro_Loc prodLocArray : parkProdInfo.latLngArray) {
				TParkInfo_Loc loc = new TParkInfo_Loc();
				copierLocOrin.copy(prodLocArray,loc, null);
				locArray.add(loc);
			}
			Logger.debug(">>>>location bean copy end");

			parkInfo.updateDate=new Date();
			parkInfo.updatePerson=session("username");
			
			TParkInfo.retrieveDataWithoutIDPolicy(parkInfo);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(parkInfo);
			response.setExtendResponseContext("回退数据成功.");
			LogController.info("retrieve submitted park data from prod park:"+parkInfo.parkname);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String json = gsonBuilderWithExpose.toJson(response);
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("ComResponse result:" + json);
		return ok(jsonNode);
	}
	
	@BasicAuth
	public static Result deleteData(Long id) {
		Logger.info("start to delete data:" + id);
		ComResponse<TParkInfoProd> response = new ComResponse<TParkInfoProd>();
		try {
			TParkInfoProd.deleteData(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setExtendResponseContext("删除数据成功.");
			LogController.info("delete prod parking data:"+id);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}

	/**
	 * 得到位置的停车信息
	 * 
	 * @param parkId
	 * @param type
	 * @param status
	 * @return
	 */
	public static Result getLocationDataById(Long parkId, int type, int status) {
		Logger.info("start to get data for getLocationDataById");

		CommFindEntity<TParkInfoPro_Loc> allData = TParkInfoPro_Loc.findData(
				parkId, type, status);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);

		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}

	/**
	 * 得到相关的图片信息
	 * 
	 * @param parkId
	 * @param type
	 * @param status
	 * @return
	 */
	public static Result getImgDataById(Long parkId) {
		Logger.info("start to get data for getImgDataById");

		CommFindEntity<TParkInfoPro_Img> allData = TParkInfoPro_Img
				.findData(parkId);

		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);

		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}
	
	public static Result disQRImage(String content) {
		Logger.info("start to disQRImage");

		if(ZXingUtil.encodeQRCodeImage(content, null, "", 315, 315, null)){
			
		}
		
		
		return ok("");
	}

}
