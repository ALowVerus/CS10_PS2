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
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
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
		else if (k == 'c' || k == 'd' || k == 'i') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		// Ask all the blobs to draw themselves.
		// Ask the colliders to draw themselves in red.
		findColliders();
		
		if (collisionHandler == 'i') {
			g.setColor(Color.RED);
			for (int i = 0; i < colliders.size(); i ++) {
				Blob blob = colliders.get(i);
				if(blob.getCollided()) {
					g.fillOval((int)(blob.getX()-blob.r/2), (int)(blob.getY()-blob.r/2), (int)(2*blob.r), (int)(2*blob.r));
				}
			}
		}
		else {
			g.setColor(Color.GREEN);
			for (Blob blob : blobs) {
				g.fillOval((int)(blob.getX()-blob.r/2), (int)(blob.getY()-blob.r/2), (int)(2*blob.r), (int)(2*blob.r));
			}
			g.setColor(Color.RED);
			for (int i = 0; i < colliders.size(); i ++) {
				Blob blob = colliders.get(i);
				g.fillOval((int)(blob.getX()-blob.r/2), (int)(blob.getY()-blob.r/2), (int)(2*blob.r), (int)(2*blob.r));
			}
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
			for (int i = 1; i < blobs.size(); i++) {
				tree.insert(blobs.get(i));
			}
			for (Blob blob : blobs) {
				ArrayList<Blob> found = (ArrayList<Blob>)tree.findInCircle(blob.getX(), blob.getY(), 2 * blob.r);
				//blobs.remove(found.size() - 1);
				if (found.size() > 1) {
					colliders.addAll(found);
				}
			}
			for (Blob blob: colliders) {
				blob.setCollided(true);
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
