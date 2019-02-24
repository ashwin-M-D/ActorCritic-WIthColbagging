package Bagging;

import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import neuralnet.Actor;

public class Actor2 extends Actor {
	private double PeformanceScore = 0D;
	private String name;
	public Actor2(int option, String name) {
		super(option, name);
		this.name = name;
		this.Log("Actor2() name = " + this.name);
		try {
			this.readPerformance(name);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void displaySpecifications() {
		this.Log("displaySpecifications() ===== Actor name " + this.name + " =====");
		super.displaySpecifications();
	}
	
	public double getPeformanceScore() {
		return PeformanceScore;
	}
	public void setPeformanceScore(double peformanceScore) {
		PeformanceScore = peformanceScore;
	}
	
	void readPerformance(String name) throws IOException {
		String performance_filename = "performance-" + name + ".dat";
		FileReader fr = new FileReader(performance_filename);
		BufferedReader buf = new BufferedReader(fr);
		String str = buf.readLine();
		this.setPeformanceScore(Double.parseDouble(str));
		this.Log("readPerformance() "+this.name+": " + this.getPeformanceScore());
		fr.close();
	}
	
	public void load() {
		
	}
	
	void Log(String log) {
		System.out.println("Actor2." + log);
	}
}
