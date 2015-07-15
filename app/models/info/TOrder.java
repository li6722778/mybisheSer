package models.info;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_order")
public class TOrder extends Model {

	@Id
	@Expose
	public Long orderId;

	@Expose
	public String orderName;

	@Expose
	public String orderCity;
	
	@Column(columnDefinition = "integer(2) default 0")
	@Expose
	public int orderFeeType;

	@Expose
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parkId")
	public TParkInfoProd parkInfo;
	
	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "order")
	@OrderBy("parkPyId DESC")
	@Expose
	public List<TOrder_Py> pay;

	@Expose
	public int orderStatus;
	
	@Expose
	@Column(length = 1000)
	public String orderDetail;
	
	@Expose
	public long couponId;

	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	public Date orderDate;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date startDate;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date endDate;
	
	@Expose
	@Column(columnDefinition = "decimal(20,17) default 0")
	public double latitude;

	@Expose
	@Column(columnDefinition = "decimal(20,17) default 0")
	public double longitude;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userid")
	@Expose
	public TuserInfo userInfo;
	
	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int feeTypeSecInScopeHoursOrder;  

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double feeTypeSecInScopeHourMoneyOrder;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double feeTypeSecOutScopeHourMoneyOrder;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double feeTypefixedHourMoneyOrder;

	@Column(columnDefinition = "integer(2) default 0")
	@Expose
	public int isDiscountAlldayOrder;

	@Column(columnDefinition = "integer(2) default 0")
	@Expose
	public int isDiscountSecOrder;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double discountHourAlldayMoneyOrder;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double discountSecHourMoneyOrder;

	@Formats.DateTime(pattern = "HH:mm:ss")
	@Column(columnDefinition = "time")
	@Expose
	public Date discountSecStartHourOrder;

	@Formats.DateTime(pattern = "HH:mm:ss")
	@Column(columnDefinition = "time")
	@Expose
	public Date discountSecEndHourOrder;

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TOrder> find = new Finder<Long, TOrder>(
			Long.class, TOrder.class);

	public static TOrder findDataById(long id) {
		return find.fetch("userInfo").fetch("pay").fetch("parkInfo").setId(id).findUnique();
	}

	/**
	 * 得到总数
	 * @return
	 */
	public static int findAllCount(){
		return find.findRowCount();
	}
	
	/**
	 * 新建或更新数据
	 * 
	 * @param userinfo
	 */
	public static void saveData(final TOrder bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				// ------------生成主键，所有插入数据的方法都需要这个-----------
				if (bean.orderId == null || bean.orderId <= 0) {
					bean.orderId = TPKGenerator.getPrimaryKey(
							TOrder.class.getName(), "orderId");
					
					bean.orderDate = new Date();
					Ebean.save(bean);
				} else {
					Ebean.update(bean);
				}
				
				if(bean.pay!=null&&bean.pay.size()>0){
					for(TOrder_Py py:bean.pay){
						TOrder order = new TOrder();
						order.orderId = bean.orderId;
						py.order = order;
						TOrder_Py.saveData(py);
					}
				}
			}
		});
	}

	/**
	 * 删除数据
	 * 
	 * @param id
	 */
	public static void deleteData(Long id) {
		Ebean.delete(TOrder.class, id);
	}

	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TOrder> findPageData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TOrder> result = new CommFindEntity<TOrder>();

		Page<TOrder> allData = find.where().orderBy(orderBy).fetch("userInfo").fetch("pay").fetch("parkInfo")
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}
	
	/**
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @param city
	 * @param filter
	 * @return
	 */
	public static Page<TOrder> pageByFilter(int currentPage,int pageSize, String orderBy,String city,String filter) {
		ExpressionList<TOrder> elist = find.where();
		if(city!=null&&!city.trim().equals("")){
			elist.ilike("orderCity", "%"+city+"%");
		}
		if(filter!=null&&!filter.trim().equals("")){
			elist.ilike("orderName", "%"+filter+"%");
		}
		Page<TOrder> allData = elist.orderBy(orderBy).fetch("userInfo").fetch("pay").fetch("parkInfo")
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
        return allData;
    }

	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TOrder> findPageData(int currentPage,
			int pageSize, String orderBy, long userid) {

		CommFindEntity<TOrder> result = new CommFindEntity<TOrder>();

		Page<TOrder> allData = find.fetch("userInfo").fetch("pay").fetch("parkInfo").where().eq("t0.userid", userid)
				.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}
	
	/**
	 * 查询parkid对应订单
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TOrder> findPageDataByparkid(int currentPage,
			int pageSize, String orderBy, long parkid) {

		CommFindEntity<TOrder> result = new CommFindEntity<TOrder>();

		Page<TOrder> allData = find.fetch("userInfo").fetch("pay").fetch("parkInfo").where().eq("parkId", parkid)
				.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}
	
	
	/**
	 * 已经支付但是未出场的车
	 * @param parkid
	 * @return
	 */
	public static  int  findnotcomeincount(long parkid)
	{
		
		String sql = "select  count(order_id) as c FROM tb_order   where order_status=1 and parkId="+parkid+" and start_date is NULL and end_date is NULL  and order_id "
				+ "= (select  distinct orderId from tb_order_py where ack_status=2 and orderId=tb_order.order_id)";

		final RawSql rawSql = RawSqlBuilder.unparsed(sql)
				.columnMapping("c", "countOrder")
		        .create();
		
		Query<ChartCityEntity> query = Ebean.find(ChartCityEntity.class).setRawSql(rawSql);
		
		ChartCityEntity count = query.findUnique();
//		
//		Query<TOrder_Py> query = Ebean.createQuery(TOrder_Py.class).select("order.orderId").setDistinct(true).where().eq("ackStatus", 2).eq("order.orderId","orderId").query();
//		int  count = find.where().eq("parkId",parkid).isNull("startDate").in("orderId", query).findRowCount();	
		
		return (count==null)?0:count.countOrder;
	}
	
	/**
	 * 已经进场但是未出场的数量
	 * @param parkid
	 * @return
	 */
	public static  int  findnotoutincount(long parkid)
	{
		
		String sql = "select  count(order_id) as c FROM tb_order   where order_status=1 and parkId="+parkid+" and start_date is not NULL and end_date is NULL";

		final RawSql rawSql = RawSqlBuilder.unparsed(sql)
				.columnMapping("c", "countOrder")
		        .create();
		
		Query<ChartCityEntity> query = Ebean.find(ChartCityEntity.class).setRawSql(rawSql);
		
		ChartCityEntity count = query.findUnique();
//		
//		Query<TOrder_Py> query = Ebean.createQuery(TOrder_Py.class).select("order.orderId").setDistinct(true).where().eq("ackStatus", 2).eq("order.orderId","orderId").query();
//		int  count = find.where().eq("parkId",parkid).isNull("startDate").in("orderId", query).findRowCount();	
		
		return (count==null)?0:count.countOrder;
	}
	
	/**
	 * 得到所有数据，有分页查询自己订单，2表示付费，1表示未付费
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TOrder> findPageDataByPay(int currentPage,
			int pageSize, String orderBy, String userid, int hasPay) {

//		CommFindEntity<TOrder> result = new CommFindEntity<TOrder>();
//		String sql = "select  * as c FROM tb_order   where userid="+parkid+" and start_date is NULL and end_date is NULL  and order_id "
//				+ "= (select  distinct orderId from tb_order_py where ack_status=2 and orderId=tb_order.order_id)";

//		final RawSql rawSql = RawSqlBuilder.unparsed(sql)
//				.columnMapping("c", "countOrder")
//		        .create();
		
		CommFindEntity<TOrder> result = new CommFindEntity<TOrder>();

		Query<TOrder_Py> query = Ebean.createQuery(TOrder_Py.class).select("order.orderId").setDistinct(true).where().eq("ackStatus",2).query();
		if(hasPay==2)
		{
		Page<TOrder> allData = find.fetch("userInfo").fetch("pay").fetch("parkInfo").where().eq("userInfo.userid", userid).in("orderId", query)
				.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		}else if(hasPay==1)
		{
			Page<TOrder> allData = find.fetch("userInfo").fetch("pay").fetch("parkInfo").where().eq("userInfo.userid", userid).not(Expr.in("orderId", query))
					.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
					.getPage(currentPage);
			result.setResult(allData.getList());
			result.setRowCount(allData.getTotalRowCount());
			result.setPageCount(allData.getTotalPageCount());
		}
		
		
		return result;
	}
	/**
	 * 查询parkid对应yic订单
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TOrder> findPageDataByparkidHasPay(int currentPage,
			int pageSize, String orderBy, long parkid) {

		CommFindEntity<TOrder> result = new CommFindEntity<TOrder>();
		Query<TOrder_Py> query = Ebean.createQuery(TOrder_Py.class).select("order.orderId").setDistinct(true).where().eq("ackStatus",2).query();
		Page<TOrder> allData = find.fetch("userInfo").fetch("pay").fetch("parkInfo").where().eq("parkInfo.parkId", parkid).in("orderId", query)
				.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}

}
