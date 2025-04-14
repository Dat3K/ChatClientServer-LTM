package crypto;

/**
 * Implementation of the PlayFair cipher algorithm for encrypting and decrypting text messages.
 * This cipher uses a 5x5 matrix of letters based on a keyword to perform substitution on pairs of letters.
 */
public class PlayFairCipher {
    private static final String DEFAULT_KEY = "keyword";
    private static final int GRID_SIZE = 5;
    
    private char[][] matrix;
    
    /**
     * Constructs a PlayFairCipher with the default key.
     */
    public PlayFairCipher() {
        this(DEFAULT_KEY);
    }
    
    /**
     * Constructs a PlayFairCipher with a specified key.
     * 
     * @param key The key to use for creating the cipher matrix
     */
    public PlayFairCipher(String key) {
        generateMatrix(key);
    }
    
    /**
     * Encrypts a plaintext message using the PlayFair cipher.
     * 
     * @param plainText The text to encrypt
     * @return The encrypted text
     */
    public String encrypt(String plainText) {
        // Prepare the text (remove non-letters, convert to uppercase)
        String prepared = prepareText(plainText);
        
        // Split the text into digraphs (pairs of letters)
        String[] digraphs = formDigraphs(prepared);
        
        StringBuilder cipherText = new StringBuilder();
        
        // Encrypt each digraph
        for (String digraph : digraphs) {
            if (digraph.length() == 2) {
                char a = digraph.charAt(0);
                char b = digraph.charAt(1);
                
                int[] posA = findPosition(a);
                int[] posB = findPosition(b);
                
                char[] encryptedPair = encryptPair(posA, posB);
                cipherText.append(encryptedPair);
            }
        }
        
        return cipherText.toString();
    }
    
    /**
     * Decrypts a ciphertext message using the PlayFair cipher.
     * 
     * @param cipherText The text to decrypt
     * @return The decrypted text
     */
    public String decrypt(String cipherText) {
        // Prepare the text (remove non-letters, convert to uppercase)
        String prepared = prepareText(cipherText);
        
        // Split the text into digraphs (pairs of letters)
        String[] digraphs = formDigraphs(prepared);
        
        StringBuilder plainText = new StringBuilder();
        
        // Decrypt each digraph
        for (String digraph : digraphs) {
            if (digraph.length() == 2) {
                char a = digraph.charAt(0);
                char b = digraph.charAt(1);
                
                int[] posA = findPosition(a);
                int[] posB = findPosition(b);
                
                char[] decryptedPair = decryptPair(posA, posB);
                plainText.append(decryptedPair);
            }
        }
        
        return plainText.toString();
    }
    
    /**
     * Generates the 5x5 matrix used for the PlayFair cipher based on the key.
     * 
     * @param key The key to use for generating the matrix
     */
    private void generateMatrix(String key) {
        matrix = new char[GRID_SIZE][GRID_SIZE];
        
        // Prepare the key (remove duplicates, convert to uppercase)
        String preparedKey = prepareKey(key);
        
        // Fill the matrix with the key and remaining alphabet
        int index = 0;
        boolean[] used = new boolean[26];
        
        // Mark 'J' as used (we'll replace it with 'I')
        used['J' - 'A'] = true;
        
        // Fill with key characters
        for (char c : preparedKey.toCharArray()) {
            int pos = c - 'A';
            if (!used[pos]) {
                used[pos] = true;
                matrix[index / GRID_SIZE][index % GRID_SIZE] = c;
                index++;
            }
        }
        
        // Fill with remaining unused alphabet characters
        for (char c = 'A'; c <= 'Z'; c++) {
            int pos = c - 'A';
            if (!used[pos]) {
                matrix[index / GRID_SIZE][index % GRID_SIZE] = c;
                index++;
            }
        }
    }
    
    /**
     * Prepares the key by removing duplicates and converting to uppercase.
     * 
     * @param key The key to prepare
     * @return The prepared key
     */
    private String prepareKey(String key) {
        // Convert to uppercase and replace J with I
        key = key.toUpperCase().replace('J', 'I');
        
        // Remove non-letters
        StringBuilder sb = new StringBuilder();
        for (char c : key.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append(c);
            }
        }
        
