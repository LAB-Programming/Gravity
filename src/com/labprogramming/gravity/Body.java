package com.labprogramming.gravity;

import static java.lang.Math.*;

public class Body {

<<<<<<< HEAD
	private double x;
	private double y;
	private double velX;
	private double velY;
	private double acelX = 0;
	private double acelY = 0;
	private double mass;

	public Body(double x, double y, double velX, double velY, double mass) {
=======
	public static final float DENSITY = 15;
	private float x;
	private float y;
	private float velX;
	private float velY;
	private float acelX = 0;
	private float acelY = 0;
	private float mass;

	public Body(float x, float y, float velX, float velY, float mass) {
>>>>>>> Now randomly generates bodies
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
		this.mass = mass;
	}

	public void update(long timeElapsed, long nanosPerSecond, double xForces,
			double yForces) {
		double secondsElapsed = timeElapsed / nanosPerSecond;
		x += velX * secondsElapsed;
		y += velY * secondsElapsed;
		velX += acelX * secondsElapsed;
		velY += acelY * secondsElapsed;
		acelX = xForces / mass;
		acelY = xForces / mass;
	}

	/**
	 * @return the x to int (for rendering)
	 */
	public int getIntX() {
		return (int) round(x);
	}

	/**
	 * @return the x
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
	 * @return the y
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
	
	public float getRadius(){
		return (float)sqrt(abs(mass)/PI)*DENSITY;
	}
	
	public float distanceTo(Body b){
		float xDiff=this.getX()-b.getX();
		float yDiff=this.getY()-b.getY();
		return (float)hypot(xDiff,yDiff);
	}

	@Override
	public String toString() {
<<<<<<< HEAD
		return new String("\n" + "Body at (" + x + "[int " + getIntX() + "],"
				+ y + "[int " + getIntY() + "])\n" + "  with motion vector ("
				+ velX + "," + velY + ")\n" + "  with acceleration vector ("
				+ acelX + "," + acelY + ")\n" + "  with mass " + mass);
=======
		return new String("Body at ("+x+", "+y+") with mass "+mass);
>>>>>>> Now randomly generates bodies
	}
}
