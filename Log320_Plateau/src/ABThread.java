public class ABThread extends Thread{
    private volatile int score;

    private Piece player;
    private int depth;
    private Board board;
    private Move lastPlayedMove;
    private boolean isMaxing;
    private int alpha;
    private int beta;
    public ABThread(Piece player, int depth, Board board, Move lastPlayedMove, boolean isMaxing, int alpha, int beta) {
        this.player = player;
        this.depth = depth;
        this.board = board;
        this.lastPlayedMove = lastPlayedMove;
        this.isMaxing = isMaxing;
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public void run() {
        CPU cpu = new CPU(player);

        board.playMove(lastPlayedMove, player);
        score = cpu.minMaxAlphaBeta(cpu.getCpuMark()==Piece.O?Piece.X:Piece.O, depth-1, board, lastPlayedMove, !isMaxing, alpha, beta);
        //System.out.println(score);
        board.undoMove(lastPlayedMove);
    }

    public int getScore(){
        return score;
    }

    public Move getMove() {
        return lastPlayedMove;
    }
}
