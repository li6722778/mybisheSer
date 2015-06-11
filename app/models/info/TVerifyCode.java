package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_verify_code")
public class TVerifyCode  extends Model{
	
	@Id
	@Expose
	public Long phone;
	
	@Expose
	@Column(nullable = false, length = 10)
	public String verifycode;
	
	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	public Date createDate;
	
	public static Finder<Long, TVerifyCode> find = new Finder<Long, TVerifyCode>(
			Long.class, TVerifyCode.class);
	
	public static void saveData(final TVerifyCode bean) {
		Ebean.execute(new TxRunnable() {
			public void run() {
					// ------------生成主键，所有插入数据的方法都需要这个-----------
				Ebean.delete(TVerifyCode.class, bean.phone);
						Ebean.save(bean);
					
				// -------------end----------------
			}
		});
	}
	
	public static TVerifyCode getCode(long phone){
		return find.byId(phone);
	}
	
	public static void deletePhone(long phone){
		Ebean.delete(TVerifyCode.class, phone);
	}
}
