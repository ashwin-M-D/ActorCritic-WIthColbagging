package neuralnet;

import neuralnet.Actor;
import neuralnet.Critic;
import neuralnet.dataClass;

import java.io.FileWriter;
//import java.io.IOException;
import java.util.Scanner;

import VectorFunctions.VectorFunctions;


public class NeuralStart extends Thread  {
	//private static final int START_GAME_STATE = 0;
	//private static final int GAME_PLAYING_STATE = 1;
	protected static final int GAME_OVER_STATE = 2;
	protected static final int GAME_SUCCESS_STATE = 3; // Added by k.yamauchi
	protected static final int SupervisedMODE = 1;
	protected static final int SelfSupervisedMode = 0;
	protected static final int FINISHED = 5; // by k.yamauchi;
	public final boolean DEBUG = false;
	public final boolean probcheck = false;
	//public final boolean DEBUG = true;
	Actor act;
	Critic cri;
	protected double trainData[] = {0.0 ,0.0 ,0.0 ,0.0 ,0.0 ,0.0 ,0.0};// You can replace this line with traindata = new double[7]; 
	protected double testData[] = {0.0 ,0.0 ,0.0 ,0.0 ,0.0 ,0.0};
	protected double input[] = {0.0 ,0.0 ,0.0 ,0.0, 0.0 }; // {speed, xPos, width, height of cactus, Y position of Dino} 
	protected double inputInitial[] = {0.0 ,0.0 ,0.0 ,0.0, 0.0 }; // {speed, xPos, width, height}
	protected double reward;
	protected double td;
	protected double output;
	
	protected dataClass dataclass;
	protected int gameState;
	protected GameStart gs=null;
	protected int time_index = 0;
	protected Scanner keyScanner;
	protected double GainSpeed = 70.0D; //70
	protected double GainPositionX=800D;  //800;
	protected double GainWidth=1.0;     //62
	protected double GainCactusHeight=1.0D; //80
	protected double GainY=80.0; // 80
	
	protected double Jumpingthreshold = 0.54;
	protected double decayCoefficient; // for LRFU of actor 
	
	public NeuralStart(dataClass dataclass, int initOption, String name, double decayCoefficient){
		this.dataclass = dataclass;
		this.Log("NeuralStart() constructor 1");
        reward = 0;
        td = 0.1;
        output = 0;
        this.act = new Actor(initOption, name);
        this.Log("NeuralStart() constructor1 after init Actor");
        this.cri = new Critic(initOption, name);
        this.Log("NeuralStart() constructor1 after init critic");
        gameState = 0;
        this.decayCoefficient = decayCoefficient; 
        this.keyScanner = new Scanner(System.in);
	}
	
	public NeuralStart(dataClass dataclass){
		this.dataclass = dataclass;
		this.Log("NeuralStart() constructor 2");
        reward = 0;
        td = 0.1;
        output = 0;
        gameState = 0;
//        this.keyScanner = new Scanner(System.in);
	}
	
