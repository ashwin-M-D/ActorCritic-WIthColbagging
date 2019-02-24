package game.gameobject;

import java.applet.Applet;
import java.applet.AudioClip;
//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import game.util.Animation;
//import game.util.Resource;

public class MainCharacter {

	public static final int LAND_POSY = 80;
	public static final float GRAVITY = 0.4f;
	
	private static final int NORMAL_RUN = 0;
	private static final int JUMPING = 1;
	private static final int DOWN_RUN = 2;
	private static final int DEATH = 3;
	private static final int TOUCH_DOWN = 4;
	
	private float posY;
	private float posX;
	private float speedX;
	private float ratioGravity=1.0F;
	private float speedY;
	private Rectangle rectBound;
	
	public int score = 0;
	//private int reward = 0;
	
	private int state = NORMAL_RUN;
	
	private Animation normalRunAnim;
	private BufferedImage jumping;
	private Animation downRunAnim;
	private BufferedImage deathImage;
	
	private AudioClip jumpSound;
	private AudioClip deadSound;
	private AudioClip scoreUpSound;
	
	private Enemy myenemy; // Added by k.yamauchi
	private int current_enemy_position=0; // Added by k.yamauchi
	
	public MainCharacter() {
		posX = 50;
		posY = LAND_POSY;
		rectBound = new Rectangle();
		normalRunAnim = new Animation(90);
		// To include png images into jar file, URLs are set as follows.
		URL mainCharacter1Url = this.getClass().getResource("main-character1.png");
		URL mainCharacter2Url = this.getClass().getResource("main-character2.png");
		URL mainCharacter3Url = this.getClass().getResource("main-character3.png");
		URL mainCharacter5Url = this.getClass().getResource("main-character5.png");
		URL mainCharacter6Url = this.getClass().getResource("main-character6.png");
		URL deathUrl = this.getClass().getResource("main-character4.png");
		BufferedImage MainCharacter1img = null;
		BufferedImage MainCharacter2img = null;
		this.jumping = null;
		BufferedImage MainCharacter5img = null;
		BufferedImage MainCharacter6img = null;
		try {
			MainCharacter1img = ImageIO.read(mainCharacter1Url);//Images are read.
			MainCharacter2img = ImageIO.read(mainCharacter2Url);
			this.jumping= ImageIO.read(mainCharacter3Url);
			MainCharacter5img = ImageIO.read(mainCharacter5Url);
			MainCharacter6img = ImageIO.read(mainCharacter6Url);
			this.deathImage = ImageIO.read(deathUrl);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		normalRunAnim.addFrame(MainCharacter1img);
		normalRunAnim.addFrame(MainCharacter2img);
		downRunAnim = new Animation(90);
		downRunAnim.addFrame(MainCharacter5img);
		downRunAnim.addFrame(MainCharacter6img);
		
		try {
			jumpSound =  Applet.newAudioClip(new URL("file","","data/jump.wav"));
			deadSound =  Applet.newAudioClip(new URL("file","","data/dead.wav"));
			scoreUpSound =  Applet.newAudioClip(new URL("file","","data/scoreup.wav"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public float getSpeedX() {
		return speedX;
	}
	public int getStateJump() {
		return state;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}
	
	public void draw(Graphics g) {
		switch(state) {
			case NORMAL_RUN:
				g.drawImage(normalRunAnim.getFrame(), (int) posX, (int) posY, null);
				break;
			case JUMPING:
				g.drawImage(jumping, (int) posX, (int) posY, null);
				break;
			case DOWN_RUN:
				g.drawImage(downRunAnim.getFrame(), (int) posX, (int) (posY + 20), null);
				break;
			case DEATH:
				g.drawImage(deathImage, (int) posX, (int) posY, null);
				break;
		}
//		Rectangle bound = getBound();
//		g.setColor(Color.RED);
//		g.drawRect(bound.x, bound.y, bound.width, bound.height);
	}
	
	public void update() {
		normalRunAnim.updateFrame();
		downRunAnim.updateFrame();
		if(posY >= LAND_POSY) {
			posY = LAND_POSY;
			if(state != JUMPING) {
				state = NORMAL_RUN;
			}else {
				state = TOUCH_DOWN;
				this.current_enemy_position = this.myenemy.getPosX();//Added by k.yamauchi (to record touch down position)
			}
		} else {
			speedY += GRAVITY * this.ratioGravity;
			posY += speedY;
		}
		//speedX = 10;
		if(speedX<10)
		speedX = (float) (speedX + 0.00001*speedX);
	}
	
	public void jump(int strength) {
		if(posY >= LAND_POSY) {
			if(jumpSound != null) {
				jumpSound.play();
			}
			switch(strength) {
			case 1: speedY = -7.3f; this.ratioGravity = 0.8F; break; //previous setting: speedY = -7.5F;
			case 2: speedY = -18f; this.ratioGravity = 3.5F; break; // previous setting: speedY = -15f; this.ratioGravity = 3F;
//			case 3: speedY = -15.5f; this.ratioGravity = 1.2F; break;
			}
			posY += speedY;
			state = JUMPING;
		}
	}
	
	public void down(boolean isDown) {
		if(state == JUMPING) {
			return;
		}
		if(isDown) {
			state = DOWN_RUN;
		} else {
			state = NORMAL_RUN;
		}
	}
	
	public Rectangle getBound() {
		rectBound = new Rectangle();
		if(state == DOWN_RUN) {
			rectBound.x = (int) posX + 5;
			rectBound.y = (int) posY + 20;
			rectBound.width = downRunAnim.getFrame().getWidth() - 10;
			rectBound.height = downRunAnim.getFrame().getHeight();
		} else {
			rectBound.x = (int) posX + 5;
			rectBound.y = (int) posY;
			rectBound.width = normalRunAnim.getFrame().getWidth() - 10;
			rectBound.height = normalRunAnim.getFrame().getHeight();
		}
		return rectBound;
	}
	
	public void dead(boolean isDeath) {
		if(isDeath) {
			state = DEATH;
		} else {
			state = NORMAL_RUN;
		}
	}
	
	public void reset() {
		posY = LAND_POSY;
		this.ratioGravity = 1.0F;
	}
	
	public void playDeadSound() {
		deadSound.play();
	}
	
	public void upScore() {
		score += 20;
		if(score % 100 == 0) {
			scoreUpSound.play();
		}
	}	
	
	// To calculate reward, system needs the information of current x position.
	public float getPositionX() {//Added by k.yamauchi.
		return this.posX;
	}
	
	// To represent current status correctly, the positionY is also needed.
	public float getPositionY() {//Added by k.yamauchi
		return this.posY;
	}
	
	// Get cactus position immediately after touch down. This information is essential for calculating rewards.
	public int getEnemyPosition() {//Added by k.yamauchi
		return this.current_enemy_position;
	}
	
	//To get correct position of the cactus immediately after touch down, Dino object is imported as myenemy.
	// myenemy is used in update() method.
	public void setMyenemy(Enemy myenemy) {//Added by k.yamauchi
		this.myenemy = myenemy;
	}
	public int getState() {// Added by k.yamauchi
		return this.state;
	}
}
