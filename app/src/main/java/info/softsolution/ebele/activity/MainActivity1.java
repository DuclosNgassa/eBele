package info.softsolution.ebele.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import info.softsolution.ebele.R;
import info.softsolution.ebele.adapter.NavDrawerListAdapter;
import info.softsolution.ebele.controller.AppConfig;
import info.softsolution.ebele.controller.AppController;
import info.softsolution.ebele.fragment.CommunityFragmentMain;
import info.softsolution.ebele.fragment.EinstellungenFragment;
import info.softsolution.ebele.fragment.FeedbackFragment;
import info.softsolution.ebele.fragment.HilfeFragment;
import info.softsolution.ebele.fragment.HomeFragment;
import info.softsolution.ebele.fragment.InfosFragment;
import info.softsolution.ebele.fragment.MeineGesundheitFragment;
import info.softsolution.ebele.fragment.StichtagFragment;
import info.softsolution.ebele.fragment.TagebuchMainFragment;
import info.softsolution.ebele.fragment.TermineFragment;
import info.softsolution.ebele.fragment.UeberUnsFragment;
import info.softsolution.ebele.fragment.VerhaltenMeldenFragment;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.NavDrawerItem;
import info.softsolution.ebele.model.User;

public class MainActivity1 extends FragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private static String TAG = MainActivity1.class.getSimpleName();
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	public static enum from {NOTIZEN, TAGEBUCH};
    private SessionManager session;
  
    private String name ;
    private String email;
    private String uid ;
    private String stichtag;
    
//    private Context context = getApplicationContext();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		setName(intent.getStringExtra("name"));
		setEmail(intent.getStringExtra("email"));
		setStichtag(intent.getStringExtra("stichtag"));
        setUid(intent.getStringExtra("uid"));
//        String _from = intent.getStringExtra("from");
     
        // session manager
        session = new SessionManager(getApplicationContext());
 
        if (!session.isLoggedIn()) {
            logoutUser();
        }
  
		mTitle = mDrawerTitle = getTitle();

		findViews();

		// load slide menu items
		initMenu();

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}

	}

private void findViews() {
	navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

	// nav drawer icons from resources
	navMenuIcons = getResources()
			.obtainTypedArray(R.array.nav_drawer_icons);

	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
}

private void initMenu() 
{
/*	navDrawerItems = new ArrayList<NavDrawerItem>();

	// adding nav drawer items to array
	// Home
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
	// Infos
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
	// Termine
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
	// Communities
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
	// Tagebuch
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
	// Meine Gesundheit
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
	// Stichtag
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
	// Benutzerin melden
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
	// Feedback
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
	// Einstellungen
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
	// Ueber uns
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(10, -1)));
	// Hilfe
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], navMenuIcons.getResourceId(11, -1)));
	//Abmelden
	navDrawerItems.add(new NavDrawerItem(navMenuTitles[12], navMenuIcons.getResourceId(12, -1)));
	
	// Recycle the typed array
	navMenuIcons.recycle();

	mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

	// setting the nav drawer list adapter
	adapter = new NavDrawerListAdapter(getApplicationContext(),
			navDrawerItems);
	mDrawerList.setAdapter(adapter);

	// enabling action bar app icon and behaving it as toggle button
	getActionBar().setDisplayHomeAsUpEnabled(true);
	getActionBar().setHomeButtonEnabled(true);

	
	mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
			R.drawable.ic_drawer, //nav menu toggle icon
			R.string.app_name, // nav drawer open - description for accessibility
			R.string.app_name // nav drawer close - description for accessibility
	) {
		public void onDrawerClosed(View view) {
			getActionBar().setTitle(mTitle);
			// calling onPrepareOptionsMenu() to show action bar icons
			invalidateOptionsMenu();
		}

		public void onDrawerOpened(View drawerView) {
			getActionBar().setTitle(mDrawerTitle);
			// calling onPrepareOptionsMenu() to hide action bar icons
			invalidateOptionsMenu();
		}
	};
	mDrawerLayout.setDrawerListener(mDrawerToggle);
*/
}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		//case R.id.action_settings:
			//return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		//menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new InfosFragment();
			break;
		case 2:
			fragment = new TermineFragment();
			break;
		case 3:
			fragment = new CommunityFragmentMain();
			break;
		case 4:
			fragment = new TagebuchMainFragment();
			break;
		case 5:
			fragment = new MeineGesundheitFragment();
			break;
		case 6:
     		fragment = new StichtagFragment();
			break;
		case 7:
     		fragment = new VerhaltenMeldenFragment();
            break;
		case 8:
     		fragment = new FeedbackFragment();
            break;
		case 9:
			fragment = new EinstellungenFragment();
            break;
		case 10:
     		fragment = new UeberUnsFragment();
            break;
		case 11:
     		fragment = new HilfeFragment();
            break;
		case 12:
            logoutUser();
            break;
		default:
			break;
		}

		if (fragment != null) {
			if(position != 1 && position != 3)
			{
				getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			}
			Bundle bundle = new Bundle();
			bundle.putString("name", name);
			bundle.putString("email", email);
			bundle.putString("stichtag", stichtag);
			
			getActionBar().removeAllTabs();
			fragment.setArguments(bundle);
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	private void logoutUser()
	{
		changeUserStatus(Utils.METHOD.updateUserStatus.toString(), session.getEmail(), User.userStatus.OFFLINE.toString());
	}

	private void changeUserStatus(final String method, final String email, final String status) {
		//Tag um Anfrage zu aborten
		String tag_string_req = "req_userStatus_setzen";
		StringRequest strReq = new StringRequest(Method.POST,
												 AppConfig.URL_MANAGE_USER,
												 new Response.Listener<String>() 
		{

			@Override
			public void onResponse(String response) 
			{
				Log.d(TAG, "BenutzerStatusAenderungsantwort: "+ response.toString());
				
				try
				{
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if(!error)
					{
						Utils.showToast(getApplicationContext(), "Ihre Status wurde erfolgreich ge�ndert!");
						session.removeAll();
//						db.deleteUsers();
						//anmeldungactivity ausfuehren
						Intent intent = new Intent(MainActivity1.this,AnmeldungActivity.class);
						startActivity(intent);
						finish();
					}
					else
					{
						//Fehler beim Speichern
						String errorMsg = jObj.getString("error_msg");
						Utils.showToast(getApplicationContext(), errorMsg);
					}
				}
				catch(JSONException e)
				{
					Log.e(TAG, e.getMessage().toString());
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) 
			{
				Log.e(TAG, "Abmeldungsfehler: " + error.getMessage());
			}
		}){
			@Override
			protected Map<String, String> getParams()
			{
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", method);
				params.put("status", status);
				params.put("email_adresse", email);
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

	public void setEmail(String passwort) {
		this.email = passwort;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getStichtag() {
		return stichtag;
	}

	public void setStichtag(String stichtag) {
		this.stichtag = stichtag;
	}
}
