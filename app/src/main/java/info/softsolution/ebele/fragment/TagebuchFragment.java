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
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

import info.softsolution.ebele.R;
import info.softsolution.ebele.adapter.TagebuchListAdapter;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.Note;

public class TagebuchFragment extends Fragment {
	private static final String TAG = TagebuchFragment.class.getSimpleName();
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private Map<String, List<Note>> listDataChild;
	private SessionManager session;
	private ProgressDialog pDialog;
	private JSONArray tageBuchArray;
	private List<Note> noteList;
	public static final String TYP = Note.NoteType.TAGEBUCH.toString();
	private TextView emptyView;
	private ImageButton btnNew;
	private ImageButton btnSpeichern;
	private ImageButton btnDelete;
	private ImageButton btnReturn;
	private String email_adresse;

	public String getEmail_adresse() {
		return email_adresse;
	}

	public void setEmail_adresse(String email_adresse) {
		this.email_adresse = email_adresse;
	}

	private EditText txtTitel;
	private EditText txtContent;
	
	private boolean isUpdate = false;
	private boolean isLoeschen = false;
	private int id = 0;

	public int getIdLocal() {
		return id;
	}

	public void setIdLocal(int id) {
		this.id = id;
	}

	public TagebuchFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_tagebuch, container,
				false);
		session = new SessionManager(getActivity());
		pDialog = new ProgressDialog(getActivity());
		setEmail_adresse(session.getEmail());
		tageBuchArray = new JSONArray();
		noteList = new ArrayList<Note>();
		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		btnNew = (ImageButton) rootView.findViewById(R.id.new_btn_notizen);
		emptyView = (TextView) rootView.findViewById(android.R.id.empty);
		listDataHeader = new ArrayList<>();
		listDataChild = new HashMap<>();
		expListView.setClickable(true);
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Note note = listDataChild
						.get(listDataHeader.get(groupPosition)).get(
								childPosition);
				showDialog(false, note);
				return false;
			}
		});

		btnNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(true, null);
							}
		});

		// Loading all Notizen in Hinterground
		if (noteList.isEmpty()) {
			loadAllTageBuch();
		}

		return rootView;
	}

	private void loadAllTageBuch() {
		// Tag used to cancel the request
		String tag_string_req = "req_all_tageBuch";
		pDialog.setMessage("Tagebuch wird geladen...");
		showDialog();
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_NOTE, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG,
								"Anwort Tagebuchladung: " + response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
								clearListe();
								tageBuchArray = jObj.getJSONArray("noteList");
								for (int i = 0; i < tageBuchArray.length(); i++) {
									JSONObject jo = tageBuchArray
											.getJSONObject(i);
									Note _note = Utils
											.convertJsonObjectToNote(jo);
									noteList.add(_note);
									Log.d(TAG, _note.getTitel());
								}
								// adapter.notifyDataSetChanged();
								prepareListData();

							} else {
								// Fehler bei der Anmeldung
								String errorMsg = jObj.getString("error_msg");
								Utils.showToast(getActivity(), errorMsg);
							}

							isListEmpty();

						} catch (JSONException e) {
							// JSON Fehler
							e.printStackTrace();
							Utils.showToast(getActivity(),
									"Jsonfehler: " + e.getMessage());
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Notenladungsfehler: " + error.getMessage());
						Utils.showToast(getActivity(), error.getMessage());
						hideDialog();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to url
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", Utils.METHOD.readAll.toString());
				params.put("email_adresse", session.getEmail());
				params.put("typ", TYP);
				return params;
			}
		};

		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void isListEmpty() {
		if (!noteList.isEmpty()) {
			expListView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		} else {
			expListView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}
	}

	private void clearListe() {
		noteList.clear();
		listDataChild.clear();
		listDataHeader.clear();
	}

	private void prepareListData() {
		if (!noteList.isEmpty()) {
			emptyView.setVisibility(View.GONE);
			String jahr = "";
			String monat = "";
			String merker = "";
			List<Note> merkerList = new ArrayList<>();

			Iterator<Note> it = noteList.listIterator();
			Note _n = it.next();
			merkerList.add(_n);

			String datum = _n.getUpdated_at();
			jahr = datum.substring(0, 4);
			monat = datum.substring(5, 7);
			merker = convertMonat(monat) + " " + jahr;
			Note n = new Note();
			while (it.hasNext()) {
				n = it.next();
				datum = n.getUpdated_at();
				jahr = datum.substring(0, 4);
				monat = datum.substring(5, 7);
				String monatJahr = convertMonat(monat) + " " + jahr;
				if (monatJahr.equals(merker)) {
					merkerList.add(n);
				} else {
					listDataChild.put(merker, merkerList);
					listDataHeader.add(merker);
					merkerList = new ArrayList<>();
					merker = monatJahr;
					merkerList.add(n);
				}

			}
			// Falls immer monatJahr.equals(merker), dann muss hier die Listen
			// gef�llt werden
			listDataChild.put(merker, merkerList);
			listDataHeader.add(merker);
		} else {
			emptyView.setVisibility(View.VISIBLE);
		}

		listAdapter = new TagebuchListAdapter(getActivity(), listDataHeader,
				listDataChild, expListView);
		expListView.setAdapter(listAdapter);

	}

	private String convertMonat(String monat) {
		String monatToString = "";
		switch (monat) {
		case "01":
			monatToString = "Januar";
			break;
		case "02":
			monatToString = "Februar";
			break;
		case "03":
			monatToString = "Maerz";
			break;
		case "04":
			monatToString = "April";
			break;
		case "05":
			monatToString = "Mai";
			break;
		case "06":
			monatToString = "Juni";
			break;
		case "07":
			monatToString = "Juli";
			break;
		case "08":
			monatToString = "August";
			break;
		case "09":
			monatToString = "September";
			break;
		case "10":
			monatToString = "Oktober";
			break;
		case "11":
			monatToString = "November";
			break;
		case "12":
			monatToString = "Dezember";
			break;
		}
		return monatToString;
	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

	@Override
	public void onResume() {
		super.onResume();
		clearListe();
		loadAllTageBuch();

		Log.e(TAG, "OnResume->LoadAllNotizen->doInBackground");
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
								loadAllTageBuch();
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

	private void showDialog(boolean isNewNote, Note _note) {

		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_note);
		dialog.setTitle("Tagebuch");
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
					loadAllTageBuch();
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
								loadAllTageBuch();
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
	
}
