package info.softsolution.ebele.helper;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import android.util.Base64;
import android.util.Log;

public class RSACryptography {
	static Cipher cipher;
	private static final String TAG = RSACryptography.class.getSimpleName();
	private static final String ALGO = "RSA";
	public static final String PUBLIC_KEY = "publicKey";
	public static final String PRIVAT_KEY = "privateKey";
	
//	public static void main(String[] args) throws Exception {
//		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//		keyGenerator.init(128);
//		SecretKey secretKey = keyGenerator.generateKey();
//		cipher = Cipher.getInstance(ALGO);
//
//		String plainText = "AES Symmetric Encryption Decryption";
//		System.out.println("Plain Text Before Encryption: " + plainText);
//
//		String encryptedText = encrypt(plainText, secretKey);
//		System.out.println("Encrypted Text After Encryption: " + encryptedText);
//
//		String decryptedText = decrypt(encryptedText, secretKey);
//		System.out.println("Decrypted Text After Decryption: " + decryptedText);
//	}
//
	
	/**
	 * Generiere Schl�sselpaar f�r 1024-bit RSA Ver -bzw. Entschl�sselung
	 * @return Map<String, Key> public/privat Schl�sselpaar
	 */
	public static Map<String, Key> generateKey() {
		Map<String, Key> keyMap = new HashMap<String, Key>();
		try{
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGO);
			keyPairGenerator.initialize(1024);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			keyMap.put(PUBLIC_KEY, keyPair.getPublic());
			keyMap.put(PRIVAT_KEY, keyPair.getPrivate());
		}
		catch(Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
		
		return keyMap;
	}

	public static String encrypt(String plainText, Key publicKey) {
		String encryptedText = "";
		try {
			byte[] plainTextByte = plainText.getBytes();
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedByte = cipher.doFinal(plainTextByte);
			encryptedText = Base64
					.encodeToString(encryptedByte, Base64.DEFAULT);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return encryptedText;
	}

	public static String decrypt(String encryptedText, Key privatKey) {
		String decryptedText = "";
		try {
			byte[] encryptedTextByte = Base64.decode(encryptedText,
					Base64.DEFAULT);
			cipher.init(Cipher.DECRYPT_MODE, privatKey);
			byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
			decryptedText = new String(decryptedByte);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return decryptedText;
	}
		
	public static Map<String, String> keyPairToString()
	{
		Map<String, String> strHashMap = new HashMap<String, String>();
		Map<String, Key> keyMap = generateKey();
		byte[] privKeyByte = keyMap.get(PRIVAT_KEY).getEncoded();
		byte[] pubKeyByte = keyMap.get(PUBLIC_KEY).getEncoded();
			
		String strPrivKey = Base64.encodeToString(privKeyByte, Base64.DEFAULT);
		String strPubKey = Base64.encodeToString(pubKeyByte, Base64.DEFAULT);
		
		strHashMap.put(PRIVAT_KEY, strPrivKey);
		strHashMap.put(PUBLIC_KEY, strPubKey);
		
		return strHashMap;
	}
	
	public static PublicKey stringToPubKey(String strPubKey) throws Exception
	{
		byte[] pubKeyBytes = Base64.decode(strPubKey, Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGO);
		EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
		PublicKey pubKey2 = keyFactory.generatePublic(pubKeySpec);
		
		return pubKey2;
	}

	public static PrivateKey stringToPrivKey(String strPrivKey) throws Exception
	{
		byte[] privKeyBytes = Base64.decode(strPrivKey, Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGO);
		EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privKeyBytes);
		PrivateKey privKey2 = keyFactory.generatePrivate(privKeySpec);
		
		return privKey2;
	}
	
}
