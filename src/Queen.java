// A class for storing info on queens, making the arrays easier to handle/use
public class Queen {
    int column;
    int row;
    int weight;

    public Queen(int row, int column, int weight) {
        this.row = row;
        this.column = column;
        this.weight = weight;
    }

    public boolean isAttackedBy(Queen usurper) {
    	if(usurper.equals(this)) { // stop hitting yourself!
    		return false;
    	}
    	// check row, no need for column since all cols have 1 queen only
    	if(usurper.row == this.row) {
    		return true;
    	}
    	// check diagonal
    	if(Math.abs(usurper.row - this.row) == Math.abs(usurper.column - this.column)) {
    		return true;
    	}
    	// else
    	return false;
    }

    public Queen(Queen q) {
        this.row = q.row;
        this.column = q.column;
        this.weight = q.weight;
    }
}
