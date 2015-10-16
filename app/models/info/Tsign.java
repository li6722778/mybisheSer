package models.info;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;
import com.sun.javafx.scene.paint.GradientUtils.Point;

import play.data.format.Formats;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name = "tb_sign")
public class Tsign extends Model{

	/**
	 * mxs
	 */
	private static final long serialVersionUID = 1L;

	
	@Id
	@Expose
	public Long userid;
	
	@Expose
	public Long telephone;
	@Expose
	public int point;
	
	@Expose
	public int signtimes;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date lastsigntime;
	
	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, Tsign> find = new Finder<Long, Tsign>(
			Long.class, Tsign.class);

	
	/**
	 * 根据主键查询数据
	 * 
	 * @param userid
	 * @return56
	 */
	public static Tsign findDataById(Long userid) {
		Tsign sign = find.byId(userid);
		return sign;
	}
	
	
	/**
	 * 保存签到信息，之前无签到
	 * 
	 * @param
	 */
	public static void savesign(final Long userid,final Long telephone) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				Tsign sign = new Tsign();
				sign.userid = userid;
				sign.telephone =telephone;
				sign.point=1;
				sign.signtimes=1;
				sign.lastsigntime=new  Date();
				Ebean.save(sign);
			}

		});

	}
	
	
	/**
	 * 保存签到信息，之前有签到
	 * 
	 * @param
	 */
	public static void updateSign(final long userid) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				Tsign sign = Tsign.findDataById(userid);	
				if(sign!=null)
				{
					int oldpoint =sign.point;
					sign.point=oldpoint+1;
					sign.lastsigntime= new Date();
					Ebean.update(sign);
				}
				      
			}

		});

	}

}
