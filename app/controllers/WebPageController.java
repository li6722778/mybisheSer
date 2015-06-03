package controllers;

import java.util.List;

import models.info.TOrder;
import models.info.TParkInfo;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Loc;
import models.info.TParkInfo_Py;
import models.info.TuserInfo;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

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
}
