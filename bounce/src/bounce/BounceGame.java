package bounce;

import java.util.ArrayList;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;

import jig.Vector;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A Simple Game of Bounce.
 * 
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 * 
 * In the playing state, our game displays a moving rectangular "ball" that
 * bounces off the sides of the game container. The ball can be controlled by
 * input from the user.
 * 
 * When the ball bounces, it appears broken for a short time afterwards and an
 * explosion animation is played at the impact site to add a bit of eye-candy
 * additionally, we play a short explosion sound effect when the game is
 * actively being played.
 * 
 * Our game also tracks the number of bounces and syncs the game update loop
 * with the monitor's refresh rate.
 * 
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 * 
 * Sound resources courtesy of DJ Chronos:
 * http://www.freesound.org/people/DJ%20Chronos/sounds/123236/
 * 
 * 
 * @author wallaces
 * 
 */
public class BounceGame extends StateBasedGame {
	
	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int PLAYINGSTATELEVEL2 = 2;
	public static final int PLAYINGSTATELEVEL3 = 3;

	public static final int GAMEOVERSTATE = 4;
	
	public static final String BALL_BALLIMG_RSC = "bounce/resource/ball.png";
	public static final String BALL_BROKENIMG_RSC = "bounce/resource/brokenball.png";
	public static final String GAMEOVER_BANNER_RSC = "bounce/resource/gameover.png";
	public static final String STARTUP_BANNER_RSC = "bounce/resource/PressSpace.png";
	public static final String BANG_EXPLOSIONIMG_RSC = "bounce/resource/explosion.png";
	public static final String BANG_EXPLOSIONSND_RSC = "bounce/resource/explosion.wav";
	public static final String PADDLE_RSC = "bounce/resource/basic-small-rectangle.png";
	public static final String BALL_WHITE = "bounce/resource/white-ball.png";

	public final int ScreenWidth;
	public final int ScreenHeight;

	Ball ball;
	PaddleEntity paddle;
	ArrayList<Bang> explosions;

	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public BounceGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;

		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
		explosions = new ArrayList<Bang>(10);
				
	}


	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new StartUpState());
		addState(new GameOverState());
		addState(new PlayingStateLevel2());
		addState(new PlayingStateLevel3());
		addState(new PlayingState());


		
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
		ResourceManager.loadSound(BANG_EXPLOSIONSND_RSC);	

		// preload all the resources to avoid warnings & minimize latency...
		ResourceManager.loadImage(BALL_BALLIMG_RSC);
		ResourceManager.loadImage(BALL_BROKENIMG_RSC);
		ResourceManager.loadImage(GAMEOVER_BANNER_RSC);
		ResourceManager.loadImage(STARTUP_BANNER_RSC);
		ResourceManager.loadImage(BANG_EXPLOSIONIMG_RSC);
		ResourceManager.loadImage(BALL_WHITE);
		
		ball = new Ball(ScreenWidth / 2, ScreenHeight / 2, .4f, .4f);
		paddle = new PaddleEntity(ScreenWidth / 2, ScreenHeight - 10, 100, 20);
		paddle.setScale(1);

	}
	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new BounceGame("Bounce!", 800, 600));
			app.setDisplayMode(800, 600, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

	//control the velocity of the ball
	public void controlBallSpeed(){
		Input input = this.getContainer().getInput();

		if (input.isKeyDown(Input.KEY_W)) {
			if (ball.getVelocity().getY() > -1f)
				ball.setVelocity(ball.getVelocity().add(new Vector(0f, -.01f)));
		}
		if (input.isKeyDown(Input.KEY_S)) {
			if (ball.getVelocity().getY() < 1f)
				ball.setVelocity(ball.getVelocity().add(new Vector(0f, +.01f)));
		}
		if (input.isKeyDown(Input.KEY_A)) {
			if (ball.getVelocity().getX() > -1f)
				ball.setVelocity(ball.getVelocity().add(new Vector(-.01f, 0)));
		}
		if (input.isKeyDown(Input.KEY_D)) {
			if (ball.getVelocity().getX() < 1f)
				ball.setVelocity(ball.getVelocity().add(new Vector(+.01f, 0f)));
		}
	}

	//bounce the ball of the edges of scree, return true if life was lost
	public boolean bounceBallScreen(){
		boolean lifeLost = false;
		if (ball.getCoarseGrainedMaxX() >= ScreenWidth
				|| ball.getCoarseGrainedMinX() <= 0) {
			ball.bounce(90);
			ball.incrementBall();

			//check if ball is stuck
			if (ball.getCoarseGrainedMaxX() > ScreenWidth) {
				ball.setX(ScreenWidth - ball.getCoarseGrainedWidth()/2 - 1);
			}
			if (ball.getCoarseGrainedMinX() < 0) {
				ball.setX(1 + ball.getCoarseGrainedWidth()/2);
			}

		} else if (ball.getCoarseGrainedMaxY() >= ScreenHeight
				|| ball.getCoarseGrainedMinY() <= 0) {
			ball.bounce(0);
			ball.incrementBall();

			if (ball.getCoarseGrainedMaxY() >= ScreenHeight) {
				//bg.ball.setY(bg.ScreenHeight - bg.ball.getCoarseGrainedHeight());
				lifeLost = true;
			}
			if (ball.getCoarseGrainedMinY() < 0) {
				ball.setY(1 + ball.getCoarseGrainedHeight()/2);
				ball.setVelocity(new Vector(ball.getVelocity().getX(), Math.abs(ball.getVelocity().getY() ) ));

			}
		}

		return lifeLost;
	}

	//control paddle
	public void controlPaddle(){
		Input input = this.getContainer().getInput();
		if (input.isKeyDown(Input.KEY_RIGHT)) {
			if (paddle.getxLoc() + paddle.getCoarseGrainedWidth()/2 < ScreenWidth )
				paddle.movePaddleRight();
		}
		if (input.isKeyDown(Input.KEY_LEFT)) {
			if (paddle.getxLoc() - paddle.getCoarseGrainedWidth()/2 >= 5)
				paddle.movePaddleLeft();
		}
	}

	public void controlLevel(){
		Input input = this.getContainer().getInput();
		if (input.isKeyDown(Input.KEY_1)){
			enterState(1);
		}
		if (input.isKeyDown(Input.KEY_2)){
			enterState(2);
		}
		if (input.isKeyDown(Input.KEY_3)){
			enterState(3);
		}

	}

	
}
