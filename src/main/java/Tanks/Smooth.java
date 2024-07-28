package Tanks;

import java.util.*;

public class Smooth {

    /**
     * Retrieves the value at a specified index from a HashMap.
     *
     * @param <K>   The type of keys in the HashMap.
     * @param <V>   The type of values in the HashMap.
     * @param map   The HashMap to retrieve the value from.
     * @param index The index of the value to retrieve.
     * @return The value at the specified index in the HashMap.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public static <K, V> V getValueAtIndex(HashMap<K, V> map, int index) {
        // Iterate over the entries of the HashMap
        int currentIndex = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (currentIndex == index) {
                return entry.getValue(); // Return value at index
            }
            currentIndex++;
        }
        throw new IndexOutOfBoundsException("Index " + index + " is out of bounds");
    }

    /**
     * Smooths a line represented by a HashMap of integer values.
     * The line is smoothed by averaging adjacent values.
     *
     * @param map The HashMap representing the line to be smoothed.
     * @return An ArrayList of smoothed float values.
     */
    public static ArrayList<Float> smoothLine(HashMap<Integer, Integer> map) {
        ArrayList<Float> result1 = new ArrayList<>();
        ArrayList<Float> result2 = new ArrayList<>();
        ArrayList<Float> smooth = new ArrayList<>();

        // Fill result1 with repeated values from the input map
        for (int i = 0; i < map.size(); i++) {
            int pcount = 32;
            while (pcount != 0) {
                result1.add(getValueAtIndex(map, i).floatValue());
                pcount--;
            }
        }

        // Smooth result1 by calculating moving averages
        for (int i = 0; i < result1.size(); i++) {
            float sum = result1.get(i);
            float n = 1;
            for (int j = 1; j < 32; j++) {
                if (i + j > result1.size() - 1) {
                    break;
                }
                sum += result1.get(i + j);
                n++;
            }
            float avg = sum / n;
            result2.add(avg);
        }

        // Further smooth result2 to obtain the final smoothed values
        for (int i = 0; i < result2.size(); i++) {
            float sum = result2.get(i);
            float n = 1;
            for (int j = 1; j < 32; j++) {
                if (i + j > result2.size() - 1) {
                    break;
                }
                sum += result2.get(i + j);
                n++;
            }
            float avg = sum / n;
            smooth.add(avg);
        }

        return smooth;
    }

    /**
     * Creates a HashMap representing smoothed locations based on original indices and smoothed values.
     *
     * @param list   The list of indices.
     * @param smooth The list of smoothed values corresponding to the indices.
     * @return A HashMap with smoothed locations (key: smoothed index, value: smoothed value).
     */
    public static HashMap<Integer, Float> smoothLocation(ArrayList<Integer> list, ArrayList<Float> smooth) {
        HashMap<Integer, Float> result = new HashMap<>();
        for (int x : list) {
            result.put((x * 16 + (x + 1) * 16), smooth.get((x * 16 + (x + 1) * 16)));
        }
        return result;
    }

}

