import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import models.ChebolePayOptions;
import models.info.TOrder;
import models.info.TParkInfoProd;
import models.info.TParkInfo_Py;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.reflect.TypeToken;
import com.ning.http.util.Base64;

import controllers.OrderController;
import controllers.PayController;
import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;
import play.test.WithApplication;
import utils.ComResponse;
import utils.CommFindEntity;
import utils.Constants;


public class TestOrderPaymentControllerTest extends WithApplication{

	String creds = String.format("%s:%s",TestConstants.AUTH_USERNAME, TestConstants.AUTH_PASSWD);
	String auth = "Basic "+ Base64.encode(creds.getBytes());
	
	@Test
	public void testPayOrder(){
		
		        //--------------parkinginfo--------------
				FakeRequest con1Request = new FakeRequest(Helpers.GET, "/a/parkinfoprod").withHeader("Authorization", auth);
				Result con1result = Helpers.route(con1Request);
				
		        assertThat(Helpers.status(con1result)).isEqualTo(Helpers.OK);
				
				String con1resultString = Helpers.contentAsString(con1result);
//				System.out.println("con1Request:"+con1resultString);
				CommFindEntity<TParkInfoProd> con1 = OrderController.gsonBuilderWithExpose.fromJson(con1resultString, new TypeToken<CommFindEntity<TParkInfoProd>>() {  
		        }.getType());
				if(con1!=null&&con1.getResult()!=null&&con1.getResult().size()>0){
					
					TParkInfoProd part=con1.getResult().get(0);
					
					//-------------------------------get payment info-----------------------------------
					FakeRequest con2Request = new FakeRequest(Helpers.GET, "/a/pay/price/"+part.parkId);
					Result con2result = Helpers.route(con2Request);
					
			        assertThat(Helpers.status(con2result)).isEqualTo(Helpers.OK);
			        String chebolePayOptionsString = Helpers.contentAsString(con2result);
			        
			        System.out.println("get pay info::::::::::::::::::::::::::"+chebolePayOptionsString);
			        
			        ComResponse<ChebolePayOptions> payOrderEntity = OrderController.gsonBuilderWithExpose.fromJson(chebolePayOptionsString, new TypeToken<ComResponse<ChebolePayOptions>>() {  
			        }.getType());
					
			        ChebolePayOptions options = payOrderEntity.getResponseEntity();
					
			        
			        String optionString = PayController.gsonBuilderWithExpose.toJson(options);
			        JsonNode jsonUpdate = Json.parse(optionString);
					FakeRequest testPayRequest = new FakeRequest(Helpers.POST, "/a/pay/in/"+part.parkId+"?c=成都").withJsonBody(jsonUpdate).withHeader("Authorization", auth);
					Result resultPayOrder = Helpers.route(testPayRequest);
					assertThat(Helpers.status(resultPayOrder)).isEqualTo(Helpers.OK);
					
					String payInfo = Helpers.contentAsString(resultPayOrder);
					
					ComResponse<ChebolePayOptions> payOrderEntity2 = OrderController.gsonBuilderWithExpose.fromJson(payInfo, new TypeToken<ComResponse<ChebolePayOptions>>() {  
			        }.getType());
					
					ChebolePayOptions chebole = payOrderEntity2.getResponseEntity();
					System.out.println("alibaba pay info::::::::::::::::::::::::::"+chebole);
					
					assertFalse(chebole==null);
					assertFalse(chebole.payInfo.isEmpty());
				}else{
					System.out.println("[***WARN***]park table is null");
				}
		
	}
	
	@Test
	public void testPayOrderUpdate(){
		
		//--------------parkinginfo--------------
				FakeRequest testRequest2 = new FakeRequest(Helpers.GET, "/a/order").withHeader("Authorization", auth);
				Result result2 = Helpers.route(testRequest2);		
				assertThat(Helpers.status(result2)).isEqualTo(Helpers.OK);
				
				String con2resultString = Helpers.contentAsString(result2);
				System.out.println("con2Request:"+con2resultString);
				CommFindEntity<TOrder> con1 = OrderController.gsonBuilderWithExpose.fromJson(con2resultString, new TypeToken<CommFindEntity<TOrder>>() {  
		        }.getType());
				
			
				if(con1!=null&&con1.getResult()!=null&&con1.getResult().size()>0){
					
					TOrder part=con1.getResult().get(0);
					
					if(part.pay!=null&&part.pay.size()>0){
						TParkInfo_Py payment = part.pay.get(0);
						
						FakeRequest testPayRequest = new FakeRequest(Helpers.POST, "/a/pay/update/"+payment.parkPyId+"/"+Constants.ORDER_TYPE_PENDING+"").withHeader("Authorization", auth);
						Result resultPayOrder = Helpers.route(testPayRequest);
						assertThat(Helpers.status(resultPayOrder)).isEqualTo(Helpers.OK);
						
						FakeRequest testPayRequest2 = new FakeRequest(Helpers.POST, "/a/pay/update/"+payment.parkPyId+"/"+Constants.ORDER_TYPE_FINISH+"").withHeader("Authorization", auth);
						Result resultPayOrder2 = Helpers.route(testPayRequest2);
						assertThat(Helpers.status(resultPayOrder2)).isEqualTo(Helpers.OK);
						
					}else{
						System.out.println("[***WARN***]TParkInfo_Py table is null");
					}

				}else{
					System.out.println("[***WARN***]order table is null");
				}
		
	}
}
