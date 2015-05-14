package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_order")
public class TOrder extends Model{

	@Id
	@Expose
	private Long orderId;
	
	@Expose
	private String orderName;
	
	@Expose
	private String orderCity;
	
	@Expose
	@ManyToOne(fetch = FetchType.LAZY)
	private TParkInfo parkInfo;
	
	@Expose
	private int orderStatus;
	
	@Expose
	private Date orderDate;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	@Expose
	private Date startDate;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	@Expose
	private Date endDate;
	
	@OneToOne
	@Expose
	private TuserInfo userInfo;
}
