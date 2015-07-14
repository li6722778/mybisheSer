package models.info;

import javax.persistence.Entity;

@Entity
public class IncomeCountEntity {

	public double payTotal;
	
	public double couponUsed;
	
	public int payMethod;
	
	public int ackStatus;
	
}
