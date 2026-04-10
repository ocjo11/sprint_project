public class ManualGame extends PegSolitaireGame {

    // ==================== CONSTRUCTOR ====================
    public ManualGame(int size, String type) {
        super(size, type);
    }

    // ==================== METHODS ====================
    @Override
    public boolean makeMove(int r1, int c1, int r2, int c2) {
        if (!isValidMove(r1, c1, r2, c2))
            return false;

        applyMove(r1, c1, r2, c2);
        return true;
    }

    @Override
    public boolean playTurn() {
        return false; // GUI handles moves for manual mode
    }
}