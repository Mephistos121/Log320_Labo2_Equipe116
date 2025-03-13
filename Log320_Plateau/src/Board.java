import java.util.ArrayList;

public class Board {
    private SubBoard[][] boards;

    public Board(SubBoard[][] boards) {
        this.boards = boards;
    }

    public SubBoard[][] getBoards() {
        return boards;
    }

    public void setBoards(SubBoard[][] boards) {
        this.boards = boards;
    }
    public void setSubBoards(int row, int col,SubBoard subBoard) {
        this.boards[row][col] = subBoard;
    }
    public SubBoard getSubBoard(int row, int col) {
        return boards[row][col];
    }
    private SubBoard getSubBoardFromMove(Move move) {
        return boards[move.getRow()/3][move.getCol()/3];
    }

    public Piece getPiece(int row, int col) {
        return boards[row/3][col/3].getValueAt(row%3, col%3);
    }

    public ArrayList<Move> getValidMoves(Move lastMove) {
        ArrayList<Move> validMoves = new ArrayList<>();

        // If there were no previous moves, that means we are first and play center.
        if(lastMove != null) {

            //If we are sent to a sub Board that is done, we can play anywhere
            if(getSubBoardFromMove(lastMove).isDone()){
                validMoves.addAll(boards[lastMove.getRow()/3][lastMove.getRow()/3].getAllPossibleMoves());
            }else{
                //We need to play on that specific board instead.
                validMoves.addAll(boards[lastMove.getRow()/3][lastMove.getRow()/3].getAllPossibleMoves());
            }

        }else{
            //The only moment there should be no last move is when the board is empty and we assume middle-middle is best
            validMoves.add(new Move(4,4));
        }

        return validMoves;
    }

    //We get all the valid moves everywhere on the Board
    private ArrayList<Move> getAllValidMoves(SubBoard subBoard) {
        ArrayList<Move> validMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                validMoves.addAll(subBoard.getAllPossibleMoves());
            }
        }
        return validMoves;
    }
}
