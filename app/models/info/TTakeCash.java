package models.info;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_takecash")
public class TTakeCash extends Model{
	
	@Id
	@Expose
	public Long takecashid;
	
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public Double takemoney;
	
	@Constraints.Required
	@Column(nullable = false, length = 100)
	@Size(max = 100)
	@Expose
	public String cardnumber;
	@Constraints.Required
	@Column(nullable = false, length = 100)
	@Size(max = 100)
	@Expose
	public String cardownername;
	@Constraints.Required
	@Column(nullable = false, length = 100)
	@Size(max = 100)
	@Expose
	public String cardname;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date askdata;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date bankHandleData;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date okData;
	//1.申请 2.处理中 3.提现成功,4.不处理
	@Column(columnDefinition = "integer(2) default 0")
	@Expose
	public	int status;
	
	@Column(nullable = false, length = 100)
	@Expose
	public long parkid;
	
	@Column(length = 50)
	@Size(max = 50)
	@Expose
	public String handleName;
	
	@Transient
	@Expose
	public TParkInfoProd parkprod;

	// 查询finder，用于其他方法中需要查询的场景
		public static Finder<Long, TTakeCash> find = new Finder<Long, TTakeCash>(
				Long.class, TTakeCash.class);

		
		/**
		 * 新建或更新数据
		 * 
		 * @param userinfo
		 */
		public static void saveData(final TTakeCash bean) {
			Ebean.execute(new TxRunnable() {
				public void run() {
					// ------------生成主键，所有插入数据的方法都需要这个-----------
					if (bean.takecashid == null || bean.takecashid <= 0) {
						bean.takecashid = TPKGenerator.getPrimaryKey(
								TTakeCash.class.getName(), "takecashid");
						bean.askdata = new Date();
						Ebean.save(bean);
					} else {
						Ebean.update(bean);
					}
					// -------------end----------------
				}
			});

		}

		/**
		 * 删除数据
		 * 
		 * @param id
		 */
		public static void deleteData(Long id) {
			Ebean.delete(TTakeCash.class, id);
		}

		/**
		 * 得到所有数据，有分页
		 * 
		 * @param currentPage
		 * @param pageSize
		 * @param orderBy
		 * @return
		 */
		public static CommFindEntity<TTakeCash> findPageData(int currentPage,
				int pageSize, String orderBy) {

			CommFindEntity<TTakeCash> result = new CommFindEntity<TTakeCash>();

			Page<TTakeCash> allData = find.where().orderBy(orderBy)
					.findPagingList(pageSize).setFetchAhead(false)
					.getPage(currentPage);

			result.setResult(allData.getList());
			result.setRowCount(allData.getTotalRowCount());
			result.setPageCount(allData.getTotalPageCount());
			return result;
		}
		
		/**
		 * 根据车位ID得到所有数据，有分页
		 * 
		 * @param currentPage
		 * @param pageSize
		 * @param orderBy
		 * @return
		 */
		public static CommFindEntity<TTakeCash> findPageData(int currentPage,
				int pageSize, String orderBy,long parkId) {

			CommFindEntity<TTakeCash> result = new CommFindEntity<TTakeCash>();

			Page<TTakeCash> allData = find.where().eq("parkid", parkId).orderBy(orderBy)
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
		 * @return
		 */
		public static Page<TTakeCash> findPageDataForWeb(int currentPage,int pageSize, String orderBy, int status) {

			ExpressionList<TTakeCash> el = find.where();
			if (status>0){
				el.eq("status", status);
			}
			
			Page<TTakeCash> allData = el.orderBy(orderBy)
					.findPagingList(pageSize).setFetchAhead(false)
					.getPage(currentPage);

			List<TTakeCash> tcashList = allData.getList();
			if(tcashList!=null){
				for(TTakeCash cash:tcashList){
					long parkid = cash.parkid;
					cash.parkprod = TParkInfoProd.findDataById(parkid);
				}
			}
			
			return allData;
		}

		
		
		public static TTakeCash findDataById(long id) {
			return find.byId(id);
		}
		
		//得到每一种提现状态的总和
		public static double findTakeCashTotalByStatus(int status) {
			String sql = "SELECT sum(takemoney) as count FROM tb_takecash where status = "+status;
			SqlQuery sq = Ebean.createSqlQuery(sql);
			SqlRow sqlRow = sq.findUnique();
			Double db = sqlRow.getDouble("count");
			return db == null ? 0 : db;
		}
		
		
		public static double findTakeCash(long id) {
			String sql = "SELECT sum(takemoney) as count FROM tb_takecash where parkid="+id+" and status!=4";
			SqlQuery sq = Ebean.createSqlQuery(sql);
			SqlRow sqlRow = sq.findUnique();
			Double db = sqlRow.getDouble("count");
			return db == null ? 0 : db;
		}
}
