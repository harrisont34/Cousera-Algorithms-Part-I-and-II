/* *****************************************************************************
 * Compilation:  javac-algs4 Solver.java
 * Execution:    java-algs4 Solver puzzle04.txt
 *
 * Algs-4 Week 4 Programming Assignment:
 * https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private boolean solvable;
    private final Stack<Board> boardStack;

    // Search Node helper class
    private class SearchNode implements Comparable<SearchNode> {
        private final Board nodeBoard;
        private final int moves;
        private final SearchNode previousNode;
        private final int priority;

        SearchNode(Board board) {
            this.nodeBoard = board;
            this.moves = 0;
            this.previousNode = null;
            this.priority = 0;
        }

        SearchNode(Board nodeBoard, int moves, SearchNode previous) {
            this.nodeBoard = nodeBoard;
            this.moves = moves;
            this.previousNode = previous;
            this.priority = nodeBoard.manhattan() + moves;
        }

        public int compareTo(SearchNode that) {
            return this.priority - that.priority;
        }

    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {

        if (initial == null) throw new IllegalArgumentException();

        // Create initial search Node and insert into Priority Queue
        SearchNode initialNode = new SearchNode(initial);
        MinPQ<SearchNode> pq = new MinPQ<>();
        pq.insert(initialNode);

        // Create Twin search node and insert into Twin Priority Queue
        SearchNode twinNode = new SearchNode(initial.twin());
        MinPQ<SearchNode> twinpq = new MinPQ<>();
        twinpq.insert(twinNode);

        // History Stack and Goal Stack
        boardStack = new Stack<>();
        Stack<SearchNode> goalBoard = new Stack<>();

        // While Loop
        while (!pq.isEmpty() && !twinpq.isEmpty()) {

            // Remove SearchNode with smallest priority in each pq
            SearchNode currentNode = pq.delMin();
            SearchNode currentTwinNode = twinpq.delMin();


            SearchNode previousNode = currentNode.previousNode;
            SearchNode previousTwinNode = currentTwinNode.previousNode;

            // Check if SearchNode board is goal
            if (currentNode.nodeBoard.isGoal()) {
                this.solvable = true;
                goalBoard.push(currentNode);
                break;
            }
            // Check if Twin SearchNode board is goal
            else if (currentTwinNode.nodeBoard.isGoal()) {
                this.solvable = false;
                break;
            }
            else {
                for (Board pqBoard : currentNode.nodeBoard.neighbors()) {
                    if (previousNode == null || !pqBoard.equals(previousNode.nodeBoard)) {
                        pq.insert(new SearchNode(pqBoard, currentNode.moves + 1,
                                                 currentNode));
                    }

                }

                for (Board pqTwinBoard : currentTwinNode.nodeBoard.neighbors()) {
                    if (previousTwinNode == null || !pqTwinBoard
                            .equals(previousTwinNode.nodeBoard)) {
                        twinpq.insert(new SearchNode(pqTwinBoard, currentTwinNode.moves + 1,
                                                     currentTwinNode));
                    }
                }

            }
        }

        // Check if unsolvable and Reverse stack order
        if (!goalBoard.isEmpty()) {
            SearchNode list = goalBoard.pop();
            while (list.previousNode != null) {
                boardStack.push(list.nodeBoard);
                list = list.previousNode;
            }
            boardStack.push(list.nodeBoard);
        }

    }


    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return boardStack.size() - 1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (boardStack.size() == 0) return null;
        return boardStack;
    }


    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);


        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}


