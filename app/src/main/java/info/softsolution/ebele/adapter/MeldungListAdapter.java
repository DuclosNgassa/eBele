package info.softsolution.ebele.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import info.softsolution.ebele.R;
import info.softsolution.ebele.model.Meldung;

public class MeldungListAdapter  extends BaseAdapter
{

	Context context;
	List<Meldung> meldungItems;
	
	
	public MeldungListAdapter(Context context, List<Meldung> meldungItems) {
		super();
		this.context = context;
		this.meldungItems = meldungItems;
	}

	@Override
	public int getCount() 
	{
		return meldungItems.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return meldungItems.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Meldung meldung = meldungItems.get(position);
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		convertView = mInflater.inflate(R.layout.row_meldung, null);
	
		TextView txtWer = (TextView) convertView.findViewById(R.id.txtwer);
		TextView txtWann = (TextView) convertView.findViewById(R.id.txtwann);
		TextView txtWas = (TextView) convertView.findViewById(R.id.was);
//		TextView anmeldeDatum = (TextView) convertView.findViewById(R.id.anmeldedatum);
//		ImageButton iVsperren = (ImageButton) convertView.findViewById(R.id.sperren);
//		ImageButton iVentsperren = (ImageButton) convertView.findViewById(R.id.entsperren);
//		ImageButton iVloeschen = (ImageButton) convertView.findViewById(R.id.loeschen);
		
		txtWer.setText(meldung.getWer());
		txtWann.setText(meldung.getWann().subSequence(0, 10));
		txtWas.setText(meldung.getWas());
//		anmeldeDatum.setText(user.getCreated_at());
		
		return convertView;
	}
	
}
