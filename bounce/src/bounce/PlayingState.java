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
	int livesRemain;
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
		livesRemain = 5;
		numberOfBallActive = 10;
		container.setSoundOn(true);

		//reset the ball
        bg.ball.setVelocity(new Vector(randomSign() * .1f, randomSign() * .2f));
        bg.ball.setPosition(bg.ScreenWidth / 2, bg.ScreenHeight / 2);

		//initialize bricks
		brickArray = new ArrayList<Brick>(10);
		for (int b = 0; b < 10; b++){
			brickArray.add(new Brick((b * 78) + 50 , 30));
			//System.out.println("just made new brick");
		}
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.ball.render(g);
        bg.paddle.render(g);

		g.drawString("Bounces: " + bounces, 10, 30);
		g.drawString("Lives: " + livesRemain, 10, 50);
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
		
		if (input.isKeyDown(Input.KEY_W)) {
		    if (bg.ball.getVelocity().getY() > -1f)
			    bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(0f, -.01f)));
		}
		if (input.isKeyDown(Input.KEY_S)) {
            if (bg.ball.getVelocity().getY() < 1f)
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(0f, +.01f)));
		}
		if (input.isKeyDown(Input.KEY_A)) {
            if (bg.ball.getVelocity().getX() > -1f)
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(-.01f, 0)));
		}
		if (input.isKeyDown(Input.KEY_D)) {
            if (bg.ball.getVelocity().getX() < 1f)
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(+.01f, 0f)));
		}
		// bounce the ball...
		boolean bounced = false;
		boolean lifeLost = false;
		if (bg.ball.getCoarseGrainedMaxX() >= bg.ScreenWidth
				|| bg.ball.getCoarseGrainedMinX() <= 0) {
			bg.ball.bounce(90);
			bounced = true;
			if (bg.ball.getCoarseGrainedMaxX() > bg.ScreenWidth) {
				bg.ball.setCoarseGrainedMinX(bg.ScreenWidth - bg.ball.getCoarseGrainedWidth() - 1);
                bg.ball.update(delta);
			}
			if (bg.ball.getX() < 0) {
				bg.ball.setX(1);
                bg.ball.update(delta);
			}

		} else if (bg.ball.getCoarseGrainedMaxY() >= bg.ScreenHeight
				|| bg.ball.getCoarseGrainedMinY() <= 0) {
			bg.ball.bounce(0);
			bounced = true;

			if (bg.ball.getCoarseGrainedMaxY() >= bg.ScreenHeight) {
				//bg.ball.setY(bg.ScreenHeight - bg.ball.getCoarseGrainedHeight());
				lifeLost = true;
			}
			if (bg.ball.getCoarseGrainedMinY() < 0) {
				bg.ball.setCoarseGrainedMinY(1);
				bg.ball.setVelocity(new Vector(bg.ball.getVelocity().getX(), Math.abs(bg.ball.getVelocity().getY() ) ));

			}
		}
		if (bounced){
			bounces++;
			System.out.println(bg.ball.getPosition());
			System.out.println(bg.ball.getCoarseGrainedMinX());
		}

		if(lifeLost){
            bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
            livesRemain--;
            bg.ball.setPosition(bg.ScreenWidth / 2, bg.ScreenHeight /2);
            bg.ball.setVelocity(new Vector(randomSign() * .1f, -.2f));

        }
		bg.ball.update(delta);

		//control the paddle

        if (input.isKeyDown(Input.KEY_RIGHT)) {
            if (bg.paddle.getxLoc() + bg.paddle.getCoarseGrainedWidth()/2 < bg.ScreenWidth - 5 )
                bg.paddle.movePaddleRight();
        }
        if (input.isKeyDown(Input.KEY_LEFT)) {
            if (bg.paddle.getxLoc() - bg.paddle.getCoarseGrainedWidth()/2 >= 5)
                bg.paddle.movePaddleLeft();
        }

        bg.paddle.update(delta);


        // check if the paddle is bouncing the ball
		if (bg.ball.collides(bg.paddle) != null){
			bg.ball.bounce(0);

			//makes sure the ball doesn't get stuck in the paddle
			if (bg.ball.getCoarseGrainedMaxY() > bg.ScreenHeight - bg.paddle.getHeight() ) {
				bg.ball.setCoarseGrainedMinY(bg.ScreenHeight - bg.paddle.getHeight() - bg.ball.getCoarseGrainedHeight() - 1);
				bg.ball.setVelocity(new Vector(bg.ball.getVelocity().getX(), -Math.abs(bg.ball.getVelocity().getY() ) ));
			}
		}

		//check for collision with the ball and paddles
		for (Brick b : brickArray){
			if (!b.getDestroyed()) { //if ball is active
				if (bg.ball.collides(b) != null) {
					b.setDestroyed(true);
					bg.ball.bounce(0);
					numberOfBallActive--;
					b.setPosition(-100, -100);    //remove off screen
					//brickArray.remove(b);
				}
			}
		}


		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

		if (bounces >= 500 || livesRemain <= 0 ) {
			((GameOverState)game.getState(BounceGame.GAMEOVERSTATE)).setUserScore(bounces);
			game.enterState(BounceGame.GAMEOVERSTATE);
		}

		if ( numberOfBallActive == 0){
            ((PlayingStateLevel2)game.getState(BounceGame.PLAYINGSTATELEVEL2)).setUserScore(bounces);
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
	
}