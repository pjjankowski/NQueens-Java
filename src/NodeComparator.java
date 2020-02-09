import java.util.Comparator;

// A comparator class for nodes, needed for A*'s priority queue
public class NodeComparator implements Comparator<Node<Queen[]>> {

    // Return 0 if heuristic and cost total for arg0 is equal to arg1, 1 if greater, -1 if less
    @Override
    public int compare(Node<Queen[]> arg0, Node<Queen[]> arg1) {
        int heurCompare = (arg0.heuristicVal + arg0.costAccumulated)
                - (arg1.heuristicVal + arg1.costAccumulated);
        if (heurCompare > 0) {
            return 1;
        } else if (heurCompare == 0) {
            return 0;
        } else {
            return -1;
        }
    }

}
