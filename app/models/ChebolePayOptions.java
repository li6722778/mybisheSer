package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChebolePayOptions {

	@Expose
	public String payInfo;
	@Expose
	public long orderId;
	@Expose
	public long paymentId;
	@Expose
    public double payOrginalPrice;
	
	@Expose
    public double payActualPrice;
	
	@Expose
    public boolean isDiscount;
	@Expose
	public boolean useCounpon;
	@Expose
	public long counponId;
}
