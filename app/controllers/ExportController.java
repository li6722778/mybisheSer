package controllers;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.ExportBean;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateHelper;
/**
 * 导出数据类
 * @author woderchen
 *
 */
public class ExportController extends Controller {

	public static String path_backup = "/Users/woderchen/Desktop/Project/chebole/test";
	public static String path_script = "/Users/woderchen/Desktop/Project/chebole/start.exportuser.sh";

	/**
	 * 得到当前备份的用户表所有数据
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result getBackupList(String typeName){
		Logger.info("start to get Backup User List");
		File exportPathDir = new File(path_backup);
		File[] csvFiles = exportPathDir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(typeName);
			}
		});
		
		List<ExportBean> exportArray = new ArrayList<ExportBean>();
		
		//检查文件名
		if (csvFiles!=null&&csvFiles.length>0){
			Logger.info("fetch file list size:"+csvFiles.length);
			for (int i=0;i<csvFiles.length;i++){
				ExportBean export = new ExportBean();
				export.fileName = csvFiles[i].getName();
				try{
				export.createDate = DateHelper.formatTime(new Date(csvFiles[i].lastModified()));
			    }catch(Exception e){
			    	Logger.error("transfer to create date:"+e.getMessage());
			    }
				export.fileSize = Math.round(csvFiles[i].length()/1024);
				
				exportArray.add(export);
			}
			
		}else{
			Logger.info("fetch file list size, return null");
		}
		
		String json = UserController.gsonBuilderWithExpose.toJson(exportArray);
		JsonNode jsonNode = Json.parse(json);
		//Logger.debug("json:"+json);
		return ok(jsonNode);
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result downloaduser(String file) {
		Logger.info("try to download file:" +file);
		String filePath = path_backup+File.separator+file;
		File targetFile = new File(filePath);
		
		if (file==null||file.trim().equals("")||!targetFile.exists() || targetFile.isDirectory()){
			return ok(file+" 文件不存在");
		}
		
		response().setContentType("application/x-download");
		response().setHeader("Content-disposition",
				"attachment; filename=" +file);
		return ok(targetFile);
	}
	
    public static int executeShell(String shellCommand) throws IOException {  
        System.out.println("shellCommand:"+shellCommand);  
        int success = 0;  
        StringBuffer stringBuffer = new StringBuffer();  
        BufferedReader bufferedReader = null;  
        // 格式化日期时间，记录日志时使用  
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS ");  
        try {  
            stringBuffer.append(dateFormat.format(new Date()))  
                    .append("准备执行Shell命令 ").append(shellCommand)  
                    .append(" \r\n");  
            Process pid = null;  
            String[] cmd = { "/bin/sh", "-c", shellCommand };  
            //给shell传递参数  
            //String[] cmd = { "/bin/sh", "-c", shellCommand+" paramater" };  
            // 执行Shell命令  
            pid = Runtime.getRuntime().exec(cmd);  
            if (pid != null) {  
                stringBuffer.append("进程号：").append(pid.toString())  
                        .append("\r\n");  
                // bufferedReader用于读取Shell的输出内容  
                bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024);  
                pid.waitFor();  
            } else {  
                stringBuffer.append("没有pid\r\n");  
            }  
            stringBuffer.append(dateFormat.format(new Date())).append(  
                    "Shell命令执行完毕\r\n执行结果为：\r\n");  
            String line = null;  
            // 读取Shell的输出内容，并添加到stringBuffer中  
            while (bufferedReader != null  
                    && (line = bufferedReader.readLine()) != null) {  
                stringBuffer.append(line).append("\r\n");  
            }  
            System.out.println("stringBuffer:"+stringBuffer);  
        } catch (Exception ioe) {  
            stringBuffer.append("执行Shell命令时发生异常：\r\n").append(ioe.getMessage())  
                    .append("\r\n");  
        } finally {  
            if (bufferedReader != null) {    
                try {  
                    bufferedReader.close();  
                    // 将Shell的执行情况输出到日志文件中  
                    System.out.println("stringBuffer.toString():"+stringBuffer.toString());  
                } catch (Exception e) {  
                    e.printStackTrace();  
                } 
            }  
            success = 1;  
        }  
        return success;  
    }  
	
	//执行脚本进行备份
	@Security.Authenticated(SecurityController.class)
	public static Result startToUserBackup(int fullbackup,int asc){
		Logger.info("startToUserBackup,fullbackup=>" +fullbackup+",asc=>"+asc);
		//执行脚本进行备份
		try {
			String command = path_script+" user";
			
			if (fullbackup!=0){//全部备份
				command += " false";
			}else{
				command += " true";
			}
			
			if (asc!=0){//全部备份
				command += " false";
			}else{
				command += " true";
			}
//			String[] cmd = { "/bin/sh", "-c", command };  
//			Process p = Runtime.getRuntime().exec(cmd);
			int result = executeShell(command);
			Logger.info("script done=>" +result+",command:"+command);
		} catch (Exception e) {
			Logger.error("startToBackup",e);
			return ok("错误:"+e.getMessage());
		}
		
		return ok("任务完成");
	}
	
	//执行脚本进行备份
	@Security.Authenticated(SecurityController.class)
	public static Result startToOrderBackup(int fullbackup,int asc){
		Logger.info("startToOrderBackup,fullbackup=>" +fullbackup+",asc=>"+asc);
		//执行脚本进行备份
		try {
			String command = path_script+" order ";
			
			if (fullbackup!=0){//全部备份
				command += " false ";
			}else{
				command += " true ";
			}
			
			if (asc!=0){//全部备份
				command += " false ";
			}else{
				command += " true ";
			}
			
			Process p = Runtime.getRuntime().exec(command);
			int result = p.waitFor();
			Logger.info("script done=>" +result+",command:"+command);
		} catch (Exception e) {
			Logger.error("startToBackup",e);
			return ok("错误:"+e.getMessage());
		}
		
		return ok("任务完成");
	}
	
	//执行脚本进行备份
	@Security.Authenticated(SecurityController.class)
	public static Result startToIncomeBackup(int fullbackup,int asc){
		Logger.info("startToIncomeBackup,fullbackup=>" +fullbackup+",asc=>"+asc);
		//执行脚本进行备份
		try {
			String command = path_script+" income ";
			
			if (fullbackup!=0){//全部备份
				command += " false ";
			}else{
				command += " true ";
			}
			
			if (asc!=0){//全部备份
				command += " false ";
			}else{
				command += " true ";
			}
			
			Process p = Runtime.getRuntime().exec(command);
			int result = p.waitFor();
			Logger.info("script done=>" +result+",command:"+command);
		} catch (Exception e) {
			Logger.error("startToBackup",e);
			return ok("错误:"+e.getMessage());
		}
		
		return ok("任务完成");
	}
}
