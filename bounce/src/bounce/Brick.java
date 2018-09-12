package bounce;

import bounce.BounceGame;
import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.Graphics;

public class Brick extends Entity {
    private boolean isDestroyed;

    final private boolean isMultipleTouchReq;
    private boolean isCracked;

    public Brick (final float x , final float y){
        super(x,y);
        addImageWithBoundingBox(ResourceManager
                .getImage(BounceGame.BALL_BALLIMG_RSC));

        this.isDestroyed = false;
        this.isMultipleTouchReq = false;
        this.isCracked = false;
    }

    public Brick (final float x , final float y, final boolean isMultipleTouchReq){
        super(x,y);
        addImageWithBoundingBox(ResourceManager
                .getImage(BounceGame.BALL_BALLIMG_RSC));

        this.isDestroyed = false;
        this.isMultipleTouchReq = isMultipleTouchReq;
        this.isCracked = false;
    }

    public void setDestroyed(boolean value ){
        this.isDestroyed = value;
    }

    public boolean getDestroyed(){
        return this.isDestroyed;
    }

    public void setCracked(boolean value) { this.isCracked = value;
        removeImage(ResourceManager.getImage(BounceGame.BALL_BALLIMG_RSC));
        addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BALL_BROKENIMG_RSC));
    }

    public boolean getCracked() { return this.isCracked;  }

//    public void render (Graphics g){
//
//    }

}
