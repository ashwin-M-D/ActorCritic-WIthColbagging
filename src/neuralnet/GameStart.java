package neuralnet;

import game.userinterface.GameWindow;
//import userinterface.GameWindow;
import neuralnet.dataClass;

public class GameStart extends Thread {
	NeuralStart ns;
	//private dataClass dataclass;
	public GameWindow game;
	int option;
	
	public GameStart(dataClass dataclass, NeuralStart ns, int option, String name) {
		//this.dataclass = dataclass;
		this.game = new GameWindow(dataclass, ns, name);
		this.option = option;
	}
	
	@Override
	public void run() {
		game.startGame(option);
	}
	
	public void setNeuralStartObj(NeuralStart ns) {
		this.ns = ns;
	}
	
	// following four methods are added by k.yamauchi
	public int getGameStatus() {
		return this.game.getGameStatus();
	}
	
	public void JumpKey() {
//		System.out.println("GameStart.JumpKey()");
		this.game.JumpKey();
	}
	
	public void my_nortify() {
		this.game.my_nortify();
	}

	public void JumpKeyReleased() {
		this.game.JumpKeyReleased();
	}
	public boolean IsManualMode() {
		return this.game.IsManualMode();
	}
	public void setManualMode(boolean data) {
		this.game.setManualMode(data);
	}
	
	public void forceStartGame(boolean withReset) {
		if (withReset) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.game.forceStartGame(withReset);
	}
	
}
