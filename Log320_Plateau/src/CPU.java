import java.util.ArrayList;
import java.util.Random;

public class CPU {
    Piece cpuMark;
    final int WORSE_SCORE = -100;
    final int BEST_SCORE = 100;

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


    public ArrayList<Move> getNextMoveAB(int maxDepth, Board board, Move lastMove, Piece playedPiece) {
        ArrayList<Move> moves = new ArrayList<>();
        int max = WORSE_SCORE;
        ArrayList<Move> possibleMoves = board.getValidMoves(lastMove);

        for (Move move : possibleMoves) {
            board.playMove(move, playedPiece== Piece.O ? Piece.X : Piece.O);
            int ret = alphaBeta(cpuMark == Piece.O ? Piece.X : Piece.O, maxDepth-1, board, move,false);
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


    //La partie récursive de alphaBeta
    private int alphaBeta(Piece player, int depth, Board board, Move playedMove, Boolean isMaxing) {
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

    private int evaluateBoard(Board board, Piece player, Move move) {
        Random rand = new Random();
        return rand.nextInt(201)-100;
    }

    public Piece getCpuMark() {
        return cpuMark;
    }

    public void setCpuMark(Piece cpuMark) {
        this.cpuMark = cpuMark;
    }
}
