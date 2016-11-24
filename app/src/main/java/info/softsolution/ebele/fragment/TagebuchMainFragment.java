package info.softsolution.ebele.fragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.softsolution.ebele.R;
import info.softsolution.ebele.fragment.NotizenFragment;
import info.softsolution.ebele.fragment.TagebuchFragment;
import info.softsolution.ebele.helper.TabListener;

public class TagebuchMainFragment extends Fragment
{

		public TagebuchMainFragment(){}
		
		private Tab tabNotizen;
		private Tab tabTagebuch;
		
		private	android.app.Fragment fragmentNotizen = new NotizenFragment();
		private	android.app.Fragment fragmentTagebuch = new TagebuchFragment();

	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	    {
	    	Bundle bundle = getArguments();
	    	fragmentNotizen.setArguments(bundle);
	    	fragmentTagebuch.setArguments(bundle);
	    	
	        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
	         
			ActionBar actionBar = getActivity().getActionBar();
			 
			// Hide Actionbar Icon
			actionBar.setDisplayShowHomeEnabled(true);
	 
			// Hide Actionbar Title
			actionBar.setDisplayShowTitleEnabled(true);
	 
			// Create Actionbar Tabs
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	 
			// Set Tab Icon and Titles
			tabNotizen = actionBar.newTab().setIcon(R.drawable.notizen);
			tabNotizen.setText("Notizen");
			
			tabTagebuch = actionBar.newTab().setText("Tagebuch");
			tabTagebuch.setIcon(R.drawable.tagebuch);
			
			// Set Tab Listeners
			tabNotizen.setTabListener(new TabListener(fragmentNotizen));
			tabTagebuch.setTabListener(new TabListener(fragmentTagebuch));
			
			actionBar.addTab(tabNotizen);
			actionBar.addTab(tabTagebuch);
	
			return rootView;
	    }	
}
