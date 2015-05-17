import static org.fest.assertions.Assertions.assertThat;
import models.info.TParkInfo;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Comment;

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

/**
 * Junit test for Ordercontroller
 * 
 * @author woderchen
 *
 */
public class TestCommentsControllerTest extends WithApplication {
	// public FakeApplication provideFakeApplication() {
	// return Helpers.fakeApplication(Helpers.inMemoryDatabase(),
	// Helpers.fakeGlobal());
	// }
	String creds = String.format("%s:%s",TestConstants.AUTH_USERNAME, TestConstants.AUTH_PASSWD);
	String auth = "Basic "+ Base64.encode(creds.getBytes());
	@Test
	public void addOrderTest() {

		TParkInfo_Comment dataBean = new TParkInfo_Comment();
		
		//--------------parkinginfo--------------
		FakeRequest con1Request = new FakeRequest(Helpers.GET, "/a/parkinfoprod").withHeader("Authorization", auth);
		Result con1result = Helpers.route(con1Request);
		
        assertThat(Helpers.status(con1result)).isEqualTo(Helpers.OK);
		
		String con1resultString = Helpers.contentAsString(con1result);
		System.out.println("con1Request:"+con1resultString);
		CommFindEntity<TParkInfoProd> con1 = OrderController.gsonBuilderWithExpose.fromJson(con1resultString, new TypeToken<CommFindEntity<TParkInfoProd>>() {  
        }.getType());
		
		
		
		if(con1!=null&&con1.getResult()!=null&&con1.getResult().size()>0){
			
			TParkInfoProd part=con1.getResult().get(0);
			
			dataBean.parkComId = 0l;
			dataBean.comments="unit test comments";
			//dataBean.orderDate = currentDate;
			dataBean.createPerson="junit";
			dataBean.rating = 1.5f;
			dataBean.parkInfo = part;

			//-----------------------test insert------------------------------
			String newString=OrderController.gsonBuilderWithExpose.toJson(dataBean);
			JsonNode jsonNew = Json.parse(newString);
			FakeRequest testRequest = new FakeRequest(Helpers.POST, "/a/comments/save").withHeader("Authorization", auth).withJsonBody(jsonNew);
			Result result = Helpers.route(testRequest);

			assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
			
			String jsonStr = Helpers.contentAsString(result);
			
			System.out.println("json string--------------:"+jsonStr);
			ComResponse<TParkInfo_Comment> info = OrderController.gsonBuilderWithExpose.fromJson(jsonStr, new TypeToken<ComResponse<TParkInfo_Comment>>() {  
	        }.getType());
			
			//------------------------------test update-------------------------------------------
			TParkInfo_Comment newData = info.getResponseEntity();
			
			Long testId = newData.parkComId;
			System.out.println("--------get new id:"+testId+",test upload this data-----------");
			
			newData.comments="unit test comments 2";
			String updateString=OrderController.gsonBuilderWithExpose.toJson(newData);
			JsonNode jsonUpdate = Json.parse(updateString);
			FakeRequest testUpdateRequest = new FakeRequest(Helpers.POST, "/a/comments/save").withHeader("Authorization", auth).withJsonBody(jsonUpdate);
			Result resultUpdate = Helpers.route(testUpdateRequest);

			assertThat(Helpers.status(resultUpdate)).isEqualTo(Helpers.OK);
			
			
			System.out.println("--------get update id:"+testId+",test delete this data-----------");
			FakeRequest testRequest2 = new FakeRequest(Helpers.GET, "/a/comments/find/"+testId).withHeader("Authorization", auth);
			Result result2 = Helpers.route(testRequest2);		
			assertThat(Helpers.status(result2)).isEqualTo(Helpers.OK);
			
			
			String jsonStr2 = Helpers.contentAsString(result2);
			
			System.out.println("updated json>>>>>"+jsonStr2);
			
			
//			TParkInfo_Comment Info2 = OrderController.gsonBuilderWithExpose.fromJson(jsonStr2, TParkInfo_Comment.class);
//			long id = Info2.parkComId;
//			FakeRequest test2Request = new FakeRequest(Helpers.GET, "/a/comments/delete/"+id).withHeader("Authorization", auth);
//			Result result3 = Helpers.route(test2Request);		
//			assertThat(Helpers.status(result3)).isEqualTo(Helpers.OK);
			
		}else{
			System.out.println("[***WARN***]park table is null");
		}
		
	}
	
	@Test
	public void getAllDataTest(){

		FakeRequest testRequest = new FakeRequest(Helpers.GET, "/a/comments").withHeader("Authorization", auth);
		Result result = Helpers.route(testRequest);

		assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
	}

}
