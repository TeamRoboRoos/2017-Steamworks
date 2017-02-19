package org.usfirst.frc4537.Steam2017V21;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

public class Config {
	//Drive joystick and axis 
	public static final int JOYSTICK_DRIVE = 0;
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	public static final int AXIS_Z = 2;
	public static final int AXIS_T = 3;
	//Joystick power index, Squared, Cubed etc
	public static final int POWER_INDEX = 2;
	//Joystick Deadzones
	public static final double DEADZONE_M = 0.01;
	public static final double DEADZONE_R = 0.01;
	//Joystick rotation and linear sensitivities
	public static final double JOYSTICK_ROTATION_SENSITIVITY = 0.75;
	public static final double JOYSTICK_LINEAR_SENSITIVITY = 1;

	//Button board and buttons
	public static final int BUTTON_BOARD = 1;
	public static final int BUTTON_CLIMB_UP = 1;
	public static final int BUTTON_RAMP_TOGGLE = 2;
	public static final int BUTTON_FLIPPERS_TOGGLE = 3;

	//Motor IDs
	public static final int MOTOR_DL_1 = 1;
	public static final int MOTOR_DL_2 = 2;
	public static final int MOTOR_DL_3 = 3;
	public static final int MOTOR_DR_4 = 4;
	public static final int MOTOR_DR_5 = 5;
	public static final int MOTOR_DR_6 = 6;
	public static final int BALL_MOTOR_7 = 7;
	public static final int BALL_MOTOR_8 = 8;
	public static final int CLIMB_MOTOR_9 = 9;

	//Robot speed limit
	public static final double SPEED_MULTIPLIER = 0.75;
	public static final double CLIMBER_SPEED = 0.75;

	//PDP IDs and ports
	public static final int PDP_CAN_PORT = 0;

	//Pneumatic IDs and ports
	public static final int PCM_CAN_PORT = 10;
	public static final int PCM_FLIPPER_PORT = 1;
	public static final int PCM_RAMP_PORT = 0;
	
	//PID control init variables
	public static final double PID_DRIVE_P = 0.1;
	public static final double PID_DRIVE_I = 1.0;
	public static final double PID_DRIVE_D = 1.0;
	public static final double PID_DRIVE_S = 0.1;
	
	//Camera server variables
	public static final String CAM_0_NAME = "0";
	public static final String CAM_0_PATH = "/dev/video0";
	public static final String CAM_1_NAME = "1";
	public static final String CAM_1_PATH = "/dev/video1";
	
	//Gyro and accelerometer variables
	public static final SPI.Port GYRO_PORT = SPI.Port.kOnboardCS0;
	public static final Accelerometer.Range ACCELEROMETER_RANGE = Accelerometer.Range.k4G;

	//Analog Sensors
	public static final int ANI_PRESSURE = 0;
	
	//Digital Sensors
	public static final int DGI_CLIMBER_SWITCH = 0;
	public static final int DGI_MXP_BLACK = 10; //MPX PIN 11 IN
	public static final int DGI_MXP_WHITE = 11; //MPX PIN 13 IN
	public static final int DGI_MXP_GREEN = 12; //MPX PIN 15 IN
	public static final int DGI_MXP_YELLOW = 13; //MPX PIN 17 IN
	public static final int DGI_MXP_BLUE = 14; //MPX PIN 19 IN
	public static final int DGO_MXP_RED = 15; //MPX PIN 21 OUT
}
