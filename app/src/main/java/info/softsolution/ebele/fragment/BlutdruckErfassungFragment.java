package info.softsolution.ebele.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import info.softsolution.ebele.helper.Utils;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class BlutdruckErfassungFragment  extends Fragment{
	
	private static final String TAG = BlutdruckErfassungFragment.class.getSimpleName();
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private Button btnSpeichern;
	private EditText txtDatum;
	private EditText txtUhrzeit;
	private EditText txtSystolisch;
	private EditText txtDiastolisch;
	private EditText txtPuls;
	private Calendar calendar;
	private boolean isUpdate = false;
	private String email_adresse;
	private List<EditText> listEditText = new ArrayList<EditText>();
    SimpleDateFormat sdfDatum = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);
    SimpleDateFormat sdfUhrzeit = new SimpleDateFormat("hh:mm", Locale.GERMANY);
	
	public BlutdruckErfassungFragment() {
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_blutdruck_erfassen, container, false);
	   	Bundle bundle = getArguments();
    	setEmail_adresse(bundle.getString("email"));

        findViews(rootView);
        return rootView;
    }
    
    private Dialog showDialogDatum()
    {
		new DatePickerDialog(getActivity(),
							mDateListener, 
							mYear, 
							mMonth, 
							mDay).show();
    	return null;
    }
    
    private Dialog showDialogUhrzeit()
    {
    	new TimePickerDialog(getActivity(), mTimeListener, mHour, mMinute, true).show();
    	return null;
    }
    
    private OnDateSetListener mDateListener =
    		new OnDateSetListener() 
    {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) 
		{
			mYear = year;
			mMonth = monthOfYear + 1;
			mDay = dayOfMonth;
			txtDatum.setText(new StringBuilder().append(mDay)
												.append("/")
												.append(mMonth)
												.append("/")
												.append(mYear));
		}
	};
	
	private OnTimeSetListener mTimeListener =
			new OnTimeSetListener() 
	{
				
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		{
			mHour = hourOfDay;
			mMinute = minute;
			txtUhrzeit.setText(new StringBuilder().append(mHour)
												  .append(":")
												  .append(mMinute));
		}
	};
    
    private void findViews(View rootView)
    {
		txtDatum = (EditText) rootView.findViewById(R.id.datum);
		listEditText.add(txtDatum);
		
		txtUhrzeit = (EditText) rootView.findViewById(R.id.uhrzeit);
		listEditText.add(txtUhrzeit);
		
		txtSystolisch = (EditText) rootView.findViewById(R.id.txt_systolisch);
		listEditText.add(txtSystolisch);
		
		txtDiastolisch = (EditText) rootView.findViewById(R.id.txt_diastolisch);
		listEditText.add(txtDiastolisch);

		txtPuls = (EditText) rootView.findViewById(R.id.txt_puls);
		listEditText.add(txtPuls);
		
        btnSpeichern = (Button) rootView.findViewById(R.id.btn_speichern);
        calendar = Calendar.getInstance();
        
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR);
        mMinute = calendar.get(Calendar.MINUTE);
        
        initZeit(sdfDatum, sdfUhrzeit);
        txtDatum.setOnClickListener(new View.OnClickListener() 
        {
 			@Override
 			public void onClick(View v) 
 			{
 				showDialogDatum();
 			}
 		});
        
    	txtUhrzeit.setOnClickListener(new View.OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
				showDialogUhrzeit();
			}
		});
    	
		btnSpeichernSetOnClickListener();

    }

	private void initZeit(SimpleDateFormat sdfDatum, SimpleDateFormat sdfUhrzeit) {
		txtDatum.setText(sdfDatum.format(calendar.getTime()));
        txtUhrzeit.setText(sdfUhrzeit.format(calendar.getTime()));
	}

	private void btnSpeichernSetOnClickListener() 
	{
		btnSpeichern.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				
				String systolisch  = txtSystolisch.getText().toString().trim();
				String diastolisch    = txtDiastolisch.getText().toString().trim();
				String puls           = txtPuls.getText().toString().trim();
				if(!systolisch.isEmpty() && !diastolisch.isEmpty() && !puls.isEmpty() )
				{

					if(isUpdate)
					{
						executeRequest(systolisch, diastolisch, puls, 
								Utils.METHOD.update.toString(),	getId(), getEmail_adresse());
						Toast.makeText(getActivity(), "Blutdruckwert erfolgreich ge�ndert", Toast.LENGTH_LONG).show();
					}
					else
					{
						executeRequest(systolisch, diastolisch, puls, 
								Utils.METHOD.create.toString(),	getId(), getEmail_adresse());
						Toast.makeText(getActivity(), "Blutdruckwert erfolgreich gespeichert", Toast.LENGTH_LONG).show();
						
					}
					
				}
				else
				{
					Toast.makeText(getActivity(), "Notizen darf nicht leer sein!", Toast.LENGTH_LONG).show();					
				}
			}
		});
	}
    

	private void executeRequest(final String systolisch, final String diastolisch, 
								final String puls,	final String method,
								final int id, final String email_adresse)
	{
	//Tag um Anfrage zu aborten
		String tag_string_req = "req_blutdruck";
		StringRequest strReq = new StringRequest(Method.POST, 
												AppConfig.URL_BLUTDRUCK,
												new Response.Listener<String>() 
		{
		
			@Override
			public void onResponse(String response)
			{
				Log.d(TAG, "Blutdruckspeicherungsantwort: " + response.toString());
				
				try
				{
                	JSONObject jObj = new JSONObject(response);
				    boolean error = jObj.getBoolean("error");
				    if(!error)
				    {
                        Utils.showToast(getActivity(),"Blutdruck Youpiii");				    	
						Utils.clearFelder(listEditText);
						initZeit(sdfDatum, sdfUhrzeit);
				    }
                    else 
                    {
                        // Fehler beim Speichern
                        String errorMsg = jObj.getString("error_msg");
                        Utils.showToast(getActivity(),errorMsg);
                    }
				    
				}
				catch(JSONException e)
				{
					Log.e(TAG, e.getMessage().toString());
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() 
		{
			@Override
			public void onErrorResponse(VolleyError error) 
			{
				Log.e(TAG, "Blutdruckspeicherungsfehler: " + error.getMessage());
			}
		}){
			@Override
			protected Map<String,String> getParams()
			{
				//Uebergabe der Blutdrucksparameter an url
				Map<String, String> params = new HashMap<String, String>();
				params.put("systolisch", systolisch);
				params.put("diastolisch", diastolisch);
				params.put("puls", puls);
				params.put("email_adresse", email_adresse);
				params.put("method", method);
				params.put("id", String.valueOf(id));
//TODO erstellt_datum				
				return params;
			}
		};
		//Anfrage in Requestqueue einf�gen
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}
	

	public String getEmail_adresse() {
		return email_adresse;
	}

	public void setEmail_adresse(String email_adresse) {
		this.email_adresse = email_adresse;
	}
}

