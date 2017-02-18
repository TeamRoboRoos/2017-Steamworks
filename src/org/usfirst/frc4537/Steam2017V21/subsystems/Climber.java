// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc4537.Steam2017V21.subsystems;

import org.usfirst.frc4537.Steam2017V21.Config;
import org.usfirst.frc4537.Steam2017V21.RobotMap;
import org.usfirst.frc4537.Steam2017V21.commands.*;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;


/**
 *
 */
public class Climber extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    //private final Encoder quadratureEncoder = RobotMap.climberQuadratureEncoder;
    //private final DigitalInput limitSwitch = RobotMap.climberLimitSwitch;
    // private final CANTalon cANTalon = RobotMap.climberCANTalon;
    private final static CANTalon auxMotor = RobotMap.climbMotor9;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
    	setDefaultCommand(new climbStop());
    }
    
    public static void climbUp() {
    	auxMotor.set(Config.CLIMBER_SPEED);
    }
    
    public static void climbDown() {
    	auxMotor.set(-Config.CLIMBER_SPEED/4);
    }
    
    public static void climbStop() {
    	auxMotor.set(0);
    }
}

