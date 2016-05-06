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
import android.widget.ScrollView;
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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Random;
import java.util.TreeMap;


public class MainActivity extends ActionBarActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	FileDownloadTask mFileDownloadTask;
	DataHandler db = null;
	ArrayList<Meal> foodList = new ArrayList<Meal>();
	ArrayList<String> newMealLines = new ArrayList<String>();
    LinkedHashMap<String, String> food_calorie_map;
    final static int NUM_SECTIONS = 10;
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

        SaveSharedPreference.updateAppInstalledFlag(getApplicationContext(), false);
        if (!SaveSharedPreference.getAppInstalledFlag(getApplicationContext())) {
            InitAlarms.setAlarms(getApplicationContext());
            Log.d("Alarms", "Initial alarms are set");
            SaveSharedPreference.updateAppInstalledFlag(getApplicationContext(), true);
        }

        SaveSharedPreference.setNotificationSetStatus(getApplicationContext(),true);


        Log.d("isDatabaseUpToDate", db.isDatabaseUpToDate() + " ");
		if (!db.isDatabaseUpToDate()) {
            if (isNetworkAvailable()) {
				try {
                    newMealLines = mFileDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null).get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; i < newMealLines.size(); i++) {
					String s = newMealLines.get(i);
					foodList.add(new Meal(s));
				}
				db.insertMeals(foodList,false);
				Log.d("databasefoodListSize", foodList.size() + " ");
			}
		    else {
                Toast.makeText(MainActivity.this, "Yemek listesinin güncellenmesi için internete bağlanmanız gereklidir.",
                        Toast.LENGTH_LONG).show();

            }
        }
        foodList = db.getNextNDaysMeals(NUM_SECTIONS);
        food_calorie_map = DataHandler.get_food_calorie_map(getApplicationContext());
        for (Meal m : foodList) {
            Log.d("Meal retrieved from db:", m.toString());
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

            String[][][] labels = new String[4][4][2];
            labels[1][1][0] = "7.30-9.30";
            labels[1][2][0] = "7.30-9.30";
            labels[1][3][0] = "7.30-9.30";
            labels[2][1][0] = "11.30-14.30";
            labels[2][2][0] = "12.30-14.30";
            labels[2][3][0] = "12.00-15.00";
            labels[3][1][0] = "17.00-19.00";
            labels[3][2][0] = "17.00-19.00";
            labels[3][3][0] = "17.00-19.00";

            labels[1][1][1] = "8.30-10.00";
            labels[1][2][1] = "8.30-10.00";
            labels[1][3][1] = "8.30-10.00";
            labels[2][1][1] = "12.30-13.45";
            labels[2][2][1] = "SORRY";
            labels[2][3][1] = "12.30-13.45";
            labels[3][1][1] = "17.30-19.30";
            labels[3][2][1] = "17.30-19.30";
            labels[3][3][1] = "17.30-19.30";

            String[] titles = {"Hafta İçi", "Hafta Sonu"};
			for (int c=0; c<2; c++) {
                labels[0][0][c] = "";
                labels[0][1][c] = "Kuzey";
                labels[0][2][c] = "Güney";
                labels[0][3][c] = "Kilyos";
                labels[1][0][c] = "Sabah";
                labels[2][0][c] = "Öğlen";
                labels[3][0][c] = "Akşam";
				TextView header = new TextView(this);
				header.setText(titles[c]);
				header.setGravity(Gravity.CENTER_HORIZONTAL);
				header.setTypeface(header.getTypeface(), Typeface.BOLD);
				header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				header.setPadding(0, 20, 0, 0);
				main_view.addView(header);

				TableLayout table = new TableLayout(this);
				table.setStretchAllColumns(true);
				for (int i = 0; i < 4; i++) {
					TableRow row = new TableRow(this);
					row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
					for (int j = 0; j < 4; j++) {
						TextView textview = new TextView(this);
						textview.setText(labels[i][j][c]);
						textview.setTextColor(Color.BLUE);
                        if (i==0 || j==0)
                            textview.setTextColor(Color.BLACK);
                        textview.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
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
		else if (id == R.id.calorie_list) {

            ScrollView scroll = new ScrollView(this);
            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

            TableLayout table = new TableLayout(this);
            table.setStretchAllColumns(true);

            for (String key : food_calorie_map.keySet()) {
                TableRow row = new TableRow(this);
                TextView textview = new TextView(this);
                textview.setText(key);
                textview.setTextColor(Color.BLUE);
                textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                row.addView(textview);

                TextView textview2 = new TextView(this);
                textview2.setText(food_calorie_map.get(key));
                textview2.setTextColor(Color.RED);
                textview2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                row.addView(textview2);

                table.addView(row, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            }

            scroll.addView(table);


            alertbox.setView(scroll);
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
			return NUM_SECTIONS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
            return "Section " + (position+1);
            /*
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
			*/
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


            notifCheckBox.setVisibility(View.INVISIBLE);
            boolean currentNotifStatus = SaveSharedPreference.getNotificationStatus(getActivity().getApplicationContext());
            if (currentNotifStatus){
                notifCheckBox.setChecked(true);
            }

            notifCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   SaveSharedPreference.updateNotificationStatus(getActivity().getApplicationContext(), isChecked);
                   Log.d("NotificationStatus", "now it is " + isChecked);
               }
            });

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
            LinkedHashMap<String,String> food_calorie_map = ((MainActivity) getActivity()).food_calorie_map;

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
            boolean areMealsSet[] = {false, false};
			for (int i = 0; i < foodList.size(); i++) {
				Meal meal = foodList.get(i);
                Log.d("meal_today", meal.toString());
				if (meal.day == currentDay && meal.month.equalsIgnoreCase(currentMonth) && meal.first_meal!=null) {
					if (meal.time.equals(Meal.MEAL1_TIME)) {
                        lunchFirstMealTextView.setText(meal.first_meal + get_calorie(food_calorie_map,meal.first_meal));
                        lunchSecondMealTextView.setText(meal.second_meal + get_calorie(food_calorie_map,meal.second_meal));
                        lunchSecondMealVegTextView.setText(meal.second_meal_veg  + get_calorie(food_calorie_map,meal.second_meal_veg));
                        lunchThirdMealTextView.setText(meal.third_meal  + get_calorie(food_calorie_map,meal.third_meal));
                        lunchFourthMealTextView.setText(meal.fourth_meal);
                        areMealsSet[0] = true;
					}
                    else if (meal.time.equals(Meal.MEAL2_TIME)) {
                        dinnerFirstMealTextView.setText(meal.first_meal + get_calorie(food_calorie_map,meal.first_meal));
                        dinnerSecondMealTextView.setText(meal.second_meal + get_calorie(food_calorie_map,meal.second_meal));
                        dinnerSecondMealVegTextView.setText(meal.second_meal_veg + get_calorie(food_calorie_map,meal.second_meal_veg));
                        dinnerThirdMealTextView.setText(meal.third_meal + get_calorie(food_calorie_map,meal.third_meal));
                        dinnerFourthMealTextView.setText(meal.fourth_meal);
                        areMealsSet[1] = true;
                    }
				}
                if (areMealsSet[0] && areMealsSet[1]) {
                    break;
                }
			}

			return rootView;
		}

        public static String get_calorie(LinkedHashMap<String,String> food_calorie_map, String meal) {

            /*
            if (meal == null) {
                return "";
            }
            String[] divisors = {"-","/","\\"};
            boolean[] divs = {false, false, false};
            for (int i=0; i<3; i++)
                divs[i] = meal.contains(divisors[i]);
            if (!divs[0] && !divs[1] && !divs[2]) {
                String cal = " - ";
                if (food_calorie_map.keySet().contains(meal)) {
                    cal += food_calorie_map.get(meal);
                } else {
                    cal += "??? kcal";
                }
                return cal;
            }
            else {
                String divisor = divisors[0];
                if (divs[1]) divisor = divisors[1];
                else if (divs[2]) divisor = divisors[2];
                String[] parts = meal.split(divisor);
                for (int i=0; i<parts.length; i++) {
                    if (parts[i].charAt(0) == ' ') {
                        parts[i] = parts[i].substring(1);
                    }
                    if (parts[i].charAt(parts[i].length()-1) == ' ') {
                        parts[i] = parts[i].substring(0,parts[i].length()-1);
                    }
                }
                String cal = " - ";
                for (String part : parts) {
                    if (food_calorie_map.keySet().contains(part)) {
                        cal += food_calorie_map.get(part) + divisor;
                    } else {
                        cal += "??? kcal" + divisor;
                    }
                }
                return cal.substring(0,cal.length()-1);
            }
            */
            return "";
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




