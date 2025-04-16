package chatclientserver.ltm.encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the PlayfairCipher class.
 */
public class PlayfairCipherTest {
    private PlayfairCipher cipher;

    @BeforeEach
    public void setUp() {
        // Create a cipher with the key "PLAYFAIR"
        cipher = new PlayfairCipher("PLAYFAIR");
    }

    @Test
    public void testEncryption() {
        // Test basic encryption
        String plaintext = "HELLO";
        String encrypted = cipher.encrypt(plaintext);

        // The encrypted text should not be the same as the plaintext
        assertNotEquals(plaintext, encrypted);

        // Test that decryption works
        String decrypted = cipher.decrypt(encrypted);

        // The decrypted text might have an X at the end if the plaintext length is odd
        // or if there are repeated letters
        assertEquals("HELXLO", decrypted);
    }

    @Test
    public void testDecryption() {
        // First encrypt a known plaintext
        String plaintext = "TESTME";
        String encrypted = cipher.encrypt(plaintext);

        // Then decrypt it
        String decrypted = cipher.decrypt(encrypted);

        // Verify the decryption result
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testWithSpecialCharacters() {
        // Test with special characters and spaces
        String plaintext = "Hello, World! 123";
        String encrypted = cipher.encrypt(plaintext);

        // The encrypted text should not contain special characters
        assertNotEquals(plaintext, encrypted);

        // Decrypt and verify
        String decrypted = cipher.decrypt(encrypted);

        // The decrypted text should now contain Z for spaces
        assertEquals("HELXLOZWORLDZX", decrypted);
    }

    @Test
    public void testWithVietnamesePhrase() {
        // Test with the Vietnamese phrase "xin chào"
        String plaintext = "xin chào";
        String encrypted = cipher.encrypt(plaintext);

        // Decrypt and verify
        String decrypted = cipher.decrypt(encrypted);

        // The decrypted text should be "XINZCHAO" (with Z for space and without diacritics)
        assertEquals("XINZCHAO", decrypted);
    }

    @Test
    public void testWithSpaces() {
        // Test with spaces
        String plaintext = "hello world";
        String encrypted = cipher.encrypt(plaintext);

        // Decrypt and verify
        String decrypted = cipher.decrypt(encrypted);

        // The decrypted text should have Z instead of spaces
        assertEquals("HELXLOZWORLD", decrypted);
    }

    @Test
    public void testWithVietnameseAndSpaces() {
        // Test with Vietnamese text and spaces
        String plaintext = "Tiếng Việt";
        String encrypted = cipher.encrypt(plaintext);

        // Decrypt and verify
        String decrypted = cipher.decrypt(encrypted);

        // The decrypted text should have Z for spaces and no diacritical marks
        assertEquals("TIENGZVIET", decrypted);
    }

    @Test
    public void testWithDifferentKeys() {
        // Test with a different key
        PlayfairCipher anotherCipher = new PlayfairCipher("KEYWORD");

        String plaintext = "HELLO";
        String encrypted1 = cipher.encrypt(plaintext);
        String encrypted2 = anotherCipher.encrypt(plaintext);

        // The two encrypted texts should be different
        assertNotEquals(encrypted1, encrypted2);

        // Decrypt with the correct cipher
        String decrypted1 = cipher.decrypt(encrypted1);
        String decrypted2 = anotherCipher.decrypt(encrypted2);

        // Both should decrypt to the same text (with possible X insertions)
        assertEquals(decrypted1, decrypted2);
    }

    @Test
    public void testWithEmptyString() {
        // Test with an empty string
        String plaintext = "";
        String encrypted = cipher.encrypt(plaintext);

        // The encrypted text should also be empty
        assertEquals("", encrypted);

        // Decrypt and verify
        String decrypted = cipher.decrypt(encrypted);
        assertEquals("", decrypted);
    }

    @Test
    public void testWithLongText() {
        // Test with a longer text
        String plaintext = "This is a longer text to test the Playfair cipher with multiple words and sentences";
        String encrypted = cipher.encrypt(plaintext);

        // Decrypt and verify
        String decrypted = cipher.decrypt(encrypted);

        // The decrypted text should be the plaintext in uppercase, with X insertions and no spaces or special characters
        assertNotEquals(plaintext.toUpperCase().replaceAll("[^A-Z]", ""), decrypted);
    }
}
