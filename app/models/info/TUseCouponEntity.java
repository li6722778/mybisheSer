package models.info;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import utils.CommFindEntity;
import utils.Constants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

import controllers.LogController;

import java.util.Date;


@Entity
@Table(name = "tb_counpon_use")
public class TUseCouponEntity extends Model{

	@Id
	@Expose
	public Long Id;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userid")
	@Expose
	public TuserInfo userInfo;
	
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "counponId")
	@Expose
	public TCouponEntity counponentity;
	
	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int isable;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date scanDate;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date useDate;
	
	// 查询finder，用于其他方法中需要查询的场景
		public static Finder<Long, TUseCouponEntity> find = new Finder<Long, TUseCouponEntity>(
				Long.class, TUseCouponEntity.class);
		
		
		public static TUseCouponEntity findDataById(long id) {
			return find.fetch("counponentity").fetch("userInfo").setId(id).findUnique();
		}
		
		
		/**
		 * 得到所有数据，有分页
		 * 
		 * @param currentPage
		 * @param pageSize
		 * @param orderBy
		 * @return
		 */
		public static CommFindEntity<TUseCouponEntity> findPageDataByuserid(int currentPage,
				int pageSize, String orderBy, long userid) {

			CommFindEntity<TUseCouponEntity> result = new CommFindEntity<TUseCouponEntity>();

			Page<TUseCouponEntity> allData = find.fetch("userInfo").fetch("counponentity").where().eq("t0.userid", userid)
					.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
					.getPage(currentPage);
		//没有这段，TCouponEntity里的数据就只有id
		
			result.setResult(allData.getList());
			result.setRowCount(allData.getTotalRowCount());
			result.setPageCount(allData.getTotalPageCount());
			return result;
		}
	
		
		/**
		 * 新建或更新数据
		 * 
		 * @param userinfo
		 */
		public static void saveData(final TUseCouponEntity bean) {
			Ebean.execute(new TxRunnable() {
				public void run() {
					// ------------生成主键，所有插入数据的方法都需要这个-----------
					if (bean.Id==null || bean.Id <= 0) {
						bean.Id= TPKGenerator.getPrimaryKey(
								TUseCouponEntity.class.getName(), "Id");
						Logger.debug("TUseCouponEntity id"+bean.Id);
						Ebean.save(bean);
						} else {
						Ebean.update(bean);
					}
				}
			});
		}
	
	
	
	

}