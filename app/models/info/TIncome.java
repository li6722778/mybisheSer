package models.info;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.data.format.Formats;
import play.db.ebean.Model;
import utils.Arith;
import utils.CommFindEntity;
import utils.Constants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_income")
public class TIncome extends Model {

	@Expose
	@Id
	public Long incomeId;

	@Expose
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parkId")
	public TParkInfoProd parkInfo;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double incometotal;
	
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double cashtotal;
	
	@Expose
	@Transient
	public double incometoday;
	
	@Expose
	@Transient
	public double incometodaycash;
	
	

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date createDate;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date updateDate;
	
	@Transient
	@Expose
	public int finishedOrder;
	
	@Transient
	@Expose
	public double onlineIncomeTotal;

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TIncome> find = new Finder<Long, TIncome>(
			Long.class, TIncome.class);

	public static TIncome findDataById(long id) {
		return find.fetch("parkInfo").setId(id).findUnique();
	}

	public static int findDoneCount(long parkid) {
		return find.where().eq("parkId", parkid).findRowCount();
	}

	/**
	 * 新建或更新数据
	 * 
	 * @param userinfo
	 */
	public static void saveData(final TIncome bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				// ------------生成主键，所有插入数据的方法都需要这个-----------
				if (bean.incomeId == null || bean.incomeId <= 0) {
					bean.createDate = new Date();
					bean.updateDate = bean.createDate;
					Ebean.save(bean);
				} else {
					bean.updateDate = new Date();
					Ebean.update(bean);
				}
			}
		});
	}
	
	public static double findDonePayment(){
		String sql = "SELECT sum(incometotal) as count FROM tb_income";
		SqlQuery sq = Ebean.createSqlQuery(sql);
		SqlRow sqlRow = sq.findUnique();
		Double db = sqlRow.getDouble("count");
		return  db==null?0: db;
	}

	
	public static Page<TIncome> pageByTypeAndFilter(int currentPage,
			int pageSize, String orderBy,String filter) {

		ExpressionList<TIncome> elist = find.where();

		if (filter!=null&&!filter.trim().equals("")) {
			//elist.eq("parkId", filter);
			 String oql = "find TParkInfoProd(parkId) where TParkInfoProd.parkname like :parkname ";
				   
				 Query<TParkInfoProd> query = Ebean.createQuery(TParkInfoProd.class).select("parkId").where().ilike("parkname", "%"+filter+"%").query();
			     elist.in("t0.parkId", query);
		}
		Page<TIncome> allData = elist.orderBy(orderBy).fetch("parkInfo")
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
		
		if (allData.getList() != null) {
			for (TIncome in : allData.getList()) {
				in.incometoday = getTodayIncome(in.parkInfo.parkId);
				in.finishedOrder = TOrderHis.findAllCountForPark(in.parkInfo.parkId);
				in.onlineIncomeTotal = Arith.decimalPrice((in.incometotal-in.incometodaycash));
			}
		}
		return allData;
	}
	
	/**
	 * 保存对应的收益
	 * 
	 * @param parkid
	 * @param income
	 */
	public static void saveIncome(final long parkid, final double income,final double cash) {
		Ebean.execute(new TxRunnable() {
			public void run() {

				TIncome incometb = findDataByParkid(parkid);

				// ------------生成主键，所有插入数据的方法都需要这个-----------
				if (incometb == null||incometb.incomeId==null||incometb.incomeId<=0) {
					incometb = new TIncome();
					TParkInfoProd parkInfo = new TParkInfoProd();
					parkInfo.parkId = parkid;
					incometb.incomeId = TPKGenerator.getPrimaryKey(
							TIncome.class.getName(), "incomeId");
					incometb.parkInfo = parkInfo;
					incometb.createDate = new Date();
					incometb.updateDate = incometb.createDate;
					incometb.incometotal = income;
					incometb.cashtotal=cash;
					Ebean.save(incometb);
				} else {
					incometb.incometotal = Arith
							.decimalPrice(incometb.incometotal + income);
					incometb.updateDate = new Date();
					Ebean.update(incometb);
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
		Ebean.delete(TIncome.class, id);
	}

	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TIncome> findPageData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TIncome> result = new CommFindEntity<TIncome>();

		Page<TIncome> allData = find.where().orderBy(orderBy).fetch("parkInfo")
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
		if (allData.getList() != null) {
			for (TIncome in : allData.getList()) {
				in.incometoday = getTodayIncome(in.parkInfo.parkId);
				in.onlineIncomeTotal = Arith.decimalPrice((in.incometotal-in.incometodaycash));
			}
		}

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
	public static Page<TIncome> pageByFilter(int currentPage, int pageSize,
			String orderBy, long parkid) {
		ExpressionList<TIncome> elist = find.where();

		if (parkid > 0) {
			elist.eq("parkId", parkid);
		}
		Page<TIncome> allData = elist.orderBy(orderBy).fetch("parkInfo")
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
		
		if (allData.getList() != null) {
			for (TIncome in : allData.getList()) {
				in.incometoday = getTodayIncome(in.parkInfo.parkId);
				in.onlineIncomeTotal = Arith.decimalPrice((in.incometotal-in.incometodaycash));
			}
		}
		return allData;
	}

	/**
	 * 查询parkid对应收益
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static TIncome findDataByParkid(long parkid) {

		List<TIncome> allData = find.fetch("parkInfo").where()
				.eq("t0.parkId", parkid).findList();

		if (allData != null && allData.size() > 0) {
			TIncome income = allData.get(0);
			income.incometoday = getTodayIncome(parkid);
			income.finishedOrder = TOrderHis.findAllCountForPark(parkid);
			income.onlineIncomeTotal = Arith.decimalPrice((income.incometotal-income.incometodaycash));
			return income;
		}

		return  new TIncome();
	}

	
	/**
	 * 得到今天的收益
	 * @param parkId
	 * @return
	 */
	public static double getTodayIncome(long parkId) {
		List<TOrderHis> orders = TOrderHis.findOnlyOrderByParkid(parkId,
				new Date());
		if (orders != null) {
			double todayotal = 0.0d;
			for (TOrderHis his : orders) {
				List<TOrderHis_Py> pys = his.pay;
				if (pys != null) {
					for (TOrderHis_Py py : pys) {
						if (py.ackStatus == Constants.PAYMENT_STATUS_FINISH) {
							todayotal += (py.payActu+py.couponUsed);
						}
						
//						if(py.payMethod==Constants.PAYMENT_TYPE_CASH){
//							incometodaycash;
//						}
					}
				}
			}
			return todayotal;
		} else {
			return 0.0;
		}
	}
}
