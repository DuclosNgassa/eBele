package info.softsolution.ebele.helper;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;
	private static Gson GSON = new Gson();

	// Shared preferences file name
	private static final String PREF_NAME = "Login";
	
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
	
	//User-Attribute
	public static final String KEY_NAME = "name";
	public static final String KEY_STICHTAG = "stichtag";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_ENCRYPTED_PASSWORD = "encrypted_password";
	public static final String KEY_TYP = "typ";
	public static final String KEY_SECRET = "secret";
	public static final String KEY_CREATED_AT = "created_at";
	
	//Verschl�sselung
	public static final String PUBLIC_KEY = "publicKey";
	public static final String SYM_KEY = "symKey";
	public static final String PRIVAT_KEY = "privateKey";

	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

/**
 * Priv and Public key setzen
 * @param privateKey
 * @param publicKey
 */
	public void setKeyPair(Key privateKey, Key publicKey)
	{
		if(privateKey == null )
		{
			throw new IllegalArgumentException("der private Schl�ssel ist null!");
		}
		if(publicKey == null)
		{
			throw new IllegalArgumentException("der �ffentliche Schl�ssel ist null!");
		}
		editor.putString(PRIVAT_KEY, GSON.toJson(privateKey, Key.class));
		editor.putString(PUBLIC_KEY, GSON.toJson(publicKey, Key.class));
		editor.commit();
	}

	/**
	 * Symetrischer key setzen
	 //* @param SecretKeySpec symKey
	 */
	public void setSymKey(SecretKeySpec symKey)
	{
		if(symKey == null )
		{
			throw new IllegalArgumentException("der Schl�ssel ist null!");
		}
		editor.putString(SYM_KEY, GSON.toJson(symKey, SecretKeySpec.class));
		editor.commit();
	}
	
	public <T> T getKey(String key, Class<T> k)
	{
		String gson = pref.getString(key, null);
		if(gson == null)
		{
			return null;
		}
		else
		{
			try{
				return GSON.fromJson(gson, k);
			}
			catch(Exception e)
			{
				throw new IllegalArgumentException("Das gespeicherte Objekt geh�rt einer anderen Klasse!");
			}
		}
		
	}

	public void setLogin(boolean isLoggedIn, String name, String email, 
						String stichtag, String secret, String typ, 
						String encryptedPassword, String createdAt) {

//		clear(
		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
		editor.putString(KEY_NAME, name);
		editor.putString(KEY_EMAIL, email);
		if(!stichtag.isEmpty())
		{
			String jahr = stichtag.substring(0, 4);		
			String monat = stichtag.substring(5, 7);
			String tag = stichtag.substring(8, 10);
			stichtag = tag + "/" + monat + "/" + jahr;
		}
		
		editor.putString(KEY_STICHTAG, stichtag);
		editor.putString(KEY_ENCRYPTED_PASSWORD, encryptedPassword);
		editor.putString(KEY_CREATED_AT, createdAt);
		editor.putString(KEY_TYP, typ);
		editor.putString(KEY_SECRET, secret);
		
		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}
	
	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}
	
	public String getName()
	{
		return pref.getString(KEY_NAME, "");
	}

	public String getStichtag()
	{
		return pref.getString(KEY_STICHTAG, "");
	}
	
	public String getEmail()
	{
		return pref.getString(KEY_EMAIL, "");
	}

	public Editor getEditor() {
		return editor;
	}

	public void setEditor(Editor editor) {
		this.editor = editor;
	}
	
	public void removeKey(String key)
	{
		editor.remove(key);
		editor.commit();
	}
	
	public void removeAll()
	{
		editor.clear();
		editor.commit();
	}
	
	public void addKeyString(String key, String value)
	{
		editor.putString(key, value);
		editor.commit();
	}

	public String getValueString(String key)
	{
		return pref.getString(key, "");
	}

	public int getValueInt(String key)
	{
		return pref.getInt(key, 0);
	}
	
}
