package com.cmpe.boun.buyemek;

import android.util.Log;

public class Meal {

	private final static String DEFAULT_NOTE = "Bulunamadi!";
	int day;
	String month = DEFAULT_NOTE;
	int year;
	String time = DEFAULT_NOTE;
	String first_meal = DEFAULT_NOTE;
	String second_meal = DEFAULT_NOTE;
	String second_meal_veg = DEFAULT_NOTE;
	String third_meal = DEFAULT_NOTE;
	String fourth_meal = DEFAULT_NOTE;

	final static String MEAL1_TIME = "oglen";
	final static String MEAL2_TIME = "aksam";

	public Meal() {
		day = -1;
		year = -1;
		month = "";
		time = "";
		first_meal = "";
		second_meal = "";
		second_meal_veg = "";
		third_meal = "";
		fourth_meal = "";
	}
	public Meal(String line)
	{
		try {

			String[] s_line = line.split(",");
			String[] c_date = s_line[0].split("-");

			this.day = Integer.parseInt(c_date[0]);
			this.month = MainActivity.namesOfMonths[Integer.parseInt(c_date[1])-1];
			this.year = Integer.parseInt(c_date[2]);
			this.time = s_line[1];
			/*
			if (this.time.contentEquals(MEAL1_TIME) || this.time.contentEquals(MEAL2_TIME)) {
				throw new IllegalArgumentException("Meal time must be either " + MEAL1_TIME + " or " + MEAL2_TIME + ". Yours is " + this.time + ".");
			}
			*/
			this.first_meal = s_line[2];
			this.second_meal = s_line[3];
			this.second_meal_veg = s_line[4];
			this.third_meal = s_line[5];
			this.fourth_meal = s_line[6];
		} catch (Exception e) {

		}
	}


	public String toString()
	{
		return day + month + year + " - " + time + "\n" + first_meal + "\n" + second_meal + "\n" + second_meal_veg + "\n" + third_meal + "\n" + fourth_meal + "\n";
	}

	public String getJustMeals()
	{
		return  first_meal + ", " + second_meal + ", " + second_meal_veg + ", " + third_meal + ", " + fourth_meal;
	}

}
