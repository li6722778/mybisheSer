package models.info;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_allowance")
public class TAllowance extends Model{

	@Id
	@Expose
	public long allowanceId;
		
	@Column(columnDefinition = "decimal(12,2) default 0.0")
	@Expose
	public double money;
	
	//1表示启用，0表示关闭
	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int isopen;
	
	//一次补贴的总数
	@Column(columnDefinition = "integer default 1")
	@Expose
	public int allowanceCount;
	
	//补贴发放事件点 1:用户预约成功并支付完立刻发, 2:用户离场订单结束后发====?目前暂时没有用，都是订单完成后发放
	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int allowancePayType;
	
	//补贴类型,1：按限单（前多少单发补贴），2：按交易量（超过多少单发补贴）
	@Column(columnDefinition = "integer(2) default 1")
	@Expose
	public int allowanceType;
	
	//补贴类型值（多少单）
	@Column(columnDefinition = "integer default 0")
	@Expose
	public int allowanceTypeValue;
	
	@Expose
	@Column(length = 2000)
	public String allowanceTimer;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date updateDate;
	
	@Expose
	public String updateName;
	
	public static Finder<Long, TAllowance> find = new Finder<Long, TAllowance>(
			Long.class, TAllowance.class);
	
	/**
	 * 
	 * @param log
	 */
	public static void saveData(TAllowance data) {
		data.updateDate = new Date();
		Ebean.save(data);
	}
	
	/**
	 * 
	 * @param data
	 */
	public static void updateData(TAllowance data) {
		data.updateDate = new Date();
		Ebean.update(data);
	}
	
	public static void deleteData(long logId){
		Ebean.delete(TAllowance.class, logId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static TAllowance findAllowance() {

		List<TAllowance> allowance = find.where().eq("allowancePayType", 0).findList();
		if(allowance==null||allowance.size()==0){
			return new TAllowance();
		}
		return allowance.get(allowance.size()-1);
	}
	
	/**
	 * 获得用户的数据
	 * @return
	 */
	public static TAllowance findAllowanceUser() {

		List<TAllowance> allowance = find.where().eq("allowancePayType", 1).findList();
		if(allowance==null||allowance.size()==0){
			return new TAllowance();
		}
		return allowance.get(allowance.size()-1);
	}
}
