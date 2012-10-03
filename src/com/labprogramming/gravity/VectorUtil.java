package com.labprogramming.gravity;

class VectorUtil {

	private final double xMag;
	private final double yMag;

	public VectorUtil(double force, double hypot, double xDest, double yDest) {
		double scaleFactor = hypot != 0 ? force / hypot : 0;
		xMag = scaleFactor * xDest;
		yMag = scaleFactor * yDest;
	}

	public VectorUtil(double newXMag, double newYMag) {
		xMag = newXMag;
		yMag = newYMag;
	}

	/**
	 * @return the xForces
	 */
	public double getXMag() {
		return xMag;
	}

	/**
	 * @return the yForces
	 */
	public double getYMag() {
		return yMag;
	}
}
