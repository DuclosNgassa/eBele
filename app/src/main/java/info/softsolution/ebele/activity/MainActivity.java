package info.softsolution.ebele.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import info.softsolution.ebele.R;
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
import info.softsolution.ebele.fragment.VerhaltenMeldenFragment;
import info.softsolution.ebele.helper.SessionManager;
import info.softsolution.ebele.helper.Utils;
import info.softsolution.ebele.model.User;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DateUtility dateUtility;
    private Date parsedDate = new Date();
    // urls to load navigation header background image
    // and profile image

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_HOME = "home";
    private static final String TAG_AGENDA = "agenda";
    private static final String TAG_NEWS = "news";
    private static final String TAG_COMMUNAUTE = "communaute";
    private static final String TAG_JOURNAL = "journal";
    private static final String TAG_SANTE = "sante";
    private static final String TAG_ACCOUCHEMENT = "accouchement";
    private static final String TAG_ESPACE_SHOPPING = "espace_shopping";
    private static final String TAG_SIGNALER = "signaler";
    private static final String TAG_FEEDBACK = "feedback";
    private static final String TAG_SETTING = "setting";
    private static final String TAG_HELP = "help";
    //    private static final String TAG_LOGOUT = "logout";
//    public static enum from {NOTIZEN, TAGEBUCH};
    private SessionManager session;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private String name;
    private String email;
    private String uid;
    private String stichtag;


    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        setName(intent.getStringExtra("name"));
        setEmail(intent.getStringExtra("email"));
        setStichtag(intent.getStringExtra("stichtag"));
        setUid(intent.getStringExtra("uid"));
        dateUtility = new DateUtility();
//        String _from = intent.getStringExtra("from");

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText("Duclos Ngassa");
        // txtWebsite.setText("www.softsolutions.cm");

        // loading header background image
        /*Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
*/
        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        Fragment fragment = null;
        switch (navItemIndex) {
            case 0:
                // home
                fragment = new HomeFragment();
                break;
            case 1:
                // photos
                fragment = new TermineFragment();
                break;
            case 2:
                // movies fragment
                fragment = new InfosFragment();
                break;
            case 3:
                // notifications fragment
                fragment = new CommunityFragmentMain();
                break;

            case 4:
                // settings fragment
                fragment = new TagebuchMainFragment();
                break;
            case 5:
                // photos
                fragment = new MeineGesundheitFragment();
                break;
            case 6:
                // movies fragment
                fragment = new StichtagFragment();
                break;
            case 7:
                // notifications fragment
                fragment = new VerhaltenMeldenFragment();
                break;
            case 8:
                // notifications fragment
                fragment = new VerhaltenMeldenFragment();
                break;

            case 9:
                // settings fragment
                fragment = new FeedbackFragment();
                break;
            case 10:
                // settings fragment
                fragment = new EinstellungenFragment();
                break;
            case 11:
                // settings fragment
                fragment = new HilfeFragment();
                break;
            default:
                fragment = new HomeFragment();
        }
        if (fragment != null) {
/*            if(position != 1 && position != 3)
            {
                getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
  */
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("email", email);
            bundle.putString("stichtag", stichtag);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

        return fragment;
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_agenda:
//                        navItemIndex = 1;
                        //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));

                        Intent intent = new Intent();
                        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                        builder.appendPath("time");
                        if (dateUtility.getBeginTime() > 0) {
                            ContentUris.appendId(builder, dateUtility.getBeginTime());
                        } else {
                            ContentUris.appendId(builder, parsedDate.getTime());
                        }
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(builder.build());
                        startActivity(intent);
                        drawer.closeDrawers();
                        return true;
//                        CURRENT_TAG = TAG_AGENDA;
                    //                      break;
                    case R.id.nav_news:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_NEWS;
                        break;
                    case R.id.nav_communaute:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_COMMUNAUTE;
                        break;
                    case R.id.nav_journal:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_JOURNAL;
                        break;
                    case R.id.nav_sante:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_SANTE;
                        break;
                    case R.id.nav_accouchement:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_ACCOUCHEMENT;
                        break;
                    case R.id.nav_espace_shopping:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_ESPACE_SHOPPING;
                        break;
                    case R.id.nav_signaler:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_SIGNALER;
                        break;
                    case R.id.nav_feedback:
                        navItemIndex = 9;
                        CURRENT_TAG = TAG_FEEDBACK;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 10;
                        CURRENT_TAG = TAG_SETTING;
                        break;
                    case R.id.nav_help:
                        navItemIndex = 11;
                        CURRENT_TAG = TAG_HELP;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_logout:
                        // launch new intent instead of loading fragment
                        logoutUser();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    private void logoutUser() {
        changeUserStatus(Utils.METHOD.updateUserStatus.toString(), session.getEmail(), User.userStatus.OFFLINE.toString());
    }

    private void changeUserStatus(final String method, final String email, final String status) {
        //Tag um Anfrage zu aborten
        String tag_string_req = "req_userStatus_setzen";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_MANAGE_USER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "BenutzerStatusAenderungsantwort: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                Utils.showToast(getApplicationContext(), "Ihre Status wurde erfolgreich ge�ndert!");
                                session.removeAll();
//						db.deleteUsers();
                                //anmeldungactivity ausfuehren
                                Intent intent = new Intent(MainActivity.this, AnmeldungActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //Fehler beim Speichern
                                String errorMsg = jObj.getString("error_msg");
                                Utils.showToast(getApplicationContext(), errorMsg);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage().toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Abmeldungsfehler: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("method", method);
                params.put("status", status);
                params.put("email_adresse", email);
                return params;
            }
        };
        //TODO
        //   AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private class DateUtility {
        private long beginTime;
        private long endTime;

        public DateUtility() {
            beginTime = 0;
            endTime = 0;
        }

        public DateUtility(long beginTime, long endTime) {
            super();
            this.beginTime = beginTime;
            this.endTime = endTime;
        }

        public long getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(long beginTime) {
            this.beginTime = beginTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

    }

}
