/* *****************************************************************************
 *  Compilation:  javac-algs4 SeamCarver.java
 *  Execution:  java-algs4 SeamCarver 3x4.png
 *  Description:
 *  https://coursera.cs.princeton.edu/algs4/assignments/seam/specification.php
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private Picture pic;
    private int height;
    private int width;
    private boolean transposed;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {

        // Exception Handling
        if (picture == null) {
            throw new IllegalArgumentException("Picture object is null.");
        }

        // Create Deep Copy of Picture
        this.pic = new Picture(picture);
        this.width = picture.width();
        this.height = picture.height();
        this.transposed = false;
    }

    // current picture
    public Picture picture() {
        return pic;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {

        // Exception Handling
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
            throw new IllegalArgumentException("Pixel is outside picture range.");
        }

        // Border Check
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return 1000;
        }

        return Math.sqrt(calculateAxisX(x, y) + calculateAxisY(x, y));
    }

    private double calculateAxisX(int x, int y) {

        int rightPixel = pic.getRGB(x + 1, y);
        int leftPixel = pic.getRGB(x - 1, y);

        int r = Math.abs(((rightPixel >> 16) & 0xFF) - ((leftPixel >> 16) & 0xFF));
        int g = Math.abs(((rightPixel >> 8) & 0xFF) - ((leftPixel >> 8) & 0xFF));
        int b = Math.abs(((rightPixel >> 0) & 0xFF) - ((leftPixel >> 0) & 0xFF));
        return Math.pow(r, 2) + Math.pow(b, 2) + Math.pow(g, 2);
    }

    private double calculateAxisY(int x, int y) {

        int upPixel = pic.getRGB(x, y - 1);
        int downPixel = pic.getRGB(x, y + 1);

        int r = Math.abs(((upPixel >> 16) & 0xFF) - ((downPixel >> 16) & 0xFF));
        int g = Math.abs(((upPixel >> 8) & 0xFF) - ((downPixel >> 8) & 0xFF));
        int b = Math.abs(((upPixel >> 0) & 0xFF) - ((downPixel >> 0) & 0xFF));
        return Math.pow(r, 2) + Math.pow(b, 2) + Math.pow(g, 2);
    }


    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {

        // Transpose Picture
        if (!transposed) {
            transpose();
        }
        int[] seam = find();
        transpose();
        return seam;
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        // Transpose if wrong orientation
        if (transposed) {
            transpose();
        }
        return find();
    }

    private int[] find() {

        // Support arrays
        double[][] distTo = new double[width][height];
        int[][] edgeTo = new int[width][height];
        double[][] energyMatrix = new double[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                energyMatrix[x][y] = energy(x, y);
                distTo[x][y] = Double.POSITIVE_INFINITY;
                if (y == 0) {
                    distTo[x][0] = energyMatrix[x][0]; // check
                }
            }
        }


        // Execute topological sort algorithm
        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Check Down-Left
                if (x > 0 && distTo[x - 1][y] > distTo[x][y - 1] + energyMatrix[x - 1][y]) {
                    distTo[x - 1][y] = distTo[x][y - 1] + energyMatrix[x - 1][y];
                    edgeTo[x - 1][y] = x;
                }
                // Check Down
                if (distTo[x][y] > distTo[x][y - 1] + energyMatrix[x][y]) {
                    distTo[x][y] = distTo[x][y - 1] + energyMatrix[x][y];
                    edgeTo[x][y] = x;
                }
                // Check Down-Right
                if (x < width - 1 && distTo[x + 1][y] > distTo[x][y - 1] + energyMatrix[x
                        + 1][y]) {
                    distTo[x + 1][y] = distTo[x][y - 1] + energyMatrix[x + 1][y];
                    edgeTo[x + 1][y] = x;
                }

            }
        }


        // Find bottom pixel with lowest distTo (smallest vertical seam)
        int shortest = 0;
        for (int x = 0; x < width; x++) {
            if (distTo[x][height - 1] < distTo[shortest][height - 1]) {
                shortest = x;
            }
        }

        // Reverse to find path
        int[] verticalSeam = new int[height];
        int minimumSeam = height - 1;
        while (minimumSeam >= 0) {
            verticalSeam[minimumSeam] = shortest;
            shortest = edgeTo[shortest][minimumSeam--];
        }

        return verticalSeam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {

        // Transpose Picture
        if (!transposed) {
            transpose();
        }

        // Error Handling
        if (seam == null) {
            throw new IllegalArgumentException("Seam input is null");
        }
        if (seam.length != height) {
            throw new IllegalArgumentException("Seam length does not span picture");
        }
        if (pic.width() <= 1) {
            throw new IllegalArgumentException("Cannot remove entire picture");
        }
        for (int s : seam) {
            if (s < 0 || s > width) {
                throw new IllegalArgumentException("Seam outside picture");
            }
        }

        // Call Remove Vertical Seam without affecting transposed
        remove(seam);
        transpose();

    }


    public void removeVerticalSeam(int[] seam) {

        // Transpose if wrong orientation
        if (transposed) {
            transpose();
        }

        // Error Handling
        if (seam == null) {
            throw new IllegalArgumentException("Seam input is null");
        }
        if (seam.length != height) {
            throw new IllegalArgumentException("Seam length does not span picture");
        }
        if (pic.width() <= 1) {
            throw new IllegalArgumentException("Cannot remove entire picture");
        }
        for (int s : seam) {
            if (s < 0 || s > width) {
                throw new IllegalArgumentException("Seam outside picture");
            }
        }

        remove(seam);
    }


    // remove vertical seam from current picture
    private void remove(int[] seam) {

        Picture trimmedPicture = new Picture(width - 1, height);

        int check = seam[0];
        for (int s : seam) {
            if (s > check + 1 || s < check - 1) {
                throw new IllegalArgumentException("Invalid Seam");
            }
            check = s;
        }

        // Copy pic to trimmedPicture minus seam
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int skip = seam[y];
                if (x < skip) {
                    trimmedPicture.set(x, y, pic.get(x, y));
                }
                else if (x > skip) {
                    trimmedPicture.set(x - 1, y, pic.get(x, y));
                }
            }
        }

        pic = trimmedPicture;
        this.width = trimmedPicture.width();
        this.height = trimmedPicture.height();

    }

    private void transpose() {

        transposed = !transposed;
        int copyHeight = width;
        int copyWidth = height;

        Picture copy = new Picture(copyWidth, copyHeight);

        // Copy picture switching x and y coordinates
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pic.get(x, y);
                copy.set(y, x, color);
            }
        }

        this.height = copyHeight;
        this.width = copyWidth;
        this.pic = copy;

    }


    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver seamCarver = new SeamCarver(picture);
        int width = seamCarver.width();
        int height = seamCarver.height();
        System.out.println("Height: " + height + " and Width: " + width);

        System.out.println("Energy: " + seamCarver.energy(1, 1));

        int[] Vseam = seamCarver.findVerticalSeam();
        for (int i = 0; i < Vseam.length; i++) {
            System.out.print(Vseam[i] + " ");
        }
        System.out.println();

        seamCarver.removeVerticalSeam(Vseam);

        for (int y = 0; y < seamCarver.pic.height(); y++) {
            for (int x = 0; x < seamCarver.pic.width(); x++) {
                System.out.print((int) seamCarver.energy(x, y) + " ");
            }
            System.out.println();
        }
        System.out.println();

        int[] Hseam = seamCarver.findHorizontalSeam();

        for (int i = 0; i < Hseam.length; i++) {
            System.out.print(Hseam[i] + " ");
        }
        System.out.println();

        seamCarver.removeHorizontalSeam(Hseam);

        for (int y = 0; y < seamCarver.pic.height(); y++) {
            for (int x = 0; x < seamCarver.pic.width(); x++) {
                System.out.print((int) seamCarver.energy(x, y) + " ");
            }
            System.out.println();
        }
        System.out.println();

    }


}






