/* *****************************************************************************
 *  Compilation: javac-algs4 PointSET.java
 *  Execution: java-algs4 PointSET < input10.txt
 *
 *  Algs-4 Week 5 Programming Assignment
 *  Description: Write a mutable data type PointSET.java that represents a set
 *  of points in the unit square. Implement the following API by using a
 *  red–black BST.  Use java.util.TreeSet.  Do not implement your own red-black
 *  BST.
 *
 *  Source:
 *  https://coursera.cs.princeton.edu/algs4/assignments/kdtree/specification.php
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;

public class PointSET {

    private final SET<Point2D> set;

    // construct an empty set of points
    public PointSET() {
        set = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return set.isEmpty();
    }

    // number of points in the set
    public int size() {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D point : set) {
            point.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        Queue<Point2D> queue = new Queue<Point2D>();
        for (Point2D point : set) {
            if (rect.contains(point)) {
                queue.enqueue(point);
            }
        }
        return queue;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        Point2D closest = null;

        for (Point2D point : set) {
            if (closest == null || point.distanceSquaredTo(p) < closest.distanceSquaredTo(p)) {
                closest = point;
            }
        }
        return closest;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {

        PointSET set = new PointSET();

        // Test insert
        while (!StdIn.isEmpty()) {
            Point2D point = new Point2D(StdIn.readDouble(), StdIn.readDouble());
            set.insert(point);
        }

        // Test isEmpty
        System.out.println("Empty: " + set.isEmpty());

        // Test Size
        System.out.println("Size: " + set.size());

        // Test Contains
        System.out.println("Contains: " + set.contains(new Point2D(0.226, 0.577)));

        // Test Draw
        StdDraw.setPenRadius(0.01);
        set.draw();

        // Test Range
        RectHV rect = new RectHV(0.1, 0.1, 0.4, 0.4);
        StdDraw.setPenRadius();
        StdDraw.setPenColor(StdDraw.BOOK_BLUE);
        rect.draw();
        for (Point2D i : set.range(rect)) {
            System.out.println("Point in Range: " + i);
        }

        // Test Nearest
        Point2D point = new Point2D(0.3, 0.6);
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.GREEN);
        point.draw();
        System.out.println("Nearest: " + set.nearest(point));

    }

}

