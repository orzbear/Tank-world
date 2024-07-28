package Tanks;

import java.io.File;

/**
 * An abstract class representing a level loader for counting level files in a directory.
 */
public abstract class LevelLoader {

    /**
     * Abstract method to count the number of level files in a specified directory.
     *
     * @param directoryPath The path to the directory containing level files.
     * @return The number of level files found in the directory.
     */
    public abstract int countLevelFiles(String directoryPath);
}

/**
 * A concrete subclass of LevelLoader that implements the countLevelFiles method to count level files.
 */
class LevelFileCounter extends LevelLoader {

    /**
     * Counts the number of level files (matching the pattern "level\d+\.txt") in the specified directory.
     *
     * @param directoryPath The path to the directory containing level files.
     * @return The number of level files found in the directory.
     */
    @Override
    public int countLevelFiles(String directoryPath) {
        // Create a File object representing the specified directory
        File directory = new File(directoryPath);
        // Get a list of files in the directory
        File[] files = directory.listFiles();
        int levelSize = 0;

        // Check if files is not null (directory is valid and contains files)
        if (files != null) {
            // Iterate through each file in the directory
            for (File file : files) {
                // Check if the file is a regular file and matches the pattern "level\d+\.txt"
                if (file.isFile() && file.getName().matches("level\\d+\\.txt")) {
                    // Increment the count of level files found
                    levelSize++;
                }
            }
        }

        // Return the total count of level files
        return levelSize;
    }
}
