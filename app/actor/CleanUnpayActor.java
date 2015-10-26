package actor;
import java.util.Calendar;
import java.util.Date;

import akka.actor.UntypedActor;
import controllers.LogController;
import models.info.TOrder;
import play.Logger;
import utils.DateHelper;

public class CleanUnpayActor extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		Logger.debug("#######AKKA schedule start>> delete unpay order task#########");
		//删除6个小时内没有支付的订单
		Date objDate = DateHelper.currentDateAddWithType(Calendar.HOUR_OF_DAY, -6);
		String objString = DateHelper.format(objDate, "yyyy-MM-dd HH:mm:ss");
		TOrder.deleteUnpayOrder(objString);
		Logger.debug("#######AKKA schedule end>> done for deleting unpay order task"+ "#########");
		
		LogController.info("auto task:delete unpay order <= "+objString,"autotask");

	}

}
