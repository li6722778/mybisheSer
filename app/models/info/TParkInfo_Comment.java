package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import play.data.format.Formats;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_parking_comment")
public class TParkInfo_Comment extends Model{

	@Id
	@Expose
	public Long parkComId;
	
	@Expose
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parkId")
	public TParkInfoProd parkInfo;
	
	@Expose
	public String comments;
	
	
	@Expose
	public float rating;
	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	public Date createDate;


	@Expose
	@Column(length = 50)
	@Size(max = 50)
	public String createPerson;

	

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TParkInfo_Comment> find = new Finder<Long, TParkInfo_Comment>(
			Long.class, TParkInfo_Comment.class);
	

	/**
	 * 新建或更新数据
	 * 
	 * @param userinfo
	 */
	public static void saveData(final TParkInfo_Comment bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				// ------------生成主键，所有插入数据的方法都需要这个-----------
				if (bean.parkComId == null || bean.parkComId <= 0) {
					bean.parkComId = TPKGenerator.getPrimaryKey(
							TParkInfo_Comment.class.getName(), "parkComId");
					Ebean.save(bean);
				} else {
					Ebean.update(bean);
				}
				// -------------end----------------
			}
		});

	}

	/**
	 * 删除数据
	 * 
	 * @param id
	 */
	public static void deleteData(Long id) {
		Ebean.delete(TParkInfo_Comment.class, id);
	}

	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TParkInfo_Comment> findPageData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TParkInfo_Comment> result = new CommFindEntity<TParkInfo_Comment>();

		Page<TParkInfo_Comment> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}
	
	/**
	 * 根据车位ID得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TParkInfo_Comment> findPageData(int currentPage,
			int pageSize, String orderBy,long parkId) {

		CommFindEntity<TParkInfo_Comment> result = new CommFindEntity<TParkInfo_Comment>();

		Page<TParkInfo_Comment> allData = find.where().eq("parkId", parkId).orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}

	
	
	public static TParkInfo_Comment findDataById(long id) {
		return find.byId(id);
	}
	
}
