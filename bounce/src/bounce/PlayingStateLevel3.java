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

import javax.swing.text.html.HTMLDocument;

class PlayingStateLevel3 extends BasicGameState{
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

        //bounces = 0;
        livesRemain = 3;
        numberOfBallActive = 120;
        container.setSoundOn(true);

        //reset the ball
        bg.ball.setVelocity(new Vector(randomSign() * .3f, -.4f));
        bg.ball.setPosition(bg.ScreenWidth / 2, bg.ScreenHeight / 2);
        bg.paddle.setScale(.5f);

        //initialize bricks
        brickArray = new ArrayList<Brick>(100);
        for (int b = 0; b < 40; b++){
            brickArray.add(new Brick((b * 20) + 10, 10, true));
        }
        for (int b = 0; b < 40; b++){
            brickArray.add(new Brick((b * 20) + 10 , 35 , true));
        }
        for (int b = 0; b < 40; b++){
            brickArray.add(new Brick((b * 20) + 10 , 60, true));
        }
        for (Brick b : brickArray){
                b.setScale(.5f);
        }
    }
    @Override
    public void render(GameContainer container, StateBasedGame game,
                       Graphics g) throws SlickException {
        BounceGame bg = (BounceGame)game;

        bg.ball.render(g);
        bg.paddle.render(g);

        g.drawString("Bounces: " + bg.ball.getBouncesBall(), 10, 30);
        g.drawString("Lives: " + livesRemain, 10, 50);
        g.drawString("Level 3" , 10, 70);

        for (Bang b : bg.explosions)
            b.render(g);
        for (Brick b : brickArray){
            if (!b.getDestroyed())
                b.render(g);
        }

    }

    @Override
    public void update(GameContainer container, StateBasedGame game,
                       int delta) throws SlickException {

        Input input = container.getInput();
        BounceGame bg = (BounceGame)game;

        bg.controlBallSpeed();
        bg.controlLevel();

        // bounce the ball...
        boolean lifeLost = false;

        lifeLost = bg.bounceBallScreen();


        if(lifeLost){
            bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
            livesRemain--;
            bg.ball.setPosition(bg.ScreenWidth / 2, bg.ScreenHeight /2);
            bg.ball.setVelocity(new Vector(randomSign() * .3f, -.4f));

        }
        bg.ball.update(delta);

        //control the paddle
        bg.controlPaddle();
        bg.paddle.update(delta);


        // check if the paddle is bouncing the ball
        if (bg.ball.collides(bg.paddle) != null){
            bg.ball.bounce(0);
            bg.ball.incrementBall();

            //makes sure the ball doesn't get stuck in the paddle
            if (bg.ball.getCoarseGrainedMaxY() > bg.ScreenHeight - bg.paddle.getHeight() ) {
                bg.ball.setY(bg.ScreenHeight - bg.paddle.getHeight() - bg.ball.getCoarseGrainedHeight()/2 - 1);
                bg.ball.setVelocity(new Vector(bg.ball.getVelocity().getX(), -Math.abs(bg.ball.getVelocity().getY() ) ));
            }
        }

        //check for collision with the ball and paddles
        for (Brick b : brickArray){
            if (!b.getDestroyed()) { //if ball is active
                if (bg.ball.collides(b) != null) {
                    if (b.getCracked()){
                        numberOfBallActive--;
                        b.setDestroyed(true);
                        b.setPosition(-100, -100);    //remove off screen
                    }
                    else{
                        b.setCracked(true);
                    }

                    bg.ball.setVelocity(new Vector(bg.ball.getVelocity().getX(), -bg.ball.getVelocity().getY()));


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
        for (Iterator <Brick> i = brickArray.iterator(); i.hasNext(); ){
            if (i.next().getDestroyed()){
                i.remove();
                System.out.println("Brick Removed");
            }
        }


        if (bounces >= 500 || livesRemain <= 0 || numberOfBallActive == 0) {
            ((GameOverState)game.getState(BounceGame.GAMEOVERSTATE)).setUserScore(bounces);
            game.enterState(BounceGame.GAMEOVERSTATE);
        }

        //clearDestroyedBricks();
    }

    @Override
    public int getID() {
        return BounceGame.PLAYINGSTATELEVEL3;
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

    public void setUserScore(int bounces) {
        this.bounces = bounces;
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
