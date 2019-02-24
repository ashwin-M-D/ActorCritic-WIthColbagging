package neuralnet;

import java.util.ArrayList;
import java.util.Scanner;

import static neuralnet.NNMath.*;

public class NeuralNetCritic {
	private final boolean DEBUG = false;// if DEBUG is true, comments are displayed.
    private NeuronLayer layer2;
    private final double learningRate;
    private final double epsilon;
    private final double sigma;
    private final double gamma;
    private double thetaFunction;
    private double outputNN;
    private int UpperBound;
    Scanner key;


    public NeuralNetCritic(NeuronLayer layer2, double learningRate, double epsilon, double sigma, double gamma, int UpperBound) {
        this.layer2 = layer2;
        this.learningRate = learningRate;
        this.epsilon = epsilon;
        this.sigma = sigma;
        this.gamma = gamma;
        this.UpperBound = UpperBound;
        this.key = new Scanner(System.in);
    }

    public void think(double[] input) {
        double[] y = new double[input.length];
        this.Log("think() size of kernels : " + layer2.kernel.size());
        this.thetaFunction = 0D;
        for(int i=0; i<layer2.weight.size(); i++){
            for( int l = 0; l<input.length; l++){
                y[l] = input[l] - layer2.kernel.get(i)[l] ;
            }
            thetaFunction = thetaFunction + layer2.weight.get(i) * Math.exp(-squareNorm(y) / (2 * sigma * sigma));
        }
        outputNN = thetaFunction; 
    }

    public void train(double[] input, double reward, double[] inputInitial) throws Exception {
        double min_distance = Double.MAX_VALUE; //revised by k.yamauchi
        double[] diff = new double[inputInitial.length];
        ArrayList<Double> deltaWeights = new ArrayList<Double>();
        think(input);
        double outputNNInput = outputNN;
        think(inputInitial);
        double outputNNinputInitial = outputNN;
        if(layer2.kernel.size() < 1) {
        	layer2.weight.add(0.0D);
        	double[] copyInput = inputInitial.clone();//<--- important!!!
        	layer2.kernel.add(copyInput);
        	this.Log("train() kernel 0 added to critic----------------------------------------------------");
        } else {
//        	VectorFunctions.VectorFunctions.display_vector("inputInitial", inputInitial);
        	for(int i=0; i<layer2.kernel.size(); i++) {
//        		VectorFunctions.VectorFunctions.display_vector("Critic.kernelcenter", layer2.kernel.get(i));
        		for( int l = 0; l<input.length; l++){
        			diff[l] = inputInitial[l] - layer2.kernel.get(i)[l] ;
        		}
        		this.Log("train() i ="+i+" and norm(y) = "+squareNorm(diff));
        		double distance = squareNorm(diff); // revised by k.yamauchi
        		if(distance < min_distance){
        			min_distance = distance;
        		}
        	}
        	double tderr = reward + (gamma * outputNNInput) - outputNNinputInitial ; // added by k.yamauchi
//        	if (tderr > 0.5) {
//        		this.LogTerminate("train() tderr = " + tderr + " outputNNInput=" + outputNNInput + " outputNNinputInitial = " + outputNNinputInitial);
//        	}
        	this.Log("train() minimum distance = "+min_distance);
        
        	if( min_distance > epsilon && layer2.kernel.size() < this.UpperBound){
        		layer2.weight.add(tderr);
        		double[] copyInputInitial = inputInitial.clone();
        		layer2.kernel.add(copyInputInitial);
        	}else{
        		for(int i = 0; i<layer2.weight.size(); i++){
        			for( int l = 0; l<input.length; l++){
        				diff[l] = inputInitial[l] - layer2.kernel.get(i)[l] ;
        			}	
        			think(input);
        			deltaWeights.add(learningRate * tderr * Math.exp(-squareNorm(diff)/(2*sigma*sigma)));// revised by k.yamauchi
        		}
        		this.Log("train() # of kernels = " + layer2.weight.size());
        		for(int i=0; i<layer2.weight.size(); i++){
        			this.Log("train() i=" + i);
        			layer2.weight.set(i, (layer2.weight.get(i) + deltaWeights.get(i)));
        		}
        	}
        }
    }

    public double getOutput() {
        return outputNN;
    }
    
    public double getOutputTDError(double[] input, double reward, double[] inputInitial) {
    	double td = 0;
        if (this.DEBUG) System.out.println("input ="+input[0]+" "+input[1]+" "+input[2]+" "+input[3]);
        think(input);
        double outputNNinput = outputNN;
        if (this.DEBUG) {
        	 System.out.println("outputNN td input = "+outputNNinput);
        	 System.out.println("inputInitial ="+inputInitial[0]+" "+inputInitial[1]+" "+inputInitial[2]+" "+inputInitial[3]);
         }
        think(inputInitial);
        double outputNNinputInitial = outputNN;
        if (this.DEBUG) System.out.println("outputNN td inputInitial = "+outputNNinputInitial);
        td = reward + (gamma * outputNNinput) - outputNNinputInitial;
        this.Log("getOutputTDError() td = "+td);
        return td;
    }
    
    void Log(String log) {
    	if (DEBUG) System.out.println("NeuralNetCritic." + log);
    }
    void LogTerminate(String log) {
    	if (this.DEBUG) System.out.println("terminate NeuralNetCritic." + log);
    	this.key.nextLine();
    }
  
}
