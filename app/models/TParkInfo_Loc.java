package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
@Table(name = "tb_parking_loc")
public class TParkInfo_Loc extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public long parkLocId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "parkId")
	public TParkInfo parkInfo;
	
	public int type;
	
	public double latitude;

	public double longitude;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP) 
	public Date createDate;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP) 
	public Date updateDate;
	
	@Column(length = 50)
	@Size(max=50)
	public String createPerson;
	
	@Column(length = 50)
	@Size(max=50)
	public String updatePerson;
}
