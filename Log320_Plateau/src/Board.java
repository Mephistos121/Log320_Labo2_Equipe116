public class Board {
    private SubBoard[][] board;

    public Board(SubBoard[][] board) {
        this.board = board;
    }

    public SubBoard[][] getBoard() {
        return board;
    }

    public void setBoard(SubBoard[][] board) {
        this.board = board;
    }
}

class SubBoard {
    private Piece[][] board;

    // Ne pas changer la signature de cette méthode

    public SubBoard() {

        //initialise the board with full of empty pieces.
        board = new Piece[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Piece.EMPTY;
            }
        }
    }


    // Plays a move and returns the position of the move to know where to play next
    //TODO add the return statement so we know which subBoard must be played in for next move.
    //TODO Add error handeling for illegal move

    public void play(Move m, Piece piece){
        if(checkLegalMove(m, piece)) {
            board[m.getRow()][m.getCol()] = piece;
        }else{
            System.out.println("Invalid move");
        }
    }

    // retourne  100 pour une victoire
    //          -100 pour une défaite
    //           0   pour un match nul
    public int evaluate(Piece piece){
        //check to be sure you don't ask for an empty player
        if (piece == Piece.EMPTY){
            return 0;
        }

        Piece enemy = piece.equals(Piece.O) ? Piece.X : Piece.O;

        if(checkColumns(piece) || checkRows(piece) || checkDiagonal(piece)){
            return 100;
        }

        if(checkColumns(enemy) || checkRows(enemy) || checkDiagonal(enemy)){
            return -100;
        }
        return 0;
    }

    //Checks the rows to know if a player has won
    private boolean checkRows(Piece piece){
        for(int i = 0; i < board.length; i++){
            if(board[i][0].equals(piece) && board[i][1].equals(piece) && board[i][2].equals(piece)){
                return true;
            }
        }
        return false;
    }

    //Checks the columns to know if a player has won
    private boolean checkColumns(Piece piece){
        for(int i = 0; i < board[0].length; i++){
            if(board[0][i].equals(piece) && board[1][i].equals(piece) && board[2][i].equals(piece)){
                return true;
            }
        }
        return false;
    }

    //Checks the diagonals to know if a player has won
    private boolean checkDiagonal(Piece piece){
        if(board[0][0].equals(piece) && board[1][1].equals(piece) && board[2][2].equals(piece)){
            return true;
        }
        if(board[0][2].equals(piece) && board[1][1].equals(piece) && board[2][0].equals(piece)){
            return true;
        }
        return false;
    }

    //Check if the board is full, needed to know if a board is a tie
    public boolean checkIfBoardFull(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j].equals(Piece.EMPTY)){
                    return false;
                }
            }
        }
        return true;
    }

    //TODO: add error handling
    //TODO: Chek if this is the write board
    private boolean checkLegalMove(Move m, Piece piece){

        //check out of bounds
        if(m.getCol() > 2 || m.getRow() > 2 || m.getCol() < 0 || m.getRow() <0){
            return false;
        }

        //check if the position is empty
        if(board[m.getRow()][m.getCol()] != Piece.EMPTY){
            return false;
        }
        return true;
    }

    public void setValueAt(int row, int column, Piece piece){
        board[row][column] = piece;
    }

    public Piece getValueAt(int row, int column){
        return board[row][column];
    }

    //Prints the subBoard so we can see whats going on
    public void printBoard(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
