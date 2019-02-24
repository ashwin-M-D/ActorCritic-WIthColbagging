package neuralnet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
//import java.io.IOException;

//import java.util.Scanner;

import neuralnet.NeuralNetCritic;
import neuralnet.NeuronLayer;


public class Critic {
    private final boolean DEBUG = false;
    private final int ReadPreviousParameters = 0;
    NeuronLayer layer2;
    NeuralNetCritic net1; // = new NeuralNetCritic(layer2);
    int InitOption;
    FileWriter fw=null;
    FileReader fr= null;
    private String filename="";
 
    public Critic(int initOption, String name) {
    	this.Log("Critic generation====================================================================================");
    	this.layer2 = new NeuronLayer();
//    	this.net1 = new NeuralNetCritic(layer2, 0.001, 3.0, 0.001, 0.9, 1000);
//    	this.net1 = new NeuralNetCritic(layer2, 0.1, 3.0, 0.02, 0.9, 150);
    	this.net1 = new NeuralNetCritic(layer2, 0.1, 0.01, 0.01, 0.9, 200);
    	this.InitOption = initOption;
    	this.filename = "critic-"+name + ".dat";
    	File file = new File(this.filename);
    	
    	if(this.InitOption == this.ReadPreviousParameters) {
    		double[][] krnel=null;
    		double[] wts=null;
    		try {
    			if (!file.exists()) {
    				System.err.println("Your parameter file " + this.filename + " is not exists. Please check your name again!");
    				System.exit(1);
    			}
				fr = new FileReader(this.filename);
				fileRead FR = new fileRead(fr);
				wts = FR.read1DArrayDouble();
				krnel = FR.read2DArrayDouble();
				fr.close();

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				System.exit(1);
			} catch (Exception e2) {
				e2.printStackTrace();
				System.exit(1);
			}
    		this.Log("fileRead() kernel size = " + krnel.length);
    		double[] x = new double[krnel[0].length];
    		for(int i=0; i<krnel.length; i++) {
    			for(int j=0;j<krnel[i].length; j++) {
    				x[j] = krnel[i][j];
    			}
    			layer2.kernel.add(x);
    		}
    		for(int i=0; i<wts.length; i++) {
        		layer2.weight.add(wts[i]);
    		}
    	}
    }
    
    public void Critics(double[] input, double reward, double[] inputInitial)throws Exception {
        this.net1.train(input, reward, inputInitial);
    }

    public double predictTDError(double[] testInput, double reward, double[] testInputInitial) {
//        net1.think(testInput);
    	double tderr = this.net1.getOutputTDError(testInput, reward, testInputInitial);
        this.Log("predictTDError() tderr = " + tderr);
        return tderr;
    }
    
    public void fileWrite(FileWriter fw) throws Exception {
    	double[] wt = new double[layer2.weight.size()];// revised by k.yamauchi
    	double[][] krnl = new double[layer2.kernel.size()][layer2.kernel.get(0).length];
    	this.Log("critic.fileWrite() layer2.weight.size() = " + layer2.weight.size() + " layer2.kernel.size() =" + layer2.kernel.size() + " input dim= " + layer2.kernel.get(0).length);
    	
    	for(int i=0;i<layer2.weight.size();i++) {
    		wt[i] = layer2.weight.get(i);
    	}
    	for(int i=0;i<layer2.kernel.size();i++) {
    		for(int j=0;j<layer2.kernel.get(0).length;j++) {
//				this.Log("critic.fileWrite() index  (i, j) = (" + i + ", " + j + ")");
    			krnl[i][j] = layer2.kernel.get(i)[j];
    		}
    	}
    	this.Log("filewrite() layer2 weight size = "+layer2.weight.size());
    	
		filewrite.datawrite(wt, fw);
		filewrite.datawrite(krnl, fw);
		this.Log("filewrite() critickernel.dat done");
    }
    
    void Log(String log) {
    	if (this.DEBUG) System.out.println("Critic." + log);
    }

	public String getFilename() {
		return filename;
	}
    
    
}
