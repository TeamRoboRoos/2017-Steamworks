package org.usfirst.frc.team4537.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends SampleRobot {
	DriveBase driveBase;
	Climber climber;
	BallSystem ballSystem;
	Pneumatics pneumatics;
	//FIXME Sensors sensors;

	//Averagable variables
	private AverageCalculator leftMotorsCurrentDrawAvg;
	private AverageCalculator rightMotorsCurrentDrawAvg;
	private AverageCalculator climberMotorCurrentDrawAvg;
	private AverageCalculator robotVelocityAvg;
	private AverageCalculator pressureAvg;

	public Robot() {
		driveBase = new DriveBase();
		climber = new Climber();
		ballSystem = new BallSystem();
		pneumatics = new Pneumatics();
		//FIXME sensors = new Sensors();

		leftMotorsCurrentDrawAvg = new AverageCalculator(200);
		rightMotorsCurrentDrawAvg = new AverageCalculator(200);
		climberMotorCurrentDrawAvg = new AverageCalculator(200);
		robotVelocityAvg = new AverageCalculator(100);
		pressureAvg = new AverageCalculator(100);
	}

	@Override
	public void robotInit() {
		//Start camera capture servers
		CameraServer.getInstance().startAutomaticCapture("Battery", 0);
		CameraServer.getInstance().startAutomaticCapture("Loader", 1);
		//Insert default values into DB variables
		SmartDashboard.putBoolean("DB/Button 0", driveBase.accCode);
		SmartDashboard.putBoolean("DB/Button 1", false);
		SmartDashboard.putBoolean("DB/Button 2", true);
		SmartDashboard.putNumber("DB/Slider 0", 0.75);
		SmartDashboard.putNumber("DB/Slider 1", 0.75);
		//Start compressor
		pneumatics.compressorStart();
		//Initialize gyroscope
		//FIXME sensors.gyroscope.calibrate();
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

			driveBase.drive();
			pneumatics.debug();

			//XXX Joystick Buttons
			if (driveBase.arcadeStick.getRawButton(1)) {
				climber.climbUp();
				pneumatics.compressorStop();
			}
			//Backwards at quarter speed
			else if (driveBase.arcadeStick.getRawButton(3)) {
				climber.climbDown();
			}
			//Set to 0 by default
			else {
				climber.climbDefault();
				if (pneumatics.compresserEnabled) {
					pneumatics.compressorStart();
				}
			}

			if (driveBase.arcadeStick.getRawButton(2)) {
				driveBase.changeDirection();
			}
			else {
				driveBase.button2Pressed = false;
			}

			if (SmartDashboard.getBoolean("DB/Button 1", false)) {
				pneumatics.toggleRamp();
				SmartDashboard.putBoolean("DB/Button 1", false);
			}
			else {
				pneumatics.db1Pressed = false;
			}

			if (driveBase.arcadeStick.getRawButton(4)) {
				driveBase.halfSpeed();
			}

			if (driveBase.arcadeStick.getRawButton(5)) {
				ballSystem.ballIn();
			}
			else if (driveBase.arcadeStick.getRawButton(16)) {
				ballSystem.ballOut();
			}
			//Set to 0 by default
			else {
				ballSystem.ballIODefault();
			}

			if (driveBase.arcadeStick.getRawButton(6)) {
				pneumatics.toggleFlippers();
			}
			else {
				pneumatics.button6Pressed = false;
			}

			if (SmartDashboard.getBoolean("DB/Button 2", false) && !pneumatics.compresserEnabled) {
				pneumatics.compressorStart();
				pneumatics.compresserEnabled = true;
			}
			else {
				pneumatics.compresserEnabled = false;
				pneumatics.compressorStop();
			}

			if (driveBase.arcadeStick.getRawButton(13)) {
				ballSystem.ballUp();
			}
			//Set to 0 by default
			else if (driveBase.arcadeStick.getRawButton(12)) {
				ballSystem.ballDown();
			}
			else {
				ballSystem.ballUDDefault();
			}

			//Calculate robot telemetry data
			//Robot current monitors
			this.leftMotorsCurrentDrawAvg.addValue(driveBase.pdp.getCurrent(0) + driveBase.pdp.getCurrent(1) + driveBase.pdp.getCurrent(2));
			this.rightMotorsCurrentDrawAvg.addValue(driveBase.pdp.getCurrent(13) + driveBase.pdp.getCurrent(14) + driveBase.pdp.getCurrent(15));
			this.climberMotorCurrentDrawAvg.addValue(driveBase.pdp.getCurrent(12));
			this.pressureAvg.addValue(pneumatics.pressureSensor.getValue());

			//Update current draw over last 500ms
			if (driveBase.lastSystemTimeCurrent + 500 < System.currentTimeMillis()) {
				driveBase.leftCurrent = leftMotorsCurrentDrawAvg.getAverage();
				driveBase.rightCurrent = rightMotorsCurrentDrawAvg.getAverage();
				driveBase.climberCurrent = climberMotorCurrentDrawAvg.getAverage();
				pneumatics.pressure = pressureAvg.getAverage();
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
			SmartDashboard.putString("DB/String 4", "Pressure: " + Double.toString(Functions.floor(Functions.pressure(pneumatics.pressure))) + "psi");
			SmartDashboard.putBoolean("DB/LED 0", driveBase.accCode);

			//Wait for a motor update time
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
