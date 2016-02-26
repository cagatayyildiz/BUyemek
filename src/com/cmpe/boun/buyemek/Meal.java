package com.cmpe.boun.buyemek;

public class Meal {

	int day;
	String month;
	int year;
	String time;
	String first_meal;
	String second_meal;
	String third_meal;
	String fourth_meal;

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


	public String toString()
	{
		return day + month + year + " - " + time + "\n" + first_meal + "\n" + second_meal + "\n" + third_meal + "\n" + fourth_meal + "\n";
	}


}
