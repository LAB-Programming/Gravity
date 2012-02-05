package com.labprogramming.gravity;

class Forces {

	private final float xForces;
	private final float yForces;

	public Forces(float force, float hypot, float xPointTo, float yPointTo) {
		float scaleFactor = force / hypot;
		xForces = scaleFactor * xPointTo;
		yForces = scaleFactor * yPointTo;
	}

	public Forces(float xForce, float yForce) {
		xForces = xForce;
		yForces = yForce;
	}

	/**
	 * @return the xForces
	 */
	public float getXForces() {
		return xForces;
	}

	/**
	 * @return the yForces
	 */
	public float getYForces() {
		return yForces;
	}
}
