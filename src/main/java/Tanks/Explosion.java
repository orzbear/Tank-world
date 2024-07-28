package Tanks;

import java.util.ArrayList;
import java.util.HashMap;

public class Explosion {
    // Instance variables
    public String type;
    public float x;
    public float y;
    public float oldX;
    public float speed;
    public float angle;
    public int fuel;
    public int power = 50;
    public boolean exploding = false;
    public boolean destroying;
    public int frameCount;
    double PI = 3.14;
    private double time,deltaTime = 1.0/30.0;
    private static final double G = 3.6;
    public int radians;
    public int score = 0;

    /**
     * Constructor for creating an Explosion object.
     * 
     * @param type    Type of explosion.
     * @param x       X-coordinate of the explosion's center.
     * @param y       Y-coordinate of the explosion's center.
     * @param radians Number of radians for the explosion (used for calculations).
     */
    public Explosion(String type, float x, float y, int radians) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.frameCount = 0;
        this.exploding = true;
        this.radians = radians / 2; // Use half of the provided radians
    }


    /**
     * Update method to handle the state of the explosion.
     * Increases the frame count and updates the explosion state.
     */
    public void update() {
        if (exploding) {
            frameCount++;
            if (frameCount > 6) {
                // Stop exploding after 7 frames
                this.exploding = false;
                this.destroying = true; // Set destroying flag to true
            }
        }
    }

    /**
    * Computes the new surface based on the explosion's effect on the surface.
    *
    * @param surface The original surface heights represented as an ArrayList of Float values.
    * @return A HashMap representing the modified surface after the explosion effect.
    */
    public HashMap<Integer, Float> surfaceDestroy(ArrayList<Float> surface){
        // Initialize a new HashMap to store updated surface values
        HashMap<Integer, Float> newSurface = new HashMap<>();
        // Initialize variables for upper and lower y bounds
        float up = 0;
        float low = 0;

        // Check if the object is in the destroying state
        if (destroying) {
            // Number of points to sample around the explosion circumference
            int numSteps = radians*12;
            // Calculate the angle increment for each step
            double angleIncrement = 2*Math.PI / numSteps;
    
            // Iterate around the ellipse circumference
            for (int i = 1; i <= numSteps; i++) {
                // Calculate the angle for the current step
                double theta = i * angleIncrement;
                // Calculate the x-coordinate of the circle's edge at the current angle
                float circlex = (float) (this.radians * Math.cos(theta)) + x;
                // Calculate the y-coordinate of the circle's edge at the current angle
                float circley1 = (float) (this.radians * Math.sin(theta)) + y;
                // Calculate the vertical offset (y difference) from the circle's center
                float ydiff = 2 * Math.abs(circley1 - y);
                // Determine the upper and lower y bounds for the current angle
                if (i != radians*6 || i != radians*12 ) {
                    float circley2 = (float) (this.radians * Math.sin(theta)) + y + ydiff;
                    if (circley1 > circley2) {
                        up = circley2;
                        low = circley1;
                    } else {
                        up = circley1;
                        low = circley2;
                    }
                }
            // Convert x-coordinate to index
            int indexX = (int) circlex;

            
            // Check if y-coordinate of the ellipse is below or touching the surface at indexX
            if (indexX >= 0 && indexX < surface.size()) {
                // Calculate the y-coordinate of the surface at the current index
                float surfaceY = 640 - surface.get(indexX) * App.CELLSIZE;

                // Update the surface value based on the ellipse position
                if (i != radians*6 || i != radians*12) {
                    if (surfaceY < up && surfaceY < low) {
                        float d = 640 - surface.get(indexX) * App.CELLSIZE + ydiff;
                        newSurface.put(indexX, -((d) - 640) / App.CELLSIZE);
                    } else if (surfaceY > up && surfaceY < low) {
                        newSurface.put(indexX, -((low) - 640) / App.CELLSIZE);
                    } else if (surfaceY > up && surfaceY > low) {
                        newSurface.put(indexX, surface.get(indexX));
                    }
                } else {
                    // Handle specific cases for ellipse intersection with surface
                    if (surfaceY < circley1 || surfaceY == circley1) {
                        newSurface.put(indexX, -((circley1) - 640) / App.CELLSIZE);
                    }
                }
            }
        }
        // Set destroying to false after updating the surface
        this.destroying = false;
    }
        return newSurface;
    }


    /**
    * Computes the damage inflicted on a tank by the explosion based on its proximity to the explosion center.
    *
    * @param tankX The x-coordinate of the tank.
    * @param tankY The y-coordinate of the tank.
    * @param t     The Tank object that is potentially damaged by the explosion.
    * @return The calculated damage value based on the tank's proximity to the explosion.
    */
    public int explosionTankDmg(float tankX, float tankY, Tank t) {
        int dmg = 0;
    
        // Calculate the Euclidean distance between the tank and explosion center
        float distance = (float) Math.sqrt(Math.pow(tankX - this.x, 2) + Math.pow(tankY - this.y, 2));
    
        // Check if the tank is outside the explosion radius
        if (distance > this.radians / 2) {
            return dmg; // No damage if the tank is outside the explosion radius
        } else {
            // Calculate damage based on proximity to the explosion center
            dmg = (int) (60 - (60 / this.radians) * distance);
        }
    
        // Mark the tank as damaged for further processing
        t.damaged = true;
    
        return dmg; // Return the calculated damage value
    }

    /**
     * Draws the explosion on the game canvas.
     * 
     * @param app The main application object used for drawing.
     */
    public void draw(App app) {
        // Ensure that the explosion stays within the canvas boundaries
        if (this.y <= 0) {
            this.y = 639;
        }
        
        // Check if the explosion is in the exploding state
        if (exploding) {
            // Draw three concentric ellipses to simulate the explosion effect
            // Outermost ellipse
            app.fill(255, 0, 0);
            app.ellipse(x, y, frameCount * 10, frameCount * 10);
    
            // Middle ellipse
            app.fill(255, 180, 0);
            app.ellipse(x, y, frameCount * 5, frameCount * 5);
    
            // Innermost ellipse
            app.fill(255, 255, 0);
            app.ellipse(x, y, frameCount * 2, frameCount * 2);
        }
    }
}
