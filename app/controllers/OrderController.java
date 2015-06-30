package controllers;

import java.util.Date;

import models.info.TIncome;
import models.info.TOrder;
import models.info.TOrderHis;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ComResponse;
import utils.CommFindEntity;
import utils.Constants;
import action.BasicAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OrderController extends Controller {
	public static Gson gsonBuilderWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	/**
	 * 根据主键ID，得到数据
	 * @param userid
	 * @return
	 */
	@BasicAuth
	public static Result getDataById(Long id) {
		Logger.info("start to get data");
		String json = gsonBuilderWithExpose.toJson(TOrder.findDataById(id));
		JsonNode jsonNode = Json.parse(json);
		Logger.debug("got Data:" + json);
		return ok(jsonNode);
	}

	/**
	 * 得到所有的数据，这里是查询出所有的数据，如果有其他条件，需要仿照TOrder.findData写一些方法
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	@BasicAuth
	public static Result getAllData(int currentPage, int pageSize,
			String orderBy) {
		Logger.info("start to all data");
		CommFindEntity<TOrder> allData = TOrder.findPageData(currentPage,
				pageSize, orderBy);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	
	/**
	 * 得到所有的数据，这里是查询出所有的数据，如果有其他条件，需要仿照TOrder.findData写一些方法
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	@BasicAuth
	public static Result getAllDataForSelf(int currentPage, int pageSize,
			String orderBy) {
		Logger.info("start to all data");
		
		String idString = flash("userid");
		Logger.info("get session value for userid:"+idString);
		long id = 0l;
		try{
			id = Long.parseLong(idString);
		}catch(Exception e){
			Logger.error("",e);
		}
		CommFindEntity<TOrder> allData = TOrder.findPageData(currentPage,
				pageSize, orderBy,id);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	
	@BasicAuth
	public static Result getAllHisDataForSelf(int currentPage, int pageSize,
			String orderBy) {
		Logger.info("start to getAllHisDataForSelf");
		
		String idString = flash("userid");
		Logger.info("get session value for userid:"+idString);
		long id = 0l;
		try{
			id = Long.parseLong(idString);
		}catch(Exception e){
			Logger.error("",e);
		}
		CommFindEntity<TOrderHis> allData = TOrderHis.findPageData(currentPage,
				pageSize, orderBy,id);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	
	@BasicAuth
	public static Result getAllDataByparkid(int currentPage, int pageSize,
			String orderBy,long parkid) {
		Logger.info("start to getAllHisDataForSelf");
		
		String idString = flash("userid");
		Logger.info("get session value for userid:"+idString);
		long id = 0l;
		try{
			id = Long.parseLong(idString);
		}catch(Exception e){
			Logger.error("",e);
		}
		CommFindEntity<TOrder> allData = TOrder.findPageDataByparkid(currentPage,
				pageSize, orderBy,parkid);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	
	@BasicAuth
	public static Result getAllHistDataByparkid(int currentPage, int pageSize,
			String orderBy,long parkid) {
		Logger.info("start to getAllHisDataForSelf");
		
		String idString = flash("userid");
		Logger.info("get session value for userid:"+idString);
		long id = 0l;
		try{
			id = Long.parseLong(idString);
		}catch(Exception e){
			Logger.error("",e);
		}
		CommFindEntity<TOrderHis> allData = TOrderHis.findPageDataByparkid(currentPage,
				pageSize, orderBy,parkid);
		String json = gsonBuilderWithExpose.toJson(allData);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("CommFindEntity result:" + json);
		return ok(jsonNode);
	}
	

	/**
	 * 新建或更新数据，ID不为空或大于0，则是更新数据；否则则是新建数据
	 * @return
	 */
	@BasicAuth
	public static Result saveData() {
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		TOrder data = gsonBuilderWithExpose.fromJson(request, TOrder.class);
		ComResponse<TOrder>  response = new ComResponse<TOrder>();
		try {
			TOrder.saveData(data);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(data);
			response.setExtendResponseContext("更新数据成功.");
			LogController.info("save order data:"+data.orderName);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	
	@BasicAuth
	public static Result savestartDate(){
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		TOrder data = gsonBuilderWithExpose.fromJson(request, TOrder.class);
		ComResponse<TOrder>  response = new ComResponse<TOrder>();
		try {
			data.startDate = new Date();
			TOrder.saveData(data);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(data);
			response.setExtendResponseContext("更新订单开始时间成功.");
			LogController.info("save start time:"+data.orderName);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	@BasicAuth
	public static Result saveendDate(){
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		TOrder data = gsonBuilderWithExpose.fromJson(request, TOrder.class);
		ComResponse<TOrder>  response = new ComResponse<TOrder>();
		try {
			data.endDate = new Date();
			data.orderStatus = Constants.ORDER_TYPE_FINISH;
			TOrder.saveData(data);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(data);
			response.setExtendResponseContext("更新订单结束时间.");
			LogController.info("save end time:"+data.orderName);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	/**
	 * 激活订单
	 * @return
	 */
	@BasicAuth
	public static Result restartorder(){
		String request = request().body().asJson().toString();
		Logger.info("start to post data:" + request);
		
		TOrder data = gsonBuilderWithExpose.fromJson(request, TOrder.class);
		ComResponse<TOrder>  response = new ComResponse<TOrder>();
		try {
			//data.endDate = new Date();
			data.orderStatus = Constants.ORDER_TYPE_START;
			TOrder.saveData(data);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setResponseEntity(data);
			response.setExtendResponseContext("更新数据成功.");
			LogController.info("open valid order manually for "+data.orderName);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	
	/**
	 * 根据ID删除数据，如果有其他条件，就需要仿照TOrder.deleteData，写类似的方法
	 * @param id
	 * @return
	 */
	@BasicAuth
	public static Result deleteData(long id){
		Logger.info("start to delete data:" + id);
		ComResponse<TOrder>  response = new ComResponse<TOrder>();
		try {
			TOrder.deleteData(id);
			response.setResponseStatus(ComResponse.STATUS_OK);
			response.setExtendResponseContext("删除数据成功.");
			LogController.info("delete order:"+id);
		} catch (Exception e) {
			response.setResponseStatus(ComResponse.STATUS_FAIL);
			response.setErrorMessage(e.getMessage());
			Logger.error("", e);
		}
		String tempJsonString = gsonBuilderWithExpose.toJson(response);
		JsonNode json = Json.parse(tempJsonString);
		return ok(json);
	}
	
	/**
	 * 得到停车场的收益
	 * @param parkId
	 * @return
	 */
	public static Result getAllIncomeData(long parkId) {
		Logger.info("getAllIncomeData");
		TIncome income = TIncome.findDataByParkid(parkId);
		String json = gsonBuilderWithExpose.toJson(income);
		JsonNode jsonNode = Json.parse(json);
		// String jsonString = Json.stringify(json);
		Logger.debug("income result:" + json);
		return ok(jsonNode);
	}
}
