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
	 * 查询是否分享
	 * 
	 * @param userinfo
	 * @return
	 */
	public static boolean isshare(final Long userid) {

		TShare tShare = findDataById(userid);

		if (tShare ==null) {
			return false;
		} else {
			int isshare = tShare.share;

			if (isshare == 1) {
				return true;
			}

			else {
				return false;
			}

		}

	}

	/**
	 * 保存分享
	 * 
	 * @param
	 */
	public static void saveshare(final Long userid) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				TShare tShare = new TShare();
				tShare.userid = userid;
				tShare.share = 1;
				Ebean.save(tShare);

			}

		});

	}

}
