package bounce;

import java.util.Iterator;

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
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		bounces = 0;
		container.setSoundOn(true);
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.ball.render(g);
        bg.paddle.render(g);

		g.drawString("Bounces: " + bounces, 10, 30);
		for (Bang b : bg.explosions)
			b.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;
		
		if (input.isKeyDown(Input.KEY_W)) {
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(0f, -.01f)));
		}
		if (input.isKeyDown(Input.KEY_S)) {
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(0f, +.01f)));
		}
		if (input.isKeyDown(Input.KEY_A)) {
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(-.01f, 0)));
		}
		if (input.isKeyDown(Input.KEY_D)) {
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
				bg.ball.setX(bg.ScreenWidth - bg.ball.getCoarseGrainedWidth());
			}
			if (bg.ball.getCoarseGrainedMinX() < 0) {
				bg.ball.setCoarseGrainedMinX(1);
			}

		} else if (bg.ball.getCoarseGrainedMaxY() >= bg.ScreenHeight
				|| bg.ball.getCoarseGrainedMinY() <= 0) {
			bg.ball.bounce(0);
			bounced = true;

			if (bg.ball.getCoarseGrainedMaxY() >= bg.ScreenHeight) {
				bg.ball.setY(bg.ScreenHeight - bg.ball.getCoarseGrainedHeight());
				lifeLost = true;
			}
			if (bg.ball.getCoarseGrainedMinY() < 0) {
				bg.ball.setCoarseGrainedMinY(1);
			}
		}
		if (bounced){
			bounces++;
		}

		if(lifeLost){
            bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
        }
		bg.ball.update(delta);

		//control the paddle

        if (input.isKeyDown(Input.KEY_RIGHT)) {
            if (bg.paddle.getxLoc() + bg.paddle.getWidth() < bg.ScreenWidth )
                bg.paddle.movePaddleRight();
        }
        if (input.isKeyDown(Input.KEY_LEFT)) {
            if (bg.paddle.getxLoc() >= 5)
                bg.paddle.movePaddleLeft();
        }

        // check if the paddle is bouncing the ball
        if (bg.ball.getCoarseGrainedMaxY() >= bg.ScreenHeight - bg.paddle.getHeight() ){
            System.out.println(bg.ball.getCoarseGrainedMaxY());
            if (checkPaddleReflection(bg.paddle , bg.ball) ){
                bounces++;
                bg.ball.bounce(0);

                //makes sure the ball doesn't get stuck in the paddle
                if (bg.ball.getCoarseGrainedMaxY() > bg.ScreenHeight - bg.paddle.getHeight() ) {
                    bg.ball.setY(bg.ScreenHeight - bg.paddle.getHeight() - bg.ball.getCoarseGrainedHeight());
                }
            }
        }

		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

		if (bounces >= 500) {
			((GameOverState)game.getState(BounceGame.GAMEOVERSTATE)).setUserScore(bounces);
			game.enterState(BounceGame.GAMEOVERSTATE);
		}
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}

	public boolean checkPaddleReflection(Paddle paddle, Ball ball){
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
	
}