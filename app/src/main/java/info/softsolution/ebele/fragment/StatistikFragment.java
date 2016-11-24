package info.softsolution.ebele.fragment;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
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
import com.androidplot.xy.XYStepMode;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.Utils;

/*
 * Klasse f�r das Anzeigen der Informatioen �ber die App
 * 
 * @author Duclos Ndanji
 */
public class StatistikFragment extends Fragment {

	private static final String TAG = StatistikFragment.class.getSimpleName();
	private ProgressDialog pDialog;
	private VerbindungAdapter verbindungAdapter;
	private Map<String, Integer> hMapGesamt ;
	private Map<String, Integer> hMapStatus ;
//	private Map<String, Integer> hMapVerbindung ;
	private List<Verbindung> verbindungList; 
	private List<Verbindung> verbindungListToShow; 
//	private List<Number> anzahlVerbindungToShow ;
	private List<Number> anzahlVerbindung = new ArrayList<>();
	private JSONArray gesamtArray;
	private JSONArray statusArray;
	private JSONArray verbindungArray;
	private LinearLayout layoutGesamt;
	private LinearLayout layoutStatus;
	private View viewChartGesamt;
	private View viewChartStatus;
	private enum statName{GESAMT,STATUS,VERBINDUNG};
	private View vInclude;
	private XYPlot xyPlot;
	private ImageButton first;
	private ImageButton previous;
	private ImageButton next;
	private ImageButton last;
	private static final int MAX_TO_SHOW = 10;
	//Hilfsvariabe f�r die Navigation
	private Iterator<Verbindung> iterator ;//= blutdruckList.listIterator(merker);
	private static final String ANZAHL_VERBINDUNG = "Anzahl Verbindungen";

	private int merker = 0;
	//Anzahl Blutdruckwerte, die aktuell angezeigt wird
	private int anzahl_aktuell_angezeigt = 0;
	private List<String> mTage = new ArrayList<String>();
	

	public StatistikFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_statistik,
				container, false);

		layoutGesamt = (LinearLayout) rootView.findViewById(R.id.stat_gesamt);
		layoutStatus = (LinearLayout) rootView.findViewById(R.id.stat_status);
        vInclude = (View) rootView.findViewById(R.id.stat_verbindung);
		xyPlot = (XYPlot) vInclude.findViewById(R.id.xyplot);

        initNavigation(vInclude);
    	pDialog = new ProgressDialog(getActivity());
		
		hMapGesamt = new HashMap<>();
		hMapStatus = new HashMap<>();
		gesamtArray = new JSONArray();
		statusArray = new JSONArray();
		verbindungArray = new JSONArray();
		anzahlVerbindung = new ArrayList<>();
