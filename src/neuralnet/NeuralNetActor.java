package neuralnet;

import java.util.ArrayList;

import VectorFunctions.VectorFunctions;

import static neuralnet.NNMath.*;

    
public class NeuralNetActor {

	private final boolean DEBUG = false;
    private final NeuronLayer layer1;
    //private double[][] output;
    private final double learningRate;
    private final double epsilon;
    private final double sigma;
    public ArrayList<Double> deltaWeights;
    private double thetaFunction;
    private double outputNN;
    private int UpperBound;
    
    public NeuralNetActor(NeuronLayer layer1) {
        this(layer1, 0.05, 0.001, 0.01, 500); //0.5, 0.5, 0.01, 500 
    }

    public NeuralNetActor(NeuronLayer layer1, double learningRate, double epsilon, double sigma, int UpperBound) {
        this.layer1 = layer1;
        this.learningRate = learningRate;
        this.epsilon = epsilon;
        this.sigma = sigma;
        this.UpperBound = UpperBound;
        //this.output = new double[1][1];
    }

    public void think(double[] input) {
        double[] y = new double[input.length];
        //int NearestNeighbor = -1;
        double min_distance = Double.MAX_VALUE;
        this.thetaFunction = 0D; // revised by k.yamauchi
//        this.Log("think() layer1.weight.size()=" + layer1.weight.size() + " layer1.kernel[0].length=" + layer1.kernel.get(0).length);
        this.Log("think() input.length=" + input.length + " " + VectorFunctions.display_vector_str("prev_input[]", input));
        synchronized(this.layer1) {
        	try {
        		this.Log("think(); layer1.weight.size() = " + layer1.weight.size());
        		for(int i=0; i<layer1.weight.size(); i++){
        		
        			for( int l = 0; l<input.length; l++){
        				y[l] = input[l] - layer1.kernel.get(i)[l] ;
        			}
        			double each_distance = squareNorm(y);
        			if (each_distance < min_distance) {
        				min_distance = each_distance;
        				//NearestNeighbor = i;
        			}
        			this.Log("think() layer1.weight.get(" + i + ") = " + layer1.weight.get(i) + VectorFunctions.display_vector_str("Centroid", layer1.kernel.get(i)));
        			thetaFunction = thetaFunction + layer1.weight.get(i) * Math.exp(-squareNorm(y) / (2 * sigma * sigma));// revised by k.yamauchi
        		}
//        		if (NearestNeighbor != -1) {
//        			double c = layer1.C.get(NearestNeighbor);
//        			c += 1D;
//        			layer1.C.set(NearestNeighbor, c);
//        		}
        		this.Log("think() thetaFunction=" + thetaFunction);
        	}catch(java.lang.IndexOutOfBoundsException ioex) {
        		ioex.printStackTrace();
        		System.exit(1);
        	}
        }
//     double[][] thetaValue = {{thetaFunction}};
//        double[][] outputValue = MatrixUtil.apply(thetaValue,layer1.activationFunction);//<--?
//        double[][] outputValue = {{thetaFunction}};
        this.outputNN = sigmoid(thetaFunction);
    }
    
    double sigmoid(double x) {
    	return 1/(1+Math.exp(-x));
    }

