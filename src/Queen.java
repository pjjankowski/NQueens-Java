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
    	// check row and col
    	if(usurper.row == this.row || usurper.column == this.column) {
    		return true;
    	}    	
    	// check diagonal
    	if(usurper.row - this.row == usurper.column - this.column) {
    		return true;
    	}
    	// else
    	return false;
    }

    //test 3
    
    public Queen(Queen q) {
        this.row = q.row;
        this.column = q.column;
        this.weight = q.weight;
    }
}
