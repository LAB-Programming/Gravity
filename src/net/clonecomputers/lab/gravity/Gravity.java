package net.clonecomputers.lab.gravity;

import static java.lang.Math.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

public class Gravity implements Runnable{
	
	private static final boolean GRAVITY = true;
	
	private boolean FULLSCREEN = false;
	
	private static boolean LOG = true;
	
	public static final double G = 5000000*Math.pow(0.667384D,0.3); // newton's gravitational
														// Pg^-1 s^-2
	private boolean is3D = true;
	
	public static final float FRICTION = GRAVITY?0.9999F:1;
	
	public static final int BUFFER_NUM = 10;

	private static Random r = new Random();
	
	public final int width;

	public final int height;

	private GraphicsDevice device;

	private JFrame frame;

	private long nanoTime;

	private final long nanosPerSecond = 400000000L;
	
	private final long nanosPerStep = 100000L;
	
	private long stepcount = 0;

	private HashSet<Body> bodies = new HashSet<Body>();
	
	private boolean paused=false;

	//private VolatileImage img;

	private boolean running = true;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Gravity app = new Gravity();
				try {
					if (args.length > 0) {
						Arrays.sort(args);
						if(Arrays.binarySearch(args, "LOG") >= 0) {
							System.out.println("Logging is on!");
							LOG = true;
						}if(Arrays.binarySearch(args, "NOLOG") >= 0) {
							System.out.println("Logging is off!");
							LOG = false;
						}
						if(Arrays.binarySearch(args, "FULLSCREEN") >= 0) {
							if(LOG) System.out.println("FULLSCREEN = true");
							app.FULLSCREEN = true;
						}
						if(Arrays.binarySearch(args, "3D") >= 0) {
							if(LOG) System.out.println("3D Gravity Calculations = true");
							app.is3D = true;
						}
					}
					//preset1(app);
					//marsDeimosPreset(app);
					randomBodies(app);
					//windowedBoundsTest(app);
					//collisionTest(app);
					app.nanoTime = System.nanoTime();
					Thread appRunner = new Thread(app);
					appRunner.setName("Simulation Thread");
					appRunner.setPriority(Thread.MAX_PRIORITY);
					appRunner.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}
	
	private static void randomBodies(Gravity app) {
		int howManyBodies = r.nextInt(300)+100;
		for(int i=0;i<howManyBodies;i++){
			int x = r.nextInt(app.width)-app.width/2;
			int y = r.nextInt(app.height)-app.height/2;
			int xv = r.nextInt(100000)-50000;
			int yv = r.nextInt(100000)-50000;
			double mass = r.nextDouble()*30+5;
			Body b = new Body(x,y,xv,yv,mass);
			if(app.isInOtherBody(b)){
				i--;
				continue;
			}
			if(LOG) System.out.println("Creating "+b);
			app.bodies.add(b);
		}
	}

	/**
	 * doesn't work!
	 * @param app who to present
	 */
	@SuppressWarnings("unused")
	private static void marsDeimosPreset(Gravity app) {
		app.bodies.add(new Body(0, 0, 0, 0, 641910000000D));
		app.bodies.add(new Body(92.356, 0, 0, 0.0002138, 10720));
	}

	@SuppressWarnings("unused")
	private static void preset1(Gravity app) {
		app.bodies.add(new Body(-40, -40, 2, -2, 10));
		app.bodies.add(new Body(20, 30, 0, 1, 3));
		app.bodies.add(new Body(40, 40, -2, 2, 20));
		app.bodies.add(new Body(0, 0, 0, 0, 10));
		app.bodies.add(new Body(70, -120, 2, 2, 3));
	}
	
	@SuppressWarnings("unused")
	private static void collisionTest(Gravity app) {
		app.bodies.add(new Body(-20, 0, -4, 0, 300));
		app.bodies.add(new Body(20, 0, 4, 0, 300));
	}
	
