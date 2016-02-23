package com.cmpe.boun.buyemek;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpe.boun.buyemek.R;
import com.parse.Parse;
import com.parse.ParseObject;

public class MainActivity extends ActionBarActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	private FileDownloadTask mFileDownloadTask = null;
	DataHandler db = null;
	private ArrayList<Meal> foodList = new ArrayList<Meal>();
	private ArrayList<String> foodListUncomplete = new ArrayList<String>();
	final private static String[] namesOfDays = { "Pazartesi", "Sal�",
		"�ar�amba", "Per�embe", "Cuma", "Cumartesi", "Pazar" };
	final private static String[] namesOfMonths = { "Ocak", "�ubat", "Mart",
		"Nisan", "May�s", "Haziran", "Temmuz", "A�ustos", "Eyl�l", "Ekim",
		"Kas�m", "Aral�k" };
	final private static String READ_PATH = "http://kyzn.org/buyemek/list.txt";
	TextView text = null;


	@SuppressLint("NewApi") protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		mFileDownloadTask = new FileDownloadTask();
		db = new DataHandler(getApplicationContext());

		Parse.enableLocalDatastore(this);
		Parse.initialize(this, "gRVas33KrZc7btfsX0TISveWFrP6DanMR5zzH5UC", "7szMV0d2rnpg1qEKZdGNlZdhaLR4L1om0Kar1rxp");
		
		ParseObject testObject = new ParseObject("AppOpened");
		testObject.put("moment", " this");
		testObject.saveEventually();
		
		

		if (isNetworkAvailable()) {
			if (db.isDatabaseUpToDate()) {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				foodList = db.getThisMonthsMeals();
			}
			else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				try {
					foodListUncomplete = mFileDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null).get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; i < foodListUncomplete.size(); i++) {
					String s = foodListUncomplete.get(i);
					Log.d("Food:", s);
					foodList.add(new Meal(s));
				}
				db.addFoods(foodList);
				foodList = db.getThisMonthsMeals();
				Log.d("databasefoodListSize", foodList.size() + " ");
			}
			else {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				try {
					foodListUncomplete = mFileDownloadTask.execute((Void[])null).get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; i < foodListUncomplete.size(); i++) {
					String s = foodListUncomplete.get(i);
					Log.d("Food:", s);
					foodList.add(new Meal(s));
				}
				db.addFoods(foodList);
				foodList = db.getThisMonthsMeals();
				Log.d("databasefoodListSize", foodList.size() + " ");

			}
			/*
			for (int i = 0; i < foodList.size(); i++) {
				Meal m = foodList.get(i);
				Log.d("Food(" + m.getDay() + "/10):", m.getSecondMeal());
			}
			*/
		} 
		else {
			if (db.isDatabaseUpToDate()) {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				foodList = db.getThisMonthsMeals();
			}
			else {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				foodList = db.getThisMonthsMeals();
				Toast.makeText(MainActivity.this, "Yemek listesinin g�ncellenmesi i�in internete ba�lanman�z gereklidir.",
						Toast.LENGTH_LONG).show();
			}
		}

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}
	}

	public static class PlaceholderFragment extends Fragment {

		private static final String ARG_SECTION_NUMBER = "section_number";

		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			Button fbButton = (Button) rootView.findViewById(R.id.facebook_button);
			fbButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Uri uriUrl = Uri.parse("https://www.facebook.com/BounYemek");
					Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
					startActivity(launchBrowser);
				}
			});

			Button twitterButton = (Button) rootView.findViewById(R.id.twitter_button);
			twitterButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Uri uriUrl = Uri.parse("https://twitter.com/buYemek");
					Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
					startActivity(launchBrowser);
				}
			});
			/*
			ArrayList<String> colors = new ArrayList<String>();
			colors.add("#FF3399");
			colors.add("#339933");
			colors.add("#A37547");
			colors.add("#FFA319");
			colors.add("#FF0000");
			colors.add("#BF00FF");
			colors.add("#DF3A01");
			colors.add("#2EFE64");
			colors.add("#990099");
			colors.add("#0000FF");
			colors.add("#FF85AD");
			colors.add("#FF6600");
			Collections.shuffle(colors);
			 */
			TextView fullDateTextView = (TextView) rootView
					.findViewById(R.id.full_date);
			//			fullDateTextView.setTextColor(Color.parseColor(colors.get(0)));
			TextView dayNameTextView = (TextView) rootView
					.findViewById(R.id.day_name);
			//			dayNameTextView.setTextColor(Color.parseColor(colors.get(1)));
			TextView lunchTitleTextView = (TextView) rootView
					.findViewById(R.id.lunch_title);
			//			lunchTitleTextView.setTextColor(Color.parseColor(colors.get(2)));
			TextView dinnerTitleTextView = (TextView) rootView
					.findViewById(R.id.dinner_title);
			//			dinnerTitleTextView.setTextColor(Color.parseColor(colors.get(3)));
			TextView lunchFirstMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_first_meal);
			//			lunchFirstMealTextView.setTextColor(Color.parseColor(colors.get(4)));
			TextView lunchSecondMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_second_meal);
			//			lunchSecondMealTextView.setTextColor(Color.parseColor(colors.get(5)));
			TextView lunchThirdMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_third_meal);
			//			lunchThirdMealTextView.setTextColor(Color.parseColor(colors.get(6)));
			TextView lunchFourthMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_fourth_meal);
			//			lunchFourthMealTextView.setTextColor(Color.parseColor(colors.get(7)));
			TextView dinnerFirstMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_first_meal);
			//			dinnerFirstMealTextView.setTextColor(Color.parseColor(colors.get(8)));
			TextView dinnerSecondMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_second_meal);
			//			dinnerSecondMealTextView.setTextColor(Color.parseColor(colors.get(9)));
			TextView dinnerThirdMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_third_meal);
			//			dinnerThirdMealTextView.setTextColor(Color.parseColor(colors.get(10)));
			TextView dinnerFourthMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_fourth_meal);
			//			fullDateTextView.setTextColor(Color.parseColor(colors.get(11)));

			// prepare date info
			Date date = new Date();
			date = new Date(date.getTime() + (1000 * 60 * 60 * 24)
					* (getArguments().getInt(ARG_SECTION_NUMBER) - 1));
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
					+ getArguments().getInt(ARG_SECTION_NUMBER) - 2;
			dayOfWeek = dayOfWeek % 7;
			if (dayOfWeek == 0)
				dayOfWeek = 7;

			ArrayList<Meal> foodList = ((MainActivity) getActivity()).foodList;

			int currentDay = Integer.parseInt(sdf.format(date).substring(0, 2));
			String currentMonth = namesOfMonths[Math.max(Integer.parseInt(sdf.format(
					date).substring(3, 5)) - 1,0)];

			fullDateTextView.setText(sdf.format(date));
			dayNameTextView.setText(namesOfDays[dayOfWeek - 1]);
			lunchTitleTextView.setText("��le Yeme�i");
			dinnerTitleTextView.setText("Ak�am Yeme�i");

			fullDateTextView.setTextColor(Color.BLUE);
			dayNameTextView.setTextColor(Color.BLUE);
			lunchTitleTextView.setTextColor(Color.BLUE);
			dinnerTitleTextView.setTextColor(Color.BLUE);

			// find correct meal
			for (int i = 0; i < foodList.size(); i++) {
				Meal meal = foodList.get(i);
				if (meal.getDay() == currentDay
						&& meal.getMonth().toLowerCase()
						.contentEquals(currentMonth.toLowerCase())) {
					lunchFirstMealTextView.setText(meal.getFirstMeal());
					lunchSecondMealTextView.setText(meal.getSecondMeal());
					lunchThirdMealTextView.setText(meal.getThirdMeal());
					lunchFourthMealTextView.setText(meal.getFourthMeal());

					if(foodList.size()>=i+1)
					{
						if (meal.getDay() == currentDay
								&& meal.getMonth().toLowerCase()
								.contentEquals(currentMonth.toLowerCase())) {
							meal = foodList.get(i + 1);
							dinnerFirstMealTextView.setText(meal.getFirstMeal());
							dinnerSecondMealTextView.setText(meal.getSecondMeal());
							dinnerThirdMealTextView.setText(meal.getThirdMeal());
							dinnerFourthMealTextView.setText(meal.getFourthMeal());
						}
					}
					break;
				}
			}

			return rootView;
		}
	}

	private boolean isNetworkAvailable() {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public ArrayList<Meal> processFoodListString() {
		ArrayList<Meal> myList = new ArrayList<Meal>();
		for (int i = 0; i < foodListUncomplete.size(); i++) {
			String s = foodListUncomplete.get(i);
			System.out.println(new Meal(s).toString());
		}
		return myList;
	}

	private class FileDownloadTask extends AsyncTask<Void, Void, ArrayList<String>> {
		ArrayList<String> list = new ArrayList<String>();

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(READ_PATH);
			try {
				HttpResponse execute = client.execute(httpGet);
				InputStream content = execute.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					list.add(s);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPreExecute(){
		}
		@Override
		protected void onPostExecute(ArrayList<String> response){
		}
		@Override  
		protected void onCancelled() {  
		}  
	}

}




