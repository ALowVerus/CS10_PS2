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
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		// If point lies in quadrant 1
		if ((int)p2.getX() <= point.getX() && (int)p2.getY() <= point.getY()) {
			if (hasChild(1)) {
				getChild(1).insert(p2);
			}
			else {
				c1 new PointQuadtree(p2, x1, y1, (int)point.getX(), (int)point.getY());
			}	
		}
		// If point lies in quadrant 2
		else if ((int)p2.getX() >= point.getX() && (int)p2.getY() <= point.getY()) {
			if (hasChild(2)) {
				getChild(2).insert(p2);
			}
			else {
				c2 = new PointQuadtree(p2, (int)point.getX(), y1, x2, (int)point.getY());
			}	
		}
		// If point lies in quadrant 3
		else if ((int)p2.getX() <= point.getX() && (int)p2.getY() >= point.getY()) {
			if (hasChild(3)) {
				getChild(3).insert(p2);
			}
			else {
				c3 = new PointQuadtree(p2, x1, (int)point.getX(), (int)point.getX(), y2);
			}	
		}
		// If point lies in quadrant 4
		else if ((int)p2.getX() >= point.getX() && (int)p2.getY() >= point.getY()) {
			if (hasChild(4)) {
				getChild(4).insert(p2);
			}
			else {
				c4 = new PointQuadtree(p2,(int)point.getX(), (int)point.getY(), x2, y2);
			}	
		}
		else {
			System.out.println("Invalid code. Check the PointQuadtree.inster(p2");
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
		//To find all points within the circle (cx,cy,cr), stored in a tree covering rectangle (x1,y1)-(x2,y2)
		List<E> myHits = new ArrayList<E>();   // ArrayList works but List doesn't
		//If the circle intersects the rectangle
		if (cx + cr >= x2 || cx - cr <= x1 || cy + cr >= y2 || cy - cr <= y1) {
			//If the tree's point is in the circle, then the blob is a "hit"
			if (cr * cr >= (point.getX() - cx)*(point.getX() - cx) + (point.getY() - cy)*(point.getY() - cy)) {
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
}
