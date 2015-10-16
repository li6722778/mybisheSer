package models.info;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

/**
 * @author mxs E-mail:308348194@qq.com
 * @version 创建时间：2015年8月21日 上午11:07:17
 */

@Entity
@Table(name = "tb_unregisteruser")
public class Tunregisteruser extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Expose
	public Long userPhone;

	@Column(columnDefinition = "integer(3) default 0")
	@Expose
	public int sharetime;
	
	
	@Column(columnDefinition = "default 0")
	@Expose
	public int limitsharetime;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date sharetDate;
	
	
	
	
	
	
	
	// 查询finder，用于其他方法中需要查询的场景
		public static Finder<Long, Tunregisteruser> find = new Finder<Long, Tunregisteruser>(
				Long.class, Tunregisteruser.class);

		/**
		 * 根据主键查询数据
		 * 
		 * @param userPhone
		 * @return56
		 */
		public static Tunregisteruser findDataById(Long userPhone) {
			Tunregisteruser unregisteruser = find.byId(userPhone);
			return unregisteruser;
		}

		/**
		 * 保存分享记录,之前无分享记录
		 * 
		 * @param
		 */
		public static void saveunregistershare(final Long userPhone ) {

			Ebean.execute(new TxRunnable() {
				public void run() {
					Tunregisteruser unregisteruser= new Tunregisteruser();
					unregisteruser.userPhone = userPhone;
					unregisteruser.sharetime =1;
					unregisteruser.limitsharetime=0;
					unregisteruser.sharetDate = new Date();
					Ebean.save(unregisteruser);

				}

			});

		}
		
		/**
		 * 保存分享记录,存在分享记录
		 * 
		 * @param
		 */
		public static void updateunregisterlimitshare(final Long userPhone ,final int addlimitsharetime) {

			Ebean.execute(new TxRunnable() {
				public void run() {	
					Tunregisteruser unregisteruser= Tunregisteruser.findDataById(userPhone);
				    int oldlimitsharetime=unregisteruser.limitsharetime;
				    unregisteruser.limitsharetime=oldlimitsharetime+1;
					Ebean.update(unregisteruser);
				}

			});

		}
		
		public static void updateunregistershare(final Long userPhone ,final int time) {

			Ebean.execute(new TxRunnable() {
				public void run() {	
					Tunregisteruser unregisteruser= Tunregisteruser.findDataById(userPhone);
					unregisteruser.userPhone =userPhone;
					unregisteruser.sharetime =time;
					unregisteruser.sharetDate= new  Date();			
					Set<String> options = new HashSet<String>();
					options.add("userPhone");
					options.add("sharetime");
					options.add("sharetDate");
					Ebean.update(unregisteruser, options);
				}

			});

		}
	
		/**
		 * 
		 * @param 新用户注册后删除 数据
		 */

		
		public static void deletfromunregisteruser(final Long userPhone) {
			
			Ebean.execute(new TxRunnable() {
				public void run() {	
					Tunregisteruser unregisteruser= Tunregisteruser.findDataById(userPhone);
					Ebean.delete(unregisteruser);

				}

			});
			
			
		}


}
