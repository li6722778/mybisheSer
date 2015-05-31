package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import controllers.routes;

public class SecurityController extends Security.Authenticator{

	@Override
	public String getUsername(Context ctx) {
	   return ctx.session().get("userphone");
	}
	@Override
	public Result onUnauthorized(Context ctx) {
	  return redirect(routes.Application.login());
	}
}
