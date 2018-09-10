package bounce;

import bounce.BounceGame;
import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.Graphics;

public class Brick extends Entity {
    private boolean isDestroyed;

    public Brick (final float x , final float y){
        super(x,y);
        addImageWithBoundingBox(ResourceManager
                .getImage(BounceGame.BALL_BALLIMG_RSC));

        this.isDestroyed = false;

    }

    public void setDestroyed(boolean value ){
        this.isDestroyed = value;
    }

    public boolean getDestroyed(){
        return this.isDestroyed;
    }

    public void render (Graphics g){
        if (!isDestroyed){
            super.render(g);
        }
    }

}
