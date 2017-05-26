package info.softsolution.ebele.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import info.softsolution.ebele.R;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.helper.BitmapScaler;
import info.softsolution.ebele.helper.DeviceDimensionsHelper;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;

public class InfoErfassenFragment extends Fragment implements OnClickListener {
	private static final String TAG = InfoErfassenFragment.class.getSimpleName();
	private static final int AUSLAENDERIN = 0;
	private static final int KLINIK = 1;
	private static final int SCHWANGERSCHAFT = 2;
	private static final int STUDENTIN = 3;
	private static final int REQUEST_CAMERA = 0;
	private static final int SELECT_FILE = 1;
	private int _infotyp;
	private ImageButton imgKlinik;
	private ImageButton imgStudentin;
	private ImageButton imgAuslaenderin;
	private ImageButton imgSchwangerschaft;

	private Button btnBildauswaehlen;
	private Button btnSpeichern;
	private Button btnAbbrechen;
	private ImageView imageView;
	private Bitmap bitmap;
	private EditText txtTitel;
	private EditText txtBeschreibung;
	private EditText txtLink;
	private EditText txtStrasse;
	private EditText txtHausnr;
	private EditText txtPlz;
	private EditText txtStadt;
	private Spinner spinnerTyp;
	private String email;
	private SessionManager session;

	public InfoErfassenFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_info_erfassen,
				container, false);
		session = new SessionManager(getActivity());
		email = session.getEmail();
		imgStudentin = (ImageButton) rootView.findViewById(R.id.studentin);
		imgSchwangerschaft = (ImageButton) rootView
				.findViewById(R.id.schwangerschaftsverlauf);
		imgAuslaenderin = (ImageButton) rootView
				.findViewById(R.id.auslaenderin);
		imgKlinik = (ImageButton) rootView.findViewById(R.id.klinik);

		imgStudentin.setOnClickListener(this);
		imgSchwangerschaft.setOnClickListener(this);
		imgKlinik.setOnClickListener(this);
		imgAuslaenderin.setOnClickListener(this);
		return rootView;
	}

	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.auslaenderin:
			showDialog(AUSLAENDERIN);
			break;
		case R.id.studentin:
			showDialog(STUDENTIN);
			break;
		case R.id.schwangerschaftsverlauf:
			showDialog(SCHWANGERSCHAFT);
			break;
		case R.id.klinik:
			showDialog(KLINIK);
			break;
		}
	}

	private void showDialog(int infoTyp) {

		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_info_erfassen);
		dialog.setTitle("Informationerfassung");
        set_infotyp(infoTyp); 

		imageView = (ImageView) dialog.findViewById(R.id.bild);
		txtTitel = (EditText) dialog.findViewById(R.id.titel);
		txtBeschreibung = (EditText) dialog.findViewById(R.id.beschreibung);
		txtLink = (EditText) dialog.findViewById(R.id.link);
		txtStrasse = (EditText) dialog.findViewById(R.id.strasse);
		txtStadt = (EditText) dialog.findViewById(R.id.stadt);
		txtPlz = (EditText) dialog.findViewById(R.id.plz);
		txtHausnr = (EditText) dialog.findViewById(R.id.hausnummer);
        
		btnBildauswaehlen = (Button) dialog.findViewById(R.id.btn_bild);
		btnBildauswaehlen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
			}
		});

		spinnerTyp = (Spinner) dialog.findViewById(R.id.typ_spinner);
		ArrayAdapter<CharSequence> typAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.info_typ,
						android.R.layout.simple_spinner_dropdown_item);
		spinnerTyp.setAdapter(typAdapter);
        
		spinnerTyp.setSelection(get_infotyp());
		spinnerTyp.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					String item = spinnerTyp.getItemAtPosition(position).toString();
					if (!item.isEmpty() && !item.equals("Klinik")) {
						txtTitel.setHint("Titel");
						txtStrasse.setVisibility(View.GONE);
						txtStadt.setVisibility(View.GONE);
						txtPlz.setVisibility(View.GONE);
						txtHausnr.setVisibility(View.GONE);
					} else {
						txtTitel.setHint("Klinikname");
						txtStrasse.setVisibility(View.VISIBLE);
						txtStadt.setVisibility(View.VISIBLE);
						txtPlz.setVisibility(View.VISIBLE);
						txtHausnr.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		 
		btnSpeichern = (Button) dialog.findViewById(R.id.btnSpeichern);
		btnSpeichern.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{

				final String typ = spinnerTyp.getSelectedItem().toString().toUpperCase(Locale.GERMAN);
				final String titel = txtTitel.getText().toString().trim();
				final String beschreibung = txtBeschreibung.getText().toString().trim();
				final String link = txtLink.getText().toString().trim();
				final String strasse = txtStrasse.getText().toString().trim();
				final String stadt = txtStadt.getText().toString().trim();
				final String plz = txtPlz.getText().toString().trim();
				final String hausnr = txtHausnr.getText().toString().trim();

				String _link ="";
				String _strasse ="";
				String _stadt ="";
				String _hausnr ="";
				String _plz ="";
				if(link.isEmpty())
				{
					_link = "NULL";
				}
				else
				{
					_link = link;
				}
				
				if(strasse.isEmpty())
				{
					_strasse = "NULL";
				}
				else
				{
					_strasse = strasse;
				}

				if(stadt.isEmpty())
				{
					_stadt = "NULL";
				}
				else
				{
					_stadt = stadt;
				}

				if(plz.isEmpty())
				{
					_plz = "0";
				}
				else
				{
					_plz = plz;
				}

				if(hausnr.isEmpty())
				{
					_hausnr = "0";
				}
				else
				{
					_hausnr = hausnr;
				}
				
				if (!(TextUtils.isEmpty(txtTitel.getText()) && TextUtils
						.isEmpty(txtBeschreibung.getText()))) {
					if (spinnerTyp.getSelectedItem() != null) {
						String image = "NULL";
						if(bitmap != null)
						{
							image = getStringImage(bitmap);
						}
						//TODO DB-Anbindung
						/*
						executeRequest(Utils.METHOD.create.toString(),titel,
									   beschreibung, email, image, typ, _link, 
									   _strasse, _hausnr, _plz, _stadt);
						*/
						dialog.dismiss();
					} else {
						Utils.showToast(getActivity(),
								"Typ darf nicht leer sein!");
					}
				} else {
					Utils.showToast(getActivity(),
							"Bitte geben Sie den Titel und/oder die Beschreibung ein!");
				}
			}
		});
		dialog.show();

		btnAbbrechen = (Button) dialog.findViewById(R.id.btnAbbrechen);
		btnAbbrechen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private String getStringImage(Bitmap bitmap)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imagesBytes = baos.toByteArray();
		String encodedImage = Base64.encodeToString(imagesBytes, Base64.DEFAULT);
		return encodedImage;
	}
	
	private void executeRequest(final String method, final String titel, final String beschreibung,
			final String email_adresse, final String image, final String typ,
			final String link, final String strasse,
			final String hausnr, final String plz, final String stadt) {
		// Tag um Anfrage zu aborten
		String tag_string_req = "req_benutzer_melden";
		final ProgressDialog pdialog = ProgressDialog.show(getActivity(), "Speicherung...", "Bitte warten");
		
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_INFORMATION, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Informationsspeicherungsantwort: "
								+ response.toString());
						pdialog.dismiss();
						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
								Utils.showToast(getActivity(),
										"Vielen Dank f�r Ihren Beitrag, die Information wurde erfolgreich publiziert!");
