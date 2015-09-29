package models.info;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OptimisticLockException;
import javax.persistence.Table;

import play.Logger;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

import controllers.LogController;
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
		
		public static Tsmsmodel getsmsmodel(String content)
		{
			try {
				List<Tsmsmodel> smsmodellist =find.where().eq("cotent", content).findList();
				if(smsmodellist!=null&&smsmodellist.size()>0)
				{
					return smsmodellist.get(0);
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				LogController.debug("get smsmode fail>>"+e);
			}
			return null;
			
	    }
		
		public static boolean savesmsmodel(final String content ,final String descripe)
		{
		
					try {
						Tsmsmodel modele= find.where().eq("cotent", content).eq("descripe", descripe).findUnique();
						if(modele!=null)
						{
							Logger.debug("modelid is exist");
							return false;
						}
						else {	
						Tsmsmodel smsmodel =new Tsmsmodel();
						smsmodel.cotent=content;
						smsmodel.descripe=descripe;
						Ebean.save(smsmodel);
						return true;
						}
					} catch (OptimisticLockException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			return false;
	    }
		
		
		public static  List<Tsmsmodel> getallsmsmodel()
		{
			Logger.info("find the smsmodellist");
		  try {
		    	List<Tsmsmodel> tsmsmodels = new ArrayList<Tsmsmodel>();
			   tsmsmodels=Tsmsmodel.find.all();
			  return tsmsmodels;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
			
	    }

}
