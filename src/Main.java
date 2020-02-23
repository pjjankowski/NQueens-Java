// The N Queens problem, using A* and greedy search

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;

public class Main {

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

    // Prints the path from the root node to a given node
    // Returns the depth of the solution
    public static int pathTo(Node<Queen[]> end) {
        int depth = 0;
        if (isSolution(end.state)) {
            Node<Queen[]> current = end;
            Stack<Node<Queen[]>> moveStack = new Stack<Node<Queen[]>>();
            // Backtrack up to the root
            while (current.parent != null) {
                depth++;
                moveStack.add(current);
                current = current.parent;
            }
            System.out.println("Note: The first row and column are row 0 and column 0 respectively");
            System.out.println("The sequence of moves to the end state is as follows:");
            // Now, print out the moves from root to end
            Queen[] state = current.state;
            while(!moveStack.empty()) {
                Queen[] lastState = state;
                state = moveStack.pop().state;
                for(int i = 0; i < state.length; i++) {
                    if (state[i].row != lastState[i].row) {
                        System.out.println("Moved queen in column " + i + " from row " +
                    lastState[i].row + " to " + state[i].row);
                        i = state.length;
                    }
                }
           }
            System.out.println("The final state in the path is a solution as follows:");
            printBoard(state);
        } else {
            System.out.println("No solution was found in 10s or less.");
        }
        return depth;
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

    // For H1, H2, H3: Returns the value for the heuristic value for a given state
    public static int hCurrent(Queen[] state, String heuristic) {
        if (heuristic.equals("h1")) {
            int lowestWeight = 82;
            int bestIndex = -1;
            // Start weight off as 82 to take the first attacked queen you see
            // If the weight stays as 82, no queens are attacked
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
        } else if (heuristic.equals("h2")) { // H2: ----------------------------------------------------------------------
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
        } else { // H3: Either the lightest attacked queen ^ 2 * distance to nearest
                 // empty row, or if lightest attacked queen has no empty row,
                 // the two lightest attacked queens
            int lowestWeight = 10;
            int bestIndex = -1;
            // Start weight off as 10 to take the first attacked queen you see
            // If the weight stays as 10, no queens are attacked
            boolean[] rowTaken = new boolean[state.length];
            int rowsTaken = 0;
            for (int i = 0; i < state.length; i++) {
                // Note: May want to add randomness to pick queen of equal cost to best or not
                // or pick the most attacked queen out of the lightest
                // If this queen is lighter than previous best, and is attacked, take it as best so far
                int queenVal = state[i].weight;
                if (rowTaken[i] == false) {
                    rowsTaken++;
                }
                rowTaken[state[i].row] = true;
                if (queenVal < lowestWeight) {
                    if (isAttacked(i, state)) {
                        bestIndex = i;
                        lowestWeight = queenVal; // The weight of the lowest cost queen so far
                    }
                }
            } if (bestIndex > -1) {
                // If all rows are taken, just take h1 + 1, since you need
                // at least two moves
                if (rowsTaken == state.length) {
                    return 1 + (lowestWeight * lowestWeight);
                }
                // Otherwise, find the lightest queen with the least cost to get to an empty row
                int leastDistance = state.length + 1;
                // Shortcut: All nodes before bestIndex have a higher weight,
                // no need to check them
                for (int i = bestIndex; i < state.length; i++) {
                    if (state[i].weight == lowestWeight) {
                        for (int j = 0; j < rowTaken.length; j++) {
                            if (!rowTaken[j]) {
                                int distance = Math.abs(state[i].row - j);
                                if (distance < leastDistance) {
                                    leastDistance = distance;
                                    if (leastDistance == 1) {
                                        return lowestWeight * lowestWeight;
                                    }
                                }
                            }
                        }
                    }
                }
                return leastDistance * lowestWeight * lowestWeight;
            } else {
                // There are no moves left to make, you are at a solution already!
                return 0;
            }
        }
    }

    // Expand the current node's state by moving ANY queen, and generate successor states, adding them
    // to the node's list of children, if the current node has not already been expanded yet
    // OPTIMIZATION: MAKE IT SO CHILDREN CANT MOVE THE SAME QUEEN AS THEIR
    // PARENT, AS THOSE MOVES WON'T HELP THINGS AT ALL, (if they are better,
    // then their parent has already expanded a child that has that better option)
    // Want to find ways to restrict expansion and minimize b as much as
    // possible
    public static Node<Queen[]> hExpand(Node<Queen[]> currentNode, String heuristic, long startTime) {
        if (currentNode.children.size() > 0) {
            // This node has already been expanded, so no need to do it again.
            return currentNode;
        }
        Queen[] currentState = currentNode.state;
        int queens = currentState.length;
        // For each queen, make all moves possible to put the queen in a different row,
        // same column
        for (int currentQueen = 0; currentQueen < queens; currentQueen++) {
            double time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
            if (time > 10) {
                return currentNode;
            }
            if (currentQueen != currentNode.lastMoveIndex) {
                int row = currentState[currentQueen].row;
                for (int i = 0; i < queens; i++) {
                    time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                    if (time > 10) {
                        return currentNode;
                    }
                    // Don't consider moves where a queen ends up in the same row, that's not a move!
                    if (i != row) {
                        // Clone the old state to a new state, only changing where the queen is located
                        Queen[] newState = new Queen[currentState.length];
                        for (int j = 0; j < newState.length; j++) {
                            time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                            if (time > 10) {
                                return currentNode;
                            }
                            newState[j] = new Queen(currentState[j]);
                            time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                            if (time > 10) {
                                return currentNode;
                            }
                        }
                        newState[currentQueen].row = i;
                        int cost = Math.abs(row - i) * currentState[currentQueen].weight * currentState[currentQueen].weight;
                        // Now, make a node that has the new state in it as a child
                        currentNode.addChild(newState, cost, hCurrent(newState, heuristic), currentQueen);
                    }
                    time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                    if (time > 10) {
                        return currentNode;
                    }
                }
            }
        }
        return currentNode;
    }

    // Read in a csv file and generate the array for the starting board
    // state from it
    public static Queen[] readQueenFile(String fileName) {

        BufferedReader reader = null;
        String line = ""; // The current line of the file being read
        // First, get the path of the file, (assuming it is in
        // the src folder for this project)
        File filePath = new File("./src//" + fileName).getAbsoluteFile();

        // Use java.io.BufferedReader to read in the file
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Now read in lines from the file until it runs out,
        // adding queens you see to a list
        ArrayList<Queen> queens = new ArrayList<Queen>();
        int linesRead = 0;
        try {
            while ((line = reader.readLine()) != null) {
                // Use comma as separator for csv files
                String[] boardAsString = line.split(",");
                int lineLength = boardAsString.length;
                for (int i = 0; i < lineLength; i++) {
                    try {
                        // See if the current space is a queen
                        // NEED TO HANDLE SPECIAL PARSING FOR THE FIRST CHARACTER IN THE CSV FILE,
                        // SINCE IT HAS AN INVISIBLE CHARACTER AT THE START. IF THIS IS A #,
                        // JAVA WON'T RECOGNIZE IT AS ONE.
                        int last = boardAsString[i].length();
                        if (last > 0) {
                            char characterToTest = new Character(boardAsString[i].charAt(last - 1));
                            String target = "" + characterToTest;
                            queens.add(new Queen(linesRead, i, Integer.parseInt(target)));
                        }
                    } catch (NumberFormatException e) {
                        // This space is not a queen, so ignore it
                        // If (0, 0) is blank, should get here due to the invisible
                        // character starting the csv file
                    }
                }
                linesRead++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Now that you have all of the queens in a list,
        // just make an array where index = column for each queen
        Queen[] board = new Queen[queens.size()];
        for (int i = 0; i < board.length; i++) {
            for (Queen e: queens) {
                if (e.column == i) {
                    board[i] = new Queen(e);
                    break;
                }
            }
        }
        // Print the starting configuration for the user
        //System.out.println("Starting board state:");
        //printBoard(board);
        return board;
    }

    // Greedy hill climbing with simulated annealing
    // Instead of always taking the best move, pick a move at random and
    // take it with a probability
    // This version resets if < 10 seconds, tries to find best solution it can
    public static void simAnnealOpt(int totalNodesExpanded, long startTime, String heuristic, Node<Queen[]> root) {
        // Do not take a better move even if one exists, unless it is immediately
        // a solution
        // Should do resets if too many consecutive rerolls or too low temp
        int timeStep = 1;
        Node<Queen[]> optimalSolution = null;
        double optimalSolutionTime = 0;
        int numResets = 0;
        int optResets = 0; // # of resets before the optimal solution was found
        // Test with starting temp 5, 50, 500, 5000
        double currentTemp = 500;
        double startingTemp = 500;
        int currentRerolls = 0;
        // For rerollLimit, 100 seems better than 1000, which is better than
        // 10 which is better than 1
        int rerollLimit = 100;
        // For geo, annealing constant 0.9 appears better than 0.8
        // but need to test
        double annealingConstant = 0.9;
        // For log, tested with annealing constants 2, 5, 10,
        // and none are better than geometric
        double time = 0;
        while (time < 10) {
            // First check if time has run out
            time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
            if (time > 10) {
                break;
            } else {
                // Expand current node, then pick from best children
                // Next we expand the current node, (add all possible successors as children based on heuristic)
                int prevChildren = current.children.size();
                Node<Queen[]> expanded = hExpand(current, heuristic, startTime);
                time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                if (time > 10) {
                    break;
                }
                if (expanded.children.size() > prevChildren) {
                    totalNodesExpanded++;
                }
                // Now, we pick a successor option at random out of all possible children,
                // UNLESS ONE IS AN IMMEDIATE SOLUTION,
                // (skipping a solution this way would make no sense)
                for (Node<Queen[]> e: current.children) {
                    time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                    if (time > 10) {
                        break;
                    }
                    if (isSolution(e.state)) {
                        current = e;
                        timeStep++;
                        // Geometric version:
                        currentTemp = currentTemp * annealingConstant;
                        // Log version:
                        //currentTemp = currentTemp / (Math.log(timeStep + annealingConstant) / Math.log(annealingConstant));
                        // Found a solution:
                        time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                        int costFound = current.costAccumulated;
                        if (optimalSolution == null || optimalSolution.costAccumulated > costFound) {
                            optResets = numResets;
                            optimalSolutionTime = time;
                            optimalSolution = current;
                        }
                        // Reset due to finding solution
                        numResets++;
                        currentRerolls = 0;
                        timeStep = 1;
                        currentTemp = startingTemp;
                        break;
                    }
                }
                time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                if (time > 10) {
                    break;
                }
                if (isSolution(current.state)) {
                    current = root;
                } else {
                    // Check time passed again
                    time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                    if (time > 10) {
                        break;
                    }
                    // Now that we are sure there are no immediate solutions, pick
                    // a successor at random until one passes the temperature formula
                    Random rand = new Random();
                    int size = current.children.size();
                    int choice = rand.nextInt(size);
                    boolean successorPassed = false;
                    // If successor is immediately better, pick it.
                    // Otherwise, see if it passes the random formula
                    while (!successorPassed && time < 10) {
                        time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                        if (time > 10) {
                            break;
                        }
                        Node<Queen[]> successor = current.children.get(choice);
                        if (successor.heuristicVal >= current.heuristicVal) {
                            time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                            if (time > 10) {
                                break;
                            }
                            currentRerolls = 0;
                            successorPassed = true;
                            current = successor;
                            timeStep++;
                            // Geometric version:
                            currentTemp = currentTemp * annealingConstant;
                            // Log version:
                            //currentTemp = currentTemp / (Math.log(timeStep + annealingConstant) / Math.log(annealingConstant));
                        } else {
                            time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                            if (time > 10) {
                                break;
                            }
                            double power = (successor.heuristicVal - current.heuristicVal) / currentTemp;
                            double probability = Math.pow(Math.E, power);
                            double probToBeat = rand.nextDouble();
                            if (probToBeat <= probability) {
                                currentRerolls = 0;
                                // Keep this one
                                successorPassed = true;
                                current = successor;
                                timeStep++;
                                // Geometric version:
                                currentTemp = currentTemp * annealingConstant;
                                // Log version:
                                //currentTemp = currentTemp / (Math.log(timeStep + annealingConstant) / Math.log(annealingConstant));
                            } else {
                                time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                                if (time > 10) {
                                    break;
                                }
                                currentRerolls++;
                                if (currentRerolls == rerollLimit) {
                                    // Reset due to too many rerolls
                                    numResets++;
                                    currentRerolls = 0;
                                    current = root;
                                    timeStep = 1;
                                    currentTemp = startingTemp;
                                    successorPassed = true;
                                } else {
                                    // Pick another successor
                                    choice = rand.nextInt(size);
                                    //System.out.println("Rerolling " + probToBeat + "  " + probability + "  " + power);
                                }
                            }
                        }
                    }
                    time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                    if (time > 10) {
                        break;
                    }
                }
            }
        }
        // Return the best solution found, if any:
        if (optimalSolution != null) {
            int depth = pathTo(optimalSolution);
            System.out.println("Number of nodes expanded: " + totalNodesExpanded);
            if (depth == 0) {
                System.out.println("Effective branching factor = 0, the start state was a solution.");
            } else {
                double b = Math.pow(totalNodesExpanded, (1/(double)depth));
                System.out.println("Effective branching factor = " + b);
            }
            System.out.println("Best Solution Found At: " + optimalSolutionTime + " seconds");
            System.out.println("Total Cost of Best Solution: " + optimalSolution.costAccumulated);
            System.out.println("Resets: " + numResets);
        } else {
            System.out.println("No solution path found.");
            System.out.println("Number of nodes expanded: " + totalNodesExpanded);
            System.out.println("Effective branching factor = Infinity, no solution path found.");
            System.out.println("Resets: " + numResets);
        }
    }

    // Greedy hill climbing with simulated annealing and a limit on sideways or worsening moves
    // Instead of always taking the best move, pick a move at random and
    // take it with a probability
    // This version resets if < 10 seconds, tries to find best solution it can
    public static void simAnnealOpt2(int totalNodesExpanded, long startTime, String heuristic, Node<Queen[]> root) {
        // Do not take a better move even if one exists, unless it is immediately
        // a solution
        // Should do resets if too many consecutive rerolls or too low temp
        int timeStep = 1;
        int sidewaysLimit = 100;
        int sidewaysMoves = 0;
        Node<Queen[]> optimalSolution = null;
        double optimalSolutionTime = 0;
        int numResets = 0;
        // Test with starting temp 5, 50, 500, 5000
        double currentTemp = 500;
        double startingTemp = 500;
        double currentRerolls = 0;
        double solutionsFound = 0;
        int solutionsTotal = 0;
        // For rerollLimit, 10 seems better than others for this function
        int rerollLimit = 10;
        // For geo, annealing constant 0.9 appears best
        double annealingConstant = 0.9;
        // For log, tested with annealing constants 2, 5, 10,
        // and none are better than geometric
        double time = 0;
        while (time < 10) {
            // First check if time has run out
            time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
            if (time > 10) {
                break;
            } else {
                // Expand current node, then pick from best children
                // Next we expand the current node, (add all possible successors as children based on heuristic)
                int prevChildren = current.children.size();
                Node<Queen[]> expanded = hExpand(current, heuristic, startTime);
                time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                if (time > 10) {
                    break;
                }
                totalNodesExpanded++;
                // Now, we pick a successor option at random out of all possible children,
                // UNLESS ONE IS AN IMMEDIATE SOLUTION,
                // (skipping a solution this way would make no sense)
                for (Node<Queen[]> e: current.children) {
                    time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                    if (time > 10) {
                        break;
                    }
                    if (isSolution(e.state)) {
                        current = e;
                        timeStep++;
                        // Geometric version:
                        currentTemp = currentTemp * annealingConstant;
                        // Log version:
                        //currentTemp = currentTemp / (Math.log(timeStep + annealingConstant) / Math.log(annealingConstant));
                        // Found a solution:
                        time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                        int costFound = current.costAccumulated;
                        solutionsFound++;
                        solutionsTotal += costFound;
                        if (optimalSolution == null || optimalSolution.costAccumulated > costFound) {
                            optimalSolutionTime = time;
                            optimalSolution = current;
                        }
                        // Reset due to finding solution
                        numResets++;
                        currentRerolls = 0;
                        timeStep = 1;
                        currentTemp = startingTemp;
                        break;
                    }
                }
                time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                if (time > 10) {
                    break;
                }
                if (isSolution(current.state)) {
                    current = root;
                } else {
                    // Check time passed again
                    time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                    if (time > 10) {
                        break;
                    }
                    // Now that we are sure there are no immediate solutions, pick
                    // a successor at random until one passes the temperature formula
                    Random rand = new Random();
                    int size = current.children.size();
                    int choice = rand.nextInt(size);
                    boolean successorPassed = false;
                    // If successor is immediately better, pick it.
                    // Otherwise, see if it passes the random formula
                    while (!successorPassed && time < 10) {
                        time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                        if (time > 10) {
                            break;
                        }
                        Node<Queen[]> successor = current.children.get(choice);
                        if (successor.heuristicVal > current.heuristicVal) {
                            time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                            if (time > 10) {
                                break;
                            }
                            sidewaysMoves = 0;
                            currentRerolls = 0;
                            successorPassed = true;
                            current = successor;
                            timeStep++;
                            // Geometric version:
                            currentTemp = currentTemp * annealingConstant;
                            // Log version:
                            //currentTemp = currentTemp / (Math.log(timeStep + annealingConstant) / Math.log(annealingConstant));
                        } else {
                            time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                            if (time > 10) {
                                break;
                            }
                            double power = (successor.heuristicVal - current.heuristicVal) / currentTemp;
                            double probability = Math.pow(Math.E, power);
                            double probToBeat = rand.nextDouble();
                            if (probToBeat <= probability) {
                                sidewaysMoves++;
                                currentRerolls = 0;
                                if (sidewaysMoves == sidewaysLimit) {
                                    // Reset due to reaching limit of sideways moves
                                    sidewaysMoves = 0;
                                    numResets++;
                                    currentRerolls = 0;
                                    current = root;
                                    timeStep = 1;
                                    currentTemp = startingTemp;
                                    successorPassed = true;
                                } else {
                                    // Keep this one
                                    successorPassed = true;
                                    current = successor;
                                    timeStep++;
                                    // Geometric version:
                                    currentTemp = currentTemp * annealingConstant;
                                    // Log version:
                                    //currentTemp = currentTemp / (Math.log(timeStep + annealingConstant) / Math.log(annealingConstant));
                                }
                            } else {
                                time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                                if (time > 10) {
                                    break;
                                }
                                currentRerolls++;
                                if (currentRerolls == rerollLimit) {
                                    // Reset due to too many rerolls
                                    sidewaysMoves = 0;
                                    numResets++;
                                    currentRerolls = 0;
                                    current = root;
                                    timeStep = 1;
                                    currentTemp = startingTemp;
                                    successorPassed = true;
                                } else {
                                    // Pick another successor
                                    choice = rand.nextInt(size);
                                }
                            }
                        }
                    }
                    time = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                    if (time > 10) {
                        break;
                    }
                }
            }
        }
        // Return the best solution found, if any:
        if (optimalSolution != null) {
            int depth = pathTo(optimalSolution);
            System.out.println("Number of nodes expanded: " + totalNodesExpanded);
            if (depth == 0) {
                System.out.println("Effective branching factor = 0, the start state was a solution.");
            } else {
                double b = Math.pow(totalNodesExpanded, (1/(double)depth));
                System.out.println("Effective branching factor = " + b);
            }
            System.out.println("Best Solution Found At: " + optimalSolutionTime + " seconds");
            System.out.println("Total Cost of Best Solution: " + optimalSolution.costAccumulated);
            System.out.println("Resets: " + numResets);
            System.out.println("Average cost of solutions found: " + (solutionsTotal / solutionsFound));
        } else {
            System.out.println("No solution path found.");
            System.out.println("Number of nodes expanded: " + totalNodesExpanded);
            System.out.println("Effective branching factor = Infinity, no solution path found.");
            System.out.println("Resets: " + numResets);
        }
    }

    // Do greedy hill climbing with sideways moves
    // This method performs over the full 10 seconds
    public static void sideWaysOpt(int totalNodesExpanded, long startTime, String heuristic, Node<Queen[]> root) {
        // Perform greedy hill climbing with restarts for 10 seconds or less if solution is found
        int sideWaysMoves = 0; // Reset after a certain # of sideways moves
        int sideWaysMovesLimit = 100; // Adjust as desired
        // NOTE: Has problems with n > 9 boards
        int numResets = 0; // Keep track of the number of times you reset
        double timeInSeconds = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
        Node<Queen[]> optimalSolution = null;
        double optimalSolutionTime = 0;
        double solutionsTotal = 0;
        double solutionsFound = 0;
        while(timeInSeconds <= 10) {
            // See if the current node is a solution
            if (isSolution(current.state)) {
                solutionsFound++;
                solutionsTotal += current.costAccumulated;
                timeInSeconds = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                if (optimalSolution == null ||
                        current.costAccumulated < optimalSolution.costAccumulated) {
                    optimalSolution = current;
                    optimalSolutionTime = timeInSeconds;
                }
                current = root;
                numResets++;
            } else {
                // If not a solution, expand current node, then pick from best children
                // Next we expand the current node, (add all possible successors as children based on heuristic)
                int prevChildren = current.children.size();
                Node<Queen[]> expanded = hExpand(current, heuristic, startTime);
                totalNodesExpanded++;
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
                    current = root;
                    sideWaysMoves = 0;
                    numResets++;
                } else {
                    int pastResets = numResets;
                    // When making a sideways move,
                    // see if it violates the limit for sideways moves or not
                    if (bestHeuristic == current.heuristicVal) {
                        sideWaysMoves++;
                        if (sideWaysMoves > sideWaysMovesLimit) {
                            totalNodesExpanded = 0;
                            current = root;
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
                    choice = rand.nextInt(choice);
                    current = options.get(choice);
                    }
                }
            }
            timeInSeconds = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
        }
        // Print out some info on the best solution found
        if (optimalSolution != null) {
            int depth = pathTo(optimalSolution);
            System.out.println("Number of nodes expanded total: " + totalNodesExpanded);
            if (depth == 0) {
                System.out.println("Effective branching factor = 0, the start state was a solution.");
            } else {
                double b = Math.pow(totalNodesExpanded, (1/(double)depth));
                System.out.println("Effective branching factor = " + b);
            }
            System.out.println("Best Solution Found At: " + optimalSolutionTime + " seconds");
            System.out.println("Total Cost of Best Solution: " + optimalSolution.costAccumulated);
            System.out.println("Resets: " + numResets);
            System.out.println("Average cost of solutions found: " + (solutionsTotal / solutionsFound));
        } else {
            System.out.println("No solution path found.");
            System.out.println("Number of nodes expanded: " + totalNodesExpanded);
            System.out.println("Effective branching factor = Infinity, no solution path found.");
            System.out.println("Resets: " + numResets);
        }
    }

    public static void main(String[] args) {
        // Start the timer right away
        long startTime = System.currentTimeMillis();
        // Input parameters
        String boardFile = args[0];
        Queen state[];
        try {
            int boardSize = Integer.parseInt(boardFile);
            // Generate a start state based on the number of queens entered,
            // each queen starts in their own column, and a random row
            // # = queen, * = empty
            state = generateStart(boardSize);
        } catch (NumberFormatException e) {
            // Board is a file, not a number for size
            state = readQueenFile(boardFile);
        }
        int searchType = Integer.parseInt(args[1]); // 1 for A*, 2 for greedy
        String heuristic = args[2]; // Either h1 or h2
        int totalNodesExpanded = 0; // Increment every time we compute heuristic values for the board

        // Save a copy of the start state for later
        Queen[] startState = new Queen[state.length];
        for (int j = 0; j < state.length; j++) {
            startState[j] = new Queen(state[j]);
        }

        // Test to show hCurrent is working
        //int test = hCurrent(state, heuristic);
        //System.out.println("Starting board heuristic value: " + test);

        // Now, make the root node for reference later if you must reset
        Node<Queen[]> root = new Node<Queen[]>(state);
        root.heuristicVal = hCurrent(state, heuristic);

        // Set the current node up to be the root node,
        current = new Node<Queen[]>(state);
        current.heuristicVal = root.heuristicVal;

        System.out.println("The starting board state is: ");
        printBoard(current.state);

        // Now, given the current board state, find out the h values of each next move,
        // (expand the state) and pick the best one as the next node
        if (searchType == 1) {
            // Note so far: The PQ successfully orders and retrieves each
            // node by costAccumulated + heuristic, but there are too many
            // nodes to deal with efficiently with h1 or h2.
            // Perform A* with backtracking if needed
            // First make a priority queue and fill it with all unexpanded nodes that
            // we've found as we go, (at start, just the root node's children)
            PriorityQueue<Node<Queen[]>> nodeQueue = new PriorityQueue<Node<Queen[]>>(new NodeComparator());
            ArrayList<Queen[]> statesAdded = new ArrayList<Queen[]>();
            // Any solution that wraps back to the starting state should be ignored
            statesAdded.add(current.state);
            // Don't add the starting node to the PQ
            //nodeQueue.add(current);
            int moves = 0;
            while (!nodeQueue.isEmpty() || moves == 0) {
                double timeInSeconds = ((double)System.currentTimeMillis() - (double)startTime) / 1000;
                if (timeInSeconds > 10) {
                    // No Solution found in under 10s
                    System.out.println("Solution Not Found In Under 10s:");
                    int depth = pathTo(current);
                    System.out.println("Number of nodes expanded: " + totalNodesExpanded);
                    if (depth == 0) {
                        System.out.println("Effective branching factor = Infinity, no solution path found.");
                    } else {
                        // Approximate Effective Branching Factor
                        double b = Math.pow(totalNodesExpanded, (1/(double)depth));
                        System.out.println("Effective branching factor = " + b);
                    }
                    System.out.println("Total Cost: " + current.costAccumulated);
                    nodeQueue.clear();
                } else {
                    if (isSolution(current.state)) {
                        // Print out info once the solution is found
                        System.out.println("Solution Found:");
                        int depth = pathTo(current);
                        System.out.println("Number of nodes expanded: " + totalNodesExpanded);
                        if (depth == 0) {
                            System.out.println("Effective branching factor = 0, the start state was a solution.");
                        } else {
                            double b = Math.pow(totalNodesExpanded, (1/(double)depth));
                            System.out.println("Effective branching factor = " + b);
                        }
                        System.out.println("Solution Found at: " + timeInSeconds + " seconds");
                        System.out.println("Total Cost: " + current.costAccumulated);
                        nodeQueue.clear();
                    } else {
                        // Generate current's children, then add them to the queue
                        // Next we expand the current node, (add all possible successors as children based on heuristic)
                        int prevChildren = current.children.size();
                        Node<Queen[]> expanded = hExpand(current, heuristic, startTime);
                        if (expanded.children.size() > prevChildren) {
                            totalNodesExpanded++;
                        }
                        for (Node<Queen[]> e: current.children) {
                            // Add the node to the PQ
                            // if its state has never been in the PQ before,
                            // or if it is better than the node with its state
                            // that is in the PQ
                            // First see if the next child to add has had its state added before
                            boolean found = false;
                            for (Queen[] g : statesAdded) {
                                if (found) {
                                    break;
                                }
                                if (Arrays.deepEquals(e.state, g)) {
                                    found = true;
                                    // This state has been added to the PQ before, so see where/if it is
                                    // currently in the PQ
                                    for (Node<Queen[]> f: nodeQueue) {
                                        if (f.state.equals(e.state)) {
                                            int oldVal = f.costAccumulated + f.heuristicVal;
                                            int newVal = e.costAccumulated + f.heuristicVal;
                                            if (newVal < oldVal) {
                                                nodeQueue.remove(f);
                                                nodeQueue.add(e);
                                                break;
                                            } else {
                                                // You know the state is in the queue,
                                                // but its already better value than
                                                // what you have here
                                                break;
                                            }
                                            }
                                        }
                                    }
                                }
                            if (!found) {
                                statesAdded.add(e.state);
                                nodeQueue.add(e);
                            }
                        }
                        Node<Queen[]> test2 = nodeQueue.remove();
                        current = test2;
                        moves++;
                    }
                }
            }
            // Each node is given a score based on the cost to get to it
            // and the heuristic value of its state
            // Score = heuristic value + cost to get here,
            // choose node with lowest score to expand next
        }
        else { //(searchType == 2)
            // Perform greedy hill climbing with restarts for 10 seconds or less if solution is found
            if (heuristic.equals("h2") && root.state.length > 9) {
                sideWaysOpt(totalNodesExpanded, startTime, heuristic, root);
            } else {
                simAnnealOpt2(totalNodesExpanded, startTime, heuristic, root);
            }
        }
        // Insert test here for comparing same boards using A* and greedy
        root.children = new ArrayList<Node<Queen[]>>();
        simAnnealOpt2(0, System.currentTimeMillis(), heuristic, root);
    }
}