package models.info;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name = "tb_parking_adm")
public class TParkInfo_adm  extends Model{

	@Id
	public long parkAdmId;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "parkId")
	public TParkInfoProd parkInfo;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "userid")
	public TuserInfo userInfo;
	
	//查询finder，用于其他方法中需要查询的场景 
	public static Finder<Long,TParkInfo_adm> find = new Finder<Long,TParkInfo_adm>(Long.class, TParkInfo_adm.class);
	
	public static TParkInfoProd findAdmPartInfo(long userId){
		TParkInfo_adm adm = find.where().eq("userid", userId).findUnique();
		return adm==null?null:adm.parkInfo;
	}
}
