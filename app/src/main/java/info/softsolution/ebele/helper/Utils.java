package info.softsolution.ebele.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import info.softsolution.ebele.model.Blutdruck;
import info.softsolution.ebele.model.Information;
import info.softsolution.ebele.model.Meldung;
import info.softsolution.ebele.model.Note;
import info.softsolution.ebele.model.User;

public class Utils 
{
	private Context context;
	private SharedPreferences sharedPref;
	
	public static final StringBuilder STRING_BUILDER = new StringBuilder();

	private static final String KEY_SHARED_PREF = "eSchwangerschaft_Chat";
	private static final int KEY_MODE_PRIVATE = 0;
	private static final String KEY_SESSION_ID = "sessionId";
	private static final String FLAG_MESSAGE = "message";
	public static final String ADMIN_EMAIL = "ndanjid@yahoo.fr";
	public static final String MELDUNG_EMPTY_FELD = "Bitte f�llen Sie die mit * gekennzeichneten Felder aus!";
	public static enum METHOD{ create, 
							   read, 
							   readAll, 
							   readAllWithTyp, 
							   update, 
							   delete, 
							   deleteAll, 
							   updateStichtag, 
							   updateUserStatus,
							   wiederherstellung,
							   sperren,
							   entsperren,
							   count,
							   status,
							   verbindung,
							   bewertung};
	
    public static enum STATUS{ONLINE,OFFLINE,GESPERRT,GESCHLOSSEN,GELOESCHT}; 
 
							   
	public Utils(Context context)
	{
		this.context = context;
		sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF, KEY_MODE_PRIVATE);
	}
	
	public void storeSessionId(String sessionId)
	{
		Editor editor = sharedPref.edit();
		editor.putString(KEY_SESSION_ID, sessionId);
		editor.commit();
	}
	
	public String getSessionId()
	{
		return sharedPref.getString(KEY_SESSION_ID, null);
	}
	
	public String getSendMessageJSON(String message)
	{
		String json = null;
		try{
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("message", message);
			
			json = jObj.toString();
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		
		return json;
	}
	
	public static void clearFelder(List<EditText> listEditText)
	{
		Iterator<EditText> iteratorEditText = listEditText.listIterator();
		while(iteratorEditText.hasNext())
		{
			EditText editText = iteratorEditText.next();
			editText.setText("");
		}
	}
	
	public static void showToast(Context context, String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	

		public static Note convertJsonObjectToNote(JSONObject jo)
				throws JSONException 
		{
			String _titel = jo.getString("titel");
			String _content = jo.getString("content");
			Object _erstellt_datum = jo.getString("erstellt_datum");
			Object _update_datum = jo.getString("update_datum");
			String _email_adresse = jo.getString("email_adresse");
			String _typ = jo.getString("typ");
			int _id  = jo.getInt("id");
			Note _note = new Note(_titel, _content, _typ);
			_note.setId(_id);
			_note.setUpdated_at(_update_datum.toString());
			_note.setUser_email(_email_adresse);
			_note.setCreated_at(_erstellt_datum.toString());
			return _note;
		}

		public static User convertJsonObjectToUser(JSONObject jo)
				throws JSONException 
		{
			String name = jo.getString("name");
			String email = jo.getString("email");
			String anmeldeDatum = jo.getString("created_at");
			String status = jo.getString("status");
			User user = new User(name, email, anmeldeDatum, status);
			return user;
		}

		public static Meldung convertJsonObjectToMeldung(JSONObject jo)
				throws JSONException 
		{
			String wer = jo.getString("wer");
			String email = jo.getString("email_adresse");
			String wann = jo.getString("wann");
			String was = jo.getString("was");
			Meldung meldung = new Meldung(wer, was, wann, email);
			
			return meldung;
		}

		public static long convertTimeToMillis(String dateInString) throws ParseException
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.GERMANY);
			GregorianCalendar gC = new GregorianCalendar();
			
			Date date = sdf.parse(dateInString);
			gC.setTime(date);
			return gC.getTimeInMillis();
			
		}

		public static Blutdruck convertJsonObjectToBlutdruck(JSONObject jo)
				throws JSONException 
		{
			int _systolisch = jo.getInt("systolisch");
			int _diastolisch = jo.getInt("diastolisch");
			Object _erstellt_datum = jo.getString("erstellt_datum");
			String _email_adresse = jo.getString("email_adresse");
			int _id  = jo.getInt("id");
			Blutdruck _blutdruck = new Blutdruck(_systolisch, _diastolisch);
			_blutdruck.setId(_id);
			_blutdruck.setUser_email(_email_adresse);
			_blutdruck.setCreated_at(_erstellt_datum.toString());
			return _blutdruck;
		}		

		public static Information parseInfo(JSONObject infoObj) throws JSONException {
			Information item = new Information();
			item.setId(infoObj.getInt("id"));
			item.setTitel(infoObj.getString("titel"));
	 
			// Image might be null sometimes
			String image = infoObj.isNull("image") ? null : infoObj
			        .getString("image");
			item.setImge(image);
			item.setBeschreibung(infoObj.getString("beschreibung"));
			item.setTimeStamp(infoObj.getString("erstellt_datum"));
	 
			// url might be null sometimes
			String infoUrl = infoObj.isNull("link") ? null : infoObj
			        .getString("link");
			item.setLink(infoUrl);
			StringBuilder adresse = new StringBuilder();
			adresse.append("Anschrift: ");
			String infoStrasse = infoObj.isNull("strasse") ? null : infoObj
			        .getString("strasse");
			String infoHausnr = infoObj.isNull("hausnr") ? null : infoObj
			        .getString("hausnr");
			String infoPlz = infoObj.isNull("plz") ? null : infoObj
			        .getString("plz");
			String infoStadt = infoObj.isNull("stadt") ? null : infoObj
			        .getString("stadt");
			adresse.append(infoStrasse).append(" ").append(infoHausnr).append(", ");
			adresse.append(infoPlz).append(" ").append(infoStadt);
			item.setAdresse(adresse.toString());
            String typ = infoObj.isNull("typ") ? null : infoObj.getString("typ");
            item.setTyp(typ);
			return item;
		}
	
		public static void isListEmpty(ListView listView, TextView textView, List<?> infoList) 
		{
			if(!infoList.isEmpty())
			{
				
				textView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
			}
			else
			{
				listView.setVisibility(View.GONE);
				textView.setVisibility(View.VISIBLE);
			}
		}		
		
}
