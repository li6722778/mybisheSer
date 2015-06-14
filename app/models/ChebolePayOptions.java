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
	
}
