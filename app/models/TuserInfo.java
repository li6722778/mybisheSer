package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Email;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.google.gson.annotations.Expose;

//@NamedUpdates(value = { 
//        @NamedUpdate( name = "setTitle", 
//                notifyCache = false, 
//                update = "update topic set title = :title, postCount = :postCount where id = :id"), 
//        @NamedUpdate( name = "setPostCount",
//                notifyCache = false,
//                update = "update f_topic set post_count = :postCount where id = :id"), 
//        @NamedUpdate( name = "incrementPostCount", 
//                notifyCache = false, 
//                update = "update Topic set postCount = postCount + 1 where id = :id") }) 

@Entity
@Table(name = "tb_user")
public class TuserInfo extends Model implements Serializable {

	private static final long serialVersionUID = -1278830614814105771L;

	@Id
	@Expose
	public Long userid;
	
	@Constraints.Required
	@Column(nullable = false, length = 50)
	@Size(max=50)
	@Expose
	public String userName;
	
	public String passwd;
	
	@Column(nullable = false, length = 30)
	@Size(max=30)
	@Expose
	public long userPhone;

	//@Column(columnDefinition = "TEXT")
	@Email
	@Expose
	public String email;

	///*用户类型,10:普通用户,20:车位管理员,30:市场用户,99超级用户*/
	@Expose
	public int userType;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	@Expose
	public Date createDate;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	@Expose
	public Date updateDate;
	
	@Column(length = 50)
	@Size(max=50)
	@Expose
	public String createPerson;
	
	@Column(length = 50)
	@Size(max=50)
	@Expose
	public String updatePerson;

	//查询finder，用于其他方法中需要查询的场景 
	public static Finder<Long,TuserInfo> find = new Finder<Long,TuserInfo>(Long.class, TuserInfo.class); 
	 
	/**
	 * 登录http header认证
	 * @param username
	 * @param password
	 * @return
	 */
	public static TuserInfo authenticate(String username, String password) {
		TuserInfo userInfo = find.where().eq("userName", username).eq("passwd",password).findUnique();
		
		return userInfo;
	}

	/**
	 * 得到所有的数据
	 * @param page
	 * @param pageSize
	 * @param sortBy
	 * @param order
	 * @param filter
	 * @return
	 */
    public static CommFindEntity<TuserInfo> findData(int currentPage, int pageSize, String orderBy) {
    	
    	CommFindEntity<TuserInfo> result = new CommFindEntity<TuserInfo>();

    	Page<TuserInfo> allData = find.where()
                .orderBy(orderBy)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(currentPage);
    	
    	result.setResult(allData.getList());
    	result.setRowCount(allData.getTotalRowCount());
    	result.setPageCount(allData.getTotalPageCount());
        return result;
    }
	
	/**
	 * 根据主键查询数据
	 * @param userid
	 * @return
	 */
	public static TuserInfo findDataById(Long userid) {
		TuserInfo userInfo = find.byId(userid);
		return userInfo;
	}
	
	/**
	 * 新建或更新数据
	 * @param userinfo
	 */
	public static void saveData(TuserInfo userinfo){
		//------------生成主键，所有插入数据的方法都需要这个-----------
		if(userinfo.userid==null||userinfo.userid<=0){
			userinfo.userid = TPKGenerator.getPrimaryKey(TuserInfo.class.getName(), "userid");
		}
		//-------------end----------------
		
		 Ebean.save(userinfo);
	}
	
	/**
	 * 删除数据
	 * @param userid
	 */
	public static void deleteData(Long userid){
		Ebean.delete(TuserInfo.class, userid);
	}

}
