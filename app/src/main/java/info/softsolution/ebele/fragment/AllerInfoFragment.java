package info.softsolution.ebele.fragment;

import java.util.ArrayList;
import java.util.HashMap;
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
import info.softsolution.ebele.adapter.InfoListAdapter;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.Information;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class AllerInfoFragment  extends Fragment
{
	
	private ListView listView;
	private InfoListAdapter infoListAdapter;
	private List<Information> infoItems;
	private ProgressDialog pDialog;
	private TextView emptyView;
	
	private static final String TAG = AllerInfoFragment.class.getSimpleName();

	
	public AllerInfoFragment() {
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
    	super.onCreateView(inflater, container, savedInstanceState);
    	pDialog = new ProgressDialog(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        listView = (ListView) rootView.findViewById(R.id.listInfo);
        infoItems = new ArrayList<Information>();
        infoListAdapter = new InfoListAdapter(getActivity(), infoItems);
        listView.setAdapter(infoListAdapter);
        emptyView = (TextView) rootView.findViewById(android.R.id.empty);
        
        if(infoItems.isEmpty())
        {
        	loadAllInfo(Utils.METHOD.readAll.toString());
        }
        
        return rootView;
    }

    @Override
    public void onResume()
    {
    	super.onResume();
    	loadAllInfo(Utils.METHOD.readAll.toString());
    }
   

	private void loadAllInfo(final String method)
	{
		//Tag used to cancel the request
		String tag_string_req = "req_all_info";
		pDialog.setMessage("Informationen werden geladen...");
		showDialog();
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_INFORMATION, new Response.Listener<String>() {
		
			@Override
			public void onResponse(String response)
			{
				Log.d(TAG,"Anwort Informationladen: " + response.toString());
                hideDialog();

                try 
                {
                	JSONObject jObj = new JSONObject(response);
				    boolean error = jObj.getBoolean("error");
				    if(!error)
				    {
				    	jObj.getJSONArray("infoList");
				    	parseJsonInfo(jObj);
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
                Log.e(TAG, "Informationladungsfehler: " + error.getMessage());
                Utils.showToast(getActivity(),error.getMessage());
                hideDialog();
			}
		}){
			@Override
			protected Map<String, String> getParams()
			{
				//Posting parameters to url
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", method);
				return params;
			}
		};
		
		AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
	}

    /**
     * Parsing json reponse and passing the data to info view list adapter
     * */
    private void parseJsonInfo(JSONObject response) {
        try {
        	infoItems.clear();
            JSONArray infoArray = response.getJSONArray("infoList");
 
            for (int i = 0; i < infoArray.length(); i++) 
            {
                JSONObject infoObj = (JSONObject) infoArray.get(i);
 
                Information item = Utils.parseInfo(infoObj);
                item.setId(infoObj.getInt("id"));                
                infoItems.add(item);
            }
            Utils.isListEmpty(listView, emptyView, infoItems);
            infoListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
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
	
}

