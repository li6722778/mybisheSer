package models.info;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.Transactional;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_parking_loc")
public class TParkInfo_Loc extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Expose
	public Long parkLocId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parkId")
	public TParkInfo parkInfo;

	// 1:开放中; 2:关闭
	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int isOpen;

	// 剩余车位
	@Expose
	public int parkFreeCount;

	/* 类型,1:入口,2:出口 */
	@Expose
	@Column(columnDefinition = "integer(2) default 1")
	public int type;

	@Expose
	@Column(columnDefinition = "decimal(20,17) NOT NULL")
	public double latitude;

	@Expose
	@Column(columnDefinition = "decimal(20,17) NOT NULL")
	public double longitude;

	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	public Date createDate;

	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	public Date updateDate;

	@Expose
	@Column(length = 50)
	@Size(max = 50)
	public String createPerson;

	@Expose
	@Column(length = 50)
	@Size(max = 50)
	public String updatePerson;

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TParkInfo_Loc> find = new Finder<Long, TParkInfo_Loc>(
			Long.class, TParkInfo_Loc.class);

	/**
	 * 新建或更新数据
	 * 
	 * @param userinfo
	 */
	@Transactional
	public static void saveData(TParkInfo_Loc bean) {

		// ------------生成主键，所有插入数据的方法都需要这个-----------
		if (bean.parkLocId == null || bean.parkLocId <= 0) {
			bean.parkLocId = TPKGenerator.getPrimaryKey(
					TParkInfo_Loc.class.getName(), "parkLocId");
			Ebean.save(bean);
		} else {
			Ebean.update(bean);
		}
		// -------------end----------------

	}

	/**
	 * 删除数据
	 * 
	 * @param id
	 */
	public static void deleteData(Long id) {
		Ebean.delete(TParkInfo_Loc.class, id);
	}

	public static void saveDataWithoutIDPolicy(final TParkInfo_Loc bean) {
		Ebean.save(bean);
	}

	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TParkInfo_Loc> findPageData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TParkInfo_Loc> result = new CommFindEntity<TParkInfo_Loc>();

		Page<TParkInfo_Loc> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}

	/**
	 * 根据parkid得到所有的车位坐标
	 * 
	 * @param parkId
	 * @return
	 */
	public static CommFindEntity<TParkInfo_Loc> findData(long parkId, int type,
			int status) {

		CommFindEntity<TParkInfo_Loc> result = new CommFindEntity<TParkInfo_Loc>();

		List<TParkInfo_Loc> allData = find.where().eq("parkId", parkId)
				.eq("type", type).eq("isOpen", status).findList();

		result.setResult(allData);
		result.setRowCount(allData.size());
		result.setPageCount(1);
		return result;
	}
	

}
