package org.usfirst.frc4537.Steam2017V21.subsystems;

import org.usfirst.frc4537.Steam2017V21.RobotMap;
import org.usfirst.frc4537.Steam2017V21.commands.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class MXP extends Subsystem {
	public static DigitalInput mxpIBlack = RobotMap.mxpI1;
	public static DigitalInput mxpIWhite = RobotMap.mxpI2;
	public static DigitalInput mxpIGreen = RobotMap.mxpI4;
	public static DigitalInput mxpIYellow = RobotMap.mxpI8;
	public static DigitalOutput mxpOBlue = RobotMap.mxpOWhite;
	public static DigitalOutput mxpORed = RobotMap.mxpOBlack;
	public static int mxpValue = 0;
	public static boolean ringColour = false;
	public static boolean ringMode = false;

	public void initDefaultCommand() {
    	// Set the default command for a subsystem here.
    	// setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new mxpRead());
    }
	
	/*private static void sendData(DigitalOutput pin, int data) {
		boolean[] output = {false, false};
		if (data > 3 || data < 0) 
		if (data == 0) {output[0] = false; output[1] = false;}
		else if (data == 1) {output[0] = true; output[1] = false;}
		else if (data == 2) {output[0] = false; output[1] = true;}
		else if (data == 3) {output[0] = true; output[1] = true;}
		
		for (int i = 0; i < 2; i++) {
			pin.set(output[i]);
		}
	}*/
	
	/**
	 * 
	 * @param pin Pin to read
	 * @return Pin state
	 */
	public static boolean readPin(DigitalInput pin) {
		return pin.get();
	}
	
	/**
	 * 
	 * @param pin Pin to set
	 * @param output Output state
	 */
	public static void setPin(DigitalOutput pin, boolean output) {
		pin.set(output);
	}
	
	/**
	 * 
	 * @param i Arduino Input
	 * @return String Name
	 */
	public static String convertMXP(int i) {
		String value = "DEFAULT";
		
		switch (i) {
		case (3):	value = "HARD_RIGHT";	break;
		case (2):	value = "MED_RIGHT";	break;
		case (1):	value = "SMALL_RIGHT";	break;
		case (12):	value = "HARD_LEFT";	break;
		case (8):	value = "MED_LEFT";		break;
		case (4):	value = "SMALL_LEFT";	break;
		case (10):	value = "FWD";			break;
		case (15):	value = "STOP";			break;
		case (0):	value = "NS";			break;
		default:	value = "ERROR";		break;
		}
		
		return value;
	}
}
