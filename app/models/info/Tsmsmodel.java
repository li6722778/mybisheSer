package models.info;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import controllers.LogController;
import play.api.libs.iteratee.internal;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import sun.util.logging.resources.logging;
import views.html.log;
@Entity
@Table(name = "tb_smsmodel")
public class Tsmsmodel extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Id
	@Expose
	public int type;
	
	@Column(length = 1000)
	@Expose
	public String cotent;
	@Column(length = 500)
	@Expose
	public String descripe;
	
	// 查询finder，用于其他方法中需要查询的场景
		public static Finder<Integer, Tsmsmodel> find = new Finder<Integer, Tsmsmodel>(
				Integer.class, Tsmsmodel.class);
		
		public static Tsmsmodel geTsmsmodel(int type)
		{
			try {
				Tsmsmodel smsmodel =Tsmsmodel.find.byId(type);
				return smsmodel;
			} catch (Exception e) {
				// TODO: handle exception
				LogController.debug("get smsmode fail>>"+e);
				return null;
			}
			
	    }

}
