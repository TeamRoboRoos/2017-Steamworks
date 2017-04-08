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
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc4537.Steam2017V21.Robot;
import org.usfirst.frc4537.Steam2017V21.subsystems.MXP;
import org.usfirst.frc4537.Steam2017V21.subsystems.Pneumatics;
import org.usfirst.frc4537.Steam2017V21.subsystems.Telemetery;

/**
 *
 */
public class AutoVision extends Command {
	private final int HARD_RIGHT = 3;
	private final int MED_RIGHT = 2;
	private final int SMALL_RIGHT = 1;
	private final int HARD_LEFT = 12;
	private final int MED_LEFT = 8;
	private final int SMALL_LEFT = 4;
	private final int FWD = 10;
	private final int STOP = 15;
	private final int NS = 0;

	private int mxpVal = 0;
	private long lastTime = 0;
	private int state = 0;
	private int side = 0;

	private double gyroAngle = 0.0;
	private double gyroAngleLast = 0.0;
	private double encDistAvg = 0.0;
	private double encDistAvgLast = 0.0;

	private final double OFFSET_TURN = 0.25;
	private final double DRIVE_SPEED_VIS = 0.55; //was 0.65
	private final double DRIVE_SPEED = 0.9; //was 0.9
	private final double TURN_SPEED = 0.8;
	private final double TURN_HELPER = 0.1;

	private final double TURN_ANGLE = 50.0; //was 60
	private final double DRIVE_DISTANCE = 2.3; //was 1.85

	public AutoVision(int param) {
		side = param;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		System.out.println("State: " + state + " Side: " + side);

		mxpVal = MXP.mxpValue;
		encDistAvg = ((Telemetery.getEncL() + Telemetery.getEncR())/2);
		gyroAngle = Telemetery.gyroGetAngle();

		if (state == 0) { //Set time for reference
			lastTime = System.currentTimeMillis();
			encDistAvgLast = encDistAvg;
			gyroAngleLast = gyroAngle;
			state = 1;
		}

		else if (state == 1) { //Drive straight
			if (side != 1) { //If we are in the middle we don't need to drive forward first
				Robot.driveBase.arcadeDrive(-DRIVE_SPEED, -(gyroAngle-gyroAngleLast)/10);
				//if (lastTime+1500 <= System.currentTimeMillis()) { //Condition to stop moving
				if (encDistAvgLast+DRIVE_DISTANCE <= encDistAvg) { //Condition to stop moving
					gyroAngleLast = gyroAngle;
					lastTime = System.currentTimeMillis();
					state = 2; //FIXMe
				}
			}
			else {
				lastTime = System.currentTimeMillis();
				gyroAngleLast = gyroAngle;
				state = 2;
			}
		}

		else if (state == 2) { //Turn if left or right
			if (side == 0) { //Turn right
				Robot.driveBase.arcadeDrive(TURN_HELPER, TURN_SPEED);
				if (gyroAngleLast+TURN_ANGLE <= gyroAngle) { //Condition to stop turning
					gyroAngleLast = gyroAngle;
					state = 3;
				}
			}
			else if (side == 2) { //Turn left
				Robot.driveBase.arcadeDrive(TURN_HELPER, -TURN_SPEED);
				if (gyroAngleLast-TURN_ANGLE >= gyroAngle) { //Condition to stop turning
					gyroAngleLast = gyroAngle;
					state = 3;
				}
			}
			else if (side == 1) { //Do nothing because we are in the middle and move on
				gyroAngleLast = gyroAngle;
				state = 3;
			}
			/*if (lastTime+2000 <= System.currentTimeMillis()) { //Condition to stop turning
				state = 3;
			}*/
		}

		if (state == 3) { //Guide using vision
			if (mxpVal == HARD_RIGHT) {
				Robot.driveBase.arcadeDrive(-DRIVE_SPEED_VIS, 0.35 + OFFSET_TURN);
				System.out.println("Vision: " + "HR");
				gyroAngleLast = gyroAngle;
			}
			else if (mxpVal == MED_RIGHT) {
				Robot.driveBase.arcadeDrive(-DRIVE_SPEED_VIS, 0.3 + OFFSET_TURN);
				System.out.println("Vision: " + "MR");
				gyroAngleLast = gyroAngle;
			}
			else if (mxpVal == SMALL_RIGHT) {
				Robot.driveBase.arcadeDrive(-DRIVE_SPEED_VIS, 0.25 + OFFSET_TURN);
				System.out.println("Vision: " + "LR");
				gyroAngleLast = gyroAngle;
			}
			else if (mxpVal == HARD_LEFT) {
				Robot.driveBase.arcadeDrive(-DRIVE_SPEED_VIS, -0.35 + OFFSET_TURN);
				System.out.println("Vision: " + "HL");
				gyroAngleLast = gyroAngle;
			}
			else if (mxpVal == MED_LEFT) {
				Robot.driveBase.arcadeDrive(-DRIVE_SPEED_VIS, -0.3 + OFFSET_TURN);
				System.out.println("Vision: " + "ML");
				gyroAngleLast = gyroAngle;
			}
			else if (mxpVal == SMALL_LEFT) {
				Robot.driveBase.arcadeDrive(-DRIVE_SPEED_VIS, -0.25 + OFFSET_TURN);
				System.out.println("Vision: " + "LL");
				gyroAngleLast = gyroAngle;
			}
			else if (mxpVal == STOP) { //Stop and move on
				Robot.driveBase.arcadeDrive(0.0, 0.0);
				System.out.println("Vision: " + "STOP");
				gyroAngleLast = gyroAngle;
				state = 4;
			}
			else { //Keep driving in case of invalid response, this either indicated tracking is lost, we should be driving forward or we have a problem
				//Robot.driveBase.arcadeDrive(-DRIVE_SPEED_VIS, 0.0 + OFFSET_TURN);
				Robot.driveBase.arcadeDrive(-DRIVE_SPEED_VIS, -(gyroAngle-gyroAngleLast)/10);
				//if (lastTime+1500 <= System.currentTimeMillis()) { //Condition to stop moving
				//if (encDistAvgLast+DRIVE_DISTANCE <= encDistAvg) { //Condition to stop moving
				//	gyroAngleLast = gyroAngle;
				//	lastTime = System.currentTimeMillis();
				//	state = 2; //FIXMe
				//}
				System.out.println("Vision: " + "NS/DF");
			}
		}

		else if (state == 4) { //Toggle flippers and record last time
			lastTime = System.currentTimeMillis();
			Pneumatics.flippersToggle();
			state = 5;
		}

		else if (state == 5) { //Wait for actuation time
			Robot.driveBase.arcadeDrive(0.0, 0.0);
			if (lastTime+500 <= System.currentTimeMillis()) {
				encDistAvgLast = encDistAvg;
				state = 6;
			}
		}

		else if (state == 6) { //Drive backwards
			Robot.driveBase.arcadeDrive(0.75, 0.0);
			//if (lastTime+500+3000 <= System.currentTimeMillis()) {
			if (encDistAvgLast-0.5 >= encDistAvg) {
				state = 7;
			}
		}
		else { //Drive nowhere if we don't know what we are doing
			Robot.driveBase.arcadeDrive(0.0, 0.0);
		}
	}


	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		if (state == 7) {
			return true;
		}
		else {
			return false;
		}
	}

	// Called once after isFinished returns true
	protected void end() {
		Robot.driveBase.arcadeDrive(0.0, 0.0);
		state = 0;
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
} 
