package controllers;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;

import models.info.TOrder;
import models.info.TParkInfo;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Py;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.CommFindEntity;

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
}
