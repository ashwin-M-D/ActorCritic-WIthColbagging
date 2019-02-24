package neuralnet;

import neuralnet.NeuralStart;
import neuralnet.GameStart;
import neuralnet.dataClass;
//import java.util.Scanner;

public class controlClass {
	
	public controlClass(String name, int InitOption) {

//    		System.out.println("enter option \n 0 -> load previous kernels \n 1 -> new training");
    		System.out.println("name = " + name + " option=" + InitOption);
    		dataClass dataclass = new dataClass();
    		NeuralStart T1 = new NeuralStart(dataclass, InitOption, name, 0.999);
    		GameStart T2 = new GameStart(dataclass, T1, 1, name);
    		T1.setGameStart(T2);
    		performanceEstimator est = new performanceEstimator();//added
        	est.estimate(T1,T2);//added
//    		T2.start();
//    		T1.start();
//    	}
	}
	
    public static void main(String[] args) {
    	if (args.length < 2) {
    		System.err.println("Usage SPActorCritic [your1stname][ReadPreviousParameter:0 / NewTraining:1]");
    		System.exit(1);
    	}
    	new controlClass(args[0], Integer.parseInt(args[1])); 
      }
}
