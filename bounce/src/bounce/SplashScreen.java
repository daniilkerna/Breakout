package bounce;

import jig.ResourceManager;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.Iterator;

public class SplashScreen extends BasicGameState {

        @Override
        public void init (GameContainer container, StateBasedGame game)
			throws SlickException {
    }

        @Override
        public void enter (GameContainer container, StateBasedGame game){
        container.setSoundOn(false);
    }


        @Override
        public void render (GameContainer container, StateBasedGame game,
            Graphics g) throws SlickException {
        BounceGame bg = (BounceGame) game;

        g.drawImage(ResourceManager.getImage(BounceGame.SPLASH_RSC), 150 , 50);
        }

        @Override
        public void update (GameContainer container, StateBasedGame game,
        int delta) throws SlickException {

        Input input = container.getInput();
        BounceGame bg = (BounceGame) game;

            try
            {
                Thread.sleep(3000);
                bg.enterState(BounceGame.STARTUPSTATE);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }



        }

        @Override
        public int getID () {
            return BounceGame.SPLASHSCREEN;
        }

}
