import java.util.ArrayList;
import java.util.Random;

public class CPU {
    Piece cpuMark;
    final int WORSE_SCORE = Integer.MIN_VALUE;
    final int BEST_SCORE = Integer.MAX_VALUE;

    //Basic evaluate points sett here for easy changing
    private static final int OVERALL_WIN_SCORE = 100000;
    private static final int SUB_BOARD_WIN_SCORE = 100;
    private static final int TWO_SUB_BOARDS_IN_LINE_SCORE = 50;

    public CPU(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }

    public ArrayList<Move> getNextMoveMinMax(int maxDepth, Board board, Move lastMove, Piece playedPiece) {
        ArrayList<Move> moves = new ArrayList<>();
        int max = WORSE_SCORE;
        ArrayList<Move> possibleMoves = board.getValidMoves(lastMove);

        for (Move move : possibleMoves) {
            board.playMove(move, playedPiece== Piece.O ? Piece.X : Piece.O);
            int ret = minMax( cpuMark == Piece.O ? Piece.X : Piece.O, maxDepth-1, board, move,false);
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

    private int minMax(Piece player, int depth, Board board, Move playedMove, Boolean isMaxing) {
        int currentScore = evaluateBoard(board, player, playedMove);
        if(depth <= 0 || board.isDone()!=Piece.EMPTY){
            return currentScore;
        }
        ArrayList<Move> possibleMoves = board.getValidMoves(playedMove);
        if(isMaxing){
            int maxScore = WORSE_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player == Piece.O ? Piece.X : Piece.O);
                int retScore = minMax(player == Piece.O ? Piece.X : Piece.O, depth - 1, board, move,false);
                board.undoMove(move);
                if (retScore >= maxScore) {
                    maxScore = retScore;
                }
            }
            return maxScore;
        }else{
            int maxScore = BEST_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player == Piece.O ? Piece.X : Piece.O);
                int retScore = minMax(player == Piece.O ? Piece.X : Piece.O, depth - 1, board, move,true);
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
        int maxScore = WORSE_SCORE; // Start with the worst possible score for the maximizer
        int alpha = WORSE_SCORE;     // Initialize alpha
        int beta = BEST_SCORE;      // Initialize beta
        ArrayList<Move> possibleMoves = board.getValidMoves(lastMove);

        Piece opponentPiece = (cpuMark == Piece.O) ? Piece.X : Piece.O;

        // The top level is always maximizing for us
        for (Move move : possibleMoves) {

            board.playMove(move, cpuMark);
            int score = minMaxAlphaBeta(opponentPiece, maxDepth - 1, board, move, false, alpha, beta);
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

    private int minMaxAlphaBeta(Piece player, int depth, Board board, Move lastPlayedMove, boolean isMaxing, int alpha, int beta) {

        Piece winner = board.isDone();
        if (depth <= 0 || winner != Piece.EMPTY) {
            return evaluateBoard(board, cpuMark, lastPlayedMove);
        }

        ArrayList<Move> possibleMoves = board.getValidMoves(lastPlayedMove);
        Piece nextPlayer = (player == Piece.O) ? Piece.X : Piece.O;

        if (isMaxing) {
            int maxScore = WORSE_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player);
                int score = minMaxAlphaBeta(nextPlayer, depth - 1, board, move, false, alpha, beta);
                board.undoMove(move);

                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, maxScore);

                if (beta <= alpha) {
                    break;
                }
            }
            return maxScore;

        } else {
            int minScore = BEST_SCORE;
            for (Move move : possibleMoves) {
                board.playMove(move, player);
                int score = minMaxAlphaBeta(nextPlayer, depth - 1, board, move, true, alpha, beta);
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

    private int evaluateBoard(Board board, Piece player, Move move) {
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

        // --- Game is ongoing ---

        int currentScore = 0;

        // 2. Evaluate based on won Sub-Boards
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Piece subWinner = board.getSubBoard(r,c).getWinner();
                if (subWinner == player) {
                    currentScore += SUB_BOARD_WIN_SCORE;
                } else if (subWinner == opponent) {
                    currentScore -= SUB_BOARD_WIN_SCORE;
                }
            }
        }

        if(board.getSubBoard(1,1).getValueAt(1,1).equals(player)){
            currentScore += 50;
        } else{
            currentScore -= 50;
        }

        return currentScore;
    }

    public Piece getCpuMark() {
        return cpuMark;
    }

    public void setCpuMark(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }
}
