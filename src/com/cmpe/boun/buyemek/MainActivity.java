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


public class MainActivity extends ActionBarActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	FileDownloadTask mFileDownloadTask;
	DataHandler db = null;
	ArrayList<Meal> foodList = new ArrayList<Meal>();
	ArrayList<String> newMealEntries = new ArrayList<String>();
	final static String[] namesOfDays = { "Pazartesi", "Salı",
		"Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar" };
	final static String[] namesOfMonths = { "Ocak", "Şubat", "Mart",
		"Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim",
		"Kasım", "Aralık" };
	final static String READ_PATH = "https://dl.dropboxusercontent.com/u/64468378/yemek_listesi_update_checker.py";
	TextView text = null;


	@SuppressLint("NewApi") protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		mFileDownloadTask = new FileDownloadTask();
		db = new DataHandler(getApplicationContext());
		

		if (isNetworkAvailable()) {
			if (db.isDatabaseUpToDate()) {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				foodList = db.getThisMonthsMeals();
			}
			else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				try {
					newMealEntries = mFileDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null).get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; i < newMealEntries.size(); i++) {
					String s = newMealEntries.get(i);
					Log.d("Food:", s);
					foodList.add(new Meal(s));
				}
				db.addFoods(foodList);
				foodList = db.getThisMonthsMeals();
				Log.d("databasefoodListSize", foodList.size() + " ");
			}
			/*
			else {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				try {
					newMealEntries = mFileDownloadTask.execute((Void[])null).get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; i < newMealEntries.size(); i++) {
					String s = newMealEntries.get(i);
					Log.d("Food:", s);
					foodList.add(new Meal(s));
				}
				db.addFoods(foodList);
				foodList = db.getThisMonthsMeals();
				Log.d("databasefoodListSize", foodList.size() + " ");

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
				Toast.makeText(MainActivity.this, "Yemek listesinin güncellenmesi için internete bağlanmanız gereklidir.",
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

		static final String ARG_SECTION_NUMBER = "section_number";

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
			TextView fullDateTextView = (TextView) rootView
					.findViewById(R.id.full_date);
			TextView dayNameTextView = (TextView) rootView
					.findViewById(R.id.day_name);
			TextView lunchTitleTextView = (TextView) rootView
					.findViewById(R.id.lunch_title);
			TextView dinnerTitleTextView = (TextView) rootView
					.findViewById(R.id.dinner_title);
			TextView lunchFirstMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_first_meal);
			TextView lunchSecondMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_second_meal);
			TextView lunchThirdMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_third_meal);
			TextView lunchFourthMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_fourth_meal);
			TextView dinnerFirstMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_first_meal);
			TextView dinnerSecondMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_second_meal);
			TextView dinnerThirdMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_third_meal);
			TextView dinnerFourthMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_fourth_meal);

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
			lunchTitleTextView.setText("Öğle Yemeği");
			dinnerTitleTextView.setText("Akşam Yemeği");

			fullDateTextView.setTextColor(Color.BLUE);
			dayNameTextView.setTextColor(Color.BLUE);
			lunchTitleTextView.setTextColor(Color.BLUE);
			dinnerTitleTextView.setTextColor(Color.BLUE);

			// find correct meal
			for (int i = 0; i < foodList.size(); i++) {
				Meal meal = foodList.get(i);
				if (meal.day == currentDay
						&& meal.month.toLowerCase()
						.contentEquals(currentMonth.toLowerCase())) {
					lunchFirstMealTextView.setText(meal.first_meal);
					lunchSecondMealTextView.setText(meal.second_meal);
					lunchThirdMealTextView.setText(meal.third_meal);
					lunchFourthMealTextView.setText(meal.fourth_meal);

					if(foodList.size()>=i+1)
					{
						if (meal.day == currentDay
								&& meal.month.toLowerCase()
								.contentEquals(currentMonth.toLowerCase())) {
							meal = foodList.get(i + 1);
							dinnerFirstMealTextView.setText(meal.first_meal);
							dinnerSecondMealTextView.setText(meal.second_meal);
							dinnerThirdMealTextView.setText(meal.third_meal);
							dinnerFourthMealTextView.setText(meal.fourth_meal);
						}
					}
					break;
				}
			}

			return rootView;
		}
	}

	boolean isNetworkAvailable() {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
/*
	public ArrayList<Meal> processFoodListString() {
		ArrayList<Meal> myList = new ArrayList<Meal>();
		for (int i = 0; i < newMealEntries.size(); i++) {
			String s = newMealEntries.get(i);
			System.out.println(new Meal(s).toString());
		}
		return myList;
	}
*/
	class FileDownloadTask extends AsyncTask<Void, Void, ArrayList<String>> {
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
					// list.add(s);
					Log.d("Line:",s);
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




