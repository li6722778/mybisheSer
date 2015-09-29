package models;

import com.google.gson.annotations.Expose;

/**
 * 导出数据bean
 * @author woderchen
 *
 */
public class ExportBean {

	@Expose
	public String fileName;
	
	@Expose
	public String createDate;
	
	@Expose
	public int fileSize;
	
}
