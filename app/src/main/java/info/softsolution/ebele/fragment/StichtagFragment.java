package info.softsolution.ebele.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;

public class StichtagFragment extends Fragment {
	private static final int MEIN_STICHTAG = 0;
	private static final int DATE_DIALOG_ID_MEIN_STICHTAG = 1;
	private static final int ZYKLUS_LAENGE28 = 28;
	private static final String TAG = StichtagFragment.class.getSimpleName();
	private SessionManager session;

	private int mYear;
	private int mMonth;
	private int mDay;
	private Button btnSpeichern;
	private Button btnBerechnen;
	private EditText txtStichtag;
	private EditText txtMerker;
	private Calendar calendar;
	private String email_adresse;
	private OnDateSetListener mDateSetListenerblutung;

	public StichtagFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stichtag_berechnen,
				container, false);
//		Bundle bundle = getArguments();
		session = new SessionManager(getContext());
//		setEmail_adresse(bundle.getString("email"));
//		String stichtag = bundle.getString("stichtag");
		setEmail_adresse(session.getEmail());
		String stichtag = session.getStichtag();
		
		findViews(rootView);

		if(!stichtag.isEmpty())
		{
			txtStichtag.setText(stichtag);
		}

		return rootView;
	}

	protected Dialog showDialog(int i) {
		switch (i) {

		case MEIN_STICHTAG:
			new DatePickerDialog(getContext(), mDateSetListener, mYear, mMonth,
					mDay).show();
			return null;
		case DATE_DIALOG_ID_MEIN_STICHTAG:
			new DatePickerDialog(getContext(), mDateSetListenerblutung, mYear,
					mMonth, mDay).show();
			return null;
		}
		return null;
	}

	private OnDateSetListener mDateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear + 1;
			mDay = dayOfMonth;
			txtStichtag.setText(new StringBuilder().append(mDay).append("/")
					.append(mMonth).append("/").append(mYear));
		}
	};

	private void findViews(View rootView) {
		txtStichtag = (EditText) rootView.findViewById(R.id.datum_stichtag);
		txtMerker = (EditText) rootView.findViewById(R.id.datum_merker);
		btnSpeichern = (Button) rootView
				.findViewById(R.id.btn_stichtag_speichern);
		btnBerechnen = (Button) rootView
				.findViewById(R.id.btn_stichtag_berechnen);
		calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",
				Locale.GERMANY);

		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);

		txtStichtag.setText(sdf.format(calendar.getTime()));

		txtStichtag.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(MEIN_STICHTAG);
			}
		});

		btnBerechnen.setOnClickListener(new View.OnClickListener() {
			int merker = 0;
			int _Year, _Monat, _Tag;
			int zyklus = 0;

			@Override
			public void onClick(View v) {
				LayoutInflater li = LayoutInflater.from(getActivity());
				View promtsView = li.inflate(R.layout.stichtag_dialog, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setView(promtsView);

				final EditText datumLetzteBlutungstag = (EditText) promtsView
						.findViewById(R.id.letzteblutungstag);
				final EditText zyklusLaenge = (EditText) promtsView
						.findViewById(R.id.txtzykluslaenge);
				zyklusLaenge.setText("28");
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",
						Locale.GERMANY);
				datumLetzteBlutungstag.setText(sdf.format(calendar.getTime()));

				datumLetzteBlutungstag
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								showDialog(DATE_DIALOG_ID_MEIN_STICHTAG);
							}
						});

				mDateSetListenerblutung = new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						_Year = year;
						_Monat = monthOfYear + 1;
						_Tag = dayOfMonth;

						datumLetzteBlutungstag.setText(new StringBuilder()
								.append(dayOfMonth).append("/")
								.append(monthOfYear + 1).append("/")
								.append(year));

					}
				};

				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										int zyklus = Integer
												.valueOf(zyklusLaenge.getText()
														.toString());
										merker = pruefeEingaben(_Year, _Monat,
												_Tag, zyklus);
										// txtStichtag.setText(datumLetzteBlutungstag.getText());

										switch (merker) {
										case 0:
											// Toast.makeText(getActivity(),
											// "Korrigieren Sie das eingegebene Datum bitte",
											// Toast.LENGTH_LONG).show();
											break;
										case 1:
											berechneStichtag(_Year, _Monat,
													_Tag, ZYKLUS_LAENGE28);
											break;
										case 2:
											berechneStichtag(_Year, _Monat,
													_Tag, zyklus);

										}
									}

								})
						.setNegativeButton("Abbrechen",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});

		btnSpeichern.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String stichtag = txtStichtag.getText().toString().trim();
				if (!stichtag.isEmpty()) {
					executeRequest(stichtag, email_adresse, Utils.METHOD.updateStichtag.toString());

				}
			}
		});
	}

	private void executeRequest(final String stichtag,
			final String _email_adresse, final String method) {
		// Tag um Anfrage zu aborten
		String tag_string_req = "req_updateStichtag";
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_REGISTER, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Stichtagsspeicherungsantwort: "
								+ response.toString());

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) 
							{
								session.removeKey(session.KEY_STICHTAG);
								session.addKeyString(session.KEY_STICHTAG, stichtag);
								Utils.showToast(getActivity(),
										"Ihren Stichtag wurde erfolgreich gespeichert!");
							}
							else 
							{
								// Fehler beim Speichern
								String errorMsg = jObj.getString("error_msg");
								Utils.showToast(getActivity(), errorMsg);
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
								"Stichtagsspeicherungsfehler: "
										+ error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				// params.put("wann", wann);
				params.put("email_adresse", _email_adresse);
				params.put("method", method);
				params.put("stichtag", stichtag);
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private int pruefeEingaben(int _Year, int _Monat, int _Day, int zyklus) {
		int merker = 0;
		if ((_Year == calendar.get(Calendar.YEAR))
				|| (_Year == calendar.get(Calendar.YEAR) - 1)) {
			if (_Year == calendar.get(Calendar.YEAR) - 1) {
				if (_Monat - 4 >= calendar.get(Calendar.MONTH)) {
					if (zyklus <= 35 && zyklus >= 21) {
						if (zyklus == 28) {
							merker = 1;
							// berechneStichtag28(year, monthOfYear,
							// dayOfMonth);
						} else {
							merker = 2;
							// zyklus =
							// Integer.valueOf(zyklusLaenge.getText().toString());
							// berechneStichtagOder(year, monthOfYear,
							// dayOfMonth);
						}
					} else {
						Toast.makeText(getActivity(),
								"Korrigieren Sie den Zyklus bitte",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getActivity(),
							"Korrigieren Sie das eingegebene Datum bitte",
							Toast.LENGTH_LONG).show();
				}
			} else if ((_Monat < calendar.get(Calendar.MONTH))
					|| (_Monat > (calendar.get(Calendar.MONTH) - 9))) {
				if (zyklus <= 35 && zyklus >= 21) {
					if (zyklus == 28) {
						merker = 1;
						// berechneStichtag28(year, monthOfYear, dayOfMonth);
					} else {
						merker = 2;
						// berechneStichtagOder(year, monthOfYear, dayOfMonth);
					}
				} else {
					Toast.makeText(getActivity(),
							"Korrigieren Sie den Zyklus bitte",
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(getActivity(),
						"Korrigieren Sie den Monat bitte!", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			Toast.makeText(getActivity(), "Korrigieren Sie das Jahr bitte!",
					Toast.LENGTH_LONG).show();
		}
		return merker;

	}

	/*
	 * Berechne Stichtag mit Tagezyklus abweichend von 28 Formel 7 tag - 3
	 * Monate + 1 Jahr +/- abweichende Tage
	 */
	private void berechneStichtag(int year, int month, int day, int zyklus) {
		int delta = 0;
		delta = zyklus - 28;

		day = 7 + day + delta;
		if (month == 2) {
			if (day > 28) {
				month += 1;
				day = day - 28;
			}
		} else if ((month % 2 == 1 && month <= 7)
				|| ((month % 2 == 0) && (month >= 8))) {
			if (day > 31) {
				month += 1;
				day -= 31;
			}

		} else {
			if (day > 30) {
				month += 1;
				day -= 30;
			}
		}

		month += 9;
		if (month > 12) {
			month = month % 12;
			year += 1;
		}
		Toast.makeText(
				getActivity(),
				new StringBuilder().append("Ihr wahrscheinlichster Entbindungstag ist der: ").append(day).append("/").append(month)
						.append("/").append(year), Toast.LENGTH_LONG).show();
		txtStichtag.setText(new StringBuilder().append(day).append("/")
				.append(month).append("/").append(year));

	}

	public String getEmail_adresse() {
		return email_adresse;
	}

	public void setEmail_adresse(String email_adresse) {
		this.email_adresse = email_adresse;
	}
}
