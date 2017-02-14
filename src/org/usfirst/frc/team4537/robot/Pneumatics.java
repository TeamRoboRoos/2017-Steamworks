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
	
	private final int PCM_PORT = 10;
	public boolean db1 = false;
	public boolean button6Pressed = false;
	
	public Pneumatics() {
		pressureSensor = new AnalogInput(1);
		compressor = new Compressor(PCM_PORT);
		ramp = new Solenoid(PCM_PORT, 0);
		flippers = new Solenoid(PCM_PORT, 1);
	}
	
	public void startCompressor() {
		compressor.start();
	}
	
	public void stopCompressor() {
		compressor.stop();
	}
	
	public void toggleRamp() {
		if (!db1) {
			ramp.set(!ramp.get());
			db1 = true;
		}
	}
	
	public void toggleFlippers() {
		if (!button6Pressed) {
			flippers.set(!flippers.get());
			button6Pressed = true;
		}
	}
	
	public void debug() {
		SmartDashboard.putBoolean("DB/LED 1", ramp.get());
		SmartDashboard.putBoolean("DB/LED 2", flippers.get());
	}
}
