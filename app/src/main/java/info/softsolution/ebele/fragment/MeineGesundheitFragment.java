package info.softsolution.ebele.fragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.softsolution.ebele.R;
import info.softsolution.ebele.helper.TabListener;

public class MeineGesundheitFragment extends Fragment {
	
	public MeineGesundheitFragment(){}
	
	private Tab tabBlutdruck;
	private Tab tabBlutdruckMessung;
	private Tab tabBlutdruckMessungTable;	
	
	private	android.app.Fragment fragmentBlutdruck = new BlutdruckErfassungFragment();
	private	android.app.Fragment fragmentBlutdruckMessung = new BlutdruckGraphikFragment();
	private	android.app.Fragment fragmentBlutdruckTable = new BlutdruckTableFragment();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
    	Bundle bundle = getArguments();
    	
    	fragmentBlutdruck.setArguments(bundle);
    	fragmentBlutdruckTable.setArguments(bundle);
    	fragmentBlutdruckMessung.setArguments(bundle);
    	
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
         
		ActionBar actionBar = getActivity().getActionBar();
		 
		// Hide Actionbar Icon
		actionBar.setDisplayShowHomeEnabled(true);
 
		// Hide Actionbar Title
		actionBar.setDisplayShowTitleEnabled(true);
 
		// Create Actionbar Tabs
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
		// Set Tab Icon and Titles
		tabBlutdruck = actionBar.newTab().setIcon(R.drawable.meine_gesundheit);
		tabBlutdruck.setText("Blutdruck erfassen");
		
		tabBlutdruckMessung = actionBar.newTab().setText("Blutdruck Control");
		tabBlutdruckMessung.setIcon(R.drawable.statistik);
		tabBlutdruckMessungTable = actionBar.newTab().setText("Blutdrucktabelle");
		tabBlutdruckMessungTable.setIcon(R.drawable.table);
		// Set Tab Listeners
		tabBlutdruck.setTabListener(new TabListener(fragmentBlutdruck));
		tabBlutdruckMessung.setTabListener(new TabListener(fragmentBlutdruckMessung));
		tabBlutdruckMessungTable.setTabListener(new TabListener(fragmentBlutdruckTable));
		
		actionBar.addTab(tabBlutdruck);
		actionBar.addTab(tabBlutdruckMessung);
		actionBar.addTab(tabBlutdruckMessungTable);
		
        return rootView;
    }	
}
