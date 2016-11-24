package info.softsolution.ebele.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.softsolution.ebele.R;

/*
 * Klasse f�r das Anzeigen der Informatioen �ber die App
 * 
 * @author Duclos Ndanji
 */
public class HomeFragment1 extends Fragment{

	
	public HomeFragment1(){
	}	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
	{
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
	}
}
