package org.usfirst.frc.team4537.robot;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
	private Victor auxMotor;
	private double climberSpeed = 0;

	public Climber() {
		auxMotor = new Victor(0);
	}

	public void setSpeed() {
		climberSpeed = Math.min(SmartDashboard.getNumber("DB/Slider 1", 0.75), 1);
	}
	public void climbUp() {
		setSpeed();
		auxMotor.set(-climberSpeed);
		System.out.println(Double.toString(climberSpeed));
	}

	public void climbDown() {
		setSpeed();
		auxMotor.set(climberSpeed/4);
		System.out.println(Double.toString(-climberSpeed/4));
	}

	public void climbDefault() {
		auxMotor.set(0);
	}
}
