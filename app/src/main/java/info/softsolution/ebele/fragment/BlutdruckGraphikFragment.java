package info.softsolution.ebele.fragment;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
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
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.Blutdruck;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class BlutdruckGraphikFragment  extends Fragment
{
	private String email_adresse;
	private ImageButton first;
	private ImageButton previous;
	private ImageButton next;
	private ImageButton last;	
	private XYPlot xyPlot;
	private BlutDruckAdapter blutDruckAdapter;
	private List<Number> systolisch = new ArrayList<Number>();
	private List<Number> diastolisch = new ArrayList<Number>();
	private static final String SYSTOLISCH = "systolisch";
	private static final String DIASTOLISCH = "diastolisch";
	private ProgressDialog pDialog;
	private JSONArray blutdruckArray = null;
	//List, die alle Blutdruckwerte enth�lt
	private List<Blutdruck> blutdruckList;
	private List<String> mMonths = new ArrayList<String>();
	private static final String TAG = BlutdruckGraphikFragment.class.getSimpleName();
	private static final int MAX_TO_SHOW = 10;
	//Hilfsvariabe f�r die Navigation
	private int merker = 0;
	//Anzahl Blutdruckwerte, die aktuell angezeigt wird
	private int anzahl_aktuell_angezeigt = 0;
	//List, deren Elemente angezeigt werden
	private List<Blutdruck> blutdruckListToShow;
	private Iterator<Blutdruck> iterator ;//= blutdruckList.listIterator(merker);
	public BlutdruckGraphikFragment() {
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_blutdruck_messung, container, false);
    	pDialog = new ProgressDialog(getActivity());
	   	Bundle bundle = getArguments();
    	setEmail_adresse(bundle.getString("email"));
        xyPlot = (XYPlot) rootView.findViewById(R.id.xyplot);

        initNavigation(rootView);
    	blutdruckList = new ArrayList<Blutdruck>();
    	blutdruckListToShow = new ArrayList<Blutdruck>();
    	loadAllBlutdruckValues();
//    	setData();
        
        return rootView;
    }
    
    private void setEnableButton()
    {
    	first.setEnabled(darfFirst());
//    	first.setActivated(darfFirst());
    	next.setEnabled(darfNext());
    	previous.setEnabled(darfPrevious());
    	last.setEnabled(darfLast());
    }
    
    private void initNavigation(View view)
    {
    	first = (ImageButton) view.findViewById(R.id.first);
    	previous = (ImageButton) view.findViewById(R.id.previous);
    	next = (ImageButton) view.findViewById(R.id.next);
    	last = (ImageButton) view.findViewById(R.id.last);
    	
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
				blutdruckListToShow.clear();
//				if(merker - MAX_TO_SHOW - anzahl_aktuell_angezeigt >= 0)
//				{
					merker = merker - (MAX_TO_SHOW + anzahl_aktuell_angezeigt);
					if(merker < 0)
					{
						merker = 0;
					}
//					merker -= MAX_TO_SHOW;
					iterator = blutdruckList.listIterator(merker);
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
			}
//				merker = 0;
				