//		anzahlVerbindungToShow = new ArrayList<>();
		verbindungList = new ArrayList<>();
		verbindungListToShow = new ArrayList<>();
		
		// Loading all Count in Hinterground
		if (hMapGesamt.isEmpty() || hMapStatus.isEmpty()) {
			executeRequest(layoutGesamt, viewChartGesamt,
					Utils.METHOD.count.toString(),
					"Gesamte �bersicht","Tabellen","Anzahl Datens�tze", statName.GESAMT.toString());
			executeRequest(layoutStatus, viewChartStatus,
					Utils.METHOD.status.toString(),
					"Userstatus-�bersicht","Status","Anzahl Datens�tze", statName.STATUS.toString());			
			executeRequest(null, null,
					Utils.METHOD.verbindung.toString(),
					null,null,null, statName.VERBINDUNG.toString());			
		}
		
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		executeRequest(layoutGesamt, viewChartGesamt,
				Utils.METHOD.count.toString(),
				"Gesamte �bersicht","Tabellen","Anzahl Datens�tze", statName.GESAMT.toString());
		executeRequest(layoutStatus, viewChartStatus,
				Utils.METHOD.status.toString(),
				"Userstatus-�bersicht","Status","Anzahl Datens�tze", statName.STATUS.toString());			
		executeRequest(null, null,
				Utils.METHOD.verbindung.toString(),
				null,null,null, statName.VERBINDUNG.toString());			

		Log.e(TAG, "OnResume->LoadStatistikenDaten->doInBackground");
	}
	
	private View getBarChart(String chartTitle, String XTitle, String YTitle,
			String[][] xy) {

		XYSeries series = new XYSeries(YTitle);

		XYMultipleSeriesDataset Dataset = new XYMultipleSeriesDataset();
		Dataset.addSeries(series);

		XYMultipleSeriesRenderer Renderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer yRenderer = new XYSeriesRenderer();
		Renderer.addSeriesRenderer(yRenderer);

		// Renderer.setApplyBackgroundColor(true);
		// Renderer.setBackgroundColor(Color.BLACK);
		Renderer.setMarginsColor(Color.WHITE);
		Renderer.setTextTypeface(null, Typeface.BOLD);

		Renderer.setShowGrid(true);
		Renderer.setGridColor(Color.GRAY);

		Renderer.setChartTitle(chartTitle);
		Renderer.setLabelsColor(Color.BLACK);
		Renderer.setChartTitleTextSize(20);
		Renderer.setAxesColor(Color.BLACK);
		Renderer.setBarSpacing(0.5);

		Renderer.setXTitle(XTitle);
		Renderer.setYTitle(YTitle);
		
		Renderer.setXLabelsColor(Color.BLACK);
		Renderer.setYLabelsColor(0, Color.BLACK);
		Renderer.setXLabelsAlign(Align.CENTER);
		Renderer.setYLabelsAlign(Align.CENTER);
		Renderer.setXLabelsAngle(-25);

		Renderer.setXLabels(0);
		Renderer.setYAxisMin(0);

		yRenderer.setColor(Color.MAGENTA);
	    yRenderer.setDisplayChartValues(true);

		series.add(0, 0);
		Renderer.addXTextLabel(0, "");
		for (int r = 0; r < xy.length; r++) {
			// Log.i("DEBUG", (r+1)+" "+xy[r][0]+"; "+xy[r][1]);
			Renderer.addXTextLabel(r + 1, xy[r][0]);
			series.add(r + 1, Integer.parseInt(xy[r][1]));
		}
		series.add(11, 0);
		Renderer.addXTextLabel(xy.length + 1, "");

		View view = ChartFactory.getBarChartView(getActivity(), Dataset,
				Renderer, Type.DEFAULT);
		return view;
	}

	private void drawGraphic(LinearLayout layout, View view ,
							 String chartTitle, String xTitle, 
							 String yTitle, String [][] xy)
	{
		try {
			view = getBarChart(chartTitle, xTitle, yTitle,
					xy);
			layout.removeAllViews();
			// llBarChart.addView(vChart);
			layout.addView(view, new LayoutParams(
					LayoutParams.WRAP_CONTENT, 600));

		} catch (Exception e) {

		}		
	}
	
	private void executeRequest(final LinearLayout layout, final View view,
								final String method, final String charTitle, 
								final String xTitle, final String yTitle, final String name) {
		// Tag um Anfrage zu aborten
		String tag_string_req = "req_count";
		pDialog.setMessage("Statistikwerte werden geladen...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_STATISTIK, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG,
								"Z�hlen-Antwort: "
										+ response.toString());
						hideDialog();
						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
								if(name.equals(statName.GESAMT.toString()))
								{
									hMapGesamt.clear();
									gesamtArray = jObj.getJSONArray("count");
									for (int i = 0; i < gesamtArray.length(); i++) {
										JSONObject jo = gesamtArray.getJSONObject(i);
										String _table_name = jo.getString("table_name");
										int anzahl  = jo.getInt("anzahl");
										hMapGesamt.put(_table_name, anzahl);
										Log.d(TAG, _table_name + " " + anzahl);
									}
									if(!hMapGesamt.isEmpty())
									{
										String[][] xy = convertMapToArray(hMapGesamt);
										drawGraphic(layout, view, charTitle, xTitle, yTitle, xy);
									}
								}
								if(name.equals(statName.STATUS.toString()))
								{
									hMapStatus.clear();
									statusArray = jObj.getJSONArray("userStatus");
									for (int i = 0; i < statusArray.length(); i++) {
										JSONObject jo = statusArray.getJSONObject(i);
										String _status = jo.getString("status");
										int anzahl  = jo.getInt("anzahl");
										hMapStatus.put(_status, anzahl);
										Log.e(TAG, _status + " " + anzahl);
									}
									if(!hMapStatus.isEmpty())
									{
										String[][] xy = convertMapToArray(hMapStatus);
										drawGraphic(layout, view, charTitle, xTitle, yTitle, xy);
									}
								}
								if(name.equals(statName.VERBINDUNG.toString()))
								{
									verbindungList.clear();
							    	verbindungArray = jObj.getJSONArray("userVerbindung");
							    	for(int i = 0; i < verbindungArray.length(); i++)
							    	{
							    		JSONObject jo = verbindungArray.getJSONObject(i);
							    		Verbindung _verbindung = convertJsonObjectToVerbindung(jo);
							    		verbindungList.add(_verbindung);
							    		Log.e(TAG, _verbindung.getDatum() +" "+_verbindung.getAnzahl());
							    	}
//							    	adapter.notifyDataSetChanged();
							    	setDataFirstTime();
//							    	setData();
							    	xyPlot.redraw();
									
								}
							} else {
								// Fehler bei der Anmeldung
								String errorMsg = jObj.getString("error_msg");
								Utils.showToast(getActivity(), errorMsg);
							}

