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

public class InfosFragment extends Fragment {
	

	public InfosFragment(){}

	private Tab tabSchwangerschaftVerlauf;
	private Tab tabKlinikInfo;
	private Tab tabStudentin;
	private Tab tabAuslaenderin;
	private Tab tabInfoErfassen;
	
	private	android.app.Fragment fragmentSchwangerschaftVerlauf = new SchwangerschaftsverlaufFragment();
	private	android.app.Fragment fragmentKlinikInfo = new KlinikFragment();
	private	android.app.Fragment fragmentInfoErfassen = new InfoErfassenFragment();
	private	android.app.Fragment fragmentStudentin = new StudentinFragment();
	private	android.app.Fragment fragmentAuslaenderin = new AuslaenderinFragment();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
         
		ActionBar actionBar = getActivity().getActionBar();
		 
		// Hide Actionbar Icon
		//actionBar.setDisplayShowHomeEnabled(true);
 
		// Hide Actionbar Title
		//actionBar.setDisplayShowTitleEnabled(true);
 
		// Create Actionbar Tabs
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
		// Set Tab Icon and Titles
		tabSchwangerschaftVerlauf = actionBar.newTab().setIcon(R.drawable.schwangerschaftsverlauf);
		tabSchwangerschaftVerlauf.setText("SchwangerschaftVerlauf");

		tabKlinikInfo = actionBar.newTab().setText("Infos �ber Kliniken");
		tabKlinikInfo.setIcon(R.drawable.klinik);

		tabStudentin = actionBar.newTab().setText("Stundentin");
		tabStudentin.setIcon(R.drawable.studentin);

		tabInfoErfassen = actionBar.newTab().setText("Info erfassen");
		tabInfoErfassen.setIcon(R.drawable.info_erfassen);
		
		tabAuslaenderin = actionBar.newTab().setText("Ausl�nderin");
		tabAuslaenderin.setIcon(R.drawable.auslaender);
		// Set Tab Listeners
		tabSchwangerschaftVerlauf.setTabListener(new TabListener(fragmentSchwangerschaftVerlauf));
		tabKlinikInfo.setTabListener(new TabListener(fragmentKlinikInfo));
		tabStudentin.setTabListener(new TabListener(fragmentStudentin));
		tabAuslaenderin.setTabListener(new TabListener(fragmentAuslaenderin));
		tabInfoErfassen.setTabListener(new TabListener(fragmentInfoErfassen));
		actionBar.addTab(tabSchwangerschaftVerlauf);
		actionBar.addTab(tabKlinikInfo);
		actionBar.addTab(tabStudentin);
		actionBar.addTab(tabAuslaenderin);
		actionBar.addTab(tabInfoErfassen);
		
        return rootView;
    }	

}
