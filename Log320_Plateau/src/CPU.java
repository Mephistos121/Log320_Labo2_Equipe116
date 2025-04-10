import java.util.ArrayList;
import java.util.HashMap;


public class CPU {
    Piece cpuMark;
    final int WORSE_SCORE = Integer.MIN_VALUE;
    final int BEST_SCORE = Integer.MAX_VALUE;
    private HashMap<Integer,Double> boardHashForX = new HashMap<>();
    private HashMap<Integer,Double> boardHashForO = new HashMap<>();


    //Basic evaluate points sett here for easy changing
    private static final double OVERALL_WIN_SCORE = 1000000;
    private static final double TWO_SUB_BOARDS_IN_LINE_SCORE = 750;


    // Strategic weight of each sub-board
    private static final double[][] EVALUATOR_BIG_BOARD = {
        {1.4,  1, 1.4 },
        {1, 1.75, 1},
        {1.4,  1, 1.4 }
    };

    // Strategic weight of position in a sub-board (not used here but is the same as the one in sub-board class)
    private static final double[][] SUB_BOARD_POSITION_POINTS = {
            {0.2,  0.17, 0.2 },  // Top-left, Top-mid, Top-right
            {0.17, 0.22, 0.17},  // Mid-left, Center, Mid-right
            {0.2,  0.17, 0.2 }   // Bot-left, Bot-mid, Bot-right
    };

    private static final double SUB_EVAL_BASE_WEIGHT = 1.5; //
    private static final double CURRENT_BOARD_BONUS_WEIGHT = 1.0; // Added weight for the forced board
    private static final double META_BOARD_EVAL_WEIGHT = 150.0; // Weight for evaluating the pattern of won boards
    private static final double SUB_BOARD_WIN_WEIGHT = 350.0; // Points for winning a sub-board (weighted by EVALUATOR_MUL)
    private static final double TWO_IN_ROW_SCORE = 6.0;
    private static final double TWO_IN_DIAG_SCORE = 7.0;
    private static final double FORK_THREAT_SCORE = 9.0;

    public CPU(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }

    public ArrayList<Move> getNextMoveMinMax(int maxDepth, Board board, Move lastMove, Piece playedPiece) {
        ArrayList<Move> moves = new ArrayList<>();
        double max = WORSE_SCORE;
        ArrayList<Move> possibleMoves = board.getValidMoves(lastMove);

        for (Move move : possibleMoves) {
            board.playMove(move, playedPiece== Piece.O ? Piece.X : Piece.O);
            double ret = minMax( cpuMark == Piece.O ? Piece.X : Piece.O, maxDepth-1, board, move,false);
            board.undoMove(move);
            if (ret == max) {
                moves.add(move);
            } else if(ret > max) {
                moves.clear();
                moves.add(move);
                max = ret;
            }
        }
        //System.out.println(max);
        return moves;
    }

    private double minMax(Piece player, int depth, Board board, Move playedMove, Boolean isMaxing) {
        double currentScore = evaluateBoard(board, player, playedMove);
        if(depth <= 0 || board.isDone()!=Piece.EMPTY){
            return currentScore;
        }
        ArrayList<Move> possibleMoves = board.getValidMoves(playedMove);
        if(isMaxing){
            double maxScore = WORSE_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player == Piece.O ? Piece.X : Piece.O);
                double retScore = minMax(player == Piece.O ? Piece.X : Piece.O, depth - 1, board, move,false);
                board.undoMove(move);
                if (retScore >= maxScore) {
                    maxScore = retScore;
                }
            }
            return maxScore;
        }else{
            double maxScore = BEST_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player == Piece.O ? Piece.X : Piece.O);
                double retScore = minMax(player == Piece.O ? Piece.X : Piece.O, depth - 1, board, move,true);
                board.undoMove(move);
                if (retScore <= maxScore) {
                    maxScore = retScore;
                }
                board.undoMove(move);
            }
            return maxScore;

        }
    }


    public ArrayList<Move> getNextMoveMinMaxAlphaBeta(int maxDepth, Board board, Move lastMove) {
        ArrayList<Move> bestMoves = new ArrayList<>();
        double maxScore = WORSE_SCORE; // Start with the worst possible score for the maximizer
        double alpha = WORSE_SCORE;     // Initialize alpha
        double beta = BEST_SCORE;      // Initialize beta
        ArrayList<Move> possibleMoves = board.getValidMoves(lastMove);

        Piece opponentPiece = (cpuMark == Piece.O) ? Piece.X : Piece.O;

        // The top level is always maximizing for us
        for (Move move : possibleMoves) {

            board.playMove(move, cpuMark);
            double score = minMaxAlphaBeta(opponentPiece, maxDepth - 1, board, move, false, alpha, beta);
            board.undoMove(move);

            if (score == maxScore) {
                bestMoves.add(move);
            } else if (score > maxScore) {
                maxScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            }
            //Root setting the alpha score, beta in recursive
            alpha = Math.max(alpha, score);
        }
        return bestMoves;
    }

    private double minMaxAlphaBeta(Piece player, int depth, Board board, Move lastPlayedMove, boolean isMaxing, double alpha, double beta) {

        Piece winner = board.isDone();
        if (depth <= 0 || winner != Piece.EMPTY) {
            return evaluateBoard(board, cpuMark, lastPlayedMove);
        }

        ArrayList<Move> possibleMoves = board.getValidMoves(lastPlayedMove);
        Piece nextPlayer = (player == Piece.O) ? Piece.X : Piece.O;

        if (isMaxing) {
            double maxScore = WORSE_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player);
                double score = minMaxAlphaBeta(nextPlayer, depth - 1, board, move, false, alpha, beta);
                board.undoMove(move);

                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, maxScore);

                if (beta <= alpha) {
                    break;
                }
            }
            return maxScore;

        } else {
            double minScore = BEST_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player);
                double score = minMaxAlphaBeta(nextPlayer, depth - 1, board, move, true, alpha, beta);
                board.undoMove(move);

                minScore = Math.min(minScore, score);
                beta = Math.min(beta, minScore);

                if (beta <= alpha) {
                    break;
                }
            }
            return minScore;
        }
    }

    private double evaluateBoard(Board board, Piece player, Move move) {
        Piece opponent = (player == Piece.X) ? Piece.O : Piece.X;

        // 1. Check for Overall Game Win/Loss/Draw
        Piece overallWinner = board.isDone();
        if (overallWinner == player) {
            return OVERALL_WIN_SCORE;
        }
        if (overallWinner == opponent) {
            return -OVERALL_WIN_SCORE;
        }
        if (overallWinner == Piece.TIE) {
            return 0;
        }

        double currentScore = 0;
        // 2. Evaluate based on won Sub-Boards
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if(player == Piece.X) {
                    currentScore +=  board.getSubBoard(r,c).evaluateFromHash(boardHashForX)*EVALUATOR_BIG_BOARD[r][c];
                }else{
                    currentScore +=  board.getSubBoard(r,c).evaluateFromHash(boardHashForO)*EVALUATOR_BIG_BOARD[r][c];
                }
            }
        }

        return currentScore+board.evaluate(player);
    }


    public Piece getCpuMark() {
        return cpuMark;
    }

    public void setCpuMark(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }

    public void setBoardHashForO(HashMap<Integer,Double> boardHashForO) {
        this.boardHashForO = boardHashForO;
    }

    public void setBoardHashForX(HashMap<Integer,Double> boardHashForX) {
        this.boardHashForX = boardHashForX;
    }
}
