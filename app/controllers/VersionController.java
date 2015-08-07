package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import models.info.TOrder;
import models.info.TVersion;
import play.Logger;
import play.cache.Cached;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import utils.ComResponse;
import utils.ConfigHelper;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VersionController extends Controller {
	public static Gson gsonBuilderWithExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	// 图片存储路径
	public static String image_store_guide_path = ConfigHelper
			.getString("image.store.guide.path");

	/**
	 * 版本
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static Result getVersion() {
		Logger.info("start to get all data");
		String json = gsonBuilderWithExpose.toJson(TVersion.findVersion());
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}

	/**
	 * 新建或更新数据，ID不为空或大于0，则是更新数据；否则则是新建数据
	 * 
	 * @return
	 */
	@BasicAuth
	public static Result saveData() {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);

		TVersion data = gsonBuilderWithExpose.fromJson(request, TVersion.class);
		ComResponse<TVersion> response = new ComResponse<TVersion>();
		try {
			TVersion.saveData(data);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(data);
			response.setExtendResponseContext("更新数据成功.");
			LogController.info("save version:" + data.version);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
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
	public static boolean upload() throws IOException {
		List<String> imagepaths = getUploadNode();
		JsonNode json = Json.toJson(imagepaths);
		Logger.info("abb:" + json.toString());

		if (imagepaths != null && imagepaths.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	private static List<String> getUploadNode() throws IOException {
		MultipartFormData body = request().body().asMultipartFormData();
		Logger.info(">>>>image_store_guide_path:" + image_store_guide_path);
		if (image_store_guide_path == null
				|| image_store_guide_path.length() <= 0) {
			image_store_guide_path = "/temp/guide";
		}
		
		//清空文件夹
		
	        deleteFile(new File(image_store_guide_path));
		
		
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

			if (imgtype != null) {
				// String contentType = picture.getContentType();
				File file = picture.getFile();
				StringBuilder _newFile = new StringBuilder("g");
				_newFile.append(i + 1).append(".").append(imgtype);
				// 相对路径
				String newFileName = image_store_guide_path
						+ _newFile.toString();

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
		return imagepaths;
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
	
	
    /**
     * 删除文件夹下的所有文件
     * @param oldPath
     */
    public static void deleteFile(File oldPath) {
          if (oldPath.isDirectory()) {
           File[] files = oldPath.listFiles();
           for (File file : files) {
             deleteFile(file);
           }
          }else{
            oldPath.delete();
          }
        }
    
    public static List<String> showAllFiles(File dir) throws Exception{
    	  File[] fs = dir.listFiles();
     	  List<String> filepath = new ArrayList<String>();
     	  filepath.clear();
    	  for(int i=0; i<fs.length; i++){
    	   System.out.println(fs[i].getAbsolutePath());
    	   filepath.add(i, fs[i].getAbsolutePath());
    	   if(fs[i].isDirectory()){
    	    try{
    	     showAllFiles(fs[i]);
    	    }catch(Exception e){}
    	   }
    	  }
    	  
    	  return  filepath;
    	  
    	 }
    
    
	/**
	 * 跳转版本界面
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result getgudicepic() {
		Logger.debug("goto gotoVersion");
		if (image_store_guide_path == null
				|| image_store_guide_path.length() <= 0) {
			image_store_guide_path = "/temp/guide";
		}
		List<String> path = new ArrayList<String>();
		File root = new File(image_store_guide_path);
		try {
			 path = VersionController.showAllFiles(root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			root=null;
		}
		
		String json = gsonBuilderWithExpose.toJson(path);
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	
	}
    
    
    	

}
