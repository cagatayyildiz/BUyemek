package com.cmpe.boun.buyemek;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


@SuppressLint("NewApi") public class DataHandler extends SQLiteOpenHelper {

	public DataHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Database Version
	static final int DATABASE_VERSION = 1;
	final static String[] namesOfMonths = { "Ocak", "Şubat", "Mart",
		"Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim",
		"Kasım", "Aralık" };

	// Database Name
	static final String DATABASE_NAME = "MyDatabase";

	static final String TABLE_FOOD = "FoodTable";
	
	static final String KEY_FOOD_DAY = "day";
	static final String KEY_FOOD_MONTH = "month";
	static final String KEY_FOOD_YEAR = "year";
	static final String KEY_FOOD_MEAL = "meal";	
	static final String KEY_FOOD_FOOD1 = "food1";
	static final String KEY_FOOD_FOOD2 = "food2";
	static final String KEY_FOOD_FOOD2_VEG = "food2_veg"; // vegaterian
	static final String KEY_FOOD_FOOD3 = "food3";
	static final String KEY_FOOD_FOOD4 = "food4";

	public DataHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DataHandler(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		String CREATE_FOOD_TABLE = "CREATE TABLE " + TABLE_FOOD + "(" +
				KEY_FOOD_DAY + " TEXT, " + KEY_FOOD_MONTH + " TEXT, " + 
				KEY_FOOD_YEAR + " TEXT, " + KEY_FOOD_MEAL + " TEXT, " +
				KEY_FOOD_FOOD1 + " TEXT, " + KEY_FOOD_FOOD2 + " TEXT, " + KEY_FOOD_FOOD2_VEG + " TEXT, " +
				KEY_FOOD_FOOD3 + " TEXT, " + KEY_FOOD_FOOD4 + " TEXT" + 
				")";
		arg0.execSQL(CREATE_FOOD_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		Log.w(DataHandler.class.getName(),
				"Upgrading database from version " + arg1 + " to "
						+ arg2 + ", which will destroy all old data.");
		arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);

		onCreate(arg0);
	}


	public void insertMeals(ArrayList<Meal> list) {
		SQLiteDatabase db = this.getWritableDatabase();
		cleanDatabase();
		for (Meal m : list) {
			insertMeal(db, m);
		}
		db.close(); 
	}
	
	public void cleanDatabase() {
		//String previousMonth = namesOfMonths[Math.max(Calendar.getInstance().get(Calendar.MONTH)-1,0)];
		//String deleteQuery = "DELETE FROM " + TABLE_FOOD  + " WHERE " + KEY_FOOD_MONTH + "=\"" + previousMonth + "\"";
		SQLiteDatabase db = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM " + TABLE_FOOD ;
		db.execSQL(deleteQuery);
	}

	public void insertMeal(SQLiteDatabase db, Meal m) {
		ContentValues values = new ContentValues();
		values.put(KEY_FOOD_DAY, String.valueOf(m.day));
		values.put(KEY_FOOD_MONTH, m.month);
		values.put(KEY_FOOD_YEAR, String.valueOf(m.year));
		values.put(KEY_FOOD_MEAL, m.time);
		values.put(KEY_FOOD_FOOD1, m.first_meal);
		values.put(KEY_FOOD_FOOD2, m.second_meal);
		values.put(KEY_FOOD_FOOD2_VEG, m.second_meal_veg);
		values.put(KEY_FOOD_FOOD3, m.third_meal);
		values.put(KEY_FOOD_FOOD4, m.fourth_meal);

		db.insert(TABLE_FOOD, null, values);
	}


	public ArrayList<Meal> getTomorowsMeals() {

		Calendar c = Calendar.getInstance();
		Date dt = new Date();
		c.setTime(dt);
		c.add(Calendar.DATE, 1);

		String month = namesOfMonths[c.get(Calendar.MONTH)];
		String day = c.get(Calendar.DAY_OF_MONTH)+"";
		String year = c.get(Calendar.DAY_OF_YEAR)+"";
		return getMealsSpecific(month,day,year);
	}

	public ArrayList<Meal> getTodaysMeals() {
		String month = namesOfMonths[Calendar.getInstance().get(Calendar.MONTH)];
		String day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"";
		String year = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+"";
		return getMealsSpecific(month,day,year);
	}

	public ArrayList<Meal> getMealsSpecific(String month,String day,String year) {
		ArrayList<Meal> list = new ArrayList<Meal>();

		String selectQuery = "SELECT  * FROM " + TABLE_FOOD + " WHERE " + KEY_FOOD_MONTH + "=\"" + month + "\""
				+ " AND " + KEY_FOOD_DAY+ "=\"" + day + "\""
				+ " AND " + KEY_FOOD_YEAR + "=\"" + year + "\"";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Meal m = new Meal();
				m.day = (Integer.parseInt(cursor.getString(0)));
				m.month = (cursor.getString(1));
				m.year = (Integer.parseInt(cursor.getString(2)));
				m.time = (cursor.getString(3));
				m.first_meal =(cursor.getString(4));
				m.second_meal = (cursor.getString(5));
				m.second_meal_veg = (cursor.getString(6));
				m.third_meal = (cursor.getString(7));
				m.fourth_meal = (cursor.getString(8));
				list.add(m);
			} while (cursor.moveToNext());
		}

		db.close();

		return list;
	}

	
	public ArrayList<Meal> getThisMonthsMeals() {
		ArrayList<Meal> list = new ArrayList<Meal>();
		String month = namesOfMonths[Calendar.getInstance().get(Calendar.MONTH)];
		
		String selectQuery = "SELECT  * FROM " + TABLE_FOOD + " WHERE " + KEY_FOOD_MONTH + "=\"" + month + "\"";
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Meal m = new Meal();
				m.day = (Integer.parseInt(cursor.getString(0)));
				m.month = (cursor.getString(1));
				m.year = (Integer.parseInt(cursor.getString(2)));
				m.time = (cursor.getString(3));
				m.first_meal =(cursor.getString(4));
				m.second_meal = (cursor.getString(5));
				m.second_meal_veg = (cursor.getString(6));
				m.third_meal = (cursor.getString(7));
				m.fourth_meal = (cursor.getString(8));
				list.add(m);
			} while (cursor.moveToNext());
		}

		db.close(); 
		
		return list;
	}
	
	public boolean isDatabaseUpToDate() {
		String month = namesOfMonths[Calendar.getInstance().get(Calendar.MONTH)];
		
		String selectQuery = "SELECT  * FROM " + TABLE_FOOD + " WHERE " + KEY_FOOD_MONTH + "=\"" + month + "\"";
		Log.d("Query:", selectQuery);

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.getCount()<55) { // if less than 55 rows exist
			return false;
		} else if (cursor.moveToFirst()) { //if no data recorded to db
			return true;
		}
		else {
			return false;
		}
	}
}
