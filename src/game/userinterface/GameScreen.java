package game.userinterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import game.gameobject.Clouds;
import game.gameobject.EnemiesManager;
import game.gameobject.Land;
import game.gameobject.MainCharacter;
//import game.util.Resource;
import neuralnet.NeuralStart;
import neuralnet.dataClass;
import neuralnet.filewrite;

@SuppressWarnings("serial")
public class GameScreen extends JPanel implements Runnable, KeyListener {
	private final boolean DEBUG = false;
	private final int MAXITERATION = 15; //maximum number of game repeats: Added by k.yamauchi November 1, 2018
	private static final int TESTITERATION = 10;
	
	private static final int START_GAME_STATE = 0;
	private static final int GAME_PLAYING_STATE = 1;
	private static final int GAME_OVER_STATE = 2;
	private static final int GAME_SUCCESS_STATE = 3; // Added by k.yamauchi
	
	private static final int TOUCH_DOWN = 4;// by k.yamauchi
	private static final int NORMAL_RUN = 0;// by k.yamauchi
	private static final int JUMPING = 1;// by k.yamauchi
	private static final int DOWN_RUN = 2;// by k.yamauchi
	private static final int FINISHED = 5; // by k.yamauchi;
	private Land land;
	private MainCharacter mainCharacter;
	private EnemiesManager enemiesManager;
	private Clouds clouds;
	private Thread thread=null;
	
    public ArrayList<Integer> totalScore;

	private boolean isKeyPressed;
	private boolean ManualMode = true; // added by k.yamauchi 
	private int gameState = START_GAME_STATE;

