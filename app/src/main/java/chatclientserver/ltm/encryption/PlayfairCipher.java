package chatclientserver.ltm.encryption;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the Playfair cipher for encrypting and decrypting text messages.
 * The Playfair cipher uses a 5x5 matrix of letters constructed using a keyword.
 */
public class PlayfairCipher {
    private static final int MATRIX_SIZE = 5;
    private char[][] matrix;
    private String key;

    /**
     * Constructs a PlayfairCipher with the specified key.
     * 
     * @param key The key to use for encryption and decryption
     */
    public PlayfairCipher(String key) {
        this.key = key.toUpperCase().replaceAll("[^A-Z]", "");
        generateMatrix();
    }

    /**
     * Generates the 5x5 matrix based on the key.
     * The matrix is filled with the key first (removing duplicates),
     * then the remaining letters of the alphabet (excluding 'J' which is replaced with 'I').
     */
    private void generateMatrix() {
        matrix = new char[MATRIX_SIZE][MATRIX_SIZE];
        
        // Create a set to hold unique characters from the key
        Set<Character> keyChars = new LinkedHashSet<>();
        
        // Add all characters from the key
        for (char c : key.toCharArray()) {
            if (c == 'J') c = 'I'; // Replace J with I
            keyChars.add(c);
        }
        
        // Add remaining alphabet characters
        for (char c = 'A'; c <= 'Z'; c++) {
            if (c == 'J') continue; // Skip J
            keyChars.add(c);
        }
        
        // Fill the matrix
        int row = 0, col = 0;
        for (char c : keyChars) {
            matrix[row][col] = c;
            col++;
            if (col == MATRIX_SIZE) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Encrypts the given plaintext using the Playfair cipher.
     * 
     * @param plaintext The text to encrypt
     * @return The encrypted text
     */
    public String encrypt(String plaintext) {
        // Prepare the plaintext
        String prepared = preparePlaintext(plaintext);
        
        // Split into digraphs
        List<String> digraphs = splitIntoDigraphs(prepared);
        
        // Encrypt each digraph
        StringBuilder ciphertext = new StringBuilder();
        for (String digraph : digraphs) {
            ciphertext.append(encryptDigraph(digraph));
        }
        
        return ciphertext.toString();
    }

    /**
     * Decrypts the given ciphertext using the Playfair cipher.
     * 
     * @param ciphertext The text to decrypt
     * @return The decrypted text
     */
    public String decrypt(String ciphertext) {
        // Prepare the ciphertext
        String prepared = ciphertext.toUpperCase().replaceAll("[^A-Z]", "");
        
        // Split into digraphs
        List<String> digraphs = splitIntoDigraphs(prepared);
        
        // Decrypt each digraph
        StringBuilder plaintext = new StringBuilder();
        for (String digraph : digraphs) {
            plaintext.append(decryptDigraph(digraph));
        }
        
        return plaintext.toString();
    }

    /**
     * Prepares the plaintext for encryption by:
     * 1. Converting to uppercase
     * 2. Removing non-alphabetic characters
     * 3. Replacing 'J' with 'I'
     * 4. Separating repeated letters with 'X'
     * 5. Adding 'X' if the length is odd
     * 
     * @param plaintext The plaintext to prepare
     * @return The prepared plaintext
     */
    private String preparePlaintext(String plaintext) {
        // Convert to uppercase and remove non-alphabetic characters
        String text = plaintext.toUpperCase().replaceAll("[^A-Z]", "");
        
        // Replace J with I
        text = text.replace('J', 'I');
        
        // Separate repeated letters with X
        StringBuilder prepared = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            prepared.append(text.charAt(i));
            if (i + 1 < text.length() && text.charAt(i) == text.charAt(i + 1)) {
                prepared.append('X');
            }
        }
        
        // Add X if the length is odd
        if (prepared.length() % 2 != 0) {
            prepared.append('X');
        }
        
        return prepared.toString();
    }

    /**
     * Splits the text into digraphs (pairs of letters).
     * 
     * @param text The text to split
     * @return A list of digraphs
     */
    private List<String> splitIntoDigraphs(String text) {
        List<String> digraphs = new ArrayList<>();
        for (int i = 0; i < text.length(); i += 2) {
            digraphs.add(text.substring(i, i + 2));
        }
        return digraphs;
    }

    /**
     * Encrypts a single digraph using the Playfair rules.
     * 
     * @param digraph The digraph to encrypt
     * @return The encrypted digraph
     */
    private String encryptDigraph(String digraph) {
        char a = digraph.charAt(0);
        char b = digraph.charAt(1);
        
        int[] posA = findPosition(a);
        int[] posB = findPosition(b);
        
        char[] result = new char[2];
        
        // Same row
        if (posA[0] == posB[0]) {
            result[0] = matrix[posA[0]][(posA[1] + 1) % MATRIX_SIZE];
            result[1] = matrix[posB[0]][(posB[1] + 1) % MATRIX_SIZE];
        }
        // Same column
        else if (posA[1] == posB[1]) {
            result[0] = matrix[(posA[0] + 1) % MATRIX_SIZE][posA[1]];
            result[1] = matrix[(posB[0] + 1) % MATRIX_SIZE][posB[1]];
        }
        // Rectangle
        else {
            result[0] = matrix[posA[0]][posB[1]];
            result[1] = matrix[posB[0]][posA[1]];
        }
        
        return new String(result);
    }

    /**
     * Decrypts a single digraph using the Playfair rules.
     * 
     * @param digraph The digraph to decrypt
     * @return The decrypted digraph
     */
    private String decryptDigraph(String digraph) {
        char a = digraph.charAt(0);
        char b = digraph.charAt(1);
        
        int[] posA = findPosition(a);
        int[] posB = findPosition(b);
        
        char[] result = new char[2];
        
        // Same row
        if (posA[0] == posB[0]) {
            result[0] = matrix[posA[0]][(posA[1] - 1 + MATRIX_SIZE) % MATRIX_SIZE];
            result[1] = matrix[posB[0]][(posB[1] - 1 + MATRIX_SIZE) % MATRIX_SIZE];
        }
        // Same column
        else if (posA[1] == posB[1]) {
            result[0] = matrix[(posA[0] - 1 + MATRIX_SIZE) % MATRIX_SIZE][posA[1]];
            result[1] = matrix[(posB[0] - 1 + MATRIX_SIZE) % MATRIX_SIZE][posB[1]];
        }
        // Rectangle
        else {
            result[0] = matrix[posA[0]][posB[1]];
            result[1] = matrix[posB[0]][posA[1]];
        }
        
        return new String(result);
    }

    /**
     * Finds the position of a character in the matrix.
     * 
     * @param c The character to find
     * @return An array containing the row and column indices
     */
    private int[] findPosition(char c) {
        if (c == 'J') c = 'I'; // Replace J with I
        
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (matrix[i][j] == c) {
                    return new int[] {i, j};
                }
            }
        }
        
        return new int[] {-1, -1}; // Should never happen if the matrix is properly initialized
    }

    /**
     * Gets the current key.
     * 
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the matrix as a string for display purposes.
     * 
     * @return A string representation of the matrix
     */
    public String getMatrixAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                sb.append(matrix[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
