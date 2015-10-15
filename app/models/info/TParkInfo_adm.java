package models.info;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
@Table(name = "tb_parking_adm")
public class TParkInfo_adm extends Model {

	@Id
	public Long parkAdmId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parkId")
	public TParkInfoProd parkInfo;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userid")
	public TuserInfo userInfo;

	@Column(length = 200)
	public String deviceid;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date lastloginDate;

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TParkInfo_adm> find = new Finder<Long, TParkInfo_adm>(Long.class, TParkInfo_adm.class);

	public static List<TParkInfoProd> findAdmPartInfo(long userId) {
		List<TParkInfo_adm> adm = find.fetch("parkInfo").where().eq("userid", userId).findList();
		List<TParkInfoProd> prodArray = new ArrayList<TParkInfoProd>();

		if (adm != null) {
			for (TParkInfo_adm a : adm) {
				prodArray.add(a.parkInfo);
			}
		}
		return prodArray;
	}

	// 更新管理员的硬件id和登录时间
	public static void registerAdmLogin(long userid, String clientId) {
		if (userid > 0 && clientId != null && !clientId.trim().equals("")) {
			List<TParkInfo_adm> adms = find.where().eq("userid", userid).findList();
			if (adms != null && adms.size() > 0) {
				Ebean.execute(new TxRunnable() {
					public void run() {
						for (TParkInfo_adm adm : adms) {
							adm.deviceid = clientId;
							adm.lastloginDate = new Date();
							Set<String> options = new HashSet<String>();
							options.add("deviceid");
							options.add("lastloginDate");
							Ebean.update(adm, options);
						}
					}
				});
			}
		}
	}
	
	/**
	 * 返回device id不为空的所有的数据
	 * @return
	 */
	public static List<TParkInfo_adm> getAllUserIdAndDeviceId(){
		String sql = "select park_adm_id,parkId, userid,deviceid from tb_parking_adm where deviceid is not null group by userid";
		final RawSql rawSql = RawSqlBuilder.unparsed(sql)
				.columnMapping("park_adm_id", "parkAdmId")
				.columnMapping("parkId", "parkInfo.parkId")
				.columnMapping("userid", "userInfo.userid")
		        .columnMapping("deviceid", "deviceid").create();
		List<TParkInfo_adm> adms = Ebean.find(TParkInfo_adm.class).setRawSql(rawSql).findList();
		return adms;
	}

	public static List<TParkInfo_adm> findAdmPartInfoByParkId(long parkid) {
		List<TParkInfo_adm> adms = find.fetch("userInfo").where().eq("parkId", parkid).findList();
		return adms;
	}

	public static Page<TParkInfoProd> findDataByUserId(int currentPage, int pageSize, String orderBy, long userid) {

		final Page<TParkInfo_adm> allData = find.where().eq("userid", userid).orderBy(orderBy).findPagingList(pageSize)
				.setFetchAhead(false).getPage(currentPage);
		;

		final List<TParkInfoProd> prodArray = new ArrayList<TParkInfoProd>();

		if (allData != null) {
			for (TParkInfo_adm adm : allData.getList()) {
				prodArray.add(adm.parkInfo);
			}
		}

		Page page = new Page() {
			@Override
			public String getDisplayXtoYofZ(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return allData.getDisplayXtoYofZ(arg0, arg1);
			}

			@Override
			public List getList() {
				// TODO Auto-generated method stub
				return prodArray;
			}

			@Override
			public int getPageIndex() {
				// TODO Auto-generated method stub
				return allData.getPageIndex();
			}

			@Override
			public int getTotalPageCount() {
				// TODO Auto-generated method stub
				return allData.getTotalPageCount();
			}

			@Override
			public int getTotalRowCount() {
				// TODO Auto-generated method stub
				return allData.getTotalRowCount();
			}

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return allData.hasNext();
			}

			@Override
			public boolean hasPrev() {
				// TODO Auto-generated method stub
				return allData.hasPrev();
			}

			@Override
			public Page next() {
				// TODO Auto-generated method stub
				return allData.next();
			}

			@Override
			public Page prev() {
				// TODO Auto-generated method stub
				return allData.prev();
			}

		};

		return page;
	}

	public static TParkInfo_adm findByUserAndPark(long userId, long parking) {
		TParkInfo_adm adm = find.where().eq("userid", userId).eq("parkId", parking).findUnique();
		return adm;
	}

	public static int findByUserAndParkCount(long userId, long parking) {
		int adm = find.where().eq("userid", userId).eq("parkId", parking).findRowCount();
		return adm;
	}

	public static TParkInfo_adm findDataById(long parkAdmId) {
		return find.byId(parkAdmId);
	}

	public static void saveData(final TParkInfo_adm info) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				// ------------生成主键，所有插入数据的方法都需要这个-----------
				if (info.parkAdmId == null || info.parkAdmId <= 0) {
					info.parkAdmId = TPKGenerator.getPrimaryKey(TParkInfo_adm.class.getName(), "parkAdmId");
					Ebean.save(info);
				} else {
					Ebean.update(info);
				}
			}
		});
		// -------------end----------------

	}

	public static boolean deleteDataByUser(Long userId) {
		String sql = "delete from tb_parking_adm where userid=:userid";
		SqlUpdate update = Ebean.createSqlUpdate(sql).setParameter("userid", userId);
		int rows = update.execute();
		return true;

	}

	public static boolean deleteDataByUserAndParkid(Long userId, long parkid) {
		String sql = "delete from tb_parking_adm where userid=:userid and parkId=:parkid";
		SqlUpdate update = Ebean.createSqlUpdate(sql).setParameter("userid", userId).setParameter("parkid", parkid);
		int rows = update.execute();
		return true;

	}
}
