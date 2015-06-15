package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.ChebolePayOptions;
import models.info.TCouponEntity;
import models.info.TOrder;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Py;
import models.info.TuserInfo;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Arith;
import utils.ComResponse;
import utils.Constants;
import utils.DateHelper;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 付款
 * @author woderchen
 *
 */
public class PayController extends Controller{
	public static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	//商户PID
	public static final String PARTNER = "";
	//商户收款账号
	public static final String SELLER = "";
	//商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "";
	//支付宝公钥
	public static final String RSA_PUBLIC = "";
	
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private static String sign(String content) {
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
					org.apache.commons.codec.binary.Base64.decodeBase64(privateKey));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

			return org.apache.commons.codec.binary.Base64.encodeBase64String(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	
	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private static String getOrderInfo(TOrder Torder,double price) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + Torder.orderId + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + Torder.orderName + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + Torder.orderDetail +",coupon:"+Torder.couponId+ "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
				+ "\"";

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
	 * web 接口
	 * @param parkingProdId
	 * @return
	 */
	@BasicAuth
	public static Result generatorOrderStringAndGetPayInfo(long parkingProdId,String city){
		Logger.info("start to generator order and generator aili pay for:"+parkingProdId+",city:"+city);
		String request = request().body().asJson().toString();
		Logger.debug("start to post data:" + request);
		
		ChebolePayOptions data = gsonBuilderWithExpose.fromJson(request, ChebolePayOptions.class);

		ComResponse<ChebolePayOptions>  response = new ComResponse<ChebolePayOptions>();
		try {
			
			if(parkingProdId>0){
				TParkInfoProd infoPark = TParkInfoProd.findDataById(parkingProdId);
				String useridString = flash("userid");
				TuserInfo user = null;
				try{
					user = TuserInfo.findDataById(Long.parseLong(useridString));
				}catch(Exception e){
					Logger.error("", e);
					throw new Exception("fetch session id fail.");
				}
				if(infoPark!=null&&user!=null){
					//开始组合订单
					TOrder dataBean = new TOrder();
					dataBean.orderCity=city;
					dataBean.orderDate = new Date();
					dataBean.orderName="停车费:"+infoPark.parkname;
					dataBean.orderStatus = Constants.ORDER_TYPE_START;
					dataBean.parkInfo = infoPark;
					dataBean.userInfo = user;
					dataBean.couponId = data.counponId;
					dataBean.orderDetail=infoPark.detail;
					
					TParkInfo_Py py = new TParkInfo_Py();
					py.payActu=data.payActualPrice;
					py.payMethod=data.payActualPrice==0?Constants.PAYMENT_TYPE_CASH:Constants.PAYMENT_TYPE_ZFB;
					py.payTotal=data.payOrginalPrice;
					py.ackStatus=Constants.PAYMENT_STATUS_START;
					py.createPerson=user.userName;
					py.payDate = new Date();
					
					List<TParkInfo_Py> pays= new ArrayList<TParkInfo_Py>();
					pays.add(py);
					
					dataBean.pay=pays;
					
					//***************组合订单完毕******************
					
					
					TOrder.saveData(dataBean);
					
					Logger.debug("本次实际付款金额:"+py.payActu+"元,orderId:"+dataBean.orderId+",payment id:"+py.parkPyId);
					
					/*********************************************************************
					 * 这里生成aili pay String
					 ***********************************************************************/
					// 订单
					String orderInfo = getOrderInfo(dataBean,py.payActu);
					Logger.debug(" ******ali pay ******order info:"+orderInfo);
					// 对订单做RSA 签名
					String sign = sign(orderInfo);
					Logger.debug(" ******ali pay ******sign:"+sign);
					try {
						// 仅需对sign 做URL编码
						sign = URLEncoder.encode(sign, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						Logger.error("generatorOrderStringAndGetPayInfo", e);
					}
					// 完整的符合支付宝参数规范的订单信息
					final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"+ getSignType();
					
					Logger.debug(" ******ali pay ******payInfo:"+payInfo);
					
					data.payInfo = payInfo;
					data.orderId = dataBean.orderId;
					data.paymentId=py.parkPyId;
					
					/*********************************************************************
					 * 生成完毕
					 ***********************************************************************/
					response.setResponseStatus(ComResponse.STATUS_OK);
					response.setResponseEntity(data);
					response.setExtendResponseContext("订单数据生成成功，并且返回支付串.");
					LogController.info("generator order successfully:"+dataBean.orderName+",from "+user.userPhone);
				}else{
					throw new Exception("服务端数据不完整不能生成订单.");
				}

			}else{
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
	 * 得到当前停车场的准备价格信息
	 * 如果是分段收费，只是返回1个小时内的价格
	 * @param parkingProdId
	 * @param counponId
	 * @return
	 */
	public static Result getRealPayInfo(long parkingProdId, long counponId){
		
       Logger.info("start to get real price for:"+parkingProdId+",counponId:"+counponId);
		
		ComResponse<ChebolePayOptions>  response = new ComResponse<ChebolePayOptions>();
		try {
		if(parkingProdId>0){
			TParkInfoProd infoPark = TParkInfoProd.findDataById(parkingProdId);
			if(infoPark!=null){
				//开始组合订单
				double realPayPrice = 0.0d;
				double orginalPrice = 0.0d;
				boolean isDiscount=false;
				boolean useCounpon = false;
				
				//计算价格
				if(infoPark.feeType!=1){//计次收费,就先付了
					realPayPrice = infoPark.feeTypefixedHourMoney;
					orginalPrice = infoPark.feeTypefixedHourMoney;

				}else if(infoPark.feeType==1){//分段收费
					realPayPrice = infoPark.feeTypeSecInScopeHourMoney;
					orginalPrice = infoPark.feeTypeSecInScopeHourMoney;
				   //先收1个小时内的钱
				}
				
				if(infoPark.isDiscountAllday==1){
					double discountHourAlldayMoney = infoPark.discountHourAlldayMoney;
					if(realPayPrice>discountHourAlldayMoney){ //如果用户选择了全天优惠，那么，这里就收最少的那个钱.
						realPayPrice=discountHourAlldayMoney;
						isDiscount = true;
					}
				}
				
				if(infoPark.isDiscountSec==1){//又勾选了优惠时段，那么
					if(infoPark.discountSecStartHour!=null&&infoPark.discountSecEndHour!=null){
						String currentDate = DateHelper.format(new Date(), "yyyy/MM/dd HH:mm:00");
						String startdate = DateHelper.format(infoPark.discountSecStartHour, "yyyy/MM/dd HH:mm:00");
						String endDate = DateHelper.format(infoPark.discountSecEndHour, "yyyy/MM/dd HH:mm:00");
						
						long currentDateDD = DateHelper.getStringtoDate(currentDate, "yyyy/MM/dd HH:mm:ss").getTime();
						long startdateDD = DateHelper.getStringtoDate(startdate, "yyyy/MM/dd HH:mm:ss").getTime();
						long endDateDD = DateHelper.getStringtoDate(endDate, "yyyy/MM/dd HH:mm:ss").getTime();
						if(endDateDD<startdateDD){//证明这里是过了24点
							endDateDD  = DateHelper.addDate(new Date(endDateDD), 1).getTime();
						}
						
						//在优惠时间内，就用优惠价格
						if(currentDateDD>=startdateDD&&currentDateDD<=endDateDD){
							if(realPayPrice>infoPark.discountSecHourMoney){
								realPayPrice = infoPark.discountSecHourMoney;
								isDiscount = true;
							}
						}
						
					}
				}
				
				if(counponId>0){
					TCouponEntity couponEntity = TCouponEntity.findDataById(counponId);
					if(couponEntity!=null){
						realPayPrice = realPayPrice-couponEntity.money;
						if(realPayPrice<0){
							realPayPrice = 0;
						}
						useCounpon = true;
					}
				}
				
				
				ChebolePayOptions payOption = new ChebolePayOptions();
				payOption.payActualPrice=Arith.decimalPrice(realPayPrice);
				payOption.payOrginalPrice=Arith.decimalPrice(orginalPrice);
				payOption.isDiscount=isDiscount;
				payOption.useCounpon=useCounpon;
				
				//***************组合订单完毕******************
				response.setResponseStatus(ComResponse.STATUS_OK);
				response.setResponseEntity(payOption);
				response.setExtendResponseContext("停车场价格计算成功.");
				
				Logger.debug("本次实际付款金额:"+realPayPrice+"元。");

				/*********************************************************************
				 * 生成完毕
				 ***********************************************************************/
			}else{
				throw new Exception("无效的停车场");
			}

		}else{
			throw new Exception("无效的停车场ID");
		}
		
		}catch(Exception e){
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		
		
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		
		return ok(json);
	}
	
	
	@BasicAuth
	public static Result updatePayment(long parkPyId,int status){
		
		ComResponse<TParkInfo_Py>  response = new ComResponse<TParkInfo_Py>();
		try {
			TParkInfo_Py order = TParkInfo_Py.findDataById(parkPyId);
			if(order!=null){
				if(status == Constants.PAYMENT_STATUS_FINISH){
					order.ackDate = new Date();
					order.ackStatus = Constants.PAYMENT_STATUS_FINISH;
					TParkInfo_Py.saveData(order);
					
					response.setExtendResponseContext("完成订单付款状态.");
					LogController.info("payment done for "+parkPyId);
				}else if(status == Constants.PAYMENT_STATUS_PENDING){
					order.ackStatus = Constants.PAYMENT_STATUS_PENDING;
					TParkInfo_Py.saveData(order);
					response.setExtendResponseContext("订单付款等待远程银行响应.");
					LogController.info("payment pending as alibaba for "+parkPyId);
				}else{
					order.ackDate = new Date();
					order.ackStatus = Constants.PAYMENT_STATUS_EXCPTION;
					TParkInfo_Py.saveData(order);
					response.setExtendResponseContext("订单付款异常.");
					LogController.info("payment excption:"+parkPyId);
				}
				response.setResponseStatus(ComResponse.STATUS_OK);
				response.setResponseEntity(order);
			}else{
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
}
