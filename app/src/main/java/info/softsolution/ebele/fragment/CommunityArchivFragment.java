package info.softsolution.ebele.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.softsolution.ebele.R;
import info.softsolution.ebele.adapter.MessageListAdapter;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.helper.JSONParser;
import info.softsolution.ebele.model.Nachricht;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class CommunityArchivFragment extends Fragment {
	
	String name ;
	String user_email;
	
	public static final String TAG = CommunityArchivFragment.class.getSimpleName();
	private ProgressDialog pDialog;
	private JSONParser jParser ;
	private JSONArray messages = null;
	
	private MessageListAdapter adapter;
	private List<Nachricht> listMessages;
	private ListView listViewMessages;
	
//	private Utils utils;
	
	//JSON Flag um die Nachrichtenart zu identifizieren
//	private static final String TAG_SELF = "self";
//	private static final String TAG_NEW	 = "new";
//	private static final String TAG_MESSAGE = "message";
//	private static final String TAG_EXIT	= "exit";

	public CommunityArchivFragment(){}



	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
    	Bundle bundle = getArguments();
    	setName(bundle.getString("name"));
    	setUser_email(bundle.getString("email"));
    	View rootView = inflater.inflate(R.layout.fragment_nachricht_archiv, container, false);
    	listViewMessages = (ListView)rootView.findViewById(R.id.list_view_messages);
		
    	jParser = new JSONParser();
        listMessages = new ArrayList<Nachricht>();
        
        new LoadAllMessage().execute();
                
        return rootView;
    }
	
	
	//Hintergrund Async Task um alle Nachrichten zu laden
	class LoadAllMessage extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Laden von Nachrichten. Bitte warten...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		protected String doInBackground(String... args)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json = jParser.makeHttpRequest(AppConfig.URL_All_MESSAGE, "GET", params);
			Log.d(TAG,json.toString());
			try{
				boolean success = json.getBoolean("success");
				if(success)
				{
					messages = json.getJSONArray("nachricht");
					for(int i = 0; i < messages.length(); i++)
					{
						JSONObject jo = messages.getJSONObject(i); 
						String _email_adresse = jo.getString("email_adresse");
						String _nachricht = jo.getString("message");
						Object created_at = jo.get("created_at");
						String _user_name = jo.getString("user_name");
//						Date created_at = new Date(jo.getString("created_at"));
						boolean isSelf = getUser_email().equals(_email_adresse)? true : false;
						Nachricht message = new Nachricht(_user_name, _nachricht, isSelf, created_at.toString());
						listMessages.add(message);
						
					}
				}
				else
				{
					Toast.makeText(getActivity(), "Keine Nachricht vorhanden", Toast.LENGTH_LONG).show();;
				}
			}
			catch(JSONException e)
			{
				e.printStackTrace();
				Log.e(TAG, e.toString());
			}
			return null;
		}

		protected void onPostExecute(String file_url)
		{
			pDialog.dismiss();
			//updating UI aus der Hintergrundtask
			getActivity().runOnUiThread(new Runnable(){
				public void run()
				{
					adapter = new MessageListAdapter(getActivity(), listMessages);
					listViewMessages.setAdapter(adapter);
				}
			});
		}
	}
	



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getUser_email() {
		return user_email;
	}



	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

}
