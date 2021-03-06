import java.util.ArrayList;

// Nodes for the search tree
public class Node<T> {

    T state; // The state held by the node
    int costAccumulated; // The cost of all previous nodes combined
    int heuristicVal; // The value of the heuristic. For h1 and h2, 0 = solution, higher is worse.
    Node<T> parent; // The node with the previous state to this one
    ArrayList<Node<T>> children; // A list of all children of the node, (nodes with successor states)
    int lastMoveIndex; // The index of the queen that was moved in the last move before this node

    public Node(T state) { // Build a node from scratch with no parent, (used for root only)
        this.state = state;
        this.parent = null;
        this.costAccumulated = 0; // The root has no cost to get to it
        this.children = new ArrayList<Node<T>>();
        lastMoveIndex = -1;
    }

    public Node<T> addChild(T childState, int cost, int heuristic, int movedQueenIndex) {
        // Add a new child node with the given state and h value to the current node
        Node<T> childNode = new Node<T>(childState);
        childNode.parent = this;
        childNode.heuristicVal = heuristic;
        // Accumulated cost for child = parent cost + cost of move from parent to child
        childNode.costAccumulated = this.costAccumulated + cost;
        childNode.lastMoveIndex = movedQueenIndex;
        this.children.add(childNode);
        return childNode;
    }
}