	@Override
	public synchronized void run() {
		boolean IsWritten = false; // data was written
		double action_new =0D; // new action (Action(t))
        	while(true){
        		try {//Added by k.yamauchi
        			wait();   // Waked up by GameScreen().
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
        		//Note: input[] denotes X(t) 
        		output = trainData[4]; //JumpState Note: Action(t-1)
        		
        		reward = trainData[5]; 
        		gameState = (int)trainData[6]; //Game status
        	
        		this.Log("run() ^^^^^^^^^^^supervised^^^^^^^^^^^^ output = " + output);
        		
//        		this.gs.game.getGameScreenObj().threadNotify();
        		
        		if (!this.gs.IsManualMode()) {// Added by k.yamauchi (Auto mode: reinforcement learning mode)
        			if (output < 0.5) { // If the user does not push space key during the semi-automatic mode. 
        				this.Log("run() ^^^^^^^^^^^supervised->automatic^^^^^^^^^^^^ output = " + output);
        				double outputvalue = act.predict(input);
        				this.LogForce("run() act.predict = " + outputvalue);
        				if(probcheck) System.out.println("prediction probability value = "+outputvalue);
//        				if ( outputvalue + 0.01 * (Math.random() - 0.5) > this.Jumpingthreshold ) {
        				if (this.getAction(outputvalue, this.Jumpingthreshold)>0) {
        					action_new = 0.8; // To prevent the weight values of each kernel going to infinity..
        				}else {
        					action_new = 0.2; // To prevent the weight values of each kernel going to - infinity..
        				}
        			}
        		}
        		
        		this.Log("run() ^^^^ pass automatic game part ^^^^");
//        		if(gameState == 0 ) {
//        			continue;
//        		}
        
        		if(gameState == NeuralStart.GAME_OVER_STATE) {
        			if (!IsWritten) {
        				try {
        					this.Log("run() writing parameters!(supervised mode)");
        					String filename = act.getFilename();
        					FileWriter fw1 = new FileWriter(filename);
        					filename = this.cri.getFilename();
        					FileWriter fw2 = new FileWriter(filename);
        					act.fileWrite(fw1);
        					fw1.close();
        					this.cri.fileWrite(fw2);
        					fw2.close();
        					IsWritten = true;
        				} catch (Exception e) {
        					e.printStackTrace();
        				} // I have not made this.
        			}
        		}else {
        			IsWritten = false;
        			if (td > 0) {
        				this.LogForce("run() "+VectorFunctions.display_vector_str("input[]", input));
        				this.LogForce("run() game status = " + this.gs.getGameStatus());
        				this.LogForce("run() ================ reward = " + reward + " output = " + output + " push any key!");
//           			this.keyScanner.nextLine();
        			}
//        		if (reward > 0) {
//        			this.LogTerminate("NeuralStart.run() positive reward " + reward);
//        		}
        			td = this.cri.predictTDError(input, reward, inputInitial); // revised by k.yamauchi. The td err of current input is for the learning of the previous state.
        		 // Therefore, the predicted td error should be calculated before executing learning methods.
        			this.Log("run() TD error calculated is = "+td);
   
        			try {
        				if (this.gs.IsManualMode()) {
        					this.Log("run() supervised learning is occured!!\n");
        					this.Log("run() input is " + VectorFunctions.display_vector_str("input", input));
        	
        					act.Actors(1, input, this.setAppropreteRegion(output), this.decayCoefficient);
        				}else {
        					act.Actors(td, input, this.setAppropreteRegion(output), this.decayCoefficient);
        				}
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
                
        			try {
        				this.cri.Critics(input, reward, inputInitial);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
             
              
        			// moved by k.yamauchi
        			/*int counter = 0;
        			for(int i=0;i<input.length;i++){
        				if(inputInitial[i] == input[i]) {
        					counter++;
        				}
        			}*/
        		}	
        		if (!this.gs.IsManualMode()) {// Added by k.yamauchi (Auto mode: reinforcement learning mode)
        			if (gameState == GAME_SUCCESS_STATE) {
//        					act.resetStatus();
        					this.gs.forceStartGame(false);
        			}else if  (gameState == GAME_OVER_STATE) {
//        					act.resetStatus();
        					this.gs.forceStartGame(true);
        			}
        		}else {
           		if (gameState == GAME_SUCCESS_STATE) {
//        				act.resetStatus();
        				this.gs.forceStartGame(false);
        			}
        		}
        		if (gameState == NeuralStart.FINISHED) {
        			this.Log("run() finished!");
            		//String filename = act.getFilename();
					try {
						this.Log("run() writing parameters!(supervised mode->finished)");
						String filename1 = act.getFilename();
						FileWriter fw1 = new FileWriter(filename1);
		      			filename1 = this.cri.getFilename();
	        			FileWriter fw2 = new FileWriter(filename1);
	        			this.act.fileWrite(fw1);
	        			this.cri.fileWrite(fw2);
	        			fw1.close(); fw2.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
  
        			System.exit(0);
        		}
           		for(int i=0;i<input.length;i++){
        			inputInitial[i] = input[i]; 
        		}
        		this.Log("run() gamescreen nortify()");
        		this.gs.my_nortify();
        		if (action_new > 0.5) {
        			this.gs.JumpKey();
        			this.gs.JumpKeyReleased();
        		}
        	}// while (true)
	}
	
	double setAppropreteRegion(double x) {
		if (x > 0.8) x = 0.8;
		else if (x < 0.2) x = 0.2;
		return x;
	}
	
	double getAction(double probability) {
		double rnd = Math.random();
		this.Log("==================================getAction() probability=" + probability);
		if (rnd < probability) {
			return 1.0;
		}else {
			return 0.0;
		}
	}
	protected double getAction(double probability, double threshold) {
		double rnd = Math.random()*0.5;
		this.Log("==================================getAction() probability=" + probability);
		if(probcheck) System.out.println("output expected = " +(0.1*rnd + probability));
		if (0.1*rnd + probability > threshold) {
			return 1.0;
		}else {
			return 0.0;
		}
	}
	
	public void setGameStart(GameStart gs) {
		this.gs = gs;
	}
	
	// Added by k.yamauchi
	void Log(String log) {
		if (this.DEBUG) System.out.println("NeuralStart." + log);
	}
	void LogForce(String log) {
		if (this.DEBUG) System.out.println("NeuralStart." + log);
	}
	void LogTerminate(String log) {
		if (this.DEBUG) System.out.println("Terminated at NeuralStart." + log);
		System.exit(1);
	}
	void LogPose(String log) {
		if (this.DEBUG) System.out.println("Terminated at NeuralStart." + log);
		this.keyScanner.next();
	}
	
	// Added by k.yamauchi
	public synchronized void threadNotify() {
		notify();
	}
}
