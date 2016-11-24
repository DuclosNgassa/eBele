package info.softsolution.ebele.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Note extends AbstractModel implements Parcelable {
	private String titel;
	private String content;
	private String type;

	public Note() {
	}

	public Note(int id) {
		this.id = id;
	}

	public Note(String titel, String content, String type) {
		super();
		this.titel = titel;
		this.content = content;
		this.type = type;
	}

	public static enum NoteType {
		NOTIZEN, TAGEBUCH
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Note)) {
			return false;
		}
		return ((Note) obj).getId() == this.getId();
	}

	@Override
	public int hashCode() {
		return 1;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public static final Creator<Note> CREATOR =
			new Creator<Note>() {
		public Note createFromParcel(Parcel source)
		{
			Note mNote = new Note();
			mNote.content = source.readString();
			mNote.titel = source.readString();
			mNote.id = source.readInt();
			mNote.created_at = source.readString();
			mNote.updated_at = source.readString();
			mNote.type = source.readString();
			
			return mNote;
			
		}

				
		@Override
		public Note[] newArray(int size) {
			return new Note[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(titel);
		parcel.writeString(content);
		parcel.writeInt(id);
		parcel.writeString(created_at);
		parcel.writeString(updated_at);
		parcel.writeString(type);
		
	}
	
	
}
