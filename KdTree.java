/* *****************************************************************************
 *  Compilation: javac-algs4 KdTree.java
 *  Execution: java-algs4 KdTree < input10.txt
 *
 *  Algs-4 Week 5 Programming Assignment
 *  Source:
 *  https://coursera.cs.princeton.edu/algs4/assignments/kdtree/specification.php
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;

public class KdTree {

    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;


    private Node root;  // root of KdTree
    private int n;      // number of key-value pairs in KdTree
    private final RectHV plane;  // Starting plane/sheet


    private class Node {
        private final Point2D point;
        private Node left, right;
        private final RectHV rect;
        private final boolean division;


        public Node(Point2D point, RectHV rect, boolean division) {
            this.point = point;
            this.rect = rect;
            this.division = division;
        }
    }

    // construct an empty set of points
    public KdTree() {
        plane = new RectHV(0, 0, 1, 1);
        n = 0;
        root = null;
    }

    // is the set empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // number of points in the set
    public int size() {
        return n;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        root = insert(root, p, plane, VERTICAL);
    }

    private Node insert(Node rootNode, Point2D p, RectHV rect, boolean division) {
        if (rootNode == null) {
            this.n++;
            return new Node(p, rect, division);
        }

        if (rootNode.point.equals(p)) {
            return rootNode;
        }

        // compare x or y coordinate
        boolean cmp;
        if (division == VERTICAL) {
            cmp = p.x() < rootNode.point.x();
        }
        else {   // h.division == HORIZONTAL
            cmp = p.y() < rootNode.point.y();
        }

        // Navigate KdTree
        if (cmp) { // less than
            if (rootNode.division == VERTICAL) {
                RectHV newRect = new RectHV(rootNode.rect.xmin(), rootNode.rect.ymin(),
                                            rootNode.point.x(),
                                            rootNode.rect.ymax());
                rootNode.left = insert(rootNode.left, p, newRect, HORIZONTAL);
            }
            else { // h.division == HORIZONTAL
                RectHV newRect = new RectHV(rootNode.rect.xmin(), rootNode.rect.ymin(),
                                            rootNode.rect.xmax(),
                                            rootNode.point.y());
                rootNode.left = insert(rootNode.left, p, newRect, VERTICAL);
            }
        }
        else {  // cmp == false, greater than
            if (rootNode.division == VERTICAL) {
                RectHV newRect = new RectHV(rootNode.point.x(), rootNode.rect.ymin(),
                                            rootNode.rect.xmax(),
                                            rootNode.rect.ymax());
                rootNode.right = insert(rootNode.right, p, newRect, HORIZONTAL);
            }
            else { // h.division == HORIZONTAL
                RectHV newRect = new RectHV(rootNode.rect.xmin(), rootNode.point.y(),
                                            rootNode.rect.xmax(),
                                            rootNode.rect.ymax());
                rootNode.right = insert(rootNode.right, p, newRect, VERTICAL);
            }
        }
        return rootNode;
    }


    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return (get(root, p) != null);
    }


    private Point2D get(Node x, Point2D p) {
        while (x != null) {
            if (x.point.equals(p)) {
                return p;
            }
            boolean cmp;
            if (x.division == VERTICAL) {
                cmp = p.x() < x.point.x();
            }
            else {   // h.division == HORIZONTAL
                cmp = p.y() < x.point.y();
            }

            if (cmp) {
                x = x.left;
            }
            else {
                x = x.right;
            }
        }
        return null;
    }

    // draw all points to standard draw
    public void draw() {
        draw(root);
    }

    private void draw(Node x) {
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        x.point.draw();
        StdDraw.setPenRadius();

        if (x.division == VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(x.point.x(), x.rect.ymin(), x.point.x(),
                         x.rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(x.rect.xmin(), x.point.y(), x.rect.xmax(),
                         x.point.y());
        }
        if (x.left != null) {
            draw(x.left);
        }
        if (x.right != null) {
            draw(x.right);
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> ptsInRange = new Queue<>();
        range(rect, root, ptsInRange);
        return ptsInRange;
    }

    private void range(RectHV rect, Node x, Queue<Point2D> queue) {
        if (x == null) {
            return;
        }

        if (rect.contains(x.point)) {
            queue.enqueue(x.point);
        }

        if (rect.intersects(x.rect)) {
            range(rect, x.left, queue);
            range(rect, x.right, queue);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (this.isEmpty()) throw new IllegalArgumentException();
        return nearest(p, root, root.point);

    }

    private Point2D nearest(Point2D p, Node x, Point2D closestPoint) {
        if (x == null) return closestPoint;

        if (x.point.distanceSquaredTo(p) < closestPoint.distanceSquaredTo(p)) {
            closestPoint = x.point;
        }

        if (x.rect.distanceSquaredTo(p) < closestPoint.distanceSquaredTo(p)) {

            boolean cmp;
            if (x.division == VERTICAL) {
                cmp = p.x() < x.point.x();
            }
            else {   // h.division == HORIZONTAL
                cmp = p.y() < x.point.y();
            }

            if (cmp) {
                closestPoint = nearest(p, x.left, closestPoint);
                closestPoint = nearest(p, x.right, closestPoint);
            }
            else {
                closestPoint = nearest(p, x.right, closestPoint);
                closestPoint = nearest(p, x.left, closestPoint);
            }
        }
        return closestPoint;
    }


    // unit testing of the methods (optional)
    public static void main(String[] args) {

        KdTree tree = new KdTree();

        while (!StdIn.isEmpty()) {
            Point2D point = new Point2D(StdIn.readDouble(), StdIn.readDouble());
            tree.insert(point);
        }

        // Test contains
        Point2D test = new Point2D(0.499, 0.218);
        System.out.println("Contains point: " + tree.contains(test));

        // Test Draw
        tree.draw();

        // Test range
        for (Point2D i : tree.range(new RectHV(0.2, 0.2, 0.6, 0.6))) {
            System.out.println("Range Point: " + i);
        }

        // Test Nearest
        Point2D nearestPoint = new Point2D(0.34, 0.73);
        System.out.println("Nearest: " + tree.nearest(nearestPoint));

        System.out.println("Size: " + tree.size());

    }
}
