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

import play.data.format.Formats;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_parking_prod_img")
public class TParkInfoPro_Img extends Model {

	@Id
	@Expose
	public Long parkImgId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parkId")
	public TParkInfoProd parkInfo;

	@Expose
	@Column(nullable = false, length = 100)
	@Size(max = 100)
	public String imgUrlHeader;

	@Expose
	@Column(nullable = false)
	@Size(max = 255)
	public String imgUrlPath;

	@Expose
	public String detail;

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
	public static Finder<Long, TParkInfoPro_Img> find = new Finder<Long, TParkInfoPro_Img>(
			Long.class, TParkInfoPro_Img.class);

	/**
	 * 新建或更新数据
	 * 
	 * @param userinfo
	 */
	public static void saveData(final TParkInfoPro_Img bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {

					// ------------生成主键，所有插入数据的方法都需要这个-----------
					if (bean.parkImgId == null || bean.parkImgId <= 0) {
						bean.parkImgId = TPKGenerator.getPrimaryKey(
								TParkInfoPro_Img.class.getName(), "parkImgId");
						Ebean.save(bean);
					} else {
						Ebean.update(bean);
					}
				
				// -------------end----------------
			}
		});

	}
	
	public static void saveDataWithoutIDPolicy(final TParkInfoPro_Img bean) {
			Ebean.save(bean);
	}
	

	/**
	 * 删除数据
	 * 
	 * @param id
	 */
	public static void deleteData(Long id) {
		Ebean.delete(TParkInfoPro_Img.class, id);
	}

	/**
	 * 找到唯一ID
	 * @param id
	 * @return
	 */
	public static TParkInfoPro_Img findImgByI(Long id){
		return find.byId(id);
	}
	
	/**
	 * 得到所有数据，有分页
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static CommFindEntity<TParkInfoPro_Img> findPageData(
			int currentPage, int pageSize, String orderBy) {

		CommFindEntity<TParkInfoPro_Img> result = new CommFindEntity<TParkInfoPro_Img>();

		Page<TParkInfoPro_Img> allData = find.where().orderBy(orderBy)
				.findPagingList(pageSize).setFetchAhead(false)
				.getPage(currentPage);

		result.setResult(allData.getList());
		result.setRowCount(allData.getTotalRowCount());
		result.setPageCount(allData.getTotalPageCount());
		return result;
	}

	/**
	 * 根据parkid得到所有的车位图片路径
	 * 
	 * @param parkId
	 * @return
	 */
	public static CommFindEntity<TParkInfoPro_Img> findData(long parkId) {

		CommFindEntity<TParkInfoPro_Img> result = new CommFindEntity<TParkInfoPro_Img>();

		List<TParkInfoPro_Img> allData = find.where().eq("parkId", parkId)
				.findList();

		result.setResult(allData);
		result.setRowCount(allData.size());
		result.setPageCount(1);
		return result;
	}

}