    public void train(double[] input, double output, double decayCoefficient, double tderr) throws Exception {
    	double min_distance = Double.MAX_VALUE;//revised by k.yamauchi
    	double[] y = new double[input.length];
    	int NearestNeighborIndex = -1;	
    	synchronized(this.layer1) {
    		deltaWeights = new ArrayList<Double>();
    		this.Log("train() # of kernels = " + layer1.kernel.size());
    		think(input);
    		if (layer1.kernel.size()>0) {// revised by k.yamauchi
    			layer1.decay(decayCoefficient);// for LRFU
        	
    			for(int i=0; i<layer1.kernel.size(); i++) {

    				for( int l = 0; l<input.length; l++){
    					y[l] = input[l] - layer1.kernel.get(i)[l] ;
    				}
    				if(squareNorm(y) < min_distance){
    					min_distance = squareNorm(y);
    					NearestNeighborIndex = i;
    				}
    			}
        		if (NearestNeighborIndex != -1 && tderr > 0) {
        			double c = layer1.C.get(NearestNeighborIndex);
        			c += 1D;
        			layer1.C.set(NearestNeighborIndex, c);
        		}
    		}else {
    			min_distance = Double.MAX_VALUE;
    		}
    		double wt = 0;
    		if(min_distance > epsilon && layer1.kernel.size() < this.UpperBound){//Addeing new kernel

    			double sum = 0;
    			for(int i = 0; i<layer1.kernel.size(); i++){
    				this.LogForce("train() kernelCenter = " + VectorFunctions.display_vector_str("x", layer1.kernel.get(i)));
    				for( int l = 0; l<input.length; l++){
    					y[l] = input[l] - layer1.kernel.get(i)[l] ;
    				}
    				wt = layer1.weight.get(i);
    				sum = sum + wt * Math.exp( -squareNorm(y) / (2 * sigma * sigma) );// revised by k.yamauchi
    			}
    			this.Log("train() sum=" + sum);
    			if (tderr < 0) {
    				wt = NNMath.sigmoidInverse(1-output) - sum;
    			}else {
    				wt = NNMath.sigmoidInverse(output) - sum;
    			}
    			this.Log("train() new wt = " + wt);

    			layer1.weight.add(wt);
    			layer1.kernel.add(input.clone());
    			layer1.C.add(1.0D);
    			this.Log("train() New kernel added!"); // revised by k.yamauchi
    		} else if (min_distance <= epsilon) {//modify parameters
//    			if (layer1.kernel.size()>0) {// Update LRFU related parameter
//    				this.Log("train() NearestNeighborIndex = " + NearestNeighborIndex);
//    				this.layer1.addC(NearestNeighborIndex);
//       	 	  	}
    			for(int i = 0; i<layer1.weight.size(); i++){
    				for( int l = 0; l<input.length; l++){
    					y[l] = input[l] - layer1.kernel.get(i)[l];
    				}
    				this.Log("train() outputNN=" + outputNN);
    				deltaWeights.add(learningRate * 2 * tderr * (output - outputNN) * outputNN * (1 - outputNN) * Math.exp( - squareNorm(y) / (2 * sigma * sigma)));
    			}
    			for(int i=0; i<layer1.weight.size(); i++){
    				this.Log("train() deltaweights(" + i + ")=" + deltaWeights.get(i));
    				layer1.weight.set(i, (layer1.weight.get(i) + deltaWeights.get(i)));
    			}
    			//int i = 0;
//    			while (i<layer1.weight.size()) {
//    				if (Math.abs(layer1.weight.get(i)) < 0.001) {
//    					layer1.weight.remove(i);
//    					layer1.kernel.remove(i);
//    					layer1.C.remove(i);
//    					this.LogForce("train() remove is occured!!");
//    				}else {
//    					i ++;
//    				}
//    			}
    			this.Log("train() Weights Adjusted");
    		} else if (layer1.kernel.size() >= this.UpperBound){ 
    			int IneffectiveKP = this.layer1.getMostInEffectiveKernel();
    			this.layer1.kernel.remove(IneffectiveKP);//prune the kernel
    			this.layer1.weight.remove(IneffectiveKP);
    			this.layer1.C.remove(IneffectiveKP);
    			this.think(input);//re-calculate output
    			double sum = 0;
    			for(int i = 0; i<layer1.kernel.size(); i++){
    				for( int l = 0; l<input.length; l++){
    					y[l] = input[l] - layer1.kernel.get(i)[l] ;
    				}
    				wt = layer1.weight.get(i);
    				sum = sum + wt * Math.exp( -squareNorm(y) / (2 * sigma * sigma) );// revised by k.yamauchi
    			}
    			this.Log("train() sum=" + sum);
    			if (tderr < 0) {
    				wt = NNMath.sigmoidInverse(1-output) - sum;
    			}else {
    				wt = (NNMath.sigmoidInverse(output) - sum);
    			}
    			layer1.weight.add(wt);
    			layer1.kernel.add(input.clone());
    			layer1.C.add(1.0D);
    			this.Log("train()  kernel is replaced!"); // revised by k.yamauchi
    		}
    	}//synchronized()
    }

    public double getOutput() {
        return this.outputNN;
    }

    // Added by k.yamauchi
    void Log(String log) {
    	if (this.DEBUG) {
    		System.out.println("NeuralNetActor." + log);
    	}
    }
    void LogForce(String log) {
    	if (this.DEBUG) System.out.println("NeuralNetActor." + log);
    }
}
