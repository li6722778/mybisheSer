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

import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;
import utils.Constants;
import utils.DateHelper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_order_his_py")
public class TOrderHis_Py extends Model{

	@Id
	@Expose
	public Long parkPyId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderId")
	@Expose
	public TOrderHis order;
		
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double payTotal;
	
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double payActu;
	
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double couponUsed;
	
	@Expose
	@Column(columnDefinition = "integer default 1")
	public int payMethod; // 1：支付宝 9：现金
	
	@Expose
	@Column(columnDefinition = "integer default 0")
	public int ackStatus;  //0:初始状态 1：支付成功 2：等待结果确认 3:支付失败
	
	
	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	public Date payDate;

	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	public Date ackDate;
	
	@Expose
	@Column(length = 50)
	@Size(max = 50)
	public String createPerson;

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TOrderHis_Py> find = new Finder<Long, TOrderHis_Py>(
			Long.class, TOrderHis_Py.class);
	
	public static TOrderHis_Py findDataById(long id) {
		return find.byId(id);
	}
	
	/**
	 * 得到总数
	 * @return
	 */
	public static double findDonePayment(){
		String sql = "SELECT sum(pay_total) as count FROM tb_order_py where ack_status="+Constants.ORDER_TYPE_FINISH;
		
		SqlQuery sq = Ebean.createSqlQuery(sql);
		SqlRow sqlRow = sq.findUnique();
		Double db = sqlRow.getDouble("count");
		return  db==null?0: db;
	}
	
	/**
	 * 保存付款
	 * @param bean
	 */
	public static void saveDataWithoutIDPolicy(final TOrderHis_Py bean) {
		Ebean.save(bean);
	}
	
	//判断当天立减数量是否符合
		public static boolean checklijiannum(long userid) {
			//分别查询历史和当前支付订单中为21（立减的）
	String sql = "select * from tb_order a left join tb_order_py b on a.order_id=b.orderId where a.userid="+userid+" and b.pay_method=21 and date_format(a.order_date,'%Y-%m-%d')='"+DateHelper.format(new Date(), "yyyy-MM-dd")+"'"+
	" union select * from tb_order_his a left join tb_order_his_py b on a.order_id=b.orderId where a.userid="+userid+" and b.pay_method=21 and date_format(a.order_date,'%Y-%m-%d')='"+DateHelper.format(new Date(), "yyyy-MM-dd")+"'";
	Logger.debug("lijian userid"+userid);
			SqlQuery sq = Ebean.createSqlQuery(sql);
			//获取已立减数量
			int hasnum = sq.findList().size();
			TOptions options = TOptions.findOption(17);
			if (options.textObject != null) {
				int Maxlijiannum = Integer.valueOf(options.textObject.toString().trim()); 
				if (hasnum < Maxlijiannum) {
					return true;
				} else {
					return false;
				}
			}else
				return false;
			
//			
//			TLiJian lijian = TLiJian.findDataById(userid);
//			if (lijian == null) {
//				Logger.debug("@@@@@@@@@@@@@@@@@@@@@@@@@");
//				return true;
//			} else {
//				String lijiantime = DateHelper.format(lijian.lijianDate, "yyyy-MM-dd");
//				if (lijiantime.equals(DateHelper.format(new Date(), "yyyy-MM-dd"))) {
//					TOptions options = TOptions.findOption(17);
//					if (options.textObject != null) {
//						int Maxlijiannum = Integer.valueOf(options.textObject.toString().trim());
//						Logger.debug("@@@@@@@@@@@@@@@@@@@@@@@@@", Maxlijiannum);
//						if (lijian.lijiannum < Maxlijiannum) {
//							return true;
//						} else {
//							return false;
//						}
	//
//					}else
//					{
//						Logger.debug("###################");
//					}
//				}
//				return true;
	//
//			}

		}
}
