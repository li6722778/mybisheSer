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

import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.CommFindEntity;
import utils.ConfigHelper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_parking")
public class TParkInfo extends Model {

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
	
	@Column(columnDefinition = "integer default 0")
	@Expose
	public int feeTypeSecMinuteOfActivite;

	@Column(columnDefinition = "integer default 0")
	@Expose
	public int feeTypeFixedMinuteOfInActivite;
	
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
	public List<TParkInfo_Img> imgUrlArray;

	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "parkInfo")
	@OrderBy("updateDate DESC")
	@Expose
	public List<TParkInfo_Loc> latLngArray;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date createDate;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
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

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TParkInfo> find = new Finder<Long, TParkInfo>(
			Long.class, TParkInfo.class);
	
	
	/**
	 * 
	 * @param page
	 * @param pageSize
	 * @param sortBy
	 * @param order
	 * @param filter
	 * @return
	 */
	public static Page<TParkInfo> page(int currentPage,int pageSize, String orderBy) {
		Page<TParkInfo> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
        return allData;
    }
	
	/**
	 * 
	 * @param page
	 * @param pageSize
	 * @param sortBy
	 * @param order
	 * @param filter
	 * @return
	 */
	public static Page<TParkInfo> pageByFilter(int currentPage,int pageSize, String orderBy,String key,String searchObj) {
		
		ExpressionList<TParkInfo> elist = find.where();
		
		if(key!=null&&searchObj!=null&&!searchObj.trim().equals("")&&!key.trim().equals("")){
			if(key.trim().toLowerCase().equals("createperson")){
				elist.or(Expr.ilike("createPerson",  "%"+searchObj+"%"), Expr.ilike("updatePerson",  "%"+searchObj+"%"));
			}else{
			   elist.ilike(key, "%"+searchObj+"%");
			}
		}
		Page<TParkInfo> allData = elist.orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
        return allData;
    }
	
	/**
	 * 查询所有数据，并且分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TParkInfo> findData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TParkInfo> result = new CommFindEntity<TParkInfo>();

		Page<TParkInfo> allData = page(currentPage,pageSize,orderBy);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}

	/**
	 * 查询所有数据，并且分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TParkInfo> findData(int currentPage,
			int pageSize, String orderBy, long userId) {

		TuserInfo user = TuserInfo.findDataById(userId);
		CommFindEntity<TParkInfo> result = new CommFindEntity<TParkInfo>();

		if (user != null) {
			Page<TParkInfo> allData = find
					.where()
					.or(Expr.ieq("createPerson", user.userName),
							Expr.ieq("updatePerson", user.userName))
					.orderBy(orderBy).findPagingList(pageSize)
					.setFetchAhead(false).getPage(currentPage);

			result.setResult(allData.getList());
			result.setRowCount(allData.getTotalRowCount());
			result.setPageCount(allData.getTotalPageCount());
		}
		return result;
	}

	/**
	 * 根据主键查询单条数据
	 * 
	 * @param parkId
	 * @return
	 */
	public static TParkInfo findDataById(long parkId) {
		return find.byId(parkId);
	}

	/**
	 * 从待审批表copy数据
	 * 
	 * @param bean
	 */
	public static void retrieveDataWithoutIDPolicy(final TParkInfo parkInfo) {
		Ebean.execute(new TxRunnable() {
			public void run() {

				Ebean.save(parkInfo);

				if (parkInfo.imgUrlArray != null
						&& parkInfo.imgUrlArray.size() > 0) {
					for (TParkInfo_Img imgBean : parkInfo.imgUrlArray) {
						imgBean.parkInfo = parkInfo;
						TParkInfo_Img.saveDataWithoutIDPolicy(imgBean);
					}
				}

				if (parkInfo.latLngArray != null
						&& parkInfo.latLngArray.size() > 0) {
					for (TParkInfo_Loc loc : parkInfo.latLngArray) {
						loc.parkInfo = parkInfo;
						TParkInfo_Loc.saveDataWithoutIDPolicy(loc);
					}
				}

				// 删除旧表数据
				TParkInfoProd.deleteData(parkInfo.parkId);
			}
		});
	}

	/**
	 * 新建或更新数据
	 * 
	 * @param bean
	 */
	public static void saveData(final TParkInfo bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {

				String urlHeader = ConfigHelper.getString("image.url.header");
				Logger.info("image url header:" + urlHeader);
				// ------------生成主键，所有插入数据的方法都需要这个-----------
				if (bean.parkId == null || bean.parkId <= 0) {
					bean.parkId = TPKGenerator.getPrimaryKey(
							TParkInfo.class.getName(), "parkId");
					Date  currentDate = new Date();
					bean.createDate = currentDate;
					bean.updateDate = currentDate;
					Ebean.save(bean);
				} else {
					bean.updateDate = new Date();
					Ebean.update(bean);
				}
				// -------------end----------------

				if (bean.imgUrlArray != null && bean.imgUrlArray.size() > 0) {
					for (TParkInfo_Img imgBean : bean.imgUrlArray) {
						imgBean.parkInfo = bean;
						imgBean.createPerson = bean.createPerson;
						imgBean.updatePerson = bean.updatePerson;
						imgBean.imgUrlHeader = urlHeader;
						TParkInfo_Img.saveData(imgBean);
					}
				}

				if (bean.latLngArray != null && bean.latLngArray.size() > 0) {
					for (TParkInfo_Loc loc : bean.latLngArray) {
						loc.parkInfo = bean;
						loc.createPerson = bean.createPerson;
						loc.updatePerson = bean.updatePerson;
						TParkInfo_Loc.saveData(loc);
					}
				}
			}
		});
	}

	/**
	 * 删除数据
	 * 
	 * @param id
	 */
	public static void deleteData(final Long id) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				TParkInfo_Img.deleteExistImage(id);
				Ebean.delete(TParkInfo.class, id);
			}
		});
	}
	
	/**
	 * 删除数据但是不删除图片
	 * 
	 * @param id
	 */
	public static void deleteDataOnly(final Long id) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				//TParkInfo_Img.deleteExistImage(id);
				Ebean.delete(TParkInfo.class, id);
			}
		});
	}

}
