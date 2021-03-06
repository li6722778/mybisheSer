package utils;


public class Constants {

	public static final String VERSION="chebole.1.0.526.ser";
	
	public static final int PARKING_TYPE_IN=1;
	public static final int PARKING_TYPE_OUT=2;
	public static final int PARKING_STATUS_OPEN=1;
	public static final int PARKING_STATUS_CLOSE=0;
	
	public static final int ORDER_TYPE_START=1;
	public static final int ORDER_TYPE_FINISH=2;
	public static final int ORDER_TYPE_PENDING=5;
	public static final int ORDER_TYPE_OVERDUE=3;
	public static final int ORDER_TYPE_EXCPTION=4;
	
	public static final int PAYMENT_STATUS_START=1;
	public static final int PAYMENT_STATUS_FINISH=2;
	public static final int PAYMENT_STATUS_PENDING=5;
	public static final int PAYMENT_STATUS_EXCPTION=3;
	
	
	public static final int PAYMENT_TYPE_ZFB=1;
	public static final int PAYMENT_TYPE_WEIXIN=2;
	public static final int PAYMENT_TYPE_YL=3;	
	public static final int PAYMENT_COUPON=4;
	public static final int PAYMENT_DISCOUNT=8;
	public static final int PAYMENT_TYPE_CASH=9;	

	public static final int PAYMENT_COUPONZFB=PAYMENT_TYPE_ZFB+PAYMENT_COUPON;
	public static final int PAYMENT_COUPONWEIXIN=PAYMENT_TYPE_WEIXIN+PAYMENT_COUPON;
	public static final int PAYMENT_COUPONCASH=PAYMENT_TYPE_CASH+PAYMENT_COUPON;
	public static final int PAYMENT_COUPONDISCOUNTCASH=PAYMENT_TYPE_CASH+PAYMENT_DISCOUNT+PAYMENT_COUPON;
	public static final int PAYMENT_DISCOUNTCASH=PAYMENT_TYPE_CASH+PAYMENT_DISCOUNT;
	public static final int PAYMENT_lijian=21;
	
	public static final int USER_TYPE_NORMAL=10;
	public static final int USER_TYPE_PADMIN=20;
	public static final int USER_TYPE_PSADMIN=21;
	public static final int USER_TYPE_MADMIN=30;
	public static final int USER_TYPE_MSADMIN=31;
	public static final int USER_TYPE_SADMIN=999;
	
	//删除验证码的时间
	public static final int SCHEDULE_TIME_DELETE_VERIFYCODE=120;
	
	public static final int OPTIONS_PROTOCOL = 1 ;//注册协议
	
	//订单将要过期时间
	public static final int ORDER_EXPIRE_MIN = 5;
	
	//PayIn的payway
	public static final int PAYIN_PAY_ZFB=0;
	public static final int PAYIN_PAY_WX=1;
	
	//PayIn的payway
		public static final int PAYOUT_PAY_ZFB=0;
		public static final int PAYOUT_PAY_WX=2;
		public static final int PAYOUT_PAY_CASH=1;
}
