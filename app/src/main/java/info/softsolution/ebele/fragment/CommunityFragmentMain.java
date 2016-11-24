package info.softsolution.ebele.fragment;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.softsolution.ebele.R;
import info.softsolution.ebele.helper.TabListener;

public class CommunityFragmentMain extends Fragment
{
	//LogCat tag
	private static final String TAG = CommunityFragmentMain.class.getSimpleName();
	
	//Userdaten
	private String name;
	private String email;
	
	ActionBar.Tab tabChat, tabArchiv;
	android.app.Fragment fragmentChat = new CommunityFragment();
	android.app.Fragment fragmentArchiv = new CommunityArchivFragment();
	
	public CommunityFragmentMain(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
	{
        Bundle bundle = getArguments();
        name = bundle.getString("name");
        email = bundle.getString("email");
 
        fragmentChat.setArguments(bundle);
        fragmentArchiv.setArguments(bundle);
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
         
		ActionBar actionBar = getActivity().getActionBar();
		// Hide Actionbar Icon
		actionBar.setDisplayShowHomeEnabled(true);
 
		// Hide Actionbar Title
		actionBar.setDisplayShowTitleEnabled(true);
 
		// Create Actionbar Tabs
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
		// Set Tab Icon and Titles
		tabChat = actionBar.newTab().setIcon(R.drawable.chat_live);
		tabChat.setText("Communities");

		tabArchiv = actionBar.newTab().setText("Nachrichtenarchiv");
		tabArchiv.setIcon(R.drawable.archiv);
		// Set Tab Listeners
		tabChat.setTabListener(new TabListener(fragmentChat));
		tabArchiv.setTabListener(new TabListener(fragmentArchiv));
 
		// Add tabs to actionbar
		actionBar.addTab(tabChat);
		actionBar.addTab(tabArchiv);
		
        return rootView;

    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}