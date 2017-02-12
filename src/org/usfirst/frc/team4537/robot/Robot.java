package org.usfirst.frc.team4537.robot;

import edu.wpi.first.wpilibj.SampleRobot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.ADXL345_SPI;
import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.ADXL362.AllAxes;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;

import java.lang.Math;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
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

	DriveBase driveBase;
	Climber climber;
	Gears gears;
	
	//Averagable variables
	public AverageCalculator leftMotorsCurrentDrawAvg = new AverageCalculator(200);
	public AverageCalculator rightMotorsCurrentDrawAvg = new AverageCalculator(200);
	public AverageCalculator climberMotorCurrentDrawAvg = new AverageCalculator(200);
	public AverageCalculator robotVelocityAvg = new AverageCalculator(100);
	public AverageCalculator pressureAvg = new AverageCalculator(100);
	
	public Robot() {
		driveBase = new DriveBase();
		climber = new Climber();
		gears = new Gears();
	}

	@Override
	public void robotInit() {
		//Start camera capture servers
		CameraServer.getInstance().startAutomaticCapture("Battery", 0);
		CameraServer.getInstance().startAutomaticCapture("Loader", 1);
		//Insert default values into DB variables
		SmartDashboard.putBoolean("DB/Button 0", driveBase.accCode);
		SmartDashboard.putNumber("DB/Slider 0", 0.75);
		SmartDashboard.putNumber("DB/Slider 1", 0.75);
		//Initialize gyroscope
		//gyroscope.calibrate();
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
			
			//Calculate robot telemetry data
			//Robot current monitors
			this.leftMotorsCurrentDrawAvg.addValue(driveBase.pdp.getCurrent(0) + driveBase.pdp.getCurrent(1) + driveBase.pdp.getCurrent(2));
			this.rightMotorsCurrentDrawAvg.addValue(driveBase.pdp.getCurrent(13) + driveBase.pdp.getCurrent(14) + driveBase.pdp.getCurrent(15));
			this.climberMotorCurrentDrawAvg.addValue(driveBase.pdp.getCurrent(12));
			this.pressureAvg.addValue(gears.pressureSensor.getValue());
			//Robot speed monitors
			this.robotVelocityAvg.addValue((driveBase.motor3.getEncPosition() + driveBase.motor4.getEncPosition() /2) * driveBase.ENCODER_RATIO);
			
			driveBase.drive();

			
			if (driveBase.arcadeStick.getRawButton(1)) {
				climber.climbUp();
			}
			//Backwards at quarter speed
			else if (driveBase.arcadeStick.getRawButton(3)) {
				climber.climbDown();
			}
			//Set to 0 by default
			else {
				climber.climbDefault();
			}
	    	
			if (driveBase.arcadeStick.getRawButton(2)) {
				driveBase.changeDirection();
			}
			
	    	if (driveBase.arcadeStick.getRawButton(3)) {
	    		driveBase.driveStraightPID();
	    	}
			
	    	if (driveBase.arcadeStick.getRawButton(5)) {
	    		driveBase.resetDrivePID();
	    	}
	    	
			if (driveBase.arcadeStick.getRawButton(6)) {
				driveBase.halfSpeed();
			}

	    	
			//Update current draw over last 500ms
			if (driveBase.lastSystemTimeCurrent + 500 < System.currentTimeMillis()) {
				driveBase.leftCurrent = leftMotorsCurrentDrawAvg.getAverage();
				driveBase.rightCurrent = rightMotorsCurrentDrawAvg.getAverage();
				driveBase.climberCurrent = climberMotorCurrentDrawAvg.getAverage();
				gears.pressure = pressureAvg.getAverage();
				leftMotorsCurrentDrawAvg.reset();
				rightMotorsCurrentDrawAvg.reset();
				climberMotorCurrentDrawAvg.reset();
				pressureAvg.reset();
				driveBase.lastSystemTimeCurrent = System.currentTimeMillis();
			}

			//Update velocities over last 250ms
			if (driveBase.lastSystemTimeVelocity + 250 < System.currentTimeMillis()) {
				driveBase.robotVelocity = robotVelocityAvg.getAverage();
				robotVelocityAvg.reset();
				driveBase.lastSystemTimeVelocity = System.currentTimeMillis();
			}
	    	
	    	
	    	
			SmartDashboard.putString("DB/String 0", "Direction: " + Integer.toString(driveBase.direction));
			SmartDashboard.putString("DB/String 5", DriverStation.getInstance().getAlliance() + " " + Integer.toString(DriverStation.getInstance().getLocation()));
			SmartDashboard.putString("DB/String 2", "LCurrent: " + Double.toString(driveBase.leftCurrent) + "A");
			SmartDashboard.putString("DB/String 7", "RCurrent: " + Double.toString(driveBase.rightCurrent) + "A");
			SmartDashboard.putString("DB/String 9", "CCurrent: " + Double.toString(driveBase.climberCurrent) + "A");
			SmartDashboard.putString("DB/String 1", "LSpeed: " + Double.toString(driveBase.leftSpeed));
			SmartDashboard.putString("DB/String 6", "RSpeed: " + Double.toString(driveBase.rightSpeed));
			SmartDashboard.putBoolean("DB/LED 0", driveBase.accCode);

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
