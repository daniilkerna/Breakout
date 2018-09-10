package bounce;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * The Ball class is an Entity that has a velocity (since it's moving). When
 * the Ball bounces off a surface, it temporarily displays a image with
 * cracks for a nice visual effect.
 * 
 */
 class Ball extends Entity {

	private Vector velocity;
	private ConvexPolygon shape;
	private int bouncesBall;

	public Ball(final float x, final float y, final float vx, final float vy) {
		super(x, y);
		//addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BALL_WHITE));

		velocity = new Vector(vx, vy);
		bouncesBall = 0;
		shape = new ConvexPolygon(5f);
		addShape(shape,  Color.white, Color.white);
		//addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BALL_WHITE));
	}

	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}

	/**
	 * Bounce the ball off a surface. This simple implementation, combined
	 * with the test used when calling this method can cause "issues" in
	 * some situations. Can you see where/when? If so, it should be easy to
	 * fix!
	 * 
	 * @param surfaceTangent
	 */
	public void bounce(float surfaceTangent) {
//		removeImage(ResourceManager.getImage(BounceGame.BALL_BALLIMG_RSC));
//		addImageWithBoundingBox(ResourceManager
//				.getImage(BounceGame.BALL_BROKENIMG_RSC));
		velocity = velocity.bounce(surfaceTangent);
	}

	/**
	 * Update the Ball based on how much time has passed...
	 * 
	 * @param delta
	 *            the number of milliseconds since the last update
	 */
	public void update(final int delta) {
		translate(velocity.scale(delta));

	}

	public void incrementBall(){
		this.bouncesBall++;
	}

	public void setBouncesBall(int count){
		this.bouncesBall = count;
	}

	public int getBouncesBall(){
		return this.bouncesBall;
	}

}
