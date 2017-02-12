package org.usfirst.frc.team4537.robot;

import edu.wpi.first.wpilibj.AnalogInput;

public class Gears {
	AnalogInput pressureSensor;
	public double pressure = 0;
	
	public Gears() {
		pressureSensor = new AnalogInput(1);
	}
	
}
