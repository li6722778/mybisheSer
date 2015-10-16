package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;
import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
@Table(name = "tb_userlimit")
public class TUserlimit extends Model {

	/**
	 * mxs
	 */
	private static final long serialVersionUID = 1L;

	
	@Id
	@Expose
	public Long userphone;
	
	@Expose
	public int sharetimes;
		
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date lastsigntime;
	
	
	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TUserlimit> find = new Finder<Long, TUserlimit>(
			Long.class, TUserlimit.class);
	
	
	/**
	 * 根据主键查询数据
	 * 
	 * @param userid
	 * @return56
	 */
	public static TUserlimit findDataById(Long userphone) {
		TUserlimit user = TUserlimit.find.byId(userphone);
		return user;
	}
	
	
	/**
	 * 保存手机号信息
	 * 
	 * @param
	 */
	public static void savelimit(final Long userphone) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				TUserlimit user = new TUserlimit();
				user.userphone=userphone;
				user.sharetimes=1;
				user.lastsigntime=new Date();
				Ebean.save(user);
			}

		});

	}
	
	/**
	 * 更新手机号信息
	 * 
	 * @param
	 */
	public static void updatelimit(final Long userphone) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				TUserlimit user = new TUserlimit();
				user.userphone=userphone;
				int oldtimes =user.sharetimes;
				user.sharetimes=oldtimes;
				user.lastsigntime=new Date();
				Ebean.update(user);
			}

		});

	}
	
}
