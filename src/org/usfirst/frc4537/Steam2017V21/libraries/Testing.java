//package org.usfirst.frc4537.Steam2017V21.libraries;
//
//import utilities.Logger;
//
//public class Testing {
//
//	private static String[] sensors = {
//			"Encoder1",
//			"Enc2",
//			"Pressure", 
//			"CurrentA",
//			"CurrentB", 
//			"SwitchA", 
//			"SwitchB"
//	};
//	static Logger logger = new Logger(sensors);
//	static DataGenerator dataGenerator = new DataGenerator();
//
//	private static Thread updateSensors = new Thread(() -> {
//		while(!Thread.interrupted()) {
//			logger.updateSensor("Encoder1", dataGenerator.encoder1);
//			logger.updateSensor("Enc2", dataGenerator.encoder2);
//			logger.updateSensor("Pressure", dataGenerator.pressure);
//			logger.updateSensor("CurrentA", dataGenerator.currentA);
//			logger.updateSensor("CurrentB", dataGenerator.currentB);
//			logger.updateSensor("SwitchA", dataGenerator.limitA);
//			logger.updateSensor("SwitchB", dataGenerator.limitB);
//		}
//	});
//
//	public static void main(String[] args) {
//		dataGenerator.stopGenerator();
//		logger.stopAutoLog();
//		dataGenerator.startGenerator();
//		updateSensors.start();
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		logger.startAutoLog();
//	}
//
//}
