package controllers;

import models.info.TOrder;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Py;
import models.info.TuserInfo;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class DashboardController extends Controller {

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
}
