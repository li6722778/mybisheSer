package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import play.data.format.Formats;
import play.db.ebean.Model;

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

}
