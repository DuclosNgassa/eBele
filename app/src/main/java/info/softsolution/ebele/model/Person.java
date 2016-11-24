package info.softsolution.ebele.model;

public abstract class Person extends AbstractModel
{
	protected String name;
	protected String passwort;
	protected String type;
	
	public static enum kontoType{ADMIN, BENUTZER};


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswort() {
		return passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
