import java.util.ArrayList;

// Nodes for the search tree
    public class Node<T> {

        T state; // The state held by the node
        int costAccumulated; // The cost of all previous nodes combined
        Node<T> parent; // The node with the previous state to this one
        ArrayList<Node<T>> children; // A list of all children of the node, (nodes with successor states)

        public Node(T state) { // Build a node from scratch with no parent, (used for root only)
            this.state = state;
            costAccumulated = 0; // The root has no cost to get to it
            this.children = new ArrayList<Node<T>>();
        }

        public Node<T> addChild(T childState, int cost) { // Add a new child node with the given state to the current node
            Node<T> childNode = new Node<T>(childState);
            childNode.parent = this;
            // Accumulated cost for child = parent cost + cost of move from parent to child
            childNode.costAccumulated = this.costAccumulated + cost;
            this.children.add(childNode);
            return childNode;
        }
    }
