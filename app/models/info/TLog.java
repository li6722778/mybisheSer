package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.google.gson.annotations.Expose;

import play.data.format.Formats;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import utils.CommFindEntity;

@Entity
@Table(name = "tb_log")
public class TLog extends Model{
	
	@Id
	@Expose
	public Long logId;
	
	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int level;
	
	@Expose
	@Column(length = 100)
	public String operateName;

	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	public Date operateDate;

	@Expose
	@Column(length = 500)
	public String content;

	@Expose
	@Column(length = 500)
	public String extraString;
	
	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TLog> find = new Finder<Long, TLog>(
			Long.class, TLog.class);
	/**
	 * 
	 * @param log
	 */
	public static void saveData(TLog log) {
		Ebean.save(log);
	}
	
	public static void deleteData(Long logId){
		Ebean.delete(TLog.class, logId);
	}
	
	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TLog> findPageData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TLog> result = new CommFindEntity<TLog>();

		Page<TLog> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}
}
