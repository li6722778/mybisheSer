import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import models.ChebolePayOptions;
import models.info.TOrder;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Py;
import models.info.TuserInfo;

import org.junit.Test;

import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;
import play.test.WithApplication;
import utils.ComResponse;
import utils.CommFindEntity;
import utils.Constants;

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
public class TestOrderControllerTest extends WithApplication {
	// public FakeApplication provideFakeApplication() {
	// return Helpers.fakeApplication(Helpers.inMemoryDatabase(),
	// Helpers.fakeGlobal());
	// }
	String creds = String.format("%s:%s",TestConstants.AUTH_USERNAME, TestConstants.AUTH_PASSWD);
	String auth = "Basic "+ Base64.encode(creds.getBytes());
	@Test
	public void addOrderTest() {

		TOrder dataBean = new TOrder();
		
		//--------------parkinginfo--------------
		FakeRequest con1Request = new FakeRequest(Helpers.GET, "/a/parkinfoprod").withHeader("Authorization", auth);
		Result con1result = Helpers.route(con1Request);
		
        assertThat(Helpers.status(con1result)).isEqualTo(Helpers.OK);
		
		String con1resultString = Helpers.contentAsString(con1result);
		System.out.println("con1Request:"+con1resultString);
		CommFindEntity<TParkInfoProd> con1 = OrderController.gsonBuilderWithExpose.fromJson(con1resultString, new TypeToken<CommFindEntity<TParkInfoProd>>() {  
        }.getType());
		
		
		//---------------------userinfo-------------
		FakeRequest con2Request = new FakeRequest(Helpers.GET, "/a/user").withHeader("Authorization", auth);
		Result con2result = Helpers.route(con2Request);
		
        assertThat(Helpers.status(con2result)).isEqualTo(Helpers.OK);
		
		String con2resultString = Helpers.contentAsString(con2result);
		System.out.println("con2Request:"+con2resultString);
		CommFindEntity<TuserInfo> con2 = OrderController.gsonBuilderWithExpose.fromJson(con2resultString, new TypeToken<CommFindEntity<TuserInfo>>() {  
        }.getType());
		
		if(con1!=null&&con1.getResult()!=null&&con1.getResult().size()>0&&con2!=null&&con2.getResult()!=null&&con2.getResult().size()>0){
			
			TParkInfoProd part=con1.getResult().get(0);
			TuserInfo user = con2.getResult().get(0);
			
			dataBean.orderId = 0l;
			dataBean.orderCity="沈阳";
			//dataBean.orderDate = currentDate;
			dataBean.orderName="unit test order";
			dataBean.orderStatus = Constants.ORDER_TYPE_START;
			dataBean.parkInfo = part;
			dataBean.userInfo = user;
			
			TParkInfo_Py py = new TParkInfo_Py();
			py.payActu=33.3;
			py.payMethod=1;
			py.payTotal=44.4;
			
			TParkInfo_Py py2 = new TParkInfo_Py();
			py2.payActu=22;
			py.payMethod=9;
			py2.payTotal=22;
			
			List<TParkInfo_Py> pays= new ArrayList<TParkInfo_Py>();
			pays.add(py);
			pays.add(py2);
			dataBean.pay=pays;

			//-----------------------test insert------------------------------
			String newString=OrderController.gsonBuilderWithExpose.toJson(dataBean);
			JsonNode jsonNew = Json.parse(newString);
			FakeRequest testRequest = new FakeRequest(Helpers.POST, "/a/order/save").withHeader("Authorization", auth).withJsonBody(jsonNew);
			Result result = Helpers.route(testRequest);

			assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
			
			String jsonStr = Helpers.contentAsString(result);
			
			System.out.println("json string--------------:"+jsonStr);
			ComResponse<TOrder> info = OrderController.gsonBuilderWithExpose.fromJson(jsonStr, new TypeToken<ComResponse<TOrder>>() {  
	        }.getType());
			
			//------------------------------test update-------------------------------------------
			TOrder newData = info.getResponseEntity();
			
			Long testId = newData.orderId;
			System.out.println("--------get new id:"+testId+",test upload this data-----------");
			
			newData.orderName="junit test order 2";
			String updateString=OrderController.gsonBuilderWithExpose.toJson(newData);
			JsonNode jsonUpdate = Json.parse(updateString);
			FakeRequest testUpdateRequest = new FakeRequest(Helpers.POST, "/a/order/save").withHeader("Authorization", auth).withJsonBody(jsonUpdate);
			Result resultUpdate = Helpers.route(testUpdateRequest);

			assertThat(Helpers.status(resultUpdate)).isEqualTo(Helpers.OK);
			

			System.out.println("--------get new userid:"+testId+",test delete this data-----------");
			FakeRequest testRequest2 = new FakeRequest(Helpers.GET, "/a/order/find/"+testId).withHeader("Authorization", auth);
			Result result2 = Helpers.route(testRequest2);		
			assertThat(Helpers.status(result2)).isEqualTo(Helpers.OK);
			
			
			String jsonStr2 = Helpers.contentAsString(result2);
			
			System.out.println("updated json>>>>>"+jsonStr2);
			
//			
//			TOrder Info2 = OrderController.gsonBuilderWithExpose.fromJson(jsonStr2, TOrder.class);
//			long id = Info2.orderId;
//			FakeRequest test2Request = new FakeRequest(Helpers.GET, "/a/order/delete/"+id).withHeader("Authorization", auth);
//			Result result3 = Helpers.route(test2Request);		
//			assertThat(Helpers.status(result3)).isEqualTo(Helpers.OK);
			

		}else{
			System.out.println("[***WARN***]park table or user table is null");
		}
		
	}

	@Test
	public void getAllDataTest(){

		FakeRequest testRequest = new FakeRequest(Helpers.GET, "/a/order").withHeader("Authorization", auth);
		Result result = Helpers.route(testRequest);

		assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
	}

}
