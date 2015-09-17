package models.info;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.Logger;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_redicturl")

public class Tredirecturl extends Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 50)
	@Expose
	public String uniqueurl;
	
	@Column(length = 1000)
	@Expose
	public String redicturl;
	
	
	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<String, Tredirecturl> find = new Finder<String, Tredirecturl>(
			String.class, Tredirecturl.class);
	

	public static void saveredicturl(final String uniqueurl,final String redicturl) {
    
		String sql ="INSERT INTO tb_redicturl VALUES ('"+uniqueurl+"','"+redicturl+"')";
		Logger.debug("insert result:" + sql);
		Ebean.createSqlUpdate(sql).execute();
	}
}
