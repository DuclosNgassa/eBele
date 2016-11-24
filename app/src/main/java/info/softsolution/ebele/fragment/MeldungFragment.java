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
import info.softsolution.ebele.adapter.MeldungListAdapter;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.Meldung;

public class MeldungFragment extends Fragment {

	private MeldungListAdapter adapter;
	private List<Meldung> meldungList;
	private ListView listViewMeldung;
	private JSONArray meldungArray;
	private View _view;
	private TextView emptyView;
	private ProgressDialog pDialog;

	private ImageButton btnAntwort;
	private ImageButton btnReturn;
	private ImageButton btnNew;
	private ImageButton btnSort;
	private ImageButton btnDelete;

	private EditText txtTitel;
	private EditText txtContent;

	private static final String TAG = MeldungFragment.class.getSimpleName();

	public MeldungFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		pDialog = new ProgressDialog(getActivity());
		meldungArray = new JSONArray();
		View rootView = inflater.inflate(R.layout.fragment_notizen, container,
				false);
		meldungList = new ArrayList<Meldung>();

		adapter = new MeldungListAdapter(getActivity(), meldungList);

		findViews(rootView);
		listViewMeldung.setAdapter(adapter);
		// btnNew.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// showDialog(true, null);
		// }
		// });

		// Loading all Notizen in Hinterground
		if (meldungList.isEmpty()) {
			loadAllMeldung();
		}

		// btnSort.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Utils.showToast(getActivity(), "Noch nicht implementiert!");
		// }
		// });

		listViewMeldung.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Meldung meldung = ((Meldung) adapter.getItem(arg2));
				showDialog(meldung);
			}
		});

		return rootView;
	}

	private void findViews(View rootView) {

		_view = (View) rootView.findViewById(R.id.line);
		listViewMeldung = (ListView) rootView.findViewById(R.id.note_list);
		btnNew = (ImageButton) rootView.findViewById(R.id.new_btn_notizen);
		btnSort = (ImageButton) rootView.findViewById(R.id.sort_btn_notizen);
		emptyView = (TextView) rootView.findViewById(android.R.id.empty);
		btnNew.setVisibility(View.GONE);
		btnSort.setVisibility(View.GONE);
		_view.setVisibility(View.GONE);

	}

	@Override
	public void onResume() {
		super.onResume();
		meldungList.clear();
		loadAllMeldung();

		Log.e(TAG, "OnResume->LoadAllMeldung->doInBackground");
	}

	private void loadAllMeldung() {
		// Tag used to cancel the request
		String tag_string_req = "req_all_meldung";
		pDialog.setMessage("Meldungen werden geladen...");
		showDialog();
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_BENUTZER_MELDEN, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG,
								"Anwort Meldungenladen: " + response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
								meldungList.clear();
								meldungArray = jObj
										.getJSONArray("meldungenList");
								for (int i = 0; i < meldungArray.length(); i++) {
									JSONObject jo = meldungArray
											.getJSONObject(i);
									Meldung _meldung = Utils
											.convertJsonObjectToMeldung(jo);
									meldungList.add(_meldung);
									Log.d(TAG, _meldung.getWer());
								}

								adapter.notifyDataSetChanged();
							} else {
								// Fehler bei der Anmeldung
								String errorMsg = jObj.getString("error_msg");
								showToast(errorMsg);
							}

							Utils.isListEmpty(listViewMeldung, emptyView,
									meldungList);
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
				return params;
			}
		};

		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
	}

	private void showDialog(Meldung _meldung) {

		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_note);
		dialog.setTitle("Notizen machen");
		btnReturn = (ImageButton) dialog.findViewById(R.id.btnReturn);
		btnAntwort = (ImageButton) dialog.findViewById(R.id.btnSpeichern);
		btnDelete = (ImageButton) dialog.findViewById(R.id.btnDelete);

		txtTitel = (EditText) dialog.findViewById(R.id.title);
		txtContent = (EditText) dialog.findViewById(R.id.content);
		final int id = _meldung.getId();
		String titel = _meldung.getWer() + "   " + _meldung.getWann().subSequence(0, 10);
		txtTitel.setText((CharSequence) titel);
		txtContent.setText((CharSequence) _meldung.getWas());
		
		txtTitel.setFocusable(false);
		txtTitel.setClickable(false);
		txtContent.setFocusable(false);
		txtContent.setClickable(false);

		btnAntwort.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(),
						"Dieses Feature Noch in Bearbeitung!",
						Toast.LENGTH_LONG).show();
			}
		});

		btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage(R.string.notiz_delete)
						.setTitle("Meldung l�schen")
						.setCancelable(false)
						.setPositiveButton(R.string.ja,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface _dialog, int which) {
										Toast.makeText(getActivity(),
												"Dieses Feature Noch in Bearbeitung!",
												Toast.LENGTH_LONG).show();

										executeRequest(Utils.METHOD.delete.toString(),
												id);
										dialog.dismiss();
									}
								})
						.setNegativeButton(R.string.nein,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface _dialog, int which) {
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

	private void executeRequest(final String method, final int id) {
		// Tag um Anfrage zu aborten
		String tag_string_req = "req_note";
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_BENUTZER_MELDEN, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG,
								"meldungsantwort: "
										+ response.toString());

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
									Utils.showToast(getActivity(),
											"Operation erfolgreich durchgef�hrt");
								loadAllMeldung();
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
								"Meldungsfehler: " + error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Uebergabe der Notenparameter an url
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", method);
				params.put("id", String.valueOf(id));
				return params;
			}
		};
		// Anfrage in Requestqueue einf�gen
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}
	
}
