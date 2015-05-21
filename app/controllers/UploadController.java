package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.ConfigHelper;

import com.fasterxml.jackson.databind.JsonNode;

public class UploadController extends Controller{
	
	
	public static Result upload() throws IOException {  
        MultipartFormData body = request().body().asMultipartFormData();  
        
        String storePath = ConfigHelper.getString("image.store.path");
        
        List<String> imagepaths=new  ArrayList<String>();
        List<FilePart> pictures=body.getFiles();
        for(FilePart picture: pictures)
        {
            String fileName = picture.getFilename();  
            if(fileName.toLowerCase().endsWith("jpeg"))
              {
                 Logger.info("aaaaaaaa:" + fileName);
                 String contentType = picture.getContentType();   
                 File file = picture.getFile();  
                 File file1=new File("c:/text/"+fileName);
                 Logger.info("bbbb:" + file.getCanonicalPath());
                 file.renameTo(file1); 
                 Logger.info("aaa:" + file.getAbsolutePath());
                 imagepaths.add("c:/text/"+fileName);
              }
       
        }  
        JsonNode json = Json.toJson(imagepaths);
        Logger.info("abb:" + json.toString());
        return ok(json.toString());
           
       
      }  
}