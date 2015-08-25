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
 * @version 创建时间：2015年8月21日 下午3:16:38
 */

@Entity
@Table(name = "tb_uniqueurl")
public class Tuniqueurl extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Expose
	public Long url;

	@Column(columnDefinition = "integer(3) default 0")
	@Expose
	public int sharetime;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date sharetDate;
	
	@Column(length = 150)
	@Expose
	public String userphoneObject;
	

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, Tuniqueurl> find = new Finder<Long, Tuniqueurl>(
			Long.class, Tuniqueurl.class);

	/**
	 * 根据主键查询数据
	 * 
	 * @param userid
	 * @return56
	 */
	public static Tuniqueurl findDataById(Long url) {
		Tuniqueurl uniqueurl = find.byId(url);
		return uniqueurl;
	}

	/**
	 * 保存分享记录,之前无分享记录
	 * 
	 * @param
	 */
	public static void saveTuniqueurl(final Long url) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				Tuniqueurl uniqueurl = new Tuniqueurl();
				uniqueurl.url = url;
				uniqueurl.sharetime =0;
				uniqueurl.sharetDate = new Date();
				Ebean.save(uniqueurl);
			}

		});

	}

	/**
	 * 保存分享记录,之前无分享记录
	 * 
	 * @param
	 */
	/**
	 * 保存分享记录,存在分享记录
	 * 
	 * @param
	 */
	public static void updateTuniqueurl(final Long url, final int time,final String userphoneObject) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				Tuniqueurl uniqueurl = Tuniqueurl.findDataById(url);	
				//判断之前是否有用户使用记录,没有使用记录
			if(uniqueurl.userphoneObject==null){
				uniqueurl.url = url;
				uniqueurl.sharetime = time;
				uniqueurl.sharetDate = new Date();
				uniqueurl.userphoneObject=userphoneObject;
				Set<String> options = new HashSet<String>();
				options.add("url");
				options.add("sharetime");
				options.add("sharetDate");
				options.add("userphoneObject");
				Ebean.update(uniqueurl, options);
			}
			else if(uniqueurl.userphoneObject!=null){
				uniqueurl.url = url;
				uniqueurl.sharetime = time;
				uniqueurl.sharetDate = new Date();
				uniqueurl.userphoneObject=uniqueurl.userphoneObject+','+userphoneObject;
				Set<String> options = new HashSet<String>();
				options.add("url");
				options.add("sharetime");
				options.add("sharetDate");
				options.add("userphoneObject");
				Ebean.update(uniqueurl, options);
			}
			}

		});

	}

	/**
	 * 删除已经获得过5次的分享链接
	 */

	public static void deleteurl(final Long url) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				Tuniqueurl uniqueurl = Tuniqueurl.findDataById(url);
				Ebean.delete(uniqueurl);
			}

		});

	}
	
	
}
