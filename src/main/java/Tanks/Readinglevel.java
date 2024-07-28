package Tanks;

import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Readinglevel {

    // HashMap to store heights of 'X' in the level
    private static HashMap<Integer, Integer> heights = new HashMap<>();
    // ArrayList to store positions of 'T' (trees) in the level
    private static ArrayList<Integer> trees = new ArrayList<>();
    // ArrayList to store positions of tanks (A, B, C, D) in the level
    private static ArrayList<Integer> tanks = new ArrayList<>(Collections.nCopies(19, null));

    /**
     * Get the heights HashMap containing positions and heights of 'X' in the level.
     *
     * @return The heights HashMap.
     */
    public static HashMap<Integer, Integer> getHeights() {
        return heights;
    }

    /**
     * Get the trees ArrayList containing positions of 'T' (trees) in the level.
     *
     * @return The trees ArrayList.
     */
    public static ArrayList<Integer> getTrees() {
        return trees;
    }

    /**
     * Get the tanks ArrayList containing positions of tanks (A, B, C, D) in the level.
     *
     * @return The tanks ArrayList.
     */
    public static ArrayList<Integer> getTanks() {
        return tanks;
    }

    /**
     * Reads the level file and populates heights, trees, and tanks data structures based on the level contents.
     *
     * @param lv The path to the level file to be read.
     */
    public static void readLvs(String lv) {
        try {
            // Open the level file for reading
            FileReader fileReader = new FileReader(lv);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int lineCount = 0;

            // Read each line of the level file
            while ((line = bufferedReader.readLine()) != null) {
                // Process each character in the line
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    // Check if the character is 'X' (representing terrain height)
                    if (c == 'X') {
                        // Store the terrain height (20 - lineCount due to top-down orientation)
                        heights.put(i, 20 - lineCount);
                    }
                    // Check if the character is 'T' (representing trees)
                    if (c == 'T') {
                        // Store the position of the tree
                        trees.add(i);
                    }
                    // Check if the character is 'A', 'B', 'C', or 'D' (representing tank positions)
                    if (c == 'A' || c == 'B' || c == 'C' || c == 'D') {
                        // Calculate tank index based on character ('A' -> 0, 'B' -> 1, ...)
                        int tankIndex = c - 'A';
                        // Store the position of the tank in the corresponding index of the tanks ArrayList
                        tanks.set(tankIndex, i);
                    }
                }
                lineCount++;
            }

            // Remove null elements (positions not filled) from the tanks ArrayList
            tanks.removeIf(element -> element == null);

            // Close the BufferedReader after reading
            bufferedReader.close();
        } catch (IOException e) {
            // Handle IO exceptions (e.g., file not found, read error)
            e.printStackTrace();
        }
    }
}
