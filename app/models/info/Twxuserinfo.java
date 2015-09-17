package models.info;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;
import com.google.gson.annotations.Expose;

/**
 * @author mxs E-mail:308348194@qq.com
 * @version 创建时间：2015年9月9日 下午5:25:18
 */

@Entity
@Table(name = "tb_wxuser")
public class Twxuserinfo extends Model{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@Column(length = 100)
	public String openid;
	
	@Column(length = 100)
	@Expose
	public String nickname;
	
	@Column(length = 1000)
	@Expose
	public String headimgurl;
	
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "timestamp NULL")
	@Expose
	public Date sharetDate;
	
	
	@Column(length = 1000)
	@Expose
	public String shareword;
	
	@Expose
	public int getcoupnprice;
	
	
	@Column(length = 50)
	@Expose
	public String uniqueurl;
	
	
	
	@Column(length = 30)
	@Expose
	public String userphone;
	
	
	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<String, Twxuserinfo> find = new Finder<String, Twxuserinfo>(
			String.class, Twxuserinfo.class);
	
	
	
	public static void saveuserinfo(final String openid,final String nickname,final String headimgurl,
			final String shareword, final int getcoupnprice,final String uniqueurl,final String userphone) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				Twxuserinfo userinfo= new Twxuserinfo();
				userinfo.openid=openid;
				userinfo.nickname =nickname;
				userinfo.headimgurl=headimgurl;
			    userinfo.sharetDate=new Date();
			    userinfo.shareword=shareword;
			    userinfo.getcoupnprice=getcoupnprice;
			    userinfo.uniqueurl=uniqueurl;
			    userinfo.userphone=userphone;
				Ebean.save(userinfo);

			}

		});

	}
	
	public static void updateuserinfo(final String url,final String phonenumber,final String openid) {

		Ebean.execute(new TxRunnable() {
			public void run() {
				Twxuserinfo userinfo= find.where().eq("uniqueurl", url).eq("openid", openid).findUnique();
				userinfo.userphone=phonenumber;
				Ebean.update(userinfo);
			}

		});

	}


	/**
	 * 得到该分享url获得优惠劵用户列表
	 * 
	 * @param parkId
	 * @return
	 */
	public static List<Twxuserinfo> getwxuserinfos(String url) {
		List<Twxuserinfo> result = find.where().eq("uniqueurl", url).ne("getcoupnprice", null).findList();
		return result;
	}
	
/**
 * 
 */
	public static Twxuserinfo getuser(String url,String openid)
	{
		Twxuserinfo wxuserinfo = find.where().eq("uniqueurl", url).eq("openid", openid).findUnique();
		if(wxuserinfo!=null)
		{
			return wxuserinfo;
		}
		
		return null;
		
	}
	
	/**
	 * 根据用户唯一标识openid获取用户信息
	 * 
	 * @param openid
	 * @return
	 */
	public static Twxuserinfo getwxuserinfo(String openid) {
		List<Twxuserinfo> result = find.where().eq("openid", openid).findList();
		if(result!=null&&result.size()>0)
		{	Twxuserinfo wxuserinfo=result.get(result.size()-1);
		    return wxuserinfo;
		}

		return null;
	}
	
	
	public static Twxuserinfo getsharestatue(String openid,String url) {
		Twxuserinfo wxuserinfo = find.where().eq("uniqueurl", url).eq("openid", openid).findUnique();
		if(wxuserinfo!=null)
		{
			return wxuserinfo;
		}
		
		return null;
	}
	
	
}
