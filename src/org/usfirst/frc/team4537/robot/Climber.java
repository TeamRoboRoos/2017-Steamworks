package org.usfirst.frc.team4537.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
	DriveBase driveBase;
	private double climberSpeed = 0.75;

	public Climber() {
		driveBase = new DriveBase();
	}

	public void setSpeed() {
		climberSpeed = Math.min(SmartDashboard.getNumber("DB/Slider 1", 0.75), 1);
		climberSpeed = Math.max(SmartDashboard.getNumber("DB/Slider 1", 0.75), -1);
	}
	public void climbUp() {
		setSpeed();
		driveBase.climbMotor9.set(climberSpeed);
		System.out.println(Double.toString(climberSpeed));
	}

	public void climbDown() {
		setSpeed();
		driveBase.climbMotor9.set(-climberSpeed/4);
		System.out.println(Double.toString(-climberSpeed/4));
	}

	public void climbDefault() {
		driveBase.climbMotor9.set(0);
	}
}
