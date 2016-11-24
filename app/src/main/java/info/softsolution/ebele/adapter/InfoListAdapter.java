package info.softsolution.ebele.adapter;

import java.text.ParseException;
import java.util.List;



import info.softsolution.ebele.R;
import info.softsolution.ebele.helper.BitmapScaler;
import info.softsolution.ebele.helper.DeviceDimensionsHelper;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.Information;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoListAdapter extends BaseAdapter
{
	private Context context;
	private LayoutInflater inflater;
	private List<Information> infoItems;
//	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public InfoListAdapter(Context context, List<Information> feedItems) {
		super();
		this.context = context;
		this.infoItems = feedItems;
	}
	
	@Override
	public int getCount()
	{
		return infoItems.size();
	}
	
	@Override
	public Object getItem(int location)
	{
		return infoItems.get(location);
	}
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(inflater == null)
		{
			inflater = (LayoutInflater)context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.info_item, null);
		}
//		if(imageLoader == null)
//		{
//			imageLoader = AppController.getInstance().getImageLoader();
//		}
		
		TextView titel = (TextView)convertView.findViewById(R.id.titel);
		TextView timestamp = (TextView)convertView.findViewById(R.id.timestamp);
		TextView beschreibung = (TextView)convertView.findViewById(R.id.txtBeschreibung);
		TextView adresse = (TextView)convertView.findViewById(R.id.txtAdresse);
		TextView link = (TextView)convertView.findViewById(R.id.txtLink);
		ImageView image = (ImageView)convertView.findViewById(R.id.image);
		
		Information item = infoItems.get(position);
		
		titel.setText(item.getTitel());
		
		CharSequence timeAgo = "";
		try {
			timeAgo = DateUtils.getRelativeTimeSpanString(
					Utils.convertTimeToMillis(item.getTimeStamp()),
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timestamp.setText(timeAgo);
		
		//Checking for empty beschreibung
		if(!TextUtils.isEmpty(item.getBeschreibung()))
		{
			beschreibung.setText(item.getBeschreibung());
			beschreibung.setVisibility(View.VISIBLE);
		}
		else
		{
			//beschreibung is empty remove from the view
			beschreibung.setVisibility(View.GONE);
		}

		//Checking for empty Adresse
		if(!TextUtils.isEmpty(item.getAdresse()) && item.getTyp() != null && item.getTyp().equals(Information.infoTyp.KLINIK.toString()))
		{
			adresse.setText(item.getAdresse());
			adresse.setVisibility(View.VISIBLE);
		}
		else
		{
			//adresse is empty remove from the view
			adresse.setVisibility(View.GONE);
		}
		
		//Checking for null feed link
		if(item.getLink() != null && !item.getLink().equalsIgnoreCase("null"))
		{
			link.setText(Html.fromHtml("<a href=\"" + item.getLink() + "\">"
					+ item.getLink() + "</a>"));
			//Making url clickable
			link.setMovementMethod(LinkMovementMethod.getInstance());
			link.setVisibility(View.VISIBLE);
		}
		else
		{
			//Url is null, remove it from the view
			link.setVisibility(View.GONE);
		}
		
		//Feed image
		if(item.getImage() != null)
		{
			byte[] decodedString = Base64.decode(item.getImage(), Base64.DEFAULT);
			Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 

			int screenWidth = DeviceDimensionsHelper.getDisplayWidth(context);
			bm = BitmapScaler.scaleToFitWidth(bm, screenWidth);
			image.setImageBitmap(bm);
			image.setVisibility(View.VISIBLE);
		}
		else
		{
			image.setVisibility(View.GONE);
		}
		
		return convertView;
	}

}
