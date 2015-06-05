package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.info.TOrder;
import models.info.TParkInfo;
import models.info.TParkInfoPro_Loc;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Img;
import models.info.TParkInfo_Loc;
import models.info.TParkInfo_Py;
import models.info.TuserInfo;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.CommFindEntity;

import com.avaje.ebean.Page;
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
		
		Logger.debug("summary information,parkingProdCount->"+parkingProdCount+",orderCount->"+orderCount+",userCount->"+userCount+",paymentCount->"+paymentCount);

		flash("parkingProdCount",""+parkingProdCount);
		flash("orderCount",""+orderCount);
		flash("userCount",""+userCount);
		flash("paymentCount",""+paymentCount);
		
		return ok(views.html.dashboard.render());
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result gotoParking(int currentPage, int pageSize, String orderBy) {
		Logger.debug("goto gotoParking");
		Page<TParkInfo> allData = TParkInfo.page(currentPage,pageSize, orderBy);
		return ok(views.html.parking.render(allData,currentPage,pageSize,orderBy));
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result getTaskOfParking(int currentPage, int pageSize, String orderBy) {
		Logger.debug("goto getTaskOfParking");
		CommFindEntity<TParkInfo> allData = TParkInfo.findData(currentPage, pageSize, orderBy);
		List<TParkInfo> result = new ArrayList<TParkInfo>();
		if(allData!=null&&allData.getResult()!=null){
			result = allData.getResult();
		}

		String json = ParkController.gsonBuilderWithExpose.toJson(result);
		JsonNode jsonNode = Json.parse(json);
		return ok(jsonNode);
		
	}
	
	
	@Security.Authenticated(SecurityController.class)
	public static Result gotoParkingProd(int currentPage, int pageSize, String orderBy) {
		Logger.debug("goto gotoParking");
		Page<TParkInfoProd> allData = TParkInfoProd.page(currentPage,pageSize, orderBy);
		return ok(views.html.parkingprod.render(allData,currentPage,pageSize,orderBy));
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result gotoDetailParkingProd(long parking) {
		Logger.debug("goto gotoDetailParking");
		TParkInfoProd allData = TParkInfoProd.findDataById(parking);
		
		String makerString = "";
		if(allData!=null){
			List<TParkInfoPro_Loc> locAarray = allData.latLngArray;
			if(locAarray!=null){
				
				for(TParkInfoPro_Loc loc:locAarray){
					makerString +=loc.longitude+","+loc.latitude+"|";
				}
				
				if(makerString.length()>0){
					makerString = makerString.substring(0,makerString.length()-2);
				}
			}
		}
		
		flash("makerString",makerString);
		
		
		return ok(views.html.parkingdetailprod.render(allData));
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result gotoDetailParking(long parking) {
		Logger.debug("goto gotoDetailParking");
		TParkInfo allData = TParkInfo.findDataById(parking);
		
		String makerString = "";
		if(allData!=null){
			List<TParkInfo_Loc> locAarray = allData.latLngArray;
			if(locAarray!=null){
				
				for(TParkInfo_Loc loc:locAarray){
					makerString +=loc.longitude+","+loc.latitude+"|";
				}
				
				if(makerString.length()>0){
					makerString = makerString.substring(0,makerString.length()-2);
				}
			}
		}
		
		flash("makerString",makerString);
		
		
		return ok(views.html.parkingdetail.render(allData));
	}
	
	/**
	 * 删除多个数据
	 * @param parkingIdArray
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result deleteParking(String pidarray){
		Logger.info("GOTO deleteParking,FOR"+pidarray);
		if(pidarray!=null&&pidarray.length()>0){
		  String[] pids = pidarray.split(",");
		  for(String pidString:pids){
			  try{
				  long pid = Long.parseLong(pidString);
				  Logger.info("try to delete pid:"+pid);
				  TParkInfo.deleteData(pid);
			  }catch(Exception e){
				  Logger.error("deleteParking:"+pidString, e);
			  }
		  }
		  return ok(""+pids.length);
		}
		
		return ok("0");
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result approveParking(String pidarray){
		Logger.info("GOTO approveParking,FOR"+pidarray);
		if(pidarray!=null&&pidarray.length()>0){
		  String[] pids = pidarray.split(",");
		  for(String pidString:pids){
			  try{
				  long pid = Long.parseLong(pidString);
				  Logger.info("try to approve pid:"+pid);
				  ParkProdController.copyData(pid);
			  }catch(Exception e){
				  Logger.error("deleteParking:"+pidString, e);
			  }
		  }
		  return ok(""+pids.length);
		}
		
		return ok("0");
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result retrieveParking(String pidarray){
		Logger.info("GOTO retrieveParking,FOR"+pidarray);
		if(pidarray!=null&&pidarray.length()>0){
		  String[] pids = pidarray.split(",");
		  for(String pidString:pids){
			  try{
				  long pid = Long.parseLong(pidString);
				  Logger.info("try to copy2orin pid:"+pid);
				  ParkProdController.copyDataToOringal(pid);
			  }catch(Exception e){
				  Logger.error("deleteParking:"+pidString, e);
			  }
		  }
		  return ok(""+pids.length);
		}
		
		return ok("0");
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result saveParkingData(){
		Logger.debug("goto saveParkingData");
		//DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TParkInfo> form = Form.form(TParkInfo.class).bindFromRequest();
		if (form.hasErrors()) {
			JsonNode node= form.errorsAsJson();
			Logger.error("###########getglobalError:"+node);
			return badRequest(node.toString());
		}
		TParkInfo parkinfo = form.get();
		if(parkinfo!=null){
			Logger.debug("###########get parkId:"+parkinfo.parkId);
			TParkInfo.saveData(parkinfo);
		}
		
		return ok("提交成功.");
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result uploadImage(long parkingId){
		Logger.debug("goto uploadImage");
		try {
			UploadController.uploadToParking(parkingId);
			
			CommFindEntity<TParkInfo_Img> imageArray = TParkInfo_Img.findData(parkingId);
			List<TParkInfo_Img> array = new ArrayList<TParkInfo_Img>();
			if(imageArray!=null&&imageArray.getResult()!=null){
				array = imageArray.getResult();
			}
			String json = ParkController.gsonBuilderWithExpose.toJson(array);
			JsonNode jsonNode = Json.parse(json);
			return ok(jsonNode);
			
		} catch (IOException e) {
			Logger.error("uploadImage:",e);
			return badRequest();
		}

	}
	
	
	@Security.Authenticated(SecurityController.class)
	public static Result gotoUser(int currentPage, int pageSize, String orderBy) {
		Logger.debug("goto gotoParking");
		Page<TuserInfo> allData = TuserInfo.page(currentPage,pageSize, orderBy);
		return ok(views.html.users.render(allData,currentPage,pageSize,orderBy));
	}
	
	/**
	 * 
	 * @return
	 */
	@Security.Authenticated(SecurityController.class)
	public static Result gotoUserAdd() {
		Logger.debug("goto gotoUserAdd");
		return ok(views.html.useradd.render());
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result saveUserData(){
		Logger.debug("goto saveUserData");
		//DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TuserInfo> form = Form.form(TuserInfo.class).bindFromRequest();
		if (form.hasErrors()) {
			JsonNode node= form.errorsAsJson();
			Logger.error("###########getglobalError:"+node);
			return badRequest(node.toString());
		}
		TuserInfo info = form.get();
		if(info!=null){
			Logger.debug("###########get parkId:"+info.userName);
			TuserInfo.saveData(info);
		}
		
		return ok("提交成功.");
	}
}
