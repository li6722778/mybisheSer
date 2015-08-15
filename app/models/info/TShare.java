package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

import play.Logger;
import play.api.libs.iteratee.internal;
import play.data.format.Formats;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.libs.Crypto;

@Entity
@Table(name = "tb_share")
public class TShare extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4187583002641695933L;

	@Id
	@Expose
	public Long userid;

	@Column(columnDefinition = "integer(3) default 0")
	@Expose
	public int share;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date sharetDate;

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TShare> find = new Finder<Long, TShare>(
			Long.class, TShare.class);

	/**
	 * 根据主键查询数据
	 * 
	 * @param userid
	 * @return
	 */
	public static TShare findDataById(Long userid) {
		TShare share = find.byId(userid);
		return share;
	}

	/**
	 * 保存分享记录,之前无分享记录
	 * 
	 * @param
	 */
	public static void saveshare(final Long userid ) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				TShare tShare = new TShare();
				tShare.userid = userid;
				tShare.share =1;
				tShare.sharetDate = new Date();
				Ebean.save(tShare);

			}

		});

	}
	
	/**
	 * 保存分享记录,存在分享记录
	 * 
	 * @param
	 */
	public static void saveshare(final Long userid ,final int time) {

		Ebean.execute(new TxRunnable() {
			public void run() {
			TShare share =TShare.findDataById(userid);
			share.userid =userid;
			share.share =time;
			share.sharetDate= new  Date();
			Ebean.update(share);

			}

		});

	}
	

}
