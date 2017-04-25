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

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc4537.Steam2017V21.commands.*;
import org.usfirst.frc4537.Steam2017V21.libraries.Functions;
import org.usfirst.frc4537.Steam2017V21.subsystems.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

    Command autonomousCommand;
    @SuppressWarnings("rawtypes") //XXX
	SendableChooser autoChooser;

    public static OI oi;
    public static Camera camera;
    public static Climber climber;
    public static DriveBase driveBase;
    public static Pickup pickup;
    public static Telemetery telemetery;
    public static Vision vision;
    public static Pneumatics pneumatics;
    public static MXP mxp;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" }) //XXX
	public void robotInit() {
    	RobotMap.init();
    	pneumatics = new Pneumatics();
        camera = new Camera();
        climber = new Climber();
        driveBase = new DriveBase();
        pickup = new Pickup();
        telemetery = new Telemetery();
        vision = new Vision();
        mxp = new MXP();

        // OI must be constructed after subsystems. If the OI creates Commands
        //(which it very likely will), subsystems are not guaranteed to be
        // constructed yet. Thus, their requires() statements may grab null
        // pointers. Bad news. Don't move it.
        oi = new OI();
        
        //Setup auto chooser
        autoChooser = new SendableChooser();
        autoChooser.addDefault("Base Line", new AutoBaseLine());
        autoChooser.addDefault("Vision Left", new AutoVision(0));
        autoChooser.addDefault("Vision Middle", new AutoVision(1));
        autoChooser.addDefault("Vision Right", new AutoVision(2));
        autoChooser.addDefault("Middle NoVis", new AutoVision(4));
        SmartDashboard.putData("Auto Chooser", autoChooser);

        SmartDashboard.putData("Compressor Start", new compressorSet(1));
        SmartDashboard.putData("Compressor Stop", new compressorSet(0));
        //SmartDashboard.putData("Compressor Toggle", new compressorSet(2));

        //Initialize camera capture servers
        for (int i = 0; i <= Config.CAM_NAMES.length-1; i++) {
        	UsbCamera camObj = CameraServer.getInstance().startAutomaticCapture(Config.CAM_NAMES[i], Config.CAM_PATHS[i]);
        	camObj.setResolution(Config.CAM_RESOLUTION[0], Config.CAM_RESOLUTION[1]);
        	camObj.setFPS(Config.CAM_FPS);
        	camObj.setExposureManual(50);
        	camObj.setWhiteBalanceManual(50);
        }
        
        //Apply calibration to pressure sensor
        Telemetery.pressureCal = Functions.statreg(Config.CAL_PRESSURE);

        //Instantiate the command used for the autonomous period
        autonomousCommand = new AutonomousCommand();

    }

    /**
     * This function is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    public void disabledInit(){

    }

    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    public void autonomousInit() {
        // schedule the autonomous command (example)
        autonomousCommand = (Command) autoChooser.getSelected();
    	if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
