import java.util.ArrayList;
import java.util.HashMap;

public class SubBoard {
    private Piece[][] subBoard;
    private boolean done;
    private Piece winner;
    private int positionRow;
    private int positionCol;

    //Section made for scoring;
    private static final double SUB_BOARD_WIN_SCORE = 350.0;
    private static final double TWO_IN_ROW_SCORE = 6.0;
    private static final double TWO_IN_DIAG_SCORE = 7.0;
    private static final double FORK_SCORE = 7.0;
    // Strategic weight of position in a sub-board
    private static final double[][] SUB_BOARD_POSITION_POINTS = {
            {0.3,  0.22, 0.3 },  // Row 0: Top-left, Top-mid, Top-right
            {0.22, 0.40, 0.22},  // Row 1: Mid-left, Center, Mid-right
            {0.3,  0.22, 0.3 }   // Row 2: Bot-left, Bot-mid, Bot-right
    };

    public SubBoard(int row, int col) {
        this.positionRow = row*3;
        this.positionCol = col*3;

        this.done = false;
        this.winner = Piece.EMPTY;
        //initialise the board with full of empty pieces.
        subBoard = new Piece[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                subBoard[i][j] = Piece.EMPTY;
            }
        }
    }

    // Plays a move and checks if the board is finished
    public void play(Move m, Piece piece){
        subBoard[m.getRow()%3][m.getCol()%3] = piece;
        updateCheckIfDone();
    }

    //-------------------- START OF EVALUATION ----------------------------------------
    /**
     * Section that scores points for one of the players
     * @param player is the player we want to evaluate the sub-board for
     * @return we return the value of the sub-board
     */
    public double evaluate(Piece player){
        double evaluation = 0;
        Piece enemy = player.equals(Piece.O) ? Piece.X : Piece.O;
        //check to be sure you don't ask for an empty player
        if (player == Piece.EMPTY || player == Piece.TIE) {
            return 0;
        }

        //if the subBoard is done, we return the score relative to the player.
        if(done){
            evaluation = SUB_BOARD_WIN_SCORE;
            if (enemy == winner) {
                evaluation *= -1;
            }
            return evaluation;
        }

        //now we check the sub-board to see the value of each square
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int scoreValue = getScoreValuePosition(subBoard[r][c], player);
                evaluation += scoreValue * SUB_BOARD_POSITION_POINTS[r][c];
            }
        }

        //all checks for threat
        //We only need to check if a position on the board is threatening if its empty

        //Top left
        if(subBoard[0][0] == Piece.EMPTY){
            if(subBoard[0][1] == player && subBoard[0][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][1] == enemy && subBoard[0][2] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][0] == player && subBoard[2][0] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][0] == enemy && subBoard[2][0] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][1] == player && subBoard[2][2] == player){
                evaluation += TWO_IN_DIAG_SCORE;
            }
            if(subBoard[1][1] == enemy && subBoard[2][2] == enemy){
                evaluation -= TWO_IN_DIAG_SCORE;
            }
        }

        //Top Middle
        if(subBoard[0][1] == Piece.EMPTY){
            if(subBoard[0][0] == player && subBoard[0][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][0] == enemy && subBoard[0][2] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][1] == player && subBoard[2][1] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][1] == enemy && subBoard[2][1] == enemy){
                evaluation += TWO_IN_ROW_SCORE;
            }
        }

        //Top Right
        if(subBoard[0][2] == Piece.EMPTY){
            if(subBoard[0][0] == player && subBoard[0][1] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][0] == enemy && subBoard[0][1] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[2][1] == player && subBoard[2][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[2][1] == enemy && subBoard[2][2] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][1] == player && subBoard[2][0] == player){
                evaluation += TWO_IN_DIAG_SCORE;
            }
            if(subBoard[1][1] == enemy && subBoard[2][0] == enemy){
                evaluation -= TWO_IN_DIAG_SCORE;
            }
        }

        //Middle Left
        if(subBoard[1][0] == Piece.EMPTY){
            if(subBoard[0][0] == player && subBoard[2][0] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][0] == enemy && subBoard[2][0] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][1] == player && subBoard[1][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][1] == enemy && subBoard[1][2] == enemy){
                evaluation += TWO_IN_ROW_SCORE;
            }
        }

        //Middle
        if(subBoard[1][1] == Piece.EMPTY){
            if(subBoard[0][0] == player && subBoard[2][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][0] == enemy && subBoard[2][2] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][2] == player && subBoard[2][0] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][2] == enemy && subBoard[2][0] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][1] == player && subBoard[2][1] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][1] == enemy && subBoard[2][1] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][0] == player && subBoard[1][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][0] == enemy && subBoard[1][2] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
        }

        //Middle Right
        if(subBoard[1][2] == Piece.EMPTY){
            if(subBoard[0][2] == player && subBoard[2][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][2] == enemy && subBoard[2][2] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][0] == player && subBoard[1][1] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][0] == enemy && subBoard[1][1] == enemy){
                evaluation += TWO_IN_ROW_SCORE;
            }
        }

        //Bottom Left
        if(subBoard[2][0] == Piece.EMPTY){
            if(subBoard[0][0] == player && subBoard[1][0] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][0] == enemy && subBoard[1][0] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[2][1] == player && subBoard[2][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[2][1] == enemy && subBoard[2][2] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][1] == player && subBoard[2][2] == player){
                evaluation += TWO_IN_DIAG_SCORE;
            }
            if(subBoard[1][1] == enemy && subBoard[2][2] == enemy){
                evaluation -= TWO_IN_DIAG_SCORE;
            }
        }

        //Bottom Middle
        if(subBoard[2][1] == Piece.EMPTY){
            if(subBoard[0][1] == player && subBoard[1][1] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][1] == enemy && subBoard[1][1] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[2][0] == player && subBoard[2][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[2][0] == enemy && subBoard[2][2] == enemy){
                evaluation += TWO_IN_ROW_SCORE;
            }
        }

        //Bottom Right
        if(subBoard[2][2] == Piece.EMPTY){
            if(subBoard[2][0] == player && subBoard[2][1] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[2][0] == enemy && subBoard[2][1] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][2] == player && subBoard[1][2] == player){
                evaluation += TWO_IN_ROW_SCORE;
            }
            if(subBoard[0][2] == enemy && subBoard[1][2] == enemy){
                evaluation -= TWO_IN_ROW_SCORE;
            }
            if(subBoard[1][1] == player && subBoard[0][0] == player){
                evaluation += TWO_IN_DIAG_SCORE;
            }
            if(subBoard[1][1] == enemy && subBoard[0][0] == enemy){
                evaluation -= TWO_IN_DIAG_SCORE;
            }
        }

        //We Return the value of the evaluate function
        return evaluation;
    }

    private int getScoreValuePosition(Piece player, Piece square) {
        if (player == square) {
            return 1;
        } else if (square == Piece.EMPTY || square == Piece.TIE) {
            return 0;
        } else { // Opponent's piece so we multiply by -1
            return -1;
        }
    }

    public double evaluateFromHash(HashMap<Integer,Double> hash) {
        return hash.get(boardToIntId(subBoard));
    }

    //-------------------- END OF EVALUATION SECTION ----------------------------------------

    //Checks the rows to know if a player has won
    private boolean checkRows(Piece piece){
        for(int i = 0; i < subBoard.length; i++){
            if(subBoard[i][0].equals(piece) && subBoard[i][1].equals(piece) && subBoard[i][2].equals(piece)){
                return true;
            }
        }
        return false;
    }

    //Checks the columns to know if a player has won
    private boolean checkColumns(Piece piece){
        for(int i = 0; i < subBoard[0].length; i++){
            if(subBoard[0][i].equals(piece) && subBoard[1][i].equals(piece) && subBoard[2][i].equals(piece)){
                return true;
            }
        }
        return false;
    }

    //Checks the diagonals to know if a player has won
    private boolean checkDiagonal(Piece piece){
        if(subBoard[0][0].equals(piece) && subBoard[1][1].equals(piece) && subBoard[2][2].equals(piece)){
            return true;
        }
        if(subBoard[0][2].equals(piece) && subBoard[1][1].equals(piece) && subBoard[2][0].equals(piece)){
            return true;
        }
        return false;
    }

    //Check if the board is full, needed to know if a board is a tie
    public boolean checkIfBoardFull(){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(subBoard[i][j].equals(Piece.EMPTY)){
                    return false;
                }
            }
        }
        return true;
    }

    private void updateCheckIfDone() {

        if (checkRows(Piece.X) || checkColumns(Piece.X) || checkDiagonal(Piece.X)) {
            this.done = true;
            this.winner = Piece.X;
            return;
        }
        if (checkRows(Piece.O) || checkColumns(Piece.O) || checkDiagonal(Piece.O)) {
            this.done = true;
            this.winner = Piece.O;
            return;
        }

        if (checkIfBoardFull()) {
            this.done = true;
            this.winner = Piece.TIE;
            return;
        }

        this.done = false;
        this.winner = Piece.EMPTY;
    }

    //Prints the subBoard so we can see whats going on
    public void printSubBoard(){
        for(int i = 0; i < subBoard.length; i++){
            for(int j = 0; j < subBoard[0].length; j++){
                System.out.print(subBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

    public ArrayList<Move> getAllPossibleMoves(){
        ArrayList<Move> possibleMoves = new ArrayList<Move>();
        for(int i = 0; i < subBoard.length; i++){
            for(int j = 0; j < subBoard[0].length; j++){
                if(subBoard[i][j] == Piece.EMPTY){
                    possibleMoves.add(new Move(i+positionRow,j+positionCol));
                }
            }
        }
        return possibleMoves;
    }

    public Piece[][] getSubBoard() {return subBoard;}

    public void setSubBoard(Piece[][] subBoard) {
        this.subBoard = subBoard;
        updateCheckIfDone();
    }

    public Piece getWinner() {
        return winner;
    }

    public void setWinner(Piece winner) {
        this.winner = winner;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setValueAt(int row, int column, Piece piece){
        subBoard[row][column] = piece;
    }

    public Piece getValueAt(int row, int column){
        return subBoard[row][column];
    }

    private int boardToIntId(Piece[][] board) {
        int id = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int cellValue = pieceToIntId(board[i][j]);
                id = id * 3 + cellValue;
            }
        }
        return id;
    }

    private int pieceToIntId(Piece piece) {
        int value =0;
        switch (piece) {
            case O -> value = 2;
            case X -> value = 1;
            case EMPTY -> value = 0;
            case TIE -> value = 0;
        }
        return value ;
    }
}
