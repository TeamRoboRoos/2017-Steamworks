
// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc4537.Steam2017V21.subsystems;

import org.usfirst.frc4537.Steam2017V21.Config;
import org.usfirst.frc4537.Steam2017V21.RobotMap;
import org.usfirst.frc4537.Steam2017V21.commands.*;
import org.usfirst.frc4537.Steam2017V21.libraries.PID;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;


/**
 *
 */
public class DriveBase extends Subsystem {
	private final CANTalon CANTalonLeft1 = RobotMap.dlMotor1;
	//private final CANTalon CANTalonLeft2 = RobotMap.dlMotor2;
	//private final CANTalon CANTalonLeft3 = RobotMap.dlMotor3;
	private final CANTalon CANTalonRight4 = RobotMap.drMotor4;
	//private final CANTalon CANTalonRight5 = RobotMap.drMotor5;
	//private final CANTalon CANTalonRight6 = RobotMap.drMotor6;

	private PID movePID;
	private PID turnPID;
	private double previousMV = 0;
	private double previousRV = 0;

	private double leftSpeed = 0;
	private double rightSpeed = 0;
	private int direction = -1;
	private boolean speedHalved = false;
	private double moveValue = 0;
	private double rotateValue = 0;
	private double moveSign = 1;
	private double rotateSign = 1;

	//private double leftCurrent = 0;
	//private double rightCurrent = 0;
	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public DriveBase() {
		movePID = new PID(Config.PID_MOVE_P, Config.PID_MOVE_I, Config.PID_MOVE_D, Config.PID_MOVE_S);
		turnPID = new PID(Config.PID_TURN_P, Config.PID_TURN_I, Config.PID_TURN_D, Config.PID_TURN_S);
	}

	public void initDefaultCommand() {

		setDefaultCommand(new drive(0, 0));

		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());

	}

	public void arcadeDrive(double speed, double turn){
		moveValue = speed * direction;
		rotateValue = turn;
		
		//Joystick deadzones to remove jitter from the joystick
		if (moveValue < Config.DEADZONE_M && moveValue > -Config.DEADZONE_M) {
			moveValue = 0;
		}
		if (rotateValue < Config.DEADZONE_R && rotateValue > -Config.DEADZONE_R) {
			rotateValue = 0;
		}

		moveSign = -1;
		if (moveValue < 0) {
			moveSign = 1;
		}

		rotateSign=1;
		if (rotateValue < 0) {
			rotateSign = -1;
		}
		
		if (speedHalved) {
			moveValue *= Config.JOYSTICK_HALF_MOVE_MULTIPLIER;
			rotateValue *= Config.JOYSTICK_HALF_ROTATE_MULTIPLIER;
		}

		moveValue = Math.pow(moveValue, Config.POWER_INDEX);
		rotateValue = Math.pow(rotateValue, Config.POWER_INDEX);

		//Check acceleraton limitation
		moveValue = Math.abs(moveValue) * moveSign;
		rotateValue = Math.abs(rotateValue) * rotateSign;

		//Use a PID for acceleration control
		movePID.setTarget(moveValue);
		moveValue = movePID.calculate(previousMV);
		previousMV = moveValue;
		
		turnPID.setTarget(rotateValue);
		rotateValue = turnPID.calculate(previousRV);
		previousRV = rotateValue;

		//Mathy arcadey stuffy
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

		//Apply speed limit
		leftSpeed *= Config.SPEED_MULTIPLIER;
		rightSpeed *= Config.SPEED_MULTIPLIER;

		//Set Maximum and Minimum
		leftSpeed = Math.max(leftSpeed, -1);
		leftSpeed = Math.min(leftSpeed, 1);
		rightSpeed = Math.max(rightSpeed, -1);
		rightSpeed = Math.min(rightSpeed, 1);
		
		//System.out.println("LeftSpeed: " + leftSpeed + " RightSpeed: " + rightSpeed);

		//For all that is good in this world, DO NOT touch or breathe on these
		//the speed controllers must have the same amount otherwise it will die 
		//Left side
		this.CANTalonLeft1.set(leftSpeed);
		//this.CANTalonLeft2.set(leftSpeed);
		//this.CANTalonLeft3.set(leftSpeed);

		//Right Side
		this.CANTalonRight4.set(rightSpeed);
		//this.CANTalonRight5.set(rightSpeed);
		//this.CANTalonRight6.set(rightSpeed);

		//Calculate robot drive power draw
		//leftCurrent = pdp.getCurrent(0) + pdp.getCurrent(1) + pdp.getCurrent(2);
		//rightCurrent = pdp.getCurrent(13) + pdp.getCurrent(14) + pdp.getCurrent(15);

		Timer.delay(0.005); // wait for a motor update time
	}
	public void changeDirection() {
		direction *= -1;
	}
	public void halfSpeedSet(boolean value) {
		speedHalved = value;
	}
}