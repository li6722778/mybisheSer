package models.info;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import net.sf.cglib.beans.BeanCopier;
import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import utils.Arith;
import utils.Constants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

import controllers.PushController;

@Entity
@Table(name = "tb_counpon_info_his")
public class TCouponHis extends Model{
	
	public static BeanCopier copier = BeanCopier.create(TCouponEntity.class,
			TCouponHis.class, false);
	
	
	@Id
	@Expose
	public Long counponId;
	
	/*@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Id")
	TUseCouponEntity usercoupon;*/
	
	@Expose
	public String counponCode;
	
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public  double  money;
	
	@Column(columnDefinition = "integer default 1")
	@Expose
	public int count;
	
	@Column(columnDefinition = "integer default 1")
	@Expose
	public int scancount;
	
	//1表示启用，0表示关闭,2表示正在使用
	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int isable;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date startDate;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date endDate;
	
	
	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NOT NULL")
	public Date createDate;
	
	@Expose
	public String createName;
	
	
	
	// 查询finder，用于其他方法中需要查询的场景
		public static Finder<Long, TCouponHis> find = new Finder<Long, TCouponHis>(
				Long.class, TCouponHis.class);

		public static TCouponHis findDataById(long id) {
			return find.byId(id);
		}
		
		
		
		public static TCouponHis findentityByCode(String code) {

			List<TCouponHis> userInfos = find.where().eq("counponCode", code).findList();

			return (userInfos == null || userInfos.size() <= 0) ? null : userInfos
					.get(0);
		}
		
		public static Page<TCouponHis> pageByTypeAndFilter(int currentPage,
				int pageSize, String orderBy, String filter) {

			ExpressionList<TCouponHis> elist = find.where();
			
			if (filter != null && !filter.trim().equals("")) {
				elist.ilike("createName", "%" + filter + "%");
			}
			Page<TCouponHis> allData = elist.orderBy(orderBy)
					.findPagingList(pageSize).setFetchAhead(false)
					.getPage(currentPage);

			return allData;
		}
		
		/**
		 * 删除数据
		 * 
		 * @param id
		 */
		public static void deleteData(Long id) {
			Ebean.delete(TCouponEntity.class, id);
		}
		
		
		
		
		/**
		 * 已经用过的优惠劵需要转到历史表
		 * @param orderId
		 */
		public static void moveToHis(final TCouponEntity tcoup){
			Logger.info("move to history table");
			final TCouponHis tcouhis=new TCouponHis();
			Ebean.execute(new TxRunnable() {
				public void run() {
					tcouhis.counponCode=tcoup.counponCode;
					tcouhis.createName=tcoup.createName;
					tcouhis.counponId=tcoup.counponId;
					tcouhis.count=tcoup.count;
					tcouhis.createDate=tcoup.createDate;
					tcouhis.endDate=tcoup.endDate;
					tcouhis.isable=0;
					tcouhis.money=tcoup.money;
					tcouhis.scancount=tcoup.scancount;
					tcouhis.startDate=tcoup.startDate;
					Ebean.save(tcouhis);
					TCouponEntity.deleteData(tcouhis.counponId);	
				}
			});
			
			
			
		}
		
		
}