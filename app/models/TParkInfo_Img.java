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
@Table(name = "tb_parking_img")
public class TParkInfo_Img extends Model{

	@Id
	public long parkImgId;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "parkId")
	public TParkInfo parkInfo;
	
	@Column(nullable = false, length = 100)
	@Size(max=100)
	public String imgUrlHeader;
	
	@Column(nullable = false)
	@Size(max=255)
	public String imgUrlPath;
	
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
