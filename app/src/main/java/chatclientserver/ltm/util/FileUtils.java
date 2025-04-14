package chatclientserver.ltm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;

/**
 * Utility class for file operations.
 */
public class FileUtils {
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String RECEIVED_FILES_DIR = TEMP_DIR + File.separator + "chat_received_files";
    
    /**
     * Determines the file type based on the file extension.
     * 
     * @param fileName The name of the file
     * @return The file type (image, audio, video, or other)
     */
    public static String getFileType(String fileName) {
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        
        if (isImageFile(extension)) {
            return Constants.FILE_TYPE_IMAGE;
        } else if (isAudioFile(extension)) {
            return Constants.FILE_TYPE_AUDIO;
        } else if (isVideoFile(extension)) {
            return Constants.FILE_TYPE_VIDEO;
        } else {
            return Constants.FILE_TYPE_OTHER;
        }
    }
    
    /**
     * Checks if the file extension corresponds to an image file.
     * 
     * @param extension The file extension
     * @return true if the file is an image, false otherwise
     */
    private static boolean isImageFile(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || 
               extension.equals("gif") || extension.equals("bmp");
    }
    
    /**
     * Checks if the file extension corresponds to an audio file.
     * 
     * @param extension The file extension
     * @return true if the file is an audio file, false otherwise
     */
    private static boolean isAudioFile(String extension) {
        return extension.equals("mp3") || extension.equals("wav") || extension.equals("ogg") || 
               extension.equals("flac") || extension.equals("aac");
    }
    
    /**
     * Checks if the file extension corresponds to a video file.
     * 
     * @param extension The file extension
     * @return true if the file is a video file, false otherwise
     */
    private static boolean isVideoFile(String extension) {
        return extension.equals("mp4") || extension.equals("avi") || extension.equals("mkv") || 
               extension.equals("mov") || extension.equals("wmv");
    }
    
    /**
     * Reads a file into a byte array.
     * 
     * @param file The file to read
     * @return The file contents as a byte array
     * @throws IOException If an I/O error occurs
     */
    public static byte[] readFileToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        }
    }
    
    /**
     * Writes a byte array to a file in the received files directory.
     * 
     * @param data The data to write
     * @param fileName The name of the file
     * @return The created file
     * @throws IOException If an I/O error occurs
     */
    public static File writeByteArrayToFile(byte[] data, String fileName) throws IOException {
        // Create the received files directory if it doesn't exist
        File dir = new File(RECEIVED_FILES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Generate a unique file name to avoid conflicts
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        File file = new File(dir, uniqueFileName);
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            return file;
        }
    }
    
    /**
     * Gets the path to the received files directory.
     * 
     * @return The path to the received files directory
     */
    public static Path getReceivedFilesDir() {
        return Paths.get(RECEIVED_FILES_DIR);
    }
    
    /**
     * Creates the received files directory if it doesn't exist.
     * 
     * @throws IOException If an I/O error occurs
     */
    public static void createReceivedFilesDir() throws IOException {
        Path dir = getReceivedFilesDir();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }
}
