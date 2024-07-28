package Tanks;

import java.math.*;

import org.checkerframework.checker.units.qual.radians;

import processing.core.PImage;

public class Tank {
    public String type;
    public float x;
    public float y;
    public float oldX;
    public float oldY;
    public float newY;
    public int hp = 100;
    private boolean moving;
    public float turretAngle ;
    private float turretPower;
    public float speed;
    public int fuel = 250;
    public int power ;
    public boolean onTurn = false;
    double PI = 3.14;
    public boolean turret = true;
    //public Projectile p = new Projectile(type, -1, -1);
    public float turretWidth = App.CELLSIZE /8*7;
    public float turretHeight = App.CELLSIZE / 8;
    public float turretCenterX ;
    public float turretCenterY;
    public float turretEndX ;
    public float turretEndY ;
    public float angle;
    public float angleChange;
    public boolean falling;
    public float falling_speed;
    public int parachute;
    public int score = 0;
    public boolean damaged;
    public boolean exploding;
    public boolean falldead = false;
    public boolean largeProjectile;
    
    /**
    * Represents a tank in the game.
    */
    /**
     * Constructs a new Tank object.
     *
     * @param type The type of tank.
     * @param x The initial x-coordinate of the tank.
     * @param y The initial y-coordinate of the tank.
     */
    public Tank(String type, float x, float y){
        this.type=type;
        this.x = x;
        this.y = y;
        this.turretCenterX = x;
        this.turretCenterY = 640-(y*App.CELLSIZE)-App.CELLSIZE/32*7-1;
        this.turretAngle = (float)PI;
        speed = 0; // Initial speed
        this.parachute = 3;
        this.power = 50;
    }
    
    /**
     * Updates the tank's position and state.
     */
    public void update() {
        if (this.x >863){this.speed = 0;}
        else if (this.x < 1){this.speed = 0;}
        else if (this.x + this.speed / App.FPS >863){this.speed = 0;}
        else if (this.x + this.speed / App.FPS< 1){this.speed = 0;}
        //else if (fuel <=0){this.speed = 0;}
        else{
            oldX = this.x;
            this.x += speed / App.FPS;
            if(falling && !falldead){
                if(this.parachute >0){this.falling_speed = 60;}
                this.y += this.falling_speed / App.FPS;
                if(this.y >= newY){this.falling = false;parachute--;}
                if(this.parachute<0){this.parachute=0;}
                this.y = -(this.y + App.CELLSIZE/32*7 - 640)/App.CELLSIZE;}
            else {this.y = App.smoothMountlevel.get(Math.round(this.x));}
            //float cost = (Math.abs(oldX-this.x));
            this.turretCenterX = x;
            this.turretCenterY = 640-(y*App.CELLSIZE)-App.CELLSIZE/32*7-1;
        }
    }

    /**
     * Initiates a falling action for the tank.
     *
     * @param oldY  The old y-coordinate.
     * @param newY  The new y-coordinate.
     * @param speed The falling speed.
     */
    public void falling(float oldY, float newY, float speed){
        this.falling = true;
        this.oldY = oldY;
        this.newY = newY;
        this.falling_speed = speed;
    }

    /**
     * Checks if a point is within the explosion range of the tank.
     *
     * @param px The x-coordinate of the point.
     * @param py The y-coordinate of the point.
     * @return True if the point is within the explosion range, otherwise false.
     */
    public boolean inExplode(float px, float py){
        return false;
    }

    
    public void stop() {
        speed = 0; // Set speed to 0 to stop the tank
    }

    /**
     * Adjusts the turret angle of the tank by a specified amount.
     *
     * @param angleChange The amount to change the turret angle by.
     */
    public void adjustTurretAngle(float angleChange) {
        if(angleChange <0){
            if (turretAngle + angleChange >0){turretAngle = (float)PI;turretAngle = normalizeAngle(turretAngle);}
            else{
                turretAngle += angleChange;
                turretAngle = normalizeAngle(turretAngle);
            }
        } else {
            if (turretAngle + angleChange < (float)PI){turretAngle = 0;turretAngle = normalizeAngle(turretAngle);}
            else{
                turretAngle += angleChange;
                turretAngle = normalizeAngle(turretAngle);
            }
        }
        turretEndX = turretCenterX - (turretWidth / 2) * App.cos(turretAngle);
        turretEndY = turretCenterY  - (turretWidth / 2) * App.sin(turretAngle);
    }


