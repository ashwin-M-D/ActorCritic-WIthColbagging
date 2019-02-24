package game.gameobject;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

//import game.util.Resource;

public class EnemiesManager {
	private final boolean DEBUG = false;
	private BufferedImage cactus1;
	private BufferedImage cactus2;
	private BufferedImage cactus3;// Added by k.yamauchi
	private BufferedImage cactus4;
	
	private Random rand;
	
	private int width = 0;
	private int height = 0;
	private int xPos = 0;
	
	private List<Enemy> enemies;
	private MainCharacter mainCharacter;
	private Cactus current_cactus; // Added by k.yamauchi. For getting current position of the cactus.
	
	public EnemiesManager(MainCharacter mainCharacter) {
		rand = new Random();
		URL cactus1URL = this.getClass().getResource("cactus1.png");
		URL cactus2URL = this.getClass().getResource("cactus2.png");
		URL cactus3URL = this.getClass().getResource("cactus3.png");
		URL cactus4URL = this.getClass().getResource("cactus4.png");
		try {
			cactus1 = ImageIO.read(cactus1URL);
			cactus2 = ImageIO.read(cactus2URL);
			cactus3 = ImageIO.read(cactus3URL);
			cactus4 = ImageIO.read(cactus4URL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		enemies = new ArrayList<Enemy>();
		this.mainCharacter = mainCharacter;
		enemies.add(createEnemy());
	}
	
	public int getWidthCactus() {
		return width;
	}
	
	public int getHeightCactus() {
		return height;
	}
	
	public int getPosX() {
		return xPos;
	}
	
	
	public void update() {
		synchronized(enemies) {// revised by k.yamauchi
			for(Enemy e : enemies) {
			if (e != null) e.update();
			}
			if (enemies.size() > 0) {
				this.Log("update() enemies.size=" + enemies.size());
				xPos = enemies.get(0).getPosX();
				Enemy enemy = enemies.get(0);
				if(enemy.isOutOfScreen()) {
					mainCharacter.upScore();
					enemies.removeAll(enemies);
					enemies.add(createEnemy());
				}
			}else {
				enemies.add(createEnemy());
			}
		}
	}
	
	public  void draw(Graphics g) {
		synchronized(enemies) {// revised by k.yamauchi
			for(Enemy e : enemies) {
				e.draw(g);
			}
		}
	}
	
	private Enemy createEnemy() {
		// if (enemyType = getRandom)
		int type = rand.nextInt(4);
		this.Log("createEnemy() type= " + type);
		switch(type) { // modified by k.yamauchi
		case 0: // added by k.yamauchi
		case 1: // modified by k.yamauchi
			width = cactus1.getWidth() - 10;
			height = cactus1.getHeight() - 10;
			this.current_cactus = new Cactus(mainCharacter, 800, cactus1.getWidth() - 10, cactus1.getHeight() - 10, cactus1); // Modified by k.yamauchi
			return this.current_cactus; // Modified by k.yamauchi
		case 2: // modified by k.yamauchi
			width = cactus2.getWidth() - 10;
			height = cactus2.getHeight() - 10;
			this.current_cactus = new Cactus(mainCharacter, 800, cactus2.getWidth() - 10, cactus2.getHeight() - 10, cactus2);// Modified by k.yamauchi
			return this.current_cactus; // Modified by k.yamauchi
		case 3: // modified by k.yamauchi
			width = cactus4.getWidth() - 10;
			height = cactus4.getHeight() - 10;
			this.current_cactus = new Cactus(mainCharacter, 800, cactus4.getWidth() - 10, cactus4.getHeight() - 10, cactus4);// Modified by k.yamauchi
			return this.current_cactus; // Modified by k.yamauchi
		default:// added by k.yamauchi
			width = cactus3.getWidth() - 10;
			height = cactus3.getHeight() - 10;
			this.current_cactus = new Cactus(mainCharacter, 800, cactus3.getWidth() - 10, cactus3.getHeight() - 10, cactus3);// Added by k.yamauchi
			return this.current_cactus; // added by k.yamauchi
		}
	}
	
	public boolean isCollision() {
		for(Enemy e : enemies) {
			if (mainCharacter.getBound().intersects(e.getBound())) {
				return true;
			}
		}
		return false;
	}
	
	// Current position of the cactus is very important to calculate reward. 
	// If the t-rex get over the cactus position without collision, it will get reward.
	public int getCurrentPositionOfCactus() {//Added by k.yamauchi 
		return this.current_cactus.getPosX();
	}
	
	public void reset() {
		synchronized(enemies) {// revised by k.yamauchi
			enemies.clear();
			enemies.add(createEnemy());
			this.mainCharacter.setMyenemy(enemies.get(0));//added by k.yamauchi
		}
	}
	
	public Enemy getEnemyObj() {//Added by k.yamauchi
		return this.current_cactus;
	}
	
	void Log(String log) {
		if (this.DEBUG) System.out.println("EnemiesManager." + log);
	}
	
}
