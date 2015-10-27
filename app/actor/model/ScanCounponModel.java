package actor.model;

import java.io.Serializable;

public class ScanCounponModel implements Serializable{

	public final static int TYPE_DEFAULT = 0;
	
	public final static int TYPE_SHARE = 1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String counponcode;
	
	public Long userid;
	
	//0:默认优惠卷  1:分享送优惠卷
	public int type;
	
	public int responseResult;
	 

}
