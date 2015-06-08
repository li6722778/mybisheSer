package controllers;

import models.info.TuserInfo;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;

public class AuthController extends Controller {
	
	
	public static Result login() {
        return ok(views.html.login.render(Form.form(LoginEntity.class)));
    }
	
	public static Result logout() {
	    session().clear();
	    flash("success", "You've been logged out");
	    return redirect(routes.Application.login());
	}
	
	public static Result authenticate() {
	    Form<LoginEntity> loginForm = Form.form(LoginEntity.class).bindFromRequest();
	    if (loginForm.hasErrors()) {
	        return badRequest(views.html.login.render(loginForm));
	    } else {
	        session().clear();
	        LoginEntity login = loginForm.get();
	        //session("user", login.user);
	        session("userphone",""+login.userPhone);
		    session("username",login.userName);
		    session("usertype",""+login.userType);
		    //session("role", login.rolename);
	        Logger.info("login successfully. user:"+login.userPhone);
	        return redirect(routes.Application.index());
	    }
	}
	
	/**
	 * web后台登录
	 * @author woderchen
	 *
	 */
	public static class LoginEntity{
		public String userPhone;
		public String passwd;
		public String userName;
		public int userType;
		
		/**
		 * 表单验证
		 * @return
		 */
		 public String validate() {
			 TuserInfo users = TuserInfo.authenticate(userPhone, passwd);

			    if ( users == null) {
			      return "用户名密码错误.";
			    }else{
			    	int  type = users.userType;
			    	if(type!= Constants.USER_TYPE_MSADMIN){
			    		 return "权限不允许.";
			    	}
			    }
				 userName = users.userName;
				 userType = users.userType;
			    return null;
			}
	}
	
}
