package models.info;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_counpon_info")
public class TCouponEntity extends Model{
	
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
	
	//1表示启用，0表示关闭
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
	public static Finder<Long, TCouponEntity> find = new Finder<Long, TCouponEntity>(
			Long.class, TCouponEntity.class);

	public static TCouponEntity findDataById(long id) {
		return find.byId(id);
	}
	
	
	
	public static TCouponEntity findentityByCode(String code) {

		List<TCouponEntity> userInfos = find.where().eq("counponCode", code).findList();

		return (userInfos == null || userInfos.size() <= 0) ? null : userInfos
				.get(0);
	}
	
	public static Page<TCouponEntity> pageByTypeAndFilter(int currentPage,
			int pageSize, String orderBy, String filter) {

		ExpressionList<TCouponEntity> elist = find.where();
		
		if (filter != null && !filter.trim().equals("")) {
			elist.ilike("createName", "%" + filter + "%");
		}
		Page<TCouponEntity> allData = elist.orderBy(orderBy)
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
	 * 新建或更新数据
	 * 
	 * @param userinfo
	 */
	public static void saveData(final TCouponEntity bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				// ------------生成主键，所有插入数据的方法都需要这个-----------
				if (bean.counponId == null || bean.counponId <= 0) {
					
					bean.counponId= TPKGenerator.getPrimaryKey(
							TCouponEntity.class.getName(), "counponId");
					Logger.debug("TUseCouponEntity id"+bean.counponId);
					bean.createDate = new Date();
					Ebean.save(bean);
					
					} else {
					Ebean.update(bean);
				}
			}
		});
	}
	
	public static void updateUseable(final TCouponEntity bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				// ------------生成主键，所有插入数据的方法都需要这个-----------
				Set<String> upobj = new HashSet<String>();
				upobj.add("isable");
			    Ebean.update(bean,upobj);
				
			}
		});
	}
	
}
