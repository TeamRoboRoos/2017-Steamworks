package org.usfirst.frc.team4537.robot;

public class BallSystem {
	DriveBase driveBase;
	
	private double ballSpeed = 0.4;
	
	public BallSystem() {
		driveBase = new DriveBase();
	}
	
	public void ballIn() {
		driveBase.ballMotor7.set(ballSpeed);
	}
	
	public void ballOut() {
		driveBase.ballMotor7.set(-ballSpeed);
	}
	
	public void ballUp() {
		driveBase.ballMotor8.set(ballSpeed);
	}
	
	public void ballDown() {
		driveBase.ballMotor8.set(-ballSpeed);
	}
	
	public void ballUDDefault() {
		driveBase.ballMotor8.set(0);
	}
	
	public void ballIODefault() {
		driveBase.ballMotor7.set(0);
	}
}
