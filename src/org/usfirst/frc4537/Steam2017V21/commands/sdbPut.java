// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc4537.Steam2017V21.commands;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc4537.Steam2017V21.Robot;
import org.usfirst.frc4537.Steam2017V21.RobotMap;
import org.usfirst.frc4537.Steam2017V21.subsystems.*;

import com.ctre.CANTalon;

/**
 *
 */
public class sdbPut extends Command {
	private CANTalon leftEncoder = RobotMap.leftEncoder;
	private CANTalon rightEncoder = RobotMap.rightEncoder;
	private DigitalInput climbSensor = RobotMap.climberLimitSwitch;
	private double pressure = 0;
	private int lEncVal = 0;
	private int rEncVal = 0;
	private boolean climbValue = false;
	private boolean rampValue = false;
	private boolean flippersValue = false;

    public sdbPut() {
    	requires(Robot.telemetery);
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	pressure = Telemetery.pressureGet();
    	lEncVal = leftEncoder.getEncPosition();
    	rEncVal = rightEncoder.getEncPosition();
    	climbValue = climbSensor.get();
    	rampValue = Pneumatics.rampGetState();
    	flippersValue = Pneumatics.flippersGetState();
    	SmartDashboard.putNumber("Pressure", pressure);
    	SmartDashboard.putNumber("LeftEncoder", lEncVal);
    	SmartDashboard.putNumber("Right Encoder", rEncVal);
    	SmartDashboard.putBoolean("Climber", climbValue);
    	SmartDashboard.putBoolean("Ramp", rampValue);
    	SmartDashboard.putBoolean("Flippers", flippersValue);
        Telemetery.telemeteryDebug();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
