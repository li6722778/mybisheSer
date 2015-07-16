package models.info;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_counpon_use")
public class TUseCouponEntity extends Model {

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
		return find.fetch("counponentity").fetch("userInfo").setId(id)
				.findUnique();
	}

	/**
	 * 查找当前用户是否有优惠券
	 * 
	 * @param id
	 * @param userid
	 * @return
	 */
	public static boolean findExistCouponByUserIdAndId(long id, long userid) {
		int total = find.where().eq("counponId", id).eq("userid", userid)
				.findRowCount();
		return total > 0 ? true : false;
	}
	
	/**
	 * 查找当前用户是否有优惠券
	 * 
	 * @param id
	 * @param userid
	 * @return
	 */
	public static TUseCouponEntity getExistCouponByUserIdAndId(long id, long userid) {
		List<TUseCouponEntity>  total = find.where().eq("counponId", id).eq("userid", userid).findList();
		return total!=null&&total.size()>0 ? total.get(0) :null;
	}

	
	/**
	 * 删除使用过的优惠券
	 * @param id
	 * @param userid
	 */
	public static void deleteUsedCoupon(final long id, final long userid) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				List<TUseCouponEntity> all = find.where().eq("counponId", id)
						.eq("userid", userid).findList();
				if (all != null) {
					for (TUseCouponEntity co : all) {
						Ebean.delete(co);
					}
				}
			}
		});

	}
	
	public static void setUseCoupon(final long id, final long userid){
		
		List<TUseCouponEntity> all = find.where().eq("counponId", id)
				.eq("userid", userid).findList();
		if (all != null) {
			for (TUseCouponEntity useCoouponEntity : all) {
				useCoouponEntity.useDate = new Date();
				useCoouponEntity.isable = 0;
				Set<String> options = new HashSet<String>();
				options.add("isable");
				options.add("useDate");
				Ebean.update(useCoouponEntity, options);
			}
		}
		
	}

	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TUseCouponEntity> findValidPageDataByuserid(
			int currentPage, int pageSize, String orderBy, long userid) {

		CommFindEntity<TUseCouponEntity> result = new CommFindEntity<TUseCouponEntity>();

		Page<TUseCouponEntity> allData = find.orderBy("scanDate").fetch("userInfo")
				.fetch("counponentity").where().eq("t0.userid", userid).eq("isable", 1)
				.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
		// 没有这段，TCouponEntity里的数据就只有id
		
		List<TUseCouponEntity> useCouponArray =  allData.getList();
		List<TUseCouponEntity> newCouponArray = new ArrayList<TUseCouponEntity>();
		//去掉过期的优惠券
		int removeNum = 0;
		if(useCouponArray!=null){
			for(TUseCouponEntity  couponUse:useCouponArray){
				TCouponEntity couponEntity = couponUse.counponentity;
				//Date startDate = couponEntity.startDate;
				Date endDate = couponEntity.endDate;
				Date currentDate = new Date();
				//
				if(endDate!=null&&endDate.before(currentDate)){
					removeNum++;
					continue;
				}
				
				newCouponArray.add(couponUse);
				
			}
		}

		result.setResult(newCouponArray);
		result.setRowCount(allData.getTotalRowCount()-removeNum);
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
				if (bean.Id == null || bean.Id <= 0) {
					bean.Id = TPKGenerator.getPrimaryKey(
							TUseCouponEntity.class.getName(), "Id");
					Logger.debug("TUseCouponEntity id" + bean.Id);
					Ebean.save(bean);
				} else {
					Ebean.update(bean);
				}
			}
		});
	}

}
