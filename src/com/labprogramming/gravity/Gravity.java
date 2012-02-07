package com.labprogramming.gravity;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JFrame;

public class Gravity {

public static final double G = 0.0000000667384D; // newton's gravitational
														// Pg^-1 s^-2
	private boolean is3D = false;

	public static final float FRICTION = 0.9F;

	private static Random r = new Random();
	
	public final int width;

	public final int height;

	private GraphicsDevice device;

	private JFrame frame;

	private long nanoTime;

	private long nanosPerSecond = 16000000L;

	private HashSet<Body> bodies = new HashSet<Body>();

	private VolatileImage img;

	private boolean running = true;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Gravity app = new Gravity();
				try {=
					if (args.length > 0 && args[0].equalsIgnoreCase("3D")) {
						System.out.println("3D Gravity Calculations = true");
						app.is3D = true;
					}
					//preset1(app);
					// marsDeimosPreset(app);=
					int howManyBodies = r.nextInt(10)+2;
					for(int i=0;i<howManyBodies;i++){
						int x = r.nextInt(app.width)-app.width/2;
						int y = r.nextInt(app.height)-app.height/2;
						int xv = r.nextInt(6)-3;
						int yv = r.nextInt(6)-3;
						float mass = r.nextFloat()*3+0.5F;
						Body b = new Body(x,y,xv,yv,mass);
						if(app.isInOtherBody(b)){
							i--;
							continue;
						}
						System.out.println("Creating "+b);
						app.bodies.add(b);
					}
					//app.bodies.add(new Body(-80, -80, 0, 0, 1));
					//app.bodies.add(new Body(140, 130, 0, 0, 0.3F));
					//app.bodies.add(new Body(40, -40, -2, 2, 3));
					//app.bodies.add(new Body(-100, 100, 0, 0, 1));
					//app.bodies.add(new Body(70, -120, 2, 2, 0.3F));=
					app.nanoTime = System.nanoTime();
					app.run();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					app.close();
				}
			}

		});
	}

	// doesn't work
	@SuppressWarnings("unused")
	private static void marsDeimosPreset(Gravity app) {
		app.bodies.add(new Body(0, 0, 0, 0, 641910000000D));
		app.bodies.add(new Body(92.356, 0, 0, 0.0002138, 10720));
	}

	private static void preset1(Gravity app) {
		app.bodies.add(new Body(-40, -40, 2, -2, 10));
		app.bodies.add(new Body(20, 30, 0, 1, 3));
		app.bodies.add(new Body(40, 40, -2, 2, 20));
		app.bodies.add(new Body(0, 0, 0, 0, 10));
		app.bodies.add(new Body(70, -120, 2, 2, 3));
	}


	private boolean isInOtherBody(Body b) {
		for(Body b2 : bodies){
			if(b.distanceTo(b2)<=b.getRadius()+b2.getRadius()) return true;
		}
		return false;
	}
	
	private void run() {
		while (running) {
			System.out.println("run() in while loop");
			long elapsedTime = System.nanoTime() - nanoTime;
			nanoTime = System.nanoTime();
			updateBodies(elapsedTime);
			render();
		}
	}

	private void render() {
		System.out.println("rendering");
		BufferStrategy bs = frame.getBufferStrategy();
		int width = frame.getWidth();
		int height = frame.getHeight();
		if (bs == null) {
			frame.createBufferStrategy(2);
			bs = frame.getBufferStrategy();
		}
		if (img == null) {
			img = frame.createVolatileImage(width, height);
		}
		Graphics g2 = img.createGraphics();
		try {
			g2.clearRect(0, 0, width, height);
			drawBodies(g2);
		} finally {
			g2.dispose();
		}

		Graphics g = bs.getDrawGraphics();
		try {
			g.drawImage(img, 0, 0, width, height, null);
		} finally {
			g.dispose();
		}
		bs.show();
	}

	private void drawBodies(Graphics g2) {
		for (Body b : bodies) {
			if(b.getMass()>0) g2.setColor(Color.CYAN);
			else g2.setColor(Color.RED);
			g2.fillOval(b.getIntX() + width / 2, b.getIntY() + height / 2,
					round(b.getRadius()), round(b.getRadius()));
		}
		g2.setColor(Color.WHITE);
		for (Body b : bodies) {
			g2.drawOval(b.getIntX() + width / 2, b.getIntY() + height / 2,
					round(b.getRadius()), round(b.getRadius()));
		}
	}

	private void updateBodies(long elapsedTime) {
		boolean finishedColliding=false;
		while(!finishedColliding){
			finishedColliding=true;
			for (Body b : bodies) {
				System.out.println("Colliding "+b);
				if(checkForCollisions(b)){
					finishedColliding=false;
					break;
				}
			}
		}
		for (Body b : bodies){
			System.out.println("Moving "+b);
			Forces forces = getForceOnBody(b);
			double xForces = forces.getXForces();
			double yForces = forces.getYForces();
			b.update(elapsedTime, nanosPerSecond, xForces, yForces);
		}
	}

	/**
	 * @param the body to check collisions for
	 * @return whether the list of bodies was modified or not
	 */
	private boolean checkForCollisions(Body b) {
		checkForCollisionsWithWall(b);
		return checkForCollisionsWithOtherBodies(b);
	}
	/**
	 * check to determine if a body hits another body, and make them join together if they do
	 * right now broken!
	 * @param the body to check collisions for
	 * @return whether or not we changed the list of bodies
	 */
	private boolean checkForCollisionsWithOtherBodies(Body b){
		for(Body b2 : bodies) {
			if(b!=b2&&b.distanceTo(b2)<=b.getRadius()+b2.getRadius()){
				System.out.println(b+" is colliding with "+b2);
				//combine(b,b2); return true;
				bounce(b,b2);
				// it is no accident that we return false even though we bounce, the return is suposed to symbolize
				// weather or not we changed the list of bodies, which we didn't
			}
		}
		return false;
	}
	
	@SuppressWarnings("all") private void combine(Body b, Body b2){
		bodies.remove(b);
		bodies.remove(b2);
		Forces bforces = getForceOnBody(b);
		Forces b2forces = getForceOnBody(b2);
		float newXLoc=((b.getMass()*b.getX())+(b2.getMass()*b2.getX()))/(b.getMass()+b2.getMass());
		float newYLoc=((b.getMass()*b.getY())+(b2.getMass()*b2.getY()))/(b.getMass()+b2.getMass());
		float newXForce=((b.getMass()*bforces.getXForces())+(b2.getMass()*b2forces.getXForces()))/(b.getMass()+b2.getMass());
		float newYForce=((b.getMass()*bforces.getYForces())+(b2.getMass()*b2forces.getYForces()))/(b.getMass()+b2.getMass());
		float newMass=b.getMass()+b2.getMass();
		bodies.add(new Body(newXLoc,newYLoc,newXForce,newYForce,newMass));
	}
	
	@SuppressWarnings("all") private void bounce(Body b, Body b2){
		//currently doesn't take into account the relative speeds and masses of the two objects
		float xv1 = b.getVelX(), xv2 = b2.getVelX();
		float yv1 = b.getVelY(), yv2 = b2.getVelY();
		float xl1 = b.getX(), xl2 = b2.getX();
		float yl1 = b.getY(), yl2 = b2.getY();
		float a1 = (float)atan(yv1/xv1);
		float r1 = (float)atan((yl1-yl2)/(xl1-xl2));
		float a1final = 2*r1-(float)PI-a1;
		float dist1=(float)hypot(xv1,yv1);
		b.setVelX((float)cos(a1final)*dist1);
		b.setVelX((float)sin(a1final)*dist1);
		float a2 = (float)tan(yv2/xv2);
		float r2 = (float)tan((yl1-yl2)/(xl1-xl2));
		float a2final = 2*r2-(float)PI-a2;
		float dist2=(float)hypot(xv2,yv2);
		b2.setVelX((float)cos(a2final)*dist2);
		b2.setVelX((float)sin(a2final)*dist2);
	}
	
	private void checkForCollisionsWithWall(Body b) {
		if (b.getVelX()<=0 && b.getIntX() - b.getRadius() / 2 <= -width / 2) {
			b.setVelX((float) (abs(b.getVelX()) * pow(FRICTION, getMassSum())));
		}
		if (b.getVelX()>=0 && b.getIntX() + b.getRadius() / 2 >= width / 2) {
			b.setVelX(-1 * (float) (abs(b.getVelX()) * pow(FRICTION, getMassSum())));
		}
		if (b.getVelY()<=0 && b.getIntY() - b.getRadius() / 2 <= -height / 2) {
			b.setVelY((float) (abs(b.getVelY()) * pow(FRICTION, getMassSum())));
		}
		if (b.getVelY()>=0 && b.getIntY() + b.getRadius() / 2 >= height / 2) {
			b.setVelY(-1 * (float) (abs(b.getVelY()) * pow(FRICTION, getMassSum())));
		}
	}
	
	private float getMassSum(){
		float sum = 0F;
		for(Body b : bodies){
			sum+=b.getMass();
		}
		return sum;
	}

	private Forces getForceOnBody(Body b) {
		double xForceSum = 0;
		double yForceSum = 0;
		for (Body b2 : bodies) {
			if (b2.equals(b)) {
				continue;
			}
			double xDiff = b2.getX() - b.getX();
			double yDiff = b2.getY() - b.getY();
			double d = hypot(xDiff, yDiff);
			double force = G
					* (b.getMass() * b2.getMass() / (is3D ? d * d : d));
			Forces forces = new Forces(force, d, xDiff, yDiff);
			xForceSum += forces.getXForces();
			yForceSum += forces.getYForces();
		}
		return new Forces(xForceSum, yForceSum);
	}

	/**
	 * Create the application.
	 */
	public Gravity() {
		initialize();
		width = frame.getWidth();
		height = frame.getHeight();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBackground(Color.BLACK);
		device = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();

		toFullScreen();
		device.getFullScreenWindow().addMouseMotionListener(
				new MouseMotionListener() {

					@Override
					public void mouseDragged(MouseEvent arg0) {
						System.out.println("mouseDragged");
						running = false;
					}

					@Override
					public void mouseMoved(MouseEvent arg0) {
						System.out.println("mouseMoved");
						if (arg0.getX() == 0) {
							running = false;
						}
					}

				});
	}

	private void toFullScreen() {
		System.out.println("Going to full screen");
		frame.setUndecorated(true);
		frame.setIgnoreRepaint(true);
		frame.setResizable(false);
		device.setFullScreenWindow(frame);
	}

	private void close() {
		System.out.println("closing");
		Window window = device.getFullScreenWindow();
		if (window != null) {
			window.dispose();
		}
		device.setFullScreenWindow(null);
	}

}
