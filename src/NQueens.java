// The N Queens problem, using A* and greedy search

// NOTES SO FAR:
// 1. We want a node class that stores a board state in it, that we can use to construct our tree,
// especially for A*, where we'll be expected to check back and test any nodes that could be better.
// Each node should store its board state and the cost to get to the node from the root.
// 2. We want heuristic functions for h1 and h2.

import java.util.Random;

public class NQueens {

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

    // For H1: Returns the coordinates of the lowest cost attacking queen, and that queen's weight
    public static int[] h1Current(char[][] state) {
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
    }

    // For H1: Returns the coordinates and cost of the best next move, given knowing what queen to move
    // Note that h1Current[0] is row, [1] is column, [2] is weight
    public static int[] h1Next(int[] h1Current) {
        return null;
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

        // Print the starting configuration for the user
        System.out.println("Starting board state:");
        for (int i = 0; i < numQueens; i++) {
            for (int j = 0; j < numQueens; j++) {
                System.out.print(state[i][j]);
            }
            System.out.println();
        }

        // Test to show h1Current is working
        int[] test = new int[3];
        test = h1Current(state);
        System.out.print(test[0] + " " + test[1] + " " + test[2]);

        // Now, make the root node
        Node<char[][]> root = new Node<char[][]>(state);

        // Now, given the current board state, find out the h values of each next move.

        // Now, for the current node in the tree, expand to the next node, making sure
        // that you keep track of your path and can go back for A* if needed.

        // Keep expanding into new nodes until a solution is reached, or you must reset.
    }
}