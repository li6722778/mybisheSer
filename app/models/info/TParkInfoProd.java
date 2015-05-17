package models.info;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import net.sf.cglib.beans.BeanCopier;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_parking_prod")
public class TParkInfoProd extends Model {
	@Id
	@Expose
	public Long parkId;

	@Constraints.Required
	@Column(nullable = false, length = 100)
	@Size(max = 100)
	@Expose
	public String parkname;

	@Column(columnDefinition = "TEXT")
	@Expose
	public String detail;

	@Column(length = 500)
	@Expose
	public String address;

	@Column(length = 200)
	@Expose
	public String vender;

	@Constraints.Required
	@Column(nullable = false, length = 30)
	@Expose
	public String owner;

	@Constraints.Required
	@Column(nullable = false, length = 30)
	@Expose
	public long ownerPhone;

	@Size(max = 255)
	@Expose
	public String venderBankName;

	@Size(max = 255)
	@Expose
	public String venderBankNumber;

	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int feeType;

	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int feeTypeSecInScopeHours;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double feeTypeSecInScopeHourMoney;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double feeTypeSecOutScopeHourMoney;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double feeTypefixedHourMoney;

	@Column(columnDefinition = "integer(2) default 0")
	@Expose
	public int isDiscountAllday;

	@Column(columnDefinition = "integer(2) default 0")
	@Expose
	public int isDiscountSec;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double discountHourAlldayMoney;

	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double discountSecHourMoney;

	@Formats.DateTime(pattern = "HH:mm:ss")
	@Column(columnDefinition = "time")
	@Expose
	public Date discountSecStartHour;

	@Formats.DateTime(pattern = "HH:mm:ss")
	@Column(columnDefinition = "time")
	@Expose
	public Date discountSecEndHour;

	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "parkInfo")
	@OrderBy("updateDate DESC")
	@Expose
	public List<TParkInfoPro_Img> imgUrlArray;

	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "parkInfo")
	@OrderBy("updateDate DESC")
	@Expose
	public List<TParkInfoPro_Loc> latLngArray;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	@Expose
	public Date createDate;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	@Expose
	public Date updateDate;

	@Column(length = 50)
	@Size(max = 50)
	@Expose
	public String createPerson;

	@Column(length = 50)
	@Size(max = 50)
	@Expose
	public String updatePerson;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	@Expose
	public Date approveDate;
	
	@Column(length = 50)
	@Size(max = 50)
	@Expose
	public String approvePerson;

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TParkInfoProd> find = new Finder<Long, TParkInfoProd>(
			Long.class, TParkInfoProd.class);

	/**
	 * 查询所有数据，并且分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TParkInfoProd> findData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TParkInfoProd> result = new CommFindEntity<TParkInfoProd>();

		Page<TParkInfoProd> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}

	/**
	 * 根据主键查询单条数据
	 * 
	 * @param parkId
	 * @return
	 */
	public static TParkInfoProd findDataById(long parkId) {
		return find.byId(parkId);
	}

	/**
	 * 新建或更新数据
	 * 
	 * @param bean
	 */
	public static void saveData(final TParkInfoProd bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {

					// ------------生成主键，所有插入数据的方法都需要这个-----------
					if (bean.parkId == null || bean.parkId <= 0) {
						bean.parkId = TPKGenerator.getPrimaryKey(
								TParkInfoProd.class.getName(), "parkId");
						Ebean.save(bean);
					} else {
						bean.updateDate = new Date();
						Ebean.update(bean);
					}
				
				// -------------end----------------

				if (bean.imgUrlArray != null && bean.imgUrlArray.size() > 0) {
					for (TParkInfoPro_Img imgBean : bean.imgUrlArray) {
						imgBean.parkInfo = bean;
						TParkInfoPro_Img.saveData(imgBean);
					}
				}

				if (bean.latLngArray != null && bean.latLngArray.size() > 0) {
					for (TParkInfoPro_Loc loc : bean.latLngArray) {
						loc.parkInfo = bean;
						TParkInfoPro_Loc.saveData(loc);
					}
				}
			}
		});
	}
	
	/**
	 * 从待审批表copy数据
	 * @param bean
	 */
	public static void approveDataWithoutIDPolicy(final TParkInfoProd parkProdInfo) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				
				Ebean.save(parkProdInfo);
			
				if (parkProdInfo.imgUrlArray != null && parkProdInfo.imgUrlArray.size() > 0) {
					for (TParkInfoPro_Img imgBean : parkProdInfo.imgUrlArray) {
						imgBean.parkInfo = parkProdInfo;
						TParkInfoPro_Img.saveDataWithoutIDPolicy(imgBean);
					}
				}

				if (parkProdInfo.latLngArray != null && parkProdInfo.latLngArray.size() > 0) {
					for (TParkInfoPro_Loc loc : parkProdInfo.latLngArray) {
						loc.parkInfo = parkProdInfo;
						TParkInfoPro_Loc.saveDataWithoutIDPolicy(loc);
					}
				}
				
				//删除旧表数据
				TParkInfo.deleteData(parkProdInfo.parkId);
			}
		});
	}

	/**
	 * 删除数据
	 * 
	 * @param id
	 */
	public static void deleteData(Long id) {
		Ebean.delete(TParkInfoProd.class, id);
	}
}
