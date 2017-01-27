package org.usfirst.frc.team4537.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;

import com.ctre.CANTalon;

import edu.wpi.cscore.VideoSource;
import edu.wpi.first.wpilibj.CameraServer;
import java.lang.Math;

import edu.wpi.first.wpilibj.Joystick;
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
    
	private final int MOTOR_1 = 1;
	private final int MOTOR_2 = 2;
	private final int MOTOR_3 = 3;
	private final int MOTOR_4 = 4;
	private final int MOTOR_5 = 5;
	private final int MOTOR_6 = 6;
	 
	CANTalon motor1;
	CANTalon motor2;
	CANTalon motor3;
	CANTalon motor4;
	CANTalon motor5;
	CANTalon motor6;
	private Victor auxMotor;
	
	//Tank definitions
	private double rightControl = 0;
	private double leftControl = 0;
	private double leftSpeed = 0;
	private double rightSpeed = 0;
	private double leftSign = 1;
	private double rightSign = 1;
	//Arcade definitions
	private double moveValue = 0;
	private double rotateValue = 0;
	private double moveSign = 0;
	private double rotateSign = 0;
	private double mDeadzone = 0.05;
	private double rDeadzone = 0.075;

	private int direction = 1;
	private double powerThingy = 3;
	//Speed limiter
	private double speedMultiplier = 0.5;
	
	private double previousLeftSpeed = 0;
	private double previousRightSpeed = 0;
	private boolean button2Pressed = false;
	private boolean ready2Change = false;
	private boolean arcade = false;
	
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
    	
    	
        //myRobot = new RobotDrive(motor1, motor2, motor3, motor4, motor5, motor6);
        //myRobot.setExpiration(0.1);
        leftStick = new Joystick(0);
        rightStick = new Joystick(1);
        arcadeStick = new Joystick(2);
    }

	@Override
    public void robotInit() {
		CameraServer.getInstance().startAutomaticCapture("Battery", 0);
		CameraServer.getInstance().startAutomaticCapture("Loader", 1);
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
            //myRobot.tankDrive(leftStick, rightStick); //drive with arcade style (use right stick)
        	
        	//Get drive functions from the dashboard
        	arcade = SmartDashboard.getBoolean("DB/Button 0", false);
        	speedMultiplier = SmartDashboard.getNumber("DB/Slider 0", 0.75)/5;
        	SmartDashboard.putNumber("DB/Slider 1", speedMultiplier);
        	
        	if (rightStick.getRawButton(2) || leftStick.getRawButton(2) || arcadeStick.getRawButton(2)) {
        		if(previousLeftSpeed > -0.3 && previousLeftSpeed < 0.3 && previousRightSpeed > -0.3 && previousRightSpeed < 0.3) {
        			button2Pressed = true;
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
        	
        	if (arcade == false) {
        		leftControl = rightStick.getRawAxis(1);
        		rightControl = leftStick.getRawAxis(1);
        		if(direction > 0) {
        			leftControl = leftStick.getRawAxis(1);
        			rightControl = rightStick.getRawAxis(1);
        		}
        		
        		leftSign = 1;
        		rightSign = 1;
        		if(powerThingy/2 == Math.floor(powerThingy/2)) {
        			if(leftControl < 0) leftSign = -1;
        			if(rightControl < 0) rightSign = -1;
        		}
        		
        		leftSpeed = Math.pow(leftControl,  powerThingy) * direction * leftSign * (1 - leftStick.getRawAxis(2));
        		rightSpeed = Math.pow(rightControl,  powerThingy) * direction * rightSign * (1 - leftStick.getRawAxis(2));
        		previousLeftSpeed = leftSpeed;
        		previousRightSpeed = rightSpeed;
        	}
        	
        	if (arcade == true) {
        		moveValue = arcadeStick.getRawAxis(1) * direction;
        		rotateValue = arcadeStick.getRawAxis(3);
        		
        		moveSign = 1;
        		rotateSign = 1;
        		if(powerThingy/2 == Math.floor(powerThingy/2)) {
        			if(moveValue < 0) moveSign = -1;
        			if(rotateValue < 0) rotateSign = -1;
        		}
        		
        		moveValue = Math.pow(moveValue, powerThingy) * moveSign;
        		rotateValue = Math.pow(rotateValue, powerThingy) * rotateSign;
        		
        		//Joystick deadzones
        		if (moveValue < mDeadzone && moveValue > -mDeadzone) moveValue = 0;
        		if (rotateValue < rDeadzone && rotateValue > -rDeadzone) rotateValue = 0;
        		
        		if (arcadeStick.getRawButton(5) == true || arcadeStick.getRawButton(6) == true) {
        			moveValue *= 0.5;
            		rotateValue *= 0.5;
            	}
        		
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
        		leftSpeed *= (1 - arcadeStick.getRawAxis(2));
        		rightSpeed *= (1 - arcadeStick.getRawAxis(2));
        	}
        	
        	//Set Maximum and Minimum
        	leftSpeed = Math.max(leftSpeed, -1);
        	leftSpeed = Math.min(leftSpeed, 1);
        	rightSpeed = Math.max(rightSpeed, -1);
        	rightSpeed = Math.min(rightSpeed, 1);
        	
        	//Apple speed limit
        	leftSpeed *= speedMultiplier;
        	rightSpeed *= speedMultiplier;
        	
        	//For all that is good in this world, DO NOT touch or breathe on these
        	//Left side
        	motor1.set(leftSpeed);
        	motor2.set(leftSpeed);
        	motor3.set(leftSpeed);
        	
        	//Right Side
           	motor4.set(rightSpeed);
        	motor5.set(rightSpeed);
        	motor6.set(rightSpeed);
            
        	//Aux Motor
        	if(leftStick.getRawButton(1)) {
        		auxMotor.set((1-rightStick.getRawAxis(2))*0.75);
        	}
        	else if(rightStick.getRawButton(1)) {
        		auxMotor.set(-(1-rightStick.getRawAxis(2))*0.75);
        	}
        	else {
        		auxMotor.set(0);
        	}
        	
        	System.out.println("Aux: " + Double.toString(1-rightStick.getRawAxis(2)*0.75));
        	SmartDashboard.putString("DB/String 0", "Direction: " + Integer.toString(direction));
        	SmartDashboard.putBoolean("DB/LED 0", SmartDashboard.getBoolean("DB/Button 0", false));
        	SmartDashboard.putString("DB/String 1", "LSpeed: " + Double.toString(leftSpeed));
        	SmartDashboard.putString("DB/String 6", "RSpeed: " + Double.toString(rightSpeed));
        	
            Timer.delay(0.005); // wait for a motor update time
        }
    }

    /**
     * Runs during test mode
     */
	@Override
    public void test() {
    }
}
