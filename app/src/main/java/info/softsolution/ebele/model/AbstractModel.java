package info.softsolution.ebele.model;


public abstract class AbstractModel 
{
	protected int id;
	protected String created_at;
	protected String user_email;
	protected String updated_at;
	
	protected void reset()
	{
		id = 0;
	}
	
	public int getId() 
	{
		return id;
	}
	
	public String getIdString()
	{
		return String.valueOf(id);
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public String getCreated_at() 
	{
		return created_at;
	}

	public void setCreated_at(String created_at) 
	{
		this.created_at = created_at;
	}
	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
}
