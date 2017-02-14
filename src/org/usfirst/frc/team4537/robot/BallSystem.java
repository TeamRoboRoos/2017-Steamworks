package org.usfirst.frc.team4537.robot;

public class BallSystem {
	DriveBase driveBase;
	private double ballSpeed = 0.5;
	
	public BallSystem() {
		driveBase = new DriveBase();
	}
	
	public void ballIn() {
		driveBase.ballMotor7.set(ballSpeed);
		driveBase.ballMotor8.set(ballSpeed);
	}
	
	public void ballOut() {
		driveBase.ballMotor7.set(-ballSpeed);
		driveBase.ballMotor8.set(-ballSpeed);
	}
	
	public void ballDefault() {
		driveBase.ballMotor7.set(0);
		driveBase.ballMotor8.set(0);
	}
}
