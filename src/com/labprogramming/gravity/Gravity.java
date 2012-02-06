package com.labprogramming.gravity;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;
import static java.lang.Math.pow;

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

import javax.swing.JFrame;

public class Gravity {

	public static final double FRICTION = 0.89D;

	public static final double G = 0.0000000667384D; // newton's gravitational
														// constant in km^3
														// Pg^-1 s^-2
	private boolean is3D = false;

	public final int width;

	public final int height;

	public static final int BODY_WIDTH = 10;
	public static final int BODY_HEIGHT = 10;

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
				try {
					if (args.length > 0 && args[0].equalsIgnoreCase("3D")) {
						System.out.println("3D Gravity Calculations = true");
						app.is3D = true;
					}
					preset1(app);
					// marsDeimosPreset(app);
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
		g2.setColor(Color.CYAN);
		for (Body b : bodies) {
			g2.fillOval(b.getIntX() + width / 2, b.getIntY() + height / 2,
					BODY_WIDTH, BODY_HEIGHT);
		}
		g2.setColor(Color.WHITE);
		for (Body b : bodies) {
			g2.drawOval(b.getIntX() + width / 2, b.getIntY() + height / 2,
					BODY_WIDTH, BODY_HEIGHT);
		}
	}

	private void updateBodies(long elapsedTime) {
		for (Body b : bodies) {
			Forces forces = getForceOnBody(b);
			double xForces = forces.getXForces();
			double yForces = forces.getYForces();
			b.update(elapsedTime, nanosPerSecond, xForces, yForces);
			checkForCollisions(b);
			System.out.println(b);
		}
	}

	private void checkForCollisions(Body b) {
		if (b.getIntX() - BODY_WIDTH / 2 <= -width / 2) {
			b.setVelX(abs(b.getVelX()) * pow(FRICTION, bodies.size()));
		}
		if (b.getIntX() + BODY_WIDTH / 2 >= width / 2) {
			b.setVelX(-1 * abs(b.getVelX()) * 0.9F);
		}
		if (b.getIntY() - BODY_HEIGHT / 2 <= -height / 2) {
			b.setVelY(abs(b.getVelY()) * 0.9F);
		}
		if (b.getIntY() + BODY_HEIGHT / 2 >= height / 2) {
			b.setVelY(-1 * abs(b.getVelY()) * 0.9F);
		}
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
		Window window = device.getFullScreenWindow();
		if (window != null) {
			window.dispose();
		}
		device.setFullScreenWindow(null);
	}

}
