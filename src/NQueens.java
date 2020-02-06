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

    // For H1: Returns the coordinates of the lowest cost attacking queen, and that queen's weight
    public static int[] h1Current(char[][] state) {
        return null;
    }

    // For H1: Returns the coordinates and cost of the best next move, given knowing what queen to move
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

        // Now, make the root node
        Node<char[][]> root = new Node<char[][]>(state);

        // Now, given the current board state, find out the h values of each next move.

        // Now, for the current node in the tree, expand to the next node, making sure
        // that you keep track of your path and can go back for A* if needed.

        // Keep expanding into new nodes until a solution is reached, or you must reset.
    }
}