//			}
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
    
	private void loadAllBlutdruckValues()
	{
		//Tag used to cancel the request
		String tag_string_req = "req_all_blutdruck";
		pDialog.setMessage("Blutdruckwerte werden geladen...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
        showDialog();

//		showDialog();
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
				    		//Log.d(TAG, _blutdruck.getTitel());
				    	}
//				    	adapter.notifyDataSetChanged();
				    	setDataFirstTime();
//				    	setData();
				    	xyPlot.redraw();
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
		xyPlot.clear();
    	blutDruckAdapter = new BlutDruckAdapter(_blutdruckList);
    	
    	systolisch  = blutDruckAdapter.get_systolisch();
    	diastolisch = blutDruckAdapter.get_diastolisch();
        mMonths     = blutDruckAdapter.get_erstellt_datum();
        
        XYSeries systolischSeries = new SimpleXYSeries(systolisch,
				   SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
				   SYSTOLISCH);
        XYSeries diastolischSeries = new SimpleXYSeries(diastolisch,
				   SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
				   DIASTOLISCH); 
        
        // Create a formatter to format Line and Point of systolisch series
        LineAndPointFormatter systolischFormat = new LineAndPointFormatter(
                Color.rgb(0, 0, 255),                   // line color
                Color.rgb(200, 200, 200),               // point color
                null,
                null);                					// fill color (none)
 
        //Create a Formater to format Liine and Point of diastolisch series
        LineAndPointFormatter expenseFormat = new LineAndPointFormatter(
        		Color.rgb(255,0,0), 
        		Color.rgb(200, 200, 200),
        		null, null);
        
        //add expence series to the xyplot:
        xyPlot.addSeries(diastolischSeries, expenseFormat);
        //add incomes series to the xyplot:
        xyPlot.addSeries(systolischSeries, systolischFormat);
        
        //Format the domain Values( X-Axis )
        xyPlot.setDomainValueFormat(new Format() {
			
			@Override
			public Object parseObject(String string, ParsePosition position) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public StringBuffer format(Object object, StringBuffer buffer,
					FieldPosition field) {

				
				int value = ((Number)object).intValue();
				if(value < 0)
				{
					value = 0;
				}
				
				if(mMonths.size() > 0)
				{
					return new StringBuffer(mMonths.get(value));
				}
				return null;
//				return new StringBuffer(mMonths[(((Number)object).intValue())]);
			}
		});
        
        xyPlot.setDomainLabel("Erfassungsdatum");
        xyPlot.setRangeLabel("Blutdruckwerte");
        xyPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        xyPlot.setRangeStepValue(10);
        xyPlot.setRangeBoundaries(40, 200, BoundaryMode.FIXED);
        
        //Increment X-Axis by 1 value
        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL,1);
        xyPlot.getGraphWidget().setDomainLabelOrientation(-45);
        xyPlot.getGraphWidget().setMarginBottom(50);
        xyPlot.getGraphWidget().setPaddingBottom(50);
        xyPlot.getGraphWidget().setMarginRight(30);
        xyPlot.getGraphWidget().setMarginTop(30);
        xyPlot.getGraphWidget().setDomainLabelHorizontalOffset(20);
//        xyPlot.setPlotMarginBottom(50);
//        xyPlot.setPlotMarginRight(20);
//        xyPlot.setPlotMarginTop(20);
        
        //Reduce the number of range labels
//        xyPlot.setTicksPerRangeLabel(2);
//        //Reduce the number of domain labels
//        xyPlot.setTicksPerDomainLabel(2);
        //Remove all the developer guide from the chart
//        xyPlot.setMarkupEnabled(true);
        xyPlot.getGraphWidget().getBackgroundPaint().setColor(Color.parseColor("#FFCCFF"));
        xyPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);

        xyPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        xyPlot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
	
//        setEnableButton();
        xyPlot.requestLayout();
        xyPlot.redraw();
	}
	
    
	private class BlutDruckAdapter
	{
		private List<Number> _systolisch = new ArrayList<Number>();
		private List<Number> _diastolisch = new ArrayList<Number>();
		private List<String> _erstellt_datum = new ArrayList<String>();
		private List<Blutdruck> _blutdruckList = new ArrayList<Blutdruck>();
		
		public BlutDruckAdapter(List<Blutdruck> _blutdruckList)
		{
			this._blutdruckList = _blutdruckList;
			init();
		}
		
		public void init() 
		{
			for(Blutdruck b : _blutdruckList)
			{
				_systolisch.add(b.getSystolisch());
				_diastolisch.add(b.getDiastolisch());
				_erstellt_datum.add(b.getCreated_at());
			}
		}

		public List<Blutdruck> getBlutdruckList() {
			return blutdruckList;
		}
		public void setBlutdruckList(List<Blutdruck> blutdruckList) {
			this._blutdruckList = blutdruckList;
		}


		public List<Number> get_systolisch() {
			return _systolisch;
		}

		public void set_systolisch(List<Number> _systolisch) {
			this._systolisch = _systolisch;
		}

		public List<Number> get_diastolisch() {
			return _diastolisch;
		}

		public void set_diastolisch(List<Number> _diastolisch) {
			this._diastolisch = _diastolisch;
		}

		public List<Blutdruck> get_blutdruckList() {
			return _blutdruckList;
		}

		public void set_blutdruckList(List<Blutdruck> _blutdruckList) {
			this._blutdruckList = _blutdruckList;
		}

		public List<String> get_erstellt_datum() {
			return _erstellt_datum;
		}

		public void set_erstellt_datum(List<String> _erstellt_datum) {
			this._erstellt_datum = _erstellt_datum;
		}
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		setDataFirstTime();
//		xyPlot.redraw();
	}

}

