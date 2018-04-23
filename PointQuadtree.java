import java.util.ArrayList;
import java.util.List;


/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || 
				(quadrant==2 && c2!=null) || 
				(quadrant==3 && c3!=null) || 
				(quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		for (int i = 1; i <= 4; i ++) {
			if (isWithinBounds(i, p2)) {
				if (hasChild(i)) {
					getChild(i).insert(p2);
				}
				else {
					setQuadrant(i, makeBoundedPoint(i, p2));
				}
			}
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE
		int size = 1;
		for (int i = 1; i <= 4; i ++) {
			if (hasChild(i)) {
				size += getChild(i).size();
			}
		}
		return size;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		List<E> myChildren = new ArrayList<E>();
		myChildren.add(point);
		for (int i = 1; i <= 4; i ++) {
			if (hasChild(i)) {
				myChildren.addAll(getChild(i).allPoints());
			}
		}
		return myChildren;
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		List<E> myHits = new ArrayList<E>();   // ArrayList works but List doesn't
		// If the circle intersects the rectangle
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
			// If the tree's point is in the circle, then the blob is a "hit"
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) {
				myHits.add(point);
			}
			//For each quadrant with a child
			for (int i = 1; i <= 4; i ++) {
				if (hasChild(i)) {
					//Recurse with that child
					myHits.addAll(getChild(i).findInCircle(cx, cy, cr));
				}
			}
		}
		return myHits;
	}
	
	// TODO: YOUR CODE HERE for any helper methods
	
	// Setters
	private void setQuadrant(int quadrant, PointQuadtree<E> target) {
		if (quadrant == 1) {
			c1 = target;
		}
		else if (quadrant == 2) {
			c2 = target;
		}
		else if (quadrant == 3) {
			c3 = target;
		}
		else if (quadrant == 4) {
			c4 = target;
		}
		else {
			System.out.println("You just tried to access an invalid quadrant. See PointQuadtree's setQuadrant.");
		}
	}
	
	// Check whether a point fits in a quadrant
	private boolean isWithinBounds(int quadrant, E p2) {
		if (
				(quadrant == 1 && (int)p2.getX() <= point.getX() && (int)p2.getY() <= point.getY()) ||
				(quadrant == 2 && (int)p2.getX() >= point.getX() && (int)p2.getY() <= point.getY()) ||
				(quadrant == 3 && (int)p2.getX() <= point.getX() && (int)p2.getY() >= point.getY()) ||
				(quadrant == 4 && (int)p2.getX() >= point.getX() && (int)p2.getY() >= point.getY())
				) {
			return true;
		}
		return false;
	}
	
	// Make a new PointQuadtree with bounds appropriate to a numbered quadrant
	private PointQuadtree<E> makeBoundedPoint(int quadrant, E p2) {
		if (quadrant == 1) {
			return new PointQuadtree(p2, x1, y1, (int)point.getX(), (int)point.getY());
		}
		else if (quadrant == 2) {
			return new PointQuadtree(p2, (int)point.getX(), y1, x2, (int)point.getY());
		}
		else if (quadrant == 3) {
			return new PointQuadtree(p2, x1, (int)point.getY(), (int)point.getX(), y2);
		}
		else if (quadrant == 4) {
			return new PointQuadtree(p2,(int)point.getX(), (int)point.getY(), x2, y2);
		}
		else {
			return null;
		}
	}
}
