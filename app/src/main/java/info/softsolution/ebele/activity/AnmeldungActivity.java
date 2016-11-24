package info.softsolution.ebele.activity;


import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.AESCrypto;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author duclos
 * since 15.10.15
 */
public class AnmeldungActivity extends Activity
{
	private static final String TAG = AnmeldungActivity.class.getSimpleName();
	private Button btnAnmeldung;
	private Button btnLinkToRegistrierung;
	private Button btnLinkToWiederherstellung;
	private EditText txtInputEmail;
	private EditText txtInputPasswort;
	private ProgressDialog pDialog;
	private SessionManager session;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anmeldung);

        txtInputEmail = (EditText) findViewById(R.id.email);
        txtInputPasswort = (EditText) findViewById(R.id.password);
        btnAnmeldung = (Button) findViewById(R.id.btnAnmeldung);
        btnLinkToRegistrierung = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnLinkToWiederherstellung = (Button)findViewById(R.id.btnLinkToWiederherstellungScreen);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // Session manager
        session = new SessionManager(getApplicationContext());
        
//        readKey(Utils.METHOD.read.toString());
        if(session.getKey(SessionManager.SYM_KEY, SecretKeySpec.class) == null)
        {
        	readSymKey(Utils.METHOD.read.toString());
        }

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(AnmeldungActivity.this, MainActivity.class);
    
            intent.putExtra("name", session.getName());
            intent.putExtra("email", session.getEmail());
            intent.putExtra("stichtag", session.getStichtag());
            
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnAnmeldung.setOnClickListener(new View.OnClickListener() 
        {

            public void onClick(View view) 
            {
                String email = txtInputEmail.getText().toString().trim();
                String password = txtInputPasswort.getText().toString().trim();

                // Check for empty data in the form
                if (eingabePruefen(email, password)) 
                {
                	if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                	{
                		// login user
                		anmelden(email, password);
                	}
                	else
                	{
                        Toast.makeText(getApplicationContext(),
                                "Bitte korrigieren Sie Ihre Emailadresse!", Toast.LENGTH_LONG).show();
                	}
                } 
                else 
                {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "F�llen Sie bitte das Formular aus!", Toast.LENGTH_LONG).show();
                }
            }

			private boolean eingabePruefen(String email, String password) 
			{
				return !email.isEmpty() && !password.isEmpty();
			}

        });
        
        //Link to Wiederherstellun Screen
        btnLinkToWiederherstellung.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View v) 
			{
                Intent i = new Intent(getApplicationContext(),
                        WiederherstellungActivity.class);
                startActivity(i);
                finish();
			}
		});        

        // Link to Register Screen
        btnLinkToRegistrierung.setOnClickListener(new View.OnClickListener() 
        {

            public void onClick(View view) 
            {
                Intent i = new Intent(getApplicationContext(),
                        RegistrierungActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
    
    
/////////////
    /**
     * Laden des Schl�sselpaares aus dem Server  
     * */
    private void readSymKey(final String method) 
    {
        // Tag wird benutzt um Anfrage zu aborten
        String tag_string_req = "req_readSymKey";

        pDialog.setMessage("Symetrische Keyladung ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_SYM_KEY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Keyladungsantwort: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                    	//Key als String holen und umwandeln
                    	String strKey = jObj.getString("symKey");
                    	SecretKeySpec symKey = AESCrypto.stringToKey(strKey);
                    	//Sch�ssel in session speichern f�r weitere Nutzung in Laufzeit
                    	session.setSymKey(symKey);
                    	
                    	Toast.makeText(getApplicationContext(), "Schl�sselspeicherung in Session erfolgreich.!", Toast.LENGTH_LONG).show();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } 
                catch (JSONException e) 
                {
                    Log.e(TAG, e.getMessage());
                }
                catch(Exception e)
                {
                    Log.e(TAG, e.getMessage());                	
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) 
            {
                Log.e(TAG, "Schl�sselspeicherungsfehler: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("method", method);
                return params;
            }

        };

        // Anfrage in Request-Queue einfuegen
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Funktion um Anmeldunsdetails in mysql db zu �berpr�fen
     * */
    private void anmelden(final String email, final String password) 
    {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Anmeldung ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Pruefe Fehler in json
                    if (!error) {
                        // user erfolgreich eingeloggt

                        // Now store the user in SQLite
                    	String stichtag = "";
                    	String typ = "";
                    	String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        if(session.getKey(SessionManager.SYM_KEY, SecretKeySpec.class) != null)
                        {
	                        String encryptedPassword = user.getString("encrypted_password");
	    					String decryptedPassword = AESCrypto.decrypt(encryptedPassword, session.getKey(SessionManager.SYM_KEY, SecretKeySpec.class));
	                        boolean isOk = isUserOk(decryptedPassword, password);
	                        if(isOk)
	                        {
	                        	executeRequest(Utils.METHOD.updateUserStatus.toString(), Utils.STATUS.ONLINE.toString(), email);
	                        	if(!user.isNull("stichtag"))
	                        	{
	                        		stichtag = user.getString("stichtag");
	                        	}
	
	                            String name = user.getString("name");
	                            String email = user.getString("email");
	        					
	        					String secret = user.getString("secret");
	        					String createdAt = user.getString("created_at");
	                        	if(!user.isNull("typ"))
	                        	{
	                        		typ = user.getString("typ");
	                        	}
	                        	
	                        	// Create login session
	                        	session.setLogin(true, name, email, stichtag, secret, typ, encryptedPassword, createdAt);
	                        	
	                        	// Launch main activity
	                        	Intent intent = new Intent(AnmeldungActivity.this,
	                        			MainActivity.class);
	                        	// Launch main activity
	                        	intent.putExtra("name", name);
	                        	intent.putExtra("email", email);
	                        	intent.putExtra("stichtag", stichtag);
	                        	intent.putExtra("uid", uid);
	                        	showToast("Anmeldung erfolgreicht");
	                        	startActivity(intent);
	                        	finish();
	                        	
	                        	
	                        }
	                        else
	                        {
	                            showToast("Anmeldedaten sind falsch, bitte versuchen Sie es nochmal!");
	                        }
                        }
                        else
                        {
                            showToast("Fehler beim Keyladen, bitte versuchen Sie es nochmal!");
                        }
                    } 
                    else 
                    {
                        // Fehler bei der Anmeldung
                        String errorMsg = jObj.getString("error_msg");
                        showToast(errorMsg);
                    }
                } 
                catch (JSONException e) 
                {
                    // JSON Fehler
                    e.printStackTrace();
                    showToast("Jsonfehler: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Anmeldungsfehler: " + error.getMessage());
                showToast(error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
//                params.put("password", password);

                return params;
            }

        };

        // Anfrage in Request-Queue hinzufuegen
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private boolean isUserOk(String decryptedPassword, String password)
    {
    	return decryptedPassword.equals(password);
    }
    
    
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    
    private void showToast(String msg)
    {
    	Toast.makeText(this, msg, Toast.LENGTH_LONG).show();;
    }
    
	private void executeRequest(final String method, final String status, final String email)
	{
		//Tag used to cancel the request
		String tag_string_req = "req_user_status";
		pDialog.setMessage("Operation wird durchgef�hrt...");
		showDialog();
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_MANAGE_USER, new Response.Listener<String>() {
		
			@Override
			public void onResponse(String response)
			{
				Log.d(TAG,"Anwort "+method + response.toString());
                hideDialog();

                try 
                {
                	JSONObject jObj = new JSONObject(response);
				    boolean error = jObj.getBoolean("error");
				    if(!error)
				    {
						Log.d(TAG,"Status erfolgreich ge�ndert "+method + response.toString());
				    }
                    else 
                    {
                        // Fehler bei der Anmeldung
                        String errorMsg = jObj.getString("error_msg");
                        showToast(errorMsg);
                    }
				    
                }
                catch(JSONException e)
                {
                    // JSON Fehler
                    e.printStackTrace();
                    showToast("Jsonfehler: " + e.getMessage());
                }
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
                Log.e(TAG, method+"sfehler: " + error.getMessage());
                showToast(error.getMessage());
                hideDialog();
			}
		}){
			@Override
			protected Map<String, String> getParams()
			{
				//Posting parameters to url
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", method);
				params.put("status", status);
				params.put("email_adresse", email);
				return params;
			}
		};
		
		AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
	}

        
}
