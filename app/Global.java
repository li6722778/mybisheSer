import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import controllers.LogController;
import controllers.PushController;
import models.info.TAllowance;
import models.info.TOrder;
import models.info.TParkInfo_adm;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import utils.DateHelper;

/**
 * server启动后的一些全局设置，目前实现 1. 定时任务，每凌晨3点钟删除未付款的订单，节约数据库空间
 * 
 * @author woderchen
 *
 */
public class Global extends GlobalSettings {

	public static TAllowance currentAllowance;

	@Override
	public void onStart(Application app) {
		Logger.info("Application has started");

		// 这里设置一个删除没有支付的订单的定期任务
		Akka.system().scheduler().schedule(Duration.create(-1, TimeUnit.MINUTES), // Initial
																					// delay
																					// 0
																					// milliseconds
				Duration.create(6, TimeUnit.HOURS), // Frequency 6 hour
				new Runnable() {
					public void run() {
						Logger.debug("#######AKKA schedule start>> delete unpay order task#########");
						// 删除6个小时内没有支付的订单
						Date objDate = DateHelper.currentDateAddWithType(Calendar.HOUR_OF_DAY, -6);
						String objString = DateHelper.format(objDate, "yyyy-MM-dd HH:mm:ss");
						TOrder.deleteUnpayOrder(objString);
						Logger.debug("#######AKKA schedule end>> done for deleting unpay order task" + "#########");

						LogController.info("auto task:delete unpay order <= " + objString, "autotask");
					}
				}, Akka.system().dispatcher());

		// 开始获取管理员的硬件id，为消息推送做好准备
		try {
			List<TParkInfo_adm> adms = TParkInfo_adm.getAllUserIdAndDeviceId();
			if (adms != null && adms.size() > 0) {
				for (TParkInfo_adm adm : adms) {
					PushController.clientMap.put(adm.userInfo.userid, adm.deviceid);
					Logger.debug("####add device to admin push map:" + adm.deviceid + " for user id:" + adm.userInfo.userid);
				}
			}
		} catch (Exception e) {
			Logger.error("global", e);
		}

	}

	@Override
	public void onStop(Application app) {
		Logger.info("Application shutdown...");
	}
	// @Override
	// public Result onBadRequest(String uri, String error) {
	// return badRequest("Don't try to hack the URI!");
	// }
	// @Override
	// public Result onError(Throwable t) {
	// return internalServerError(
	// views.html.errorPage(t)
	// );
	// }
}
