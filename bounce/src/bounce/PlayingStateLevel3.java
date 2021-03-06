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
    int numberOfBallActive;
    ArrayList <Brick> brickArray;
    ArrayList <Brick> brickArrayIndestructible;

    @Override
    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) {
        BounceGame bg = (BounceGame)game;

        //bounces = 0;
        numberOfBallActive = 60;
        container.setSoundOn(true);

        //reset the ball
        bg.ball.setVelocity(new Vector(randomSign() * .2f, -.3f));
        bg.ball.setPosition(bg.ScreenWidth / 2, bg.ScreenHeight / 2);
        bg.paddle.setScale(1);

        //initialize bricks
        brickArray = new ArrayList<Brick>(numberOfBallActive);
        brickArrayIndestructible = new ArrayList<Brick>( 30);
        for (int b = 0; b < 5; b++){
            brickArrayIndestructible.add(new Brick((b * 160) + 190, 30, true, true));
        }
        for (int b = 0; b < 5; b++){
            brickArrayIndestructible.add(new Brick((b * 160) + 110, 70, true, true));
        }
        for (int b = 0; b < 20; b++){
            brickArray.add(new Brick((b * 40) + 10, 10, true));
        }
        for (int b = 0; b < 20; b++){
            brickArray.add(new Brick((b * 40) + 10 , 50 , true));
        }
        for (int b = 0; b < 20; b++){
            brickArray.add(new Brick((b * 40) + 10, 90, true));
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
        g.drawString("Lives: " + bg.getLivesRemaining(), 10, 50);
        g.drawString("Level 3" , 10, 70);

        for (Bang b : bg.explosions)
            b.render(g);
        for (Brick b : brickArray){
            if (!b.getDestroyed())
                b.render(g);
        }
        for (Brick b : brickArrayIndestructible){
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


        bg.toggleDemoMode();
        if (bg.isDemoModeOn()){
            bg.playDemoMode();
        }

        // bounce the ball...
        boolean lifeLost = false;

        lifeLost = bg.bounceBallScreen();


        if(lifeLost){
            bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
            bg.loseLife();
            bg.ball.setPosition(bg.ScreenWidth / 2, bg.ScreenHeight /2);
            bg.ball.setVelocity(new Vector(randomSign() * .2f, -.3f));

        }
        bg.ball.update(delta);

        //control the paddle
        bg.controlPaddle();
        bg.paddle.update(delta);


        // check if the paddle is bouncing the ball
        bg.bounceBallPaddle();

        boolean removedBrick = false;
        //check for collision with the ball and paddles
        if (bg.ball.getPosition().getY() < bg.ScreenHeight) {
            for (Brick b : brickArray) {
                if (!b.getDestroyed()) { //if ball is active
                    if (bg.ball.collides(b) != null) {
                        if (b.getCracked()) {
                            numberOfBallActive--;
                            b.setDestroyed(true);
                            b.setPosition(-100, -100);    //remove off screen
                            removedBrick = true;

                        } else {
                            b.setCracked(true);

                        }

                        //bg.ball.setVelocity(new Vector(bg.ball.getVelocity().getX(), -bg.ball.getVelocity().getY()));

                        bg.reflectBallFromBrick(b, bg.ball);

                    }
                }
            }
        }

        //check collisions with "bricks"
        if (bg.ball.getPosition().getY() < bg.ScreenHeight) {
            for (Brick b : brickArrayIndestructible) {
                if (!b.getDestroyed()) { //if ball is active
                    if (bg.ball.collides(b) != null) {
                        bg.reflectBallFromBrick(b, bg.ball);
                    }
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
            bg.incrementCurrentScore();
            clearDestroyedBricks();
            System.out.println("Brick Left :" + brickArray.size());
        }


        if (bg.getLivesRemaining() <= 0 || numberOfBallActive == 0) {
            game.enterState(BounceGame.GAMEOVERSTATE);
        }

        if (brickArray.size() == 0){
            game.enterState(BounceGame.GAMEOVERSTATE);
        }


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
