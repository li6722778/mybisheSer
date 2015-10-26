import java.util.concurrent.TimeUnit;

import actor.CleanUnpayActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

/**
 * server启动后的一些全局设置，目前实现
 * 1. 定时任务，每凌晨3点钟删除未付款的订单，节约数据库空间
 * @author woderchen
 *
 */
public class Global extends GlobalSettings{
	
    
	@Override
	  public void onStart(Application app) {
	    Logger.info("Application has started");

	    ActorRef cleanActor = Akka.system().actorOf(Props.create(CleanUnpayActor.class),"CleanUnpayActor");
	    
	  //这里设置一个删除没有支付的订单的定期任务
	    Akka.system().scheduler().schedule(
	            Duration.create(-1, TimeUnit.MINUTES), //Initial delay 0 milliseconds
	            Duration.create(6, TimeUnit.HOURS),     //Frequency 6 hour
	            cleanActor,
	            "run",
	            Akka.system().dispatcher(),
	            null
	    );

	  }  

	  @Override
	  public void onStop(Application app) {
	    Logger.info("Application shutdown...");
	  }  
//	  @Override
//	  public Result onBadRequest(String uri, String error) {
//	    return badRequest("Don't try to hack the URI!");
//	  }  
//	  @Override
//	  public Result onError(Throwable t) {
//	    return internalServerError(
//	      views.html.errorPage(t)
//	    );
//	  }  
}