        // Remove duplicates
        StringBuilder result = new StringBuilder();
        for (char c : sb.toString().toCharArray()) {
            if (result.indexOf(String.valueOf(c)) == -1) {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Prepares the text for encryption/decryption by removing non-letters,
     * converting to uppercase, and replacing J with I.
     * 
     * @param text The text to prepare
     * @return The prepared text
     */
    private String prepareText(String text) {
        // Convert to uppercase and replace J with I
        text = text.toUpperCase().replace('J', 'I');
        
        // Remove non-letters
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append(c);
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Forms digraphs (pairs of letters) from the prepared text.
     * If a pair would have the same letter twice, inserts 'X' between them.
     * If the text has an odd length, appends 'X'.
     * 
     * @param text The prepared text
     * @return An array of digraphs
     */
    private String[] formDigraphs(String text) {
        StringBuilder modified = new StringBuilder(text);
        
        // Insert 'X' between same letters in a pair
        for (int i = 0; i < modified.length() - 1; i += 2) {
            if (modified.charAt(i) == modified.charAt(i + 1)) {
                modified.insert(i + 1, 'X');
            }
        }
        
        // Append 'X' if the length is odd
        if (modified.length() % 2 != 0) {
            modified.append('X');
        }
        
        // Form the digraphs
        int count = modified.length() / 2;
        String[] digraphs = new String[count];
        for (int i = 0; i < count; i++) {
            digraphs[i] = modified.substring(i * 2, i * 2 + 2);
        }
        
        return digraphs;
    }
    
    /**
     * Finds the position (row, column) of a character in the matrix.
     * 
     * @param c The character to find
     * @return An array containing the row and column indices
     */
    private int[] findPosition(char c) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (matrix[i][j] == c) {
                    return new int[] {i, j};
                }
            }
        }
        return null;
    }
    
    /**
     * Encrypts a pair of characters according to the PlayFair rules.
     * 
     * @param posA The position of the first character
     * @param posB The position of the second character
     * @return The encrypted pair of characters
     */
    private char[] encryptPair(int[] posA, int[] posB) {
        char[] result = new char[2];
        
        // Same row: take the character to the right (wrapping around)
        if (posA[0] == posB[0]) {
            result[0] = matrix[posA[0]][(posA[1] + 1) % GRID_SIZE];
            result[1] = matrix[posB[0]][(posB[1] + 1) % GRID_SIZE];
        }
        // Same column: take the character below (wrapping around)
        else if (posA[1] == posB[1]) {
            result[0] = matrix[(posA[0] + 1) % GRID_SIZE][posA[1]];
            result[1] = matrix[(posB[0] + 1) % GRID_SIZE][posB[1]];
        }
        // Different row and column: form a rectangle and take the character in the same row but opposite column
        else {
            result[0] = matrix[posA[0]][posB[1]];
            result[1] = matrix[posB[0]][posA[1]];
        }
        
        return result;
    }
    
    /**
     * Decrypts a pair of characters according to the PlayFair rules.
     * 
     * @param posA The position of the first character
     * @param posB The position of the second character
     * @return The decrypted pair of characters
     */
    private char[] decryptPair(int[] posA, int[] posB) {
        char[] result = new char[2];
        
        // Same row: take the character to the left (wrapping around)
        if (posA[0] == posB[0]) {
            result[0] = matrix[posA[0]][(posA[1] - 1 + GRID_SIZE) % GRID_SIZE];
            result[1] = matrix[posB[0]][(posB[1] - 1 + GRID_SIZE) % GRID_SIZE];
        }
        // Same column: take the character above (wrapping around)
        else if (posA[1] == posB[1]) {
            result[0] = matrix[(posA[0] - 1 + GRID_SIZE) % GRID_SIZE][posA[1]];
            result[1] = matrix[(posB[0] - 1 + GRID_SIZE) % GRID_SIZE][posB[1]];
        }
        // Different row and column: form a rectangle and take the character in the same row but opposite column
        else {
            result[0] = matrix[posA[0]][posB[1]];
            result[1] = matrix[posB[0]][posA[1]];
        }
        
        return result;
    }
}
