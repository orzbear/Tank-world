package Tanks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;

public class SmoothTest {

    @Test
    public void testSmoothLocation() {
        // Create sample input ArrayLists for testing
        ArrayList<Integer> inputList = new ArrayList<>();
        inputList.add(0);
        inputList.add(1);
        inputList.add(2);

        ArrayList<Float> inputSmoothValues = new ArrayList<>();
        inputSmoothValues.add(20.0f);
        inputSmoothValues.add(22.5f);
        inputSmoothValues.add(25.0f);

        // Call the smoothLocation method
        HashMap<Integer, Float> smoothedLocations = Smooth.smoothLocation(inputList, inputSmoothValues);

        // Verify the size and content of the output HashMap
        assertEquals(3, smoothedLocations.size()); // Expected size based on input list size

        // Verify specific key-value pairs in the output HashMap
        assertTrue(smoothedLocations.containsKey(32)); // Check if key 32 is present
        assertEquals(20.0f, smoothedLocations.get(32), 0.001f); // Check value corresponding to key 32
        assertTrue(smoothedLocations.containsKey(64)); // Check if key 64 is present
        assertEquals(22.5f, smoothedLocations.get(64), 0.001f); // Check value corresponding to key 64
        assertTrue(smoothedLocations.containsKey(96)); // Check if key 96 is present
        assertEquals(25.0f, smoothedLocations.get(96), 0.001f); // Check value corresponding to key 96
        // Add more specific checks for other key-value pairs as needed
    }
}