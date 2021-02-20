/*
BOMB RUNNER 

SP1_F2021 grid game

!OBS! You must install processings sound library in order for this sketch to work properly !OBS!

Made by: Aleksander Willersrud
*/


import processing.sound.*;
import java.util.HashSet;
import java.util.Collections;



Boolean debug = true;

/* Grid */
int gridLength = 46;
int gridSquareSize = 20;
int[][] grid = new int[gridLength][gridLength];

/* Game settings */
int frames = 10;
int gameIteration = 0;         // used for creating unique wall generation every time a player restarts
Boolean gamePaused = false;
Boolean gameOver = false;
Boolean useAstarPathfinding = true;

/* UI screens */
Boolean startMenu = true;
Boolean howToPlay = false;

/* Player movement */
Boolean movingLeft = false;
Boolean movingRight = false;
Boolean movingUp = false;
Boolean movingDown = false;

/* Player settings */
int playerHP = 3;
int playerScore = 0;
int highScore = 0;

/* Enemy settings */
int amountOfEnemies = 4;
int distanceToEnemy = 17;       // min distance used when generating random enemy spawn positions
int enemyDifficulty = 2;        // the higher the difficulty, the lower the chance of random enemy movement

/* Food settings */
int amountOfFood = 10;
int distanceToFood = 10;         // min distance used when generating random food spawn positions
int foodDifficulty = 3;         // the higher the difficulty, the lower the chance of random food movement
int safeDistance = 6;           // the distance a food will have to player before moving freely

/* Box settings */
int amountOfBoxs = 2;
int distanceToBox = 15;

/* Bomb settings */
ArrayList<Bombs> activeBombs = new ArrayList<Bombs>();
int startingBombs = 2;
int maxBombs = 3;
int bombExplodeTick = 12;

/* Explosion settings */
ArrayList<Explosions> activeExplosions = new ArrayList<Explosions>();
int explosionTickLength = 7;

/* Terrain settings */
float wallPerlinCap = 0.62;
float wallPerlinScale = 0.3;

/* Fonts */
PFont arcadeIn;

/* Components */
Player player;
Enemy[] enemies = new Enemy[amountOfEnemies];
Food[] foods = new Food[amountOfFood];
Box[] boxs = new Box[amountOfBoxs];

Pathfind pathfind;
PathfindGrid pathfindGrid;
PathfindNode pathfindNode;

Walls walls;

SoundFile soundtrack;

void setup() {
    size(1200, 921);        // 1100, 921
    frameRate(frames);

    /* Load Fonts */
    arcadeIn = createFont("arcadeIn.ttf", 100, false);
    textFont(arcadeIn);

    /* Load sprites */
    assignSprites();

    /* Display start menu */
    startMenuUI();
    
    /* Play soundtrack */
    println("Ignore the error below, it is caused by slow loading speed of soundtrack");
    soundtrack = new SoundFile(this, "data/soundtrack.wav");        // .wav files load quicker than .mp3 files
    soundtrack.play();
    soundtrack.loop();
    soundtrack.amp(0.8);

}


void init() {
   
    /* Create walls with perlin noise */
    walls = new Walls(wallPerlinCap, wallPerlinScale);
    walls.wallGeneration();

    /* Create game objects */
    player = new Player((int)gridLength/2, (int)gridLength/2, playerHP, playerScore, startingBombs, maxBombs);    // player starts in "middle" of grid

    for (int i = 0; i < enemies.length; i++) {          
        enemies[i] = new Enemy(player, distanceToEnemy, enemyDifficulty);                // create the enemies
        enemies[i].spawnEnemy();                                                         // generate a random start position for enemies
    }
    
    for (int i = 0; i < foods.length; i++) {
        foods[i] = new Food(player, distanceToFood, foodDifficulty, safeDistance);       // create the food
        foods[i].spawnFood();                                                            // generate a random start position for food
    }
    
    for (int i = 0; i < boxs.length; i++) {
        boxs[i] = new Box(distanceToBox);
        boxs[i].spawnBox();
    }
    
    /* Setup A star pathfinding */
    pathfindGrid = new PathfindGrid(gridLength);
    pathfindGrid.createGrid();
    pathfind = new Pathfind(pathfindGrid);
    
    background(190);
    gameOver = false;
    gamePaused = false;
}


void draw() {
    if (!gamePaused && !startMenu) {
        
        /* Handle player movement */
        if (movingLeft) player.moveLeft();
        if (movingRight) player.moveRight();
        if (movingUp) player.moveUp();
        if (movingDown) player.moveDown();

        /* Game logic */
        playGame();

        /* UI */
        updateUI(); 
    }

    if (gameOver && !startMenu) {
        gamePaused = true;
        gameOverUI();
    }
}
