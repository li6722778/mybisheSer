package wxutils;

public class ConstantUtil {
	/**
	 * �̼ҿ��Կ��Ƕ�ȡ�����ļ�
	 */
	
	 //appid
    //请同时修改  androidmanifest.xml里面，.PayActivityd里的属性<data android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid
  public static final String APP_ID = "wx4eb75affbe3a59d5";

  //detail
  public static  String detail="停车费";
  //商户号
   public static final String MCH_ID = "1262322801";


//  API密钥，在商户平台设置

    
    //获得返回字段access_token
    public static String ACCESS_TOKEN = "access_token";

    //appsecret
    public static String APP_SECRET = "966ce8f242eb6e3adafdacc99c70aa0b";
    
    public static String GRANT_TYPE = "client_credential";
    
    //获得take_accesces接口
    public static String TOKENURL = "https://api.weixin.qq.com/cgi-bin/token";
    
    //设置的api-key
    public static String APP_KEY = "42342dsadfdq24ffdsfsf432424fsdfd";
    
//	//��ʼ��
//	public static String APP_ID = "wx4eb75affbe3a59d5";//΢�ſ���ƽ̨Ӧ��id
//	public static String APP_SECRET = "966ce8f242eb6e3adafdacc99c70aa0b";//Ӧ�ö�Ӧ��ƾ֤
//	//Ӧ�ö�Ӧ����Կ
//	public static String APP_KEY = "42342dsadfdq24ffdsfsf432424fsdfd";
//	public static String PARTNER = "1262322801";//�Ƹ�ͨ�̻���
//	public static String PARTNER_KEY = "8934e7d15453e97507ef794cf7b0519d";//�̻��Ŷ�Ӧ����Կ
//	public static String TOKENURL = "https://api.weixin.qq.com/cgi-bin/token";//��ȡaccess_token��Ӧ��url
//	public static String GRANT_TYPE = "client_credential";//�����̶�ֵ 
//	public static String EXPIRE_ERRCODE = "42001";//access_tokenʧЧ�����󷵻ص�errcode
//	public static String FAIL_ERRCODE = "40001";//�ظ���ȡ������һ�λ�ȡ��access_tokenʧЧ,���ش�����
//	public static String GATEURL = "https://api.weixin.qq.com/pay/genprepay?access_token=";//��ȡԤ֧��id�Ľӿ�url
//	public static String ACCESS_TOKEN = "access_token";//access_token����ֵ
//	public static String ERRORCODE = "errcode";//�����ж�access_token�Ƿ�ʧЧ��ֵ
//	public static String SIGN_METHOD = "sha1";//ǩ���㷨����ֵ
//	//package����ֵ
//	public static String packageValue = "bank_type=WX&body=%B2%E2%CA%D4&fee_type=1&input_charset=GBK&notify_url=http%3A%2F%2F127.0.0.1%3A8180%2Ftenpay_api_b2c%2FpayNotifyUrl.jsp&out_trade_no=2051571832&partner=1900000109&sign=10DA99BCB3F63EF23E4981B331B0A3EF&spbill_create_ip=127.0.0.1&time_expire=20131222091010&total_fee=1";
//	public static String traceid = "testtraceid001";//�����û�id
}
