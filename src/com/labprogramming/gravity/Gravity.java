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

	public static final float FRICTION = 0.89F;

	public static final float G = (float) 66.740; // newton's gravitational
													// constant

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
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Gravity app = new Gravity();
				try {
					app.bodies.add(new Body(-40, -40, 2, -2, 1));
					app.bodies.add(new Body(20, 30, 0, 1, 0.3F));
					app.bodies.add(new Body(40, 40, -2, 2, 2));
					app.bodies.add(new Body(0, 0, 0, 0, 1));
					app.bodies.add(new Body(70, -120, 2, 2, 0.3F));
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
			float xForces = forces.getXForces();
			float yForces = forces.getYForces();
			b.update(elapsedTime, nanosPerSecond, xForces, yForces);
			checkForCollisions(b);
			System.out.println(b);
		}
	}

	private void checkForCollisions(Body b) {
		if (b.getIntX() - BODY_WIDTH / 2 <= -width / 2) {
			b.setVelX((float) (abs(b.getVelX()) * pow(FRICTION, bodies.size())));
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
		float xForceSum = 0;
		float yForceSum = 0;
		for (Body b2 : bodies) {
			if (b2.equals(b)) {
				continue;
			}
			float xDiff = b2.getX() - b.getX();
			float yDiff = b2.getY() - b.getY();
			float d = (float) hypot(xDiff, yDiff);
			float force = G * (b.getMass() * b2.getMass() / d);
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
