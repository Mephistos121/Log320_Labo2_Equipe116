import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BoardStateHash {
    private  Map<BoardState, Double> cache;

    public BoardStateHash() {
        this.cache = new HashMap<>();
    }

    public void addBoardValue(Piece[][] board, Double value) { // Need value param
        if (board == null || value == null) {
            throw new IllegalArgumentException("Board or value cannot be null.");
        }
        BoardState key = new BoardState(board);
        cache.put(key, value);
    }
   
    public Double getValueForBoard(Piece[][] board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }
        BoardState key = new BoardState(board); // Create the proper key
        return cache.get(key); // Look up using BoardState key
    }
    
    public boolean containsBoard(Piece[][] board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }
        BoardState key = new BoardState(board);
        return cache.containsKey(key);
    }

    public int hashCode(Piece[][] board) {
        return Arrays.deepHashCode(board);
    }

    public int getNumberOfStoredBoards() {
        return cache.size();
    }
}
