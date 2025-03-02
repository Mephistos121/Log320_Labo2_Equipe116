import java.util.ArrayList;

public class Evalutation {

    private int numExploredNodes;
    private final int BEST_SCORE = 100;
    private final int WORSE_SCORE = -100;
    private Piece cpuMark;

    // Retourne la liste des coups possibles. Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveAB(SubBoard subBoard) {
        numExploredNodes = 0;
        int alpha = WORSE_SCORE;
        int beta = BEST_SCORE;

        int bestMoveValue = WORSE_SCORE;
        ArrayList<Move> bestMoves = new ArrayList<>();

        Piece enemy = (cpuMark.equals(Piece.X)) ? Piece.O : Piece.X;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (subBoard.getValueAt(i, j).equals(Piece.EMPTY)) {
                    subBoard.setValueAt(i, j, cpuMark);
                    int moveValue = alphaBeta(enemy, subBoard, alpha, beta);
                    subBoard.setValueAt(i, j, Piece.EMPTY);

                    bestMoveValue = getBestMoveValue(bestMoveValue, bestMoves, i, j, moveValue);
                }
            }
        }
        return bestMoves;
    }

    //La partie récursive de alphaBeta
    private int alphaBeta(Piece player, SubBoard subBoard, int alpha, int beta) {
        numExploredNodes++;

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
                    int score = alphaBeta(enemy, subBoard, alpha, beta);
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

    //method to get the best move
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

}
