package com.cmpe.boun.buyemek;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


@SuppressLint("NewApi") public class DataHandler extends SQLiteOpenHelper {

	public DataHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Database Version
	private static final int DATABASE_VERSION = 1;
	final private static String[] namesOfMonths = { "Ocak", "Þubat", "Mart",
		"Nisan", "Mayýs", "Haziran", "Temmuz", "Aðustos", "Eylül", "Ekim",
		"Kasým", "Aralýk" };

	// Database Name
	private static final String DATABASE_NAME = "MyDatabase";

	private static final String TABLE_FOOD = "FoodTable";
	
	private static final String KEY_FOOD_DAY = "day";
	private static final String KEY_FOOD_MONTH = "month";
	private static final String KEY_FOOD_YEAR = "year";
	private static final String KEY_FOOD_MEAL = "meal";	
	private static final String KEY_FOOD_FOOD1 = "food1";
	private static final String KEY_FOOD_FOOD2 = "food2";
	private static final String KEY_FOOD_FOOD3 = "food3";
	private static final String KEY_FOOD_FOOD4 = "food4";

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
				KEY_FOOD_FOOD1 + " TEXT, " + KEY_FOOD_FOOD2 + " TEXT, " + 
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




	public void addFood(Meal m) {
		SQLiteDatabase db = this.getWritableDatabase();
		insertMeal(db, m);
		db.close(); 
	}
	
	public void addFoods(ArrayList<Meal> list) {
		SQLiteDatabase db = this.getWritableDatabase();
		cleanDatabase(db);
		for (Meal m : list) {
			insertMeal(db, m);
		}
		db.close(); 
	}
	
	public void cleanDatabase(SQLiteDatabase db) {
		//String previousMonth = namesOfMonths[Math.max(Calendar.getInstance().get(Calendar.MONTH)-1,0)];
		//String deleteQuery = "DELETE FROM " + TABLE_FOOD  + " WHERE " + KEY_FOOD_MONTH + "=\"" + previousMonth + "\"";
		String deleteQuery = "DELETE FROM " + TABLE_FOOD ;
		db.execSQL(deleteQuery);
	}

	public void insertMeal(SQLiteDatabase db, Meal m) {
		ContentValues values = new ContentValues();
		values.put(KEY_FOOD_DAY, String.valueOf(m.getDay()));
		values.put(KEY_FOOD_MONTH, m.getMonth());
		values.put(KEY_FOOD_YEAR, String.valueOf(m.getYear()));
		values.put(KEY_FOOD_MEAL, m.getTime());
		values.put(KEY_FOOD_FOOD1, m.getFirstMeal());
		values.put(KEY_FOOD_FOOD2, m.getSecondMeal());
		values.put(KEY_FOOD_FOOD3, m.getThirdMeal());
		values.put(KEY_FOOD_FOOD4, m.getFourthMeal());

		db.insert(TABLE_FOOD, null, values);
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
				m.setDay(Integer.parseInt(cursor.getString(0)));
				m.setMonth(cursor.getString(1));
				m.setYear(Integer.parseInt(cursor.getString(2)));
				m.setTime(cursor.getString(3));
				m.setFirst_meal(cursor.getString(4));
				m.setSecond_meal(cursor.getString(5));
				m.setThird_meal(cursor.getString(6));
				m.setFourth_meal(cursor.getString(7));
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
