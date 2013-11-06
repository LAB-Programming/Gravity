package net.clonecomputers.lab.gravity;

import static java.lang.Math.atan2;
import static java.lang.Math.hypot;

class VectorUtil {

	private final double mag;
	private final double angle;
	private final double xMag;
	private final double yMag;

	public VectorUtil(double force, double hypot, double xDest, double yDest) {
		double scaleFactor = hypot != 0 ? force / hypot : 0;
		xMag = scaleFactor * xDest;
		yMag = scaleFactor * yDest;
		mag = force;
		angle = atan2(yMag, xMag);
	}

	public VectorUtil(double newXMag, double newYMag) {
		xMag = newXMag;
		yMag = newYMag;
		mag = hypot(xMag, yMag);
		angle = atan2(yMag, xMag);
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
	
	public double getMag() {
		return mag;
	}
	
	public double getDirection() {
		return angle;
	}
	
	public static VectorUtil scaleMagnitude(VectorUtil vector, double newMag) {
		return new VectorUtil(newMag, vector.mag, vector.xMag, vector.yMag);
	}
}
