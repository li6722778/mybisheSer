package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;

@Entity
@Table(name = "tb_parking")
public class TParkInfo extends Model {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long parkId;

	@Constraints.Required
	@Column(nullable = false, length = 100)
	@Size(max = 100)
	public String parkname;

	@Column(columnDefinition = "TEXT")
	public String detail;

	@Column(length = 500)
	public String address;

	@Column(length = 200)
	public String vender;

	@Constraints.Required
	@Column(nullable = false, length = 30)
	public String owner;

	@Constraints.Required
	@Column(nullable = false, length = 30)
	public int ownerPhone;

	@Size(max = 255)
	public String venderBankName;

	@Size(max = 255)
	public String venderBankNumber;

	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "parkInfo")
	@OrderBy("updateDate ASC")
	public List<TParkInfo_Img> imgUrlArray;

	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "parkInfo")
	public List<TParkInfo_Loc> latLngArray;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	public Date createDate;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	public Date updateDate;

	@Column(length = 50)
	@Size(max = 50)
	public String createPerson;

	@Column(length = 50)
	@Size(max = 50)
	public String updatePerson;

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TParkInfo> find = new Finder<Long, TParkInfo>(
			Long.class, TParkInfo.class);

	/**
	 * 查询所有数据，并且分页
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TParkInfo> findData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TParkInfo> result = new CommFindEntity<TParkInfo>();

		Page<TParkInfo> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}

	/**
	 * 根据主键查询单条数据
	 * @param parkId
	 * @return
	 */
	public static TParkInfo findDataById(long parkId) {
		return find.byId(parkId);
	}

	/**
	 * 新建或更新数据
	 * @param userinfo
	 */
	public static void saveData(TParkInfo bean) {
		// ------------生成主键，所有插入数据的方法都需要这个-----------
		if (bean.parkId == null || bean.parkId <= 0) {
			bean.parkId = TPKGenerator.getPrimaryKey(
					TParkInfo.class.getName(), "parkId");
		}
		// -------------end----------------

		Ebean.save(bean);
	}

	/**
	 * 删除数据
	 * @param userid
	 */
	public static void deleteData(Long id) {
		Ebean.delete(TParkInfo.class, id);
	}
}
