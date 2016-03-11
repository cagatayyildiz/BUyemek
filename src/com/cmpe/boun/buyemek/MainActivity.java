package com.cmpe.boun.buyemek;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	FileDownloadTask mFileDownloadTask;
	DataHandler db = null;
	ArrayList<Meal> foodList = new ArrayList<Meal>();
	ArrayList<String> newMealLines = new ArrayList<String>();
	final static String[] namesOfDays = { "Pazartesi", "Salı",
		"Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar" };
	final static String[] namesOfMonths = { "Ocak", "Şubat", "Mart",
		"Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim",
		"Kasım", "Aralık" };
	final static String READ_PATH = "https://dl.dropboxusercontent.com/u/64468378/BUyemek/yemek_listesi.txt";
    final static String meal1Title = "Öğle Yemeği";
    final static String meal2Title = "Akşam Yemeği";

	final static String[] BUTTON_LABELS = {"Tmm!","Tşk!","Okkk!","Anladım!","Orrayt!","Deal!"};



	@SuppressLint("NewApi") protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mFileDownloadTask = new FileDownloadTask();
		db = new DataHandler(getApplicationContext());


		InitAlarms.setAlarms(getApplicationContext());

		if (isNetworkAvailable()) {
			if (db.isDatabaseUpToDate()) {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				foodList = db.getThisMonthsMeals();

			}
			else {
				Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
				try {
                    newMealLines = mFileDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null).get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; i < newMealLines.size(); i++) {
					String s = newMealLines.get(i);
					Log.d("Food:", s);
					foodList.add(new Meal(s));
				}
				db.insertMeals(foodList);
                foodList = db.getThisMonthsMeals();
				Log.d("databasefoodListSize", foodList.size() + " ");
			}
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
		// http://stackoverflow.com/questions/13026141/nullpointerexception-when-use-findviewbyid-in-alertdialog-builder
		// http://stackoverflow.com/questions/4954130/center-message-in-android-dialog-box
		int id = item.getItemId();
		if (id == R.id.working_hours) {


			LinearLayout main_view = new LinearLayout(this);
			main_view.setOrientation(LinearLayout.VERTICAL);

			for (int c=0; c<2; c++) {
				TextView header = new TextView(this);
				header.setText("Hafta içi");
				header.setGravity(Gravity.CENTER_HORIZONTAL);
				header.setTypeface(header.getTypeface(), Typeface.BOLD);
				header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				header.setPadding(0,20,0,0);
				main_view.addView(header);

				TableLayout table = new TableLayout(this);
				table.setStretchAllColumns(true);
				String[][] labels = new String[4][4];
				labels[0][0] = "";
				labels[0][1] = "Kuzey";
				labels[0][2] = "Güney";
				labels[0][3] = "Kilyos";
				labels[1][0] = "Sabah";
				labels[2][0] = "Öğlen";
				labels[3][0] = "Akşam";
				for (int i = 1; i < 4; i++) {
					for (int j = 1; j < 4; j++) {
						labels[i][j] = i + j + "";
					}
				}
				for (int i = 0; i < 4; i++) {
					TableRow row = new TableRow(this);
					row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
					for (int j = 0; j < 4; j++) {
						TextView textview = new TextView(this);
						textview.setText(labels[i][j]);
						textview.setTextColor(Color.BLUE);
						row.addView(textview);
					}
					table.addView(row, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				}

				main_view.addView(table);
			}

			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setView(main_view);
			alertbox.setNeutralButton(BUTTON_LABELS[new Random().nextInt(BUTTON_LABELS.length)], new DialogInterface.OnClickListener() {
				// click listener on the alert box
				public void onClick(DialogInterface arg0, int arg1) {
					// the button was clicked
				}
			});

			alertbox.show();
			return true;
		}
		else if (id == R.id.announcements) {
			LinearLayout main_view = new LinearLayout(this);
			main_view.setOrientation(LinearLayout.VERTICAL);

			TextView msg1 = new TextView(this);
			msg1.setText("This is the alertbox!\nThis is the alertbox!\nThis is the alertbox!\n");
			msg1.setGravity(Gravity.CENTER_HORIZONTAL);
			main_view.addView(msg1);

			TextView msg2 = new TextView(this);
			msg2.setText("This is the alertbox!n");
			msg2.setGravity(Gravity.CENTER_HORIZONTAL);
			main_view.addView(msg2);
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setView(main_view);
			alertbox.setNeutralButton(BUTTON_LABELS[new Random().nextInt(BUTTON_LABELS.length)], new DialogInterface.OnClickListener() {
				// click listener on the alert box
				public void onClick(DialogInterface arg0, int arg1) {
					// the button was clicked
				}
			});
			alertbox.show();
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
            TextView lunchSecondMealVegTextView = (TextView) rootView
                    .findViewById(R.id.lunch_second_meal_veg);
			TextView lunchThirdMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_third_meal);
			TextView lunchFourthMealTextView = (TextView) rootView
					.findViewById(R.id.lunch_fourth_meal);
			TextView dinnerFirstMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_first_meal);
			TextView dinnerSecondMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_second_meal);
            TextView dinnerSecondMealVegTextView = (TextView) rootView
                    .findViewById(R.id.dinner_second_meal_veg);
			TextView dinnerThirdMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_third_meal);
			TextView dinnerFourthMealTextView = (TextView) rootView
					.findViewById(R.id.dinner_fourth_meal);

            CheckBox notifCheckBox = (CheckBox) rootView
                    .findViewById(R.id.notif_checkbox);

            if (getArguments().getInt(ARG_SECTION_NUMBER) != 1) {
                notifCheckBox.setVisibility(View.GONE);
            }

            boolean currentNotifStatus = SaveSharedPreference.getNotificationStatus(getActivity().getApplicationContext());
            if (currentNotifStatus){
                notifCheckBox.setChecked(true);
            }

            notifCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   SaveSharedPreference.updateNotificationStatus(getActivity().getApplicationContext(), isChecked);
               }
           }
            );

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
			lunchTitleTextView.setText(meal1Title);
			dinnerTitleTextView.setText(meal2Title);

			fullDateTextView.setTextColor(Color.BLUE);
			dayNameTextView.setTextColor(Color.BLUE);
			lunchTitleTextView.setTextColor(Color.BLUE);
			dinnerTitleTextView.setTextColor(Color.BLUE);

            lunchSecondMealVegTextView.setTextColor(Color.RED);
            dinnerSecondMealVegTextView.setTextColor(Color.RED);

			// find correct meal
			for (int i = 0; i < foodList.size(); i++) {
				Meal meal = foodList.get(i);
				if (meal.day == currentDay
						&& meal.month.toLowerCase()
						.contentEquals(currentMonth.toLowerCase())) {
					lunchFirstMealTextView.setText(meal.first_meal);
					lunchSecondMealTextView.setText(meal.second_meal);
                    lunchSecondMealVegTextView.setText(meal.second_meal_veg);
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
                            dinnerSecondMealVegTextView.setText(meal.second_meal_veg);
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

	class FileDownloadTask extends AsyncTask<Void, Void, ArrayList<String>> {
		ArrayList<String> lines = new ArrayList<String>();

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
                    lines.add(s);
					Log.d("Line:",s);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return lines;
		}

		@Override
		protected void onPreExecute(){
		}
		@Override
		protected void onPostExecute(ArrayList<String> response){
            // implement here so that view is updated
		}
		@Override
		protected void onCancelled() {
		}
	}


}




