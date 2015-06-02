package controllers;

import models.info.TOrder;
import models.info.TParkInfo;
import models.info.TParkInfoProd;
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
		return ok(views.html.parkingdetail.render(allData));
	}
	
	@Security.Authenticated(SecurityController.class)
	public static Result saveParkingData(){
		Logger.debug("goto saveParkingData");
		//DynamicForm dynamicForm = Form.form().bindFromRequest();
		Form<TParkInfo> form = Form.form(TParkInfo.class).bindFromRequest();
		if (form.hasErrors()) {
			
			JsonNode message = form.errorsAsJson();
			Logger.error("###########getglobalError:"+message);
			return ok(message);
		}
		TParkInfo parkinfo = form.get();
		if(parkinfo!=null){
			Logger.debug("###########get parkname:"+parkinfo.parkname);
			Logger.debug("###########get parkId:"+parkinfo.parkId);
			Logger.debug("###########get feeType:"+parkinfo.feeType);
			Logger.debug("###########get feeTypeSecInScopeHours:"+parkinfo.feeTypeSecInScopeHours);
		}
		
		return ok();
	}
}
