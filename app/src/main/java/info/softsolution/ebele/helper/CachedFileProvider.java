package info.softsolution.ebele.helper;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class CachedFileProvider extends ContentProvider
{
	private static final String CLASS_NAME = "CachedFileProvider";
	
	//Der Authority ist der symbolische Name von der Providerklasse
	public static final String AUTHORITY = "de.e_schwangerschaft.helper.provider";
	
	//UriMatcher use to match against incoming request
	private UriMatcher uriMatcher;
	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		//Add a URI to the matcher which will match against the form
		//'content://de.e_schwangerschaft.helper.provider/*'
		//and return 1 in the case that the incoming Uri matches this pattern
		uriMatcher.addURI(AUTHORITY, "*", 1);
		
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException
	{
		String LOG_TAG = CLASS_NAME + " - openfile";
		Log.v(LOG_TAG, "Aufgerufen mit uri: '" + uri + "'." + uri.getLastPathSegment());
		
		//Check incoming Uri against the matcher
		switch(uriMatcher.match(uri))
		{
			//If it return 1 - then it matches the Uri defined in onCreate
			case 1:
				//The desired file name is specified by the last segment of the path
				String fileLocation = getContext().getCacheDir() + File.separator + uri.getLastPathSegment();
				
				//Create and rturn a ParcelFileDescriptor pointing to the file
				ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(fileLocation), 
										   ParcelFileDescriptor.MODE_READ_ONLY);
				return pfd;
		    
			default:
				Log.v(LOG_TAG, "Nicht unterst�tzte uri: '" + uri + "'.");
				throw new FileNotFoundException("Nicht unterst�tzte uri: " + uri.toString());
		}
	}

}
