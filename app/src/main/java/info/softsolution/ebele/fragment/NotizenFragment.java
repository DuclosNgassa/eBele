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
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import info.softsolution.ebele.R;
import info.softsolution.ebele.adapter.NotizenListAdapter;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.Note;

public class NotizenFragment extends Fragment {

	private NotizenListAdapter adapter;
	private List<Note> noteList;
	private ListView listViewNote;
	private JSONArray noteArray;
	private String email_adresse;
	private TextView emptyView;
	private SessionManager session;
	private ProgressDialog pDialog;

	private ImageButton btnNew;
	private ImageButton btnSort;
	private ImageButton btnSpeichern;
	private ImageButton btnDelete;
	private ImageButton btnReturn;

	private EditText txtTitel;
	private EditText txtContent;
	private boolean isUpdate = false;
	private boolean isLoeschen = false;
	private int id = 0;

	private static final String TAG = NotizenFragment.class.getSimpleName();
	private static final String TYP = Note.NoteType.NOTIZEN.toString();

	public NotizenFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		session = new SessionManager(getActivity());
		pDialog = new ProgressDialog(getActivity());
		noteArray = new JSONArray();
		setEmail_adresse(session.getEmail());
		View rootView = inflater.inflate(R.layout.fragment_notizen, container,
				false);
		noteList = new ArrayList<Note>();

		adapter = new NotizenListAdapter(getActivity(), noteList);

