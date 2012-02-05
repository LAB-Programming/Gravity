package com.labprogramming.gravity;

import static java.lang.Math.round;

public class Body {

	private float x;
	private float y;
	private float velX;
	private float velY;
	private float acelX = 0;
	private float acelY = 0;
	private float mass;

	public Body(float x, float y, float velX, float velY, float mass) {
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
		this.mass = mass;
	}

	public void update(long timeElapsed, long nanosPerSecond, float xForces,
			float yForces) {
		float secondsElapsed = timeElapsed / nanosPerSecond;
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
		return round(x);
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return the y to int (for rendering)
	 */
	public int getIntY() {
		return round(y);
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the velX
	 */
	public float getVelX() {
		return velX;
	}

	/**
	 * @param velX
	 *            the velX to set
	 */
	public void setVelX(float velX) {
		this.velX = velX;
	}

	/**
	 * @return the velY
	 */
	public float getVelY() {
		return velY;
	}

	/**
	 * @param velY
	 *            the velY to set
	 */
	public void setVelY(float velY) {
		this.velY = velY;
	}

	/**
	 * @return the acelX
	 */
	public float getAcelX() {
		return acelX;
	}

	/**
	 * @param acelX
	 *            the acelX to set
	 */
	public void setAcelX(float acelX) {
		this.acelX = acelX;
	}

	/**
	 * @return the acelY
	 */
	public float getAcelY() {
		return acelY;
	}

	/**
	 * @param acelY
	 *            the acelY to set
	 */
	public void setAcelY(float acelY) {
		this.acelY = acelY;
	}

	/**
	 * @return the mass
	 */
	public float getMass() {
		return mass;
	}

	/**
	 * @param mass
	 *            the mass to set
	 */
	public void setMass(float mass) {
		this.mass = mass;
	}

	@Override
	public String toString() {
		return new String("\n" + "Body at (" + x + "[int " + getIntX() + "],"
				+ "[int " + getIntY() + "])\n" + "  with motion vector ("
				+ velX + "," + velY + ")\n" + "  with acceleration vector ("
				+ acelX + "," + acelY + ")\n" + "  with mass " + mass);
	}
}
