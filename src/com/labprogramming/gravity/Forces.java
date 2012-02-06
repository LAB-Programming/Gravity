package com.labprogramming.gravity;

class Forces {

	private final double xForces;
	private final double yForces;

	public Forces(double force, double hypot, double xPointTo, double yPointTo) {
		double scaleFactor = force / hypot;
		xForces = scaleFactor * xPointTo;
		yForces = scaleFactor * yPointTo;
	}

	public Forces(double xForce, double yForce) {
		xForces = xForce;
		yForces = yForce;
	}

	/**
	 * @return the xForces
	 */
	public double getXForces() {
		return xForces;
	}

	/**
	 * @return the yForces
	 */
	public double getYForces() {
		return yForces;
	}
}