//							isListEmpty();
						} catch (JSONException e) {
							Log.e(TAG, e.getMessage().toString());
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG,
								"Z�hlungsfehler: " + error.getMessage());
						hideDialog();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Uebergabe der Notenparameter an url
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", method);
				return params;
			}
		};
		// Anfrage in Requestqueue einf�gen
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}
	
	private String[][] convertMapToArray(Map<String, Integer> _hMap)
	{
		String[][] array = new String[_hMap.size()][2];
		Iterator<?> it = _hMap.entrySet().iterator();
		int i = 0;
		while(it.hasNext())
		{
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>)it.next();
			array[i][0] = pair.getKey();
			int value = pair.getValue().intValue();
			array[i][1] = String.valueOf(value);
			i++;
		}
		
		return array;
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
				verbindungListToShow.clear();
				iterator = verbindungList.listIterator(merker);
				while(iterator.hasNext() && (merker < MAX_TO_SHOW))
				{
					verbindungListToShow.add(iterator.next());
					merker++;
					anzahl_aktuell_angezeigt++;
				}
				setData(verbindungListToShow);
				setEnableButton();
			}
		});
    	
    	previous.setOnClickListener(new View.OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
				verbindungListToShow.clear();
//				if(merker - MAX_TO_SHOW - anzahl_aktuell_angezeigt >= 0)
//				{
					merker = merker - (MAX_TO_SHOW + anzahl_aktuell_angezeigt);
					if(merker < 0)
					{
						merker = 0;
					}
//					merker -= MAX_TO_SHOW;
					iterator = verbindungList.listIterator(merker);
					anzahl_aktuell_angezeigt = 0;
					while(iterator.hasNext() && (anzahl_aktuell_angezeigt < MAX_TO_SHOW))
					{
						verbindungListToShow.add(iterator.next());
						merker++;
						anzahl_aktuell_angezeigt++;
					}
					setData(verbindungListToShow);
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
					if(merker >= verbindungList.size())
					{
						merker = verbindungList.size() - MAX_TO_SHOW;
					}
					iterator = verbindungList.listIterator(merker);
					verbindungListToShow.clear();
					anzahl_aktuell_angezeigt = 0;
					while(iterator.hasNext() && (anzahl_aktuell_angezeigt < MAX_TO_SHOW))
					{
						verbindungListToShow.add(iterator.next());
						merker++;
						anzahl_aktuell_angezeigt++;
					}
					setData(verbindungListToShow);
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
					merker = verbindungList.size() - MAX_TO_SHOW;
					iterator = verbindungList.listIterator(merker);
					verbindungListToShow.clear();
					anzahl_aktuell_angezeigt = 0;
					while(iterator.hasNext())
					{
						verbindungListToShow.add(iterator.next());
						merker++;
						anzahl_aktuell_angezeigt++;
					}
					setData(verbindungListToShow);
					setEnableButton();
//				}
//				merker = 0;
				
			}
		});
    }
    
    private void setEnableButton()
    {
    	first.setEnabled(darfFirst());
//    	first.setActivated(darfFirst());
    	next.setEnabled(darfNext());
    	previous.setEnabled(darfPrevious());
    	last.setEnabled(darfLast());
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
    	if(verbindungList != null)
    	{
    		return verbindungList.size() > merker;
    	}
    	return false;
    }
    
    private boolean darfLast()
    {
    	if(verbindungList != null)
    	{
    		return verbindungList.size() > merker;
    	}
    	return false;
    }

    
	private void setData(List<Verbindung> _verbindungList)
	{
		xyPlot.clear();
		verbindungAdapter = new VerbindungAdapter(_verbindungList);
		anzahlVerbindung = verbindungAdapter.get_anzahlVerbindung();
		mTage = verbindungAdapter.get_datum();
       
        com.androidplot.xy.XYSeries anzahlVerbindungSeries = new SimpleXYSeries(anzahlVerbindung,
				   SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
				   ANZAHL_VERBINDUNG);
        
        // Create a formatter to format Line and Point of systolisch series
        LineAndPointFormatter verbindungFormat = new LineAndPointFormatter(
                Color.rgb(0, 0, 255),                   // line color
                Color.rgb(200, 200, 200),               // point color
                null,
                null);                					// fill color (none)
         
        //add expence series to the xyplot:
        xyPlot.addSeries(anzahlVerbindungSeries, verbindungFormat);
        
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
				
				if(mTage.size() > 0)
				{
					return new StringBuffer(mTage.get(value));
				}
				return null;
//				return new StringBuffer(mMonths[(((Number)object).intValue())]);
			}
		});
        
        xyPlot.setPlotMarginBottom(50);
        xyPlot.setPlotMarginTop(50);
        xyPlot.setDomainLabel("Datum");
        xyPlot.setRangeLabel("Anzahl Verbindungen");
        xyPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        xyPlot.setRangeStepValue(1);
        xyPlot.setRangeBoundaries(0, 20, BoundaryMode.FIXED);
        
        //Increment X-Axis by 1 value
        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL,1);
