package utils;


import actor.OrderHandleActor;
import actor.model.OrderMovingObject;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.Future;

public class ActorHelper {

	static ActorSystem actorOrderSystem = ActorSystem.create( "play" );
    
    static {
         // Create our local actors
    	actorOrderSystem.actorOf( Props.create( OrderHandleActor.class ), "OrderHandleActor" );
    }
    
	private static ActorHelper actorHelper;
	//local actor
	static ActorSelection orderMoveingActor;
	
	//remote actor
	static ActorSelection smsActor;
	
	//remote actor
	static ActorSelection pushActor;
	
	public static ActorHelper getInstant(){
		if (actorHelper==null){
			actorHelper = new ActorHelper();
			orderMoveingActor = actorOrderSystem.actorSelection( "user/OrderHandleActor" );
			smsActor = Akka.system().actorSelection(ConfigHelper.getString("tcp.actor.sms.address"));
			pushActor  = Akka.system().actorSelection(ConfigHelper.getString("tcp.actor.push.address"));
		}
		return actorHelper;
	}
	
	
	/**
	 * 发送消息给order actor
	 * @param message
	 */
	public void sendMoveToHisOrderMessage( Long orderId,  int status){
		Logger.info("ActorHelper>send message to order actor");
		 OrderMovingObject orderMove = new OrderMovingObject();
	     orderMove.setOrderId(orderId);
	     orderMove.setStatus(status);
	    orderMoveingActor.tell(orderMove, ActorRef.noSender());
	    Logger.info("ActorHelper>sending done for order actor");
	}
	
	/**
	 * 发送消息给短信actor
	 * @param message
	 */
	public void sendSMSMessage(Object message){
		Logger.info("ActorHelper>send message to SMS actor");
		smsActor.tell(message, ActorRef.noSender());
	    Logger.info("ActorHelper>sending done for SMS actor");
	}
	
	/**
	 * 发送消息给推送actor
	 * @param message
	 */
	public void sendPushMessage(Object message){
		Logger.info("ActorHelper>send message to Push actor");
		pushActor.tell(message, ActorRef.noSender());
	    Logger.info("ActorHelper>sending done for Push actor");
	}
	
	/**
	 * 需要等待返回处理短信消息
	 * @param message
	 * @return
	 */
	public Future<Object> askSMSFuture(Object message){
		Logger.info("ActorHelper>send message to SMS actor with Futrue Model");
		return Patterns.ask(smsActor, message, 10000);
	}
	
}
