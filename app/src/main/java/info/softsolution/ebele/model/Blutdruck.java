package info.softsolution.ebele.model;


public class Blutdruck extends AbstractModel{

	private int systolisch;
	private int diastolisch;
	
	public Blutdruck() {
		super();
	}

	public Blutdruck(int systolisch, int diastolisch) {
		super();
		this.systolisch = systolisch;
		this.diastolisch = diastolisch;
	}

	public int getSystolisch() {
		return systolisch;
	}

	public String getSystolischString() {
		return String.valueOf(systolisch);
	}

	public void setSystolisch(int systolisch) {
		this.systolisch = systolisch;
	}

	public int getDiastolisch() {
		return diastolisch;
	}

	public String getDiastolischString() {
		return String.valueOf(diastolisch);
	}
	
	public void setDiastolisch(int diastolisch) {
		this.diastolisch = diastolisch;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	
	
}
