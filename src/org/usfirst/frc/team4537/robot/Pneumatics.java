package org.usfirst.frc.team4537.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pneumatics {
	AnalogInput pressureSensor;
	Compressor compressor;
	Solenoid ramp;
	Solenoid flippers;
	public double pressure = 0;
	
	public boolean button5Pressed = false;
	public boolean button6Pressed = false;
	
	public Pneumatics() {
		pressureSensor = new AnalogInput(1);
		compressor = new Compressor(7);
		ramp = new Solenoid(7, 0);
		flippers = new Solenoid(7, 1);
	}
	
	public void startCompressor() {
		compressor.setClosedLoopControl(true);
	}
	
	public void stopCompressor() {
		compressor.setClosedLoopControl(false);
	}
	
	public void toggleRamp() {
		if (!button5Pressed) {
			ramp.set(!ramp.get());
			SmartDashboard.putBoolean("DB/LED 1", ramp.get());
			button5Pressed = true;
		}
	}
	
	public void toggleFlippers() {
		if (!button6Pressed) {
			flippers.set(!flippers.get());
			SmartDashboard.putBoolean("DB/LED 2", flippers.get());
			button6Pressed = true;
		}
	}
	
	public void debug() {
		System.out.println(compressor.getClosedLoopControl());
		System.out.println("RAMP "+ramp.get());
		System.out.println("FLIP "+flippers.get());
	}
}
