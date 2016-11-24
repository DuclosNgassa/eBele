package info.softsolution.ebele.helper;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

/**
 * Diese Klasse implementiert das AES-Verschl�sselungsverfahren
 * @author duclos
 */
public class AESCrypto {

	private static final String TAG = AESCrypto.class.getSimpleName();
	private static final String ALGO = "AES";
	public static final int SIZE = 128;
	public static final String KEY = "Key";
	public static final String INSTANCE = "SHA1PRNG";
	public static final String SEED = "irgendwelche Datei, die als Zufallseed, benutzt wird.";
		
	/**
	 * Generiere symetrischer Schl�ssel f�r 128-bit AES Ver -bzw. Entschl�sselung
	 * @return  Key
	 */
	public static SecretKeySpec generateKey() {
		SecretKeySpec sks = null;
		try{
			SecureRandom sr = SecureRandom.getInstance(INSTANCE);
			sr.setSeed(SEED.getBytes());
			KeyGenerator kg = KeyGenerator.getInstance(ALGO);
			kg.init(SIZE,sr);
			sks = new SecretKeySpec((kg.generateKey()).getEncoded(), ALGO);
		}
		catch(Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
		
		return sks;
	}

	/**
	 * Diese Methode wandelt den Schl�ssel f�r den Transport in String um
	 * @return den Schl�ssel als String
	 */
	public static String keyToString()
	{
		SecretKeySpec sks = generateKey();
		if(sks != null)
		{
			return Base64.encodeToString(sks.getEncoded(), Base64.DEFAULT);
		}
		Log.e(TAG, "Der Schl�ssel ist NULL!!!");
		return null;
	}
	
	/**
	 * Diese Methode wandelt den Schl�ssel vom String zu SecretKeySpec um
	 * @return SecretKeySpec
	 */
	public static SecretKeySpec stringToKey(String strKey) throws Exception
	{
		byte[] keyBytes = Base64.decode(strKey, Base64.DEFAULT);
		if(keyBytes != null)
		{
			SecretKeySpec originalKey = new SecretKeySpec(keyBytes,ALGO);
			return originalKey;
		}
		Log.e(TAG, "Der Schl�sselbyte ist NULL!!!");
		return null;
	}
	
	/**
	 * Diese Methode verschl�sselt einen Text anhand eines Schl�ssel
	 * @param //String plainText
	 * @param //SecretKeySpec key
	 * @return String verschl�sselter Text
	 */
	public static String encrypt(String plainText, SecretKeySpec key) {
		String encryptedText = "";
		try {
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] plainTextByte = plainText.getBytes();
			byte[] encryptedByte = c.doFinal(plainTextByte);
			
			encryptedText = Base64
					.encodeToString(encryptedByte, Base64.DEFAULT);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return encryptedText;
	}

	/**
	 * Diese Methode entschl�sselt einen Text anhand eines Schl�ssel
	 * @param //String encryptedText
	 * @param //SecretKeySpec key
	 * @return String entschl�sselter Text
	 */
	public static String decrypt(String encryptedText, SecretKeySpec key) {
		String decryptedText = "";
		try {
			byte[] encryptedTextByte = Base64.decode(encryptedText,
					Base64.DEFAULT);
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedByte = c.doFinal(encryptedTextByte);
			decryptedText = new String(decryptedByte);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return decryptedText;
	}
			
}
