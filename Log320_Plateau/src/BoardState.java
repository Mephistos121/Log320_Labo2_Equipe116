import java.util.Arrays;

public final class BoardState { // 'final' prevents subclassing issues with equals/hashCode

    private final Piece[][] board; // Internal state - defensive copy is stored here

    public BoardState(Piece[][] boardData) {
        if (boardData == null) {
            throw new IllegalArgumentException("null board given");
        }
        this.board = deepCopy(boardData);
    }

    public Piece[][] getBoardCopy() {
        // Return a copy to maintain encapsulation and immutability of the key state
        return deepCopy(this.board);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardState that = (BoardState) o;
        return Arrays.deepEquals(this.board, that.board);
    }


    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.board);
    }


    private Piece[][] deepCopy(Piece[][] original) {
        final Piece[][] result = new Piece[original.length][];
        for (int i = 0; i < original.length; i++) {
            if (original[i] == null) {
                result[i] = null;
            } else {
                result[i] = Arrays.copyOf(original[i], original[i].length);
            }
        }
        return result;
    }
}