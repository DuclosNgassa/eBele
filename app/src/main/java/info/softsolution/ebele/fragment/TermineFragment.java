package info.softsolution.ebele.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import info.softsolution.ebele.R;

public class TermineFragment extends Fragment implements OnClickListener
{

	private Button btnShowTermin;
	private DateUtility dateUtility;

	private TextView currentMonth;
	private Button selectedDayMonthYearButton;
	private Button btnAddTermin;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Date parsedDate = new Date();
	private Calendar _calendar;
	@SuppressLint("NewApi")
	private int month, year;
	@SuppressWarnings("unused")
	@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";
	
	private static final String TAG = TermineFragment.class.getSimpleName();
	
	public TermineFragment(){}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_termin, container, false);
                
        dateUtility = new DateUtility();
        
        btnAddTermin = (Button) rootView.findViewById(R.id.addEvent);
        btnShowTermin = (Button) rootView.findViewById(R.id.settings);
//
        btnAddTermin.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				intent.setType("vnd.android.cursor.item/event");
		
				if( dateUtility.getBeginTime() > 0)
				{
					intent.putExtra("beginTime", dateUtility.getBeginTime());
					intent.putExtra("endTime", dateUtility.getEndTime());
				}
				else
				{
					intent.putExtra("beginTime", parsedDate.getTime());
					intent.putExtra("endTime", parsedDate.getTime() + DateUtils.HOUR_IN_MILLIS);
				}
				intent.setAction(Intent.ACTION_EDIT);
				startActivity(intent);
			}
		});

        btnShowTermin.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent();

				Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
				builder.appendPath("time");
				if( dateUtility.getBeginTime() > 0)
				{
					ContentUris.appendId(builder, dateUtility.getBeginTime());
				}
				else
				{
					ContentUris.appendId(builder, parsedDate.getTime());
				}
				
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(builder.build());
				startActivity(intent);
			}
		});

        initCalender(rootView);

        return rootView;
    }
    
    private void initCalender(View rootview)
    {

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) +1 ;
        year  = _calendar.get(Calendar.YEAR);
        Log.d(TAG,"Kalender := " +"Monat: " + month + " " + "Jahr: " + year );
        
        selectedDayMonthYearButton = (Button) rootview.findViewById(R.id.selectedDayMonthYear);
        selectedDayMonthYearButton.setText("Selektiert: ");

        prevMonth = (ImageView) rootview.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);
        
        currentMonth = (TextView) rootview.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        
        nextMonth = (ImageView) rootview.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);
        
        calendarView = (GridView) rootview.findViewById(R.id.calendar);
        
        //Initialisierung
		adapter = new GridCellAdapter(getActivity(),
				R.id.calendar_day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
    	
    	
    }

	/**
	 * 
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(getActivity(),
				R.id.calendar_day_gridcell, month, year);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			if (month <= 1) {
				month = 12;
				year--;
			} else {
				month--;
			}
			Log.d(TAG, "Setting Prev Month in GridCellAdapter: " + "Month: "
					+ month + " Year: " + year);
			setGridCellAdapterToDate(month, year);
		}
		if (v == nextMonth) {
			if (month > 11) {
				month = 1;
				year++;
			} else {
				month++;
			}
			Log.d(TAG, "Setting Next Month in GridCellAdapter: " + "Month: "
					+ month + " Year: " + year);
			setGridCellAdapterToDate(month, year);
		}

	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Destroying View ...");
		super.onDestroy();
	}
	

    private class DateUtility
    {
    	private long beginTime;
    	private long endTime;
		
    	public DateUtility()
    	{
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

	public DateUtility getDateUtility() {
		return dateUtility;
	}

	public void setDateUtility(DateUtility dateUtility) {
		this.dateUtility = dateUtility;
	}
	
	//Innere Adapter Klasse
	public class GridCellAdapter extends BaseAdapter implements OnClickListener
	{
		private final String TAG = GridCellAdapter.class.getSimpleName();
		private final Context _context;
		
		private final List<String> list;
		private final int DAY_OFFSET = 1;
		private final String[] weekdays = {"Son","Mon","Die","Mit","Don","Fre","Sam"};
		private final String[] months = {"Januar","Februar","M�rz","April","Mai","Juni",
													 "Juli","August","September","Oktober","November","Dezember"};

		private final int[] daysOfMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private TextView num_events_per_day;
		private final HashMap<String, Integer> eventsPerMonthMap;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
		
		//Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId,
								int month, int year)
		{
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			Log.d(TAG, "==> Passed in Date for Month: " + month + " " + "Jahr: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			Log.d(TAG, "New Calendar:= " + calendar.getTime().toString());
			Log.d(TAG, "CurrentDayOfWeek :" + getCurrentWeekDay());
			Log.d(TAG, "CurrentDayOfMonth :" + getCurrentDayOfMonth());
			// Print Month
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}
		
		private String getMonthAsString(int i)
		{
			return months[i];
		}
		
		private String getWeekDayAsString(int i)
		{
			return weekdays[i];
		}
		
		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Zeigt Monat an
		 * @param mm, int Monat
		 * @param yy, int Jahr
		 */
		private void printMonth(int mm, int yy)
		{
			Log.d(TAG, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;
			
			int currentMonth = mm - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);
			
			Log.d(TAG, "Current Month: " + " " + currentMonthName + " having "
					+ daysInMonth + " days.");
			
			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			Log.d(TAG, "Gregorian Calender:= " + cal.getTime().toString());
			
			switch(currentMonth)
			{
				case 11:
						prevMonth = currentMonth - 1;
						daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
						nextMonth = 0;
						prevYear = yy;
						nextYear = yy + 1;
						Log.d(TAG, "*->PrevYear: " + prevYear + " PrevMonth:"
								+ prevMonth + " NextMonth: " + nextMonth
								+ " NextYear: " + nextYear);
						break;
				
				case 0:
					   prevMonth = 11;
					   prevYear = yy - 1;
					   nextYear = yy;
					   daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
					   nextMonth = 1;
   					   Log.d(TAG, "**--> PrevYear: " + prevYear + " PrevMonth:"
								+ prevMonth + " NextMonth: " + nextMonth
								+ " NextYear: " + nextYear);
   					   break;
   			    
				default:
					   prevMonth = currentMonth - 1;
					   nextMonth = currentMonth + 1;
					   nextYear = yy;
					   prevYear = yy;
					   daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
					   Log.d(TAG, "***---> PrevYear: " + prevYear + " PrevMonth:"
								+ prevMonth + " NextMonth: " + nextMonth
								+ " NextYear: " + nextYear);
					   break;
			}

			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			Log.d(TAG, "Week Day:" + currentWeekDay + " is "
					+ getWeekDayAsString(currentWeekDay));
			Log.d(TAG, "No. Trailing space to Add: " + trailingSpaces);
			Log.d(TAG, "No. of Days in Previous Month: " + daysInPrevMonth);

			if (cal.isLeapYear(cal.get(Calendar.YEAR)))
				if (mm == 2)
					++daysInMonth;
				else if (mm == 3)
					++daysInPrevMonth;

			// Trailing Month days
			for(int i = 0; i < trailingSpaces; i++)
			{
				Log.d(TAG, "PREV MONTH:= "
						+ prevMonth
						+ " => "
						+ getMonthAsString(prevMonth)
						+ " "
						+ String.valueOf((daysInPrevMonth
								- trailingSpaces + DAY_OFFSET)
								+ i));
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY"
						+ "-"
						+ getMonthAsString(prevMonth)
						+ "-"
						+ prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				Log.d(currentMonthName, String.valueOf(i) + " "
						+ getMonthAsString(currentMonth) + " " + yy);
				if (i == getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-BLUE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				Log.d(TAG, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ getMonthAsString(nextMonth) + "-" + nextYear);
			}
			
		}
		
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/**
		 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
		 * ALL entries from a SQLite database for that month. Iterate over the
		 * List of All entries, and get the dateCreated, which is converted into
		 * day.
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
				int month) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();

			return map;
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View row = convertView;
			if(row == null)
			{
				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.screen_grid_cell, parent, false);
			}
			
			//Get a reference to the day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);
			
			//Account for spacing 
			Log.d(TAG, "Current Day: " + getCurrentDayOfMonth());
			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];
			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				if (eventsPerMonthMap.containsKey(theday)) {
					num_events_per_day = (TextView) row
							.findViewById(R.id.num_events_per_day);
					Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
					num_events_per_day.setText(numEvents.toString());
				}
			}

			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);
			Log.d(TAG, "Setting GridCell " + theday + "-" + themonth + "-"
					+ theyear);

			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(getResources()
						.getColor(R.color.lightgray));
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(getResources().getColor(
						R.color.lightgray02));
			}
			if (day_color[1].equals("BLUE")) {
				gridcell.setTextColor(getResources().getColor(R.color.orrange));
			}
			return row;
	
		}

		@Override
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();
			selectedDayMonthYearButton.setText("Selektiert: " + date_month_year);
			Log.e("Selected date", date_month_year);
			try 
			{
				parsedDate = dateFormatter.parse(date_month_year);
				Log.d(TAG, "Parsed Date: " + parsedDate.toString());

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
		
	}
}
