package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import play.data.format.Formats;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_parking_py")
public class TParkInfo_Py extends Model{

	@Id
	@Expose
	public Long parkPyId;
		
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double payTotal;
	
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double payActu;
	
	@Expose
	@Column(columnDefinition = "integer default 1")
	public int payMethod;
	
	@Expose
	@Column(columnDefinition = "integer default 0")
	public int ackStatus;
	
	
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
	public static Finder<Long, TParkInfo_Py> find = new Finder<Long, TParkInfo_Py>(
			Long.class, TParkInfo_Py.class);
	/**
	 * 得到总数
	 * @return
	 */
	public static double findDonePayment(){
		String sql = "SELECT sum(pay_total) as count FROM tb_parking_py where ack_status=1";
		
		SqlQuery sq = Ebean.createSqlQuery(sql);
		SqlRow sqlRow = sq.findUnique();
		Double db = sqlRow.getDouble("count");
		return  db==null?0: db;
	}
}
