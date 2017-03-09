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
	//Joystick buttons
	public static final int HALVE_SPEED_TOGGLE = 1;
	public static final int CHANGE_DIRECTION_TOGGLE = 2;
	//Joystick power index, Squared, Cubed etc
	public static final int POWER_INDEX = 2;
	//Joystick Deadzones
	public static final double DEADZONE_M = 0.001;
	public static final double DEADZONE_R = 0.001;
	//Joystick rotation and linear sensitivities and modifiers
	public static final double JOYSTICK_LINEAR_SENSITIVITY = 1;
	public static final double JOYSTICK_ROTATION_SENSITIVITY = 0.8;
	public static final double JOYSTICK_HALF_MOVE_MULTIPLIER = 0.5;
	public static final double JOYSTICK_HALF_ROTATE_MULTIPLIER = 0.6;

	//Button board and axis
	public static final int BUTTON_BOARD = 1;
	//Button board buttons
	public static final int BUTTON_CLIMB_UP = 3;
	public static final int BUTTON_CLIMB_RESET = 2;
	public static final int BUTTON_RAMP_TOGGLE = 4;
	public static final int BUTTON_FLIPPERS_TOGGLE = 1;
	public static final int BUTTON_PARTY_OFF = 8;

	//Motor IDs
	public static final int MOTOR_DL_1 = 1;
	public static final int MOTOR_DL_2 = 2;
	public static final int MOTOR_DL_3 = 3;
	public static final int MOTOR_DR_4 = 4;
	public static final int MOTOR_DR_5 = 5;
	public static final int MOTOR_DR_6 = 6;
	public static final int CLIMB_MOTOR_1 = 7;
	public static final int CLIMB_MOTOR_2 = 9;

	//Robot speed limit
	public static final double SPEED_MULTIPLIER = 1;
	public static final double CLIMBER_SPEED = 1;

	//PDP IDs and ports
	public static final int PDP_CAN_PORT = 0;

	//Pneumatic IDs and ports
	public static final int PCM_CAN_PORT = 10;
	public static final int PCM_FLIPPER_PORT = 1;
	public static final int PCM_RAMP_PORT = 0;
	
	//PID control init variables
	//Move PID values
	public static final double PID_MOVE_P = 0.1;
	public static final double PID_MOVE_I = 1.0;
	public static final double PID_MOVE_D = 1.0;
	public static final double PID_MOVE_S = 0.1;
	//Turn PID values
	public static final double PID_TURN_P = 0.15;
	public static final double PID_TURN_I = 1.0;
	public static final double PID_TURN_D = 1.0;
	public static final double PID_TURN_S = 0.1;
	
	//Camera server variables
	public static final String[] CAM_NAMES = {"Climber", "Gear"};
	public static final String[] CAM_PATHS = {"/dev/video0", "/dev/video1"};
	public static final int[] CAM_RESOLUTION = {320, 240}; //{Width, Height}
	public static final int CAM_FPS = 15;
	
	//Gyro and accelerometer variables
	public static final SPI.Port GYRO_PORT = SPI.Port.kOnboardCS0;
	public static final Accelerometer.Range ACCELEROMETER_RANGE = Accelerometer.Range.k4G;

	//Analog Sensors
	public static final int ANI_PRESSURE = 0;
	
	//Digital Sensors
	//Climber Limit Switch
	public static final int DGI_CLIMBER_SWITCH = 0;
	//MXP IO
	public static final int DGI_MXP_1 = 12; 	//MPX PIN 15 IN		//Green
	public static final int DGI_MXP_2 = 13; 	//MPX PIN 17 IN		//Yellow
	public static final int DGI_MXP_4 = 14;		//MPX PIN 19 IN		//Blue
	public static final int DGI_MXP_8 = 15;		//MPX PIN 21 IN		//Red
	public static final int DGO_MXP_WHITE = 11;	//MPX PIN 13 OUT	//White
	public static final int DGO_MXP_BLACK = 10;	//MPX PIN 11 OUT	//Black
	
	//Calibration
	//Pressure 
	public static final double[][] CAL_PRESSURE = {{0, 410.38}, {25, 814.744}, {50, 1206.064}, {75, 1606.08}, {100, 2012.1832}};
	
	/**
	 * 
	 */
}
