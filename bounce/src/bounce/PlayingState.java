package bounce;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 * 
 * Transitions From StartUpState
 * 
 * Transitions To GameOverState
 */
class PlayingState extends BasicGameState {
	int bounces;
	int numberOfBallActive;
	ArrayList <Brick> brickArray;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
        BounceGame bg = (BounceGame)game;

		bounces = 0;
		bg.setLivesReaning(3);
		numberOfBallActive = 20;
		container.setSoundOn(true);

        bg.paddle.setScale(1);

		//reset the ball
        bg.ball.setVelocity(new Vector(randomSign() * .1f, -.3f));
        bg.ball.setPosition(bg.ScreenWidth / 2, bg.ScreenHeight / 2);
        bg.ball.setBouncesBall(0);

		//initialize bricks
		brickArray = new ArrayList<Brick>(10);
		for (int b = 0; b < 20; b++){
			brickArray.add(new Brick((b * 40) + 20 , 20));
		}

//		for (Brick b : brickArray){
//			System.out.println("Width" + b.getCoarseGrainedWidth() + "Height : "  + b.getCoarseGrainedHeight());
//		}
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.ball.render(g);
        bg.paddle.render(g);

		g.drawString("Bounces: " + bg.ball.getBouncesBall() , 10, 30);
		g.drawString("Lives: " + bg.getLivesRemaining(), 10, 50);
        g.drawString("Level 1" , 10, 70);

		for (Bang b : bg.explosions)
			b.render(g);
		for (Brick b : brickArray)
			b.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {


		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;

		bg.controlBallSpeed();
        bg.controlLevel();

		bg.toggleDemoMode();
		if (bg.isDemoModeOn()){
			bg.playDemoMode();
		}

		// bounce the ball...
		boolean lifeLost = false;

		lifeLost = bg.bounceBallScreen();
        bg.ball.update(delta);

		if(lifeLost){
            bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
            bg.loseLife();
            bg.ball.setPosition(bg.ScreenWidth / 2, bg.ScreenHeight /2);
            bg.ball.setVelocity(new Vector(randomSign() * .1f, -.3f));

        }

		//control the paddle
        bg.controlPaddle();
        bg.paddle.update(delta);



        // check if the paddle is bouncing the ball
		bg.bounceBallPaddle();

		//check for collision with the ball and bricks
		boolean removedBrick = false;
		for (Brick b : brickArray){
			if (!b.getDestroyed()) { //if ball is active
				if (bg.ball.collides(b) != null) {
					b.setDestroyed(true);
					bg.reflectBallFromBrick(b, bg.ball);
					numberOfBallActive--;
					b.setPosition(-100, -100);    //remove off screen
					//brickArray.remove(b);
					removedBrick = true;
				}
			}
		}


		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

		//clear removed bricks
		if (removedBrick) {
			clearDestroyedBricks();
			System.out.println("Brick Left :" + brickArray.size());
		}



        if (bg.getLivesRemaining() <= 0 ) {
			game.enterState(BounceGame.GAMEOVERSTATE);
		}

		if ( brickArray.size() == 0){
		    game.enterState(BounceGame.PLAYINGSTATELEVEL2);
        }
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}

	public boolean checkPaddleReflection(PaddleEntity paddle, Ball ball){
	    float minPaddle = paddle.getxLoc();
	    float maxPaddle = minPaddle + paddle.getWidth();
	    float minBall = ball.getCoarseGrainedMinX();
	    float maxBall = ball.getCoarseGrainedMaxX();

	    if (minBall >= minPaddle && minBall <= maxPaddle )
	        return true;
	    else if (maxBall >= minPaddle && maxBall <= maxPaddle)
	        return true;
	    else
	        return false;
    }

    public static float getRandomFloat(){
        Random r = new Random();
        float random = .1f + r.nextFloat() * (.3f + .1f);
        return random;
        }

        public int randomSign(){
            Random r = new Random();
            return r.nextBoolean() ? 1 : -1;
        }

	public void clearDestroyedBricks(){
		Iterator itr = brickArray.iterator();
		while (itr.hasNext())
		{
			Brick x = (Brick) itr.next();
			if (x.getDestroyed())
				itr.remove();
		}
	}
	
}