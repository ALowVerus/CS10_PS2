import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=400, height=400;		// size of the universe

	private ArrayList<Blob> blobs = new ArrayList<Blob>();						// all the blobs
	private ArrayList<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

	ArrayList<Blob>	blobs = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method called whenever a key is pressed
	 */
	@Override
	public void handleKeyPress(char k)  {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else if (k == '0') { // run my test case
			test0();
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs in green and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		// Ask all the blobs to draw themselves.
		// Ask just the colliders to draw themselves in red.
		findColliders();
		g.setColor(Color.GREEN);
		for (Blob blob : blobs) {
			blob.draw(g);
		}
		
		g.setColor(Color.RED);
		for (Blob blob : colliders) {
			blob.draw(g);
		}
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// TODO: YOUR CODE HERE
		// Create the tree
		// For each blob, see if anybody else collided with it
		colliders = new ArrayList<Blob>();
		if (blobs.size() > 0) {
			PointQuadtree tree = new PointQuadtree(blobs.get(0), 0, 0, width, height);
			
			// Add each blob to the tree
			for (Blob blob : blobs) {
				tree.insert(blob);
			}
			
			// Add each colliding blob to the list of colliders
			for (Blob blob : blobs) {
				ArrayList<Blob> found = (ArrayList<Blob>)tree.findInCircle(blob.getX(), blob.getY(), 2 * blob.r);
				if (found.size() > 1) {
					colliders.addAll(found);
				}
			}
		}
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}
	
	/**
	 * Test method. Create two blobs, set them to go fwds or bwds into each other.
	 */
	public void test0() {
		blobs = new ArrayList<Blob>();
		blobs.add(new Bouncer(100, 100, width, height));
		blobs.get(0).setVelocity(0, 1);
		blobs.add(new Bouncer(100, 200, width, height));
		blobs.get(1).setVelocity(0, -1);
		blobs.add(new Bouncer(200, 100, width, height));
		blobs.get(2).setVelocity(0, 1);
		blobs.add(new Bouncer(200, 200, width, height));
		blobs.get(3).setVelocity(0, -1);
		System.out.println(colliders.size());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
