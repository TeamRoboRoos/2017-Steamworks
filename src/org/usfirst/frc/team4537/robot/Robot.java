package org.usfirst.frc.team4537.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;

import com.ctre.CANTalon;

import edu.wpi.cscore.VideoSource;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;

import java.lang.Math;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {

	Joystick leftStick;
	Joystick rightStick;
	Joystick arcadeStick;
	PowerDistributionPanel pdp;

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
	private Victor auxMotor;

	//Arcade definitions
	private double leftSpeed = 0;
	private double rightSpeed = 0;
	private double moveValue = 0;
	private double rotateValue = 0;
	private double moveSign = 1;
	private double rotateSign = 1;
	private boolean preserveSign = false;
	private double mDeadzone = 0.05;
	private double rDeadzone = 0.05;
	private int direction = -1;
	private double powerIndex = 3;

	//Speed limiter
	private double speedMultiplier = 0.5;

	//Acceleration limiter
	private double previousMoveValue = 0;
	private double previousRotateValue = 0;
	private double maxMoveAcceleration = 0.01;
	private double maxMoveDeceleration = 0.05;
	private double maxRotateAcceleration = 0.05;
	//private double maxRotateDeceleration = 0.05; //Currently isn't used

	//Direction change variables
	private double previousLeftSpeed = 0;
	private double previousRightSpeed = 0;
	private boolean button2Pressed = false;
	private boolean ready2Change = false;
	private boolean accCode = true;

	//Climber variables
	private double climberSpeed = 0;

	////System telemetary
	//private int loopIterations = 0;
	//private long systemTime = 0;

	//Robot telemetary
	private double leftCurrent = 0;
	private double rightCurrent = 0;
	private double climberCurrent = 0;
	private double robotVelocity = 0;
	//private double shooterVelocity = 0;
	private long lastSystemTimeCurrent = 0;
	private long lastSystemTimeVelocity = 0;

	//Averagable variables
	private AverageCalculator leftMotorsCurrentDrawAvg = new AverageCalculator(200);
	private AverageCalculator rightMotorsCurrentDrawAvg = new AverageCalculator(200);
	private AverageCalculator climberMotorCurrentDrawAvg = new AverageCalculator(200);
	private AverageCalculator robotVelocityAvg = new AverageCalculator(100);
	//private AverageCalculator shooterVelocityAvg = new AverageCalculator(100);
	private final int ENCODER_RATIO = 1;

	public Robot() {

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

		auxMotor = new Victor(0);

		//Setup joysticks
		leftStick = new Joystick(0);
		rightStick = new Joystick(1);
		arcadeStick = new Joystick(2);

		//Setup PDP
		pdp = new PowerDistributionPanel();
	}

	@Override
	public void robotInit() {
		//Start camera capture servers
		CameraServer.getInstance().startAutomaticCapture("Battery", 0);
		CameraServer.getInstance().startAutomaticCapture("Loader", 1);
		//Insert default values into DB variables
		SmartDashboard.putBoolean("DB/Button 0", accCode);
		SmartDashboard.putNumber("DB/Slider 0", 0.75);
		SmartDashboard.putNumber("DB/Slider 1", 0.75);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the if-else structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomous() {

	}

	/**
	 * Runs the motors with arcade steering.
	 */
	@Override
	public void operatorControl() {
		while (isOperatorControl() && isEnabled()) {

			//Iterate loop number
			//loopIterations++;
			//systemTime = System.currentTimeMillis();

			//Get drive functions from the dashboard
			accCode = SmartDashboard.getBoolean("DB/Button 0", false);
			speedMultiplier = Math.min(SmartDashboard.getNumber("DB/Slider 0", 0.75), 1);
			climberSpeed = Math.min(SmartDashboard.getNumber("DB/Slider 1", 0.75), 1);

			//Check if we need to reverse the direction of the robot
			if (rightStick.getRawButton(2) || leftStick.getRawButton(2) || arcadeStick.getRawButton(2)) {
				//Check we arn't moving
				if (previousLeftSpeed > -0.3 && previousLeftSpeed < 0.3 && previousRightSpeed > -0.3 && previousRightSpeed < 0.3) {
					button2Pressed = true; //Look into weather or not this line is required
					//Check if robot is actually ready to change direction
					if (button2Pressed && ready2Change) {
						direction *= -1;
						button2Pressed = false;
						ready2Change = false;
					}
				}
			}
			else {
				ready2Change = true;
			}

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

			//Halve speed if required
			if (arcadeStick.getRawButton(5) == true || arcadeStick.getRawButton(6) == true) {
				moveValue *= 0.5;
				rotateValue *= 0.5;
			}

			//Check acceleraton limitation
			//Remove sign to avoid confusion between forward and backwards as opposed to speeding up and slowing down
			moveValue = Math.abs(moveValue);
			rotateValue = Math.abs(rotateValue);

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
			//Reapply sign
			moveValue *= moveSign;
			rotateValue *= rotateSign;

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
			//Left side
			motor1.set(leftSpeed);
			motor2.set(leftSpeed);
			motor3.set(leftSpeed);

			//Right Side
			motor4.set(rightSpeed);
			motor5.set(rightSpeed);
			motor6.set(rightSpeed);

			//Code to control the Aux Motor, this is currently the climber
			//Forwards
			if (arcadeStick.getRawButton(1)) {
				auxMotor.set(climberSpeed);
				System.out.println(Double.toString(climberSpeed));
			}
			//Backwards at quarter speed
			else if (arcadeStick.getRawButton(3)) {
				auxMotor.set(-climberSpeed/4);
				System.out.println(Double.toString(-climberSpeed/4));
			}
			//Set to 0 by default
			else {
				auxMotor.set(0);
			}

			//Calculate robot telemetary data
			//Robot current monitors
			this.leftMotorsCurrentDrawAvg.addValue(pdp.getCurrent(0) + pdp.getCurrent(1) + pdp.getCurrent(2));
			this.rightMotorsCurrentDrawAvg.addValue(pdp.getCurrent(13) + pdp.getCurrent(14) + pdp.getCurrent(15));
			this.climberMotorCurrentDrawAvg.addValue(pdp.getCurrent(12));
			//Robot speed monitors
			this.robotVelocityAvg.addValue((this.motor3.getEncPosition() + this.motor4.getEncPosition() /2) * ENCODER_RATIO);

			//Update current draw over last 500ms
			if (this.lastSystemTimeCurrent + 500 < System.currentTimeMillis()) {
				this.leftCurrent = this.leftMotorsCurrentDrawAvg.getAverage();
				this.rightCurrent = this.rightMotorsCurrentDrawAvg.getAverage();
				this.climberCurrent = this.climberMotorCurrentDrawAvg.getAverage();
				this.leftMotorsCurrentDrawAvg.reset();
				this.rightMotorsCurrentDrawAvg.reset();
				this.climberMotorCurrentDrawAvg.reset();
				this.lastSystemTimeCurrent = System.currentTimeMillis();
			}

			//Update velocities over last 250ms
			if (this.lastSystemTimeVelocity + 250 < System.currentTimeMillis()) {
				this.robotVelocity = this.robotVelocityAvg.getAverage();
				this.robotVelocityAvg.reset();
				this.lastSystemTimeVelocity = System.currentTimeMillis();
			}

			//Print telemetary/debug information to the Smart Dashboard
			SmartDashboard.putString("DB/String 0", "Direction: " + Integer.toString(direction));
			SmartDashboard.putString("DB/String 5", DriverStation.getInstance().getAlliance() + " " + Integer.toString(DriverStation.getInstance().getLocation()));
			SmartDashboard.putString("DB/String 2", "LCurrent: " + Double.toString(leftCurrent) + "A");
			SmartDashboard.putString("DB/String 7", "RCurrent: " + Double.toString(rightCurrent) + "A");
			SmartDashboard.putString("DB/String 9", "CCurrent: " + Double.toString(climberCurrent) + "A");
			SmartDashboard.putString("DB/String 1", "LSpeed: " + Double.toString(leftSpeed));
			SmartDashboard.putString("DB/String 6", "RSpeed: " + Double.toString(rightSpeed));
			SmartDashboard.putBoolean("DB/LED 0", accCode);

			// wait for a motor update time
			Timer.delay(0.005); 
		}
	}

	/**
	 * Runs during test mode
	 */
	@Override
	public void test() {
	}
}
