package info.softsolution.ebele.activity;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.crypto.spec.SecretKeySpec;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.AESCrypto;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;

/**
 * Diese Klasse dient der Wiederherstellung des Passworts
 * @author duclos
 *
 */
public class WiederherstellungActivity  extends Activity
{
	private static final String TAG = WiederherstellungActivity.class.getSimpleName();
	private ProgressDialog pDialog;
	private SessionManager session;
//	private Mail mail;
	private Button btnWiederherstellung;
	private Button btnLinkToAnmeldung;
	
	private EditText inputSecret;
	private EditText email;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passwort_wiederherstellung);
//		mail = new Mail("ngassarmel@gmail.com", "clarisse123456");
		btnLinkToAnmeldung = (Button) findViewById(R.id.btnLinkToAnmeldungScreen);
		btnWiederherstellung = (Button) findViewById(R.id.btnPasswortWiederherstellung);
		
		inputSecret = (EditText) findViewById(R.id.secret);
		email = (EditText) findViewById(R.id.email);
		
		pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // Session manager
        session = new SessionManager(getApplicationContext());
		
		//Wiederherstellungsbutton Cklick-Ereigniss
		btnWiederherstellung.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) 
			{
				String _secret = inputSecret.getText().toString().trim();
				String _email = email.getText().toString().trim();
				if(!(_secret.isEmpty() && _email.isEmpty()))
				{
                	if(Patterns.EMAIL_ADDRESS.matcher(_email).matches())
                	{
                		wiederherstellung(_email, _secret, Utils.METHOD.wiederherstellung.toString());
                	}
                	else
                	{
                        Utils.showToast(getApplicationContext(),"Bitte korrigieren Sie Ihre Emailadresse!");
                	}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Bitte geben Sie Ihre E-Mail und Ihr Geheimnis ein!", Toast.LENGTH_LONG).show();
				}
					
			}
		});
		
		btnLinkToAnmeldung.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(getApplicationContext(), 
								AnmeldungActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

///////
    private boolean isUser(JSONObject user, String klarSecret) throws JSONException
    {        		
        String encryptedSecret = user.getString("secret");
        String decryptedSecret = AESCrypto.decrypt(encryptedSecret, session.getKey(SessionManager.SYM_KEY, SecretKeySpec.class));
    	
    	return klarSecret.equals(decryptedSecret);
    }
////////////    
    /**
     * Funktion um Anmeldunsdetails in mysql db zu �berpr�fen
     * */
    private void wiederherstellung(final String email, final String secret, final String method) 
    {
        // Tag used to cancel the request
        String tag_string_req = "req_wiederherstellung";

        pDialog.setMessage("Wiederherstellung ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_MANAGE_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Wiederherstellungsantwort: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Pruefe Fehler in json
                    if (!error) {
                        // user gefunden pr�efe jetzt das Geheimnis
                        JSONObject user = jObj.getJSONObject("user");
                        String email = user.getString("email");
                        boolean isOk = isUser(user, secret);
                        if(isOk)
                        {
                        	Session sessionMail = createSessionObject();
                        	// Email mit Passwort senden
                        	String encryptedPasswort = user.getString("encrypted_password"); 
                            String decryptedPasswort = AESCrypto.decrypt(encryptedPasswort, session.getKey(SessionManager.SYM_KEY, SecretKeySpec.class));
//                        	sendEmail(email, decryptedPasswort);
                            Message message = createMessage("ndanjid@yahoo.fr","Passwortwiederherstellung", "Ihr Passwort lautet: " + decryptedPasswort, sessionMail );
//                        	String[] params = {email, decryptedPasswort};
                            new EmailSenden().execute(message);
                        	// Launch main activity
                        	Intent intent = new Intent(WiederherstellungActivity.this,
                        			AnmeldungActivity.class);
                        	// Launch main activity
                        	Utils.showToast(getApplicationContext(), "Ihr Passwort wurde an Ihre E-Mail geschickt");
                        	startActivity(intent);
                        	finish();
                        	
                        }
                        else
                        {
                            Utils.showToast(getApplicationContext(), "Anmeldedaten sind falsch, bitte versuchen Sie es nochmal!");
                        }
                    } 
                    else 
                    {
                        // Fehler bei der Anmeldung
                        String errorMsg = jObj.getString("error_msg");
                        Utils.showToast(getApplicationContext(), errorMsg);
                    }
                } 
                catch (Exception e) 
                {
                    // JSON Fehler
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "Jsonfehler: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Anmeldungsfehler: " + error.getMessage());
                Utils.showToast(getApplicationContext(), error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email_adresse", email);
                params.put("method", method);

                return params;
            }

        };

        // Anfrage in Request-Queue hinzufuegen
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    
    
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }	
    
    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("ndanjid@yahoo.fr", "Duclos Ndanji"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ngassarmel@gmail.com", "clarisse123456");
            }
        });
    }    
    
    class EmailSenden extends AsyncTask<Message, Void, Void>
    {
      	 private ProgressDialog progressDialog;

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             progressDialog = ProgressDialog.show(WiederherstellungActivity.this, "Bitte warten Sie", "Email wird gesendet", true, false);
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);
             progressDialog.dismiss();
         }    	
         @Override
         protected Void doInBackground(Message... messages) 
         {
             try 
             {
                 Transport.send(messages[0]);
             } 
             catch (MessagingException e) 
             {
                 e.printStackTrace();
             }
             return null;
         }
    }

}
