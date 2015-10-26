package actor;

import actor.model.TemplateSMS;
import akka.actor.UntypedActor;
import models.info.TVerifyCode;
import play.Logger;

/**
 * 用于验证短信成功发送后的回调
 * @author woderchen
 *
 */
public class VerifyCodeActor extends UntypedActor{

	@Override
	public void onReceive(Object response) throws Exception {
		 Logger.info("SMSActor=>message:"+response);
		 if (response instanceof TemplateSMS) {
			 TemplateSMS message = ( TemplateSMS )response;
        	 Logger.debug("SMSActor=>result:"+message.getResultCode());
        	 if (message.getResultCode().trim().indexOf("\"respCode\":\"000000\"")>=0){
        		 try{
        		     TVerifyCode.deletePhone(Long.parseLong(message.getTo()));
        		 }catch(Exception e){
        			 Logger.error("VerifyCodeActor", e);
        		 }
        	 }
		 }else{
			 Logger.info("SMSActor=>unhandled.");
			 unhandled(response);
		 }
		
	}

}
