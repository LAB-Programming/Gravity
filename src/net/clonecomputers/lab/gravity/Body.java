package net.clonecomputers.lab.gravity;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import java.util.HashSet;
import java.util.Iterator;

public class Body {

	public static final double DENSITY = 1;
	private double x;
	private double y;
	private double velX;
	private double velY;
	private double acelX = 0;
	private double acelY = 0;
	private double mass;
	public HashSet<Body> collidingWith = new HashSet<Body>();
	private final int id;
	private static int bodyCount = 0;
	//private boolean colliding = false;

	public Body(double x, double y, double velX, double velY, double mass) {
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
		this.mass = mass;
		id = bodyCount++;
	}

	public void update(double t, double xForces,
			double yForces) {
		/*double secondsElapsed = timeElapsed / nanosPerSecond;
		x += velX * secondsElapsed + 1/2 * acelX * pow(secondsElapsed, 2);
		y += velY * secondsElapsed + 1/2 * acelY * pow(secondsElapsed, 2);
		velX += acelX * secondsElapsed;
		velY += acelY * secondsElapsed;
		acelX = xForces / mass;
		acelY = yForces / mass;*/

		setAcel(xForces/mass, yForces/mass);
		setPos(MathUtil.positionAfterStep(this, t));
		setVel(MathUtil.velocityAfterStep(this, t));
	}
	
	private void setPos(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	private void setVel(double x, double y){
		this.velX = x;
		this.velY = y;
	}
	
	private void setAcel(double x, double y){
		this.acelX = x;
		this.acelY = y;
	}

	private void setPos(double[] pos) {
		setPos(pos[0],pos[1]);
	}
	
	private void setVel(double[] vel) {
		setVel(vel[0],vel[1]);
	}
	
	private void setAcel(double[] acel){
		setAcel(acel[0],acel[1]);
	}

	/**
	 * @return the x to int (for rendering)
	 */
	public int getIntX() {
		return (int) round(x);
	}

	/**
	 * @return the x location
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y location
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the y to int (for rendering)
	 */
	public int getIntY() {
		return (int) round(y);
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the velX
	 */
	public double getVelX() {
		return velX;
	}

	/**
	 * @param velX
	 *            the velX to set
	 */
	public void setVelX(double velX) {
		this.velX = velX;
	}

	/**
	 * @return the velY
	 */
	public double getVelY() {
		return velY;
	}

	/**
	 * @param velY
	 *            the velY to set
	 */
	public void setVelY(double velY) {
		this.velY = velY;
	}

	/**
	 * @return the acelX
	 */
	public double getAcelX() {
		return acelX;
	}

	/**
	 * @param acelX
	 *            the acelX to set
	 */
	public void setAcelX(double acelX) {
		this.acelX = acelX;
	}

	/**
	 * @return the acelY
	 */
	public double getAcelY() {
		return acelY;
	}

	/**
	 * @param acelY
	 *            the acelY to set
	 */
	public void setAcelY(double acelY) {
		this.acelY = acelY;
	}

	/**
	 * @return the mass
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * @param mass
	 *            the mass to set
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public double getRadius(){
		return sqrt(abs(mass)/(DENSITY*PI));
	}
	
	public double distanceTo(Body b){
		double xDiff=this.getX()-b.getX();
		double yDiff=this.getY()-b.getY();
		return hypot(xDiff,yDiff);
	}
	
	/*Didn't realise colliding with had been implemented
	public boolean isColliding() {
		return colliding;
	}
	
	public Iterator<Body> getCollisionIterator() {
		return collidingWith.iterator();
	}
	
	public boolean addCollisionBody(Body b) {
		colliding = true;
		return collidingWith.add(b);
	}
	
	public boolean removeCollisionBody(Body b) {
		boolean result = collidingWith.remove(b);
		if(collidingWith.isEmpty()) colliding = false;
		return result;
	}
	*/

	@Override
	public String toString() {
		return new String("Body #" + id + " at ("+x+", "+y+") with mass "+mass + " and radius "+getRadius());
	}

}
