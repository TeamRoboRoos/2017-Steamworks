// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc4537.Steam2017V21;

import org.usfirst.frc4537.Steam2017V21.commands.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.*;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc4537.Steam2017V21.subsystems.*;


/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.
	// Joystick stick = new Joystick(port);
	// Button button = new JoystickButton(stick, buttonNumber);

	// There are a few additional built in buttons you can use. Additionally,
	// by subclassing Button you can create custom triggers and bind those to
	// commands the same as any other Button.

	//// TRIGGERING COMMANDS WITH BUTTONS
	// Once you have a button, it's trivial to bind it to a button in one of
	// three ways:

	// Start the command when the button is pressed and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenPressed(new ExampleCommand());

	// Run the command while the button is being held down and interrupt it once
	// the button is released.
	// button.whileHeld(new ExampleCommand());

	// Start the command when the button is released  and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenReleased(new ExampleCommand());


	public Joystick arcade;
	public JoystickButton toggleDirectionButton;
	public JoystickButton toggleHalfSpeedButton;
	public Joystick buttonBoard;
	public JoystickButton climbUpButton;
	public JoystickButton climbResetButton;
	public JoystickButton rampToggleButton;
	public JoystickButton flippersToggleButton;
	public JoystickButton compOnButton;
	public JoystickButton compOffButton;
	public JoystickButton ringModeToggleButton;
	public JoystickButton ringColourToggleButton;

	public OI() {
		arcade = new Joystick(Config.JOYSTICK_DRIVE);
		buttonBoard = new Joystick(Config.BUTTON_BOARD);

		toggleHalfSpeedButton = new JoystickButton(arcade,Config.HALVE_SPEED_TOGGLE);
		toggleHalfSpeedButton.whenPressed(new halfDriveSpeedEnable());
		toggleHalfSpeedButton.whenReleased(new halfDriveSpeedDisable());

		toggleDirectionButton = new JoystickButton(arcade,Config.CHANGE_DIRECTION_TOGGLE);
		toggleDirectionButton.whenPressed(new changeDirection());

		climbUpButton = new JoystickButton(buttonBoard, Config.BUTTON_CLIMB_UP);
		climbUpButton.whileHeld(new climbUp());

		climbResetButton = new JoystickButton(buttonBoard, Config.BUTTON_CLIMB_RESET);
		climbResetButton.whenPressed(new climbReset());

		rampToggleButton = new JoystickButton(buttonBoard, Config.BUTTON_RAMP_TOGGLE);
		rampToggleButton.whenPressed(new rampToggle());

		flippersToggleButton = new JoystickButton(buttonBoard, Config.BUTTON_FLIPPERS_TOGGLE);
		flippersToggleButton.whenPressed(new flippersToggle());

		compOnButton = new JoystickButton(buttonBoard, Config.BUTTON_COMP_ON);
		compOnButton.whenPressed(new compressorSet(1));
		
		compOffButton = new JoystickButton(buttonBoard, Config.BUTTON_COMP_OFF);
		compOffButton.whenPressed(new compressorSet(0));

		ringModeToggleButton = new JoystickButton(buttonBoard, Config.BUTTON_RING_MODE_TOGGLE);
		ringModeToggleButton.whenPressed(new ringSet(0));
		
		ringColourToggleButton = new JoystickButton(buttonBoard, Config.BUTTON_RING_COLOUR_TOGGLE);
		ringColourToggleButton.whenPressed(new ringSet(1));


		// SmartDashboard Buttons
		/* SmartDashboard.putData("Autonomous Command", new AutonomousCommand());
        SmartDashboard.putData("cameraStart", new cameraStart());
        SmartDashboard.putData("climb", new climbUp());
        SmartDashboard.putData("compressorStart", new compressorStart());
        SmartDashboard.putData("compressorStop", new compressorStop());
        SmartDashboard.putData("gearsLoad", new gearsLoad());
        SmartDashboard.putData("pickup", new pickup());
        SmartDashboard.putData("sendStatus", new sendStatus());
        SmartDashboard.putData("visionMode", new visionMode());
        SmartDashboard.putData("visionPin", new visionPin()); */
	}

	public Joystick getButtonBoard() {
		return buttonBoard;
	}

	public Joystick getArcade() {
		return arcade;
	}
}

