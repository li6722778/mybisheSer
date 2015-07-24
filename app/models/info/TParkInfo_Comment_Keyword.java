package models.info;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;
import utils.CommFindEntity;
import views.html.commentskeyword;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;


@Entity
@Table(name = "tb_parking_comment_keyword")

public class TParkInfo_Comment_Keyword extends Model {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Expose
	public Long KeywordId;
	
	@Expose
	public String content;
	
	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	@Expose
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp")
	public Date createDate;
	

	
	
	
	
	
		
	
	/**
	 * 添加关键字
	 * 
	 * @param userinfo
	 */
	public static void saveData( final String keyword) {
		Ebean.execute(new TxRunnable() {
			public void run() {
				Date date =new  Date();
				TParkInfo_Comment_Keyword KEYWORD = new TParkInfo_Comment_Keyword();
				KEYWORD.setContent(keyword);
				KEYWORD.setCreateDate(date);
				KEYWORD.save();
			}
		});

	}
	
	
	/**
	 * 删除关键字
	 * 
	 * @param id
	 */
	public static void deleteData(Long id) {
		Ebean.delete(TParkInfo_Comment_Keyword.class, id);
	}
		
	
	/**
	 * 
	   关键字搜索
	 */
	public static Page<TParkInfo_Comment_Keyword> pageByFilter(int currentPage,	int pageSize, String orderBy,String searchObj) {
		 ExpressionList<TParkInfo_Comment_Keyword> elist = find.where();
		if (searchObj != null && !searchObj.trim().equals("")) {
				elist.like ("content" , "%"+searchObj+"%");		
		}
		Page<TParkInfo_Comment_Keyword> allData = elist.orderBy(orderBy).findPagingList(pageSize).setFetchAhead(false).getPage(currentPage);	
		return allData;
	}

	
	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, TParkInfo_Comment_Keyword> find = new Finder<Long, TParkInfo_Comment_Keyword>(
			Long.class, TParkInfo_Comment_Keyword.class);


	
	
	
	/**
	 * 
	   关键字遍历
	 * @return 
	 * @return 
	 */
	public static  String getallkeywords() {
	String allData = null;	
	List<TParkInfo_Comment_Keyword> Comment_Keyword = find.all();	
	for(int i = 0; i < Comment_Keyword.size(); i++)  
    {  
		allData+=Comment_Keyword.get(i).getContent();          
    }  
		return allData;
	}
}
