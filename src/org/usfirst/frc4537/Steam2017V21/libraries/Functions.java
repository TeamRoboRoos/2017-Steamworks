package org.usfirst.frc4537.Steam2017V21.libraries;

public class Functions {

	/**
	 * Truncates  the decimal from a number
	 * @param number Number to truncate
	 * @return Truncated value
	 */
	public static double floor(double number) {
		return Math.floor(number);
	}

	/**
	 * Truncates to places decimal places
	 * @param number Number to truncate
	 * @param places Number of places to truncate to
	 * @return Truncated value
	 */
	public static double floor(double number, double places) {
		places = Math.pow(10, places);
		return Math.floor(number * places)/places;
	}

	/**
	 * Applies calibration to pressure reading
	 * @param analogReading Reading from pressure sensor
	 * @param gradient Gradient of trend line
	 * @param intercept Intercept of trend line
	 * @return Calibrated pressure reading
	 */
	public static double pressure(double analogReading, double gradient, double intercept) {
		return (analogReading-intercept)/gradient;
	}

	/**
	 * Applied calibration to encoder reading
	 * @param encoderReading Reading from encoder
	 * @return Calibrated encoder reading
	 */
	public static double encoder(double encoderReading) {
		return encoderReading * 12557;
	}

	/**
	 * Calculate the gradient and intercept of the trend line
	 * @param coords [[x,y],[x,y],...]
	 * @return [Gradient, Intercept]
	 */
	public static double[] statreg(double[][] coords) {
		double a = 0;
		double b = 0;
		double b1 = 0;
		double b2 = 0;
		double c = 0;
		double d = 0;
		double d1 = 0;
		double e = 0;

		int n = coords.length;

		for (int i = 0; i < n; i++) {
			a += coords[i][0]*coords[i][1];
			b1 += coords[i][0];
			b2 += coords[i][1];
			c += Math.pow(coords[i][0], 2);
			d1 += coords[i][0];
			e += coords[i][1];
		}
		a *= n;
		b = b1 * b2;
		c *= n;
		d = Math.pow(d1, 2);

		double grad = (a-b)/(c-d);
		double icpt = (e-(grad * d1))/n;

		double[] returner = {grad, icpt};
		return returner;
	}
}
