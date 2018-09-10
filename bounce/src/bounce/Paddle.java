package bounce;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Transform;

public class Paddle {
    private float xLoc, yLoc, width, height;
    private int countdown;


    public Paddle (float xLoc, float yLoc , float width, float height ) {
        super();
        countdown = 0;

        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.width = width;
        this.height = height;


    }

    //gettters and setters

    public float getxLoc(){
        return this.xLoc;
    }

    public void setxLoc (float xLoc){
        this.xLoc = xLoc;
    }

    public float getyLoc(){
        return this.yLoc;
    }

    public void setyLoc (float yLoc){
        this.yLoc = yLoc;
    }

    public float getWidth(){
        return this.width;
    }

    public void setWidth (float width){
        this.width = width;
    }

    public float getHeight(){
        return this.height;
    }

    public void setHeight (float height){
        this.height = height;
    }

    public void render(Graphics g){
        g.fillRect(getxLoc(), getyLoc(), getWidth(), getHeight());
    }

    public void movePaddleLeft(){
        setxLoc(getxLoc() - 10);
    }

    public void movePaddleRight(){
        setxLoc(getxLoc() + 10);
    }


}
