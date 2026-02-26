import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PegSolitaireLogicTest {
    @Test
    void testValidMove() {
        PegSolitaireLogic logic = new PegSolitaireLogic();
        assertTrue(logic.isValidMove(3, 5));
    }

    @Test
    void testInvalidMove() {
        PegSolitaireLogic logic = new PegSolitaireLogic();
        assertFalse(logic.isValidMove(3, 6));
    }

    @Test
    void testRemovePeg() {
        PegSolitaireLogic logic = new PegSolitaireLogic();
        assertEquals(9, logic.removePeg(10));
    }
}
