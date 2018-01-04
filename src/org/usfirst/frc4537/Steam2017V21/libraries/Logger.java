package org.usfirst.frc4537.Steam2017V21.libraries;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Logger {

	private String logFileName;

	private SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss:SSSS"); //Log entry time
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'_'HH-mm-ss"); //File name

	private ArrayList<String> sensors = new ArrayList<String>();
	private ArrayList<String> data = new ArrayList<String>();
	private long autoLogDelay = 250;
	private String logDirectory = "C:\\Users\\bryce\\Desktop\\FRC\\logs\\"; //"\\home\\lvuser\\logs\\";

	/**
	 * @param sensorsin Array of sensors to log
	 */
	public Logger(String[] sensorsin) {
		//Create Sensors
		for(int i=0;i < sensorsin.length; i++) {
			addSensor(sensorsin[i]);
		}

		//Create log file
		logFileName = logDirectory+"log-"+sdf2.format(System.currentTimeMillis())+".csv";
		
		//Create header
		String header = "Time";
		for(int i=0; sensors.size()>i; i++) {
			header += ","+sensors.get(i);
		}

		//Write to file
		System.out.println(logFileName);
		System.out.println(header);
		appendFile(logFileName, header);
	}

	/**
	 * Thread for auto logging
	 */
	private Thread autoLogger = new Thread(() -> {
		long timer = 0;
		long delay = autoLogDelay;
		while(!Thread.interrupted()) {
			if(System.currentTimeMillis() > timer+delay) {
				log();
				timer = System.currentTimeMillis();
			}
		}
	});

	/**
	 * Opens file from path
	 * @param path of file
	 * @return <b>FileWriter</b> object from path
	 */
	private boolean appendFile(String path, String data) {
		FileWriter file = null;
		BufferedWriter buffer = null;
		PrintWriter writer= null;
		try {
			file = new FileWriter(path, true);
			buffer = new BufferedWriter(file);
			writer = new PrintWriter(buffer);
			writer.println(data);
			return true;
		}
		catch(IOException e) {
			return false;
		}
		finally {
			try{
				if(buffer != null) {
					buffer.close();
				}
			}
			catch (IOException e) {}
		}
	}

	/**
	 * Adds a sensor to logger
	 * @param sensor to add
	 * @return <b>true</b> if added or already exists, <b>false</b> if failed
	 */
	private boolean addSensor(String sensor) {
		if(!sensors.contains(sensor)) {
			sensors.add(sensor);
			data.add("");
			if(sensors.contains(sensor) && data.contains("")) return true;
			else return false;
		}
		else return true;
	}

	/**
	 * Removes a sensor from the logger
	 * @param sensor to remove
	 * @return <b>true</b> if removed or never existed, <b>false</b> if failed
	 * @deprecated
	 */
	public boolean removeSensor(String sensor) {
		if(sensors.contains(sensor)) {
			data.remove(sensor.indexOf(sensor));
			sensors.remove(sensor);
			if(!sensors.contains(sensor)) return true;
			else return false;
		}
		return true;
	}

	/**
	 * Updates sensor data
	 * @param sensor to update
	 * @param value to update sensor with
	 * @return <b>true</b> if sensor exists or was created, <b>false</b> if failed 
	 */
	public boolean updateSensor(String sensor, double value) {
		return updateData(sensor, Double.toString(value));
	}

	/**
	 * Updates sensor data
	 * @param sensor to update
	 * @param value to update sensor with
	 * @return <b>true</b> if sensor exists or was created, <b>false</b> if failed 
	 */
	public boolean updateSensor(String sensor, String value) {
		return updateData(sensor, value);
	}

	/**
	 * Updates sensor data
	 * @param sensor to update
	 * @param value to update sensor with
	 * @return <b>true</b> if sensor exists or was created, <b>false</b> if failed
	 */
	public boolean updateSensor(String sensor, boolean value) {
		return updateData(sensor, Boolean.toString(value));
	}

	/**
	 * Updates sensor data
	 * @param sensor to update
	 * @param value to update sensor with
	 * @return <b>true</b> if sensor exists or was created, <b>false</b> if failed 
	 */
	private boolean updateData(String sensor, String value) {
		if(sensors.contains(sensor)) {
			data.set(sensors.indexOf(sensor), value);
			return true;
		}
		else {
			addSensor(sensor);
			return updateData(sensor, value);
		}
	}

	/**
	 * 
	 * @return <b>ArrayList</b> of sensors
	 */
	public ArrayList<String> getSensors() {
		return sensors;
	}

	/**
	 * 
	 * @return <b>ArrayList</b> of all sensor data
	 */
	public ArrayList<String> getData() {
		return data;
	}

	/**
	 * Gets sensor data from storage
	 * @param sensor to get data from
	 * @return <b>String</b> data from sensor
	 */
	public String getData(String sensor) {
		if(sensors.contains(sensor)) {
			return data.get(sensors.indexOf(sensor));
		}
		else return null;
	}

	/**
	 * Starts auto logger thread with custom delay
	 * @param delay to set in milliseconds
	 * @return <b>true</b> if started, <b>false</b> if already running
	 */
	public boolean startAutoLog(long delay) {
		autoLogDelay = delay;
		return startAutoLog();
	}

	/**
	 * Starts auto logger with default delay of 250ms
	 * @return <b>true</b> if started, <b>false</b> if already running
	 */
	public boolean startAutoLog() {
		if(!autoLogger.isAlive()) {
			autoLogger.start();
			return true;
		}
		else return false;
	}

	/**
	 * Stops auto logger
	 */
	public void stopAutoLog() {
		autoLogger.interrupt();
	}

	/**
	 * Logs to file
	 */
	private void log() {
		String log = sdf1.format(System.currentTimeMillis());

		for(int i=0; sensors.size()>i; i++) {
			log += ","+data.get(i);
		}
		if(log.length() > 0) {
			System.out.println(log);
			appendFile(logFileName, log);
		}
	}
}
