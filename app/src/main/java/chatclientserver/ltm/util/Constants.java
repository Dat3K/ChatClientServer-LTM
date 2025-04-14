package chatclientserver.ltm.util;

/**
 * Constants used throughout the application.
 */
public class Constants {
    // Server configuration
    public static final int SERVER_PORT = 8888;
    public static final String SERVER_HOST = "localhost";

    // Message types
    public static final int MESSAGE_TYPE_TEXT = 1;
    public static final int MESSAGE_TYPE_FILE = 2;
    public static final int MESSAGE_TYPE_KEY_EXCHANGE = 3;
    public static final int MESSAGE_TYPE_PHRASE_POSITIONS = 4;

    // File types
    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_AUDIO = "audio";
    public static final String FILE_TYPE_VIDEO = "video";
    public static final String FILE_TYPE_OTHER = "other";

    // Buffer sizes
    public static final int BUFFER_SIZE = 8192;

    // Search phrase
    public static final String SEARCH_PHRASE = "xin chào";

    // Default key for Playfair cipher
    public static final String DEFAULT_KEY = "PLAYFAIR";

    // GUI constants
    public static final int GUI_WIDTH = 800;
    public static final int GUI_HEIGHT = 600;
    public static final int TEXT_AREA_ROWS = 20;
    public static final int TEXT_AREA_COLS = 50;
    public static final int TEXT_FIELD_COLS = 40;
}
