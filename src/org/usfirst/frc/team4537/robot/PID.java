package org.usfirst.frc.team4537.robot;

public class PID {

	private double kp;
	private double ki;
	private double kd;
	private double error;
	private double previousError;
	private double proportional;
	private double integral;
	private double derivative;
	private double target;
	private double output;
	private double scale;

	/**
	 * Sets the target P, I and D values to 1 for the control loop
	 */
	public PID() {
		this.kp = 1;
		this.ki = 1;
		this.kd = 1;
		this.scale = 1;
		this.init();
	}

	/**
	 * Sets the target P, I and D values for the control loop
	 * And an output scaler
	 * @param Proportional
	 * @param Integral
	 * @param Derivative
	 * @param Scale
	 */
	public PID(double kp, double ki, double kd, double scale) {
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.scale = scale;
		this.init();
	}

	/**
	 * Initialises variables
	 */
	public void init() {
		this.error = 0;
		this.previousError = 0;
		this.proportional = 0;
		this.integral = 0;
		this.derivative = 0;
	}
	
	public static double floor(double value) {
		return Math.floor(value * 100)/100;
	}

	/**
	 * Sets target value for control loop
	 * @param Target
	 */
	public void setTarget(double target) {
		this.target = target;
	}

	/**
	 * Runs PID loop and returns output value, takes input
	 * @param Value
	 * @return Output
	 */
	public double calculate(double value) {
		this.error = this.target - value;
		this.integral = this.integral + this.error;
		this.derivative = this.error - this.previousError;
		this.previousError = this.error;
		
		this.proportional = this.kp * this.error;
		this.integral = this.ki * this.integral;
		this.derivative = this.kd * this.derivative;

		this.output = this.proportional + this.integral + this.derivative;
		this.output *= scale;
		return this.output;
	}
	
	public void debug() {
		System.out.println("T    : " + Double.toString(floor(this.target)));
		//System.out.println("V    : " + Double.toString(floor(value)));
		System.out.println("E    : " + Double.toString(floor(this.error)));
		System.out.println("P    : " + Double.toString(floor(this.proportional)));
		System.out.println("I    : " + Double.toString(floor(this.integral)));
		System.out.println("D    : " + Double.toString(floor(this.derivative)));
		System.out.println("O    : " + Double.toString(floor(this.output)));
		
	}
}
