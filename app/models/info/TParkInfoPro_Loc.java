package models.info;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_parking_prod_loc")
public class TParkInfoPro_Loc extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Expose
	public Long parkLocId;

	@Expose
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parkId")
	public TParkInfoProd parkInfo;

	// 1:开放中; 2:关闭
	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int isOpen;

	// 剩余车位
	@Expose
	public int parkFreeCount;

	/* 类型,1:出口,2:入口 */
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
	public static Finder<Long, TParkInfoPro_Loc> find = new Finder<Long, TParkInfoPro_Loc>(
			Long.class, TParkInfoPro_Loc.class);

	/**
	 * 新建或更新数据
	 * 
	 * @param userinfo
	 */
	public static void saveData(final TParkInfoPro_Loc bean) {

		Ebean.execute(new TxRunnable() {
			public void run() {
					// ------------生成主键，所有插入数据的方法都需要这个-----------
					if (bean.parkLocId == null || bean.parkLocId <= 0) {
						bean.parkLocId = TPKGenerator.getPrimaryKey(
								TParkInfoPro_Loc.class.getName(), "parkLocId");
						Ebean.save(bean);
					} else {
						Ebean.update(bean);
					}
				
				// -------------end----------------
			}
		});

	}
	
	
	public static void saveOpenCloseStatus(final long parkingId,final String updatedPerson) {

		Ebean.execute(new TxRunnable() {
			public void run() {
					// ------------生成主键，所有插入数据的方法都需要这个-----------
				List<TParkInfoPro_Loc> result = getLocationPointByParkingId(parkingId);
				if(result!=null&&result.size()>0){
					TParkInfoPro_Loc loc = result.get(0);
					//判断其中一个点是否开放
					int isopen = loc.isOpen;
					for(TParkInfoPro_Loc tmp : result){
						Logger.debug("update TParkInfoPro_Loc status:"+isopen+" to new status");
						if(isopen!=1){
							tmp.isOpen=1;
							tmp.updateDate=new Date();
							tmp.updatePerson=updatedPerson;
							Set<String> updates = new HashSet<String>();
							updates.add("isOpen");
							updates.add("updateDate");
							updates.add("updatePerson");
							Ebean.update(tmp, updates);
						}else if(isopen==1){
							tmp.isOpen=0;
							tmp.updateDate=new Date();
							tmp.updatePerson=updatedPerson;
							Set<String> updates = new HashSet<String>();
							updates.add("isOpen");
							updates.add("updateDate");
							updates.add("updatePerson");
							Ebean.update(tmp, updates);
						}
						
//						TParkInfoProd prod = TParkInfoProd.findDataById(parkingId);
//						prod.updateDate = new Date();
//						prod.updatePerson = updatedPerson;
//						
//						Set<String> updatesParking = new HashSet<String>();
//						updatesParking.add("updateDate");
//						updatesParking.add("updatePerson");
//						Ebean.update(prod, updatesParking);
						
					}
				}
				
				// -------------end----------------
			}
		});

	}
	
	/**
	 * 直接插入数据
	 * 
	 * @param userinfo
	 */
	public static void saveDataWithoutIDPolicy(final TParkInfoPro_Loc bean) {
						Ebean.save(bean);
	}

	/**
	 * 删除数据
	 * 
	 * @param id
	 */
	public static void deleteData(Long id) {
		Ebean.delete(TParkInfoPro_Loc.class, id);
	}

	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TParkInfoPro_Loc> findPageData(
			int currentPage, int pageSize, String orderBy) {

		CommFindEntity<TParkInfoPro_Loc> result = new CommFindEntity<TParkInfoPro_Loc>();

		Page<TParkInfoPro_Loc> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}
	
	/**
	 * 得到所有的点
	 * @param parkId
	 * @return
	 */
	public static List<TParkInfoPro_Loc> getLocationPointByParkingId(long parkId){
		List<TParkInfoPro_Loc> result = find.where().eq("parkId", parkId).findList();
		return result;
	}

	/**
	 * 根据parkid得到所有的车位坐标
	 * 
	 * @param parkId
	 * @return
	 */
	public static CommFindEntity<TParkInfoPro_Loc> findData(long parkId,
			int type, int status) {

		CommFindEntity<TParkInfoPro_Loc> result = new CommFindEntity<TParkInfoPro_Loc>();

		List<TParkInfoPro_Loc> allData = find.where().eq("parkId", parkId)
				.eq("type", type).eq("isOpen", status).findList();

		result.setResult(allData);
		result.setRowCount(allData.size());
		result.setPageCount(1);
		return result;
	}
	
	/**
	 * latitude 和 longitude建立复合索引
	 * @param myLat
	 * @param myLng
	 * @param scope
	 * @return
	 */
	public static List<TParkInfoPro_Loc> findNearbyParking(double myLat,double myLng, float scope){
		
		//先计算经纬度范围
		double range = 180 / Math.PI * scope / 6372.797;  
		double lngR = range / Math.cos(myLat * Math.PI / 180.0);
		double maxLat = myLat + range;
		double minLat = myLat - range;
		double maxLng = myLng + lngR;
		double minLng = myLng - lngR;
		
		Logger.debug("-------minLat:"+minLat+",maxLat:"+maxLat+"");
		Logger.debug("-------minLng:"+minLng+",maxLng:"+maxLng+"");
		
		List<TParkInfoPro_Loc> result = find.where().eq("type",1).between("latitude", minLat, maxLat).between("longitude", minLng, maxLng).findList();
		
		//未来可以再次优化优化？？？？？？？？
		if(result!=null){
			for(TParkInfoPro_Loc loc:result){
				long parkid = loc.parkInfo.parkId;
				loc.parkInfo = TParkInfoProd.findDataById(parkid);
			}
		}
		
		return result;
	}
}
