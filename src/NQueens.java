// The N Queens problem, using A* and greedy search

// NOTES SO FAR:
// 1. We want a node class that stores a board state in it, that we can use to construct our tree,
// especially for A*, where we'll be expected to check back and test any nodes that could be better.
// Each node should store its board state and the cost to get to the node from the root.
// 2. We want heuristic functions for h1 and h2.

import java.util.ArrayList;
import java.util.Random;

public class NQueens {

    // A global variable for the current node
    static Node<char[][]> current = null;

    // Generates the starting state for the board with N queens
    public static char[][] generateStart(int numQueens) {
        char[][] state = new char[numQueens][numQueens];
        for (int i = 0; i < numQueens; i++) {
            for (int j = 0; j < numQueens; j++) {
                state[i][j] = '*';
            }
        }
        // Each queen is given a weight value that is represented
        for(int i = 0; i < numQueens; i++) {
            Random rand = new Random();
            int rowNum = rand.nextInt(numQueens);
            int queenWeight = rand.nextInt(9) + 1;
            state[rowNum][i] = Integer.toString(queenWeight).charAt(0);
        }
        return state;
    }

    // Evaluate if a given board state is a solution, (no queens are attacked)
    public static boolean isSolution(char[][] state) {
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                if (state[i][j] != '*') { // Found a queen, so see if it is attacked
                    if (isAttacked(i, j, state)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // A helper function to check if the queen at (row, column) is attacked
    public static boolean isAttacked(int row, int column, char[][] state) {
        //Check if there is another queen in the same row:
        for (int k = 0; k < state.length; k++) {
            if (state[row][k] != '*' && k != column) {
                // Queen is attacked in the same row
                return true;
            }
        }

        // Check if there is another queen on the same left diagonal
        int currentCol = column - 1;
        int currentRow = row - 1;
        while (currentCol >= 0 && currentRow >= 0) {
            if (state[currentRow][currentCol] != '*') {
                return true;
            }
            currentCol--;
            currentRow--;
        }
        currentCol = column + 1;
        currentRow = row + 1;
        while (currentCol < state.length && currentRow < state.length) {
            if (state[currentRow][currentCol] != '*') {
                return true;
            }
            currentCol++;
            currentRow++;
        }

        // Check if there is another queen on the same right diagonal
        currentCol = column - 1;
        currentRow = row + 1;
        while (currentCol >= 0 && currentRow < state.length) {
            if (state[currentRow][currentCol] != '*') {
                return true;
            }
            currentCol--;
            currentRow++;
        }
        currentCol = column + 1;
        currentRow = row - 1;
        while (currentCol < state.length && currentRow >= 0) {
            if (state[currentRow][currentCol] != '*') {
                return true;
            }
            currentCol++;
            currentRow--;
        }

        // If you get here, nothing attacks this queen
        return false;
    }

    // A helper function to count the number of attackers on a given queen at row, column
    public static int numAttackers(int row, int column, char[][] state) {
        int attackers = 0;
        // Check if there is another queen in the same row:
        for (int k = 0; k < state.length; k++) {
            if (state[row][k] != '*' && k != column) {
                // Queen is attacked in the same row
                attackers++;
            }
        }

        // Check if there is another queen on the same left diagonal
        int currentCol = column - 1;
        int currentRow = row - 1;
        while (currentCol >= 0 && currentRow >= 0) {
            if (state[currentRow][currentCol] != '*') {
                attackers++;
            }
            currentCol--;
            currentRow--;
        }
        currentCol = column + 1;
        currentRow = row + 1;
        while (currentCol < state.length && currentRow < state.length) {
            if (state[currentRow][currentCol] != '*') {
                attackers++;
            }
            currentCol++;
            currentRow++;
        }

        // Check if there is another queen on the same right diagonal
        currentCol = column - 1;
        currentRow = row + 1;
        while (currentCol >= 0 && currentRow < state.length) {
            if (state[currentRow][currentCol] != '*') {
                attackers++;
            }
            currentCol--;
            currentRow++;
        }
        currentCol = column + 1;
        currentRow = row - 1;
        while (currentCol < state.length && currentRow >= 0) {
            if (state[currentRow][currentCol] != '*') {
                attackers++;
            }
            currentCol++;
            currentRow--;
        }

        // If you get here, nothing attacks this queen
        return attackers;
    }

    // For H1 and H2: Returns the coordinates of the lowest cost attacking queen, and that queen's weight
    public static int[] hCurrent(char[][] state, String heuristic) {
        if (heuristic.equals("h1")) {
            int[] coordsThenWeight = new int[3];
            coordsThenWeight[2] = 10;

            // Start weight off as 10 to take the first attacked queen you see
            // If the weight stays as 10, no queens are attacked
            for (int i = 0; i < state.length; i++) {
                for (int j = 0; j < state.length; j++) {

                    // Note: May want to add randomness to pick queen of equal cost to best or not
                    // or pick the most attacked queen out of the lightest
                    if (state[i][j] != '*') {
                        // If this queen is lighter than previous best, and is attacked, take it as best so far
                        int queenVal = Integer.parseInt(Character.toString(state[i][j]));
                        if (queenVal < coordsThenWeight[2]) {
                            if (isAttacked(i, j, state)) {
                                coordsThenWeight[0] = i; // The row of the lowest cost queen so far
                                coordsThenWeight[1] = j; // The column of the lowest cost queen so far
                                coordsThenWeight[2] = queenVal; // The weight of the lowest cost queen so far
                            }
                        }
                    }
                }
            }
            return coordsThenWeight;
        } else { // H2: TODO
            return null;
        }
    }

    // May want a function for H1 and H2: Returns the coordinates and cost of the best next move, given knowing what queen to move
    // Note that h1Current[0] is row, [1] is column, [2] is weight

    // Expand the current node's state using h1, and generate successor states, adding them
    // to the node's list of children, if the current node has not already been expanded yet
    public static Node<char[][]> hExpand(int[] currentQueen, Node<char[][]> currentNode, String heuristic) {
        if (currentNode.children.size() > 0) {
            // This node has already been expanded, so no need to do it again.
            return currentNode;
        }
        if (heuristic.equals("h1")) {
            // TODO
            int row = currentQueen[0];
            int column = currentQueen[1];
            char[][] currentState = currentNode.state;
            int rows = currentState.length;
            for (int i = 0; i < rows; i++) {
                if (i != row) {
                    // Clone the old state to a new state, only changing where the queen is located
                    char[][] newState = currentState.clone();
                    newState[i][column] = currentState[row][column];
                    newState[row][column] = '*';
                    int cost = Math.abs(row - i) * currentQueen[2];
                    // Now, make a node that has the new state in it as a child
                    currentNode.addChild(newState, cost, hCurrent(newState, heuristic)[2]);
                }

            }
            return currentNode;
        } else {
            // TODO
            return currentNode;
        }
    }

    public static void main(String[] args) {
        // Input parameters
        int numQueens = Integer.parseInt(args[0]);
        int searchType = Integer.parseInt(args[1]); // 1 for A*, 2 for greedy
        String heuristic = args[2]; // Either h1 or h2
        int totalNodesExpanded = 0; // Increment every time we compute heuristic values for the board

        // Generate a start state based on the number of queens entered,
        // each queen starts in their own column, and a random row
        // # = queen, * = empty
        char[][] state = generateStart(numQueens);
        char[][] startState = state.clone();

        // Print the starting configuration for the user
        System.out.println("Starting board state:");
        for (int i = 0; i < numQueens; i++) {
            for (int j = 0; j < numQueens; j++) {
                System.out.print(state[i][j]);
            }
            System.out.println();
        }

        // Test to show hCurrent is working
        int[] test = new int[3];
        test = hCurrent(state, heuristic);
        System.out.print(test[0] + " " + test[1] + " " + test[2]);

        // Now, make the root node for reference later if you must reset
        Node<char[][]> root = new Node<char[][]>(state);
        root.heuristicVal = hCurrent(state, heuristic)[2];

        // Set the current node up to be the root node,
        current = new Node<char[][]>(state);
        current.heuristicVal = root.heuristicVal;

        // Now, given the current board state, find out the h values of each next move,
        // (expand the state) and pick the best one as the next node
        if (searchType == 1) {
            // Perform A* with backtracking
        }

        else { //(searchType == 2)
            // Perform greedy hill climbing with restarts for 10 seconds or less if solution is found
            while(!isSolution(current.state)) {
                // Stop once the current state is a solution, and print it out
                // First we find what queen we want to move
                int[] queenToMove = new int[2];
                queenToMove = hCurrent(current.state, heuristic);
                // Next we expand the current node, (add all possible successors as children based on heuristic)
                Node<char[][]> expanded = hExpand(queenToMove, current, heuristic);
                if (expanded.children.size() > current.children.size()) {
                    totalNodesExpanded++;
                    current.children = new ArrayList<Node<char[][]>>(expanded.children);
                }
                // Now we look at each of the children of the current state that have been generated and
                // pick one at random to use next based on what has the best heuristic, ignoring cost
                int bestHeuristic = -1;
                ArrayList<Node<char[][]>> options = new ArrayList<Node<char[][]>>();
                //Node<char[][]> next = null;
                for (Node<char[][]> e: current.children) {
                    if (e.heuristicVal < bestHeuristic || bestHeuristic == -1) {
                        bestHeuristic = e.heuristicVal;
                        options = new ArrayList<Node<char[][]>>();
                        options.add(e);
                    } else if (e.heuristicVal == bestHeuristic) {
                        options.add(e);
                    }
                }
                // What do we do if no children provide improvements or sideways? Reset!
                // (currently gets stuck with sideways moves)
                if (bestHeuristic > current.heuristicVal) {
                    current = root;
                    System.out.println("Resetting!");
                } else {
                    // Now that we have a list of best possible children that are better,
                    // pick one at random for the next looping
                    int choice = options.size();
                    Random rand = new Random();
                    choice = rand.nextInt(choice);
                    current = options.get(choice);
                    // Print current board state to see what happens
                    System.out.println("Current board state:");
                    for (int i = 0; i < numQueens; i++) {
                        for (int j = 0; j < numQueens; j++) {
                            System.out.print(current.state[i][j]);
                        }
                        System.out.println();
                    }
                    test = hCurrent(current.state, heuristic);
                    System.out.println(test[0] + " " + test[1] + " " + test[2]);
                }
            }
            // IF we run into a situation where there are no improvements to be made, then
            // make a number of sideways moves, after which, we reset
        }

        // Now, for the current node in the tree, go the next node, making sure
        // that you keep track of your path and can go back for A* if needed.

        // Keep expanding into new nodes until a solution is reached, or you must reset.
    }
}