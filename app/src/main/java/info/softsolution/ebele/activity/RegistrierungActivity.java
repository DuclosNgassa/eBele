package info.softsolution.ebele.activity;

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

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.AESCrypto;
import info.softsolution.ebele.helper.RSACryptography;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.User;


public class RegistrierungActivity extends Activity
{
    private static final String TAG = RegistrierungActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputEmailWiederholt;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
	private EditText inputPasswortWiederholt;
	private EditText inputSecret;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_registrierung);

    	
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputEmailWiederholt = (EditText) findViewById(R.id.email_wiederholt);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswortWiederholt = (EditText)findViewById(R.id.password_wiederholt);
        inputSecret = (EditText) findViewById(R.id.geheimnis);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

//        generateAndStoreKey();
//        generateAndStoreSymKey();
        // Session manager
        session = new SessionManager(getApplicationContext());

        // Pruefe, ob User schon eingeloggt ist
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegistrierungActivity.this,
                    MainActivity.class);
            intent.putExtra("name", session.getName());
            intent.putExtra("email", session.getEmail());
            intent.putExtra("stichtag", session.getStichtag());
            startActivity(intent);
            finish();
        }
        
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String emailWiederholt = inputEmailWiederholt.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String passwordWiederholt = inputPasswortWiederholt.getText().toString().trim();
                String secret = inputSecret.getText().toString().trim();
                
                if (pruefeEingabe(name, email, emailWiederholt, password, passwordWiederholt, secret)) 
                {
                	//Pr�fung der �bereinstimmung den angegebenen Passw�rtern
                	if(password.equals(passwordWiederholt))
                	{
                		//Pr�fung der Konformit�t der Emailadresse 
	                	if(Patterns.EMAIL_ADDRESS.matcher(email).matches() 
	                			&& Patterns.EMAIL_ADDRESS.matcher(emailWiederholt).matches()
	                			&& email.equals(emailWiederholt))
	                	{
	                		//Verschl�sselung des Passwortes und Geheimnis
	                		String encryptedSecret = AESCrypto.encrypt(secret, session.getKey(SessionManager.SYM_KEY, SecretKeySpec.class));
	                		String encryptedPassword = AESCrypto.encrypt(password, session.getKey(SessionManager.SYM_KEY, SecretKeySpec.class));
	                		registrieren(name, email, encryptedPassword, Utils.METHOD.create.toString(), encryptedSecret, User.kontoType.USER.toString());
	                	}
	                	else
	                	{
	                        Toast.makeText(getApplicationContext(),
	                                "Bitte korrigieren Sie Ihre Emailadresse!", Toast.LENGTH_LONG).show();
	                	}
                	}
                	else
                	{
                        Toast.makeText(getApplicationContext(),
                                "Die Passw�rter stimmen nicht �berein!", Toast.LENGTH_LONG).show();
                	}
                } 
                else 
                {
                    Toast.makeText(getApplicationContext(),
                            "Bitte Formular richtig ausf�llen!", Toast.LENGTH_LONG).show();
                }
            }

			private boolean pruefeEingabe(String name, String email, String emailWiederholt,
					String password, String passwordWiederholt, String secret) {
				return !name.isEmpty() && !email.isEmpty()	&& !password.isEmpty() && !passwordWiederholt.isEmpty() && !secret.isEmpty();
			}
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        AnmeldungActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
  
	/**
	 * Diese Methode generiere und speichert ein Schl�sselpaar, 
	 * falls es dies noch nicht gibt      
	 */
    private void generateAndStoreKey()
    {
    	Map<String, String> keyMap = RSACryptography.keyPairToString();
    	saveKey(keyMap.get(RSACryptography.PRIVAT_KEY),
    			keyMap.get(RSACryptography.PUBLIC_KEY), Utils.METHOD.create.toString());
    }

	/**
	 * Diese Methode generiere und speichert ein Schl�sselpaar, 
	 * falls es dies noch nicht gibt      
	 */
    private void generateAndStoreSymKey()
    {
    	String strKey = AESCrypto.keyToString();
    	saveSymKey(strKey, Utils.METHOD.create.toString());
    }
/////////////////////////
    /**
     * Speicherung des Schl�ssel im Server  
     //* @param String Key
     * @param method(create)
     * */
    private void saveSymKey(final String strKey, final String method) 
    {
        // Tag wird benutzt um Anfrage zu aborten
        String tag_string_req = "req_saveSymKey";

        pDialog.setMessage("Keysaving ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_SYM_KEY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Speicherungsantwort: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                    	Toast.makeText(getApplicationContext(), "Schl�sselspeicherung erfolgreich!", Toast.LENGTH_LONG).show();

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
                    e.printStackTrace();
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
                params.put("symKey", strKey);
                params.put("method", method);
                return params;
            }

        };

        // Anfrage in Request-Queue einfuegen
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    
    
//////////////////////////    
    /**
     * Speicherung des Schl�sselpaares im Server  
     //* @param String privKey
     //* @param String pubKey
     * @param method
     * */
    private void saveKey(final String priKey, final String pubKey, final String method) 
    {
        // Tag wird benutzt um Anfrage zu aborten
        String tag_string_req = "req_saveKey";

        pDialog.setMessage("Keysaving ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_KEY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                    	Toast.makeText(getApplicationContext(), "Schl�sselspeicherung erfolgreich.!", Toast.LENGTH_LONG).show();

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
                    e.printStackTrace();
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
                params.put(RSACryptography.PRIVAT_KEY, priKey);
                params.put(RSACryptography.PUBLIC_KEY, pubKey);
                params.put("method", method);
                return params;
            }

        };

        // Anfrage in Request-Queue einfuegen
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    
    
    /**
     * Speicherung von User in MySQL DB  
     //* @param tag
     * @param name
     * @param email
     * @param password 
     * @param method
     * @param secret
     * */
    private void registrieren(final String name, final String email,
                              final String password, final String method, final String secret, final String typ) {
        // Tag wird benutzt um Anfrage zu aborten
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                    	Toast.makeText(getApplicationContext(), "Registrierung erfolgreich. Melden Sie sich jetzt an!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegistrierungActivity.this,
                                AnmeldungActivity.class);
                        startActivity(intent);
                        finish();
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
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) 
            {
                Log.e(TAG, "Registrierungsfehler: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("method", method);
                params.put("secret", secret);
                params.put("typ", typ);
                return params;
            }

        };

        // Anfrage in Request-Queue einfuegen
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
}
