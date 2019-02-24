package Bagging;

//import java.io.FileReader;
//import java.io.IOException;
import VectorFunctions.VectorFunctions;

//import neuralnet.Actor;
//import neuralnet.fileRead;

public class bagging {
	private final boolean DEBUG = false;
	Actor2[] actors;
	double[] weight4EachActor;
	
	public bagging(String args[]) {
		actors = new Actor2[args.length];
		this.Log("bagging() length of argments: " + args.length);
		this.weight4EachActor = new double[args.length];
		double max_weight = Double.MIN_VALUE;
		for (int i=0; i<args.length; i++) {
			this.Log("bagging() the " + i + "-th name is " + args[i]);
			actors[i] = new Actor2(0, args[i]);
//			actors[i].displaySpecifications();
			this.Log("bagging() end of reading   name = " + args[i]);
			if (max_weight < actors[i].getPeformanceScore()) {
				max_weight = actors[i].getPeformanceScore();
			}
		}
		
		for (int j=0; j<args.length; j++) {
			this.weight4EachActor[j] = Math.exp(10 * actors[j].getPeformanceScore()/max_weight);
		}
//		System.out.println("Stopped at bagging.constructor()");
//		System.exit(1);
	}
	
	
	double getIntegratedAction(double[] X) {
		double result=0, total_weight=0D;
		this.Log("getIntegratedAction() # of actors = " + actors.length);
		this.Log("getIntegratedAction() " + VectorFunctions.display_vector_str("X", X));
		for (int a=0; a<actors.length; a++) {
			this.Log("getIntegratedAction() Performance score of " + a + " = " + actors[a].getPeformanceScore());
			this.Log("getIntegratedAction() action[" + a + "]=" + actors[a].predict(X) );
		}
		for (int a=0; a<actors.length; a++) {
			this.Log("getIntegratedAction() weight=" + this.weight4EachActor[a]);; 
			result += this.weight4EachActor[a] * actors[a].predict(X);
			total_weight += this.weight4EachActor[a];
		}
		
		result /= total_weight;
		return result;
	}
	
	public void resetStatus() {
		for (int a=0; a<actors.length; a++) {
			actors[a].resetStatus();
		}
	}

	
	void Log(String log) {
		if (this.DEBUG) System.out.println("bagging." + log);
	}
	
//	public static void main(String[] args) {
//		// 
//		new bagging(args);
//	}

}
