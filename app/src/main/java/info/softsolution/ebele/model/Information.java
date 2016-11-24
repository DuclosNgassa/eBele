package info.softsolution.ebele.model;

public class Information {
    private int id;
    private String titel;
    private String beschreibung;
    private String image;
    private String timeStamp;
    private String link;
    private String adresse;
    private String typ;

    public static enum infoTyp {
		SCHWANGERSCHAFT, KLINIK, STUDENTIN, AUSLAENDERIN
	};
   
    public Information() {
    }
 
    public Information(int id, String titel, String image, String beschreibung,
            String timeStamp, String link, String adresse, String typ) {
        super();
        this.id = id;
        this.titel = titel;
        this.image = image;
        this.beschreibung = beschreibung;
        this.timeStamp = timeStamp;
        this.link = link;
        this.adresse = adresse;
        this.typ = typ;
    }
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
  
    public String getImage() {
        return image;
    }
 
    public void setImge(String image) {
        this.image = image;
    }
  
 
    public String getTimeStamp() {
        return timeStamp;
    }
 
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}
 
}
