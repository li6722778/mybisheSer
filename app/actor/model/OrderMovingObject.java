package actor.model;

import java.io.Serializable;

public class OrderMovingObject implements Serializable {

	private Long orderId;
	
	private int status;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
