package org.usfirst.frc4537.Steam2017V21.subsystems;

import org.usfirst.frc4537.Steam2017V21.RobotMap;
import org.usfirst.frc4537.Steam2017V21.commands.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class MXP extends Subsystem {
	public static DigitalInput mxpIBlack = RobotMap.mxpIBlack;
	public static DigitalInput mxpIWhite = RobotMap.mxpIWhite;
	public static DigitalInput mxpIGreen = RobotMap.mxpIGreen;
	public static DigitalInput mxpIYellow = RobotMap.mxpIYellow;
	public static DigitalInput mxpIBlue = RobotMap.mxpIBlue;
	public static DigitalOutput mxpORed = RobotMap.mxpORed;
	public static int mxpValue = 0;
    
	public void initDefaultCommand() {
    	// Set the default command for a subsystem here.
    	// setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new mxpRead());
    }
	
	public static boolean readPin(DigitalInput pin) {
		return pin.get();
	}
	
	public static void setPin(DigitalOutput pin, boolean output) {
		pin.set(output);
	}
}
