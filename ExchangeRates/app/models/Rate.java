package models;


/**
 * @author john
 * Represents a conversion rate at a particular date
 */
public class Rate {
	
	private String date;
	private String rate;
	
	public Rate(String date, String rate){
		this.date = date;
		this.rate = rate;
	}

	public String getDate() {
		return date;
	}

	public String getRate() {
		return rate;
	}

}
