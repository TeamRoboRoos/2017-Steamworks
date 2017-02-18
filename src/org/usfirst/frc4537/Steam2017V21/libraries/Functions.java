package org.usfirst.frc4537.Steam2017V21.libraries;

public class Functions {
	
	/**
	 * Truncates  the decimal from a number
	 * @param value Number to truncate
	 * @return Truncated value
	 */
	public static double floor(double value) {
		return Math.floor(value);
	}
	
	/**
	 * Truncates to places decimal places
	 * @param value Number to truncate
	 * @param places Number of places to truncate to
	 * @return Truncated value
	 */
	public static double floor(double value, double places) {
		places = Math.pow(10, places);
		return Math.floor(value * places)/places;
	}
	
	/**
	 * Applies calibration to pressure reading
	 * @param analogReading Reading from pressure sensor
	 * @return Calibrated pressure reading
	 */
	public static double pressure(double analogReading) {
		return (analogReading-258.2)/4.348;
	}
	
	/**
	 * Applied calibration to encoder reading
	 * @param encoderReading Reading from encoder
	 * @return Calibrated encoder reading
	 */
	public static double encoder(double encoderReading) {
		return encoderReading * 12557;
	}
}
