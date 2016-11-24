package info.softsolution.ebele.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.Utils;

public class VerhaltenMeldenFragment  extends Fragment
{
	private static final int DATE_DIALOG_ID = 0;
	private static final String TAG = VerhaltenMeldenFragment.class.getSimpleName();
	private EditText txtWann;
	private EditText txtWer;
	private EditText txtWas;
	private String email_adresse;
	private List<EditText> listEditText = new ArrayList<EditText>();
	private int mYear;
	private int mMonth;
	private int mDay;
	private Button btnMelden;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private Calendar calendar;

	
	
	public VerhaltenMeldenFragment() {
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_benutzerin_melden, container, false);

        Bundle bundle = getArguments();
        setEmail_adresse(bundle.getString("email"));
        findViews(rootView);
     
        return rootView;
    }
    
    protected Dialog showDialog(int id)
    {
    	switch(id)
    	{
    		case DATE_DIALOG_ID:
    			new DatePickerDialog(getContext(),
    								mDateSetListener,
    								mYear,
    								mMonth,
    								mDay).show();
    	}
    	return null;
    }
    
    private OnDateSetListener mDateSetListener = new OnDateSetListener()
    {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) 
		{
			mYear = year;
			mMonth = monthOfYear + 1;
			mDay = dayOfMonth;
			txtWann.setText(new StringBuilder().append(mDay)
											   .append("/")
											   .append(mMonth)
											   .append("/")
											   .append(mYear));							
		}
	};

	private void findViews(View rootView) 
	{
		
		txtWann = (EditText) rootView.findViewById(R.id.wann);
		
		listEditText.add(txtWann);

		txtWer = (EditText) rootView.findViewById(R.id.wer);
		listEditText.add(txtWer);
		
		txtWas = (EditText) rootView.findViewById(R.id.was);
		listEditText.add(txtWas);
		
        btnMelden = (Button) rootView.findViewById(R.id.btnMelden);
        calendar = Calendar.getInstance();

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        
        txtWann.setText(sdf.format(calendar.getTime()));

        txtWann.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
        
        btnMelden.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String wer = txtWer.getText().toString().trim();
				if(txtWann.getText().toString().isEmpty())
				{
			        txtWann.setText(sdf.format(calendar.getTime()));
				}
				String wann = txtWann.getText().toString().trim();
				
				String was = txtWas.getText().toString().trim();
				String method = Utils.METHOD.create.toString();
				String _email_adresse = getEmail_adresse();
				if(!was.isEmpty())
				{
					executeRequest(wer,was,wann, method, _email_adresse);
				}
				else
				{
					Utils.showToast(getActivity(), Utils.MELDUNG_EMPTY_FELD);
				}
			}
		});
        
	}

	private void executeRequest(final String wer, final String was, 
								final String wann, final String method, final String email_adresse) 
	{
		//Tag um Anfrage zu aborten
		String tag_string_req = "req_benutzer_melden";
		StringRequest strReq = new StringRequest(Method.POST,
												 AppConfig.URL_BENUTZER_MELDEN,
												 new Response.Listener<String>() 
		{

			@Override
			public void onResponse(String response) 
			{
				Log.d(TAG, "Benutzermeldungsspeicherungsantwort: "+ response.toString());
				
				try
				{
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if(!error)
					{
						Utils.showToast(getActivity(), "Vielen Dank. Ihre meldung wurde erfolgreich gesendet!");
						Utils.clearFelder(listEditText);
				        txtWann.setText(sdf.format(calendar.getTime()));
						
					}
					else
					{
						//Fehler beim Speichern
						String errorMsg = jObj.getString("error_msg");
						Utils.showToast(getActivity(), errorMsg);
					}
				}
				catch(JSONException e)
				{
					Log.e(TAG, e.getMessage().toString());
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) 
			{
				Log.e(TAG, "Meldungsspeicherungsfehler: " + error.getMessage());
			}
		}){
			@Override
			protected Map<String, String> getParams()
			{
				Map<String, String> params = new HashMap<String, String>();
				params.put("wann", wann);
				params.put("was", was);
				params.put("email_adresse", email_adresse);
				params.put("method", method);
				params.put("wer", wer);
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	public String getEmail_adresse() {
		return email_adresse;
	}

	public void setEmail_adresse(String email_adresse) {
		this.email_adresse = email_adresse;
	}
	
	
}

