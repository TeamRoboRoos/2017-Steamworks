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
		System.out.println("T    : " + Double.toString(CustomFunctions.floor(this.target, 2)));
		//System.out.println("V    : " + Double.toString(RandomCrap.floor(value, 2)));
		System.out.println("E    : " + Double.toString(CustomFunctions.floor(this.error)));
		System.out.println("P    : " + Double.toString(CustomFunctions.floor(this.proportional, 2)));
		System.out.println("I    : " + Double.toString(CustomFunctions.floor(this.integral, 2)));
		System.out.println("D    : " + Double.toString(CustomFunctions.floor(this.derivative, 2)));
		System.out.println("O    : " + Double.toString(CustomFunctions.floor(this.output, 2)));
		
	}
}
