package chatclientserver.ltm.util;

/**
 * Constants used throughout the application.
 */
public class Constants {
    // Default server port for server startup
    public static final int DEFAULT_SERVER_PORT = 8888;

    // Message types
    public static final int MESSAGE_TYPE_TEXT = 1;
    public static final int MESSAGE_TYPE_FILE = 2;
    public static final int MESSAGE_TYPE_KEY_EXCHANGE = 3;
    public static final int MESSAGE_TYPE_PHRASE_POSITIONS = 4;
    public static final int MESSAGE_TYPE_USER_INFO = 5;
    public static final int MESSAGE_TYPE_LOGIN = 6;
    public static final int MESSAGE_TYPE_REGISTER = 7;
    public static final int MESSAGE_TYPE_LOGIN_RESULT = 8;
    public static final int MESSAGE_TYPE_REGISTER_RESULT = 9;

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
    public static final int GUI_WIDTH = 900;
    public static final int GUI_HEIGHT = 650;
    public static final int TEXT_AREA_ROWS = 20;
    public static final int TEXT_AREA_COLS = 50;
    public static final int TEXT_FIELD_COLS = 40;

    // UI constants
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 15;
    public static final int BORDER_RADIUS = 10;
    public static final int BUTTON_HEIGHT = 36;
    public static final int INPUT_HEIGHT = 36;

    // Status constants
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_WARNING = 3;
    public static final int STATUS_INFO = 4;
}
