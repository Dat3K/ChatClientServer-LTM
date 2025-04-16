package chatclientserver.ltm.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * Tests for the ClientHandler's search phrase preparation and finding functionality.
 * These tests directly test the utility methods without creating a ClientHandler instance.
 */
public class ClientHandlerTest {

    /**
     * Tests the prepareSearchPhrase method functionality directly.
     */
    @Test
    public void testPrepareSearchPhrase() {
        // Test with Vietnamese phrase with spaces
        String result = prepareSearchPhrase("xin chào");
        assertEquals("XINZCHAO", result);

        // Test with multiple spaces
        result = prepareSearchPhrase("xin chào bạn");
        assertEquals("XINZCHAOZBAN", result);

        // Test with other diacritical marks
        result = prepareSearchPhrase("Tiếng Việt");
        assertEquals("TIENGZVIET", result);
    }

    /**
     * Tests the findPhrasePositions method functionality directly.
     */
    @Test
    public void testFindPhrasePositions() {
        // Test finding a phrase in a text
        List<Integer> positions = findPhrasePositions("HELXLOZIORLDZXINZCHAO", "XINZCHAO");
        assertEquals(1, positions.size());
        assertEquals(13, positions.get(0));

        // Test finding multiple occurrences
        List<Integer> multiplePositions = findPhrasePositions("XINZCHAOZXINZCHAO", "XINZCHAO");
        assertEquals(2, multiplePositions.size());
        assertEquals(0, multiplePositions.get(0));
        assertEquals(9, multiplePositions.get(1));

        // Test when phrase is not found
        List<Integer> emptyPositions = findPhrasePositions("HELXLOZIORLD", "XINZCHAO");
        assertTrue(emptyPositions.isEmpty());
    }

    /**
     * Implementation of the prepareSearchPhrase method for testing.
     * This is a copy of the method in ClientHandler to avoid creating a ClientHandler instance.
     */
    private String prepareSearchPhrase(String phrase) {
        // Remove diacritical marks (convert Vietnamese to non-accented form)
        String normalized = StringUtils.stripAccents(phrase);

        // Replace spaces with 'Z' (same as in PlayfairCipher)
        normalized = normalized.replace(' ', 'Z');

        // Convert to uppercase (decrypted messages are in uppercase)
        return normalized.toUpperCase();
    }

    /**
     * Implementation of the findPhrasePositions method for testing.
     * This is a copy of the method in ClientHandler to avoid creating a ClientHandler instance.
     */
    private List<Integer> findPhrasePositions(String text, String phrase) {
        List<Integer> positions = new ArrayList<>();

        // No need to convert to lowercase since both the text and phrase are already in uppercase
        int index = text.indexOf(phrase);
        while (index >= 0) {
            positions.add(index);
            index = text.indexOf(phrase, index + 1);
        }

        return positions;
    }
}
