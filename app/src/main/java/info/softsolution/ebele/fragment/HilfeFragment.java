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
public class HilfeFragment extends Fragment{

	
	public HilfeFragment(){
	}	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
	{
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);

        return rootView;
	}
}
