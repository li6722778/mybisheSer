package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import utils.ConfigHelper;
import utils.Constants;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
    	
    	String version = Constants.VERSION;
    	
    	try{
    	version = ConfigHelper.getString("chebole.version");
    	}catch(Exception e){
    		
    	}
    	
        return ok(index.render(version));
    }

}
