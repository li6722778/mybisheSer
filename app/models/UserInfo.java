package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import play.db.ebean.Model;

@Entity
public class UserInfo extends Model implements Serializable {

	private Long userid;

	private String userName;
	private String passwd;
	private long userPhone;

	private String carNumber;

	private int userType;

	private String admParkname;

	private double admLatitude;

	private double admLongitude;

	private String admDetail;

	private String admAddress;

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getAdmAddress() {
		return admAddress;
	}

	public void setAdmAddress(String admAddress) {
		this.admAddress = admAddress;
	}

	public String getAdmDetail() {
		return admDetail;
	}

	public void setAdmDetail(String admDetail) {
		this.admDetail = admDetail;
	}

	public String getAdmParkname() {
		return admParkname;
	}

	public void setAdmParkname(String admParkname) {
		this.admParkname = admParkname;
	}

	public double getAdmLatitude() {
		return admLatitude;
	}

	public void setAdmLatitude(double admLatitude) {
		this.admLatitude = admLatitude;
	}

	public double getAdmLongitude() {
		return admLongitude;
	}

	public void setAdmLongitude(double admLongitude) {
		this.admLongitude = admLongitude;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}


	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public static UserInfo authenticate(String username, String password) {

		if (username.equals("110") && password.equals("110")) {
			UserInfo users = new UserInfo();
			users.setUserid(8888888l);
			users.setUserName("auth user");
			users.setUserPhone(13551190701l);
			users.setUserType(1);
			return users;
		}
		return null;
		// return find.where().eq("username", username).eq("password",
		// password).findUnique();

	}

	public static UserInfo findUser(Long userid) {
		UserInfo user = new UserInfo();
		user.setUserid(userid);
		user.setUserName("woderchen");
		user.setUserPhone(13551190701l);
		user.setUserType(1);
		return user;
	}

	public long getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(long userPhone) {
		this.userPhone = userPhone;
	}
}
