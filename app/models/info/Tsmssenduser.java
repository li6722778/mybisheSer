package models.info;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.Logger;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_smssenduser")
public class Tsmssenduser extends Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Expose
	public int id;
	
	@Column(length = 20)
	@Expose
	public String telephone;
	
	@Expose
	public int type;
	
	
	@Column(length = 1000)
	@Expose
	public String cotent;
	
	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Integer, Tsmssenduser> find = new Finder<Integer, Tsmssenduser>(
			Integer.class, Tsmssenduser.class);
	
	//遍历整个表
	public static List<Tsmssenduser> findalluser() { 
		
        try {
			List<Tsmssenduser> smsList= Tsmssenduser.find.findList();
			if(smsList.size()>0&&smsList!=null)
			{
				return smsList;
			}
			else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
