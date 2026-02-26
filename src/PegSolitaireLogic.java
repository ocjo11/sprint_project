public class PegSolitaireLogic {
    // Checks if a move is valid based on distance
    // In English Peg Solitaire, pegs jump exactly 2 spaces
    public boolean isValidMove(int from, int to) {
        return Math.abs(from - to) == 2;
    }

    // Removes a peg (simulated by decreasing peg count)
    public int removePeg(int pegCount) {
        if (pegCount <= 0) {
            return 0;
        }
        return pegCount - 1;
    }
}
