public class Move {
    // ==================== FIELDS ====================
    public String type; // "MOVE" or "RANDOMIZE"
    public int r1, c1, r2, c2;

    // ==================== CONSTRUCTORS ====================
    public Move(int r1, int c1, int r2, int c2) {
        this.type = "MOVE";
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
    }

    public Move(String type) {
        this.type = type;
    }

    // ==================== METHODS ====================
    @Override
    public String toString() {
        if (type.equals("RANDOMIZE"))
            return "RANDOMIZE";
        return r1 + "," + c1 + "," + r2 + "," + c2;
    }
}