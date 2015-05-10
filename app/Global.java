import play.Application;
import play.GlobalSettings;
import play.Logger;


public class Global extends GlobalSettings{
	@Override
	  public void onStart(Application app) {
	    Logger.info("Application has started");
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
