package info.softsolution.ebele.adapter;


import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import info.softsolution.ebele.R;
import info.softsolution.ebele.model.User;

public class UserListAdapter  extends BaseAdapter
{

	Context context;
	List<User> userItems;
	
	
	public UserListAdapter(Context context, List<User> userItems) {
		super();
		this.context = context;
		this.userItems = userItems;
	}

	@Override
	public int getCount() 
	{
		return userItems.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return userItems.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		User user = userItems.get(position);
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		convertView = mInflater.inflate(R.layout.row_user, null);
	
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView email = (TextView) convertView.findViewById(R.id.email);
		TextView status = (TextView) convertView.findViewById(R.id.status);
//		TextView anmeldeDatum = (TextView) convertView.findViewById(R.id.anmeldedatum);
//		ImageButton iVsperren = (ImageButton) convertView.findViewById(R.id.sperren);
//		ImageButton iVentsperren = (ImageButton) convertView.findViewById(R.id.entsperren);
//		ImageButton iVloeschen = (ImageButton) convertView.findViewById(R.id.loeschen);
		
		name.setText(user.getName());
		email.setText(user.getUser_email());
		status.setText(user.getStatus().toLowerCase(Locale.GERMANY));
//		anmeldeDatum.setText(user.getCreated_at());
		
		return convertView;
	}
	
}
