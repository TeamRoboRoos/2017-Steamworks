package org.usfirst.frc4537.Steam2017V21.libraries;

public class DataGenerator {

	public int encoder1=0, encoder2=0;
	public double pressure=0.0;
	public double currentA=2.5, currentB=5.0;
	public boolean limitA=false, limitB=false;

	private long t1, t2;
	
	private Thread dataThread = new Thread(() -> {
		while (!Thread.interrupted()) {
			if(System.currentTimeMillis() > t1+100) {
				t1 = System.currentTimeMillis();
				encoder1 += Math.ceil(Math.random()*3);
				encoder2 += Math.ceil(Math.random()*3-1);
				pressure += Math.random()*10-2.5;
				pressure = Math.max(Math.min(pressure, 120.0), 0.0);
				currentA = 2.5 + Math.random()*2-1;
				currentB = 5.0 + Math.random()*4-2;
			}
			if(System.currentTimeMillis() > t2 + 2000) {
				if(Math.random() < 0.10) limitA = !limitA;
				if(Math.random() < 0.02) limitB = !limitB;
			}
		}
	});

	public void startGenerator() {
		if(!dataThread.isAlive()) {
			dataThread.start();
		}
	}
	
	public void stopGenerator() {
		dataThread.interrupt();
	}
}
