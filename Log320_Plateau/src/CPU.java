import java.util.ArrayList;
import java.util.Random;

public class CPU {
    Piece cpuMark;
    final int WORSE_SCORE = Integer.MIN_VALUE;
    final int BEST_SCORE = Integer.MAX_VALUE;

    public CPU(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }

    public ArrayList<Move> getNextMoveMinMax(int maxDepth, Board board, Move lastMove, Piece playedPiece) {
        ArrayList<Move> moves = new ArrayList<>();
        int max = WORSE_SCORE;
        ArrayList<Move> possibleMoves = board.getValidMoves(lastMove);

        for (Move move : possibleMoves) {
            board.playMove(move, playedPiece== Piece.O ? Piece.X : Piece.O);
            int ret = minMax( cpuMark == Piece.O ? Piece.X : Piece.O, maxDepth-1, board, move);
            if (ret == max) {
                moves.add(move);
            } else if(ret > max) {
                moves.clear();
                moves.add(move);
                max = ret;
            }
            board.undoMove(move);
        }
        //System.out.println(max);
        return moves;
    }

    private int minMax(Piece player, int depth, Board board, Move playedMove) {
        int currentScore = evaluateBoard(board, player);
        if(depth <= 0){
            return currentScore;
        }
        int max = player==cpuMark?WORSE_SCORE : BEST_SCORE;

        ArrayList<Move> possibleMoves = board.getValidMoves(playedMove);
        for (Move move : possibleMoves) {
            board.playMove(move, player == Piece.O ? Piece.X : Piece.O);
            int ret = minMax(cpuMark == Piece.O ? Piece.X : Piece.O, depth - 1, board, move);
            if (ret >= max) {
                max = ret;
            }
            board.undoMove(move);
        }
        return max;
    }


    public ArrayList<Move> getNextMoveAB(SubBoard subBoard, Board board) {
        int alpha = WORSE_SCORE;
        int beta = BEST_SCORE;

        int bestMoveValue = WORSE_SCORE;
        ArrayList<Move> bestMoves = new ArrayList<>();

        Piece enemy = (cpuMark.equals(Piece.X)) ? Piece.O : Piece.X;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (subBoard.getValueAt(i, j).equals(Piece.EMPTY)) {
                    subBoard.setValueAt(i, j, cpuMark);
                    int moveValue = alphaBeta(enemy, subBoard, alpha, beta, board);
                    subBoard.setValueAt(i, j, Piece.EMPTY);

                    bestMoveValue = getBestMoveValue(bestMoveValue, bestMoves, i, j, moveValue);
                }
            }
        }
        return bestMoves;
    }

    //La partie récursive de alphaBeta
    private int alphaBeta(Piece player, SubBoard subBoard, int alpha, int beta, Board board) {
        int currentScore = subBoard.evaluate(cpuMark);
        if (currentScore != 0 || subBoard.checkIfBoardFull()) {
            return currentScore;
        }

        boolean maxing = player == cpuMark;
        int bestScore = maxing ? WORSE_SCORE : BEST_SCORE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (subBoard.getValueAt(i, j) == Piece.EMPTY) {
                    subBoard.setValueAt(i, j, player);
                    Piece enemy = player.equals(Piece.O) ? Piece.X : Piece.O;
                    int score = alphaBeta(enemy, subBoard, alpha, beta, board);
                    subBoard.setValueAt(i, j, Piece.EMPTY);

                    if (maxing) {
                        bestScore = Math.max(score, bestScore);
                        alpha = Math.max(alpha, bestScore);
                    } else {
                        bestScore = Math.min(score, bestScore);
                        beta = Math.min(beta, bestScore);
                    }

                    if (beta <= alpha) {
                        return bestScore;
                    }
                }
            }
        }
        return bestScore;
    }

    private int getBestMoveValue(int bestMoveValue, ArrayList<Move> bestMovesList, int i, int j, int moveValue) {
        if (moveValue > bestMoveValue) {
            bestMoveValue = moveValue;
            bestMovesList.clear();
            Move bestMove = new Move(i, j);
            bestMovesList.add(bestMove);
        } else if (moveValue == bestMoveValue) {
            Move bestMove = new Move(i, j);
            bestMovesList.add(bestMove);
        }
        return bestMoveValue;
    }
    private int evaluateBoard(Board board, Piece player) {
        Random random = new Random();
        return random.nextInt(201) - 100 ;
    }

    public Piece getCpuMark() {
        return cpuMark;
    }

    public void setCpuMark(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }
}
