package org.usfirst.frc.team4537.robot;

public class CustomFunctions {
	
	public static double floor(double value) {
		return Math.floor(value);
	}
	
	public static double floor(double value, double places) {
		places = Math.pow(10, places);
		return Math.floor(value * places)/places;
	}
}
