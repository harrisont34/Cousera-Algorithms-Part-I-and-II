/* *****************************************************************************
 * Compilation:  javac-algs4 Board.java
 * Execution:    java-algs4 Board puzzle04.txt
 *
 * Algs-4 Week 4 Programming Assignment:
 * https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

// comment

public class Board {

    private final int[][] board;
    private final int width;
    private int zeroX;
    private int zeroY;


    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {

        if (tiles == null) throw new IllegalArgumentException();

        width = tiles[0].length;
        this.board = new int[width][width];


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                if (tiles[x][y] == 0) {
                    zeroX = x;
                    zeroY = y;
                }
                board[x][y] = tiles[x][y];
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(width + "\n");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                s.append(String.format("%2d ", board[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return width;
    }

    // number of tiles out of place
    public int hamming() {
        int hammingCount = 0;
        int tilePosition = 1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                if (board[x][y] != tilePosition && board[x][y] != 0) {
                    hammingCount++;
                }
                tilePosition++;
            }
        }
        return hammingCount;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattanCount = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                int tilePosition = board[x][y];
                if (tilePosition != 0) {
                    int expectedX = (tilePosition - 1) / width;
                    int expectedY = tilePosition - 1 - (expectedX * width);
                    manhattanCount += Math.abs(x - expectedX) + Math.abs(y - expectedY);
                }
            }
        }
        return manhattanCount;
    }


    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (that.dimension() != this.dimension()) return false;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++)
                if (this.board[i][j] != that.board[i][j]) {
                    return false;
                }
        }
        return true;
    }

    private static void swap(int[][] a, int x1, int y1, int x2, int y2) {
        int temp = a[x1][y1];
        a[x1][y1] = a[x2][y2];
        a[x2][y2] = temp;
    }


    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> neighbors = new Queue<Board>();

        // Clone Reference Board in 2d
        int[][] referenceBoard = new int[width][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++)
                referenceBoard[i][j] = board[i][j];
        }

        // Left
        if (zeroY - 1 >= 0) {
            swap(referenceBoard, zeroX, zeroY, zeroX, zeroY - 1);
            neighbors.enqueue(new Board(referenceBoard));
            swap(referenceBoard, zeroX, zeroY, zeroX, zeroY - 1);
        }
        // Right
        if (zeroY + 1 < width) {
            swap(referenceBoard, zeroX, zeroY, zeroX, zeroY + 1);
            neighbors.enqueue(new Board(referenceBoard));
            swap(referenceBoard, zeroX, zeroY, zeroX, zeroY + 1);
        }
        // Up
        if (zeroX - 1 >= 0) {
            swap(referenceBoard, zeroX, zeroY, zeroX
                    - 1, zeroY);
            neighbors.enqueue(new Board(referenceBoard));
            swap(referenceBoard, zeroX, zeroY, zeroX
                    - 1, zeroY);
        }
        // Down
        if (zeroX + 1 < width) {
            swap(referenceBoard, zeroX, zeroY, zeroX
                    + 1, zeroY);
            neighbors.enqueue(new Board(referenceBoard));
            swap(referenceBoard, zeroX, zeroY, zeroX
                    + 1, zeroY);
        }

        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {

        int[][] twin = new int[width][width];

        // Copy Board
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++)
                twin[i][j] = board[i][j];
        }

        // Find Two Non Zero Number
        int count = 2;
        int x1, y1, x2, y2;
        x1 = 0;
        y1 = 0;
        x2 = 0;
        y2 = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                if (count == 0) break;
                else if (twin[i][j] != 0 && count == 2) {
                    x1 = i;
                    y1 = j;
                    count--;
                }
                else if (twin[i][j] != 0 && count == 1) {
                    x2 = i;
                    y2 = j;
                    count--;
                }
            }
        }

        swap(twin, x1, y1, x2, y2);
        Board twinBoard = new Board(twin);

        return twinBoard;
    }

    // unit testing (not graded)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }

        Board initial = new Board(tiles);

        // Test dimension
        System.out.println("dimension: " + initial.dimension());

        // Test toString
        System.out.println("toString: ");
        System.out.println(initial);

        // Test Hamming
        System.out.println("hamming: " + initial.hamming());

        // Test Manhattan
        System.out.println("manhattan: " + initial.manhattan());

        // Test Manhattan
        System.out.println("isGoal: " + initial.isGoal());

        // Test Neighbors
        System.out.println("Neighbors: ");
        for (Board i : initial.neighbors()) {
            System.out.print(i);
        }
        System.out.println(" ");

        // Test Twin
        System.out.println("Twin: " + initial.twin());

    }
}

