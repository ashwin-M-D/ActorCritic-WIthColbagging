package neuralnet;

import java.util.function.Function;
import java.util.ArrayList;


public class NeuronLayer {

    public enum ActivationFunctionType {
        SIGMOID
    }

    public ArrayList<double[]> kernel;
    
    public ArrayList<Double> weight;

    public ArrayList<Double> C;
    
    public final Function<Double, Double> activationFunction, activationFunctionDerivative;

    public NeuronLayer() {
        this(ActivationFunctionType.SIGMOID);
    }

    public NeuronLayer(ActivationFunctionType activationFunctionType) {
        this.kernel = new ArrayList<double[]>();
        this.weight = new ArrayList<Double>();
        activationFunction = NNMath::sigmoid;
        activationFunctionDerivative = NNMath::sigmoidDerivative;
        this.C = new ArrayList<Double>();
    }    
    
    public void decay(double coefficient) {
    	for (int i=0; i<this.C.size(); i++) {
    		double each_c = this.C.get(i);
    		each_c *= coefficient;
    		this.C.set(i, each_c);
    	}
    }
    
    public int getMostInEffectiveKernel() {
    	int minIndex = -1;
    	double minC = Double.MAX_VALUE;
    	for (int i=0; i<this.C.size(); i++ ) {
    		if (this.C.get(i) < minC) {
    			minC = this.C.get(i);
    			minIndex = i;
    		}
    	}
    	return minIndex;
    }
    
    public void addC(int index) {
    	double c = this.C.get(index);
    	c += 1.0D;
    	this.C.set(index, c);
    }
}
