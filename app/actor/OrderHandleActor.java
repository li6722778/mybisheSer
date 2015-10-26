package actor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;

import actor.model.OrderMovingObject;
import akka.actor.UntypedActor;
import controllers.PushController;
import models.info.TAllowanceOffer;
import models.info.TIncome;
import models.info.TOrder;
import models.info.TOrderHis;
import models.info.TOrderHis_Py;
import models.info.TOrder_Py;
import models.info.TParkInfoProd;
import models.info.TUseCouponEntity;
import models.info.TuserInfo;
import net.sf.cglib.beans.BeanCopier;
import play.Logger;
import utils.Arith;
import utils.Constants;

/**
 * 负责处理订单
 * @author woderchen
 *
 */
public class OrderHandleActor extends UntypedActor {
	private BeanCopier copier = BeanCopier.create(TOrder.class,
			TOrderHis.class, false);
	private  BeanCopier copierPy = BeanCopier.create(TOrder_Py.class,
			TOrderHis_Py.class, false);
	
	@Override
	public void onReceive(Object response) throws Exception {
		Logger.info("SMSActor=>message:"+response);
		 if (response instanceof OrderMovingObject) {
			 
			 OrderMovingObject orderMove = (OrderMovingObject)response;
			 
			 Long orderId = orderMove.getOrderId();
			 int status = orderMove.getStatus();
			 
				Logger.info("move to history table");
				final TOrder order = TOrder.findDataById(orderId);
				Ebean.execute(new TxRunnable() {
					public void run() {
						
						if(order!=null){
							Logger.debug(">>>>order moving start");
							
							order.endDate = new Date();
							order.orderStatus = status;
							
							TOrderHis orderHis = new TOrderHis();
							copier.copy(order, orderHis, null);
							
							List<TOrderHis_Py> pyArray = new ArrayList<TOrderHis_Py>();
							orderHis.pay = pyArray;
							
							if(order.pay!=null){
								Logger.debug(">>>>>>>>>>>TOrder_Py moving start");
								for(TOrder_Py py:order.pay){
									TOrderHis_Py hisPy = new TOrderHis_Py();
									copierPy.copy(py, hisPy, null);
									hisPy.order = orderHis;
									pyArray.add(hisPy);
									if(hisPy.ackStatus == Constants.PAYMENT_STATUS_FINISH){
										
										double cash = 0;
										if(hisPy.payMethod==Constants.PAYMENT_TYPE_CASH)
										{
											cash=hisPy.payActu;
											
										}
									
										Logger.debug(">>>>>>>>>>cash:"+cash+"#######used coupon:"+hisPy.couponUsed);
										
									   TIncome.saveIncome(order.parkInfo.parkId, Arith.decimalPrice(hisPy.payActu+hisPy.couponUsed),cash,hisPy.couponUsed);
									}
								}
								Logger.debug(">>>>>>>>>>>TOrder_Py moving end");
							}
							TOrderHis.saveDataWithoutIDPolicy(orderHis);
							Logger.debug(">>>>order moving end");

							if(order.couponId>0){ //不能删除优惠劵，只是标记为使用
								
							    TUseCouponEntity.setUseCoupon(order.couponId, order.userInfo.userid);
							    Logger.debug(">>>>delete used coupon");
							}
							
							//看下是不是需要发放补贴
							TAllowanceOffer.offerAllowance(orderHis);
							
							TOrder.deleteData(orderId);
						}
						
					}
				});
				
				
				if(order!=null){
					TParkInfoProd parkInfo = order.parkInfo;
					if(parkInfo!=null){
						
						TuserInfo user = order.userInfo;
						String phone = "";
						if(user!=null){
							phone = ""+user.userPhone;
						}else{
							phone = "订单号:"+order.orderId+" ";
						}
						
				       //发送通知
				       PushController.pushToParkAdminForOut(parkInfo.parkId, phone, parkInfo.parkname,order.orderId);
					}
				}
			 
		 }else{
			 unhandled(response);
		 }

	}

}
