package info.softsolution.ebele.model;

public class Meldung extends AbstractModel
{
    private String wer;
    private String was;
    private String wann;
    private String email;
	
    public Meldung(String wer, String was, String wann, String email) {
		super();
		this.wer = wer;
		this.was = was;
		this.wann = wann;
		this.email = email;
	}

	public String getWer() {
		return wer;
	}

	public void setWer(String wer) {
		this.wer = wer;
	}

	public String getWas() {
		return was;
	}

	public void setWas(String was) {
		this.was = was;
	}

	public String getWann() {
		return wann;
	}

	public void setWann(String wann) {
		this.wann = wann;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
    
	
}
