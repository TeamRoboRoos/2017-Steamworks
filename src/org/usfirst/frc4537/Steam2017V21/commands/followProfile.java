package org.usfirst.frc4537.Steam2017V21.commands;

import org.usfirst.frc4537.Steam2017V21.Robot;
import org.usfirst.frc4537.Steam2017V21.subsystems.*;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class followProfile extends Command {
	
	private double[][][] profile;

    public followProfile(String profilePath) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveBase);
    	
    	profile = Telemetery.unpackProfile(profilePath);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
