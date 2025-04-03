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
    public void playMove(Move move, Piece piece) {
        SubBoard subBoard = getSubBoardFromMove(move);
        subBoard.play(move,piece);
    }
    public void undoMove(Move move) {
        SubBoard subBoard = getSubBoardFromMove(move);
        subBoard.play(move, Piece.EMPTY);
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
            //See where we are forced to play
            SubBoard subBoardForced = getSubBoard(lastMove.getRow()%3,lastMove.getCol()%3);

            //If we are sent to a sub Board that is done, we can play anywhere
            if(subBoardForced.isDone()){
                for (int i = 0; i < boards.length ; i++) {
                    for (int j = 0; j < boards[i].length ; j++) {
                        if(!boards[i][j].isDone()){
                            validMoves.addAll(getSubBoard(i,j).getAllPossibleMoves());
                        }
                    }
                }
            }else{
                //We need to play on that specific board instead.
                validMoves.addAll(subBoardForced.getAllPossibleMoves());
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

    public void displayBoard() {
        final String COLOR_DEFAULT = "\u001B[0m";
        final String COLOR_RED = "\u001B[31m";
        final String COLOR_BLUE = "\u001B[34m";
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-");

        int offSetX;
        int offSetY = 0;
        for(int i = 0; i < 9; i++) {
            offSetX = 0;
            if(i != 0 && i%3 == 0) {
                System.out.println("\n---------┼---------┼---------");
                offSetY++;
            }
            else
                System.out.println();
            for(int j = 0; j < 9; j++) {
                if (j != 0 && j % 3 == 0) {
                    System.out.print("|");
                    offSetX++;
                }
                //System.out.println(offSetX+", "+offSetY);
                Piece p = getSubBoard(i/3,j/3).getValueAt(i%3, j%3);
                if(p == Piece.X)
                    System.out.print(COLOR_RED);
                else if(p == Piece.O)
                    System.out.print(COLOR_BLUE);
                System.out.print(" "+ (p==Piece.EMPTY?" ":p)+" ");
                System.out.print(COLOR_DEFAULT);
            }
        }
    }

    public Piece isDone() {
        SubBoard wins = new SubBoard(1, 1);
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(boards[i][j].isDone())
                    wins.play(new Move(i, j), boards[i][j].getWinner());
                else if(boards[i][j].checkIfBoardFull())
                    wins.play(new Move(i, j), Piece.TIE);
                else
                    wins.play(new Move(i, j), Piece.EMPTY);
            }
        }
        return wins.getWinner();
    }

    public Board clone() {
        return new Board(this.boards.clone());
    }
}
