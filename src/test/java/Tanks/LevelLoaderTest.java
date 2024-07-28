package Tanks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.io.File;

public class LevelLoaderTest {

    @Test
    public void testCountLevelFiles_ExistingDirectory() {
        // Prepare a temporary directory with sample level files
        String directoryPath = "testDirectory";
        prepareTestDirectory(directoryPath, new String[]{"level1.txt", "level2.txt", "level3.txt", "file.txt", "level4.txt"});

        // Create an instance of LevelFileCounter
        LevelFileCounter fileCounter = new LevelFileCounter();

        // Call countLevelFiles method
        int numberOfLevelFiles = fileCounter.countLevelFiles(directoryPath);

        // Verify the result
        assertEquals(3, numberOfLevelFiles); // Expecting 3 level files (level1.txt, level2.txt, level3.txt)

        // Clean up the test directory
        cleanupTestDirectory(directoryPath);
    }

    private void prepareTestDirectory(String directoryPath, String[] fileNames) {
        // Create a test directory
        File directory = new File(directoryPath);
        directory.mkdir();

        // Create sample files in the test directory
        for (String fileName : fileNames) {
            File file = new File(directoryPath + File.separator + fileName);
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cleanupTestDirectory(String directoryPath) {
        // Delete the test directory and its contents
        File directory = new File(directoryPath);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            directory.delete();
        }
    }

    @Test
public void testCountLevelFiles_EmptyDirectory() {
    // Prepare an empty test directory
    String directoryPath = "emptyDirectory";
    prepareTestDirectory(directoryPath, new String[]{});

    // Create an instance of LevelFileCounter
    LevelFileCounter fileCounter = new LevelFileCounter();

    // Call countLevelFiles method
    int numberOfLevelFiles = fileCounter.countLevelFiles(directoryPath);

    // Verify the result
    assertEquals(0, numberOfLevelFiles); // Expecting 0 level files in an empty directory

    // Clean up the test directory
    cleanupTestDirectory(directoryPath);
}
@Test
public void testCountLevelFiles_NonLevelFiles() {
    // Prepare a test directory with non-level files
    String directoryPath = "nonLevelFilesDirectory";
    prepareTestDirectory(directoryPath, new String[]{"file1.txt", "file2.jpg", "document.pdf"});

    // Create an instance of LevelFileCounter
    LevelFileCounter fileCounter = new LevelFileCounter();

    // Call countLevelFiles method
    int numberOfLevelFiles = fileCounter.countLevelFiles(directoryPath);

    // Verify the result
    assertEquals(0, numberOfLevelFiles); // Expecting 0 level files in a directory with non-level files

    // Clean up the test directory
    cleanupTestDirectory(directoryPath);
}
@Test
public void testCountLevelFiles_MixedFiles() {
    // Prepare a test directory with mixed files including level files
    String directoryPath = "mixedFilesDirectory";
    prepareTestDirectory(directoryPath, new String[]{"level1.txt", "file1.txt", "level2.txt", "image.jpg", "level3.txt"});

    // Create an instance of LevelFileCounter
    LevelFileCounter fileCounter = new LevelFileCounter();

    // Call countLevelFiles method
    int numberOfLevelFiles = fileCounter.countLevelFiles(directoryPath);

    // Verify the result
    assertEquals(3, numberOfLevelFiles); // Expecting 3 level files in a directory with mixed files

    // Clean up the test directory
    cleanupTestDirectory(directoryPath);
}

}
