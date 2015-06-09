package models.info;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;
import utils.CommFindEntity;
import utils.Constants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
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

	@Expose
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parkId")
	public TParkInfoProd parkInfo;
	
	@OneToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@Expose
	public TParkInfo_Py pay;

	@Expose
	public int orderStatus;

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

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userid")
	@Expose
	public TuserInfo userInfo;

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
	public static int findDoneCount(){
		return find.where().eq("orderStatus", Constants.ORDER_TYPE_FINISH).findRowCount();
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
					
					if(bean.pay!=null&&(bean.pay.parkPyId==null||bean.pay.parkPyId<=0)){
						bean.pay.parkPyId = TPKGenerator.getPrimaryKey(TParkInfo_Py.class.getName(), "parkPyId");
//						bean.pay.torder = bean;
						bean.pay.createPerson=bean.userInfo==null?"":bean.userInfo.userName;
					}
					bean.orderDate = new Date();
					Ebean.save(bean);
				} else {
					Ebean.update(bean);
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

		Page<TOrder> allData = find.fetch("userInfo").fetch("pay").fetch("parkInfo").where().eq("userid", userid)
				.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}
}
