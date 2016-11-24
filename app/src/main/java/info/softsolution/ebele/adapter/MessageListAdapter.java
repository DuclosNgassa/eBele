package info.softsolution.ebele.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import info.softsolution.ebele.R;
import info.softsolution.ebele.model.Nachricht;

public class MessageListAdapter extends BaseAdapter
{
	private Context context;
	private List<Nachricht> messagesItems;
	
	public MessageListAdapter(Context context, List<Nachricht> messagesItems) {
		super();
		this.context = context;
		this.messagesItems = messagesItems;
	}
	
	@Override
	public int getCount()
	{
		return messagesItems.size();
	}
	
	@Override
	public Object getItem(int position)
	{
		return messagesItems.get(position);
	}
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Nachricht m = messagesItems.get(position);
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		if(messagesItems.get(position).isSelf())
		{
			convertView = mInflater.inflate(R.layout.list_item_message_right, null);
		}
		else
		{
			convertView = mInflater.inflate(R.layout.list_item_message_left, null);
		}
		
		TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
		TextView txtMsg  = (TextView) convertView.findViewById(R.id.txtMsg);
		TextView lblUhrzeit = (TextView) convertView.findViewById(R.id.lblUhrZeit);
		
		txtMsg.setText(m.getText());
		lblFrom.setText(m.getFromName());
		lblUhrzeit.setText(m.getCreated_at());
		
		return convertView;
	}
	
}
