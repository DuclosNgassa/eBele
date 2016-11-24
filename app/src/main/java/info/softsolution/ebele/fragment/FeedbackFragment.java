package info.softsolution.ebele.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/*
 * Klasse f�r das Anzeigen der Informatioen �ber die App
 * 
 * @author Duclos Ndanji
 */
public class FeedbackFragment extends Fragment {

	private static final String TAG = FeedbackFragment.class.getSimpleName();

	private RadioGroup radioZufriedenheitGroup;
	private RadioButton radioZufriedenheitGrad;
	private ImageButton btnSpeichern;
	private ImageButton btnReturn;
	private EditText editAnmerkung;
	private SessionManager session;
	private ProgressDialog pDialog;

	public FeedbackFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feedback, container,
				false);
		session = new SessionManager(getActivity());
		addListenerOnButton(rootView);
	    pDialog = new ProgressDialog(getActivity());
	    pDialog.setCancelable(false);

	    return rootView;
	}

	private void addListenerOnButton(final View view) {
		radioZufriedenheitGroup = (RadioGroup) view
				.findViewById(R.id.radioZufriedenheitGroup);
		btnSpeichern = (ImageButton) view.findViewById(R.id.btnSpeichern);
		btnReturn = (ImageButton) view.findViewById(R.id.btnReturn);
		btnReturn.setVisibility(View.GONE);
		editAnmerkung = (EditText) view.findViewById(R.id.anmerkung);

		btnSpeichern.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int selectedId = radioZufriedenheitGroup
						.getCheckedRadioButtonId();
				radioZufriedenheitGrad = (RadioButton) view
						.findViewById(selectedId);
				String anmerkung = editAnmerkung.getText().toString();
				String bewertung = radioZufriedenheitGrad.getText().toString();
				String method = Utils.METHOD.bewertung.toString();
				String email = session.getEmail();
				reinit();
//				executeRequest(method, email, anmerkung, bewertung);

			}
		});
	}

	private void executeRequest(final String _method, final String _email,
								final String _anmerkung,final String _bewertung) {
		// Tag um Anfrage zu aborten
		String tag_string_req = "req_bewertung";
		pDialog.setMessage("Operation wird durchgef�hrt...");
		showDialog();
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_STATISTIK, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Bewertungsspeicherungsantwort: "
								+ response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
								Utils.showToast(getActivity(),
										"Vielen Dank f�r Ihre Bewertung und Ihre konstruktive Anmerkung!");
								reinit();
							} else {
								// Fehler beim Speichern
								String errorMsg = jObj.getString("error_msg");
								Utils.showToast(getActivity(), errorMsg);
							}
						} catch (JSONException e) {
							Log.e(TAG, e.getMessage().toString());
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG,
								"bewertungsspeicherungsfehler: "
										+ error.getMessage());
						hideDialog();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("anmerkung", _anmerkung);
				params.put("bewertung", _bewertung);
				params.put("email", _email);
				params.put("method", _method);
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void reinit() {
		editAnmerkung.setText("");
		radioZufriedenheitGrad = (RadioButton) radioZufriedenheitGroup.getChildAt(0);
		radioZufriedenheitGrad.setChecked(true);
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
