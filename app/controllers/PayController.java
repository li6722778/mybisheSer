package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import models.ChebolePayOptions;
import models.info.TCouponEntity;
import models.info.TOrder;
import models.info.TOrderHis;
import models.info.TOrder_Py;
import models.info.TParkInfoProd;
import models.info.TUseCouponEntity;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import utils.Arith;
import utils.ComResponse;
import utils.ConfigHelper;
import utils.Constants;
import utils.DateHelper;
import action.BasicAuth;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 付款
 * 
 * @author woderchen
 *
 */
public class PayController extends Controller {
	public static Gson gsonBuilderWithExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	// 商户PID
	public static final String PARTNER = ConfigHelper
			.getString("bank.ailipay.pid");
	// 商户收款账号
	public static final String SELLER = ConfigHelper
			.getString("bank.ailipay.account");
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = ConfigHelper
			.getString("bank.ailipay.rsaprivate");
	// 支付宝公钥
	public static final String RSA_PUBLIC = ConfigHelper
			.getString("bank.ailipay.rsapublic");

	// 支付完成后的回调
	public static final String NOTIFY_AILIPAY = ConfigHelper
			.getString("bank.ailipay.notify");

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private static String sign(String content) {
		Logger.debug("RSA_PRIVATE", RSA_PRIVATE);
		return sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private static String getSignType() {
		return "sign_type=\"RSA\"";
	}

	private static final String ALGORITHM = "RSA";

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static String sign(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					org.apache.commons.codec.binary.Base64
							.decodeBase64(privateKey));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

			return org.apache.commons.codec.binary.Base64
					.encodeBase64String(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private static String getOrderInfo(TOrder Torder, long payId, double price) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + payId + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + Torder.orderName + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + Torder.orderDetail + ",coupon:"
				+ Torder.couponId + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + NOTIFY_AILIPAY + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * 第一次付款。 计次收费原则上只有一次付款 分段收费有二次付款
	 * 
	 * @param parkingProdId
	 * @return
	 */
	@BasicAuth
	public static Result payForIn(long parkingProdId, String city,
			double latitude, double longitude,int userSelectedpayWay) {
		Logger.info("start to generator order and generator aili pay for:"
				+ parkingProdId + ",city:" + city);
		String request = request().body().asJson().toString();
		Logger.debug("start to post data:" + request);

		ChebolePayOptions chebolePayOptions = gsonBuilderWithExpose.fromJson(
				request, ChebolePayOptions.class);

		ComResponse<ChebolePayOptions> response = new ComResponse<ChebolePayOptions>();
		try {

			if (parkingProdId > 0) {
				TParkInfoProd infoPark = TParkInfoProd
						.findDataById(parkingProdId);
				String useridString = flash("userid");
				TuserInfo user = null;
				try {
					user = TuserInfo.findDataById(Long.parseLong(useridString));
				} catch (Exception e) {
					Logger.error("", e);
					throw new Exception("获取当前操作用户失败");
				}
				if (infoPark != null && user != null) {
					// 开始组合订单

					// 首先判断一下是否需要新建订单
					TOrder dataBean = null;
					// 付款单
					TOrder_Py payment = null;
					if (chebolePayOptions.order != null
							&& chebolePayOptions.order.orderId != null
							&& chebolePayOptions.order.orderId > 0) { // 该订单已经存在
						dataBean = TOrder
								.findDataById(chebolePayOptions.order.orderId);
						Logger.debug("订单已经存在");
						if (dataBean.orderStatus == Constants.ORDER_TYPE_FINISH) {// 订单已经完成了
							throw new Exception("订单已经完成，不能再次付款");
						}
						// 查看当前状态下是否有付款单
						if (dataBean.pay != null) {
							// 付款单
							long payID = chebolePayOptions.paymentId;
							payment = TOrder_Py.findDataById(payID);
							if (payment != null) {

								if (payment.ackStatus == Constants.PAYMENT_STATUS_FINISH) {
									throw new Exception("该订单付款单已经支付完成");
								}
								if (payment.ackStatus == Constants.PAYMENT_STATUS_PENDING) {
									throw new Exception("付款单正在等待支付接口响应,请不要重复付款");
								}
								Logger.debug("订单中的付款单已经存在");
							} else {
								// 指定的付款单是空的，就从订单中看看是否有之前没有付款成功的订单
								List<TOrder_Py> oldplays = dataBean.pay;
								if (oldplays != null && oldplays.size() > 0) {
									for (TOrder_Py pay : oldplays) {
										if (pay.ackStatus == Constants.PAYMENT_STATUS_START
												|| pay.ackStatus == Constants.PAYMENT_STATUS_EXCPTION) {
											payment = pay;
											break;
										}
									}
								}
							}

						}

					}
					if (dataBean == null) {
						dataBean = new TOrder(); // 生成一个新的订单
						dataBean.latitude = latitude;
						dataBean.longitude = longitude;
					}
					if (payment == null) {
						payment = new TOrder_Py();
					}

					int payWay = Constants.PAYMENT_TYPE_ZFB;

					if (chebolePayOptions.useCounpon) {
						if (chebolePayOptions.payActualPrice <= 0) { // 如果小于0，肯定是优惠券就够用了
							payWay = Constants.PAYMENT_COUPON;
						} else {
							payWay = Constants.PAYMENT_COUPON
									+ Constants.PAYMENT_TYPE_ZFB;
						}
					} else {
						if (chebolePayOptions.payActualPrice <= 0) { // 如果小于0，肯定是搞活动不要钱了
							payWay = Constants.PAYMENT_DISCOUNT;
						}
					}
					
					

					Date currentDate = new Date();
					dataBean.orderCity = city;
					dataBean.orderDate = currentDate;
					dataBean.orderName = "停车费:" + infoPark.parkname;
					dataBean.orderStatus = Constants.ORDER_TYPE_START;
					dataBean.orderFeeType = infoPark.feeType; 
					dataBean.parkInfo = infoPark;
					dataBean.userInfo = user;
					dataBean.couponId = chebolePayOptions.counponId;
					dataBean.orderDetail = infoPark.detail;

					
					
					payment.payActu = chebolePayOptions.payActualPrice;
					payment.payMethod = payWay;
					payment.payTotal = chebolePayOptions.payOrginalPrice;

					payment.ackStatus = Constants.PAYMENT_STATUS_START;
					payment.ackDate = null;

					payment.createPerson = user.userName;
					payment.payDate = currentDate;

					payment.couponUsed = chebolePayOptions.counponUsedMoney;

					List<TOrder_Py> pays = new ArrayList<TOrder_Py>();
					pays.add(payment);

					dataBean.pay = pays;

					
					// ***************组合订单完毕******************

					TOrder.saveData(dataBean);
				
					Logger.debug("本次实际付款金额:" + payment.payActu + "元,orderId:"
							+ dataBean.orderId + ",payment id:"
							+ payment.parkPyId);

					/*********************************************************************
					 * 这里生成aili pay String
					 ***********************************************************************/
					// 订单
					String orderInfo = getOrderInfo(dataBean, payment.parkPyId,
							payment.payActu);
					Logger.debug(" ******ali pay ******order info:" + orderInfo);
					// 对订单做RSA 签名
					String sign = sign(orderInfo);
					Logger.debug(" ******ali pay ******sign:" + sign);
					try {
						// 仅需对sign 做URL编码
						sign = URLEncoder.encode(sign, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						Logger.error("generatorOrderStringAndGetPayInfo", e);
					}
					// 完整的符合支付宝参数规范的订单信息
					final String payInfo = orderInfo + "&sign=\"" + sign
							+ "\"&" + getSignType();

					Logger.debug(" ******ali pay ******payInfo:" + payInfo);

					chebolePayOptions.payInfo = payInfo;
					chebolePayOptions.order = dataBean;
					chebolePayOptions.paymentId = payment.parkPyId;

					/*********************************************************************
					 * 生成完毕
					 ***********************************************************************/
					response.setResponseStatus(ComResponse.STATUS_OK);
					response.setResponseEntity(chebolePayOptions);
					response.setExtendResponseContext("订单数据生成成功，并且返回支付串.");
					LogController
							.info("generated order and payment successfully:"
									+ dataBean.orderName + ",from "
									+ user.userPhone + ",bill:"
									+ chebolePayOptions.payActualPrice);

					if (chebolePayOptions.payActualPrice <= 0) {
						response.setExtendResponseContext("pass");
						throw new Exception("当前您无需付费,请前往停车场扫码进场");
					}

				} else {
					throw new Exception("服务端数据不完整不能生成订单.");
				}

			} else {
				throw new Exception("无效的停车场ID:" + parkingProdId);
			}
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setResponseEntity(chebolePayOptions);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);

		return ok(json);
	}

	/**
	 * 第二次付款
	 * 
	 * @param orderId
	 * @return
	 */
	@BasicAuth
	public static Result payForOut(long orderId, String scanResult,int payway) {

		Logger.info("pay for out, order id:" + orderId);

		ComResponse<ChebolePayOptions> response = new ComResponse<ChebolePayOptions>();
		TOrder order = TOrder.findDataById(orderId);
		ChebolePayOptions payOption = new ChebolePayOptions();
		
		try {

			if (order == null) {
				throw new Exception("系统无法找到该订单:" + orderId);
			}

			TParkInfoProd parkinfo = order.parkInfo;

			long parkIdFromScan = ScanController.decodeScan(scanResult);

			if (parkinfo == null) {
				throw new Exception("该订单无有效停车场");
			} else if (parkinfo.parkId != parkIdFromScan) {
				throw new Exception("该订单不属于此停车场，请检查");
			}

			// 剩下优惠卷的钱
			double canbeUsedCoupon = 0.0;
			// 总共付了多少钱（没有优惠后或打折的价格）
			double totalAlreadyPay = 0.0;
			double actuAlreadyPay = 0.0;
			boolean isDiscount = false;
			boolean useCounpon = false;

			// 最总改付多少钱
			double newpriceWithCouponAndDiscount = 0.0d;
			// 最总改付多少钱
			double newpriceWithoutCouponAndDiscount = 0.0d;

			// 得到当前优惠卷价值
			double couponPrice = 0.0;

			// 总共停车小时
			double spentHour = 0.0;
			if (order.couponId > 0) {
				TCouponEntity couponEntity = TCouponEntity
						.findDataById(order.couponId);
				if (couponEntity != null) {
					couponPrice = couponEntity.money;
				}
			}

			// 先看已经付款多少了
			List<TOrder_Py> py = order.pay;
			
			if (py != null && py.size() > 0) {
				double couponUsed = 0.0;
				for (int i=0 ;i<py.size();i++) {
					TOrder_Py p = py.get(i);

					// 状态为完成和处理中的，总价都要计算上，因为“处理中”可能是支付接口问题，单根据支付宝协议，会在几个小时内post结果信息
					if (p.ackStatus == Constants.ORDER_TYPE_FINISH
							|| p.ackStatus == Constants.ORDER_TYPE_PENDING) {
						totalAlreadyPay += p.payTotal;
						actuAlreadyPay += p.payActu;
						couponUsed += p.couponUsed;
					}
				}
				canbeUsedCoupon = Arith.decimalPrice(Math.abs(couponPrice
						- couponUsed));
			}

			Logger.debug("pay for out:::::::::::show pay money:"
					+ totalAlreadyPay);

			int feeType = order.orderFeeType<=0?parkinfo.feeType:order.orderFeeType;
			// 计算价格
			if (feeType != 1) {// 计次收费

					double realPayPrice = parkinfo.feeTypefixedHourMoney;
	
					newpriceWithoutCouponAndDiscount = Arith
							.decimalPrice(realPayPrice - totalAlreadyPay); // 还多少钱没有付
					if (newpriceWithoutCouponAndDiscount <= 0.1) { // 不差钱
						response.setExtendResponseContext("pass");
						// ***********已经完成的订单需要移到历史表**************/
						TOrderHis.moveToHisFromOrder(orderId,
								Constants.ORDER_TYPE_FINISH);
	
						throw new Exception("已经付款"
								+ Arith.decimalPrice(totalAlreadyPay) + "元[实际付款:"
								+ Arith.decimalPrice(actuAlreadyPay) + "元],无需再次付款。");
					}

			} else if (feeType == 1) {// 分段收费
				// 头几个小时
				int feeTypeSecInScopeHours = parkinfo.feeTypeSecInScopeHours;

				// 先计算总共停了多少小时
				if (order.startDate == null) { // 没有刷开始时间，这下遭了。。。。。。
					throw new Exception("无进场时间记录，请联系车位管理员.");
				}

				Date startDate = order.startDate;
				Date endDate = new Date();

				int mins = DateHelper.diffDateForMin(endDate, startDate);
				double mhour = mins / 60.0;
				spentHour = Math.ceil(mhour); // 总共停车这么多小时
				
				// 这里我们要剔除起步价时间
				double realSpentHour = spentHour - feeTypeSecInScopeHours;


					if (realSpentHour > 0) {
						newpriceWithoutCouponAndDiscount = realSpentHour
								* parkinfo.feeTypeSecOutScopeHourMoney;
					} else {// 还在一个小时以内不用付款了。。。
	
					}
				
                
				Logger.debug("pay for out:::::::::::parkinfo.feeTypeSecInScopeHourMoney:"
						+ parkinfo.feeTypeSecInScopeHourMoney
						+ ", realSpentHour:" + realSpentHour);
			}

			// 得到当前情况下优惠后的价格
			double newprice = getNewPriceAfterDiscount(parkinfo,
					newpriceWithoutCouponAndDiscount);
			if (newprice < newpriceWithoutCouponAndDiscount) {
				isDiscount = true;
			}
			Logger.debug("pay for out:::::::::::getNewPriceAfterDiscount:"
					+ newprice);
			if (newprice > 0) {
				if (newprice - canbeUsedCoupon <= 0) {
					newpriceWithCouponAndDiscount = 0;
				} else {
					// 计算使用优惠卷后的的价格
					newpriceWithCouponAndDiscount = Arith.decimalPrice(Math
							.abs(newprice - canbeUsedCoupon));
				}

				if (newpriceWithCouponAndDiscount < newprice) {
					useCounpon = true;
				}

			}

			Logger.debug("pay for out:::::::::::show Actual Price:"
					+ newpriceWithCouponAndDiscount);
			Logger.debug("pay for out:::::::::::show Orginal Price:"
					+ newpriceWithoutCouponAndDiscount);

			/****************************** 生成新的付款单 *************************/
			/****************************************************************/
			String username = flash("username");

			payOption.payActualPrice = Arith
					.decimalPrice(newpriceWithCouponAndDiscount);
			payOption.payOrginalPrice = Arith
					.decimalPrice(newpriceWithoutCouponAndDiscount);
			payOption.isDiscount = isDiscount;
			payOption.useCounpon = useCounpon;
			payOption.counponUsedMoney = canbeUsedCoupon;
			payOption.order = order;
			payOption.parkSpentHour = spentHour;
			TOrder_Py newpay = new TOrder_Py();

			if (newpriceWithCouponAndDiscount > 0) {

				if (order.orderStatus == Constants.ORDER_TYPE_FINISH) {// 订单已经完成了
					throw new Exception("订单已经完成，不能再次付款");
				}
				
				
				 if(payway==Constants.PAYMENT_TYPE_CASH){//用户付现金
					 response.setExtendResponseContext("wait");
					 double actPay= Arith.decimalPrice(newpriceWithCouponAndDiscount);
					 
						
						//还是发个消息给管理员吧
						
						PushController.pushToParkAdminForRequestPay(
								order.parkInfo.parkId, ""
										+ order.userInfo.userPhone,
										actPay);
						
					 
					 throw new Exception("应付现金"+actPay+"元，已付0元，还需付现金"+actPay+"元。");
				 }
				
				// 查看当前状态下是否有付款单
				if (order.pay != null) {

					List<TOrder_Py> orderPys = order.pay;

					if (orderPys != null) {
						// 只需要判断当前pending的付款单
						for (TOrder_Py payment : orderPys) {
							if (payment.ackStatus == Constants.PAYMENT_STATUS_PENDING) {
								throw new Exception("当前订单已经有一笔付款["
										+ payment.payActu + "元]正在等待支付接口响应");
							} else if (payment.ackStatus == Constants.PAYMENT_STATUS_START
									|| payment.ackStatus == Constants.PAYMENT_STATUS_EXCPTION) {
								newpay.parkPyId = payment.parkPyId; // 这里传对象有问题
								break;
							}
						}

					}
				}

				int payWay = Constants.PAYMENT_TYPE_ZFB;

				if (useCounpon) {
					payWay = Constants.PAYMENT_COUPON
							+ Constants.PAYMENT_TYPE_ZFB;
				}
			

				newpay.payActu = newpriceWithCouponAndDiscount;
				newpay.payMethod = payWay;
				newpay.payTotal = newpriceWithoutCouponAndDiscount;
				newpay.ackStatus = Constants.PAYMENT_STATUS_START;
				newpay.createPerson = username;
				newpay.payDate = new Date();
				newpay.order = order;
				newpay.couponUsed = canbeUsedCoupon;

				TOrder_Py.saveData(newpay);

				// 生成alipay的info
				// 订单
				String orderInfo = getOrderInfo(order, newpay.parkPyId,
						newpay.payActu);
				Logger.debug(" ******ali pay ******order info:" + orderInfo);
				// 对订单做RSA 签名
				String sign = sign(orderInfo);
				Logger.debug(" ******ali pay ******sign:" + sign);
				try {
					// 仅需对sign 做URL编码
					sign = URLEncoder.encode(sign, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					Logger.error("generatorOrderStringAndGetPayInfo", e);
				}
				// 完整的符合支付宝参数规范的订单信息
				final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
						+ getSignType();

				Logger.debug(" ******ali pay ******payInfo:" + payInfo);

				payOption.payInfo = payInfo;
				payOption.paymentId = newpay.parkPyId;

				response.setExtendResponseContext("出场付款单数据生成成功，并且返回支付串.");

				LogController.info("generator order successfully:"
						+ order.orderName + ",from " + username);
			} else {
				response.setExtendResponseContext("pass");

				// ***********已经完成的订单需要移到历史表**************/
				TOrderHis.moveToHisFromOrder(orderId,
						Constants.ORDER_TYPE_FINISH);

				double actPay= Arith.decimalPrice(actuAlreadyPay);
				
				throw new Exception("应付"+actPay+"元，已付"+actPay+"元，还需付0元");
			}

			/*********************************************************************
			 * 生成完毕
			 ***********************************************************************/
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(payOption);
		} catch (Exception e) {
			if (response.getExtendResponseContext() != null
					&& (response.getExtendResponseContext().equals("pass"))) {

				order.endDate = new Date();
				order.orderStatus = Constants.ORDER_TYPE_FINISH;
				payOption.order = order;
				response.setResponseEntity(payOption);
			}else if (response.getExtendResponseContext() != null
					&& (response.getExtendResponseContext().equals("wait"))) {
				
				order.endDate = new Date();
				payOption.order = order;
				response.setResponseEntity(payOption);

			}
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("updatePayment", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}

	/**
	 * 得到当前停车场的准备价格信息 如果是分段收费，只是返回1个小时内的价格
	 * 
	 * @param parkingProdId
	 * @param counponId
	 * @return
	 */
	public static Result getRealPayInfo(long parkingProdId, long counponId) {

		Logger.info("start to get real price for:" + parkingProdId
				+ ",counponId:" + counponId);

		ComResponse<ChebolePayOptions> response = new ComResponse<ChebolePayOptions>();
		try {
			if (parkingProdId > 0) {
				TParkInfoProd infoPark = TParkInfoProd
						.findDataById(parkingProdId);
				if (infoPark != null) {
					// 开始组合订单
					double realPayPrice = 0.0d;
					double orginalPrice = 0.0d;
					boolean isDiscount = false;
					boolean useCounpon = false;
					double couponUsedMoney = 0.0d;
					int keepMinus = 0;

					// 计算价格
					if (infoPark.feeType != 1) {// 计次收费,就先付了
						realPayPrice = infoPark.feeTypefixedHourMoney;
						orginalPrice = infoPark.feeTypefixedHourMoney;
						keepMinus = infoPark.feeTypeFixedMinuteOfInActivite;

					} else if (infoPark.feeType == 1) {// 分段收费
						realPayPrice = infoPark.feeTypeSecInScopeHourMoney;
						orginalPrice = infoPark.feeTypeSecInScopeHourMoney;
						keepMinus = infoPark.feeTypeSecMinuteOfActivite;
						// 先收1个小时内的钱
					}

					// 得到当前情况下优惠后的价格
					double newprice = getNewPriceAfterDiscount(infoPark,
							realPayPrice);
					if (newprice < realPayPrice) {
						isDiscount = true;
						realPayPrice = newprice;
					}

					// 是否能够使用用户选择的优惠卷
					if (counponId > 0) {
						TCouponEntity couponEntity = TCouponEntity
								.findDataById(counponId);
						if (couponEntity != null) {
							realPayPrice = realPayPrice - couponEntity.money;
							if (realPayPrice < 0) {
								couponUsedMoney = Arith
										.decimalPrice(couponEntity.money
												- Math.abs(realPayPrice));
								realPayPrice = 0;
							} else {
								couponUsedMoney = couponEntity.money;
							}

							useCounpon = true;
						}
					}

					ChebolePayOptions payOption = new ChebolePayOptions();
					payOption.payActualPrice = Arith.decimalPrice(realPayPrice);
					payOption.payOrginalPrice = Arith
							.decimalPrice(orginalPrice);
					payOption.isDiscount = isDiscount;
					payOption.useCounpon = useCounpon;
					payOption.counponId = counponId;
					payOption.counponUsedMoney = couponUsedMoney;
					if (keepMinus > 0) {
						try {
							payOption.keepToDate = DateHelper.format(DateHelper
									.currentDateAddWithType(Calendar.MINUTE,
											keepMinus), "yyyy-MM-dd HH:mm:ss");
						} catch (Exception e) {
							Logger.error("getRealPayInfo", e);
						}
					} else {
						payOption.keepToDate = "--:--";
					}

					// ***************组合订单完毕******************
					response.setResponseStatus(ComResponse.STATUS_OK);
					response.setResponseEntity(payOption);
					response.setExtendResponseContext("停车场价格计算成功.");

					Logger.debug("本次实际付款金额:" + realPayPrice + "元。");

					/*********************************************************************
					 * 生成完毕
					 ***********************************************************************/
				} else {
					throw new Exception("无效的停车场");
				}

			} else {
				throw new Exception("无效的停车场ID");
			}

		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}

		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);

		return ok(json);
	}

	/**
	 * 得到当前时间段或者后台设置优惠属性后的最新价格
	 * 
	 * @param infoPark
	 * @param planedPrice
	 * @return
	 */
	private static double getNewPriceAfterDiscount(TParkInfoProd infoPark,
			double planedPrice) {

		if (planedPrice <= 0) {
			return 0.0;
		}

		double newPrice = planedPrice;
		if (infoPark.isDiscountAllday == 1) {
			double discountHourAlldayMoney = infoPark.discountHourAlldayMoney;
			if (newPrice > discountHourAlldayMoney) { // 如果用户选择了全天优惠，那么，这里就收最少的那个钱.
				newPrice = discountHourAlldayMoney;
			}
		}

		if (infoPark.isDiscountSec == 1) {// 又勾选了优惠时段，那么
			if (infoPark.discountSecStartHour != null
					&& infoPark.discountSecEndHour != null) {
				String currentDate = DateHelper.format(new Date(),
						"yyyy/MM/dd HH:mm:00");
				String startdate = DateHelper.format(
						infoPark.discountSecStartHour, "yyyy/MM/dd HH:mm:00");
				String endDate = DateHelper.format(infoPark.discountSecEndHour,
						"yyyy/MM/dd HH:mm:00");

				long currentDateDD = DateHelper.getStringtoDate(currentDate,
						"yyyy/MM/dd HH:mm:ss").getTime();
				long startdateDD = DateHelper.getStringtoDate(startdate,
						"yyyy/MM/dd HH:mm:ss").getTime();
				long endDateDD = DateHelper.getStringtoDate(endDate,
						"yyyy/MM/dd HH:mm:ss").getTime();
				if (endDateDD < startdateDD) {// 证明这里是过了24点
					endDateDD = DateHelper.addDate(new Date(endDateDD), 1)
							.getTime();
				}

				// 在优惠时间内，就用优惠价格
				if (currentDateDD >= startdateDD && currentDateDD <= endDateDD) {
					if (newPrice > infoPark.discountSecHourMoney) {
						newPrice = infoPark.discountSecHourMoney;
					}
				}

			}
		}
		return newPrice;
	}

	/**
	 * 
	 * @param orderid
	 */
	public static void scheduleTaskForOverdue(final long orderid) {

		Logger.debug("#######start one thread to run overdue task, order:"
				+ orderid + "#########");
		try {
			Thread newThread = new Thread(new Runnable() {

				@Override
				public void run() {
					 TOrder order = TOrder.findDataById(orderid);
					if (order != null) {
						final TParkInfoProd parking = order.parkInfo;
						if (parking != null && order.startDate == null) {

							// 取出支付成功的时间
							Date payDate = new Date();

							int time = 0;
							
							final int feeType = order.orderFeeType<=0?parking.feeType:order.orderFeeType;
							
							if (feeType == 1) {// 分段计费的情况下
								time = parking.feeTypeSecMinuteOfActivite;
							} else {
								time = parking.feeTypeFixedMinuteOfInActivite;
							}

							if (time > 0) {
								Akka.system()
										.scheduler()
										.scheduleOnce(
												Duration.create(time,
														TimeUnit.MINUTES),
												new Runnable() {
													public void run() {
														Logger.debug("#######AKKA schedule start>> set order to overdue:"
																+ orderid
																+ "#########");
														TOrder order = TOrder
																.findDataById(orderid);
														
														//计次收费，就这是为过期就行
														if (order != null
																&& order.startDate == null) {
															
															if(feeType!=1){
															    order.orderStatus = Constants.ORDER_TYPE_OVERDUE;
																Set<String> options = new HashSet<String>();
																options.add("orderStatus");
																Ebean.update(order,
																		options);
															}else if(feeType==1){
																String scanResult = "http://chebole#"+parking.parkId;
																 ScanController.scanForIn(order.orderId, scanResult);
															}
															Logger.debug("#######AKKA schedule end>> done for overdue:"
																	+ orderid
																	+ "#########");

														}

													}
												}, Akka.system().dispatcher());
							}
						}
					}
				}

			});

			newThread.start();

		} catch (Exception e) {
			Logger.error("scheduleTaskForOverdue", e);
		}
	}

	@BasicAuth
	public static Result updatePayment(long orderid, long payId, int status,
			String needfinishedOrder) {

		ComResponse<TOrder_Py> response = new ComResponse<TOrder_Py>();
		try {
			TOrder_Py order = TOrder_Py.findDataById(payId);
			if (order != null) {
				if (status == Constants.PAYMENT_STATUS_FINISH) {

					if (order.ackStatus != Constants.PAYMENT_STATUS_FINISH) {
						order.ackDate = new Date();
						order.ackStatus = Constants.PAYMENT_STATUS_FINISH;
						// TOrder_Py.saveData(order);
						Set<String> options = new HashSet<String>();
						options.add("ackDate");
						options.add("ackStatus");
						Ebean.update(order, options);

						// start to push
						TOrder torder = order.order;
						if (torder != null) {
							if (torder.parkInfo != null
									&& torder.userInfo != null
									&& torder.startDate == null
									&& torder.endDate == null) {
								
								if(torder.couponId>0){
									TUseCouponEntity userCoupon = TUseCouponEntity.getExistCouponByUserIdAndId(torder.couponId, torder.userInfo.userid);
									if(userCoupon.isable==1){
										Set<String> optionsCoupon = new HashSet<String>();
										userCoupon.isable=2; //设置为正在使用
										optionsCoupon.add("isable");
										Ebean.update(userCoupon,optionsCoupon);;
									}
								}
								
								PushController.pushToParkAdmin(
										torder.parkInfo.parkId, ""
												+ torder.userInfo.userPhone,
										torder.parkInfo.parkname);

								// 开始一个任务去设置过期任务
								scheduleTaskForOverdue(orderid);

							} else {
								Logger.warn("push service is not working. park or user is empty for order:"
										+ torder.orderId);
							}
						} else {
							Logger.warn("push service is not working. order is empty for paymentid:"
									+ order.parkPyId);
						}

						// 以下主要是防止json解析无限递归
						TOrder torderNew = new TOrder();
						torderNew.orderId = torder.orderId;
						order.order = torderNew;
					}

					if (orderid > 0 && needfinishedOrder != null
							&& needfinishedOrder.trim().equals("true")) {
						// ***********已经完成的订单需要移到历史表**************/
						TOrderHis.moveToHisFromOrder(orderid,
								Constants.ORDER_TYPE_FINISH);
					}

					LogController.info("payment done for payment id:" + payId
							+ ",order id:" + orderid);
				} else if (status == Constants.PAYMENT_STATUS_PENDING) {
					order.ackDate = new Date();
					order.ackStatus = Constants.PAYMENT_STATUS_PENDING;
					// TOrder_Py.saveData(order);
					Set<String> options = new HashSet<String>();
					options.add("ackDate");
					options.add("ackStatus");
					Ebean.update(order, options);
					LogController.info("payment pending as alibaba for "
							+ payId);
				} else {
					order.ackDate = new Date();
					order.ackStatus = Constants.PAYMENT_STATUS_EXCPTION;
					// TOrder_Py.saveData(order);
					Set<String> options = new HashSet<String>();
					options.add("ackDate");
					options.add("ackStatus");
					Ebean.update(order, options);
					LogController.info("payment exception for parking " + payId
							+ ",status:" + status);
				}
				response.setResponseStatus(ComResponse.STATUS_OK);
				response.setResponseEntity(order);
			} else {
				throw new Exception("没有找到订单付款项");
			}

		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("updatePayment", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);

	}

	/**
	 * 结账放行专用,人工用
	 * 
	 * @param payId
	 * @param status
	 * @return
	 */
	@BasicAuth
	public static Result parkingOutForAdm(long parkingId, long orderId,
			double pay) {

		ComResponse<TOrder_Py> response = new ComResponse<TOrder_Py>();
		try {

			TOrder order = TOrder.findDataById(orderId);
			if (order == null) {
				throw new Exception("系统无法找到该订单:" + orderId);
			}

			TParkInfoProd parkinfo = order.parkInfo;
			if (parkinfo == null) {
				throw new Exception("该订单无有效停车场");
			} else if (parkinfo.parkId != parkingId) {
				throw new Exception("该订单不属于此停车场，请检查");
			}

			Date currentDate = new Date();

			TOrder_Py orderPy = new TOrder_Py();
			orderPy.ackStatus = Constants.PAYMENT_STATUS_FINISH;
			orderPy.payMethod = Constants.PAYMENT_TYPE_CASH;
			orderPy.payActu = pay;
			orderPy.payTotal = pay;
			orderPy.ackDate = currentDate;
			orderPy.payDate = currentDate;
			orderPy.createPerson = flash("username");
			orderPy.order = order;
			TOrder_Py.saveData(orderPy);

			// ***********已经完成的订单需要移到历史表**************/
			TOrderHis.moveToHisFromOrder(orderId, Constants.ORDER_TYPE_FINISH);

			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(orderPy);
			response.setExtendResponseContext("付款单生成成功");

			LogController.info("payment for out of park for " + parkingId
					+ ", pay:" + pay);

		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("parkingOutForAdm", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);

	}

	/**
	 * 支付成功后的回调，目前只是写入日志表
	 * 
	 * @return
	 */
	public static Result notifyPayResult() {
		Logger.info("###########get feedback from aili post#############");
		try {
			String request = request().body().toString();

			Map<String, String[]> params = request().body().asFormUrlEncoded();
			String[] paramsNumber = params.get("out_trade_no");
			String paymentIdString = paramsNumber == null ? "-1"
					: paramsNumber[0];

			String[] trade_status = params.get("trade_status");
			String status = trade_status == null ? "unknow" : trade_status[0];

			String[] total_fee = params.get("total_fee");
			String fee = total_fee == null ? "0" : total_fee[0];

			String[] trade_no = params.get("trade_no");
			String ailiNo = trade_no == null ? "0" : trade_no[0];
			long paymentId = Long.parseLong(paymentIdString);

			TOrder_Py order = TOrder_Py.findDataById(paymentId);

			if (status.trim().equals("WAIT_BUYER_PAY")) {

				if (order != null) {
					order.ackDate = new Date();
					order.ackStatus = Constants.PAYMENT_STATUS_PENDING;
					Set<String> options = new HashSet<String>();
					options.add("ackDate");
					options.add("ackStatus");
					Ebean.update(order, options);

				}

			} else if (status.trim().equals("TRADE_SUCCESS")) { // 如果交易成功，更新订单状态并且推送消息
				try {
					if (order != null) {
						if (order.ackStatus != Constants.PAYMENT_STATUS_FINISH) {
							order.ackDate = new Date();
							order.ackStatus = Constants.PAYMENT_STATUS_FINISH;
							Set<String> options = new HashSet<String>();
							options.add("ackDate");
							options.add("ackStatus");
							Ebean.update(order, options);

							// 状态更新后，如果是刚下订单，需要推送消息
							// start to push
							TOrder torder = order.order;
							if (torder != null) {
								if (torder.parkInfo != null
										&& torder.userInfo != null
										&& torder.startDate == null
										&& torder.endDate == null) {
									
									if(torder.couponId>0){
										TUseCouponEntity userCoupon = TUseCouponEntity.getExistCouponByUserIdAndId(torder.couponId, torder.userInfo.userid);
										if(userCoupon.isable==1){
											Set<String> optionsCoupon = new HashSet<String>();
											userCoupon.isable=2; //设置为正在使用
											optionsCoupon.add("isable");
											Ebean.update(userCoupon,optionsCoupon);;
										}
									}
									
									PushController.pushToParkAdmin(
											torder.parkInfo.parkId,
											"" + torder.userInfo.userPhone,
											torder.parkInfo.parkname);

									// 开始一个任务去设置过期任务
									scheduleTaskForOverdue(torder.orderId);

								} else {
									Logger.warn("push service is not working. torder.parkInfo!=null&&torder.userInfo!=null&&torder.startDate==null&&torder.endDate==null");
								}
							} else {
								Logger.warn("push service is not working. order is empty for paymentid:"
										+ order.parkPyId);
							}

						}
					}

				} catch (Exception e) {
					Logger.error("notifyPayResult", e);
				}
				
				return ok("success");
				
			}

			LogController.info(
					"notify-->payment id:" + paymentId + ",status:" + status
							+ ",total fee:" + fee + ",aili_trade_no:" + ailiNo,
					"alipay");

			Logger.info("#####feedback:" + request);

		} catch (Exception e) {
			LogController.info("exception:" + e.getMessage(), "alipay");
		}
		return ok("nok");
	}
}