//        xyPlot.getGraphWidget().setDomainLabelOrientation(-45);
        xyPlot.getGraphWidget().setMarginBottom(0);
        xyPlot.getGraphWidget().setPaddingBottom(0);
        xyPlot.getGraphWidget().setMarginRight(50);
        xyPlot.getGraphWidget().setMarginTop(30);
        xyPlot.getGraphWidget().setDomainLabelHorizontalOffset(5);
        xyPlot.setPlotMarginBottom(50);
        xyPlot.setPlotMarginRight(20);
        xyPlot.setPlotMarginTop(20);
        
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

	private class Verbindung
	{
		private String datum;
		private int anzahl;
		
		public Verbindung(String datum, int anzahl) {
			super();
			this.datum = datum;
			this.anzahl = anzahl;
		}
		
		public String getDatum() {
			return datum;
		}
	
		public void setDatum(String datum) {
			this.datum = datum;
		}
		public int getAnzahl() {
			return anzahl;
		}
		public void setAnzahl(int anzahl) {
			this.anzahl = anzahl;
		}
		
	}
	
	private class VerbindungAdapter
	{
		private List<Number> _anzahlVerbindung = new ArrayList<>();
		private List<String> _datum = new ArrayList<>();
		private List<Verbindung> _verbindungList = new ArrayList<>();
		
		public VerbindungAdapter(List<Verbindung> _verbindungList) {
			super();
			this._verbindungList = _verbindungList;
			init();
		}
		
		public void init()
		{
			for(Verbindung vb : _verbindungList)
			{
				_anzahlVerbindung.add(vb.getAnzahl());
				_datum.add(vb.getDatum());
			}
		}

		public List<Number> get_anzahlVerbindung() {
			return _anzahlVerbindung;
		}

		public void set_anzahlVerbindung(List<Number> _anzahlVerbindung) {
			this._anzahlVerbindung = _anzahlVerbindung;
		}

		public List<String> get_datum() {
			return _datum;
		}

		public void set_datum(List<String> _datum) {
			this._datum = _datum;
		}

		public List<Verbindung> get_verbindungList() {
			return _verbindungList;
		}

		public void set_verbindungList(List<Verbindung> _verbindungList) {
			this._verbindungList = _verbindungList;
		}
	}

	private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

	private Verbindung convertJsonObjectToVerbindung(JSONObject jo)
			throws JSONException 
	{
		int _anzahl = jo.getInt("anzahl");
		Object _datum = jo.getString("datum");
		String datumString = _datum.toString();
		Verbindung _verbindung = new Verbindung(datumString, _anzahl);
		return _verbindung;
	}

	private void setDataFirstTime()
	{
		anzahl_aktuell_angezeigt = 0;
		merker = 0;
		iterator = verbindungList.listIterator(merker);
		verbindungListToShow.clear();
		while(iterator.hasNext() && (merker < MAX_TO_SHOW))
		{
			verbindungListToShow.add(iterator.next());
			merker++;
			anzahl_aktuell_angezeigt++;
		}
		setData(verbindungListToShow);
		setEnableButton();
	}	
	
}
