package info.softsolution.ebele.fragment;

import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.codebutler.android_websockets.WebSocketClient;

import info.softsolution.ebele.R;
import info.softsolution.ebele.adapter.MessageListAdapter;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.JSONParser;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.Nachricht;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CommunityFragment extends Fragment 
{
	//LogCat tag
	private static final String TAG = CommunityFragment.class.getSimpleName();

	private Button btnSend;
	private EditText inputMsg;
	private ProgressDialog pDialog;
	
	private WebSocketClient client;
	
	//Chat MessageListAdapter
	private MessageListAdapter adapter;
	private List<Nachricht> listMessages;
	private ListView listViewMessages;
	private JSONParser jParser ;
	private JSONArray messages = null;	
	private Utils utils;
	
	//Userdaten
	private String name ;
	private String email;
	
	private Nachricht nachricht;
	
	//JSON Flag um die Nachrichtenart zu identifizieren
	private static final String TAG_SELF = "self";
	private static final String TAG_NEW	 = "new";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_EXIT	= "exit";
	
	
	public CommunityFragment(){}


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
 
    	Bundle bundle = getArguments();
    	setName(bundle.getString("name"));
    	setEmail(bundle.getString("email"));
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);
        initFelder(rootView);
        initBntSend();
        jParser = new JSONParser();
        listMessages = new ArrayList<Nachricht>();
        adapter = new MessageListAdapter(getActivity(), listMessages);
        listViewMessages.setAdapter(adapter);
        createWebSocket();
        new Load10LetzteNachricht().execute();
        client.connect();
        return rootView;
    }

	/**
	 * Erzeugt den Websocket-Klient, dieser hat die Callbackmethoden	
	 */
	@SuppressWarnings("deprecation")
	public void createWebSocket()
	{
		client = new WebSocketClient(URI.create(AppConfig.URL_WEBSOCKET
				+ URLEncoder.encode(name)), new WebSocketClient.Listener() {
					
					@Override
					public void onMessage(byte[] data) {
						Log.d(TAG, String.format("Binarynachricht empfangen! %s", bytesToHex(data)));
						
						//JSON-Formatierung
						parseMessage(bytesToHex(data));
					}
					
					/*
					 * @see com.codebutler.android_websockets.WebSocketClient.Listener#onMessage(java.lang.String)
					 */
					@Override
					public void onMessage(String message) 
					{
						Log.d(TAG, String.format("Stringnachricht empfangen! %s", message));
						
						parseMessage(message);
					}
					
					@Override
					public void onError(Exception error) 
					{
						Log.e(TAG, "Error! : " + error);
//						Utils.showToast(getActivity(),"Error! : " + error);
					}
					
					@Override
					public void onDisconnect(int code, String reason) {
						String message = String.format(Locale.GERMAN, "Ausgeloggt! Code: %d Reason: %s", code, reason);
						
//						Utils.showToast(getActivity(), message);
						
						//Clear the SessionId aus Shared preferences
						utils.storeSessionId(null);
					}
					
					@Override
					public void onConnect() {
						
					}
				}, null);
	}
	
	/**
     * Parsing the JSON message received from server The intent of message will
     * be identified by JSON node 'flag'. flag = self, message belongs to the
     * person. flag = new, a new person joined the conversation. flag = message,
     * a new message received from server. flag = exit, somebody left the
     * conversation.
     * */	
	private void parseMessage(final String msg)
	{
		try{
			JSONObject jObj = new JSONObject(msg);
			String flag = jObj.getString("flag");
			if(flag.equalsIgnoreCase(TAG_SELF))
			{
				String sessionId = jObj.getString("sessionId");
				//speichern session id in shared preferences
				utils.storeSessionId(sessionId);
				Log.e(TAG, "Ihre Session id: " + utils.getSessionId());
			}
			else if(flag.equalsIgnoreCase(TAG_NEW))
			{
				String name = jObj.getString("name");
				String message = jObj.getString("message");
				String onlineCount = jObj.getString("onlineCount");
//				Utils.showToast(getActivity(), name + message + ". Gerade sind " + onlineCount
//						+"Personen online!");
			}
			else if(flag.equalsIgnoreCase(TAG_MESSAGE))
			{
				//neue Nachricht empfangen
				String fromName = name;
				String message = jObj.getString("message");
				String sessionId = jObj.getString("sessionId");
				boolean isSelf = true;
				
				//Checking if the message was sent by you
				if(!sessionId.equals(utils.getSessionId()))
				{
					fromName = jObj.getString("name");
					isSelf = false;
				}
				String pattern = "yyyy-MM-dd HH:mm:ss";
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				String dateToString = sdf.format(new Date());
				
				nachricht = new Nachricht(fromName, message, isSelf, dateToString);
			
				//Nachricht in der Listchat anh�ngen
				appendMessage(nachricht);
				//Nachricht in Wamp DB speichern
//				new CreateNewMessage().execute();
				
			}
			else if(flag.equalsIgnoreCase(TAG_EXIT))
			{
				String name = jObj.getString("name");
				String message = jObj.getString("message");
//				Utils.showToast(getActivity(), name + message);
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(client != null & client.isConnected())
		{
			client.disconnect();
		}
	}
	
	/*
	 * Nachricht in der Listview anh�ngen
	 */
	private void appendMessage(final Nachricht m)
	{
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				listMessages.add(m);
				adapter.notifyDataSetChanged();
				//Playing device�s notification
				playBeep();
			}
		});
	}
	
	/*
	 * Playing device�s default notification sound
	 */
	public void playBeep()
	{
		try{
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
			r.play();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
	/*
	 * Nachricht an Server senden
	 */
	private void sendMessageToServer(String message)
	{
		if(client != null && client.isConnected())
		{
			client.send(message);
		}
	}
	
	private void initBntSend() 
	{
		btnSend.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				// Nachricht an Websocket Server senden
				if(!inputMsg.getText().toString().equals(""))
				{
					//Nachricht in der DB speichern
					storeMessage(inputMsg.getText().toString(), getEmail());
					
					sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText().toString()));
				}
				else
				{
					Toast.makeText(getActivity(), "Nachricht eingeben!", Toast.LENGTH_SHORT).show();
				}
				//InputMsg reinigen
				inputMsg.setText("");
			}
		});
	}

	private void initFelder(View rootView) 
	{
		btnSend = (Button) rootView.findViewById(R.id.btnSend);
        inputMsg = (EditText) rootView.findViewById(R.id.inputMsg);
        listViewMessages = (ListView)rootView.findViewById(R.id.list_view_messages);
        
        utils = new Utils(getActivity());
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getEmail() 
	{
		return email;
	}

	public void setEmail(String email) 
	{
		this.email = email;
	}

	private void storeMessage(final String msg, final String email)
	{
		//Tag um Anfrage zu aborten
		String tag_string_req = "req_nachricht";
		
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_STORE_MESSAGE, new Response.Listener<String>() {
			
			@Override 
			public void onResponse(String response)
			{
				Log.d(TAG, "Nachrichtspeicherungsantwort: " + response.toString());
				
				try
				{
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					
					//Speicherung fehlerhaft
					if(error)
					{
						String errorMsg = jObj.getString("error_msg");
//						Utils.showToast(getActivity(),errorMsg);
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
				Log.e(TAG, "Nachrichtspeicherungsfehler: " + error.getMessage());
//				Utils.showToast(getActivity(), error.getMessage());
			}
		}) {
			@Override
			protected Map<String,String> getParams()
			{
				//Uebergabe des Nachrichtparameter an url
				Map<String,String> params = new HashMap<String, String>();
				params.put("message", msg);
				params.put("email_adresse", email);
				
				return params;
			}
		};
		//Anfrage in Requestqueue einf�gen
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}


	public Nachricht getNachricht()
	{
		return nachricht;
	}


	public void setNachricht(Nachricht nachricht) 
	{
		this.nachricht = nachricht;
	}
	
	class Load10LetzteNachricht extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog = new  ProgressDialog(getActivity());
			pDialog.setMessage("Laden von Nachrichten. Bitte warten Sie!");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		protected String doInBackground(String...args)
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json = jParser.makeHttpRequest(AppConfig.URL_10_OLDER_MESSAGE, "GET", params);
			Log.d(TAG, json.toString());
			try
			{
				boolean success = json.getBoolean("success");
				if(success)
				{
					messages = json.getJSONArray("nachricht");
					for(int i = messages.length()-1; i >= 0; i--)
					{
						JSONObject jo = messages.getJSONObject(i);
						String _email_adresse = jo.getString("email_adresse");
						String _nachricht = jo.getString("message");
						String created_at = jo.getString("created_at");
						String _user_name = jo.getString("user_name");
						boolean isSelf = getEmail().equals(_email_adresse) ? true : false;
						Nachricht message = new Nachricht(_user_name, _nachricht, isSelf, created_at);
						listMessages.add(message);
					}
				}
			}
			catch(JSONException e)
			{
				e.printStackTrace();
				Log.e(TAG,e.toString());
			}
			return null;
		}
		
		protected void onPostExecute(String file_url)
		{
			pDialog.dismiss();
			getActivity().runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					listViewMessages.setAdapter(adapter);
				}
			});
		}
	}
}
