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
	public static final int PAYMENT_COUPONCASH=PAYMENT_TYPE_CASH+PAYMENT_COUPON;
	public static final int PAYMENT_COUPONDISCOUNTCASH=PAYMENT_TYPE_CASH+PAYMENT_DISCOUNT+PAYMENT_COUPON;
	public static final int PAYMENT_DISCOUNTCASH=PAYMENT_TYPE_CASH+PAYMENT_DISCOUNT;
	
	public static final int USER_TYPE_NORMAL=10;
	public static final int USER_TYPE_PADMIN=20;
	public static final int USER_TYPE_PSADMIN=21;
	public static final int USER_TYPE_MADMIN=30;
	public static final int USER_TYPE_MSADMIN=31;
	public static final int USER_TYPE_SADMIN=999;
	
	//删除验证码的时间
	public static final int SCHEDULE_TIME_DELETE_VERIFYCODE=120;
	
	
}
