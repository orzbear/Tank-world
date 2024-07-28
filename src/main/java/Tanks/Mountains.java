package Tanks;

import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Mountains {

    // Instance variables
    public float x;  // x-coordinate of the mountain
    private float height; // height of the mountain (in game units)
    
     /**
     * Constructor to initialize a Mountains object.
     * 
     * @param x      The x-coordinate of the mountain.
     * @param height The height of the mountain.
     */
    public Mountains(float x, float height){
        this.x = x;
        this.height = height;
    }

    /**
     * Getter method to retrieve the height of the mountain.
     * 
     * @return The height of the mountain.
     */
    public double getHeight(){
        return this.height;
    }

    /**
     * Draws the mountain on the game canvas.
     * 
     * @param app The main application object used for drawing.
     */
    public void draw(App app){
        // Draw the rectangle using the moving average as the x-coordinate
        app.fill(App.groundColors[0], App.groundColors[1], App.groundColors[2]);
        app.rect(x, 640 - height * App.CELLSIZE, 1, height* App.CELLSIZE);
    }
}
