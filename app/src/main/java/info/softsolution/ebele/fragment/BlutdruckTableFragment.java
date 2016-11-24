package info.softsolution.ebele.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.CachedFileProvider;
import info.softsolution.ebele.helper.PdfCreator;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.Blutdruck;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BlutdruckTableFragment  extends Fragment
{
	private ImageButton first;
	private ImageButton previous;
	private ImageButton next;
	private ImageButton last;	
	private Button btnDemArztSenden;
	private String email_adresse;
	private TableLayout tableLayout;
	private TableLayout tableLayoutBewertung;
	private ProgressDialog pDialog;
	private JSONArray blutdruckArray = null;
	private List<Blutdruck> blutdruckList;
	private static final String TAG = BlutdruckTableFragment.class.getSimpleName();
	private static final String FILE = "Blutdruck.pdf";
	private static final int MAX_TO_SHOW = 10;
	//Hilfsvariabe f�r die Navigation
	private int merker = 0;
	//Anzahl Blutdruckwerte, die aktuell angezeigt wird
	private int anzahl_aktuell_angezeigt = 0;
	//List, deren Elemente angezeigt werden
	private List<Blutdruck> blutdruckListToShow;
	private Iterator<Blutdruck> iterator ;//= blutdruckList.listIterator(merker);
	private SessionManager session;
	
	public BlutdruckTableFragment() {
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_blutdruck_table, container, false);
    	pDialog = new ProgressDialog(getActivity());
	   	Bundle bundle = getArguments();
    	setEmail_adresse(bundle.getString("email"));
    	tableLayout = (TableLayout) rootView.findViewById(R.id.blutdruck_table);
    	tableLayoutBewertung = (TableLayout) rootView.findViewById(R.id.blutdruck_table_bewertung);
    	session = new SessionManager(getActivity());
    	blutdruckList = new ArrayList<Blutdruck>();
    	blutdruckListToShow = new ArrayList<Blutdruck>();
    	initNavigation(rootView);
		drawTableBewertung(setDataBewertung());
    	loadAllBlutdruckValues();
        
        return rootView;
    }
    
	private void loadAllBlutdruckValues()
	{
		//Tag used to cancel the request
		String tag_string_req = "req_all_blutdruck";
		pDialog.setMessage("Blutdruckwerte werden geladen...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		showDialog();
		
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_BLUTDRUCK, new Response.Listener<String>() {
		
			@Override
			public void onResponse(String response)
			{
				Log.e(TAG,"Anwort Blutdruckladen: YOUPIIIIIIIIIIIII");
                hideDialog();

                try 
                {
                	JSONObject jObj = new JSONObject(response);
				    boolean error = jObj.getBoolean("error");
				    if(!error)
				    {
				    	blutdruckArray = jObj.getJSONArray("blutdruckList");
				    	for(int i = 0; i < blutdruckArray.length(); i++)
				    	{
				    		JSONObject jo = blutdruckArray.getJSONObject(i);
				    		Blutdruck _blutdruck = Utils.convertJsonObjectToBlutdruck(jo);
				    		blutdruckList.add(_blutdruck);
				    	}
				    	setDataFirstTime();
				    }
                    else 
                    {
                        // Fehler bei der Anmeldung
                        String errorMsg = jObj.getString("error_msg");
                        Utils.showToast(getActivity(),errorMsg);
                    }
				    
                }
                catch(JSONException e)
                {
                    // JSON Fehler
                    e.printStackTrace();
                    Utils.showToast(getActivity(),"Jsonfehler: " + e.getMessage());
                }
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Blutdruckladungsfehler: " + error.getMessage());
                Utils.showToast(getActivity(), error.getMessage());
                hideDialog();
			}
		}){
			@Override
			protected Map<String, String> getParams()
			{
				//Posting parameters to url
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", Utils.METHOD.readAll.toString());
				params.put("email_adresse", getEmail_adresse());
				return params;
			}
		};
		
		AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
	}

	private void setDataFirstTime()
	{
		anzahl_aktuell_angezeigt = 0;
		merker = 0;
		iterator = blutdruckList.listIterator(merker);
		blutdruckListToShow.clear();
		while(iterator.hasNext() && (merker < MAX_TO_SHOW))
		{
			blutdruckListToShow.add(iterator.next());
			merker++;
			anzahl_aktuell_angezeigt++;
		}
		setData(blutdruckListToShow);
		setEnableButton();
	}
	

	private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

	public String getEmail_adresse() {
		return email_adresse;
	}

	public void setEmail_adresse(String email_adresse) {
		this.email_adresse = email_adresse;
	}

	private void setData(List<Blutdruck> _blutdruckList)
	{
		View vg = tableLayout.getChildAt(0);
		tableLayout.removeAllViews();
		tableLayout.addView(vg);
		Iterator<Blutdruck> iterator = _blutdruckList.iterator();
		int count = 0;
		while(iterator.hasNext())
		{
			TableRow tableRow = (TableRow)LayoutInflater.from(getActivity()).inflate(R.layout.fragment_blutdruck_tablerow, null);
			if(count % 2 != 0)
			{
				tableRow.setBackgroundColor(Color.MAGENTA);
			}
			
			Blutdruck _blutdruck = iterator.next();
			
			//schlechte Blutdruckwerte mit rot f�rben
			if(_blutdruck.getSystolisch() >= 140 || _blutdruck.getDiastolisch() >= 90
					|| (_blutdruck.getSystolisch() > 140 && _blutdruck.getDiastolisch() < 90 ))
			{
				tableRow.setBackgroundColor(Color.RED);
			}

//			((TextView)tableRow.findViewById(R.id.item_id)).setText(_blutdruck.getIdString());
//			tableRow.findViewById(R.id.item_id).setVisibility(View.INVISIBLE);
			int nr = count + 1;
			((TextView)tableRow.findViewById(R.id.item_nr)).setText(String.valueOf(nr));
			((TextView)tableRow.findViewById(R.id.item_datum)).setText(_blutdruck.getCreated_at());
			((TextView)tableRow.findViewById(R.id.item_systolisch)).setText(_blutdruck.getSystolischString());
			((TextView)tableRow.findViewById(R.id.item_diastolisch)).setText(_blutdruck.getDiastolischString());
			tableLayout.addView(tableRow);
			count++;
		}
//		setEnableButton();
		tableLayout.requestLayout();
	}

	private void drawTableBewertung(List<String> _blutdruckList)
	{
		Iterator<String> iterator = _blutdruckList.iterator();
	
		int count = 0;
		while(iterator.hasNext())
		{
			TableRow tableRow = (TableRow)LayoutInflater.from(getActivity()).inflate(R.layout.fragment_blutdruck_tablerow_bewertung, null);
			if(count % 2 != 0)
			{
				tableRow.setBackgroundColor(Color.MAGENTA);
			}
			
			((TextView)tableRow.findViewById(R.id.item_bewertung)).setText(iterator.next());
			((TextView)tableRow.findViewById(R.id.item_systolisch)).setText(iterator.next());
			((TextView)tableRow.findViewById(R.id.item_diastolisch)).setText(iterator.next());
			tableLayoutBewertung.addView(tableRow);
			count++;
		}
		tableLayoutBewertung.requestLayout();
	}
	
    private boolean darfFirst()
    {
    	return merker > MAX_TO_SHOW;
    }

    private boolean darfPrevious()
    {
    	return merker > MAX_TO_SHOW;
    }

    private boolean darfNext()
    {
    	if(blutdruckList != null)
    	{
    		return blutdruckList.size() > merker;
    	}
    	return false;
    }
    
    private boolean darfLast()
    {
    	if(blutdruckList != null)
    	{
    		return blutdruckList.size() > merker;
    	}
    	return false;
    }

    private void initNavigation(View view)
    {
    	first = (ImageButton) view.findViewById(R.id.first);
    	previous = (ImageButton) view.findViewById(R.id.previous);
    	next = (ImageButton) view.findViewById(R.id.next);
    	last = (ImageButton) view.findViewById(R.id.last);
        btnDemArztSenden = (Button) view.findViewById(R.id.btn_arzt_senden);

    	setEnableButton();
    	
    	first.setOnClickListener(new View.OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
				merker = 0;
				anzahl_aktuell_angezeigt = 0;
				iterator = blutdruckList.listIterator(merker);
				blutdruckListToShow.clear();
				while(iterator.hasNext() && (merker < MAX_TO_SHOW))
				{
					blutdruckListToShow.add(iterator.next());
					merker++;
					anzahl_aktuell_angezeigt++;
				}
				setData(blutdruckListToShow);
				setEnableButton();
			}
		});
    	
    	previous.setOnClickListener(new View.OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
//				if(merker - MAX_TO_SHOW - anzahl_aktuell_angezeigt >= 0)
//				{
					merker = merker - (MAX_TO_SHOW + anzahl_aktuell_angezeigt);
					if(merker < 0)
					{
						merker = 0;
					}
					iterator = blutdruckList.listIterator(merker);
					blutdruckListToShow.clear();
					anzahl_aktuell_angezeigt = 0;
					while(iterator.hasNext() && (anzahl_aktuell_angezeigt < MAX_TO_SHOW))
					{
						blutdruckListToShow.add(iterator.next());
						merker++;
						anzahl_aktuell_angezeigt++;
					}
					setData(blutdruckListToShow);
					setEnableButton();
//				}
//				merker = 0;
				
			}
		});
    	
    	next.setOnClickListener(new View.OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
//				if(merker < blutdruckList.size())
//				{
//					merker -= MAX_TO_SHOW - anzahl_aktuell_angezeigt;
					if(merker >= blutdruckList.size())
					{
						merker = blutdruckList.size() - MAX_TO_SHOW;
					}
					iterator = blutdruckList.listIterator(merker);
					blutdruckListToShow.clear();
					anzahl_aktuell_angezeigt = 0;
					while(iterator.hasNext() && (anzahl_aktuell_angezeigt < MAX_TO_SHOW))
					{
						blutdruckListToShow.add(iterator.next());
						merker++;
						anzahl_aktuell_angezeigt++;
					}
					setData(blutdruckListToShow);
					setEnableButton();
//				}
//				merker = 0;
				
			}
		});
    	
    	last.setOnClickListener(new View.OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
//				if(blutdruckList.size() - MAX_TO_SHOW >= 0)
//				{
					merker = blutdruckList.size() - MAX_TO_SHOW;
					iterator = blutdruckList.listIterator(merker);
					blutdruckListToShow.clear();
					anzahl_aktuell_angezeigt = 0;
					while(iterator.hasNext())
					{
						blutdruckListToShow.add(iterator.next());
						merker++;
						anzahl_aktuell_angezeigt++;
					}
					setData(blutdruckListToShow);
					setEnableButton();
//				}
//				merker = 0;
				
			}
		});
    	
    	btnDemArztSenden.setOnClickListener(new View.OnClickListener() 
    	{
			
			@Override
			public void onClick(View v) 
			{
				final Dialog dialog = new Dialog(getActivity());
				
				dialog.setContentView(R.layout.dialog_blutdruck_senden);
				dialog.setTitle("Blutdruckswerte senden");
				
				Button senden = (Button) dialog.findViewById(R.id.buttonSend);
				Button abbrechen = (Button) dialog.findViewById(R.id.buttonAbbrechen);
				
				
				abbrechen.setOnClickListener(new View.OnClickListener() 
				{
					
					@Override
					public void onClick(View v) 
					{
						dialog.dismiss();
					}
				});
				
				
				senden.setOnClickListener(new View.OnClickListener() 
				{
					
					@Override
					public void onClick(View v) 
					{
						try
						{
							
							EditText sendenAn = (EditText) dialog.findViewById(R.id.sendenAn);
							EditText betreff = (EditText) dialog.findViewById(R.id.betreff);
							EditText nachricht = (EditText) dialog.findViewById(R.id.nachricht);
							String email = sendenAn.getText().toString().trim();
							String _betreff = betreff.getText().toString().trim();
							String _nachricht = nachricht.getText().toString().trim();
							String name = session.getName();
							PdfCreator pdfCreator = new PdfCreator(getActivity());
							pdfCreator.create(blutdruckList, name);
							
							if(!email.isEmpty())
							{
								Intent emailIntent = new Intent(Intent.ACTION_SEND);
								emailIntent.setType("*/*");
								emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
								emailIntent.putExtra(Intent.EXTRA_SUBJECT, _betreff);
								emailIntent.putExtra(Intent.EXTRA_TEXT, _nachricht);
								emailIntent.setClassName("com.google.android.gm",
										"com.google.android.gm.ComposeActivityGmail");
//								String targetFilePath = getActivity().getFilesDir() + File.separator + FILE;
//								Uri attachmentUri = Uri.parse(targetFilePath);
								
								emailIntent.putExtra(Intent.EXTRA_STREAM, 
										Uri.parse("content://" + CachedFileProvider.AUTHORITY + "/" + FILE));
//								startActivity(Intent.createChooser(emailIntent, "Email senden..."));
								startActivity(emailIntent);
							}
							else
							{
								Utils.showToast(getActivity(), "Bitte Email-Adresse eingeben!");
							}
						}
						catch(ActivityNotFoundException e)
						{
							e.printStackTrace();
						}
/*						try {
							// Write a dummy text file to this application's internal
							// cache dir.
							PdfCreator.createCachedFile(getActivity(),
									"Test.txt", "This is a test");

							// Then launch the activity to send that file via gmail.
							startActivity(PdfCreator.getSendEmailIntent(
									getActivity(),
									// TODO - Change email to yours
									"<YOUR_EMAIL_HERE>@<YOUR_DOMAIN>.com", "Test",
									"See attached", "Test.txt"));
						} catch (IOException e) {
							e.printStackTrace();
						}
						// Catch if Gmail is not available on this device
						catch (ActivityNotFoundException e) {
							Toast.makeText(getActivity(),
									"Gmail is not available on this device.",
									Toast.LENGTH_SHORT).show();
						}
 */
						
					}
				});
				
				dialog.show();
			}
		});
    }

	
    private void setEnableButton()
    {
    	first.setEnabled(darfFirst());
    	first.setActivated(darfFirst());
    	next.setEnabled(darfNext());
    	previous.setEnabled(darfPrevious());
    	last.setEnabled(darfLast());
    }

	@Override
	public void onResume()
	{
		super.onResume();
		setDataFirstTime();
//		drawTableBewertung(setDataBewertung());
	}

	private List<String> setDataBewertung()
	{
		List<String> listBewertung = new ArrayList<String>();

		listBewertung.add("optimaler Blutdruck");
		listBewertung.add("<120");
		listBewertung.add("<80");

		listBewertung.add("normaler Blutdruck");
		listBewertung.add("120-129");
		listBewertung.add("80-84");

		listBewertung.add("hoch-normaler Blutdruck");
		listBewertung.add("130-139");
		listBewertung.add("85-89");

		listBewertung.add("milde Hypertonie (Stufe 1)");
		listBewertung.add("140-159");
		listBewertung.add("90-99");

		listBewertung.add("mittlere Hypertonie (Stufe 2)");
		listBewertung.add("160-179");
		listBewertung.add("100-109");

		listBewertung.add("schwere Hypertonie (Stufe 3)");
		listBewertung.add(">180");
		listBewertung.add(">110");

		listBewertung.add("isolierte systolische Hypertonie");
		listBewertung.add(">140");
		listBewertung.add("<90");

		return listBewertung;
	}
	
}
