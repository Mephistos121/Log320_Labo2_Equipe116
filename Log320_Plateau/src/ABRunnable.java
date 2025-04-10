import java.util.ArrayList;
import java.util.List;

public class ABRunnable implements Runnable{
    private volatile int score;

    private Piece player;
    private int depth;
    private Board board;
    private Move lastPlayedMove;
    private boolean isMaxing;
    private int alpha;
    private int beta;
    private List<Move> moves;
    private ArrayList<Move> bestMoves = new ArrayList<>();

    public ABRunnable(List<Move> moves, Piece player, int depth, Board board, boolean isMaxing, int alpha, int beta) {
        this.player = player;
        this.depth = depth;
        this.board = board;
        //this.lastPlayedMove = lastPlayedMove;
        this.isMaxing = isMaxing;
        this.alpha = alpha;
        this.beta = beta;
        this.moves = moves;
    }

    @Override
    public void run() {
        int maxScore = Integer.MIN_VALUE;
        CPU cpu = new CPU(player);

        for(Move move : moves) {
            if(move != null) {
                board.playMove(move, player);
                this.score = cpu.minMaxAlphaBeta(cpu.getCpuMark() == Piece.O ? Piece.X : Piece.O, depth - 1, board, move, !isMaxing, alpha, beta);
                //System.out.println(score);
                board.undoMove(move);
            }

            if (score == maxScore) {
                bestMoves.add(move);
            } else if(score > maxScore) {
                bestMoves.clear();
                bestMoves.add(move);
                maxScore = score;
            }
            alpha = Math.max(alpha, score);
        }

    }

    public int getScore(){
        return score;
    }

    public ArrayList<Move> getMoves() {
        return bestMoves;
    }
}
