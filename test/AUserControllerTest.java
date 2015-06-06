import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;

import models.info.TuserInfo;

import org.junit.Test;

import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;
import play.test.WithApplication;
import utils.ComResponse;
import utils.RoleConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.reflect.TypeToken;
import com.ning.http.util.Base64;

import controllers.ParkController;
import controllers.UserController;

/**
 * Junit test for Usercontroller
 * 
 * @author woderchen
 *
 */
public class AUserControllerTest extends WithApplication {
	// public FakeApplication provideFakeApplication() {
	// return Helpers.fakeApplication(Helpers.inMemoryDatabase(),
	// Helpers.fakeGlobal());
	// }
	String creds = String.format("%s:%s",TestConstants.AUTH_USERNAME, TestConstants.AUTH_PASSWD);
	String auth = "Basic "+ Base64.encode(creds.getBytes());
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

		String newString=UserController.gsonBuilderWithExpose.toJson(userinfo);
		JsonNode json = Json.parse(newString);

		FakeRequest testRequest = new FakeRequest(Helpers.POST, "/a/user/save").withHeader("Authorization", auth).withJsonBody(json);
		Result result = Helpers.route(testRequest);

		assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
		
		String jsonStr = Helpers.contentAsString(result);
		ComResponse<TuserInfo> info = ParkController.gsonBuilderWithExpose.fromJson(jsonStr, new TypeToken<ComResponse<TuserInfo>>() {  
        }.getType());
		
		
		TuserInfo newUser = info.getResponseEntity();
		
		Long testId = newUser.userid;
		System.out.println("--------get new userid:"+testId+",test upload this data-----------");
		
		newUser.userName="junit update";
		
		String updateString=UserController.gsonBuilderWithExpose.toJson(newUser);
		JsonNode jsonUpdate = Json.parse(updateString);
		FakeRequest testUpdateRequest = new FakeRequest(Helpers.POST, "/a/user/save").withHeader("Authorization", auth).withJsonBody(jsonUpdate);
		Result resultUpdate = Helpers.route(testUpdateRequest);

		assertThat(Helpers.status(resultUpdate)).isEqualTo(Helpers.OK);
		
		
		System.out.println("--------get new userid:"+testId+",test delete this data-----------");
		FakeRequest testRequest2 = new FakeRequest(Helpers.GET, "/a/user/find/"+testId).withHeader("Authorization", auth);
		Result result2 = Helpers.route(testRequest2);		
		assertThat(Helpers.status(result2)).isEqualTo(Helpers.OK);
		
		
		String jsonStr2 = Helpers.contentAsString(result2);
		
		System.out.println("updated json>>>>>"+jsonStr2);
		
//		TuserInfo Info2 = ParkController.gsonBuilderWithExpose.fromJson(jsonStr2, TuserInfo.class);
//		long id = Info2.userid;
//		FakeRequest test2Request = new FakeRequest(Helpers.GET, "/a/user/delete/"+id).withHeader("Authorization", auth);
//		Result result3 = Helpers.route(test2Request);		
//		assertThat(Helpers.status(result3)).isEqualTo(Helpers.OK);
	}
	
	@Test
	public void getAllDataTest(){

		FakeRequest testRequest = new FakeRequest(Helpers.GET, "/a/user").withHeader("Authorization", auth);
		Result result = Helpers.route(testRequest);

		assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
	}

}
