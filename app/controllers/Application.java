package controllers;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.ConfigHelper;
import utils.Constants;
import controllers.AuthController.LoginEntity;

public class Application extends Controller {

	@Security.Authenticated(SecurityController.class)
    public static Result index() {
		String version = Constants.VERSION;
    	try{
    	version = ConfigHelper.getString("chebole.version");
    	}catch(Exception e){
    		
    	}
    	flash("version",version);
        return ok(views.html.index.render());
    }
    
    /**
     * web login UI
     * @return
     */
    public static Result login(){
        String version = Constants.VERSION;
    	try{
    	version = ConfigHelper.getString("chebole.version");
    	}catch(Exception e){
    		
    	}
    	flash("version",version);
    	return ok(views.html.login.render(Form.form(LoginEntity.class)));
    	
    }
    
}
