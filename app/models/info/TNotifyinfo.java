package models.info;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import play.data.format.Formats;

@Entity
@Table(name = "tb_notify_info")
public class TNotifyinfo {
	
	@Id
	@Expose
	public Long logId;
	
	
	@Expose
	public Long userId;
    
	@Column(columnDefinition = "integer(3) default 0")
	@Expose
	public String message;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date notifyDate;
	

	
}
