package models;

import models.info.TOrder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChebolePayOptions {

	@Expose
	public String payInfo;
	@Expose
	public TOrder order;
	@Expose
	public long paymentId;
	
	@Expose
    public double payOrginalPriceFoTotal;
	
	@Expose
    public double payOrginalPrice;
	
	@Expose
    public double payActualPriceForTotal;
	
	@Expose
    public double payActualPrice;
	
	//用户补贴
	@Expose
    public double userAllowance;
	
	//用户补贴
	@Expose
    public String userAllowanceDescription;
	
	@Expose
	public double counponUsedMoneyForTotal;
	
	@Expose
	public double counponUsedMoneyForIn;
	
	@Expose
	public double counponUsedMoneyForOut;
	
	@Expose
	public double parkSpentHour;
	
	@Expose
    public boolean isDiscount;
	@Expose
	public boolean useCounpon;
	@Expose
	public long counponId;
	
	@Expose
	public String keepToDate;
	

}
