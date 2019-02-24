package game.userinterface;

import javax.swing.JFrame;

import neuralnet.NeuralStart;
import neuralnet.dataClass;
import game.userinterface.GameScreen;

@SuppressWarnings("serial")
public class GameWindow extends JFrame {
	
	public static final int SCREEN_WIDTH = 600;
	private GameScreen gameScreen;
	NeuralStart ns;
	
	public GameWindow(dataClass dataclass, NeuralStart ns, String name) {
		super("Java T-Rex game");
		this.ns = ns;
		setSize(SCREEN_WIDTH, 175);
		setLocation(400, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		gameScreen = new GameScreen(dataclass, ns, name);
		addKeyListener(gameScreen);
		add(gameScreen);
	}
	
	public void startGame(int option) {
		setVisible(true);
		gameScreen.startGame(option);
	}
	
	public float getSpeedX() {
		return gameScreen.getSpeedX();
	}
	
	public int getStateJump() {
		return gameScreen.getStateJump();
	}
	
	public int getWidthCactus() {
		return gameScreen.getWidthCactus();
	}
	
	public int getGameStatus() {// Added by k.yamuchi 2018.8.28
		return gameScreen.getGameState();
	}
	
//	public int getReward() { //Added by k.yamauchi
//		return gameScreen.getReward();
//	}
	
	public int getHeightCactus() {
		return gameScreen.getHeightCactus();
	}
	
	public int getPosX() {
		return gameScreen.getPosX();
	}
	
	// following four methods are added by k.yamauchi
	public void JumpKey() {// Added by k.yamauchi
		this.gameScreen.automatedKeyPress();
	}
	public void JumpKeyReleased() {// Added by k.yamauchi
		this.gameScreen.automatedKeyReleased();
	}
	public boolean IsManualMode() {// Added by k.yamauchi
		return this.gameScreen.isManualMode();
	}
	public void setManualMode(boolean data) {
		this.gameScreen.setManualMode(data);
	}
	/*
	  public static void main(String args[]) {
		(new GameWindow()).startGame();
	} 
	*/
	
	public void forceStartGame(boolean withReset) {
		this.gameScreen.forceStartGame(withReset);
	}
	public GameScreen getGameScreenObj() {// added by k.yamauchi
		return this.gameScreen;
	}
	
	public void my_nortify() {// added by k.yamauchi
		this.gameScreen.threadNotify();
	}
}
