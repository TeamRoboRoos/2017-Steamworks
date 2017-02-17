package org.usfirst.frc.team4537.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveBase {
	//FIXME Sensors sensors;

	Joystick arcadeStick;
	PowerDistributionPanel pdp;

	//These shouldn't need to be changed
	CANTalon dlMotor1;
	CANTalon dlMotor2;
	CANTalon dlMotor3;
	CANTalon drMotor4;
	CANTalon drMotor5;
	CANTalon drMotor6;
	CANTalon ballMotor7;
	CANTalon ballMotor8;
	CANTalon climbMotor9;

	//Arcade definitions
	public double leftSpeed = 0;
	public double rightSpeed = 0;
	private double moveValue = 0;
	private double rotateValue = 0;
	private double moveSign = 1;
	private double rotateSign = 1;
	private boolean preserveSign = false;
	public int direction = -1; //-1 is true, 1 is false

	//Acceleration limiter
	private double previousMoveValue = 0;
	private double previousRotateValue = 0;
	private double currentMV = 0;
	//private double maxRotateDeceleration = 0.05; //Currently isn't used

	//Direction change variables
	private double previousLeftSpeed = 0;
	private double previousRightSpeed = 0;
	public boolean button2Pressed = false;
	public boolean accCode = true;

	//Robot telemetry
	public double leftCurrent = 0;
	public double rightCurrent = 0;
	public double climberCurrent = 0;
	private double turnAngle = 0;

	public double robotVelocity = 0;
	//private double shooterVelocity = 0;
	public long lastSystemTimeCurrent = 0;
	public long lastSystemTimeVelocity = 0;
	public long lastSystemTimePID = 0;

	//private AverageCalculator shooterVelocityAvg = new AverageCalculator(100);
	//Ticks per metre: 12557

	//PID Loops
	private PID turnLeftPID = new PID(0.001, 0.001, 0.001, 0.01);
	private PID turnRightPID = new PID(0.001, 0.001, 0.001, 0.01);
	private PID drivePID = new PID(0.1, 1, 1, 0.1);

	public DriveBase() {
		//FIXME sensors = new Sensors();
		arcadeStick = new Joystick(Config.JOYSTICK_DRIVE);
		//For all that is good in this world, DO NOT touch or breathe on these
		//Left motors
		dlMotor1 = new CANTalon(Config.MOTOR_DL_1); //frontLeftMotor
		dlMotor1.setExpiration(0.1);
		dlMotor1.setInverted(true);
		dlMotor2 = new CANTalon(Config.MOTOR_DL_2); //middleLeftMotor
		dlMotor2.setExpiration(0.1);
		dlMotor2.setInverted(true);
		dlMotor3 = new CANTalon(Config.MOTOR_DL_3); //rearLeftMotor
		dlMotor3.setExpiration(0.1);
		dlMotor3.setInverted(true);

		//Right motors
		drMotor4 = new CANTalon(Config.MOTOR_DR_4); //frontRightMotor
		drMotor4.setExpiration(0.1);
		drMotor5 = new CANTalon(Config.MOTOR_DR_5); //middleRightMotor
		drMotor5.setExpiration(0.1);
		drMotor6 = new CANTalon(Config.MOTOR_DR_6); //rearRightMotor
		drMotor6.setExpiration(0.1);

		//Ball grabber motors and climber motor
		//FIXME A ball motor may need to be inverted
		ballMotor7 = new CANTalon(Config.BALL_MOTOR_7);
		ballMotor7.setExpiration(0.1);
		ballMotor7.setInverted(true);
		ballMotor8 = new CANTalon(Config.BALL_MOTOR_8);
		ballMotor8.setExpiration(0.1);
		climbMotor9 = new CANTalon(Config.CLIMB_MOTOR_9);
		climbMotor9.setExpiration(0.1);

		//Setup PDP
		pdp = new PowerDistributionPanel();
	}

	public void drive() {		
		//Get drive functions from the dashboard
		accCode = SmartDashboard.getBoolean("DB/Button 0", false);
		Config.speedMultiplier = Math.min(SmartDashboard.getNumber("DB/Slider 0", 0.75), 1);

		//Get move values from joystick
		moveValue = arcadeStick.getRawAxis(Config.AXIS_Y) * Config.JOYSTICK_LINEAR_SENSITIVITY * direction;
		rotateValue = arcadeStick.getRawAxis(Config.AXIS_Z) * Config.JOYSTICK_ROTATION_SENSITIVITY;

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
		if (Config.POWER_INDEX/2 == Math.floor(Config.POWER_INDEX/2)) {
			preserveSign = true;
		}

		//Bring control inputs to the power of powerIndex
		moveValue = Math.pow(moveValue, Config.POWER_INDEX);
		rotateValue = Math.pow(rotateValue, Config.POWER_INDEX);
		//Reapply sign if needed
		if (preserveSign) {
			moveValue *= moveSign;
			rotateValue *= rotateSign;
		}


		//Joystick deadzones to remove jitter from controller
		if (moveValue < Config.DEADZONE_M && moveValue > -Config.DEADZONE_M) {
			moveValue = 0;
		}
		if (rotateValue < Config.DEADZONE_R && rotateValue > -Config.DEADZONE_R) {
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
		leftSpeed *= (1 - arcadeStick.getRawAxis(Config.AXIS_T));
		rightSpeed *= (1 - arcadeStick.getRawAxis(Config.AXIS_T));

		//Store speed outputs for next iteration
		previousLeftSpeed = leftSpeed;
		previousRightSpeed = rightSpeed;

		//Cap maximum and minimum speed outputs
		leftSpeed = Math.max(leftSpeed, -1);
		leftSpeed = Math.min(leftSpeed, 1);
		rightSpeed = Math.max(rightSpeed, -1);
		rightSpeed = Math.min(rightSpeed, 1);

		//Apply speed limit
		leftSpeed *= Config.speedMultiplier;
		rightSpeed *= Config.speedMultiplier;

		/*
		 * This block of code will send the speed outputs to the motors
		 * It should under no circumstances need to be changed
		 * It can cause the motors to fight and possibly break the gearbox
		 */
		//System.out.println("LS: " + leftSpeed);
		//Left side
		dlMotor1.set(leftSpeed);
		dlMotor2.set(leftSpeed);
		dlMotor3.set(leftSpeed);

		//System.out.println("RS: " + rightSpeed);
		//Right Side
		drMotor4.set(rightSpeed);
		drMotor5.set(rightSpeed);
		drMotor6.set(rightSpeed);
	}



	public void resetDrivePID() {
		//FIXME sensors.leftEncoder.setEncPosition(0);
		//FIXME sensors.rightEncoder.setEncPosition(0);
		currentMV = 0;
		turnLeftPID.init();
		turnRightPID.init();
		drivePID.init();
	}

	public void changeDirection() {
		//Check we aren't moving
		if (previousLeftSpeed > -0.3 && previousLeftSpeed < 0.3 && previousRightSpeed > -0.3 && previousRightSpeed < 0.3) {
			if (!button2Pressed) {
				direction *= -1;
				button2Pressed = true;
			}
		}
	}

	public void halfSpeed() {
		moveValue *= 0.5;
		rotateValue *= 0.5;
	}

	public void driveStraightPID() {
		turnAngle = SmartDashboard.getNumber("DB/Slider 2", 0) - SmartDashboard.getNumber("DB/Slider 3", 0);


		/*if (turnAngle != 0) {
			turnAngle = ((0.56 * Math.PI) / (360 / turnAngle));
		}*/

		turnAngle = -arcadeStick.getRawAxis(Config.AXIS_T) * 2;
		turnAngle *= 12557;
		//turnAngle = 2 * 12557;

		this.turnLeftPID.setTarget(turnAngle);
		this.turnRightPID.setTarget(turnAngle);

		//FIXME leftSpeed = this.turnLeftPID.calculate(this.sensors.leftEncoder.getEncPosition());// /12557
		//FIXME rightSpeed = this.turnRightPID.calculate(-this.sensors.rightEncoder.getEncPosition());

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
		if (moveValue > previousMoveValue + Config.ACCELERATION_MOVE_MAX) {
			moveValue = previousMoveValue + Config.ACCELERATION_MOVE_MAX;
		}
		//Check if move speed is decreasing
		if (moveValue < previousMoveValue - Config.DECELERATION_MOVE_MAX) {
			moveValue = previousMoveValue - Config.DECELERATION_MOVE_MAX;
		}
		//Check if rotate speed is decreasing
		if (rotateValue > previousRotateValue + Config.ACCELERATION_ROTATE_MAX) {
			rotateValue = previousRotateValue + Config.ACCELERATION_ROTATE_MAX;
		}

		//Store previous motion inputs for next iteration
		previousMoveValue = moveValue;
		previousRotateValue = rotateValue;
	}

}
