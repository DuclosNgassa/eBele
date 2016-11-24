package info.softsolution.ebele.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import info.softsolution.ebele.R;
import info.softsolution.ebele.model.Note;

public class TagebuchListAdapter extends BaseExpandableListAdapter{
	
	private Context context;
	private List<String> listDataHeader;
	private Map<String, List<Note>> listDataChild;
	private ExpandableListView expandableListView;
	private int[] groupStatus;
	public TagebuchListAdapter(Context context, List<String> listDataHeader,
			Map<String, List<Note>> listDataChild, ExpandableListView expandableListView) {
		super();
		this.context = context;
		this.listDataHeader = listDataHeader;
		this.listDataChild = listDataChild;
		this.expandableListView = expandableListView;
		groupStatus = new int[listDataHeader.size()];
		setListEvent();
	}
	
	private void setListEvent()
	{
		expandableListView
		.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				groupStatus[groupPosition] = 1;
			}
		});
		
		expandableListView
		.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
				groupStatus[groupPosition] = 0;
			}
		});		
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		return this.listDataChild.get(this.listDataHeader.get(groupPosition))
				.get(childPosition);
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}
	
	@Override 
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		final Note child = (Note) getChild(groupPosition, childPosition);
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater)this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item, null);
		}
		TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
		
		txtListChild.setText(child.getTitel()+ "   "+ child.getUpdated_at().substring(0, 7));
		
		return convertView;
	}
	
	@Override
	public int getChildrenCount(int groupPosition)
	{
		return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
	}
	
	@Override
	public Object getGroup(int groupPosition)
	{
		return this.listDataHeader.get(groupPosition);
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		String headerTitle = (String)getGroup(groupPosition);
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater)this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_group, null);
		}
		TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
		TextView lblKinderZahl = (TextView) convertView.findViewById(R.id.lblKinderZahl);
		String anzahl = "(" + getChildrenCount(groupPosition) +")";
		lblKinderZahl.setText(anzahl);
		ImageView tagImgHeader = (ImageView) convertView.findViewById(R.id.tag_img); 
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);
		
		if(groupStatus[groupPosition] == 0)
		{
			tagImgHeader.setImageResource(R.drawable.group_down);
		}
		else
		{
			tagImgHeader.setImageResource(R.drawable.group_up);			
		}
		
		return convertView;
	}
	
	@Override
	public boolean hasStableIds()
	{
		return true;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int ChildPosition)
	{
		return true;
	}

	@Override
	public int getGroupCount() {
		return this.listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
}
