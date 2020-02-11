// The N Queens problem, using A* and greedy search

// NOTES SO FAR:
// 1. We want a node class that stores a board state in it, that we can use to construct our tree,
// especially for A*, where we'll be expected to check back and test any nodes that could be better.
// Each node should store its board state and the cost to get to the node from the root.
// 2. We want heuristic functions for h1 and h2.
// 3. TODO Retrofit to work with Queen.java class and 1D array of queens

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

public class NQueens {

    // A global variable for the current node
    static Node<Queen[]> current = null;

    // Generates the starting state for the board with N queens
    public static Queen[] generateStart(int numQueens) {
        Queen[] state = new Queen[numQueens];
        // Each queen is given a weight value that is represented
        for(int i = 0; i < numQueens; i++) {
            Random rand = new Random();
            int rowNum = rand.nextInt(numQueens);
            int queenWeight = rand.nextInt(9) + 1;
            state[i] = new Queen(rowNum, i, queenWeight);
        }
        return state;
    }

    // Prints the given state
    public static void printBoard(Queen[] state) {
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                for (int k = 0; k < state.length; k++) {
                    if (state[k].row == i && state[k].column == j) {
                        System.out.print(" " + state[k].weight);
                        k = state.length;
                    }
                    if (k == state.length - 1) {
                        System.out.print(" *");
                    }
                }
            }
            System.out.println();
        }
    }

    // Evaluate if a given board state is a solution, (no queens are attacked)
    public static boolean isSolution(Queen[] state) {
        for (int i = 0; i < state.length; i++) {
                if (isAttacked(i, state)) {
                    return false;
                }
            }
        return true;
    }

    // A helper function to check if the queen at (row, column) is attacked
    public static boolean isAttacked(int queenIndex, Queen[] state) {
        for (int i = 0; i < state.length; i++) {
            // Don't check own queen
            if (i != queenIndex) {
                // Check if same row
                if (state[i].row == state[queenIndex].row) {
                    return true;
                } else if (Math.abs(state[i].row - state[queenIndex].row)
                        == Math.abs(state[i].column - state[queenIndex].column)) {
                    // Check on either diagonal
                    return true;
                }
             }
        }
        return false;
    }

    // A helper function to count the number of attackers on a given queen
    public static int numAttackers(int queenIndex, Queen[] state) {
        int attackers = 0;
        for (int i = 0; i < state.length; i++) {
            // Don't check own queen
            if (i != queenIndex) {
                // Check if same row
                if (state[i].row == state[queenIndex].row) {
                    attackers++;
                } else if (Math.abs(state[i].row - state[queenIndex].row)
                        == Math.abs(state[i].column - state[queenIndex].column)) {
                    // Check on either diagonal
                    attackers++;
                }
             }
        }
        return attackers;
    }

    // For H1 and H2: Returns the value for the heuristic value for a given state
    public static int hCurrent(Queen[] state, String heuristic) {
        if (heuristic.equals("h1")) {
            int lowestWeight = 82;
            int bestIndex = -1;
            // Start weight off as 10 to take the first attacked queen you see
            // If the weight stays as 10, no queens are attacked
            for (int i = 0; i < state.length; i++) {
                // Note: May want to add randomness to pick queen of equal cost to best or not
                // or pick the most attacked queen out of the lightest
                // If this queen is lighter than previous best, and is attacked, take it as best so far
                int queenVal = state[i].weight * state[i].weight;
                if (queenVal < lowestWeight) {
                    if (isAttacked(i, state)) {
                        bestIndex = i;
                        lowestWeight = queenVal; // The weight of the lowest cost queen so far
                    }
                }
            } if (bestIndex > -1) {
                return lowestWeight;
            } else {
                // There are no moves left to make, you are at a solution already!
                return 0;
            }
        } else { // H2: ----------------------------------------------------------------------
        	int sumOfLightestQueens = 0;
        	/*
        	 * For each queen on the board:
        	 * Iterate through every queen after it, and
        	 * if they attack each other,
        	 * Add the lowest of their weights to the sum
        	 */
        	// for each queen on the board...
        	for(int i = 0; i < state.length; i++) {
        		// iterate through every queen after it
	        	for(int curPos = i; curPos < state.length; curPos++) {
	        		// and if they attack each other...
	        		if(state[i].isAttackedBy(state[curPos])) {
	        			//add the lowest of their weights to the sum
	        			sumOfLightestQueens += Math.min(state[i].weight * state[i].weight, state[curPos].weight * state[curPos].weight);
	        		}
	        	}
        	}
        	return sumOfLightestQueens;
        }
    }

    // May want a function for H1 and H2: Returns the coordinates and cost of the best next move, given knowing what queen to move

    // Expand the current node's state by moving only the queen at the given index, and generate successor states, adding them
    // to the node's list of children, if the current node has not already been expanded yet
    // NOTE: WE MAY NOT ACTUALLY NEED THIS FUNCTION, AS WE WANT TO EXPAND ALL QUEENS FROM EACH STATE
    public static Node<Queen[]> hExpand(int currentQueen, Node<Queen[]> currentNode, String heuristic) {
        if (currentNode.children.size() > 0) {
            // This node has already been expanded, so no need to do it again.
            return currentNode;
        }
        Queen[] currentState = currentNode.state;
        int row = currentState[currentQueen].row;
        int rows = currentState.length;
        for (int i = 0; i < rows; i++) {
            if (i != row) {
                // Clone the old state to a new state, only changing where the queen is located
                Queen[] newState = new Queen[currentState.length];
                for (int j = 0; j < newState.length; j++) {
                    newState[j] = new Queen(currentState[j]);
                }
                newState[currentQueen].row = i;
                int cost = Math.abs(row - i) * currentState[currentQueen].weight * currentState[currentQueen].weight;
                // Now, make a node that has the new state in it as a child
                currentNode.addChild(newState, cost, hCurrent(newState, heuristic), currentQueen);
            }
        }
        return currentNode;
    }

    // Expand the current node's state by moving ANY queen, and generate successor states, adding them
    // to the node's list of children, if the current node has not already been expanded yet
    // OPTIMIZATION TODO: MAKE IT SO CHILDREN CANT MOVE THE SAME QUEEN AS THEIR
    // PARENT, AS THOSE MOVES WON'T HELP THINGS AT ALL, (if they are better,
    // then their parent has already expanded a child that has that better option)
    // Want to find ways to restrict expansion and minimize b as much as
    // possible
    public static Node<Queen[]> hExpand(Node<Queen[]> currentNode, String heuristic) {
        if (currentNode.children.size() > 0) {
            // This node has already been expanded, so no need to do it again.
            return currentNode;
        }
        Queen[] currentState = currentNode.state;
        int queens = currentState.length;
        // For each queen, make all moves possible to put the queen in a different row,
        // same column
        for (int currentQueen = 0; currentQueen < queens; currentQueen++) {
            if (currentQueen != currentNode.lastMoveIndex) {
                int row = currentState[currentQueen].row;
                for (int i = 0; i < queens; i++) {
                    // Don't consider moves where a queen ends up in the same row, that's not a move!
                    if (i != row) {
                        // Clone the old state to a new state, only changing where the queen is located
                        Queen[] newState = new Queen[currentState.length];
                        for (int j = 0; j < newState.length; j++) {
                            newState[j] = new Queen(currentState[j]);
                        }
                        newState[currentQueen].row = i;
                        int cost = Math.abs(row - i) * currentState[currentQueen].weight * currentState[currentQueen].weight;
                        // Now, make a node that has the new state in it as a child
                        currentNode.addChild(newState, cost, hCurrent(newState, heuristic), currentQueen);
                    }
                }
            }
        }
        return currentNode;
    }

    public static void main(String[] args) {
        // Start the timer right away
        long startTime = System.nanoTime();
        // Input parameters
        int numQueens = Integer.parseInt(args[0]);
        int searchType = Integer.parseInt(args[1]); // 1 for A*, 2 for greedy
        String heuristic = args[2]; // Either h1 or h2
        int totalNodesExpanded = 0; // Increment every time we compute heuristic values for the board

        // Generate a start state based on the number of queens entered,
        // each queen starts in their own column, and a random row
        // # = queen, * = empty
        Queen[] state = generateStart(numQueens);
        // Save a copy of the start state for later
        Queen[] startState = new Queen[state.length];
        for (int j = 0; j < state.length; j++) {
            startState[j] = new Queen(state[j]);
        }

        // Print the starting configuration for the user
        System.out.println("Starting board state:");
        printBoard(state);

        // Test to show hCurrent is working
        int test = hCurrent(state, heuristic);
        System.out.println(test);

        // Now, make the root node for reference later if you must reset
        Node<Queen[]> root = new Node<Queen[]>(state);
        root.heuristicVal = hCurrent(state, heuristic);

        // Set the current node up to be the root node,
        current = new Node<Queen[]>(state);
        current.heuristicVal = root.heuristicVal;

        // Now, given the current board state, find out the h values of each next move,
        // (expand the state) and pick the best one as the next node
        if (searchType == 1) {
            // TODO
            // Note so far: The PQ successfully orders and retrieves each
            // node by costAccumulated + heuristic, but the problem is that
            // it has no preference for solutions of the same cost to
            // random configurations of the same cost + heuristic value.
            // Perform A* with backtracking if needed
            // First make a priority queue and fill it with all unexpanded nodes that
            // we've found as we go, (at start, just the root node's children)
            PriorityQueue<Node<Queen[]>> nodeQueue = new PriorityQueue<Node<Queen[]>>(new NodeComparator());
            // Add the starting node to the PQ
            //nodeQueue.add(current);
            int moves = 0;
            while (!nodeQueue.isEmpty() || moves == 0) {
                if (isSolution(current.state)) {
                    // TODO
                    // Continue checking if any nodes are left in the pq that
                    // can be better
                    // Print out the info like time, and path from start to end states
                    nodeQueue.clear();
                } else {
                    // Generate current's children, then add them to the queue
                    // Next we expand the current node, (add all possible successors as children based on heuristic)
                    int prevChildren = current.children.size();
                    Node<Queen[]> expanded = hExpand(current, heuristic);
                    if (expanded.children.size() > prevChildren) {
                        totalNodesExpanded++;
                    }
                    for (Node<Queen[]> e: current.children) {
                        nodeQueue.add(e);
                    }
                    Node<Queen[]> test2 = nodeQueue.remove();
                    current = test2;
                    System.out.println("Current board state:");
                    printBoard(current.state);
                    test = hCurrent(current.state, heuristic);
                    System.out.println(test + current.costAccumulated);
                    moves++;
                }
            }
            // Each node will be given a score based on the cost to get to it
            // and the heuristic value of its state
            // Score = heuristic value + cost to get here,
            // choose node with lowest score to expand next

        }

        else { //(searchType == 2)
            // NOTE: h2 appears to need simulated annealing,
            // can also try adding it for h1
            // Perform greedy hill climbing with restarts for 10 seconds or less if solution is found
            int sideWaysMoves = 0; // Reset after a certain # of sideways moves
            int sideWaysMovesLimit = 500; // Adjust as desired
            // NOTE: Has severe problems with n > 9 boards
            int numResets = 0; // Keep track of the number of times you reset
            while(!isSolution(current.state)) {
                // First expand current node, then pick from best children
                // Next we expand the current node, (add all possible successors as children based on heuristic)
                int prevChildren = current.children.size();
                Node<Queen[]> expanded = hExpand(current, heuristic);
                if (expanded.children.size() > prevChildren) {
                    totalNodesExpanded++;
                }
                // Now we look at each of the children of the current state that have been generated and
                // pick one at random to use next based on what has the best heuristic, ignoring cost
                int bestHeuristic = -1;
                ArrayList<Node<Queen[]>> options = new ArrayList<Node<Queen[]>>();
                for (Node<Queen[]> e: current.children) {
                    if (e.heuristicVal < bestHeuristic || bestHeuristic == -1) {
                        bestHeuristic = e.heuristicVal;
                        options = new ArrayList<Node<Queen[]>>();
                        options.add(e);
                    } else if (e.heuristicVal == bestHeuristic) {
                        options.add(e);
                    }
                }
                // What do we do if no children provide improvements or sideways? Reset!
                if (bestHeuristic > current.heuristicVal) {
                    // TODO: Need Sim Annealing to avoid
                    // getting stuck, (if that is allowed)
                    current = root;
                    // Starting from scratch instead of current = root might be better,
                    // but needs testing with a set board state for time:
                    //current = new Node<Queen[]>(state);
                    //current.heuristicVal = hCurrent(state, heuristic);
                    System.out.println("Resetting!");
                    sideWaysMoves = 0;
                    numResets++;
                } else {
                    int pastResets = numResets;
                    // When making a sideways move,
                    // see if it violates the limit for sideways moves or not
                    if (bestHeuristic == current.heuristicVal) {
                        sideWaysMoves++;
                        if (sideWaysMoves > sideWaysMovesLimit) {
                            current = root;
                            //current = new Node<Queen[]>(state);
                            //current.heuristicVal = hCurrent(state, heuristic);
                            System.out.println("Resetting!");
                            sideWaysMoves = 0;
                            numResets++;
                        }
                    } else { // We've made an improvement, so reset the tolerance
                        // for sideways moves
                        sideWaysMoves = 0;
                    }
                    // If we did not just reset from too many sideways moves,
                    // make a move
                    if (pastResets == numResets) {
                        // Now that we have a list of best possible children that are better,
                        // pick one at random for the next looping
                        int choice = options.size();
                        Random rand = new Random();
                        // NOTE: DOES NOT NECESSARILY PICK OPTIONS
                        // THAT IMPROVE IF THEY HAVE SAME H VALUE
                        // AS ONES THAT DONT, (if 4 is attacked at minimum
                        // no matter what, it can pick a move that does not
                        // help immediately even if one exists)
                        choice = rand.nextInt(choice);
                        current = options.get(choice);
                        // Print current board state to see what happens
                        System.out.println("Current board state:");
                        printBoard(current.state);
                        test = hCurrent(current.state, heuristic);
                        System.out.println(test);
                        }
                    }
            }
            long estimatedTime = System.nanoTime() - startTime;
            double timeInSeconds = estimatedTime;
            timeInSeconds = timeInSeconds / 1000000000;
            // A solution is found! Print out some info:
            // Nodes expanded and time are across all restarts,
            // costAccumulated is only for the path that we end up with
            System.out.println("Number of nodes expanded: " + totalNodesExpanded);
            System.out.println("Total Cost: " + current.costAccumulated);
            System.out.println("Resets: " + numResets);
            System.out.println("Time Elapsed: " + timeInSeconds + " seconds");
        }
    }
}