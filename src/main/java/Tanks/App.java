package Tanks;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;
    public JSONObject config;
    public int selectedLevelIndex = 1;
    public String directoryPath = "./";
    public int levelSize; 
    public String selectedLevel;

    public static Random random = new Random();
    public PImage backgroundImage;
    public HashMap<Integer, Integer> mountlevel;
    public static HashMap<Integer, Float> treeLocation;
    public HashMap<Integer, Float> tankLocation;    
    public static ArrayList<Float> smoothMountlevel = new ArrayList<>();
    public ArrayList<Mountains> smoothMountains;

    public ArrayList<Tank> tanks;
    public ArrayList<Projectile> pjs;
    public ArrayList<Explosion> explodes;
    public ArrayList<Explosion> selfExplodes;
    public int playercount;
    public String[] playerTypes = {"A","B","C","D","E","F","G","H","I",
                                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    public String[] aiTypes =  {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    public static HashMap<String, int[]> tankColors = new HashMap<>();
    public static Integer[] groundColors = new Integer[3];; 
    public ArrayList<String> turns = new ArrayList<>();
    public int currentPlayerIndex = 0;
    public boolean onTurn = false;
    public float cost;
    public Projectile p;
    public static boolean fire = false;
    public int frameCount = 0;
    public int damage;
    public float windSpeed;

    private int levelTransitionTime = 1000; // Transition time in milliseconds (1 second)
    private int transitionStartTime = 0; // Time when the transition started
    private boolean waitingForInput = false; // Flag to indicate whether waiting for user input

    private int maxLevels = 3; // Total number of levels
    private boolean gameEnded = false; // Flag to indicate if the game has ended
    public Map<String, Integer> tankScores = new HashMap<>();
    public String highestScoringTankType = null;
    public int highestScore = 0;
    public List<Map.Entry<String, Integer>> tankScoreList = new ArrayList<>(tankScores.entrySet());
    public int delayFrames = 21;

    public PImage treeImage;  // Declare a PImage variable to hold the image
    public PImage fuelImage;  // Declare a PImage variable to hold the image
    public PImage paraImg; 
    public PImage windImage1;
    public PImage windImage2; 
	

    public App() {
       this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        // Set frame rate
        frameRate(FPS);

        // Load configuration file
        LevelLoader counter = new LevelFileCounter();
        levelSize = counter.countLevelFiles(directoryPath);
        
        loadLevelSettings(selectedLevelIndex);    
     
    }

    private void loadLevelSettings(int levelIndex) {
        // Construct the filename for the selected level
        selectedLevel = "level" + levelIndex + ".txt";

        //String selectedLevel = "level"+selectedLevelIndex+".txt";
        this.config = loadJSONObject(configPath);

        // Get selected level information
        JSONArray levels = config.getJSONArray("levels");
        for (int i = 0; i < levels.size(); i++) {
            JSONObject level = levels.getJSONObject(i);
            String layout = level.getString("layout");
            if (layout.equals(selectedLevel)) {
                selectedLevelIndex = i;
                break;
            }
        }

        // Get configuration for the selected level
        JSONObject selectedLevelConfig = levels.getJSONObject(selectedLevelIndex);
        selectedLevelIndex ++;

        // Load background image, colors, and tree image
        String background = selectedLevelConfig.getString("background");
        String foregroundColour = selectedLevelConfig.getString("foreground-colour");
        String trees;
        if (selectedLevelConfig.hasKey("trees")) {
            trees = selectedLevelConfig.getString("trees");
        } else {
            trees = "tree1.png"; // Default value
        }

        // Split the foreground color string into RGB components
        String[] rgbComponents = foregroundColour.split(",");
        groundColors[0] = Integer.parseInt(rgbComponents[0]);  // Red component
        groundColors[1] = Integer.parseInt(rgbComponents[1]);  // Green component
        groundColors[2] = Integer.parseInt(rgbComponents[2]);  // Blue component        

        // Load and resize background image
        backgroundImage = loadImage("src/main/resources/Tanks/" + background);
        backgroundImage.resize(width, height);

        // Read level data
        Readinglevel.readLvs(selectedLevel);

        // Smooth mountain levels and object locations
        smoothMountlevel = Smooth.smoothLine(Readinglevel.getHeights());
        treeLocation = Smooth.smoothLocation(Readinglevel.getTrees(), smoothMountlevel);
        tankLocation = Smooth.smoothLocation(Readinglevel.getTanks(), smoothMountlevel);

        // Create smooth mountains
        smoothMountains = new ArrayList<>();
        for (int i = 0; i < smoothMountlevel.size(); i++) {
           Mountains newMountain = new Mountains(i, smoothMountlevel.get(i));
           smoothMountains.add(newMountain);
        }
        // Load tree image
        treeImage = loadImage("src/main/resources/Tanks/" + trees);

        // Load player colors
        JSONObject player_colours = config.getJSONObject("player_colours");
        for (String name : playerTypes) {
            String s = player_colours.getString(name);
            Random random = new Random();
            int[] randoms = {random.nextInt(256), random.nextInt(256), random.nextInt(256)};
            if (s.equals("random")) {
                tankColors.put(name, randoms);
            } else {
                String[] parts = s.split(",");
                int[] colorArray = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    colorArray[i] = Integer.parseInt(parts[i]);
                }
                tankColors.put(name, colorArray);
            }
        }
        //reset hp fuel and others
        if (levelIndex > 1) {
            // Update scores for existing tanks from previous level
            for (Tank t : tanks) {
                t.hp = 100;
                t.power = 50;
                t.parachute = 3;
                t.fuel = 250;
                t.falling = false;
                t.falldead = false;
            }
            //reset turns and start location
            currentPlayerIndex = 0;
            playercount = 0;
            for (Map.Entry<Integer, Float> entry : tankLocation.entrySet()) {
                Integer x = entry.getKey();
                Float y = entry.getValue();
                for (Tank t : tanks) {
                    if (t.type == playerTypes[playercount]){
                        t.x = x;
                        t.y = y;
                    }
                }
                playercount++;
            }    
        } else {
            // Initialize tanks for the first level
            // Create tanks
            tanks = new ArrayList<>();
            playercount = 0;
            for (Map.Entry<Integer, Float> entry : tankLocation.entrySet()) {
                Integer x = entry.getKey()-16;
                Float y = entry.getValue();
                Tank newTank = new Tank(playerTypes[playercount], x, y);
                tanks.add(newTank);
                playercount++;
            }
        }

        
        // Initialize turns
        turns = new ArrayList<>();
        for (Tank t : tanks) {
            turns.add(t.type);
        }

        //Initialize fuels
        //fuel = 250;
        fuelImage = loadImage("src/main/resources/Tanks/fuel.png");

        //Initialize projectiles
        pjs = new ArrayList<>();

        //Initialize explosions
        explodes = new ArrayList<>();
        selfExplodes = new ArrayList<>();
        paraImg = loadImage("src/main/resources/Tanks/parachute.png");

         // Initialize wind with a random value between -35 and 35
         Random rand = new Random();
         windSpeed = rand.nextInt(71) - 35;
         windImage1 = loadImage("src/main/resources/Tanks/wind.png");
         windImage2 = loadImage("src/main/resources/Tanks/wind-1.png");
    }

    public void updateLevel() {
        //next level index and check
        int newselectedLevelIndex = selectedLevelIndex + 1;
        if (!gameEnded && checkingWinner()) {
            if (newselectedLevelIndex > levelSize) {
                gameEnded = true;
                endGame(); // End the game if all levels are completed
            } else {
                // Check if transition to the next level is pending
                if (!waitingForInput && millis() - transitionStartTime >= levelTransitionTime) {
                    this.selectedLevelIndex = newselectedLevelIndex; // Move to the next level
                    loadLevelSettings(this.selectedLevelIndex);
                    waitingForInput = true; // Set flag to wait for user input after level transition
                    // Reset transition start time for next level
                    transitionStartTime = millis();
                }
    
                // Check for user input after level transition
                if (waitingForInput && key == ' ') {
                    this.selectedLevelIndex = newselectedLevelIndex; // Move to the next level
                    loadLevelSettings(this.selectedLevelIndex);
                    waitingForInput = false; // Reset flag after moving to the next level
                }
            }
        } else {
            // Reset transition start time if no winner yet
            transitionStartTime = millis();
            waitingForInput = false; // Reset flag when checking for winner again
            // System.out.println("No winner yet...");
        }
    }
    

    private void endGame() {
        // Determine the winner based on scores
        // Populate map with tank types and scores
        for (Tank t : tanks) {
            tankScores.put(t.type, t.score);
        }
        
        // Find tank type with the highest score
        for (Map.Entry<String, Integer> entry : tankScores.entrySet()) {
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                highestScoringTankType = entry.getKey();
            }
        }

        if (highestScoringTankType != null) {
            System.out.println("Tank type with the highest score: " + highestScoringTankType);
            System.out.println("Highest score: " + highestScore);
        }

        // Sort tank types by score (descending order)
        tankScoreList = new ArrayList<>(tankScores.entrySet());
        tankScoreList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        // Display tank types sorted by score
        System.out.println("Tank types sorted by score (descending order):");
        for (Map.Entry<String, Integer> entry : tankScoreList) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void restartGame() {
        gameEnded = false;
        selectedLevelIndex = 1;
        loadLevelSettings(selectedLevelIndex); // Load the first level settings
        // Reset player scores and any other game state if needed
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode(); // Get the key code from the event
        for (Tank t : tanks) {
            if (turns.get(currentPlayerIndex % 4).equals(t.type) && t.hp > 0) {
                t.onTurn = true;
            } else {
                t.onTurn = false;
            }
            if (t.onTurn) {
                // Adjust turret angle
                if (keyCode == UP) {
                    t.adjustTurretAngle(3);
                } else if (keyCode == DOWN) {
                    t.adjustTurretAngle(-3);
                } else if (keyCode == LEFT && t.fuel > 0) {
                    // Move left
                    if (t.x > 0) {
                        t.move(-60);
                        if (t.x + t.speed / FPS < 1) {
                            t.speed = 0;
                        } else {
                            t.oldX = t.x;
                            t.x = max(1, t.x + t.speed / FPS);
                            cost = Math.abs(t.oldX - t.x);
                        }
                    } else {
                        t.speed = 0;
                    }
                    if (t.speed == 0) {
                        cost = 0;
                    }
                } else if (keyCode == RIGHT && t.fuel > 0) {
                    // Move right
                    if (t.x < 864) {
                        t.move(60);
                        if (t.x + t.speed / FPS > 863) {
                            t.speed = 0;
                        } else {
                            t.oldX = t.x;
                            t.x = min(863, t.x + t.speed / FPS);
                            cost = Math.abs(t.oldX - t.x);
                        }
                    } else {
                        t.speed = 0;
                    }
                    if (t.speed == 0) {
                        cost = 0;
                    }
                } else if (keyCode == 's' || keyCode == 'S') {
                    // Decrease turret power
                    t.adjustTurretPower(-36);
                } else if (keyCode == 'w' || keyCode == 'W') {
                    // Increase turret power
                    t.adjustTurretPower(36);
                } else if (keyCode == ' ') {
                    // Fire projectile
                } else if (keyCode == 'r' || keyCode == 'R' && t.score >= 20 && t.hp != 100) {
                    // Repair kit
                    t.hp = Math.min(100, t.hp + 20);
                    t.score -= 20;
                } else if (keyCode == 'r' || keyCode == 'R' && gameEnded) {
                    // gameEnded
                    restartGame();
                    currentPlayerIndex = 0;
                    gameEnded = false;
                } else if (keyCode == 'f' || keyCode == 'F' && t.score >= 10) {
                    // Additional fuel
                    t.score -= 10;
                    t.fuel += 200;
                } else if (keyCode == 'p' || keyCode == 'P' && t.score >= 15) {
                    // Additional parachute
                    t.score -= 15;
                    t.parachute += 1;
                } else if (keyCode == 'x' || keyCode == 'X' && t.score >= 20) {
                    // Larger projectile
                    t.score -= 20;
                    t.largeProjectile = true;
                }
                
            }
        }
    }   
    
    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased() {
        for (Tank t : tanks) {
            if (t.hp > 0) {
                if (keyCode == LEFT || keyCode == RIGHT) {
                    t.stop(); // Stop the tank when LEFT or RIGHT key is released
                    cost = 0;
                }
                if (key == ' ') {
                    if (turns.get(currentPlayerIndex % 4).equals(t.type) && t.hp > 0) {
                        Projectile pj = new Projectile(t.type, t.x, t.y, t.turretAngle, t.largeProjectile);
                        pjs.add(pj);
                        pj.move((8 * t.power / 100) + 1, windSpeed);
                        t.largeProjectile =false;
                    }
                }
            }
        }
    
        if (key == ' ') {
            //Setting winds
            Random rand = new Random();
            float windChange = rand.nextInt(11) - 5; // Random integer between -5 and 5
            windSpeed += windChange;
    
            // Limit wind speed to -35 to 35 range
            windSpeed = constrain(windSpeed, -35, 35);
            // Change turns
            currentPlayerIndex ++;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /*
     * Define the process of exploding for explosions
     */
    public void explodingProcess(Explosion e) {
        // Draw the explosion
        e.draw(this);
        
        // Update the explosion state
        e.update();
        
        // Process the explosion aftermath if the explosion has stopped
        if (!e.exploding) {
            // Calculate new surface after explosion
            HashMap<Integer, Float> newSurface = e.surfaceDestroy(smoothMountlevel);
            
            // Process each affected surface point
            for (Integer i : newSurface.keySet()) {
                // Calculate old and new locations for the surface point
                float oldlocation = 640 - (smoothMountlevel.get(i) * App.CELLSIZE) - App.CELLSIZE / 32 * 7;
                float newlocation = 640 - (newSurface.get(i) * App.CELLSIZE) - App.CELLSIZE / 32 * 7;
                
                // Update smoothMountlevel and treeLocation with new surface data
                smoothMountlevel.set(i, newSurface.get(i));
                treeLocation.replace(i, newSurface.get(i));
                smoothMountains.set(i, new Mountains(i, newSurface.get(i)));
                
                // Process tanks affected by the explosion
                for (Tank t : tanks) {
                    if (!t.damaged) {
                        // Calculate damage to tank from explosion
                        int explodeDmg = e.explosionTankDmg(t.x, t.y, t);
                        if (t.hp - explodeDmg > 0) {
                            t.hp -= explodeDmg;
                        } else {
                            explodeDmg = t.hp;
                            t.hp = 0;
                        }
                        // Update explosion score based on tank damage
                        if (t.type != e.type){
                            e.score += explodeDmg;
                            explodeDmg = 0;
                        }else{
                            explodeDmg = 0;
                        }
                    }
                    
                    if (t.x == i && !t.falling) {
                        // Make tanks fall if they are directly above affected surface
                        t.falling(oldlocation, newlocation, 120);
                        if (t.parachute < 1) {
                            // Calculate additional damage if tanks are falling without parachutes
                            damage = (int) (640 - (newSurface.get(i)) * CELLSIZE - t.y);
                            if (t.hp - damage > 0) {
                                t.hp -= damage;
                            } else {
                                damage = t.hp;
                                t.hp = 0;
                            }
                            if (t.hp <= 0) {
                                t.hp = 0;
                                t.power = 0;
                                t.onTurn = false;
                            }
                        }
                        // Update explosion score based on additional damage
                        if (t.type != e.type){
                            e.score += damage;
                            damage = 0;
                        }else{
                            damage = 0;
                        }
                    }
                }
            }
            
            // Draw updated smooth mountains
            for (Mountains m : smoothMountains) {
                for (Integer i : newSurface.keySet()) {
                    if (m.x == i) {
                        m.draw(this);
                    }
                }
            }
            
            // Reset damaged status and adjust tank power
            for (Tank t : tanks) {
                t.damaged = false;
                if (t.power > t.hp) {
                    t.power = t.hp;
                }
            }
        }
    }
    
    public boolean checkingWinner(){
        int count = 0;
        for(Tank t: tanks){
            if(t.hp<=0){
                count++;
                //System.out.println(count);
            }
        } 
        //System.out.println(count);
        return(count>=3);
    }

    /*
     * Draw Horizontal Lines
     */
    public void drawHorizontalLine(int x, int y, int length) {
        stroke(0);  // Set line color to black
        strokeWeight(3);  // Set line thickness
        line(x, y, x + length, y);  // Draw the line
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
         // Disable stroke for drawing
        this.noStroke();

        // Draw background image
        background(backgroundImage);

        // Draw mountains
        for (Mountains m: smoothMountains) {
            m.draw(this);
        }

        // Process and draw projectiles
        for (Projectile p :pjs){
            if(p.moving == true){
                // Update and draw active projectiles
                p.exploding = false;
                p.update();
                p.draw(this);
            } else {
                if (!p.exploding){
                    // Create explosion when projectile stops
                    p.exploding = true;
                    if (p.largeProjectile) {
                        Explosion e = new Explosion(p.type,p.x, p.y, 120);
                        explodes.add(e);
                        p.largeProjectile = false;   
                    } else {
                        Explosion e = new Explosion(p.type,p.x, p.y, 60);
                        explodes.add(e);
                    }
                } else {
                    // Process explosion effects
                    for (Explosion e : explodes) {
                        if (e.x == p.x && e.y == p.y) {
                            explodingProcess(e);
                        }
                        // Update tank scores based on explosion type
                        for (Tank tt : tanks) {
                            if (tt.type.equals(e.type)) {
                                tt.score += e.score;
                                e.score = 0;
                            }
                        }
                    }
                }
            }
        }

        //Draw trees
        for (Map.Entry<Integer, Float> entry : treeLocation.entrySet()) {
            Integer x = entry.getKey();
            Float y = entry.getValue();
            image(treeImage, x - 16-6, 640 - (y * CELLSIZE) - 32, 32, 32);
        }
            
        // Draw tanks and handle tank behavior
        for (Tank t : tanks) {
            if (t.hp > 0) {
                // Check if fuel is depleted
                if (t.fuel <= 0) {
                    t.speed = 0;
                }
                if (t.y > 639) {
                    // Tank has hit the ground and falls
                    t.hp = 0;
                    t.parachute = 0;
                    t.falling = true;
                    t.falldead = true;
                    // Create self-explosion when tank hits the ground
                    if (!t.exploding) {
                        t.exploding = true;
                        Explosion se = new Explosion(t.type, t.x, t.y, 60);
                        selfExplodes.add(se);
                    } else {
                        // Process self-explosion effects
                        for (Explosion e : selfExplodes) {
                            if (e.x == t.x && e.y == t.y) {
                                explodingProcess(e);
                            }
                        }
                    }
                } else {
                    // Update and draw tank
                    t.update();
                    t.draw(this);
                    t.exploding = false;
                    // Draw parachute if tank is falling and still have parachute
                    if (t.falling && t.parachute > 0) {
                        image(paraImg, t.x - 15, t.y - 35, 32, 32);
                    }
                }
            } else {
                // Handle tank explosion when tank is destroyed
                if (!t.exploding) {
                    t.exploding = true;
                    Explosion se = new Explosion(t.type, t.x, t.y, 30);
                    selfExplodes.add(se);
                } else {
                    // Process self-explosion effects
                    for (Explosion e : selfExplodes) {
                        if (e.x == t.x && e.y == t.y) {
                            explodingProcess(e);
                            //System.out.println(e.y);
                        }
                    }
                }
            }
        }

        for (Tank t : tanks) {
            if (turns.get(currentPlayerIndex % 4).equals(t.type)) {
                if (t.fuel - cost < 1) {
                    t.fuel = 0;
                } else {
                    t.fuel -= cost;
                }

                float arrowHeight =20; // Height of the arrow relative to canvas height
                float arrowWidth = 20; // Width of the arrow relative to canvas width

                // Calculate the arrow's position relative to the moving object
                float arrowX = t.x; // X-coordinate of the arrow
                float arrowY = t.y - 60; // Y-coordinate of the arrow (adjusted to be below the object)
        
                // Draw arrow body (vertical line)
                stroke(0); // Set stroke color to black
                strokeWeight(2); // Set stroke weight
                line(t.x, t.y - 60 - arrowHeight / 2-50, t.x, t.y - 60 + arrowHeight / 2);
        
                // Draw arrowhead (triangle)
                noStroke(); // Disable stroke for arrowhead
                fill(0); // Set fill color to black
        
                line(arrowX, arrowY, arrowX, arrowY + arrowHeight); // Draw vertical line for arrow body

                // Draw arrowhead (triangle)
                noStroke(); // Disable stroke for arrowhead
                fill(0); // Set fill color to black

                // Calculate coordinates for the arrowhead triangle
                float arrowheadX1 = arrowX - arrowWidth / 2; // Left corner of the arrowhead base
                float arrowheadY = arrowY + arrowHeight; // Base (top) of the arrowhead
                float arrowheadX2 = arrowX + arrowWidth / 2; // Right corner of the arrowhead base

                triangle(arrowX, arrowheadY, arrowheadX1, arrowY, arrowheadX2, arrowY); // Draw triangle for arrowhead 

            }
        }

        
              
        //----------------------------------
        //display HUD:
        //----------------------------------
        // Draw fuel image at (10, 10) with size 32x32
        image(fuelImage, 10, 10, 32, 32);

        // Display player's turn information
        text("|  Player "+ turns.get(currentPlayerIndex%4)+ "'s turn", 102, 32);

        // Display tank-specific information for the current player
        for(Tank t: tanks){
            if (t.type.equals(turns.get(currentPlayerIndex%4))){

                // Draw Power up shot
                float r = random(0, 255); // Random red value (0 to 255)
                float g = random(0, 255); // Random green value (0 to 255)
                float b = random(0, 255); // Random blue value (0 to 255)

                if (t.largeProjectile){
                    fill(r, g, b);
                    textSize(30);
                    text("Power up!", 80, 80);
                }

                // Draw health bar
                fill(0, 0, 255); 
                rect(340, 16,  t.hp*2, 20); // Health bar
                noFill(); 
                stroke(0);
                strokeWeight(3); 
                rect(340, 16, 200, 20); // Outline for health bar
                fill(0, 0, 0);
                textSize(16);
                text("Health ", 285, 25);
                text(t.hp, 548, 25);

                // Display tank's power
                text("Power: "+t.power, 290, 64);
                noFill(); 
                stroke(128);
                rect(340, 16, t.power*2, 20);// Power bar
                noStroke();
                fill(255, 0, 0); 
                rect(340+t.power*2, 10, 1, 36); // Separator for power bar

                // Set font properties for text
                PFont boldFont = createFont("Arial Bold", 16);
                textFont(boldFont);
                textSize(16);

                // Draw fuel level text next to fuel image
                fill(0, 0, 0);
                text(": "+(int)t.fuel, 42, 32);

                // Display parachute image and parachute count
                image(paraImg, 10, 60, 32, 32);
                fill(0, 0, 0);
                text(": "+t.parachute, 40, 80);
            }
        }
        //TODO

        //----------------------------------
        //display scoreboard:
        //----------------------------------
        // Draw top border
        pushStyle();
        int tableWidth = 150;
        int cellHeight = 30;
        int lineHeight = 4;
        int offsetX = 120;  // Offset from right edge

        // Calculate positions for top right corner
        int startX = width - tableWidth - offsetX;
        int startY = 40;

        // Draw top border
        drawHorizontalLine(startX, startY, tableWidth);

        // Draw table title
        fill(0);
        textAlign(CENTER, CENTER);
        textSize(20);
        text("Scores", startX + tableWidth / 2, startY - 20);

        // Draw player rows
        String[] players = turns.toArray(new String[0]);
        for (int i = 0; i < players.length; i++) {
            int y = startY + i * cellHeight;
        
           
            
        
        // Draw score
        for (Tank t: tanks){
                if(t.type ==players[i]){
                    PFont boldFont1 = createFont("Arial Bold", 16);
                    textFont(boldFont1);

                     // Draw player name
                    fill(tankColors.get(t.type)[0],App.tankColors.get(t.type)[1],App.tankColors.get(t.type)[2]);
                    textAlign(LEFT, CENTER);
                    textSize(16);
                    text("Player "+players[i], startX + 20, y + cellHeight / 2);  // Center vertically within cell


                    fill(tankColors.get(t.type)[0],App.tankColors.get(t.type)[1],App.tankColors.get(t.type)[2]);
                    int score = (int)t.score;
                    textAlign(RIGHT, CENTER);
                    text(score, startX + tableWidth - 20, y + cellHeight / 2);  // Center vertically within cell
                }
            }        
        
        // Draw horizontal line below each row
        drawHorizontalLine(startX, y + cellHeight, tableWidth);  // Use y + cellHeight for line position
        }
    
        popStyle();

        //Draw Windspeed
        int windIconSize = 40;
        int windIconX = width - windIconSize - 40; // Position from the right edge
        int windIconY = 20; // Position from the top edge

        //Draw Windspeed icon based on the direction of the wind
        if (windSpeed > 0) {
            image(windImage1, windIconX, windIconY, windIconSize, windIconSize); // Display windImage1 for positive wind
        } else {
            image(windImage2, windIconX, windIconY, windIconSize, windIconSize); //Display windImage2 for negative wind
        }

        //Setting fonts and writing Windspeed information
        PFont boldFont1 = createFont("Arial Bold", 16);
        textFont(boldFont1);
        fill(0);
        textAlign(LEFT, CENTER);
        textSize(16);
        text(Math.abs((int)windSpeed), windIconX+50, windIconY+15);
		//----------------------------------
        //----------------------------------

        // Initialize a variable to keep track of the next tank index with hp > 0    
        int nextTankIndex = (currentPlayerIndex) % 4; // Start with the next tank after currentPlayerIndex

        // Find the next tank with hp > 0
        while (tanks.get((nextTankIndex) % 4).hp == 0) {
            nextTankIndex = (nextTankIndex + 1) % 4; // Move to the next tank
        }

        // Update currentPlayerIndex to the tank index with hp > 0
        currentPlayerIndex = nextTankIndex; 

        if(tanks.get(currentPlayerIndex).hp <= 0){
            currentPlayerIndex ++;
        }

        // Dealing with end game logic
        if(!gameEnded){
          updateLevel();  
        }else{
            // Calculating socres and generate the final result output
            int endstartY = 100;
            int boxHeight = 40;
           
            frameCount++;



           for (int i = 0; i < tankScoreList.size(); i++) {
               String tankType = tankScoreList.get(i).getKey();
               int score = tankScoreList.get(i).getValue();
           
               // Calculate y-position for displaying score
               int yPosition = endstartY + i * (boxHeight + 10);
           
               // Retrieve the RGB color associated with the tank type from tankColors map
               int[] colorRGB = tankColors.get(tankType);
               int[] colorRGBWin = tankColors.get(tankScoreList.get(0).getKey());

                // Determine the target frame for displaying this score
                int targetFrame = i * delayFrames;
                
                stroke(0);
                fill((int)(colorRGBWin[0] ), (int)(colorRGBWin[1] * 1.5), (int)(colorRGBWin[2] * 1.5), 80);
                rect(330, endstartY-20, 200, 200);

                fill(colorRGBWin[0] ,colorRGBWin[1],colorRGBWin[2]);
                textSize(40); // Set text size
                textAlign(LEFT, CENTER);
                text("Player " + tankScoreList.get(0).getKey() + " wins!" , 342, 300);

                

               // Draw box filled with color corresponding to tank type
               if (frameCount >= targetFrame) {
                // Draw box filled with color corresponding to tank type
                fill(colorRGB[0], colorRGB[1], colorRGB[2]);
                textSize(20); // Set text size
                textAlign(LEFT, CENTER);
                text("Player " + tankType + ": " + score, 342, yPosition); // Adjust x-position for centering
            }
           } 
        }
        

        //End
        // Display scores

    }



    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
