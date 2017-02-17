package org.usfirst.frc.team4537.robot;

public class Config {
	public static final int JOYSTICK_DRIVE = 0;
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	public static final int AXIS_Z = 2;
	public static final int AXIS_T = 3;

	//These shouldn't need to be changed
	public static final int MOTOR_DL_1 = 1;
	public static final int MOTOR_DL_2 = 2;
	public static final int MOTOR_DL_3 = 3;
	public static final int MOTOR_DR_4 = 4;
	public static final int MOTOR_DR_5 = 5;
	public static final int MOTOR_DR_6 = 6;
	public static final int BALL_MOTOR_7 = 7;
	public static final int BALL_MOTOR_8 = 8;
	public static final int CLIMB_MOTOR_9 = 9;

	public static final double DEADZONE_M = 0.01;
	public static final double DEADZONE_R = 0.01;

	public static final double ACCELERATION_MOVE_MAX = 0.01;
	public static final double DECELERATION_MOVE_MAX = 0.05;
	public static final double ACCELERATION_ROTATE_MAX = 0.05;

	public static final int POWER_INDEX = 2;
	public static double speedMultiplier = 0.5;
	public static final double JOYSTICK_ROTATION_SENSITIVITY = 0.4;
	public static final double JOYSTICK_LINEAR_SENSITIVITY = 1;

	public static final int PCM_CAN_PORT = 10;
	public static final int PCM_FLIPPER_PORT = 1;
	public static final int PCM_RAMP_PORT = 0;

}
