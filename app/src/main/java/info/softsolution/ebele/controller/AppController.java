package info.softsolution.ebele.controller;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import info.softsolution.ebele.R;


/*
 * Kontrollerklasse 
 *  
 * @author Duclos Ndanji
 */
public class AppController extends Application 
{

	private static final String TAG = AppController.class.getSimpleName();

	public static final String VIBRATE_PREF = "vibrate_pref";
	public static final String RINGTONE_PREF = "ringtone_pref";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	
	
	public static SharedPreferences prefs;
	private RequestQueue mRequestQueue;
	
//	private ImageLoader mImageLoader;
//	LruBitmapCache mLruBitmapCache;
	
	private static AppController mInstance;

	@Override
	public void onCreate() 
	{
		super.onCreate();
		
		PreferenceManager.setDefaultValues(this, R.xml.settings , false);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mInstance = this;
		
	}

	
	public static synchronized AppController getInstance() 
	{
		return mInstance;
	}

	public RequestQueue getRequestQueue() 
	{
		if (mRequestQueue == null) 
		{
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}
	
//	public ImageLoader getImageLoader()
//	{
//		getRequestQueue();
//		if(mImageLoader == null)
//		{
//			getLruBitmapCache();
//			mImageLoader = new ImageLoader(this.mRequestQueue, (ImageCache) mLruBitmapCache);
//		}
//		return this.mImageLoader;
//	}
	
//	public LruBitmapCache getLruBitmapCache()
//	{
//		if(mLruBitmapCache == null)
//		{
//			mLruBitmapCache = new LruBitmapCache();
//		}
//		return this.mLruBitmapCache;
//	}
	
	public <T> void addToRequestQueue(Request<T> req, String tag) 
	{
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) 
	{
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) 
	{
		if (mRequestQueue != null) 
		{
			mRequestQueue.cancelAll(tag);
		}
	}
	
	public static boolean isNotify() 
	{
		return prefs.getBoolean("notifications_new_message", true);
	}
	
	
	public static boolean isVibrate()
	{
		return prefs.getBoolean(VIBRATE_PREF, true);
	}
	
	public static String getRingtone()
	{
		return prefs.getString(RINGTONE_PREF, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString());
	}
}