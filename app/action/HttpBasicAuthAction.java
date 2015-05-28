package action;



import org.apache.commons.codec.binary.Base64;

import models.info.TuserInfo;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Flash;
import play.mvc.Result;

public class HttpBasicAuthAction extends Action.Simple {
	private static final String AUTHORIZATION = "authorization";
	private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
	private static final String REALM = "Basic realm=\"chebole\"";

	@Override
	public Promise<Result> call(Http.Context context) throws Throwable {

		String authHeader = context.request().getHeader(AUTHORIZATION);
		if (authHeader == null) {
			context.response().setHeader(WWW_AUTHENTICATE, REALM);
			return Promise.pure((Result)unauthorized());
		}
		Logger.info("**********auth header:"+authHeader+"************");
		String auth = authHeader.substring(6);
		byte[] decodedAuth = Base64.decodeBase64(auth);
		String[] credString = new String(decodedAuth, "UTF-8").split(":");

		if (credString == null || credString.length != 2) {
			return Promise.pure((Result)unauthorized());
		}

		String username = credString[0];
		String password = credString[1];
		Logger.info("basic auth,user:"+username);
		Logger.debug("basic auth,password:"+password);
		TuserInfo authUser = TuserInfo.authenticate(username, password);
		
		// added flash
		if (authUser != null) {
			Flash flash = context.flash();
			flash.put("userphone", "" + authUser.userPhone);
			flash.put("username", "" + authUser.userName);
			flash.put("userid", "" + authUser.userid);
		}
		Logger.info("**********auth end,user:"+(authUser==null?"null":authUser.userid)+"************");
		
		return (authUser == null) ? Promise.pure((Result)unauthorized()): delegate.call(context);
	}
}