	private BufferedImage replayButtonImage;
	private BufferedImage gameOverButtonImage;
	private dataClass dataclass;
	private int num_of_iterations = 0;
	double reward = 0; // moved by k.yamauchi
	NeuralStart ns; // added by k.yamauchi
	private String name;
	private String OriginalName;
	public GameScreen(dataClass dataclass, NeuralStart ns, String name) {//modified by k.yamauchi
		this.name = "player-"+name;
		OriginalName = name;
		this.dataclass = dataclass;
		this.ns = ns;//Added by k.yamauchi.
		mainCharacter = new MainCharacter();
		totalScore = new ArrayList<Integer>();
		land = new Land(GameWindow.SCREEN_WIDTH, mainCharacter);
		mainCharacter.setSpeedX(4);
		URL replayButtonURL = this.getClass().getResource("replay_button.png");
		URL GameoverURL = this.getClass().getResource("gameover_text.png");
		try {
			replayButtonImage = ImageIO.read(replayButtonURL);
			gameOverButtonImage = ImageIO.read(GameoverURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		enemiesManager = new EnemiesManager(mainCharacter);
		this.mainCharacter.setMyenemy(this.enemiesManager.getEnemyObj());//modified by k.yamauchi
		clouds = new Clouds(GameWindow.SCREEN_WIDTH, mainCharacter);
	}

	public void startGame(int option) {
		if(option == 0) {
			ManualMode = false;
		}
		if (thread == null) {
			thread = new Thread(this);
		}
		thread.start();
	}
	
	public int getGameState() {
		return gameState;
	}
	
//	public int getReward() {// Added by k.yamauchi
//		return this.reward;
//	}
	public float getSpeedX() {
		return mainCharacter.getSpeedX();
	}
	
	public int getStateJump() {
		return mainCharacter.getStateJump();
	}
	
	public int getWidthCactus() {
		return enemiesManager.getWidthCactus();
	}
	
	public int getHeightCactus() {
		return enemiesManager.getHeightCactus();
	}
	
	public int getPosX() {
		return enemiesManager.getPosX();
	}
	
	

	public void gameUpdate() {
		if (gameState == GAME_PLAYING_STATE) {
			clouds.update();
			land.update();
			mainCharacter.update();
			enemiesManager.update();
		
			if (enemiesManager.isCollision()) {
				this.Log("gameUpdate() collision!!");
				mainCharacter.playDeadSound();
				gameState = GAME_OVER_STATE; 
				mainCharacter.dead(true);
				this.reward = -1;
				
				if (++this.num_of_iterations >= this.MAXITERATION) { // added by k.yamauchi
					this.LogForce("gameUpdate() finished!! num_of_iterations=" + this.num_of_iterations);
					gameState = GameScreen.FINISHED;
				}
				if(this.num_of_iterations >= this.TESTITERATION) {
					this.ManualMode = false;
					if(this.num_of_iterations==this.TESTITERATION) {
						this.name = OriginalName;
						for(int i=0;i<this.TESTITERATION;i++) {
							totalScore.set(i, 0);
						}
					}
					
				}

				this.PerformanceScoreUpdate(this.name);
			}else { //modified by k.yamauchi
				this.Log("gameUpdate() cactus position x = " + this.enemiesManager.getCurrentPositionOfCactus() + " mainCharacter.X position=" + this.mainCharacter.getPositionX() + " game status = " + mainCharacter.getState()  );
				switch (mainCharacter.getState()) {
				case DOWN_RUN:
					this.Log("gameUpdate() DOWN_RUN");
					this.reward = 0.0; break;
				case JUMPING:
					this.Log("gameUpdate() JUMPING");
//					this.NumberOfJumping+=1;
					this.reward = 0.0; break;
				case TOUCH_DOWN:
					this.Log("gameUpdate() cactus position=" + this.enemiesManager.getCurrentPositionOfCactus());
					this.Log("gameUpdate() TOUCH_DOWN  and GET reward *************************************************************");
					if (this.mainCharacter.getEnemyPosition() < this.mainCharacter.getPositionX()) {//Modified by k.yamauchi (enemisManager.getCurrentPositionOfCuctus() does not yield correct value. The value is highly depending on the timing of method call.)
						this.reward = 1;
						gameState = GAME_SUCCESS_STATE;
//						this.mainCharacter.upScore();
//						this.LogTerminate("gameUpdate() reward = " + this.reward + " : TOUCH_DOWN: enemyposition=" + this.mainCharacter.getEnemyPosition() + " mainCharacterPosition=" + this.mainCharacter.getPositionX());
					}else {
						this.reward = -1 ; //((int)(this.mainCharacter.getPositionX() - this.mainCharacter.getEnemyPosition()));
						
//						 this.LogTerminate("gameUpdate() reward = " + this.reward + " : TOUCH_DOWN: enemyposition=" + this.mainCharacter.getEnemyPosition() + " mainCharacterPosition=" + this.mainCharacter.getPositionX());
					}
					mainCharacter.reset();
					break;
				case NORMAL_RUN:
					this.Log("gemeUpdate() NORMAL_RUN ===========================================================");
					this.reward = 0.1;
					break;
				}
			}
		}
	}

	public void paint(Graphics g) {
		g.setColor(Color.decode("#f7f7f7"));
		g.fillRect(0, 0, getWidth(), getHeight());

		switch (gameState) {
		case START_GAME_STATE:
			mainCharacter.draw(g);
			break;
		case GAME_PLAYING_STATE:
		case GAME_SUCCESS_STATE://Added by k.yamauchi
		case GAME_OVER_STATE:
			clouds.draw(g);
			land.draw(g);
			enemiesManager.draw(g);
			mainCharacter.draw(g);
			g.setColor(Color.BLACK);
			g.drawString("score " + mainCharacter.score, 500, 20);
			if(gameState == GAME_OVER_STATE) {
				g.drawString("ITR " + (this.num_of_iterations), 450, 20);
			}
			else {
				g.drawString("ITR " + (this.num_of_iterations+1), 450, 20);
			}
			if(this.num_of_iterations>=this.TESTITERATION) {
				g.drawString("Testing Learning Machine. Don't press any key", 200, 50);
			}
			if (gameState == GAME_OVER_STATE) {
				g.drawImage(gameOverButtonImage, 200, 30, null);
				g.drawImage(replayButtonImage, 283, 50, null);
				
			}
			break;
		}
	}

	@Override
	public synchronized void run() {

		int fps = 100;
		long msPerFrame = 1000 * 1000000 / fps;
		long lastTime = 0;
		long elapsed;
		
		int msSleep;
		int nanoSleep;
//		int reward = 3; // modified by k.yamauchi
		double[] input = new double[5];// modified by k.yamauchi (4dim->5dim)
		int jumpState = 0;

		int gameStateExport = 0;

//		long endProcessGame;
//		long lag = 0;
//		boolean GoOn = true;
		while (true) {
	
			input[0] = getSpeedX();
			input[1] = getPosX();
			input[2] = getWidthCactus();
			input[3] = getHeightCactus();
//			input[4] = (double)this.mainCharacter.getPositionY();  by k.yamauchi 2019.01.11

			jumpState = getStateJump();
			gameStateExport = getGameState();

			
			dataclass.setData(input, reward, jumpState, gameStateExport);

			// After GAME_OVER_STATE, send nortify signal. However, the learner should know it is failed.
			// So, after one step, nofity is sent.
//			if (GoOn) this.ns.threadNotify();// Added by k.yamauchi
//			if (gameStateExport == GAME_OVER_STATE) GoOn = false; //Added by k.yamauchi
//			else GoOn = true; 
			this.ns.threadNotify();// Added by k.yamauchi This part wake up the NeuralStart().
			
			try {//Added by k.yamauchi  This part is waked up by NeuralStart's nortify(). 
				wait();
			}catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			
//			endProcessGame = System.nanoTime();
			this.gameUpdate();
			repaint();
			
			elapsed = (lastTime + msPerFrame - System.nanoTime());
			msSleep = (int) (elapsed / 1000000);
			nanoSleep = (int) (elapsed % 1000000);
			if (msSleep <= 0) {
				lastTime = System.nanoTime();
				continue;
			}
			try {
				Thread.sleep(msSleep, nanoSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lastTime = System.nanoTime();

		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!isKeyPressed) {
			isKeyPressed = true;
			switch (gameState) {
			case START_GAME_STATE:
				this.LogForce("keyPressed() START_GAME_STATE");
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					gameState = GAME_PLAYING_STATE;
				}
				break;
			case GAME_PLAYING_STATE:
				this.Log("keyPressed() GAME_PLAYING_STATE");
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					this.JUMP(this.getPosX());
				}else	if (e.getKeyCode() == KeyEvent.VK_0) {//Added by k.yamauchi
					this.ManualMode = ! this.ManualMode; // Added by k.yamauchi
					if (this.ManualMode) this.Log("keyTyped() ManualMode!!");
					else this.Log("keyTyped() AutomaticMode!!");
//					mainCharacter.down(true);
				}
				break;
				
			case GAME_OVER_STATE:
				this.Log("keyPressed() GAME_OVER_STATE num_of_iterations=" + this.num_of_iterations);
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					this.PerformanceScoreUpdate(this.name);
					gameState = GAME_PLAYING_STATE;
					resetGame();
				}
				break;

			}
		}
	}
	
	void JUMP(double position) {
		if (position < 150 && position >= 0) mainCharacter.jump(2);
		else mainCharacter.jump(1); //mainCharacter.down(true);
	}
	
	// Revised by k.yamauchi
	// The averaged score was not reflect precise performance of the player.
	// Because if the player is a novice player, the player wastes almost iterations for training. 
	// As a result, the averaged score becomes very small value even if the player really get the ability to overcome the various cactus.
	// So, the revised version estimates the maximum score of the player.
	void PerformanceScoreUpdate(String name) {
		totalScore.add(mainCharacter.score);
		double MaxScore = 0;
		for(int i = 0; i<totalScore.size();i++) {
			if (MaxScore < totalScore.get(i)) {
				MaxScore = totalScore.get(i);
			}
		}
		
		String filename = "performance-" + name + ".dat";
		try {
			FileWriter fw = new FileWriter(filename);
			filewrite.datawrite(MaxScore, fw);
			fw.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
//	void PerformanceScoreUpdate(String name) {
//		totalScore.add(mainCharacter.score);
//		double avg = 0;
//		for(int i = 0; i<totalScore.size();i++) {
//			avg = avg + totalScore.get(i);
//		}
//		avg = avg/totalScore.size();
//		String filename = "performance-" + name + ".dat";
//		try {
//			FileWriter fw = new FileWriter(filename);
//			filewrite.datawrite(avg, fw);
//			fw.close();
//		} catch (Exception e1) {
//			// 
//			e1.printStackTrace();
//		}
//	}
	
	//Added by k.yamauchi
	public void forceStartGame(boolean withReset) {
		gameState = GAME_PLAYING_STATE;
		if (withReset) resetGame();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		isKeyPressed = false;
		if (gameState == GAME_PLAYING_STATE) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
//				mainCharacter.down(false);
			}
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		this.Log("keyTyped()");
	}

	private void resetGame() {
		enemiesManager.reset();
		mainCharacter.dead(false);
		mainCharacter.reset();
		mainCharacter.score = 0;
		mainCharacter.setSpeedX(4);
//		if (this.num_of_iterations % 3 == 0) this.ManualMode = true; // Added by k.yamauchi
//		else this.ManualMode = false;
//		if (this.ManualMode) {
//			gameState = this.START_GAME_STATE;
//		}
		isKeyPressed = false;
	}
	
	public void automatedKeyPress() {// Added by k.yamauchi: for controlling from NeuralNetActor
		this.Log("automaticKeyPress() Dino State= " + this.mainCharacter.getState());
		if (this.mainCharacter.getState() !=1 
				&& !isKeyPressed) {
			
			switch (gameState) {
			case GAME_OVER_STATE:// Added by k.yamauchi 2018.11.01
				this.Log("keyPressed() GAME_OVER_STATE");
				totalScore.add(mainCharacter.score);
				double avg = 0;
				for(int i = 0; i<totalScore.size();i++) {
					avg = avg + totalScore.get(i);
				}
				avg = avg/totalScore.size();
				String filename = "performance-" + name + ".dat";
				try {
					FileWriter fw = new FileWriter(filename);
					filewrite.datawrite(avg, fw);
					fw.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				gameState = GAME_PLAYING_STATE;
				isKeyPressed = false;
				resetGame();
				
				break;

			case GAME_PLAYING_STATE:
				this.JUMP(this.getPosX());
				this.Log("automatedKeyPress() Dino state = " + this.mainCharacter.getState());
				break;
			}
		}else {
			this.reward = -1; // Added by k.yamauchi 
		}
	}
	
	public void automatedKeyReleased() {// Added by k.yamauchi: for controlling from NeuralNetActor
		isKeyPressed = false;
		if (gameState == GAME_PLAYING_STATE) {
//				mainCharacter.down(true);
		}
	}

	public boolean isManualMode() {// Added by k.yamauchi: if this is true, supervised mode otherwise self-supervised mode.
		return ManualMode;
	}
	
	public void setManualMode(boolean data) {
		this.ManualMode = data;
	}
	
	void Log(String log) {
		if (this.DEBUG) System.out.println("GameScreen." + log);
	}
	void LogForce(String log) {
		System.out.println("GameScreen." + log);
	}
	void LogTerminate(String log) {
		System.out.println("Terminated at GameScreen." + log);
		System.exit(1);
	}
	
	// Added by k.yamauchi
	public synchronized void threadNotify() {
		notify();
	}
}