package bounce;

import bounce.BounceGame;
import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Brick extends Entity {
    private boolean isDestroyed;
    private ConvexPolygon shape;
    final private boolean isMultipleTouchReq;
    private boolean isCracked;
    private boolean isIndestructible;

    public Brick (final float x , final float y){
        super(x,y);
        addImageWithBoundingBox(ResourceManager
                .getImage(BounceGame.BALL_BALLIMG_RSC));

        this.isDestroyed = false;
        this.isMultipleTouchReq = false;
        this.isCracked = false;
        this.isIndestructible = false;
    }

    public Brick (final float x , final float y, final boolean isMultipleTouchReq){
        super(x,y);
        addImageWithBoundingBox(ResourceManager
                .getImage(BounceGame.BALL_BALLIMG_RSC));

        this.isDestroyed = false;
        this.isMultipleTouchReq = isMultipleTouchReq;
        this.isCracked = false;
        this.isIndestructible = false;
    }

    public Brick (final float x , final float y , final boolean isMultipleTouchReq , final boolean isIndestructible){
        super(x,y);

        this.isDestroyed = false;
        this.isMultipleTouchReq = isMultipleTouchReq;
        this.isCracked = false;
        this.isIndestructible = isIndestructible;
        shape = new ConvexPolygon(20f, 20f);
        addShape(shape,  Color.red, Color.red);
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

    public boolean getIsDestructible(){
        return this.isIndestructible;
    }

//    public void render (Graphics g){
//
//    }

}