//								Utils.clearFelder(listEditText);
//								txtWann.setText(sdf.format(calendar.getTime()));

							} else {
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
						pdialog.dismiss();
						Log.e(TAG,
								"Informationsspeicherungsfehler: "
										+ error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", method);
				params.put("titel", titel);
				params.put("beschreibung",beschreibung);
				params.put("email_adresse", email_adresse);
				params.put("typ", typ);
				params.put("image", image);
				params.put("link",link);
				params.put("strasse",strasse);
				params.put("hausnr",hausnr);
				params.put("plz",plz);
				params.put("stadt",stadt);
				
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void selectImage() {
		final CharSequence[] items = { "Bild aufnehmen",
				"Aus der Galerie ausw�hlen", "Abbrechen" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Bild ausw�hlen!");
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				String itemSelected = items[item].toString();
				if (itemSelected.equals("Bild aufnehmen")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (itemSelected.equals("Aus der Galerie ausw�hlen")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Bild ausw�hlen"),
							SELECT_FILE);
				} else if (itemSelected.equals("Abbrechen")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_FILE) {
				onSelectFromGalleryResult(data);
			} else if (requestCode == REQUEST_CAMERA) {
				onCaptureImageResult(data);
			}
		}
	}

	private void onCaptureImageResult(Intent data) {
		bitmap = (Bitmap) data.getExtras().get("data");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

		File destination = new File(Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".jpg");

		FileOutputStream fo;
		try {
			destination.createNewFile();
			fo = new FileOutputStream(destination);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}

		int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
		bitmap = BitmapScaler.scaleToFitWidth(bitmap, screenWidth);
		imageView.setImageBitmap(bitmap);
	}

	private void onSelectFromGalleryResult(Intent data) {
		Uri selectedImageUri = data.getData();
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = getActivity().getContentResolver().query(
				selectedImageUri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		String selectedImagePath = cursor.getString(column_index);


		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(selectedImagePath, options);
		final int REQUIRED_SIZE = 200;
		int scale = 1;
		while (options.outWidth / scale / 2 >= REQUIRED_SIZE
				&& options.outHeight / scale / 2 >= REQUIRED_SIZE) {
			scale *= 2;
		}

		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
		int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
		bitmap = BitmapScaler.scaleToFitWidth(bitmap, screenWidth);
		imageView.setImageBitmap(bitmap);
	}

	public int get_infotyp() {
		return _infotyp;
	}

	public void set_infotyp(int _infotyp) {
		this._infotyp = _infotyp;
	}

}
