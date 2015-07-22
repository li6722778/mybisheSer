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

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.CommFindEntity;
import utils.DateHelper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
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
	public List<TParkInfoPro_Img> imgUrlArray;

	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "parkInfo")
	@OrderBy("updateDate DESC")
	@Expose
	public List<TParkInfoPro_Loc> latLngArray;
	
	@Column(columnDefinition = "float default 0")
	@Expose
	public float averagerat;

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
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
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
	 * 得到总数
	 * @return
	 */
	public static int findCount(){
		return find.findRowCount();
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
	public static Page<TParkInfoProd> page(int currentPage,int pageSize, String orderBy) {
		Page<TParkInfoProd> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);
        return allData;
    }
	
	
	/**
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @param key
	 * @param searchObj
	 * @return
	 */
    public static Page<TParkInfoProd> pageByFilter(int currentPage,int pageSize, String orderBy,String key,String searchObj,int isopen) {
		ExpressionList<TParkInfoProd> elist = find.where();
		
		if(key!=null&&searchObj!=null&&!searchObj.trim().equals("")&&!key.trim().equals("")){
			if(key.trim().toLowerCase().equals("createperson")){
				elist.or(Expr.ilike("createPerson",  "%"+searchObj+"%"), Expr.ilike("updatePerson",  "%"+searchObj+"%"));
			}else{
			   elist.ilike(key, "%"+searchObj+"%");
			}
		}
		if(isopen>=0){
		    Query<TParkInfoPro_Loc> query = Ebean.createQuery(TParkInfoPro_Loc.class).select("parkInfo.parkId").where().eq("isOpen", isopen).query();
		    elist.in("parkId", query);
		}
		Page<TParkInfoProd> allData = elist.orderBy(orderBy)
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
	public static CommFindEntity<TParkInfoProd> findData(int currentPage,
			int pageSize, String orderBy) {

		CommFindEntity<TParkInfoProd> result = new CommFindEntity<TParkInfoProd>();

		Page<TParkInfoProd> allData = page(currentPage,pageSize,orderBy);

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
	public static CommFindEntity<TParkInfoProd> findData(int currentPage,
			int pageSize, String orderBy,long userId) {

		TuserInfo user = TuserInfo.findDataById(userId);
		CommFindEntity<TParkInfoProd> result = new CommFindEntity<TParkInfoProd>();

		if(user!=null){
		Page<TParkInfoProd> allData = find.where().or(Expr.ieq("createPerson", user.userName), Expr.ieq("updatePerson", user.userName)).orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

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

				String feeDetail ="";
				if(bean.feeType==1){//分段收费
					 feeDetail += bean.feeTypeSecInScopeHours+"小时内收费为"+bean.feeTypeSecInScopeHourMoney+"元/小时";
					 feeDetail+=",超过"+bean.feeTypeSecInScopeHours+"小时后,收费为"+ bean.feeTypeSecOutScopeHourMoney+"元/小时";
				}else{
					feeDetail += "按次收费,每次"+bean.feeTypefixedHourMoney+"元/次";
				}
				if(bean.isDiscountAllday==1){
					if(bean.feeType==1){
						feeDetail+="。全天优惠计费:"+ bean.discountHourAlldayMoney+"元/小时";
					}else{
						feeDetail+="。全天优惠:"+ bean.discountHourAlldayMoney+"元/天";
					}
					
				}
				
				if(bean.isDiscountSec==1){
					if(bean.discountSecStartHour!=null&&bean.discountSecEndHour!=null){
						feeDetail+=DateHelper.format(bean.discountSecStartHour, "HH:mm:00")+"至"+DateHelper.format(bean.discountSecEndHour, "HH:mm:00")+"收费为"+bean.discountSecHourMoney+"元/小时";
					}
					
				}
				bean.detail = feeDetail;
				
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
						TParkInfoProd prod = new TParkInfoProd();
						prod.parkId = bean.parkId;
						imgBean.parkInfo = prod;
						TParkInfoPro_Img.saveData(imgBean);
					}
				}

				if (bean.latLngArray != null && bean.latLngArray.size() > 0) {
					for (TParkInfoPro_Loc loc : bean.latLngArray) {
						TParkInfoProd prod = new TParkInfoProd();
						prod.parkId = bean.parkId;
						loc.parkInfo = prod;
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
				
				String feeDetail ="";
				if(parkProdInfo.feeType==1){//分段收费
					 feeDetail += parkProdInfo.feeTypeSecInScopeHours+"小时内收费为"+parkProdInfo.feeTypeSecInScopeHourMoney+"元/小时";
					 feeDetail+=",超过"+parkProdInfo.feeTypeSecInScopeHours+"小时后,收费为"+ parkProdInfo.feeTypeSecOutScopeHourMoney+"元/小时";
				}else{
					feeDetail += "按次收费,每次"+parkProdInfo.feeTypefixedHourMoney+"元";
				}
				if(parkProdInfo.isDiscountAllday==1){
					feeDetail+=",全天优惠:"+ parkProdInfo.discountHourAlldayMoney+"元/天";
				}
				
				if(parkProdInfo.isDiscountSec==1){
					if(parkProdInfo.discountSecStartHour!=null&&parkProdInfo.discountSecEndHour!=null){
						feeDetail+=DateHelper.format(parkProdInfo.discountSecStartHour, "HH:mm:00")+"至"+DateHelper.format(parkProdInfo.discountSecEndHour, "HH:mm:00")+"收费为"+parkProdInfo.discountSecHourMoney+"元/小时";
					}
					
				}
				parkProdInfo.detail = feeDetail;
				
				Ebean.save(parkProdInfo);
			
				if (parkProdInfo.imgUrlArray != null && parkProdInfo.imgUrlArray.size() > 0) {
					for (TParkInfoPro_Img imgBean : parkProdInfo.imgUrlArray) {
						TParkInfoProd prod = new TParkInfoProd();
						prod.parkId = parkProdInfo.parkId;
						imgBean.parkInfo = prod;
						TParkInfoPro_Img.saveDataWithoutIDPolicy(imgBean);
					}
				}

				if (parkProdInfo.latLngArray != null && parkProdInfo.latLngArray.size() > 0) {
					for (TParkInfoPro_Loc loc : parkProdInfo.latLngArray) {
						TParkInfoProd prod = new TParkInfoProd();
						prod.parkId = parkProdInfo.parkId;
						loc.parkInfo = prod;
						TParkInfoPro_Loc.saveDataWithoutIDPolicy(loc);
					}
				}
				
				//删除旧表数据
				TParkInfo.deleteDataOnly(parkProdInfo.parkId);
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
