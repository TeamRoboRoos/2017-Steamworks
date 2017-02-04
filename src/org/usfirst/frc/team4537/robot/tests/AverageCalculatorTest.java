package org.usfirst.frc.team4537.robot.tests;

import org.usfirst.frc.team4537.robot.*;

public class AverageCalculatorTest {

	public AverageCalculatorTest() {
		this.testValues(100);
		this.testBoundary(100);
		this.testReset(10);
	}
	
	private void testValues(int size) {
		System.out.println("Trying " + size + " array with " + size + " values ...");
		
		AverageCalculator myTest = new AverageCalculator(size);
		
		double totalValues = 0;
		for (int i = 1; i < size + 1; i++) {
			myTest.addValue(i);
			totalValues += i;
		}
		
		double actualAverage = totalValues / size;
		double calculatedAverage = myTest.getAverage();
		
		if (actualAverage == calculatedAverage) {
			System.out.println("Test Passed");
		}
		else {
			System.out.println("Test Failed. Expected " + actualAverage + " got " + calculatedAverage);
		}
		
	}
	
	private void testBoundary(int size) {
		System.out.println("Trying " + size + " array with " + (size + 1) + " values ...");
		
		AverageCalculator myTest = new AverageCalculator(size);
		
		double totalValues = 0;
		for (int i = 1; i < size + 2; i++) {
			myTest.addValue(i);
			totalValues += i;
		}
		
		totalValues = totalValues - 1;
		
		double actualAverage = totalValues / size;
		double calculatedAverage = myTest.getAverage();
		
		if (actualAverage == calculatedAverage) {
			System.out.println("Test Passed");
		}
		else {
			System.out.println("Test Failed. Expected " + actualAverage + " got " + calculatedAverage);
		}
		
	}
	

	private void testReset(int size) {
		System.out.println("Trying reset ...");
		
		AverageCalculator myTest = new AverageCalculator(size);
		
		double totalValues = 0;
		for (int i = 1; i < size + 1; i++) {
			myTest.addValue(i);
			totalValues += i;
		}
		
		myTest.reset();
		
		totalValues = 0;
		for (int i = 1; i < size + 1; i++) {
			myTest.addValue(i);
			totalValues += i;
		}
		
		double actualAverage = totalValues / size;
		double calculatedAverage = myTest.getAverage();
		
		if (actualAverage == calculatedAverage) {
			System.out.println("Test Passed");
		}
		else {
			System.out.println("Test Failed. Expected " + actualAverage + " got " + calculatedAverage);
		}
		
	}
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Starting Tests");
		System.out.println("*************************");
		
		new AverageCalculatorTest();
	}

}