	@SuppressWarnings("unused")
	private static void windowedBoundsTest(Gravity app) {
		double mass = 484*PI; // this is so radius = 22 (menubar height on mac osx sl)
		double minX = -app.width/2D;
		double minY = -app.height/2D;
		double midX = -0.5;
		double midY = -0.5;
		double maxX = app.width/2D - 1;
		double maxY = app.height/2D - 1;
		double qt1X = (minX + midX)/2; // 1/4 x position
		double qt1Y = (minY + midY)/2; // 1/4 y position
		double qt3X = (midX + maxX)/2; // 3/4 x position
		double qt3Y = (midY + maxY)/2; // 3/4 y position
		
		//reference points
		app.bodies.add(new Body(minX, minY, 0, 0, mass));
		app.bodies.add(new Body(midX, minY, 0, 0, mass));
		app.bodies.add(new Body(maxX, minY, 0, 0, mass));
		app.bodies.add(new Body(minX, midY, 0, 0, mass));
		app.bodies.add(new Body(midX, midY, 0, 0, mass));
		app.bodies.add(new Body(maxY, midY, 0, 0, mass));
		app.bodies.add(new Body(minX, maxY, 0, 0, mass));
		app.bodies.add(new Body(midX, maxY, 0, 0, mass));
		app.bodies.add(new Body(maxX, maxY, 0, 0, mass));
		
		//bounds testers
		app.bodies.add(new Body(qt1X, qt1Y, app.width*8, 0, mass));
		app.bodies.add(new Body(qt3X, qt1Y, 0, app.height*8, mass));
		app.bodies.add(new Body(qt1X, qt3Y, 0, -app.height*8, mass));
		app.bodies.add(new Body(qt3X, qt3Y, -app.width*8, 0, mass));
	}


	private boolean isInOtherBody(Body b) {
		for(Body b2 : bodies){
			if(b.distanceTo(b2)<=b.getRadius()+b2.getRadius()) return true;
		}
		return false;
	}
	
