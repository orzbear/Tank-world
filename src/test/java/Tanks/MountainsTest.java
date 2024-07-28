package Tanks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class MountainsTest {

    private Mountains mountains;

    public void setUp() {
        // Initialize Mountains object for testing
        mountains = new Mountains(200.0f, 5.0f); // Example values for x-coordinate and height
    }

    @Test
    public void testConstructor() {
        // Verify initial values set by the constructor
        assertEquals(200.0f, mountains.x, 0.001f); // Check x-coordinate
        assertEquals(5.0f, mountains.getHeight(), 0.001f); // Check height using getter method
    }

    @Test
    public void testDraw() {
        // Create a simple App implementation for testing purposes
        TestApp testApp = new TestApp();

        // Call draw() method with the TestApp object
        mountains.draw(testApp);

        // Verify the expected drawing operations based on the TestApp state
        assertEquals(200.0f, testApp.rectX, 0.001f); // Check x-coordinate passed to rect
        assertEquals(640 - 5.0f * TestApp.CELLSIZE, testApp.rectY, 0.001f); // Check y-coordinate passed to rect
        assertEquals(1.0f, testApp.rectWidth, 0.001f); // Check width passed to rect
        assertEquals(5.0f * TestApp.CELLSIZE, testApp.rectHeight, 0.001f); // Check height passed to rect
    }

    // Helper class to simulate the App for testing purposes
    private static class TestApp extends App {
        static final int CELLSIZE = 10; // Example CELLSIZE for testing
        float rectX, rectY, rectWidth, rectHeight;

        

        @Override
        public void rect(float x, float y, float width, float height) {
            // Capture parameters passed to rect method for verification
            rectX = x;
            rectY = y;
            rectWidth = width;
            rectHeight = height;
        }
    }

    @Test
    public void testConstructor_MinimumHeight() {
        // Create a Mountain object with minimum height (0)
        Mountains mountain = new Mountains(100.0f, 0.0f);

        // Verify that the height is set correctly
        assertEquals(0.0f, mountain.getHeight(), 0.001f);
    }

    @Test
    public void testDraw_MinimumHeight() {
        // Create a mock App object for testing
        MockApp mockApp = new MockApp();

        // Create a Mountain object with minimum height (0)
        Mountains mountain = new Mountains(200.0f, 0.0f);

        // Call draw method with the mock App object
        mountain.draw(mockApp);

        // Verify the drawing coordinates
        assertEquals(200.0f, mockApp.rectX, 0.001f); // x-coordinate
        assertEquals(640.0f, mockApp.rectY, 0.001f); // y-coordinate (base of the mountain)
        assertEquals(1.0f, mockApp.rectWidth, 0.001f); // width
        assertEquals(0.0f, mockApp.rectHeight, 0.001f); // height (zero height should not draw anything)
    }

    // Mock App class for testing draw method
    private static class MockApp extends App {
        float rectX, rectY, rectWidth, rectHeight;


        @Override
        public void rect(float x, float y, float width, float height) {
            // Capture parameters passed to rect method for verification
            rectX = x;
            rectY = y;
            rectWidth = width;
            rectHeight = height;
        }
    }

    @Test
public void testConstructor_MaximumHeight() {
    // Create a Mountain object with maximum height (e.g., Float.MAX_VALUE)
    float maxHeight = Float.MAX_VALUE;
    Mountains mountain = new Mountains(300.0f, maxHeight);

    // Verify that the height is set correctly
    assertEquals(maxHeight, mountain.getHeight(), 0.001f);
}

@Test
public void testDraw_MaximumHeight() {
    // Create a mock App object for testing
    MockApp mockApp = new MockApp();

    // Create a Mountain object with maximum height (e.g., Float.MAX_VALUE)
    float maxHeight = Float.MAX_VALUE;
    Mountains mountain = new Mountains(400.0f, maxHeight);

    // Call draw method with the mock App object
    mountain.draw(mockApp);

    // Verify the drawing coordinates
    assertEquals(400.0f, mockApp.rectX, 0.001f); // x-coordinate
    assertEquals(Float.POSITIVE_INFINITY, mockApp.rectY, 0.001f); // y-coordinate (should handle large heights gracefully)
    assertEquals(1.0f, mockApp.rectWidth, 0.001f); // width
    assertEquals(Float.POSITIVE_INFINITY, mockApp.rectHeight, 0.001f); // height (should handle large heights gracefully)
    }
}