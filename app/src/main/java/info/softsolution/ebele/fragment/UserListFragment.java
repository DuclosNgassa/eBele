package info.softsolution.ebele.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import info.softsolution.ebele.R;
import info.softsolution.ebele.adapter.MeldungListAdapter;
import info.softsolution.ebele.adapter.UserListAdapter;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.Meldung;
import info.softsolution.ebele.model.User;

public class UserListFragment extends Fragment
{

	private boolean jaNein = false;
	private UserListAdapter adapter;
	private MeldungListAdapter adapterMeldung;
	private List<User> userList;
	private List<Meldung> meldungList;
	private ListView listViewUser;
	private ListView listViewMeldung;
	private TextView emptyView;
	private JSONArray userArray = null;
	private JSONArray meldungArray = null;
	private ImageButton btnEntsprren;
	private ImageButton btnSperren;
	private ImageButton btnLoeschen;

	private ImageButton dialogSperren;
	private ImageButton dialogEntsperren;
	private ImageButton dialogLoeschen;
	private ImageButton dialogAbbrechen;
	
	private TextView txtName;
	private TextView txtEmail;
	private TextView txtAnmeldedatum;
	private TextView txtStatus;
	private Map<String, List<Meldung>> hMapMeldung;
	
	private Button btnUserAnschreiben;
	
	private ProgressDialog pDialog;
	
	
	private static final String TAG = UserListFragment.class.getSimpleName();
	
	public UserListFragment() {}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
    	super.onCreateView(inflater, container, savedInstanceState);

    	pDialog = new ProgressDialog(getActivity());
//	   	Bundle bundle = getArguments();
////    	setName(bundle.getString("name"));
//    	setEmail_adresse(bundle.getString("email"));
    	hMapMeldung = new HashMap<String, List<Meldung>>();
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        findViews(rootView);
        
        userList = new ArrayList<User>();
        userList.clear();
        adapter = new UserListAdapter(getActivity(), userList);
        listViewUser.setAdapter(adapter);
        

        
        //Loading all Notizen in Hinterground
		if( userList.isEmpty())
		{
//			new LoadAllNotizen().execute();
			readAllUser(Utils.METHOD.readAll.toString());
		}
         
        isListEmpty();
      
        listViewUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{

				String name = ((User)adapter.getItem(arg2)).getName();
				String email = ((User)adapter.getItem(arg2)).getUser_email();
				String status = ((User)adapter.getItem(arg2)).getStatus();
				String anmeldeDatum = ((User)adapter.getItem(arg2)).getCreated_at();
				
//				int _id = arg2 + 1;
				Bundle bundle = new Bundle();
				bundle.putString("email", email);
				
				showDetailsUser(name, email, anmeldeDatum, status, hMapMeldung.get(email));
				
			}
		});
        
        return rootView;
    }

	private void isListEmpty() {
		if(!userList.isEmpty())
        {
        	emptyView.setVisibility(View.GONE);
        	listViewUser.setVisibility(View.VISIBLE);
        }
        else
        {
        	emptyView.setVisibility(View.VISIBLE);
        	listViewUser.setVisibility(View.GONE);
        }
	}
    
	private void showDetailsUser(String name, String email, String anmeldeDatum, String status, List<Meldung> listMeldung) 
	{

		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_user_details);
		dialog.setTitle("Nutzerindetails");

		txtName = (TextView) dialog.findViewById(R.id.dialog_name);
		txtEmail = (TextView) dialog.findViewById(R.id.dialog_email);
		txtAnmeldedatum = (TextView) dialog.findViewById(R.id.dialog_anmeldedatum);
		txtStatus = (TextView) dialog.findViewById(R.id.dialog_status);
		listViewMeldung = (ListView) dialog.findViewById(R.id.user_meldungen);
		
		meldungList = listMeldung;
        
		adapterMeldung = new MeldungListAdapter(getActivity(), meldungList);
        listViewMeldung.setAdapter(adapterMeldung);

		txtName.setText(name);
		txtEmail.setText(email);
		txtAnmeldedatum.setText(anmeldeDatum);
		txtStatus.setText(status);
		final String _name = name; 
		final String _email = email; 
		dialog.show();

		dialogLoeschen = (ImageButton) dialog.findViewById(R.id.dialog_loeschen);
		dialogLoeschen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAlertDialog("L�schen", "Wollen Sie Nutzerin " + _name + " l�schen?", getActivity(), Utils.METHOD.delete.toString(),_email);
			}
		});
		dialogSperren = (ImageButton) dialog.findViewById(R.id.dialog_sperren);
		dialogSperren.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAlertDialog("L�schen", "Wollen Sie Nutzerin " + _name + " sperren?", getActivity(), Utils.METHOD.sperren.toString(), _email);
			}
		});
		dialogEntsperren = (ImageButton) dialog.findViewById(R.id.dialog_entsperren);
		dialogEntsperren.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAlertDialog("L�schen", "Wollen Sie Nutzerin " + _name + " entsperren?", getActivity(), Utils.METHOD.entsperren.toString(), _email);				
			}
		});

		dialogAbbrechen = (ImageButton) dialog.findViewById(R.id.dialog_cancel);
		dialogAbbrechen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	public void showAlertDialog(String title, String msg, Context context, final String method, final String _email) 
	{
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(R.string.ja,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
									switch(method)
									{
									case "sperren" :
										executeRequest(Utils.METHOD.sperren.toString(), _email);
										break;
									case "entsperren" :
										executeRequest(Utils.METHOD.entsperren.toString(), _email);
										break;
									case "delete" :
										executeRequest(Utils.METHOD.delete.toString(), _email);
										break;
									default :
										Utils.showToast(getActivity(), "Unbekannte Methode!!!");
										break;
									}
								}
						})
				.setNegativeButton(R.string.nein,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}
	
	private void findViews(View rootView) 
	{
		final String alle = "alle";
		listViewUser = (ListView) rootView.findViewById(R.id.user_list);
		btnSperren = (ImageButton) rootView.findViewById(R.id.btn_alle_sperren);
        btnEntsprren = (ImageButton) rootView.findViewById(R.id.btn_alle_entsperren);
        btnLoeschen = (ImageButton) rootView.findViewById(R.id.btn_alle_loeschen);
        emptyView = (TextView) rootView.findViewById(android.R.id.empty);

		btnSperren.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showAlertDialog("Sperren", "Wollen Sie alle Nutzerinnen sperren?", getActivity(),Utils.METHOD.sperren.toString(), "alle");
			}
		});
        
		btnEntsprren.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				showAlertDialog("Sperren", "Wollen Sie alle Nutzerinnen sperren?", getActivity(),Utils.METHOD.entsperren.toString(), "alle");
			}
		});

		btnLoeschen.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				showAlertDialog("Sperren", "Wollen Sie alle Nutzerinnen sperren?", getActivity(),Utils.METHOD.delete.toString(), "alle");
			}
		});
        
	}
	
	private void executeRequest(final String method, final String alleOrEmail)
	{
		//Tag used to cancel the request
		String tag_string_req = "req_user";
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
//				    	userList.clear();
//				    	hMapMeldung.clear();
				    	readAllUser(Utils.METHOD.readAll.toString());
//				    	adapter.notifyDataSetChanged();
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
				params.put("alleOrEmail", alleOrEmail);
				return params;
			}
		};
		
		AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
	}

	
	@Override
	public void onResume()
	{
		super.onResume();
		userList.clear();
		hMapMeldung.clear();
		readAllUser(Utils.METHOD.readAll.toString());
		Log.e(TAG, "OnResume->LoadAllNotizen->doInBackground");
	}
	

	private void readAllUser(final String method)
	{
		//Tag used to cancel the request
		String tag_string_req = "req_all_user";
		pDialog.setMessage("User werden geladen...");
		showDialog();
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_MANAGE_USER, new Response.Listener<String>() {
		
			@Override
			public void onResponse(String response)
			{
				Log.d(TAG,"Anwort Nutzerinladung: " + response.toString());
                hideDialog();

                try 
                {
                	JSONObject jObj = new JSONObject(response);
				    boolean error = jObj.getBoolean("error");
				    if(!error)
				    {
				    	userList.clear();
				    	hMapMeldung.clear();
				    	userArray = jObj.getJSONArray("userList");
				    	for(int i = 0; i < userArray.length(); i++)
				    	{
				    		JSONObject jo = userArray.getJSONObject(i);
				    		User user = Utils.convertJsonObjectToUser(jo);
				    		
				    		meldungArray = jo.getJSONArray("meldungen");
				    		List<Meldung> listMeldung = new ArrayList<Meldung>();
				    		
				    		for(int j = 0; j < meldungArray.length(); j++)
				    		{
				    			JSONObject jmeldung = meldungArray.getJSONObject(j);
				    			Meldung m = Utils.convertJsonObjectToMeldung(jmeldung);
				    			listMeldung.add(m);
				    		}
				    		
				    		hMapMeldung.put(user.getUser_email(), listMeldung);
				    		userList.add(user);
				    		Log.d(TAG, user.getName());
				    	}
				    	isListEmpty();
				    	adapter.notifyDataSetChanged();
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
                Log.e(TAG, "Nutzerinnenladungsfehler: " + error.getMessage());
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
				return params;
			}
		};
		
		AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
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
    	Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();;
    }

}

