package info.softsolution.ebele.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.AESCrypto;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.TabListener;
import info.softsolution.ebele.helper.Utils;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActionBar.Tab;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class EinstellungenFragment extends Fragment {
	private static final String TAG = EinstellungenFragment.class
			.getSimpleName();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",
			Locale.GERMANY);
	private SessionManager session;
	private ProgressDialog pDialog;
//	private Calendar calendar = Calendar.getInstance();	

	private EditText name;
	private EditText email;
	private EditText email2;
	private EditText stichtag;
	private EditText erstelltDatum;
	private EditText secret;
	private EditText secretNeu;
	private EditText passwort;
	private EditText passwortNeu;
	private EditText passwortNeu2;
	private TextView txtEmail2;
	private TextView txtNeuPass;
	private TextView txtPass;
	private TextView txtSecret;
	private TextView txtNeuPass2;
	private TextView txtNeuSecret;
	private CheckBox chkSchliessen;
	private CheckBox chkLoeschen;
	private View line2;
	private static final String AENDERN = "Profil Bearbeiten";
//	private static final String SPEICHERN = "Speichern";
	private static final String NOCH_NICHT_BERECHNET = "Noch nicht berechnet";
	private Button editOrStore;
	private String status = "EINGELOGGT";
	
	private Tab tabUserList;
	private Tab tabInfo;
	private Tab tabMeldung;
	private Tab tabAuslaenderin;
	private Tab tabStatistik;
	
	private	android.app.Fragment fragmentUserList = new UserListFragment();
	private	android.app.Fragment fragmentAllerInfo = new AllerInfoFragment();
	private	android.app.Fragment fragmentStatistik = new StatistikFragment();
	private	android.app.Fragment fragmentMeldung = new MeldungFragment();
	private	android.app.Fragment fragmentAuslaenderin = new AuslaenderinFragment();
	

	public EinstellungenFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		session = new SessionManager(getActivity());
		View rootView = null;
		if(session.getValueString(SessionManager.KEY_TYP).equals("USER"))
		{
			rootView = inflater.inflate(R.layout.fragment_einstellungen,
					container, false);
			
			findViewsUser(rootView);
		}
		if(session.getValueString(SessionManager.KEY_TYP).equals("ADMIN"))
		{
			rootView = inflater.inflate(R.layout.fragment_layout,
					container, false);
			
			findViewsAdmin(rootView);
		}
		
			
        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

		return rootView;
	}

    private void findViewsAdmin(View rootView)
    {
		ActionBar actionBar = getActivity().getActionBar();
		 
		// Hide Actionbar Icon
		actionBar.setDisplayShowHomeEnabled(true);
 
		// Hide Actionbar Title
		actionBar.setDisplayShowTitleEnabled(true);
 
		// Create Actionbar Tabs
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
		// Set Tab Icon and Titles
		tabUserList = actionBar.newTab().setIcon(R.drawable.user_list);
		tabUserList.setText("Liste aller Nutzerinnen");

		tabInfo = actionBar.newTab().setText("Informationen");
		tabInfo.setIcon(R.drawable.info);

		tabMeldung = actionBar.newTab().setText("meldungen");
		tabMeldung.setIcon(R.drawable.user_melden1);

		tabStatistik = actionBar.newTab().setText("Statistik");
		tabStatistik.setIcon(R.drawable.statistik);
		
		tabAuslaenderin = actionBar.newTab().setText("Ausl�nderin");
		tabAuslaenderin.setIcon(R.drawable.auslaender);
		// Set Tab Listeners
		tabUserList.setTabListener(new TabListener(fragmentUserList));
		tabInfo.setTabListener(new TabListener(fragmentAllerInfo));
		tabMeldung.setTabListener(new TabListener(fragmentMeldung));
		tabAuslaenderin.setTabListener(new TabListener(fragmentAuslaenderin));
		tabStatistik.setTabListener(new TabListener(fragmentStatistik));
		actionBar.addTab(tabUserList);
		actionBar.addTab(tabInfo);
		actionBar.addTab(tabMeldung);
		actionBar.addTab(tabAuslaenderin);
		actionBar.addTab(tabStatistik);
    	
    }
	/**
	 * Diese Methose setzt die Eigenschaften einer Nutzerin, in den
	 * entsprechenden Feldern
	 */
	private void setFieldValue() {
		email.setText(session.getValueString(SessionManager.KEY_EMAIL));
		name.setText(session.getValueString(SessionManager.KEY_NAME));
		String st = session.getValueString(SessionManager.KEY_STICHTAG);
		if(st.isEmpty())
		{
			stichtag.setHint(NOCH_NICHT_BERECHNET);			
		}
		stichtag.setText(st);
		
//		secret.setText(session.getValueString(SessionManager.KEY_SECRET));
		String _erstelltdatum = session
				.getValueString(SessionManager.KEY_CREATED_AT);
		String eD = "";

		if(!_erstelltdatum.isEmpty() && _erstelltdatum.length() > 9)
		{			
			String jahr = _erstelltdatum.substring(0, 4);		
			String monat = _erstelltdatum.substring(5, 7);
			String tag = _erstelltdatum.substring(8, 11);
			eD = tag + "/" + monat + "/" + jahr;
		}
		erstelltDatum.setText(eD);
		
//		passwort.setText(session.getValueString(SessionManager.KEY_ENCRYPTED_PASSWORD));
	}

	/**
	 * Diese Methode steuert die Editierbarkeit eines Fenster
	 * 
	 * @param //boolean edit, falls true => view darf editiert werden
	 */
	private void setEdit(boolean edit) {
		email.setClickable(edit);
		email.setFocusable(edit);
		email.setFocusableInTouchMode(edit);
		email2.setClickable(edit);
		email2.setFocusable(edit);
		email2.setFocusableInTouchMode(edit);
		name.setClickable(edit);
		name.setFocusable(edit);
		name.setFocusableInTouchMode(edit);
		stichtag.setClickable(edit);
		stichtag.setFocusable(edit);
		stichtag.setFocusableInTouchMode(edit);
		if(edit)
		{
			stichtag.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDateDialog();
					
				}
			});
		}
		else
		{
			stichtag.setOnClickListener(null);
		}
		secret.setClickable(edit);
		secret.setFocusable(edit);
		secret.setFocusableInTouchMode(edit);
		secretNeu.setClickable(edit);
		secretNeu.setFocusable(edit);
		secretNeu.setFocusableInTouchMode(edit);
		passwort.setClickable(edit);
		passwort.setFocusable(edit);
		passwort.setFocusableInTouchMode(edit);
		passwortNeu.setClickable(edit);
		passwortNeu.setFocusable(edit);
		passwortNeu.setFocusableInTouchMode(edit);
		passwortNeu2.setClickable(edit);
		passwortNeu2.setFocusable(edit);
		passwortNeu2.setFocusableInTouchMode(edit);

		if (!edit) {
			secretNeu.setVisibility(View.GONE);
			email2.setVisibility(View.GONE);
			txtEmail2.setVisibility(View.GONE);
			secret.setVisibility(View.GONE);
			passwort.setVisibility(View.GONE);
			txtPass.setVisibility(View.GONE);
			txtSecret.setVisibility(View.GONE);
			txtNeuSecret.setVisibility(View.GONE);
			passwortNeu.setVisibility(View.GONE);
			txtNeuPass.setVisibility(View.GONE);
			passwortNeu2.setVisibility(View.GONE);
			txtNeuPass2.setVisibility(View.GONE);
			chkLoeschen.setVisibility(View.GONE);
			chkSchliessen.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
			editOrStore.setText(R.string.aendern);
		} else {
			editOrStore.setText(R.string.btn_speichern);
			email2.setVisibility(View.VISIBLE);
			txtEmail2.setVisibility(View.VISIBLE);
			secretNeu.setVisibility(View.VISIBLE);
			secret.setVisibility(View.VISIBLE);
			passwort.setVisibility(View.VISIBLE);
			txtPass.setVisibility(View.VISIBLE);
			txtSecret.setVisibility(View.VISIBLE);
			txtNeuSecret.setVisibility(View.VISIBLE);
			passwortNeu.setVisibility(View.VISIBLE);
			txtNeuPass.setVisibility(View.VISIBLE);
			passwortNeu2.setVisibility(View.VISIBLE);
			txtNeuPass2.setVisibility(View.VISIBLE);
			chkLoeschen.setVisibility(View.VISIBLE);
			chkSchliessen.setVisibility(View.VISIBLE);
			line2.setVisibility(View.VISIBLE);
		}
	}

	private void findViewsUser(View rootView) {
		name = (EditText) rootView.findViewById(R.id.name);
		email = (EditText) rootView.findViewById(R.id.email);
		email2 = (EditText) rootView.findViewById(R.id.email2);
		stichtag = (EditText) rootView.findViewById(R.id.stichtag);
		
		erstelltDatum = (EditText) rootView.findViewById(R.id.erstelltdatum);
		secret = (EditText) rootView.findViewById(R.id.secret);
		secretNeu = (EditText) rootView.findViewById(R.id.neusecret);
		passwort = (EditText) rootView.findViewById(R.id.passwort);
		passwortNeu = (EditText) rootView.findViewById(R.id.neupasswort);
		passwortNeu2 = (EditText) rootView.findViewById(R.id.neupasswort2);

		line2 = (View) rootView.findViewById(R.id.line2);

		txtNeuSecret = (TextView) rootView.findViewById(R.id.txtneusecret);
		txtNeuPass = (TextView) rootView.findViewById(R.id.txtneupasswort);
		txtNeuPass2 = (TextView) rootView.findViewById(R.id.txtneupasswort2);
		txtEmail2 = (TextView) rootView.findViewById(R.id.txtemail2);
		txtPass = (TextView) rootView.findViewById(R.id.txtpasswort);
		txtSecret = (TextView) rootView.findViewById(R.id.txtsecret);

		chkSchliessen = (CheckBox) rootView.findViewById(R.id.chkSchliessen);
		chkLoeschen = (CheckBox) rootView.findViewById(R.id.chkloeschen);
		editOrStore = (Button) rootView.findViewById(R.id.btnEditOrStore);

		chkSchliessen.setOnCheckedChangeListener(new CheckBoxChangeListener());
		
		chkLoeschen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	if(isChecked)
		    	{
		    		showAlertDialog(
		    				"Konto l�schen",
		    				"Wollen Sie wirklich Ihr Konto endg�ltig schliessen?",
		    				2);												    		
		    	}
		    }
		});
		
		setFieldValue();		
		// View als nicht editierbar setzen	
		setEdit(false);

		editOrStore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editOrStore.getText().toString().equals(AENDERN)) {
					setEdit(true);
				} else {
					String _name = name.getText().toString().trim();
					String _email = email.getText().toString().trim();
					String _email2 = email2.getText().toString().trim();
					String _stichtag = stichtag.getText().toString().trim();
					String _secret = secret.getText().toString().trim();
					String _secretNeu = secretNeu.getText().toString().trim();
					String _passwort = passwort.getText().toString().trim();
					String _passwortNeu = passwortNeu.getText().toString()
							.trim();
					String _passwortNeu2 = passwortNeu2.getText().toString()
							.trim();
					boolean eingabenIsOk = pruefeEingabe(_name, _email,
							_email2,_secret, _secretNeu, _passwort, 
							_passwortNeu, _passwortNeu2);


					if (eingabenIsOk) {
						//Verschl�sselung
                		String encryptedSecret = AESCrypto.encrypt(_secretNeu, session.getKey(SessionManager.SYM_KEY, SecretKeySpec.class));
                		String encryptedPassword = AESCrypto.encrypt(_passwortNeu, session.getKey(SessionManager.SYM_KEY, SecretKeySpec.class));

                		String emailAlt = session.getEmail();
						// Update User
						updateUser(_name, _email, emailAlt, _stichtag, encryptedSecret, 
								encryptedPassword,getStatus(), Utils.METHOD.update.toString());
					}
				}

			}

		});
	}

	private Dialog showDateDialog()
	{
		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);

		new DatePickerDialog(getContext(), mDateSetListener, mYear, mMonth, mDay).show();		
		return null;
	}
	
	private OnDateSetListener mDateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			stichtag.setText(new StringBuilder().append(dayOfMonth).append("/")
					.append(monthOfYear + 1).append("/").append(year));
		}
	};
	
	// //////////
	/**
	 * Diese Methode f�hrt das Updaten einer Nutzerin durch
	 * */
	private void updateUser(final String _name, final String _email, final String emailAlt, 
			final String _stichtag, final String _secret, final String _passwort,
			final String _status, final String method) {
		// Tag used to cancel the request
		String tag_string_req = "req_update";

		pDialog.setMessage("Updaten ...");
		showDialog();

		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_MANAGE_USER, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Updatenantwort: " + response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");

							// Pruefe Fehler in json
							if (!error) {
								// Updaten erfolgreich durchgef�hrt

								// Now store the user in SQLite
								String stichtag = "";

								JSONObject user = jObj.getJSONObject("user");

								String encryptedPassword = user
										.getString("encrypted_password");
								// String decryptedPassword =
								// RSACryptography.decrypt(encryptedPassword,
								// session.getKey(SessionManager.PUBLIC_KEY,
								// Key.class));
									if (!user.isNull("stichtag")) {
										stichtag = user.getString("stichtag");
									}

									String name = user.getString("name");
									String email = user.getString("email");

									String secret = user.getString("secret");
									String createdAt = user
											.getString("created_at");
									String typ = user.getString("typ");

									// Create login session
									session.setLogin(true, name, email,	stichtag, 
											secret, typ, encryptedPassword,	createdAt);

									Utils.showToast(getActivity(), "Update erfolgreich durchgef�hrt");

							} else {
								// Fehler beim Updaten
								String errorMsg = jObj.getString("error_msg");
								Utils.showToast(getActivity(), errorMsg);
							}
						} catch (JSONException e) {
							// JSON Fehler
							e.printStackTrace();
							Utils.showToast(getActivity(), "Jsonfehler: " + e.getMessage());
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Anmeldungsfehler: " + error.getMessage());
						Utils.showToast(getActivity(), error.getMessage());
						hideDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to login url
				Map<String, String> params = new HashMap<String, String>();
				params.put("email", _email);
				params.put("emailAlt", emailAlt);
				params.put("password", _passwort);
				params.put("name", _name);
				params.put("secret", _secret);
				params.put("status", _status);
				params.put("stichtag", _stichtag);
				params.put("method", method);

				return params;
			}

		};

		// Anfrage in Request-Queue hinzufuegen
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	// //////////

	private boolean pruefeEingabe(String _name, String _email, String _email2,
			String _secret,String _secretNeu, String _passwort, String _passwortNeu,
			String _passwortNeu2) {

		if (!(_name.isEmpty() && _email.isEmpty() && _email2.isEmpty()
				&& _secret.isEmpty() && _secretNeu.isEmpty()
				&& _passwort.isEmpty() && _passwortNeu.isEmpty() && _passwortNeu2
					.isEmpty())) {
			String encryptedPassword = session
					.getValueString(SessionManager.KEY_ENCRYPTED_PASSWORD);
			String decryptedPassword = AESCrypto
					.decrypt(encryptedPassword, session.getKey(
							SessionManager.SYM_KEY, SecretKeySpec.class));
			String encryptedSecret = session
					.getValueString(SessionManager.KEY_SECRET);
			String decryptedSecret = AESCrypto
					.decrypt(encryptedSecret, session.getKey(
							SessionManager.SYM_KEY, SecretKeySpec.class));
			if (decryptedSecret.equals(_secret)) {
				if (decryptedPassword.equals(_passwort)) {
					if (_passwortNeu.equals(_passwortNeu2)) {
						if (_email.equals(_email2)) {
							return true;
						} else {
							Utils.showToast(
									getActivity(),
									"Die neue Emailadresse stimmt nicht mit der wiederholten neuen Emailadresse �berein!");
						}
					} else {
						Utils.showToast(getActivity(),
								"Das neue Passwort stimmt nicht mit dem wiederholten neuen Passwort �berein!");
					}
				} else {
					Utils.showToast(getActivity(),
							"Das eingegebene Passwort stimmt nicht mit dem gespeicherten Passwort �berein!");
				}
			} else {
				Utils.showToast(
						getActivity(),
						"Das eingegebene Geheimnis stimmt nicht mit dem gespeicherten Geheimnis �berein!");
			}
		} else {
			Utils.showToast(getActivity(),
					"F�llen Sie bitte die leeren Felder aus!");
		}

		return false;
	}

	private void showAlertDialog(String title, String msg, final int merker) {
		new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(R.string.ja,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (merker == 1) {
									setStatus("SCHLIESSEN");
								}
								if (merker == 2) {
									setStatus("LOESCHEN");
								}
							}
						})
				.setNegativeButton(R.string.nein,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (merker == 1) {
									chkSchliessen.setChecked(false);
								}
								if (merker == 2) {
									chkLoeschen.setChecked(false);
								}
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
	
	private class CheckBoxChangeListener implements OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			if(isChecked)
			{
				if(buttonView == chkLoeschen)
				{
					showAlertDialog(
							"Konto l�schen",
							"Wollen Sie wirklich Ihr Konto endg�ltig schliessen?",
							2);
				}
				
				if(buttonView == chkSchliessen)
				{
					showAlertDialog(
							"Konto schliessen",
							"Wollen Sie wirklich Ihr Konto schliessen?",
							1);
				}
			}
			else
			{
				setStatus("EINGELOGGT");
			}
		}
	}
	
}
