package wxutils;

import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;



public class WXUtil {
	
	public static String getNonceStr() {
		Random random = new Random();
		return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "GBK");
	}

	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
	
	
	public static String genAppSign(List<NameValuePair> params,String apikey){
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(apikey);

        
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		
		sb.append("appSign\n"+appSign+"\n\n");
		return appSign;
	}
	
}