    /**
     * Moves the tank by setting its speed.
     *
     * @param speed The speed to set for the tank.
     */
    public void move(float speed) {   
        this.speed = speed;
    }

    /**
     * Adjusts the turret power of the tank by a specified amount.
     *
     * @param amount The amount to adjust the turret power by.
     */
    public void adjustTurretPower(float amount) {
        this.power += amount/30.0;
        if(this.power > this.hp){this.power = this.hp;}
        if(this.power < 0){this.power = 0;}
    }

    /**
     * Fires a projectile from the tank.
     *
     * @param p The projectile to fire.
     */
    public void fireProjectile(Projectile p) {
        // Implement projectile firing logic
        p.x = this.x;
        p.y = this.y;
    }

    public void draw(App app){
        // Ensure the angle is between 0 and 180 degrees
        // Calculate the positions of the turret's corners based on the angle
        float halfTurretWidth = turretWidth / 2;
        float halfTurretHeight = turretHeight / 2;

        float turretTopLeftX = turretCenterX - halfTurretWidth * App.cos(turretAngle) - halfTurretHeight * App.sin(turretAngle);
        float turretTopLeftY = turretCenterY - halfTurretWidth * App.sin(turretAngle) + halfTurretHeight * App.cos(turretAngle);

        float turretTopRightX = turretCenterX + halfTurretWidth * App.cos(turretAngle) - halfTurretHeight * App.sin(turretAngle);
        float turretTopRightY = turretCenterY + halfTurretWidth * App.sin(turretAngle) + halfTurretHeight * App.cos(turretAngle);

        float turretBottomLeftX = turretCenterX - halfTurretWidth * App.cos(turretAngle) + halfTurretHeight * App.sin(turretAngle);
        float turretBottomLeftY = turretCenterY - halfTurretWidth * App.sin(turretAngle) - halfTurretHeight * App.cos(turretAngle);

        float turretBottomRightX = turretCenterX + halfTurretWidth * App.cos(turretAngle) + halfTurretHeight * App.sin(turretAngle);
        float turretBottomRightY = turretCenterY + halfTurretWidth * App.sin(turretAngle) - halfTurretHeight * App.cos(turretAngle);

        // Draw the rectangle using the moving average as the x-coordinate
        // Calculate the midpoints of the turret's sides
        float turretMidTopX = (turretTopLeftX + turretTopRightX) / 2;
        float turretMidTopY = (turretTopLeftY + turretTopRightY) / 2;
        float turretMidBottomX = (turretBottomLeftX + turretBottomRightX) / 2;
        float turretMidBottomY = (turretBottomLeftY + turretBottomRightY) / 2;

        // Draw the left half of the rectangle in grey
        app.fill(100,100,100); 
        app.quad(turretTopLeftX, turretTopLeftY, turretMidTopX, turretMidTopY, turretMidBottomX, turretMidBottomY, turretBottomLeftX, turretBottomLeftY);
        
        app.fill(App.tankColors.get(type)[0],App.tankColors.get(type)[1],App.tankColors.get(type)[2]);
        app.rect(x-App.CELLSIZE/4, 640-(y*App.CELLSIZE)-App.CELLSIZE/16*3, App.CELLSIZE/2, App.CELLSIZE/4);

        this.y = 640-(y*App.CELLSIZE)-App.CELLSIZE/32*7;
        app.fill(App.tankColors.get(type)[0],App.tankColors.get(type)[1],App.tankColors.get(type)[2]);
        app.ellipse(x, y, App.CELLSIZE/3, App.CELLSIZE/3);  
    }

    public static float normalizeAngle(float angle) {
        // Normalize angle to be within the range of 0 to 2π (0 to 360 degrees)
        while (angle < 0) {
            angle +=  Math.PI; // Add 2π until angle is positive
        }
        while (angle >= Math.PI) {
            angle -= Math.PI; // Subtract 2π until angle is less than 2π
        }
        return angle;
    }
}
