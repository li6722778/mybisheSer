package actor;

import java.util.Date;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;

import actor.model.ScanCounponModel;
import akka.actor.UntypedActor;
import controllers.LogController;
import models.info.TCouponEntity;
import models.info.TCouponHis;
import models.info.TUseCouponEntity;
import models.info.TUseCouponHis;
import models.info.TuserInfo;
import play.Logger;

/**
 * 扫描优惠卷actor
 * @author woderchen
 *
 */
public class ScanCouponActor extends UntypedActor {

	private boolean insertCoupon(String counponcode, Long userid, int type) {
		TCouponEntity counponbean = TCouponEntity.findentityByCode(counponcode);
		TuserInfo useinfo;
		if (counponbean == null || (counponbean.count > 0 && counponbean.scancount >= counponbean.count)
				|| counponbean.isable == 0 || counponbean.isable == 2) {
			Logger.debug("not find TCouponEntity");
			if (counponbean != null && counponbean.scancount >= counponbean.count
					&& !TUseCouponEntity.findExistCouponByCouponId(counponbean.counponId)) {

				TCouponHis.moveToHis(counponbean);
			}
			return false;

		} else {
			Date startDate = counponbean.startDate;
			Date endDate = counponbean.endDate;
			Date currentDate = new Date();
			if (startDate != null) { // 如果还没有到
				if (startDate.after(currentDate)) {
					Logger.debug("not find coupon as start Date after current Date");
					return false;
				}

			} else {// 优惠券没有开始时间
				if (endDate != null) {// 失效了
					if (endDate.before(currentDate)) {
						if (!TUseCouponEntity.findExistCouponByCouponId(counponbean.counponId)) {
							TCouponHis.moveToHis(counponbean);
						}
						Logger.debug("not find coupon as end Date before current Date");
						return false;
					}
				}

			}

			if (endDate != null) {// 失效了
				if (endDate.before(currentDate)) {
					if (startDate.before(currentDate)) {
						if (!TUseCouponEntity.findExistCouponByCouponId(counponbean.counponId)) {
							TCouponHis.moveToHis(counponbean);
						}
						Logger.debug("not find coupon as end Date before current Date");
						return false;
					}
				}
			}

			if (type == ScanCounponModel.TYPE_DEFAULT){
				// 判断是否已经有优惠券了
				if (TUseCouponEntity.findExistCouponByUserIdAndId(counponbean.counponId, userid)
						|| TUseCouponHis.findExistCouponByUserIdAndId(counponbean.counponId, userid)) {
					Logger.debug("existing coupon!");
					return false;
				}
			}

			useinfo = TuserInfo.findDataById(userid);
			if (useinfo == null) {
				Logger.debug("not find useinfo");
				return false;
			} else {
				Ebean.execute(new TxRunnable() {
					public void run() {
						// 更新用户优惠表
						TUseCouponEntity databean = new TUseCouponEntity();
						databean.scanDate = new Date();
						databean.Id = null;
						databean.userInfo = useinfo;
						databean.counponentity = counponbean;
						databean.counponId = counponbean.counponId;
						databean.isable = 1;
						TUseCouponEntity.saveData(databean);
						// 更新优惠信息表
						counponbean.scancount = counponbean.scancount + 1;
						TCouponEntity.saveData(counponbean);
						LogController.info("save coupon data:" + counponbean.counponCode+" for "+userid);
					}
				});
				return true;

			}
		}
	}

	@Override
	public void onReceive(Object response) throws Exception {
		Logger.info("ScanCouponActor=>message:" + response);

		if (response instanceof ScanCounponModel) {
			ScanCounponModel message = (ScanCounponModel) response;

			String counponcode = message.counponcode;
			Long userid = message.userid;
			
			boolean flag = insertCoupon(counponcode,userid,message.type);
			//返回结果给发送者
			message.responseResult = flag?0:1;
			getSender().tell(message, getSelf());

		} else {
			unhandled(response);
		}

	}

}
