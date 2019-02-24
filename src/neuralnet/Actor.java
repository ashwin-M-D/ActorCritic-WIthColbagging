package neuralnet;

import java.io.File;
import java.io.FileReader;
//import java.nio.file.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import neuralnet.NeuralNetActor;
import neuralnet.NeuronLayer;

public class Actor {
	private final boolean DEBUG = false;
	private final int ReadPreviousParameters = 0;
    NeuronLayer layer1 = new NeuronLayer();
    NeuralNetActor net = new NeuralNetActor(layer1);
    double[] previous_inputs=null;
    double previous_output=0;
    Scanner keyScanner;
    private String filename="";
    double[][] krnel=null;
    double[] wts=null, cts=null;
    
    public Actor(int InitOption, String name) { // constructor 1 (original)
    	filename = "actor-" + name + ".dat";
    	File file = new File(filename);
    	if(InitOption == this.ReadPreviousParameters) {
    		if (!file.exists()) {
    			System.err.println("Your parameterfile " + filename + " is not exists! Please check your name again!");
    			System.exit(1);
    		}
    		this.Log("Actor() InitOption="+ this.ReadPreviousParameters + ", filename=" + this.filename);
			try {
				
				FileReader fr = new FileReader(filename);
				fileRead FR = new fileRead(fr);
				wts = FR.read1DArrayDouble();// reading order was wrong!
				this.Log("Actor() pass reading weights");
				cts = FR.read1DArrayDouble();
				krnel = FR.read2DArrayDouble();// reading order was wrong!
				this.Log("Actor() pass reading kernels");
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
			
			for(int i=0; i<wts.length; i++) {
        		layer1.weight.add(wts[i]);// revised by k.yamauchi
        		layer1.C.add(cts[i]);
    		}
    		this.Log("Actor() pass new kernel weights size " + layer1.weight.size());
    		
    		for(int i=0; i<krnel.length; i++) {
    			double[] x = new double[krnel[0].length];
    			for(int j=0;j<krnel[i].length; j++) {
    				x[j] = krnel[i][j];
    			}
    			layer1.kernel.add(x);
    		}
    		this.Log("Actor() pass new kernel addition kernel size is " + layer1.kernel.size());
    	
    	}
    }
  
    
    public String getFilename() {
		return filename;
	}

    public void displaySpecifications() {
    	this.Log("displaySpecifications() size of kernels: " + this.krnel.length);
    }

	public void Actors(double td, double[] input, double output, double decayCoefficient)throws Exception {
		synchronized(layer1.kernel) {
			if(this.previous_inputs != null){// revised by  k.yamauchi
				this.Log("Actors() " + VectorFunctions.VectorFunctions.display_vector_str("previous_inputs", this.previous_inputs));
				System.out.println("td = "+td);
				net.train(this.previous_inputs, output, decayCoefficient, td);//revised by k.yamauchi
			}
			this.recordInputOutput(input, output);// added by k.yamauchi
		}
    }
    
    // Added by k.yamauchi (Store current inputs and output.)
    void recordInputOutput(double[] input, double output) {
    	this.previous_inputs = new double[input.length];
    	for (int i=0; i<input.length; i++) {
    		this.previous_inputs[i]=input[i];
    	}
    	this.previous_output = output;
    }

    public double predict(double[] testInput) {
        net.think(testInput);
        return net.getOutput();
    }
    
    public void fileWrite(FileWriter fw) throws Exception {
    	synchronized(layer1.kernel) {
    		double[] wt = new double[layer1.weight.size()]; // revised by k.yamauchi
    		double[][] krnl = new double[layer1.kernel.size()][layer1.kernel.get(0).length];
    		double[] ct = new double[layer1.C.size()];
    		for(int i=0;i<layer1.weight.size();i++) {
    			wt[i] = layer1.weight.get(i);
    			ct[i] = layer1.C.get(i);
    		}
    		try {
    			for(int i=0;i<layer1.kernel.size();i++) {
    				for(int j=0;j<layer1.kernel.get(0).length;j++) {
//    					this.Log("fileWrite() index  (i, j) = (" + i + ", " + j + ")");
    					krnl[i][j] = layer1.kernel.get(i)[j];
    				}
    			}
    		}catch(java.lang.ArrayIndexOutOfBoundsException aex) {
    			this.Log("Actor.fileWrite() layer1.kernel.size() = " + layer1.kernel.size() + " layer1.kernel.k[].size =" + layer1.kernel.get(0).length);
    			aex.printStackTrace();
    			System.exit(1);
    		}
    		this.Log("filewrite() layer1 weight size = "+layer1.weight.size());
    		this.Log("filewrite() layer1 kernel size = "+layer1.kernel.size());
    		filewrite.datawrite(wt,fw);
    		filewrite.datawrite(ct, fw);
    		filewrite.datawrite(krnl,fw);
    		this.Log("filewrite() " + this.filename + " done");
    	}
    }
    
	// Added by k.yamauchi
    public void resetStatus() {
    	this.previous_inputs = null;
    	this.previous_output = 0;
    }
	void Log(String log) {
		if (this.DEBUG) System.out.println("Actor." + log);
	}
	void LogForce(String log) {
		if (this.DEBUG) System.out.println("Actor." + log);
	}
	void LogTerminate(String log) {
		if (this.DEBUG) {
			System.out.println("Terminated at Actor." + log);
			System.exit(1);
		}
	}
	void LogPose(String log, Scanner keyScanner) {
		if (this.DEBUG) {
			System.out.println("Terminated at Actor." + log);
			keyScanner.next();
		}
	}
	
}
