package org.usfirst.frc.team4537.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveBase {
	Joystick arcadeStick;
	PowerDistributionPanel pdp;
	ADXRS450_Gyro gyroscope;
	ADXL362 accelerometer;
	ADXL362.AllAxes accelerometerAll;
	Accelerometer accRio;
	
	//These shouldn't need to be changed
	private final int MOTOR_1 = 1;
	private final int MOTOR_2 = 2;
	private final int MOTOR_3 = 3;
	private final int MOTOR_4 = 4;
	private final int MOTOR_5 = 5;
	private final int MOTOR_6 = 6;

	//These shouldn't need to be changed
	CANTalon motor1;
	CANTalon motor2;
	CANTalon motor3;
	CANTalon motor4;
	CANTalon motor5;
	CANTalon motor6;

	//Arcade definitions
	public double leftSpeed = 0;
	public double rightSpeed = 0;
	private double moveValue = 0;
	private double rotateValue = 0;
	private double moveSign = 1;
	private double rotateSign = 1;
	private boolean preserveSign = false;
	private double mDeadzone = 0.01;
	private double rDeadzone = 0.01;
	public int direction = -1;
	private double powerIndex = 2;
	//Speed limiter
	private double speedMultiplier = 0.5;

	//Acceleration limiter
	private double previousMoveValue = 0;
	private double previousRotateValue = 0;
	private double maxMoveAcceleration = 0.01;
	private double maxMoveDeceleration = 0.05;
	private double maxRotateAcceleration = 0.05;
	private double currentMV = 0;
	//private double maxRotateDeceleration = 0.05; //Currently isn't used

	//Direction change variables
	private double previousLeftSpeed = 0;
	private double previousRightSpeed = 0;
	private boolean button2Pressed = false;
	private boolean ready2Change = false;
	public boolean accCode = true;

	////System telemetry
	//private int loopIterations = 0;
	//private long systemTime = 0;

	//Robot telemetry
	public double leftCurrent = 0;
	public double rightCurrent = 0;
	public double climberCurrent = 0;
	private double turnAngle = 0;

	
	private int loopIterations = 0;
	private long systemTime = 0;
	public double robotVelocity = 0;
	//private double shooterVelocity = 0;
	public long lastSystemTimeCurrent = 0;
	public long lastSystemTimeVelocity = 0;
	public long lastSystemTimePID = 0;



	//private AverageCalculator shooterVelocityAvg = new AverageCalculator(100);
	public final int ENCODER_RATIO = 1;

	//PID Loops
	private PID turnLeftPID = new PID(0.001, 0.001, 0.001, 0.01);
	private PID turnRightPID = new PID(0.001, 0.001, 0.001, 0.01);
	private PID drivePID = new PID(0.1, 1, 1, 0.1);
	
	public DriveBase() {
		//For all that is good in this world, DO NOT touch or breathe on these
		//Left motors
		motor1 = new CANTalon(MOTOR_1); //frontLeftMotor
		motor1.setExpiration(0.1);
		motor1.setInverted(true);
		motor2 = new CANTalon(MOTOR_2); //middleLeftMotor
		motor2.setExpiration(0.1);
		motor2.setInverted(true);
		motor3 = new CANTalon(MOTOR_3); //rearLeftMotor
		motor3.setExpiration(0.1);
		motor3.setInverted(true);

		//Right motors
		motor4 = new CANTalon(MOTOR_4); //frontRightMotor
		motor4.setExpiration(0.1);
		motor5 = new CANTalon(MOTOR_5); //middleRightMotor
		motor5.setExpiration(0.1);
		motor6 = new CANTalon(MOTOR_6); //rearRightMotor
		motor6.setExpiration(0.1);

		//Setup PDP
		pdp = new PowerDistributionPanel();

		gyroscope = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
		accelerometer = new ADXL362(SPI.Port.kOnboardCS1, Accelerometer.Range.k8G);
		accRio = new BuiltInAccelerometer(Accelerometer.Range.k4G);
	}
	
	public void drive() {
		arcadeStick = new Joystick(2);
		
		//Get drive functions from the dashboard
		accCode = SmartDashboard.getBoolean("DB/Button 0", false);
		speedMultiplier = Math.min(SmartDashboard.getNumber("DB/Slider 0", 0.75), 1);

		//Get move values from joystick
		moveValue = arcadeStick.getRawAxis(1) * direction;
		rotateValue = arcadeStick.getRawAxis(3);

		//Store signs in case they need to be reapplied
		moveSign = 1;
		rotateSign = 1;
		preserveSign = false;
		if (moveValue < 0) {
			moveSign = -1;
		}
		if (rotateValue < 0) {
			rotateSign = -1;
		}
		//Find out if we need to reapply the sign after ^2, ^4, etc.
		if (powerIndex/2 == Math.floor(powerIndex/2)) {
			preserveSign = true;
		}

		//Bring control inputs to the power of powerIndex
		moveValue = Math.pow(moveValue, powerIndex);
		rotateValue = Math.pow(rotateValue, powerIndex);
		//Reapply sign if needed
		if (preserveSign) {
			moveValue *= moveSign;
			rotateValue *= rotateSign;
		}

		//Joystick deadzones to remove jitter from controller
		if (moveValue < mDeadzone && moveValue > -mDeadzone) {
			moveValue = 0;
		}
		if (rotateValue < rDeadzone && rotateValue > -rDeadzone) {
			rotateValue = 0;
		}
			
		moveValue = Math.abs(moveValue);
		rotateValue = Math.abs(rotateValue);

		if (accCode) {
			accCode();
		}
		//Reapply sign
		moveValue *= moveSign;
		rotateValue *= rotateSign;

		if (!accCode){
			drivePID.setTarget(moveValue);
			moveValue = drivePID.calculate(currentMV);
			currentMV = moveValue;
		}
		
		//Mathy arcadey stuffy
		/*
		 * This block of code shouldn't really need to be changed
		 * It works out the math of how much power to put into
		 * each motor based on the inputs from the joystick
		 */
		if (moveValue > 0) {
			if (rotateValue > 0) {
				leftSpeed = moveValue - rotateValue;
				rightSpeed = Math.max(moveValue, rotateValue);
			}
			else {
				leftSpeed = Math.max(moveValue, -rotateValue);
				rightSpeed = moveValue + rotateValue;
			}
		}
		else {
			if (rotateValue > 0) {
				leftSpeed = -Math.max(-moveValue, rotateValue);
				rightSpeed = moveValue + rotateValue;
			}
			else {
				leftSpeed = moveValue - rotateValue;
				rightSpeed = -Math.max(-moveValue, -rotateValue);
			}
		}

		//Limit speed output to the throttle for adjustment on the fly
		leftSpeed *= (1 - arcadeStick.getRawAxis(2));
		rightSpeed *= (1 - arcadeStick.getRawAxis(2));

		//Store speed outputs for next iteration
		previousLeftSpeed = leftSpeed;
		previousRightSpeed = rightSpeed;

		//Cap maximum and minimum speed outputs
		leftSpeed = Math.max(leftSpeed, -1);
		leftSpeed = Math.min(leftSpeed, 1);
		rightSpeed = Math.max(rightSpeed, -1);
		rightSpeed = Math.min(rightSpeed, 1);

		//Apply speed limit
		leftSpeed *= speedMultiplier;
		rightSpeed *= speedMultiplier;

		/*
		 * This block of code will send the speed outputs to the motors
		 * It should under no circumstances need to be changed
		 * It can cause the motors to fight and possibly break the gearbox
		 */
		System.out.println("LS: " + leftSpeed);
		//Left side
		motor1.set(leftSpeed);
		motor2.set(leftSpeed);
		motor3.set(leftSpeed);

		System.out.println("RS: " + rightSpeed);
		//Right Side
		motor4.set(rightSpeed);
		motor5.set(rightSpeed);
		motor6.set(rightSpeed);
    	

		//Pressure

		

		
		randomGyroAndAccStuff();
		randomEncoderStuff();

	}
	
	public void randomGyroAndAccStuff() {
		//Print telemetry/debug information to the Smart Dashboard/Console
		//System.out.println("Pressure: " + Double.toString(PID.floor((pressure-258.2)/4.348)) + "psi"); //y = 4.348x + 258.2 x=(y-258.2)/4.348
		
		//System.out.print("Gyro A: " + Double.toString(gyroscope.getAngle()));
		//System.out.println(" Gyro R: " + Double.toString(gyroscope.getRate()));
		
		//System.out.println(Double.toString(accelerometer.getZ()));
		//System.out.println(accelerometer);
		
		//System.out.println("AccR: " + Double.toString(Math.sqrt(Math.pow(accRio.getX(), 2) + Math.pow(accRio.getY(), 2) + Math.pow(accRio.getZ(), 2))));
		
		/*this.accelerometerAll = this.accelerometer.getAccelerations();
		if (this.accelerometerAll != null) {
			System.out.print("Acce X: " + Double.toString(accelerometerAll.XAxis));
			System.out.print(" Acce Y: " + Double.toString(accelerometerAll.YAxis));
			System.out.println(" Acce Z: " + Double.toString(accelerometerAll.ZAxis));
		}
		else {
			System.out.println("Can't Read Acc.");
		}
		*/
	}
	
	public void randomEncoderStuff() {
		/*
		if (motor3.getEncPosition() < turnAngle) {
			leftSpeed = 0.35;
			System.out.println("L+");
		}
		else if (motor3.getEncPosition() > turnAngle){
			leftSpeed = -0.35;
			System.out.println("L-");
		}
		
		if (-motor4.getEncPosition() < -turnAngle) {
			rightSpeed = 0.35;
			System.out.println("R+");
		}
		else if (-motor4.getEncPosition() > -turnAngle){
			rightSpeed = -0.35;
			System.out.println("R-");
		}
		*/
	}
	
	public void resetDrivePID() {
		motor3.setEncPosition(0);
		motor4.setEncPosition(0);
		currentMV = 0;
		turnLeftPID.init();
		turnRightPID.init();
		drivePID.init();
	}
	
	public void changeDirection() {
		//Check we aren't moving
		if (previousLeftSpeed > -0.3 && previousLeftSpeed < 0.3 && previousRightSpeed > -0.3 && previousRightSpeed < 0.3) {
			button2Pressed = true; //Look into whether or not this line is required
			//Check if robot is actually ready to change direction
			if (button2Pressed && ready2Change) {
				direction *= -1;
				button2Pressed = false;
				ready2Change = false;
			}
			else {
				ready2Change = true;
			}
		}
	}
	
	public void halfSpeed() {
		moveValue *= 0.5;
		rotateValue *= 0.5;
	}
	
	public void driveStraightPID() {
		turnAngle = SmartDashboard.getNumber("DB/Slider 2", 0) - SmartDashboard.getNumber("DB/Slider 3", 0);
		//Ticks per metre: 12557
		
		/*if (turnAngle != 0) {
			turnAngle = ((0.56 * Math.PI) / (360 / turnAngle));
		}*/
		
		turnAngle = -arcadeStick.getRawAxis(2) * 2;
		turnAngle *= 12557;
		//turnAngle = 2 * 12557;
		
		this.turnLeftPID.setTarget(turnAngle);
		this.turnRightPID.setTarget(turnAngle);
		
		leftSpeed = this.turnLeftPID.calculate(this.motor3.getEncPosition());// /12557
		rightSpeed = this.turnRightPID.calculate(-this.motor4.getEncPosition());
		
		/*if (lastSystemTimePID+500 < System.currentTimeMillis()) {
			System.out.println("LENC : " + Double.toString(PID.floor(motor3.getEncPosition())));// / 12557)));
			//System.out.println("RENC : " + Double.toString(PID.floor(-motor4.getEncPosition())));// / 12557)));
			System.out.println("Angle: " + Double.toString(turnAngle));
			System.out.println("Left : " + Double.toString(leftSpeed));
			//System.out.println("Right: " + Double.toString(rightSpeed));
			this.turnLeftPID.debug();
			System.out.println("");
			lastSystemTimePID = System.currentTimeMillis();
		}*/
		
		leftSpeed = Math.min(leftSpeed, 1);
		leftSpeed = Math.max(leftSpeed, -1);
		rightSpeed = Math.min(rightSpeed, 1);
		rightSpeed = Math.max(rightSpeed, -1);
		
		leftSpeed *= 0.75;
		rightSpeed *= 0.75;
	}
	
	public void accCode() {
		//Check acceleraton limitation
		//Remove sign to avoid confusion between forward and backwards as opposed to speeding up and slowing down

		//Check if move speed is increasing
		if (moveValue > previousMoveValue + maxMoveAcceleration) {
			moveValue = previousMoveValue + maxMoveAcceleration;
		}
		//Check if move speed is decreasing
		if (moveValue < previousMoveValue - maxMoveDeceleration) {
			moveValue = previousMoveValue - maxMoveDeceleration;
		}
		//Check if rotate speed is decreasing
		if (rotateValue > previousRotateValue + maxRotateAcceleration) {
			rotateValue = previousRotateValue + maxRotateAcceleration;
		}

		//Store previous motion inputs for next iteration
		previousMoveValue = moveValue;
		previousRotateValue = rotateValue;
	}
	
}
