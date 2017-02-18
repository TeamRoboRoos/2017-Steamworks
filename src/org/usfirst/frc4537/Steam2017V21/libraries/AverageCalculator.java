package org.usfirst.frc4537.Steam2017V21.libraries;

public class AverageCalculator {

	private double[] values;
	
	private int tail = 0;
	private int head = 0;
	
	/**
	 * Creates an array of size 100 to store values to be averaged
	 */
	public AverageCalculator() {
		this.values = new double[100];
	}
	
	/**
	 * Creates an array of size size to store values to be averaged
	 * @param size
	 */
	public AverageCalculator(int size) {
		this.values = new double[size];
	}
	
	/**
	 * Adds a value to the array to be averaged by getAverage()
	 * @param value
	 */
	public void addValue(double value) {
    	this.values[this.head] = value;
    	
    	this.head++;
    	
    	if (this.head == this.values.length) {
    		this.head = 0;
    	}
    	
    	if (this.head == this.tail) {
    		this.tail++;
        	if (this.tail == this.values.length) {
        		this.tail = 0;
        	}
    	}
	}
	
	/**
	 * getAverage returns the average value of all stored values
	 * @return average (double)
	 */
	public double getAverage() {    	
		// If averaging, do this ...
    	// Possibility 1: tail < head - add from tail to head
    	// Possibility 2: head < tail - add from tail to end, beginning to head
    	// Possibility 3: head == tail - do nothing
    	
    	double avgValue = -1;
    	
    	if (this.tail < this.head) {
    		double overallValue = 0.0;
    		for (int i = this.tail; i < this.head; i++) {
    			overallValue += this.values[i];
    		}
    		avgValue = overallValue / (this.head - this.tail);
    	}
    	
    	else if (this.tail > this.head) {
    		double overallValue = 0.0;
    		for (int i = this.tail - 1; i < this.values.length; i++) {
    			overallValue += this.values[i];
    		}
    		for (int i = 0; i < this.head; i++) {
    			overallValue += this.values[i];
    		}
    		avgValue = overallValue / ((0 + this.head)+(this.values.length - this.tail + 1)); 
    	}
    	
    	return avgValue;
	}
	
	/**
	 * Clears array of values
	 */
	public void reset() {
		this.head = 0;
		this.tail = 0;
	}
}
