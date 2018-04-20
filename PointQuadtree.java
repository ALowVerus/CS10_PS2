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
		// TODO: YOUR CODE HERE --bounds might be wrong--
		
		
		// Original, custom bounds. We commented this out after we read the documentation. F
		
		//To find all points within the circle (cx,cy,cr), stored in a tree covering rectangle (x1,y1)-(x2,y2)
		List<E> myHits = new ArrayList<E>();   // ArrayList works but List doesn't
		// If the circle intersects the rectangle
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
		// if (cx - cr <= x2 && cx + cr >= x1 && cy + cr >= y1 && cy - cr <= y2) {
			// If the tree's point is in the circle, then the blob is a "hit"
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) {
			// if (cr * cr >= (point.getX() - cx)*(point.getX() - cx) + (point.getY() - cy)*(point.getY() - cy)) {
				// System.out.println("Trange things have happened here");
				myHits.add(point);
			}
			//For each quadrant with a child
			for (int i = 1; i <= 4; i ++) {
				if (hasChild(i)) {
					//Recurse with that child
					//System.out.println("Children acquired");
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

	// Delete a point from the PointQuadtree
	public void deleteNode(PointQuadtree<E> target) {
		// First, navigate to the point above the target
		int targetNumber = 5;
		for (int i = 1; i <= 4; i ++) {
			if (isWithinBounds(i, target.getPoint())) {
				// Check whether the child is the target, and if it is, run delete methods
				if (getChild(i) == target) {
					// If node is null, delete it
					if (getChild(i).size() == 1) {
						setQuadrant(i, null);
					}
					// If node has one child, set child to own area
					if (getChild(i).size() == 2) {
						PointQuadtree<E> child = getChild(i);
						setQuadrant(i, null);
						for (int j = 1; j <= 4; j ++) {
							if (child.hasChild(j)) {
								insert(child.getChild(j).getPoint());
							}
						}
					}
					// If node has more than one child, get the largest on the left or the smallest on the right
				}
				// If the child isn't the father, recurse into the child
				else {
					deleteNode(target);
				}
			}
		}
	}
}
