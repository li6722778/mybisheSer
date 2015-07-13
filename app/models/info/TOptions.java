package models.info;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import play.data.format.Formats;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_options_set")
public class TOptions extends Model{

	@Id
	@Expose
	public Long optionId;
	
	@Column(columnDefinition = "integer(2) default 0")
	@Expose
	public int optionType;
	
	@Column(columnDefinition = "longtext NULL")
	@Expose
	public String longTextObject;
	
	@Column(length = 500)
	@Expose
	public String textObject;
	
	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	public Date updateDate;
	
	
	@Column(length = 50)
	@Size(max = 50)
	@Expose
	public String updatePerson;
	
	public static Finder<Long, TOptions> find = new Finder<Long, TOptions>(
			Long.class, TOptions.class);
	
	/**
	 * 
	 * @param log
	 */
	public static void saveData(TOptions data) {
		data.updateDate = new Date();
		Ebean.save(data);
	}
	
	/**
	 * 
	 * @param data
	 */
	public static void updateData(TOptions data) {
		data.updateDate = new Date();
		Ebean.update(data);
	}
	
	public static void deleteData(long logId){
		Ebean.delete(TOptions.class, logId);
	}
	
	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static TOptions findOption(int type) {

		List<TOptions> option = find.where().eq("optionType", type).findList();
		if(option==null||option.size()==0){
			return new TOptions();
		}
		return option.get(option.size()-1);
	}
}
