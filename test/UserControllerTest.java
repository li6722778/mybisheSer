import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;

import models.TuserInfo;

import org.junit.Test;

import play.libs.Json;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.FakeRequest;
import play.test.Helpers;
import play.test.WithApplication;
import utils.RoleConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.ning.http.util.Base64;

/**
 * Junit test for Usercontroller
 * 
 * @author woderchen
 *
 */
public class UserControllerTest extends WithApplication {
	// public FakeApplication provideFakeApplication() {
	// return Helpers.fakeApplication(Helpers.inMemoryDatabase(),
	// Helpers.fakeGlobal());
	// }

	@Test
	public void addUserTest() {

		TuserInfo userinfo = new TuserInfo();
		Date currentDate = new Date();
		userinfo.userid=0l;
		//userinfo.createDate = currentDate;
		userinfo.createPerson = "junit";
		userinfo.email = "junit@chebole.com";
		userinfo.passwd = "123";
		//userinfo.updateDate = currentDate;
		userinfo.updatePerson = "junit";
		userinfo.userName = "junit test";
		userinfo.userPhone = 13551190701l;
		userinfo.userType = RoleConstants.USER_TYPE_NORMAL;
		
		JsonNode json = Json.toJson(userinfo);
		String jsonString = Json.stringify(json);
		
		String creds = String.format("%s:%s","root", "123");
		String auth = "Basic "+ Base64.encode(creds.getBytes());
		
		FakeRequest testRequest = new FakeRequest(Helpers.POST, "/a/user/save").withHeader("Authorization", auth).withJsonBody(json);
		Result result = Helpers.route(testRequest);

		assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
	}

}
