import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BoardStateHash {

    public static final int BOARD_SIZE = 9;
    public static final int CELL_STATES = 3; // Number of states per cell
    public static final int EMPTY = 0;
    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = 2;

    // Calculate the total number of possible configurations (3^9)
    public static final int TOTAL_CONFIGURATIONS = 19683;

    public int boardToIntId(Piece[][] board) {

        int id = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int cellValue = pieceToIntId(board[i][j]);
                id = id * CELL_STATES + cellValue;
            }
        }
        return id;
    }

    public int pieceToIntId(Piece piece) {
        int value =0;
        switch (piece) {
            case O -> value = 2;
            case X -> value = 1;
            case EMPTY -> value = 0;
        }
        return value ;
    }

}
