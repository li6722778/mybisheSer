import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import models.info.TParkInfo;
import models.info.TParkInfo_Img;
import models.info.TParkInfo_Loc;

import org.eclipse.jetty.util.log.Log;
import org.junit.Test;

import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;
import play.test.WithApplication;
import utils.ComResponse;
import utils.Constants;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.reflect.TypeToken;
import com.ning.http.util.Base64;

import controllers.ParkController;

/**
 * Junit test for Usercontroller
 * 
 * @author woderchen
 *
 */
public class ParkingControllerTest extends WithApplication {
	// public FakeApplication provideFakeApplication() {
	// return Helpers.fakeApplication(Helpers.inMemoryDatabase(),
	// Helpers.fakeGlobal());
	// }
	String creds = String.format("%s:%s",TestConstants.AUTH_USERNAME, TestConstants.AUTH_PASSWD);
	String auth = "Basic "+ Base64.encode(creds.getBytes());
	
	@Test
	public void addParkInfoTest() {

		TParkInfo info = new TParkInfo();
		info.createPerson="junit";
		info.address="#3,Xixin road Chengdu Sichuan";
		info.detail="每小时3元";
		info.discountHourAlldayMoney=0.5;
		info.discountSecHourMoney=1;
		info.feeTypefixedHourMoney=2;
		info.feeTypeSecInScopeHours=3;
		info.feeTypeSecOutScopeHourMoney=5;
		info.isDiscountAllday=1;
		info.isDiscountSec=1;
		info.owner="chenxp";
		info.ownerPhone=13551190701l;
		info.parkname="unit test 停车场";
		info.vender="成都市高新停车场管理公司";
		info.venderBankName="中国建设银行";
		info.venderBankNumber="12345678909876";


		/*图片路径*/
		TParkInfo_Img img = new TParkInfo_Img();
		img.imgUrlHeader="http://localhost:9001/";
		img.imgUrlPath="2015/05/01/pic1.jpg";
		img.detail="地下车库";
		
		TParkInfo_Img img2 = new TParkInfo_Img();
		img2.imgUrlHeader="http://localhost:9002/";
		img2.imgUrlPath="2015/05/02/pic2.jpg";
		img2.detail="标准车库";
		
		List<TParkInfo_Img> imgArray = new ArrayList<>();
		imgArray.add(img);
		imgArray.add(img2);
		info.imgUrlArray = imgArray;
		
		/*坐标上传*/
		TParkInfo_Loc loc = new TParkInfo_Loc();
		loc.createPerson="junit";
		loc.isOpen=Constants.PARKING_STATUS_OPEN;
		loc.parkFreeCount=55;
		loc.type=Constants.PARKING_TYPE_IN;
		loc.latitude=55.99;
		loc.longitude=55.88;
		
		TParkInfo_Loc loc2 = new TParkInfo_Loc();
		loc2.createPerson="junit";
		loc2.isOpen=Constants.PARKING_STATUS_CLOSE;
		loc2.parkFreeCount=66;
		loc2.type=Constants.PARKING_TYPE_OUT;
		loc2.latitude=44.99;
		loc2.longitude=44.88;
		List<TParkInfo_Loc> locArray = new ArrayList<>();
		locArray.add(loc);
		locArray.add(loc2);
		info.latLngArray = locArray;
		
		JsonNode json = Json.toJson(info);

		FakeRequest testRequest = new FakeRequest(Helpers.POST, "/a/parkinfo/save").withHeader("Authorization", auth).withJsonBody(json);
		Result result = Helpers.route(testRequest);

		assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
		
		String jsonStr = Helpers.contentAsString(result);
		ComResponse<TParkInfo> parkInfo = ParkController.gsonBuilderWithExpose.fromJson(jsonStr, new TypeToken<ComResponse<TParkInfo>>() {  
        }.getType());
		TParkInfo newParkInfo = parkInfo.getResponseEntity();
		
		Long testParkId = newParkInfo.parkId;
		Log.info("--------get new ParkID:"+testParkId+",test upload this data-----------");
		
		newParkInfo.detail="每小时8元";
		TParkInfo_Loc locUp = newParkInfo.latLngArray.get(0);
		locUp.parkFreeCount = 199;
		JsonNode jsonUpdate = Json.toJson(newParkInfo);
		FakeRequest testUpdateRequest = new FakeRequest(Helpers.POST, "/a/parkinfo/save").withHeader("Authorization", auth).withJsonBody(jsonUpdate);
		Result resultUpdate = Helpers.route(testUpdateRequest);

		assertThat(Helpers.status(resultUpdate)).isEqualTo(Helpers.OK);
		
		
		Log.info("--------get new ParkID:"+testParkId+",test delete this data-----------");
		FakeRequest testRequest2 = new FakeRequest(Helpers.GET, "/a/parkinfo/find/"+testParkId).withHeader("Authorization", auth);
		Result result2 = Helpers.route(testRequest2);		
		assertThat(Helpers.status(result2)).isEqualTo(Helpers.OK);
		
		
		String jsonStr2 = Helpers.contentAsString(result2);
		
		TParkInfo parkInfo2 = ParkController.gsonBuilderWithExpose.fromJson(jsonStr2, TParkInfo.class);
		long id = parkInfo2.parkId;
		FakeRequest test2Request = new FakeRequest(Helpers.GET, "/a/parkinfo/delete/"+id).withHeader("Authorization", auth);
		Result result3 = Helpers.route(test2Request);		
		assertThat(Helpers.status(result3)).isEqualTo(Helpers.OK);
	}
	
	@Test
	public void getAllDataTest(){

		FakeRequest testRequest = new FakeRequest(Helpers.GET, "/a/parkinfo").withHeader("Authorization", auth);
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
