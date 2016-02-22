package com.cmpe.boun.buyemek;

public class Meal {

	private int day;
	private String month;
	private int year;
	private String time;
	private String first_meal;
	private String second_meal;
	private String third_meal;
	private String fourth_meal;

	public Meal() {
		day = -1;
		year = -1;
		month = "";
		time = "";
		first_meal = "";
		second_meal = "";
		third_meal = "";
		fourth_meal = "";
	}
	public Meal(String line)
	{
		try {
			line = line.replace(".", "").replace("\\n", "");
			String dateAndTime = line.substring(0,line.indexOf(':'));
			if(dateAndTime.contains("("))
				dateAndTime = dateAndTime.substring(0,dateAndTime.indexOf('('));
			int seperatorIndex = dateAndTime.lastIndexOf(' ');
			String [] temp = dateAndTime.split(" ");
			this.day = Integer.parseInt(temp[0]);
			this.month = temp[1];
			this.year = Integer.parseInt(temp[2]);
			this.time = dateAndTime.substring(seperatorIndex+1);

			String [] meals = line.substring(line.indexOf(':')+1).split(",");
			this.first_meal = meals[0];
			this.second_meal = meals[1];
			this.third_meal = meals[2];
			this.fourth_meal = meals[3];
		} catch (Exception e) {

		}
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String toString()
	{
		return day + month + year + " - " + time + "\n" + first_meal + "\n" + second_meal + "\n" + third_meal + "\n" + fourth_meal + "\n";
	}


	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getFirstMeal() {
		return first_meal;
	}
	public void setFirst_meal(String first_meal) {
		this.first_meal = first_meal;
	}
	public String getSecondMeal() {
		return second_meal;
	}
	public void setSecond_meal(String second_meal) {
		this.second_meal = second_meal;
	}
	public String getThirdMeal() {
		return third_meal;
	}
	public void setThird_meal(String third_meal) {
		this.third_meal = third_meal;
	}
	public String getFourthMeal() {
		return fourth_meal;
	}
	public void setFourth_meal(String fourth_meal) {
		this.fourth_meal = fourth_meal;
	}


}