		findViews(rootView);
		listViewNote.setAdapter(adapter);
		btnNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(true, null);
			}
		});

		// Loading all Notizen in Hinterground
		if (noteList.isEmpty()) {
			loadAllNote();
		}

		btnSort.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.showToast(getActivity(), "Noch nicht implementiert!");
			}
		});

		listViewNote.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Note note = ((Note) adapter.getItem(arg2));
				showDialog(false, note);
			}
		});

		return rootView;
	}

	private void findViews(View rootView) {

		listViewNote = (ListView) rootView.findViewById(R.id.note_list);
		btnNew = (ImageButton) rootView.findViewById(R.id.new_btn_notizen);
		btnSort = (ImageButton) rootView.findViewById(R.id.sort_btn_notizen);
		emptyView = (TextView) rootView.findViewById(android.R.id.empty);
	}

	@Override
	public void onResume() {
		super.onResume();
		noteList.clear();
		loadAllNote();

		Log.e(TAG, "OnResume->LoadAllNotizen->doInBackground");
	}

	private void loadAllNote() {
		// Tag used to cancel the request
		String tag_string_req = "req_all_note";
		pDialog.setMessage("Note werden geladen...");
		showDialog();
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_NOTE, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Anwort Notenladen: " + response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
								noteList.clear();
								noteArray = jObj.getJSONArray("noteList");
								for (int i = 0; i < noteArray.length(); i++) {
									JSONObject jo = noteArray.getJSONObject(i);
									Note _note = Utils
											.convertJsonObjectToNote(jo);
									noteList.add(_note);
									Log.d(TAG, _note.getTitel());
								}

								adapter.notifyDataSetChanged();
							} else {
								// Fehler bei der Anmeldung
								String errorMsg = jObj.getString("error_msg");
								showToast(errorMsg);
							}

							isListEmpty();
						} catch (JSONException e) {
							// JSON Fehler
							e.printStackTrace();
							showToast("Jsonfehler: " + e.getMessage());
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Notenladungsfehler: " + error.getMessage());
						showToast(error.getMessage());
						hideDialog();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to url
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", Utils.METHOD.readAll.toString());
				params.put("email_adresse", getEmail_adresse());
				params.put("typ", Note.NoteType.NOTIZEN.toString());
				return params;
			}
		};

		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void isListEmpty() {
		if (!noteList.isEmpty()) {
			emptyView.setVisibility(View.GONE);
			listViewNote.setVisibility(View.VISIBLE);
		} else {
			listViewNote.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
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

	private void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
		;
	}

	public String getEmail_adresse() {
		return email_adresse;
	}

	public void setEmail_adresse(String email_adresse) {
		this.email_adresse = email_adresse;
	}

	private void showDialog(boolean isNewNote, Note _note) {

		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_note);
		dialog.setTitle("Notizen machen");
		btnReturn = (ImageButton) dialog.findViewById(R.id.btnReturn);
		btnSpeichern = (ImageButton) dialog.findViewById(R.id.btnSpeichern);
		btnDelete = (ImageButton) dialog.findViewById(R.id.btnDelete);
		txtTitel = (EditText) dialog.findViewById(R.id.title);
		txtContent = (EditText) dialog.findViewById(R.id.content);

		if (!isNewNote) {
			setIdLocal(_note.getId());
			txtTitel.setText((CharSequence) _note.getTitel());
			txtContent.setText((CharSequence) _note.getContent());
			isUpdate = true;
		} else {
			isUpdate = false;
			btnDelete.setVisibility(View.GONE);
		}

		
		btnSpeichern.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String titel = txtTitel.getText().toString().trim();
				String content = txtContent.getText().toString().trim();

				if (!titel.isEmpty() && !content.isEmpty()) {
					if (isUpdate) {
						executeRequest(titel, content, TYP, getEmail_adresse(),
								Utils.METHOD.update.toString(), getIdLocal());
						
						Toast.makeText(getActivity(),
								TYP + " erfolgreich ge�ndert",
								Toast.LENGTH_LONG).show();
					} else {
						executeRequest(titel, content, TYP, getEmail_adresse(),
								Utils.METHOD.create.toString(), getIdLocal());
					}
					
					dialog.dismiss();

				} else {
					Toast.makeText(getActivity(),
							TYP + " darf nicht leer sein!", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		
		btnDelete.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(R.string.notiz_delete)
					   .setTitle(TYP +" l�schen")
					   .setCancelable(false)
					   .setPositiveButton(R.string.ja, 
						new DialogInterface.OnClickListener() 
   					    {
							public void onClick(DialogInterface _dialog, int which) 
							{
								isLoeschen = true;
								isUpdate = false;
								String titel = txtTitel.getText().toString().trim();
								String content = txtContent.getText().toString().trim();
								
								executeRequest(titel, content, TYP, 
												getEmail_adresse(), Utils.METHOD.delete.toString(), getIdLocal());
								dialog.dismiss();
							}
					    })
						.setNegativeButton(R.string.nein, new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface _dialog, int which) 
							{
								_dialog.cancel();
							}
						});
				builder.create().show();
			}
		});
		
		dialog.show();

		btnReturn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private void executeRequest(final String titel, final String content,
			final String typ, final String email, final String method,
			final int id) {
		// Tag um Anfrage zu aborten
		String tag_string_req = "req_note";
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_NOTE, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG,
								"Notespeicherungsantwort: "
										+ response.toString());

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
								if (isUpdate) {
									Utils.showToast(getActivity(),
											TYP + " erfolgreich ge�ndert");
								} else if (isLoeschen) {
									Utils.showToast(getActivity(),
											TYP + " erfolgreich gel�scht");
								} else {
									Utils.showToast(
											getActivity(),
											TYP	+ " erfolgreich gespeichert");
								}
								loadAllNote();
							} else {
								// Fehler bei der Anmeldung
								String errorMsg = jObj.getString("error_msg");
								Utils.showToast(getActivity(),
										errorMsg);
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
								"Notespeicherungsfehler: " + error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Uebergabe der Notenparameter an url
				Map<String, String> params = new HashMap<String, String>();
				params.put("titel", titel);
				params.put("content", content);
				params.put("typ", typ);
				params.put("email_adresse", email);
				params.put("method", method);
				params.put("id", String.valueOf(id));
				return params;
			}
		};
		// Anfrage in Requestqueue einf�gen
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	public int getIdLocal() {
		return id;
	}

	public void setIdLocal(int id) {
		this.id = id;
	}

}
