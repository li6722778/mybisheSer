package models.info;

import java.util.ArrayList;
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

import net.sf.cglib.beans.BeanCopier;
import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;
import utils.Arith;
import utils.CommFindEntity;
import utils.Constants;
import utils.DateHelper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_order_his")
public class TOrderHis extends Model {
	public static BeanCopier copier = BeanCopier.create(TOrder.class,
			TOrderHis.class, false);
	public static BeanCopier copierPy = BeanCopier.create(TOrder_Py.class,
			TOrderHis_Py.class, false);
	@Id
	@Expose
	public Long orderId;

	@Expose
	public String orderName;

	@Expose
	public String orderCity;

	@Expose
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parkId")
	public TParkInfoProd parkInfo;
	
	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "order")
	@OrderBy("parkPyId DESC")
	@Expose
	public List<TOrderHis_Py> pay;

	@Expose
	public int orderStatus;
	
	@Expose
	@Column(length = 1000)
	public String orderDetail;
	
	@Expose
	public long couponId;

	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NOT NULL")
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

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TOrderHis> find = new Finder<Long, TOrderHis>(
			Long.class, TOrderHis.class);

	public static TOrderHis findDataById(long id) {
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
	 * 得到停车场的完成订单数
	 * @param parkId
	 * @return
	 */
	public static int findAllCountForPark(long parkId){
		return find.where().eq("parkid", parkId).eq("orderStatus", 2).findRowCount();
	}
	
	/**
	 * 新建或更新数据
	 * 
	 * @param userinfo
	 */
	public static void saveDataWithoutIDPolicy(final TOrderHis bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {
					Ebean.save(bean);
					
					if(bean.pay!=null&&bean.pay.size()>0){
						for(TOrderHis_Py py:bean.pay){
							TOrderHis order = new TOrderHis();
							order.orderId = bean.orderId;
							py.order = order;
							TOrderHis_Py.saveDataWithoutIDPolicy(py);
						}
					}
			}
		});
	}
	
	/**
	 * 已经完成的订单需要转到历史表
	 * @param orderId
	 */
	public static void moveToHisFromOrder(final Long orderId, final int status){
		Logger.info("move to history table");
		
		Ebean.execute(new TxRunnable() {
			public void run() {
				
				TOrder order = TOrder.findDataById(orderId);
				if(order!=null){
					Logger.debug(">>>>order moving start");
					
					order.endDate = new Date();
					order.orderStatus = status;
					
					TOrderHis orderHis = new TOrderHis();
					copier.copy(order, orderHis, null);
					
					List<TOrderHis_Py> pyArray = new ArrayList<TOrderHis_Py>();
					orderHis.pay = pyArray;
					
					if(order.pay!=null){
						Logger.debug(">>>>>>>>>>>TOrder_Py moving start");
						for(TOrder_Py py:order.pay){
							TOrderHis_Py hisPy = new TOrderHis_Py();
							copierPy.copy(py, hisPy, null);
							hisPy.order = orderHis;
							pyArray.add(hisPy);
							if(hisPy.ackStatus == Constants.PAYMENT_STATUS_FINISH){
							   TIncome.saveIncome(order.parkInfo.parkId, Arith.decimalPrice(hisPy.payActu+hisPy.couponUsed));
							}
						}
						Logger.debug(">>>>>>>>>>>TOrder_Py moving end");
					}
					saveDataWithoutIDPolicy(orderHis);
					Logger.debug(">>>>order moving end");

					if(order.couponId>0){ //不能删除优惠劵，只是标记为使用
						
					    TUseCouponEntity.setUseCoupon(order.couponId, order.userInfo.userid);
					    Logger.debug(">>>>delete used coupon");
					}
					
					TOrder.deleteData(orderId);
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
		Ebean.delete(TOrderHis.class, id);
	}

	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TOrderHis> findPageData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TOrderHis> result = new CommFindEntity<TOrderHis>();

		Page<TOrderHis> allData = find.where().orderBy(orderBy).fetch("userInfo").fetch("pay").fetch("parkInfo")
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
	public static Page<TOrderHis> pageByFilter(int currentPage,int pageSize, String orderBy,String city,String filter) {
		ExpressionList<TOrderHis> elist = find.where();
		if(city!=null&&!city.trim().equals("")){
			elist.ilike("orderCity", "%"+city+"%");
		}
		if(filter!=null&&!filter.trim().equals("")){
			elist.ilike("orderName", "%"+filter+"%");
		}
		Page<TOrderHis> allData = elist.orderBy(orderBy).fetch("userInfo").fetch("pay").fetch("parkInfo")
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
        return allData;
    }

	/**
	 * 得到停车场下的的所有订单
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @param parkId
	 * @param filter
	 * @return
	 */
	public static Page<TOrderHis> pageByFilterForPark(int currentPage,int pageSize, String orderBy,long parkId,String filter) {
		ExpressionList<TOrderHis> elist = find.where();
		
		elist.eq("parkId", parkId);
		
		if(filter!=null&&!filter.trim().equals("")){
			elist.ilike("parkId", "%"+filter+"%");
		}
		Page<TOrderHis> allData = elist.orderBy(orderBy).fetch("userInfo").fetch("pay").fetch("parkInfo")
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
	public static CommFindEntity<TOrderHis> findPageData(int currentPage,
			int pageSize, String orderBy, long userid) {

		CommFindEntity<TOrderHis> result = new CommFindEntity<TOrderHis>();

		Page<TOrderHis> allData = find.fetch("userInfo").fetch("pay").fetch("parkInfo").where().eq("t0.userid", userid)
				.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}
	/**
	 * 得到所有数据
	 * @param parkid
	 * @return
	 */
	public static List<TOrderHis> findOnlyOrderByParkid( long parkid,Date date) {
		
//		String sql = "select * from chebole.tb_order where parkId=:parkid and date(order_date) = curdate();";
		
		 List<TOrderHis> allData = find.fetch("pay").where().eq("parkid", parkid).eq("date(order_date)", DateHelper.format(new Date(), "yyyyMMdd")).findList();
		
		
		return allData;
	}
	/**
	 * 得到所有数据
	 * @param parkid
	 * @return
	 */
	public static CommFindEntity<TOrderHis> findPageDataByparkid(int currentPage,
			int pageSize, String orderBy, long parkid) {

		CommFindEntity<TOrderHis> result = new CommFindEntity<TOrderHis>();

		Page<TOrderHis> allData = find.fetch("userInfo").fetch("pay").fetch("parkInfo").where().eq("parkId", parkid)
				.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}
}
