package Bagging;

//import neuralnet.NeuralStart;
import neuralnet.GameStart;
import neuralnet.dataClass;
import neuralnet.performanceEstimator;

import java.util.Scanner;

public class controlBaggingClass {

    public static void main(String[] args) {
    	Scanner sc=new Scanner(System.in);
        /* option is 0 -> test
         *           1 -> train
         */
    	if (args.length < 1) {
    		System.err.println("Usage SABagging.jar [user name1][user name2]...");
    		System.exit(1);
    	}
    	dataClass dataclass = new dataClass();
    	IntegratedNeuralStart T1 = new IntegratedNeuralStart(args, dataclass);
    	GameStart T2 = new GameStart(dataclass, T1, 0, "Integrate");
    	T1.setGameStart(T2);
    	performanceEstimator est = new performanceEstimator();
    	est.estimate(T1,T2);
    	sc.close();
    }
}
