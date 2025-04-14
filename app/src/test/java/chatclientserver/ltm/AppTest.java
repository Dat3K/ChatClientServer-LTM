/*
 * Test for the App class
 */
package chatclientserver.ltm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test void appClassExists() {
        // Just verify that the App class exists and can be instantiated
        App classUnderTest = new App();
        assertNotNull(classUnderTest, "app should be instantiated");
    }
}
