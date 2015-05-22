import static org.fest.assertions.Assertions.assertThat;
import models.info.TParkInfo;
import models.info.TParkInfoPro_Loc;
import models.info.TParkInfoProd;

import org.eclipse.jetty.util.log.Log;
import org.junit.Test;

import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;
import play.test.WithApplication;
import utils.ComResponse;
import utils.CommFindEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.reflect.TypeToken;
import com.ning.http.util.Base64;

import controllers.OrderController;
import controllers.ParkController;

/**
 * Junit test for Usercontroller
 * 
 * @author woderchen
 *
 */
public class ParkingProdControllerTest extends WithApplication {
	// public FakeApplication provideFakeApplication() {
	// return Helpers.fakeApplication(Helpers.inMemoryDatabase(),
	// Helpers.fakeGlobal());
	// }
	String creds = String.format("%s:%s",TestConstants.AUTH_USERNAME, TestConstants.AUTH_PASSWD);
	String auth = "Basic "+ Base64.encode(creds.getBytes());
	
	@Test
	public void copyParkInfoTest() {

		
		FakeRequest testPreParkRequest = new FakeRequest(Helpers.GET, "/a/parkinfo").withHeader("Authorization", auth);
		Result resultPre = Helpers.route(testPreParkRequest);
		
	    assertThat(Helpers.status(resultPre)).isEqualTo(Helpers.OK);
			
	    String con2resultString = Helpers.contentAsString(resultPre);
		
		CommFindEntity<TParkInfo> con2 = OrderController.gsonBuilderWithExpose.fromJson(con2resultString, new TypeToken<CommFindEntity<TParkInfo>>() {  
        }.getType());
		
		if(con2!=null&&con2.getResult()!=null&&con2.getResult().size()>0){
			
			TParkInfo parkInfoPre = con2.getResult().get(0);
			
			FakeRequest testRequest = new FakeRequest(Helpers.GET, "/a/parkinfoprod/copy/"+parkInfoPre.parkId).withHeader("Authorization", auth);
			Result result = Helpers.route(testRequest);

			assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
			
			String jsonStr = Helpers.contentAsString(result);
			System.out.println("json string--------------:"+jsonStr);
			ComResponse<TParkInfoProd> parkInfo = ParkController.gsonBuilderWithExpose.fromJson(jsonStr, new TypeToken<ComResponse<TParkInfoProd>>() {  
	        }.getType());
			TParkInfoProd newParkInfo = parkInfo.getResponseEntity();
			
			Long testParkId = newParkInfo.parkId;
			System.out.println("--------get new ParkID:"+testParkId+",test upload this data-----------");
			
			newParkInfo.detail="每小时8元";
			TParkInfoPro_Loc locUp = newParkInfo.latLngArray.get(0);
			locUp.parkFreeCount = 199;
			
			String updateString=OrderController.gsonBuilderWithExpose.toJson(newParkInfo);
			JsonNode jsonUpdate = Json.parse(updateString);
			FakeRequest testUpdateRequest = new FakeRequest(Helpers.POST, "/a/parkinfoprod/save").withHeader("Authorization", auth).withJsonBody(jsonUpdate);
			Result resultUpdate = Helpers.route(testUpdateRequest);

			assertThat(Helpers.status(resultUpdate)).isEqualTo(Helpers.OK);
			
			
			System.out.println("--------get new ParkID:"+testParkId+",test delete this data-----------");
			FakeRequest testRequest2 = new FakeRequest(Helpers.GET, "/a/parkinfoprod/find/"+testParkId).withHeader("Authorization", auth);
			Result result2 = Helpers.route(testRequest2);		
			assertThat(Helpers.status(result2)).isEqualTo(Helpers.OK);
			
			
			String jsonStr2 = Helpers.contentAsString(result2);
			
//			TParkInfo parkInfo2 = ParkController.gsonBuilderWithExpose.fromJson(jsonStr2, TParkInfo.class);
//			long id = parkInfo2.parkId;
//			FakeRequest test2Request = new FakeRequest(Helpers.GET, "/a/parkinfo/delete/"+id).withHeader("Authorization", auth);
//			Result result3 = Helpers.route(test2Request);		
//			assertThat(Helpers.status(result3)).isEqualTo(Helpers.OK);
			
			
		}else{
			System.out.println("[***WARN***]park table  is null");
		}

		
	}
	
	
	@Test
	public void copyParkInfoToOringalTest() {

		FakeRequest testPreParkRequest = new FakeRequest(Helpers.GET, "/a/parkinfoprod").withHeader("Authorization", auth);
		Result resultPre = Helpers.route(testPreParkRequest);
		
	    assertThat(Helpers.status(resultPre)).isEqualTo(Helpers.OK);
			
	    String con2resultString = Helpers.contentAsString(resultPre);
		
		CommFindEntity<TParkInfoProd> con2 = OrderController.gsonBuilderWithExpose.fromJson(con2resultString, new TypeToken<CommFindEntity<TParkInfoProd>>() {  
        }.getType());
		
		if(con2!=null&&con2.getResult()!=null&&con2.getResult().size()>0){
			
			TParkInfoProd parkInfoProd = con2.getResult().get(0);
			
			FakeRequest testRequest = new FakeRequest(Helpers.GET, "/a/parkinfoprod/copy2orin/"+parkInfoProd.parkId).withHeader("Authorization", auth);
			Result result = Helpers.route(testRequest);

			assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
				
			
		}else{
			System.out.println("[***WARN***]park table  is null");
		}

		
	}
	
	@Test
	public void getAllDataTest(){

		FakeRequest testRequest = new FakeRequest(Helpers.GET, "/a/parkinfoprod").withHeader("Authorization", auth);
		Result result = Helpers.route(testRequest);

		assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
	}
	
	
//	@Test
//	public void addParkLocalTest() {
//
//		TParkInfo_Loc info = new TParkInfo_Loc();
//		info.createPerson="junit";
//		info.isOpen=Constants.PARKING_STATUS_OPEN;
//		info.parkFreeCount=188;
//		info.type=Constants.PARKING_TYPE_IN;
//		info.latitude=99.99;
//		info.longitude=88.88;
//		
//		JsonNode json = Json.toJson(info);
//
//		String creds = String.format("%s:%s","root", "123");
//		String auth = "Basic "+ Base64.encode(creds.getBytes());
//		
//		FakeRequest testRequest = new FakeRequest(Helpers.POST, "/a/user/save").withHeader("Authorization", auth).withJsonBody(json);
//		Result result = Helpers.route(testRequest);
//
//		assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
//	}
	
	

}
