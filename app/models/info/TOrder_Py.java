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
import utils.Constants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_order_py")
public class TOrder_Py extends Model{

	@Id
	@Expose
	public Long parkPyId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderId")
	@Expose
	public TOrder order;
		
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
	public static Finder<Long, TOrder_Py> find = new Finder<Long, TOrder_Py>(
			Long.class, TOrder_Py.class);
	
	public static TOrder_Py findDataById(long id) {
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
	public static void saveData(final TOrder_Py bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				if (bean.parkPyId == null || bean.parkPyId <= 0) {
					bean.parkPyId = TPKGenerator.getPrimaryKey(TOrder_Py.class.getName(), "parkPyId");
					Ebean.save(bean);
				}else{
					Ebean.update(bean);
				}

			}
		});
	}
	public static double findDataBystaute(long id){
		String sql = "SELECT sum(pay_total) as count FROM tb_order_py where ack_status="+Constants.ORDER_TYPE_FINISH;
		SqlQuery sq = Ebean.createSqlQuery(sql);
		SqlRow sqlRow = sq.findUnique();
		Double db = sqlRow.getDouble("count");
		return  db==null?0: db;
	}
	
	
}
