package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.sf.cglib.beans.BeanCopier;
import play.data.format.Formats;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import com.avaje.ebean.Ebean;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_counpon_use_his")
public class TUseCouponHis extends Model {
	public static BeanCopier copier = BeanCopier.create(TUseCouponEntity.class,
			TUseCouponHis.class, false);

	@Id
	@Expose
	public Long Id;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userid")
	@Expose
	public TuserInfo userInfo;

	@Expose
	public Long counponId;
	
	
	@Expose
	@Transient
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
		public static Finder<Long, TUseCouponHis> find = new Finder<Long, TUseCouponHis>(
				Long.class, TUseCouponHis.class);
	
	
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
	
	public static void saveData(final TUseCouponHis bean)
	{
		if(bean.Id>0)
			Ebean.save(bean);
		
		
	}
}
