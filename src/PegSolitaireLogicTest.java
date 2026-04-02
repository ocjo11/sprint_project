import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PegSolitaireLogicTest {

    @Test
    void testBoardInitializationManual() {

        PegSolitaireGame game = new ManualGame(7, "English");

        assertEquals(7, game.getSize());
        assertEquals("English", game.getType());
    }

    @Test
    void testStartNewGameManual() {

        PegSolitaireGame game = new ManualGame(7, "Diamond");

        game.startNewGame();

        int pegCount = game.getPegCount();

        assertTrue(pegCount > 0);
    }

    @Test
    void testValidMoveManual() {

        PegSolitaireGame game = new ManualGame(7, "English");

        assertTrue(game.isValidMove(3,1,3,3));
    }

    @Test
    void testInvalidMoveManual() {

        PegSolitaireGame game = new ManualGame(7, "English");

        assertFalse(game.isValidMove(0,0,0,1));
    }

    @Test
    void testMakeMoveManual() {

        PegSolitaireGame game = new ManualGame(7, "English");

        boolean moved = game.makeMove(3,1,3,3);

        assertTrue(moved);
    }

    @Test
    void testPegCountDecreaseAfterMoveManual() {

        PegSolitaireGame game = new ManualGame(7, "English");

        int before = game.getPegCount();

        game.makeMove(3,1,3,3);

        int after = game.getPegCount();

        assertTrue(after < before);
    }

    @Test
    void testWinConditionManual() {

        PegSolitaireGame game = new ManualGame(3, "English");

        assertFalse(game.checkWin());
    }

    @Test
    void testMovesRemainingManual() {

        PegSolitaireGame game = new ManualGame(7, "English");

        assertTrue(game.hasMovesLeft());
    }

    @Test
    void testBoardInitializationAutomated() {

        PegSolitaireGame game = new AutomatedGame(7, "English");

        assertEquals(7, game.getSize());
        assertEquals("English", game.getType());
    }

    @Test
    void testAutoMoveExecutes() {

        PegSolitaireGame game = new AutomatedGame(7, "English");

        int before = game.getPegCount();

        boolean moved = game.playTurn(); // automated move

        int after = game.getPegCount();

        assertTrue(moved);
        assertTrue(after < before);
    }

    @Test
    void testAutoMoveUntilNoMovesLeft() {

        PegSolitaireGame game = new AutomatedGame(7, "English");

        int safetyCounter = 0;

        // keep playing automatically
        while (game.hasMovesLeft() && safetyCounter < 1000) {
            game.playTurn();
            safetyCounter++;
        }

        // Game should eventually stop
        assertFalse(game.hasMovesLeft() || game.getPegCount() <= 1);
    }

    @Test
    void testRandomizeBoard() {

        PegSolitaireGame game = new AutomatedGame(7, "English");

        game.randomizeBoard();

        int pegCount = game.getPegCount();

        assertTrue(pegCount >= 0); // just ensure it runs without crash
    }
}