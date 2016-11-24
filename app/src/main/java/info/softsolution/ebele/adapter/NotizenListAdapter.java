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
import info.softsolution.ebele.model.Note;

public class NotizenListAdapter  extends BaseAdapter
{

	Context context;
	List<Note> noteItems;
	
	
	public NotizenListAdapter(Context context, List<Note> noteItems) {
		super();
		this.context = context;
		this.noteItems = noteItems;
	}

	@Override
	public int getCount() 
	{
		return noteItems.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return noteItems.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Note note = noteItems.get(position);
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		convertView = mInflater.inflate(R.layout.row_note, null);
	
//		ImageView icon = (ImageView) convertView.findViewById(R.id.note_icon);
		TextView txtid = (TextView) convertView.findViewById(R.id.note_id);
		TextView titel = (TextView) convertView.findViewById(R.id.note_titel);
		TextView created_at = (TextView) convertView.findViewById(R.id.note_created_at);
	
		txtid.setText(String.valueOf(note.getId()));
		titel.setText(note.getTitel());
		created_at.setText(note.getCreated_at());
		
		return convertView;
	}
	
}
