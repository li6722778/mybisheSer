package models.info;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Email;
import play.db.ebean.Model;
import utils.CommFindEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
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
	
	@Constraints.Required
	@Expose
	public String passwd;
	

	@Column(nullable = false, length = 30)
	@Expose
	public long userPhone;

	//@Column(columnDefinition = "TEXT")
	@Expose
	public String email;

	///*用户类型,10:普通用户,20:车位管理员,30:市场用户,99超级用户*/
	@Expose
	@Column(columnDefinition = "integer(3) default 10")
	public int userType;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date createDate;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
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
	 * 得到总数
	 * @return
	 */
	public static int findCount(){
		return find.findRowCount();
	}
	
	/**
	 * 登录http header认证
	 * @param username
	 * @param password
	 * @return
	 */
	public static TuserInfo authenticate(String userPhone, String password) {
		
		List<TuserInfo> userInfos = find.where().eq("userPhone", userPhone).eq("passwd",password).findList();
		
		return (userInfos==null||userInfos.size()<=0)?null:userInfos.get(0);
	}

	/**
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param orderBy
	 * @return
	 */
	public static Page<TuserInfo> page(int currentPage,int pageSize, String orderBy) {
		Page<TuserInfo> allData = find.where()
                .orderBy(orderBy)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(currentPage);
        return allData;
    }
	

	public static Page<TuserInfo> pageByTypeAndFilter(int currentPage,int pageSize, String orderBy,int type,String filter) {
		
		ExpressionList<TuserInfo> elist = find.where().between("userType", type, type+9);
		if(filter!=null&&!filter.trim().equals("")){
			elist.or(Expr.ilike("userPhone", "%"+filter+"%"), Expr.ilike("userName", "%"+filter+"%"));
		}
		Page<TuserInfo> allData=elist.orderBy(orderBy)
        .findPagingList(pageSize)
        .setFetchAhead(false)
        .getPage(currentPage);
		
        return allData;
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

    	Page<TuserInfo> allData = page(currentPage,pageSize,orderBy);;
    	
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
	 * 根据电话查询数据
	 * @param userid
	 * @return
	 */
	public static TuserInfo findDataByPhoneId(Long userPhone) {
		List<TuserInfo> userInfos = find.where().eq("userPhone", userPhone).findList();
		return (userInfos==null||userInfos.size()<=0)?null:userInfos.get(0);
	}
	
	/**
	 * 新建或更新数据
	 * @param userinfo
	 */
	public static void saveData(TuserInfo userinfo){
		
		Ebean.beginTransaction(); 
		try{
		//------------生成主键，所有插入数据的方法都需要这个-----------
		if(userinfo.userid==null||userinfo.userid<=0){
			userinfo.userid = TPKGenerator.getPrimaryKey(TuserInfo.class.getName(), "userid");
			userinfo.createDate = new Date();
			 Ebean.save(userinfo);
		}else{
			userinfo.updateDate = new Date();
			Ebean.update(userinfo);
		}
		Ebean.commitTransaction();  
		}finally{
			 Ebean.endTransaction();
		}
		//-------------end----------------
		
		
	}
	
	/**
	 * 更新用户权限
	 * @param userid
	 * @param roleType
	 */
	public static TuserInfo updateRole(Long userid, int roleType){
		TuserInfo userInfo = find.byId(userid);
		userInfo.userType = roleType;
		Ebean.update(userInfo);
		return userInfo;
	}
	
	/**
	 * 删除数据
	 * @param userid
	 */
	public static void deleteData(Long userid){
		Ebean.delete(TuserInfo.class, userid);
	}

}
