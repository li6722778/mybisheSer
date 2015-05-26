package models.info;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_client_ver")
public class TVersion extends Model{

	@Id
	@Expose
	public Long versionId;
	
	@Column(columnDefinition = "integer(2) default 0")
	@Expose
	public int forceUpdate;
	
	@Expose
	public Long version;
	
	@Expose
	@Column(length = 100)
	public String updateUrl;
	
	@Expose
	@Column(length = 100)
	public String updatesContent;
	
	public static Finder<Long, TVersion> find = new Finder<Long, TVersion>(
			Long.class, TVersion.class);
	
	/**
	 * 
	 * @param log
	 */
	public static void saveData(TVersion data) {
		Ebean.save(data);
	}
	
	/**
	 * 
	 * @param data
	 */
	public static void updateData(TVersion data) {
		Ebean.update(data);
	}
	
	public static void deleteData(long logId){
		Ebean.delete(TVersion.class, logId);
	}
	
	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static TVersion findVersion() {

		List<TVersion> version = find.findList();
		if(version==null||version.size()==0){
			return new TVersion();
		}
		return version.get(version.size()-1);
	}
}
