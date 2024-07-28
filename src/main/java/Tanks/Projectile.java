package Tanks;

public class Projectile {
    public String type; // Type of projectile
    public float x; // Current x-coordinate of the projectile
    public float y; // Current y-coordinate of the projectile
    public float speed; // Speed of the projectile
    public float angle; // Angle of the projectile's trajectory
    public int fuel; // Fuel remaining for the projectile
    public int power = 50; // Power of the projectile
    public boolean moving = false; // Flag indicating if the projectile is currently moving
    private double time, deltaTime = 1.0 / 30.0; // Time variables for projectile movement
    private static final double G = 7.2; // Acceleration due to gravity
    public boolean exploding = false; // Flag indicating if the projectile is exploding
    public float wind; // Wind affecting the projectile's movement
    public boolean largeProjectile; // Flag indicating if the projectile is large

    /**
     * Constructs a new Projectile object.
     *
     * @param type            The type of projectile.
     * @param x               The initial x-coordinate of the projectile.
     * @param y               The initial y-coordinate of the projectile.
     * @param angle           The initial angle of the projectile's trajectory.
     * @param largeProjectile Flag indicating if the projectile is large.
     */
    public Projectile(String type, float x, float y, float angle, boolean largeProjectile) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.largeProjectile = largeProjectile;
    }

    /**
     * Initiates movement of the projectile.
     *
     * @param speed The initial speed of the projectile.
     * @param wind  The wind affecting the projectile's movement.
     */
    public void move(float speed, float wind) {
        this.moving = true;
        this.wind = (float) (wind * 0.03 * deltaTime);
        this.speed = 2 * (float) speed;
    }

    /**
     * Stops the movement of the projectile.
     */
    public void stop() {
        this.speed = 0;
        this.moving = false;
    }

    /**
     * Updates the position of the projectile based on its movement parameters.
     */
    public void update() {
        if (moving) {
            float dx = (float) (speed * Math.cos(angle));
            float dy = (float) (speed * Math.sin(angle));
            x -= dx + wind;
            y -= dy - (G * time);
            time += deltaTime;

            int numSteps = 360; // Number of points to sample around the ellipse circumference
            double angleIncrement = 2 * Math.PI / numSteps;

            // Iterate around the ellipse circumference
            for (int i = 0; i < numSteps; i++) {
                double theta = i * angleIncrement;
                float circlex = (float) (4 * Math.cos(theta)) + x;
                float circley = (float) (4 * Math.sin(theta)) + y;

                // Convert x-coordinate to index
                int indexX = (int) circlex;

                // Check if y-coordinate of the ellipse is below or touching the surface at indexX
                if (indexX >= 0 && indexX < App.smoothMountlevel.size()) {
                    float surfaceY = 640 - App.smoothMountlevel.get(indexX) * App.CELLSIZE;
                    if (circley >= surfaceY) {
                        stop(); // Ellipse is touching or below the surface
                    }
                }
            }
        }
    }

    /**
     * Draws the projectile on the application canvas.
     *
     * @param app The main application object used for drawing.
     */
    public void draw(App app) {
        app.fill(App.tankColors.get(type)[0], App.tankColors.get(type)[1], App.tankColors.get(type)[2]);
        app.ellipse(x, y, 8, 8); // Draw projectile as a small ellipse
    }
}
