package info.softsolution.ebele.model;

import java.util.Date;

public class Nachricht extends AbstractModel
{
	private String text;
	private boolean isSelf;
	private Date created_at_date;
	private String fromName;
	
	public Nachricht(){}
	
	public Nachricht(String user_email, String fromString, String text,Date created_at, boolean isSelf) {
		this.user_email = user_email;
		this.text = text;
		this.isSelf = isSelf;
		this.created_at_date = created_at;
		this.fromName = fromString;
	}

	public Nachricht(String user_email, String text)
	{
		this.user_email = user_email;
		this.text = text;
//		this.created_at = created_at;
	}

	public Nachricht(String user_name, String text, boolean isSelf, String created_at_string)
	{
		this.text = text;
		this.isSelf = isSelf;
		this.fromName = user_name;
		this.created_at = created_at_string;
		
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isSelf() {
		return isSelf;
	}

	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public Date getCreated_at_date() {
		return created_at_date;
	}

	public void setCreated_at_date(Date created_at_date) {
		this.created_at_date = created_at_date;
	}

}
