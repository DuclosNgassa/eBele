package info.softsolution.ebele.model;

import java.util.Date;

public class User extends Person{
	
	private String secret;
	private int schwangerschaftswoche;
	private Date stichtag;
	private String status;
	private String type;
	
	public User() {
	}

	public static enum userStatus {
		ONLINE, OFFLINE, GESPERRT, GELOESCHT
	};

	public static enum kontoType{
		ADMIN, USER
	};
	
	public User(int id, String name, String email, String passwort,
			String created_at, String updated_at, String status) {
		super();
		this.id = id;
		this.name = name;
		this.user_email = email;
		this.passwort = passwort;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.status = status;
	}

	public User(String name, String email, String created_at,String status) {
		super();
		this.name = name;
		this.user_email = email;
		this.created_at = created_at;
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public int getSchwangerschaftswoche() {
		return schwangerschaftswoche;
	}

	public void setSchwangerschaftswoche(int schwangerschaftswoche) {
		this.schwangerschaftswoche = schwangerschaftswoche;
	}

	public Date getStichtag() {
		return stichtag;
	}

	public void setStichtag(Date stichtag) {
		this.stichtag = stichtag;
	}

	@Override
	public boolean equals(Object obj)
	{
		if((obj == null) || !(obj instanceof User))
		{
			return false;
		}
		return ((User)obj).getId() == getId(); 
	}
	
	@Override
	public int hashCode()
	{
		return 1;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
