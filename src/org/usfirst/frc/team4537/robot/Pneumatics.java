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

	public boolean db1Pressed = false;
	public boolean button6Pressed = false;

	public Pneumatics() {
		pressureSensor = new AnalogInput(1);
		compressor = new Compressor(Config.PCM_CAN_PORT);
		ramp = new Solenoid(Config.PCM_CAN_PORT, Config.PCM_RAMP_PORT);
		flippers = new Solenoid(Config.PCM_CAN_PORT, Config.PCM_FLIPPER_PORT);
	}

	public void startCompressor() {
		compressor.start();
	}

	public void stopCompressor() {
		compressor.stop();
	}

	public void toggleRamp() {
		if (!db1Pressed) {
			ramp.set(!ramp.get());
			db1Pressed = true;
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
