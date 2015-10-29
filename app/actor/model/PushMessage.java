package actor.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 消息推送消息体
 * @author woderchen
 *
 */
public class PushMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String type;
	
	public String message;
	
	public String title;
	
	public String sender;
	
	public String date;
	
	public String ext1;
	
	public String ext2;
	
	public ArrayList<PushTarget> targets;
	
	/**
	 * 组合消息
	 * @return
	 */
	public String encodeMessage(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(type).append("#").append(message).append("#").append(title).append("#").append(sender).append("#").append(date).append("#").append(ext1).append("#").append(ext2).append("#");
		
		return sb.toString();
	}
	
	/**
	 * 解码消息
	 * @param message
	 */
	public void decodeMessage(String pushmessage){
		if(pushmessage!=null){
			String[] mess = pushmessage.split("\\#");
			for(int i=0;i<mess.length;i++){
				if(i==0){
					type = mess[i];
				}else if(i==1){
					message = mess[i];
				}else if(i==2){
					title = mess[i];
				}else if(i==3){
					sender = mess[i];
				}else if(i==4){
					date = mess[i];
				}else if(i==5){
					ext1 = mess[i];
				}else if(i==6){
					ext2 = mess[i];
				}
			}
		}
	}
	
	
	
}
