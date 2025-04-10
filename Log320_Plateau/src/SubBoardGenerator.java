import java.util.ArrayList;
import java.util.List;
// Assumes Piece enum and BoardState class are defined

public class SubBoardGenerator {

    private static final Piece[] POSSIBLE_CELL_STATES = {Piece.EMPTY, Piece.X, Piece.O};
    private final int boardSize = 3;

    public List<Piece[][]> generateAllPossibleBoardStates() {
        List<Piece[][]> allBoards = new ArrayList<>();
        Piece[][] currentBoard = new Piece[boardSize][boardSize];
        recursiveGenerate(0, 0, currentBoard, allBoards);
        return allBoards;
    }

    private void recursiveGenerate(int row, int col, Piece[][] board, List<Piece[][]> results) {

        if (row == boardSize) {
            Piece[][] deepCopiedBoard = new Piece[boardSize][boardSize];
            for (int i = 0; i < boardSize; i++) {
                System.arraycopy(board[i], 0, deepCopiedBoard[i], 0, boardSize);
            }
            results.add(deepCopiedBoard);
            return;
        }

        int nextRow = row;
        int nextCol = col + 1;
        if (nextCol == boardSize) {
            nextRow = row + 1;
            nextCol = 0;
        }

        for (Piece state : POSSIBLE_CELL_STATES) {
            board[row][col] = state;
            recursiveGenerate(nextRow, nextCol, board, results);
        }
    }
}