package org.usfirst.frc.team4537.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

public class Sensors {
	DriveBase driveBase;
	
	ADXRS450_Gyro gyroscope;
	Accelerometer accRio;

	//CANTalon leftEncoder;
	//CANTalon rightEncoder;

	public Sensors() {
		driveBase = new DriveBase();
		//Setup gyro and accelerometers
		accRio = new BuiltInAccelerometer(Accelerometer.Range.k4G);
		gyroscope = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
		//leftEncoder = driveBase.dlMotor3;
		//rightEncoder = driveBase.drMotor4;
	}

	public void randomGyroAndAccStuff() {
		//Print telemetry/debug information to the Smart Dashboard/Console
		//System.out.println("Pressure: " + Double.toString(PID.floor((pressure-258.2)/4.348)) + "psi"); //y = 4.348x + 258.2 x=(y-258.2)/4.348

		//System.out.print("Gyro A: " + Double.toString(gyroscope.getAngle()));
		//System.out.println(" Gyro R: " + Double.toString(gyroscope.getRate()));

		//System.out.println(Double.toString(accelerometer.getZ()));
		//System.out.println(accelerometer);

		//System.out.println("AccR: " + Double.toString(Math.sqrt(Math.pow(accRio.getX(), 2) + Math.pow(accRio.getY(), 2) + Math.pow(accRio.getZ(), 2))));

		/*this.accelerometerAll = this.accelerometer.getAccelerations();
		if (this.accelerometerAll != null) {
			System.out.print("Acce X: " + Double.toString(accelerometerAll.XAxis));
			System.out.print(" Acce Y: " + Double.toString(accelerometerAll.YAxis));
			System.out.println(" Acce Z: " + Double.toString(accelerometerAll.ZAxis));
		}
		else {
			System.out.println("Can't Read Acc.");
		}
		 */
	}
}