	public void run() {
		/*while (running) {
			if(LOG) System.out.println("run() in while loop");
			long elapsedTime = System.nanoTime() - nanoTime;
			nanoTime = System.nanoTime();
			if(!paused) updateBodies(elapsedTime);
			render();
			Thread.yield();
		}*/
		try {
			while(running){
				if(LOG) System.out.println("run() in while loop");
				long startTime = System.nanoTime();
				stepcount++;
				render();
				updateBodies(nanosPerStep);
				// collisions!!!
				while(System.nanoTime()<startTime+nanosPerStep) Thread.yield();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	public void togglePause(){
		paused=!paused;
	}

	private void render() {
		if(LOG) System.out.println("rendering");
		BufferStrategy bs = frame.getBufferStrategy();
		// we must use root pane dimensions so menubar is excluded windowed mode
		int width = frame.getRootPane().getWidth();
		int height = frame.getRootPane().getHeight();
		if (bs == null) {
			frame.createBufferStrategy(BUFFER_NUM);
			bs = frame.getBufferStrategy();
		}
		/*if (img == null) {
			img = frame.createVolatileImage(width, height);
		}
		Graphics g2 = img.createGraphics();*/
		Graphics g2 = null;
		g2 = bs.getDrawGraphics();
		
		// move origin of graphics so that we don't end up drawing behind menubar in windowed mode
		g2.translate(frame.getRootPane().getX(), frame.getRootPane().getY());
		if (LOG && !FULLSCREEN) {
			System.out.println("(width,height) = (" + width + "," + height + ")");
			System.out.println("JFrame: " + frame);
			System.out.println("JFrame bounds: " + frame.getBounds());
			System.out.println("Content Pane: " + frame.getContentPane());
			System.out.println("Content Pane bounds: " + frame.getContentPane().getBounds());
			System.out.println("Root Pane: " + frame.getRootPane());
			System.out.println("Root Pane bounds: " + frame.getRootPane().getBounds());
		}
		try {
			g2.clearRect(0, 0, width, height);
			drawBodies(g2);
		} finally {
			g2.dispose();
		}

		/*Graphics g = bs.getDrawGraphics();
		try {
			g.drawImage(img, 0, 0, width, height, null);
		} finally {
			g.dispose();
		}*/
		bs.show();
	}

	private void drawBodies(Graphics g2) {
		for (Body b : bodies) {
			if(b.getMass()>0) g2.setColor(Color.CYAN);
			else g2.setColor(Color.RED);
			g2.fillOval((int) round(b.getX() + width/2D - b.getRadius()),
					(int) round(b.getY() + height/2D - b.getRadius()),
					(int) round(b.getRadius()*2), (int)round(b.getRadius()*2));
		}
		g2.setColor(Color.WHITE);
		for (Body b : bodies) {
			g2.drawOval((int) round(b.getX() + width/2D - b.getRadius()),
					(int) round(b.getY() + height/2D - b.getRadius()),
					(int) round(b.getRadius()*2), (int)round(b.getRadius()*2));
		}
	}

	private void updateBodies(long elapsedTime) {
		boolean finishedColliding=false;
		while(!finishedColliding){
			finishedColliding=true;
			for (Body b : bodies) {
				if(checkForCollisions(b)){
					finishedColliding=false;
					break;
				}
			}
		}
		for (Body b : bodies){
			if(LOG) System.out.println("Moving "+b);
			VectorUtil forces = getGravitationalForceOnBody(b);
			double xForces = forces.getXMag();
			double yForces = forces.getYMag();
			/*b.setAcel(xForces, yForces);
			b.setPos(MathUtil.positionAfterStep(b, (double)elapsedTime/(double)nanosPerSecond));
			b.setVel(MathUtil.velocityAfterStep(b, (double)elapsedTime/(double)nanosPerSecond));*/
			b.update((double)elapsedTime/(double)nanosPerSecond, xForces, yForces);
		}
	}

	/**
	 * @param b the body to check collisions for
	 * @return whether the list of bodies was modified or not
	 */
	private boolean checkForCollisions(Body b) {
		checkForCollisionsWithWall(b);
		return checkForCollisionsWithOtherBodies(b);
	}
	
	private static void updateCollidingWith(Body b){
		HashSet<Body> doneCollidingWith=new HashSet<Body>();
		for(Body temp:b.collidingWith){
			if(temp == b) continue;
			if(temp.distanceTo(b) > temp.getRadius()+b.getRadius()) doneCollidingWith.add(temp);
		}
		b.collidingWith.removeAll(doneCollidingWith);
	}
	
	/**
	 * check to determine if a body hits another body, and do something if they do
	 * @param b the body to check collisions for
	 * @return whether or not we changed the list of bodies
	 */
	private boolean checkForCollisionsWithOtherBodies(Body b){
		updateCollidingWith(b);
		for(Body b2 : bodies) {
			if(b == b2) continue;
			if(b.distanceTo(b2) <= b.getRadius()+b2.getRadius() && !b.collidingWith.contains(b2) && !b2.collidingWith.contains(b)){
				if(LOG) System.out.println(b+" is colliding with "+b2);
				b.collidingWith.add(b2);
				b2.collidingWith.add(b);
				combine(b,b2); return true;
				//bounce(b,b2);
				// it is no accident that we return false even though we bounce, the return is suposed to symbolize
				// weather or not we changed the list of bodies, which we didn't
			}
		}
		return false;
	}
	
	@SuppressWarnings("all")
	private void combine(Body b, Body b2){
		bodies.remove(b);
		bodies.remove(b2);
		VectorUtil bforces = getGravitationalForceOnBody(b);
		VectorUtil b2forces = getGravitationalForceOnBody(b2);
		double newXLoc=((b.getMass()*b.getX())+(b2.getMass()*b2.getX()))/(b.getMass()+b2.getMass());
		double newYLoc=((b.getMass()*b.getY())+(b2.getMass()*b2.getY()))/(b.getMass()+b2.getMass());
		double newXForce=((b.getMass()*b.getVelX())+(b2.getMass()*b2.getVelX()))/(b.getMass()+b2.getMass());
		double newYForce=((b.getMass()*b.getVelY())+(b2.getMass()*b2.getVelY()))/(b.getMass()+b2.getMass());
		double newMass=b.getMass()+b2.getMass();
		bodies.add(new Body(newXLoc,newYLoc,newXForce,newYForce,newMass));
	}
	
	@SuppressWarnings("all")
	private void bounce(Body b, Body b2){
		double bRelXVel = b.getVelX()-b2.getVelX();
		double bRelYVel = b.getVelY()-b2.getVelY();
		//System.out.println("Hmm... shifting frame of reference so that b2 is stationary");
		//System.out.println("b velocity now is (" + bRelXVel + "," + bRelYVel + ")");
		double bRelSpeed = hypot(bRelXVel, bRelYVel);
		double bRelDirection = atan2(bRelYVel, bRelXVel);
		//System.out.println("Ok the velocity of b is now a vector");
		//System.out.println("Magnitude = " + bRelSpeed + ", angle = " + bRelDirection);
		double collisionDirection = atan2(b2.getY()-b.getY(), b2.getX()-b.getX());
		//System.out.println("The collision")
		double bColParaRelVel = bRelSpeed*sin(collisionDirection-bRelDirection); //The Relative Velocity of b parallel to the collision
		double bColPerpRelVel = bRelSpeed*cos(collisionDirection-bRelDirection); //The Relative Velocity of b perpendicular to the collision
		double bAbsMass = b.getMass();//abs(b.getMass());
		double b2AbsMass = b2.getMass();//abs(b2.getMass());
		double bAfterColPerpRelVel = bColPerpRelVel*(bAbsMass-b2AbsMass)/(bAbsMass+b2AbsMass); //The relative velocity of b after the collision perpendicular to the collision
		double b2AfterColPerpRevVel = 2*bAbsMass*bColPerpRelVel/(bAbsMass+b2AbsMass); //The relative velocity of b2 after the collision perpendicular to the collision
		double bNewVelX = bAfterColPerpRelVel*cos(collisionDirection)+bColParaRelVel*cos(PI+collisionDirection)+b2.getVelX();
		double bNewVelY = bAfterColPerpRelVel*sin(collisionDirection)+bColParaRelVel*sin(PI+collisionDirection)+b2.getVelY();
		double b2NewVelX = b2AfterColPerpRevVel*cos(collisionDirection)+b2.getVelX();
		double b2NewVelY = b2AfterColPerpRevVel*sin(collisionDirection)+b2.getVelY();
		//System.out.println("collisionDirection = " + collisionDirection + ", bRelDirection = " + bRelDirection);
		if(LOG) {
			System.out.println("bOldVelX = " + b.getVelX() + ", bNewVelX = " + bNewVelX);
			System.out.println("bOldVelY = " + b.getVelY() + ", bNewVelY = " + bNewVelY);
			System.out.println("b2OldVelX = " + b2.getVelX() + ", b2NewVelX = " + b2NewVelX);
			System.out.println("b2OldVelY = " + b2.getVelY() + ", b2NewVelY = " + b2NewVelY);
		}
		b.setVelX(bNewVelX);
		b.setVelY(bNewVelY);
		b2.setVelX(b2NewVelX);
		b2.setVelY(b2NewVelY);
		
		/*//currently doesn't take into account the relative speeds and masses of the two objects
		double xv1 = b.getVelX(), xv2 = b2.getVelX();
		double yv1 = b.getVelY(), yv2 = b2.getVelY();
		double xl1 = b.getX(), xl2 = b2.getX();
		double yl1 = b.getY(), yl2 = b2.getY();
		double a1 = atan2(yv1,xv1);
		double r1 = atan2((yl1-yl2),(xl1-xl2));
		double a1final = 2*r1-PI-a1;
		double dist1=hypot(xv1,yv1);
		b.setVelX(cos(a1final)*dist1);
		b.setVelX(sin(a1final)*dist1);
		double a2 = tan(yv2/xv2);
		double r2 = tan((yl1-yl2)/(xl1-xl2));
		double a2final = 2*r2-PI-a2;
		double dist2=hypot(xv2,yv2);
		b2.setVelX(cos(a2final)*dist2);
		b2.setVelX(sin(a2final)*dist2);*/
	}
	
	private void checkForCollisionsWithWall(Body b) {
		if (b.getVelX() <= 0 && b.getIntX() - b.getRadius() <= -width/2) {
			b.setVelX( (abs(b.getVelX()) * pow(FRICTION, getMassSum())));
		}
		if (b.getVelX() >= 0 && b.getIntX() + b.getRadius() >= width/2 - 1) {
			b.setVelX(-1 *  (abs(b.getVelX()) * pow(FRICTION, getMassSum())));
		}
		if (b.getVelY() <= 0 && b.getIntY() - b.getRadius() <= -height/2) {
			b.setVelY( (abs(b.getVelY()) * pow(FRICTION, getMassSum())));
		}
		if (b.getVelY() >= 0 && b.getIntY() + b.getRadius() >= height/2 - 1) {
			b.setVelY(-1 *  (abs(b.getVelY()) * pow(FRICTION, getMassSum())));
		}
	}
	
	private double getMassSum(){
		double sum = 0F;
		for(Body b : bodies){
			sum+=b.getMass();
		}
		return sum;
	}

	private VectorUtil getGravitationalForceOnBody(Body b) {
		double xForceSum = 0;
		double yForceSum = 0;
		if(GRAVITY) for (Body b2 : bodies) {
			if (b2.equals(b) || b.collidingWith.contains(b2) || b2.collidingWith.contains(b)) { // makes sure we arent trying to apply gravity between a body and its self or a body that it is colliding with
				continue;
			}
			double xDiff = b2.getX() - b.getX();
			double yDiff = b2.getY() - b.getY();
			double d = hypot(xDiff, yDiff);
			double force = G
					* (b.getMass() * b2.getMass() / (is3D ? d * d : d));
			VectorUtil forces = new VectorUtil(force, d, xDiff, yDiff);
			xForceSum += forces.getXMag();
			yForceSum += forces.getYMag();
		}
		return new VectorUtil(xForceSum, yForceSum);
	}

	/**
	 * Create the application.
	 */
	public Gravity() {
		//nanosPerSecond = readNanosPerSecond();
		if(LOG) System.out.println("nanosPerSecond = " + nanosPerSecond);
		initialize();
		// we must use root pane dimensions so menubar is excluded windowed mode
		width = frame.getRootPane().getWidth();
		height = frame.getRootPane().getHeight();
	}

	private long readNanosPerSecond() {
		File file = new File("nanos.txt");
		if(!file.exists()) {
			try {
				if(!file.createNewFile()) throw new IOException("I thought I checked that the file didn't exist!!!");
			} catch (IOException ioe) {
				System.err.println("IOException while attempting to create nano.txt");
				ioe.printStackTrace();
			}
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(file));
			} catch(IOException ioe) {
				System.err.println("Failed to open nanos.txt");
				ioe.printStackTrace();
			}
			try {
				writer.write("" + nanosPerSecond);
				writer.flush();
			} catch (IOException ioe) {
				System.err.println("Failed to write default value to file");
				System.err.println("Deleting file...");
				if(file.delete()) {
					System.err.println("nanos.txt deleted!");
				} else {
					System.err.println("Arggh! Failed to delete file!");
				}
				ioe.printStackTrace();
			}
			try {
				writer.close();
			} catch (IOException ioe) {
				System.err.println("the write stream has failed to close! VERY BAD THING!");
				ioe.printStackTrace();
			}
			return nanosPerSecond;
		}else if(!file.isFile() || !file.canRead()) {
			System.err.println("a nanos.txt already exists but is not readable and/or is not a file");
			return nanosPerSecond;
		} else {
			Scanner reader = null;
			try {
				reader = new Scanner(new BufferedReader(new FileReader(file)));
			} catch (FileNotFoundException e) {
				System.err.println("How the hell did this happen!! I checked to make sure the file was there!");
				e.printStackTrace();
				return nanosPerSecond;
			}
			Long nanos = null;
			try {
				nanos = reader.nextLong();
				if(nanos <= 0) throw new NoSuchElementException();
			} catch(NoSuchElementException nsee) {
				System.err.println("No valid values found for nanos per second (a number greater than 0 and within range of the long)");
				return nanosPerSecond;
			}
			reader.close();
			return nanos;
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBackground(Color.BLACK);
		device = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();

		if(FULLSCREEN) toFullScreen();
		else toPartScreen();
		
		(FULLSCREEN?device.getFullScreenWindow():frame).addMouseMotionListener(
				new MouseMotionListener() {

					@Override
					public void mouseDragged(MouseEvent arg0) {
						//if(LOG) System.out.println("mouseDragged");
						//running = false;
					}

					@Override
					public void mouseMoved(MouseEvent arg0) {
						//if(LOG) System.out.println("mouseMoved");
						if (arg0.getX() == 0) {
							//running = false;
						}
					}

				});
		(FULLSCREEN?device.getFullScreenWindow():frame).addMouseListener(
				new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent arg0){
						togglePause();
					}

				});
	}
	
	private void toPartScreen() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024,768);
		frame.setVisible(true);
	}

	private void toFullScreen() {
		if(LOG) System.out.println("Going to full screen");
		frame.setUndecorated(true);
		frame.setIgnoreRepaint(true);
		frame.setResizable(false);
		device.setFullScreenWindow(frame);
	}

	private void close() {
		if(LOG) System.out.println("closing");
		Window window = device.getFullScreenWindow();
		if (window != null) {
			window.dispose();
		}
		device.setFullScreenWindow(null);
	}

}
