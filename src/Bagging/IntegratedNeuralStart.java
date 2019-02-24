package Bagging;

//import neuralnet.Actor;
//import neuralnet.Critic;
import neuralnet.GameStart;
import neuralnet.NeuralStart;
import neuralnet.dataClass;

//import java.io.FileWriter;
import java.util.Scanner;

//import VectorFunctions.VectorFunctions;


public class IntegratedNeuralStart extends NeuralStart  {

	bagging BaggingActors;

	public IntegratedNeuralStart(String args[], dataClass dataclass){
		super(dataclass);
		this.dataclass = dataclass;
		System.out.println("starting neural network");
        reward = 0;
        td = 0.1;
        output = 0;
        this.BaggingActors = new bagging(args);
        gameState = 0;
        this.keyScanner = new Scanner(System.in);
	}
	
	@Override
	public synchronized void run() {
        	while(true){
        		try {//Added by k.yamauchi
        			wait();
        		}catch (InterruptedException ex) {
        			ex.printStackTrace();
        		}
        		time_index++;
 
        		
        		trainData = dataclass.getDataTrain();
          		input[0] = trainData[0]/GainSpeed; // speed
        		input[1] = trainData[1]/GainPositionX; // positionX of cactus
        		input[2] = trainData[2]/GainWidth; // width of cactus
        		input[3] = trainData[3]/GainCactusHeight; // height of cactus
//        		input[4] = (80.0-trainData[4])/GainY; // Y position of Dino        		
        		
        		output = trainData[4]; //JumpState
        		reward = (int)trainData[5]; 
        		gameState = (int)trainData[6]; //Game status
        	
//        		this.gs.game.getGameScreenObj().threadNotify();
        		
        		double outputvalue = this.BaggingActors.getIntegratedAction(input);
        		this.LogForce("run() act.predict = " + outputvalue);
//        				if (Math.random() < outputvalue) {	
        		if ( this.getAction(outputvalue, this.Jumpingthreshold)>0) {
        			output = 0.8; // To prevent the weight values of each kernel going to infinity..
        		}else {
        			output = 0.2; // To prevent the weight values of each kernel going to - infinity..
        		}
        		
     
              
              // moved by k.yamauchi
         		/*int counter = 0;
        		for(int i=0;i<input.length;i++){
        			if(inputInitial[i] == input[i]) {
        				counter++;
        			}
        		} */
        		
        		if (gameState == NeuralStart.GAME_SUCCESS_STATE) {
        			this.BaggingActors.resetStatus();
        			this.gs.forceStartGame(false);
        		}else if  (gameState == NeuralStart.GAME_OVER_STATE) {
        			this.BaggingActors.resetStatus();
        			this.gs.forceStartGame(true);
        		}else if (gameState == NeuralStart.FINISHED) {
        			System.exit(0);
        		}
        		this.gs.my_nortify();  
        		if (!this.gs.IsManualMode()) {// Added by k.yamauchi (Auto mode: reinforcement learning mode)
        			if (output == 0.8) {
    					this.gs.JumpKey();
    					this.gs.JumpKeyReleased();
    				}
        		}
     		for(int i=0;i<input.length;i++){
    			inputInitial[i] = input[i]; 
    		}      		
        	}// while (true)
   	}
	
	public void setGameStart(GameStart gs) {
		this.gs = gs;
	}
	
	// Added by k.yamauchi
	void Log(String log) {
		if (this.DEBUG) System.out.println("NeuralStart." + log);
	}
	void LogForce(String log) {
		System.out.println("NeuralStart." + log);
	}
	void LogTerminate(String log) {
		System.out.println("Terminated at NeuralStart." + log);
		System.exit(1);
	}
	void LogPose(String log) {
		System.out.println("Terminated at NeuralStart." + log);
		this.keyScanner.next();
	}
	
	// Added by k.yamauchi
	public synchronized void threadNotify() {
		notify();
	}
}
