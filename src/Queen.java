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

    public Queen(Queen q) {
        this.row = q.row;
        this.column = q.column;
        this.weight = q.weight;
    }
}
