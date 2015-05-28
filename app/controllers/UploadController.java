package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import models.info.TParkInfo_Img;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.ComResponse;
import utils.ConfigHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UploadController extends Controller {
	public static Gson gsonBuilderWithExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	// 图片存储路径
	public static String image_store_path = ConfigHelper
			.getString("image.store.path");

	
	/**
	 * 删除图片并且也删除数据库中数据
	 * @param imgId
	 * @return
	 */
	public static Result deleteRemoteImage(Long imgId) {
		Logger.info("start to remote image:" + imgId);

		ComResponse<TParkInfo_Img> response = new ComResponse<TParkInfo_Img>();
		try {
			// 先找到
			TParkInfo_Img imgBean = TParkInfo_Img.findImgByID(imgId);

			if (imgBean != null) {
				// 然后去删除img
				String pathUri = imgBean.imgUrlPath;

				Logger.info(">>>>image.store.path:" + image_store_path);
				if (image_store_path == null || image_store_path.length() <= 0) {
					image_store_path = "/tmp";
				}

				File file = new File(image_store_path + pathUri);
				if (file.exists()) {
					Logger.info("delete existing file:"
							+ file.getCanonicalPath());
					file.delete();
				}
			}

			// 最后才删除数据库
			TParkInfo_Img.deleteData(imgId);

			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(imgBean);
			response.setExtendResponseContext("删除数据成功.");
			LogController.info("delete image imgid:"+imgId);
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
	 * 图片上传
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Result upload() throws IOException {
		MultipartFormData body = request().body().asMultipartFormData();

		Logger.info(">>>>image.store.path:" + image_store_path);
		if (image_store_path == null || image_store_path.length() <= 0) {
			image_store_path = "/tmp";
		}
		// 用户图片类型
		String imgType = ConfigHelper.getString("image.store.type");
		Logger.info(">>>>image.store.type:" + imgType);
		List<String> imgTypeList = new ArrayList<>();

		if (imgType != null && imgType.length() > 0) {
			String[] imgTypeArray = imgType.split(",");
			imgTypeList = Arrays.asList(imgTypeArray);
		}

		
		List<String> imagepaths = new ArrayList<String>();
		List<FilePart> pictures = body.getFiles();
		for (int i = 0; i < pictures.size(); i++) {
			FilePart picture = pictures.get(i);
			String fileName = picture.getFilename();
			Logger.debug(">>>>try to store the image:" + fileName);
			String imgtype = isExistType(imgTypeList, fileName);

			Calendar cal = Calendar.getInstance();// 使用日历类
			int year = cal.get(Calendar.YEAR);// 得到年
			int month = cal.get(Calendar.MONTH) + 1;// 得到月，因为从0开始的，所以要加1
			int day = cal.get(Calendar.DAY_OF_MONTH);// 得到天
			int hour = cal.get(Calendar.HOUR_OF_DAY);// 得到小时
			int minute = cal.get(Calendar.MINUTE);// 得到分钟
			int second = cal.get(Calendar.SECOND);// 得到秒
			int ms = cal.get(Calendar.MILLISECOND);// 得到毫秒
			// 返回给数据库的URI
			String fileUri = File.separator + year + File.separator + month
					+ File.separator + day + File.separator;
			if (imgtype != null) {
				// String contentType = picture.getContentType();
				File file = picture.getFile();

				StringBuilder _newFile = new StringBuilder(fileUri);
				_newFile.append(hour).append(minute).append(second).append(ms).append(i+1)
						.append(".").append(imgtype);
				// 相对路径
				String newFileName = image_store_path + _newFile.toString();

				Logger.info(">>>>rename [" + file.getCanonicalPath() + "] to ["
						+ newFileName + "]");

				// 存储在服务器上的绝对路径
				File remoteFile = new File(newFileName);
				if (!remoteFile.getParentFile().exists()) {
					Logger.debug(">>>>create path:"
							+ remoteFile.getParentFile().getCanonicalPath());
					remoteFile.getParentFile().mkdirs();
				}

				file.renameTo(remoteFile);
				Logger.info("restore done,new path:" + file.getAbsolutePath());
				imagepaths.add(_newFile.toString());
			} else {
				Logger.warn(">>>>image type is not allowed.");
			}

		}
		JsonNode json = Json.toJson(imagepaths);
		Logger.info("abb:" + json.toString());
		return ok(json.toString());

	}

	/**
	 * 判断当前图片类型是否接受
	 * 
	 * @param imgTypeList
	 * @param fileName
	 * @return
	 */
	private static String isExistType(List<String> imgTypeList, String fileName) {
		for (String type : imgTypeList) {
			if (fileName.toLowerCase().endsWith(type.toLowerCase())) {
				return type.toLowerCase();
			}
		}
		return null;
	}
}