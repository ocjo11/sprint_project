import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PegSolitaireLogicTest {

    @Test
    void testBoardInitialization() {

        PegSolitaireLogic logic = new PegSolitaireLogic(7, "English");

        assertEquals(7, logic.getSize());
        assertEquals("English", logic.getType());
    }

    @Test
    void testStartNewGame() {

        PegSolitaireLogic logic = new PegSolitaireLogic(7, "Diamond");

        logic.startNewGame();

        int pegCount = logic.getPegCount();

        assertTrue(pegCount > 0);
    }

    @Test
    void testValidMove() {

        PegSolitaireLogic logic = new PegSolitaireLogic(7, "English");

        boolean result = logic.isValidMove(3,1,3,3);

        assertTrue(result);
    }

    @Test
    void testInvalidMove() {

        PegSolitaireLogic logic = new PegSolitaireLogic(7, "English");

        boolean result = logic.isValidMove(0,0,0,1);

        assertFalse(result);
    }

    @Test
    void testMakeMove() {

        PegSolitaireLogic logic = new PegSolitaireLogic(7, "English");

        boolean moved = logic.makeMove(3,1,3,3);

        assertTrue(moved);
    }

    @Test
    void testPegCountDecreaseAfterMove() {

        PegSolitaireLogic logic = new PegSolitaireLogic(7, "English");

        int before = logic.getPegCount();

        logic.makeMove(3,1,3,3);

        int after = logic.getPegCount();

        assertTrue(after < before);
    }

    @Test
    void testWinCondition() {

        PegSolitaireLogic logic = new PegSolitaireLogic(3, "English");

        boolean win = logic.checkWin();

        assertFalse(win);
    }

    @Test
    void testMovesRemaining() {

        PegSolitaireLogic logic = new PegSolitaireLogic(7, "English");

        boolean moves = logic.hasMovesLeft();

        assertTrue(moves);
    }
}