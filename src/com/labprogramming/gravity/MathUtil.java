package com.labprogramming.gravity;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MathUtil {
	
	public static final double SQRT_2 = sqrt(2);
	
	private MathUtil() {}
	
	public static double[] getTimeTillCollision(Body p, Body q) {
		double r_p = p.getRadius();
		double r_q = q.getRadius();
		double x_p_o = p.getX();
		double x_q_o = q.getX();
		double v_p_x_o = p.getVelX();
		double v_q_x_o = q.getVelX();
		double a_p_x = p.getAcelX();
		double a_q_x = q.getAcelX();
		double y_p_o = p.getY();
		double y_q_o = q.getY();
		double v_p_y_o = p.getVelY();
		double v_q_y_o = q.getVelY();
		double a_p_y = p.getAcelY();
		double a_q_y = q.getAcelY();
		return new double[] {
				SQRT_2/2 * sqrt(4* (sqrt(sqr((a_p_x-a_q_x)*(x_p_o-x_q_o)+(a_p_y-a_q_y)*(y_p_o-y_q_o)) + (sqr(a_p_x-a_q_x) + sqr(a_p_y-a_q_y))*(sqr(r_p+r_q) - sqr(x_p_o-x_q_o) - sqr(y_p_o-y_q_o))) - ((a_p_x-a_q_x)*(x_p_o-x_q_o) + (a_p_y-a_q_y)*(y_p_o-y_q_o)))/((a_p_x-a_q_x) + (a_p_y-a_q_y))),
				SQRT_2/2 * sqrt(4*(-sqrt(sqr((a_p_x-a_q_x)*(x_p_o-x_q_o)+(a_p_y-a_q_y)*(y_p_o-y_q_o)) + (sqr(a_p_x-a_q_x) + sqr(a_p_y-a_q_y))*(sqr(r_p+r_q) - sqr(x_p_o-x_q_o) - sqr(y_p_o-y_q_o))) - ((a_p_x-a_q_x)*(x_p_o-x_q_o) + (a_p_y-a_q_y)*(y_p_o-y_q_o)))/((a_p_x-a_q_x) + (a_p_y-a_q_y)))
		};
	}
	
	public static double[] positionAfterStep(Body b, double t){
		double v_x_0 = b.getVelX();
		double v_y_0 = b.getVelY();
		double x_0 = b.getX();
		double y_0 = b.getY();
		double a_x = b.getAcelX();
		double a_y = b.getAcelY();
		return new double[]{
				x_0 + v_x_0*t + a_x*t*t,
				y_0 + v_y_0*t + a_y*t*t,
		};
	}
	
	public static double[] velocityAfterStep(Body b, double t){
		double v_x_0 = b.getVelX();
		double v_y_0 = b.getVelY();
		double a_x = b.getAcelX();
		double a_y = b.getAcelY();
		return new double[]{
			v_x_0 + a_x*t,
			v_y_0 + a_y*t,
		};
	}
	
	public static double sqr(double root) {
		return pow(root, 2);
	}
}
