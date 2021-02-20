import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 
import java.util.HashSet; 
import java.util.Collections; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BombRunner extends PApplet {

/*
BOMB RUNNER 

SP1_F2021 grid game

!OBS! You must install processings sound library in order for this sketch to work properly !OBS!

Made by: Aleksander Willersrud
*/








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
float wallPerlinCap = 0.62f;
float wallPerlinScale = 0.3f;

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

public void setup() {
            // 1100, 921
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
    soundtrack.amp(0.8f);

}


public void init() {
   
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


public void draw() {
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
class Bombs {

    int x;
    int y;

    int bombTick = 0;
    int explodeTick;

    int type = 6;

    Bombs(int _x, int _y, int _explodeTick) {
        this.x = _x;
        this.y = _y;
        this.explodeTick = _explodeTick;
    }

    public void tick() {
        bombTick++;

        if (type == 6) type = 7;            // change color of bomb to illustrate ticking
        else type = 6;

        if (bombTick >= explodeTick) {
            bombExplode();
        }
    }


    public void bombExplode() {
        Bombs currentBomb = activeBombs.get(0);
        activeBombs.remove(currentBomb);

        Explosions explosion = new Explosions(x, y, explosionTickLength);
        activeExplosions.add(explosion);
    }
}
class Box {
    
    /* Boxes that can be picked to get bombs */
    
    /* Position stuff */
    int x;
    int y;
    int minDistanceToPlayer;
    
    /* GFX */
    int type = 5;
    
    /* Componenets */
    Positions positions = new Positions();
    
    /* Constructor */
    Box(int _minDistanceToPlayer) {
        this.minDistanceToPlayer = _minDistanceToPlayer;
    }
    
    public void spawnBox() {
        int[] spawnPoints = positions.randomPosition(minDistanceToPlayer);
        x = spawnPoints[0];
        y = spawnPoints[1];  
    }

}
class Enemy {
    /* Position stuff */
    int x; 
    int y;
    int minDistanceToPlayer = 17;

    /* Difficulty */
    int difficulty;

    /* GFX */
    int type = 1;

    /* Pathfinding */
    ArrayList<PathfindNode> enemyPath = new ArrayList<PathfindNode>();

    /* Components */
    Player player;
    Positions positions = new Positions();

    /* Constructor */
    Enemy(Player player, int distanceToPlayerSpawn, int enemyDifficulty) {
        this.player = player;
        this.minDistanceToPlayer = distanceToPlayerSpawn;
        this.difficulty = enemyDifficulty;
    }


    public void spawnEnemy() {
        int[] spawnPoints = positions.randomPosition(minDistanceToPlayer);
        x = spawnPoints[0];
        y = spawnPoints[1];
    }


    public void moveEnemy() {
        int[] newPos;

        Boolean findingNewPos = true;
        while (findingNewPos) {
            if (useAstarPathfinding) {
                newPos = positions.aStarPathfinding(this, null, difficulty);
            }
            else newPos = positions.oldPathfinding(true, difficulty, x, y);

            if (grid[newPos[0]][newPos[1]] != 4) {
                findingNewPos = false;

                x = newPos[0];
                y = newPos[1];                 
                break;
            } else {
                // println("findingNewPos = false, restarting while loop");
                continue;
            }
        }
    }
}



/* not working */

// int newPosX = newPos[0];
// int newPosY = newPos[1];

// newPos = positions.checkPositionNotOccupied(x, y, newPosX, newPosY);        

// newPosX = newPos[0];
// newPosY = newPos[1];        


//Boolean checkingIfPosOccupied = true;

//while (checkingIfPosOccupied) {

//    Boolean posOccupied = true;
//    for (int i = 0; i < enemies.length; i++) {
//        int indexX = enemies[i].x;
//        int indexY = enemies[i].y;
//        if (x == indexX && y == indexY) continue;                                    // were not interested in checking if out own position = our own position... continue
//        if (!positions.positionOccupied(newPos[0], newPos[1], indexX, indexY)) {
//            posOccupied = false;
//        }
//        println(newPos[0], newPos[1], indexX, indexY);
//    }
//    if (posOccupied == true) {
//        newPos = positions.randomNewPosition(x, y);
//        break;
//    }

//    if (posOccupied == false) {
//        checkingIfPosOccupied = false;
//        break;
//    }
//}
class Explosions {
    int x;
    int y;

    int currentTick = 0;
    int explosionTickLength;

    Explosions(int _x, int _y, int _explosionTickLength) {
        this.x = _x;
        this.y = _y;
        this.explosionTickLength = _explosionTickLength;
    }


    public void triggerExplosions() {
        currentTick++;
        if (currentTick < explosionTickLength) {
            
            grid[x][y] = 12;        // mid
            
            for (int i = 1; i <= currentTick; i++) {
                if (x - i >= 0) 
                    grid[x-i][y] = 11;                 // left mid
                
                if (y - i >= 0) 
                    grid[x][y-i] = 16;                 // top mid
                
                if (x + i <= gridLength - 1) 
                    grid[x+i][y] = 14;                 // right mid
               
                if (y + i <= gridLength - 1)
                    grid[x][y+i] = 9;                  // bot mid
            }
            if (x - currentTick - 1 >= 0)
                grid[x - currentTick - 1][y] = 10;     // left left
                
            if (y - currentTick - 1 >= 0)
                grid[x][y - currentTick - 1] = 15;     // top top
                
            if (x + currentTick + 1 <= gridLength - 1)
                grid[x + currentTick + 1][y] = 13;     // right right
            
            if (y + currentTick + 1 <= gridLength -1)
                grid[x][y + currentTick + 1] = 8;      // bot bot
            
            
        } else {
            activeExplosions.remove(this);
            pathfindGrid.createGrid();
        }
    }
}
class Food {
    /* Position stuff */
    int x;
    int y;
    int minDistanceToPlayer;
    int targetX;
    int targetY;

    /* Difficulty */
    int difficulty;
    int safeDistance;
    Boolean runAwayFromPlayer = false;

    /* GFX */
    int type = 2;

    /* Pathfinding */
    ArrayList<PathfindNode> foodPath = new ArrayList<PathfindNode>();

    /* Components */
    Player player;
    Positions positions = new Positions();

    /* Constructor */
    Food(Player _player, int _distanceToPlayerSpawn, int _foodDifficulty, int _safeDistance) {
        this.player = _player;
        this.minDistanceToPlayer = _distanceToPlayerSpawn;
        this.difficulty = _foodDifficulty;
        this.safeDistance = _safeDistance;
    }        


    public void spawnFood() {            // Generate a random spawn position for food
        int[] spawnPoints = positions.randomPosition(minDistanceToPlayer);
        x = spawnPoints[0];
        y = spawnPoints[1];
        generateTargetPos();
    }


    public void moveFood() {             // move food away from player
        // int[] newPos = positions.randomClosePosition(x, y);
        int[] newPos;

        Boolean findingNewPos = true;
        while (findingNewPos) {
            if (useAstarPathfinding && !runAwayFromPlayer) newPos = positions.aStarPathfinding(null, this, difficulty);
            else newPos = positions.oldPathfinding(false, difficulty, x, y);

            if (grid[newPos[0]][newPos[1]] != 4) {
                findingNewPos = false;

                x = newPos[0];
                y = newPos[1];                 
                return;
            } else continue;
        }   
    }


    public void generateTargetPos() {
        Boolean generatingTargetPos = true;

        while (generatingTargetPos) {
            int[] targetPos = positions.randomPosition(minDistanceToPlayer);
            if (grid[targetPos[0]][targetPos[1]] != 4) {
                targetX = targetPos[0];
                targetY = targetPos[1];
                generatingTargetPos = false;
                break;
            } else continue;
        }
    }


    public void checkDistanceToTargetPos() {
        if ((positions.measureDistance('x', x, targetX) + positions.measureDistance('y', y, targetY)) / 2 <= 5) {
            generateTargetPos();
        }
    }


    public void checkDistanceToPlayerPos() {
        if ((positions.measureDistance('x', x, player.x) + positions.measureDistance('y', y, player.y)) / 2 <= safeDistance) {
            generateTargetPos();
            runAwayFromPlayer = true;
        } else runAwayFromPlayer = false;
    }
}
/* GAME GFX */

PImage groundSpr;
PImage wallSpr;

PImage enemySpr;
PImage foodSpr;
PImage playerSpr;
PImage heartSpr;

PImage boxSpr;
PImage box2Spr;

/* Bomb GFX */
PImage bombSpr;
PImage bomb2Spr;
PImage bomb3Spr;

/* Explosion GFX */
PImage exploBotBotSpr;
PImage exploBotMidSpr;
PImage exploLeftLeftSpr;
PImage exploLeftMidSpr;

PImage exploMidSpr;

PImage exploRightRightSpr;
PImage exploRightMidSpr;
PImage exploTopTopSpr;
PImage exploTopMidSpr;

/* Menu background */
PImage menuBackground;

/* Buttons */
PImage btnHow2play;
PImage btnMenu;
PImage btnRestart;
PImage btnSettings;
PImage btnStart;

public void assignSprites() {
    groundSpr = loadImage("data/ground.png");
    wallSpr = loadImage("data/wall.png");
    
    enemySpr = loadImage("data/enemy.png");
    foodSpr = loadImage("data/food.png");
    playerSpr = loadImage("data/player.png");
    heartSpr = loadImage("data/heart.png");    
    
    boxSpr = loadImage("data/box1.png");
    box2Spr = loadImage("data/box2.png");        // ui box
    
    bombSpr = loadImage("data/bomb.png");        // ui bomb
    bomb2Spr = loadImage("data/bomb2.png");      // black bomb
    bomb3Spr = loadImage("data/bomb3.png");      // red bomb
    
    
    exploBotBotSpr = loadImage("data/explo/explo_bot_bot.png");
    exploBotMidSpr = loadImage("data/explo/explo_bot_mid.png");
    exploLeftLeftSpr = loadImage("data/explo/explo_left_left.png");
    exploLeftMidSpr = loadImage("data/explo/explo_left_mid.png");
    
    exploMidSpr = loadImage("data/explo/explo_mid.png");
    
    exploRightRightSpr = loadImage("data/explo/explo_right_right.png");
    exploRightMidSpr = loadImage("data/explo/explo_right_mid.png");
    exploTopTopSpr = loadImage("data/explo/explo_top_top.png");
    exploTopMidSpr = loadImage("data/explo/explo_top_mid.png");
    
    
    menuBackground = loadImage("data/background.png");
    
    btnHow2play = loadImage("data/buttons/btn_how2play.png");
    btnMenu = loadImage("data/buttons/btn_menu.png");
    btnRestart = loadImage("data/buttons/btn_restart.png");
    btnSettings = loadImage("data/buttons/btn_settings.png");
    btnStart = loadImage("data/buttons/btn_start.png");
}


public PImage getSpriteFromType(int type) {
    PImage sprite = groundSpr;
    if (type == 0) sprite = groundSpr;
    if (type == 1) sprite = enemySpr;
    if (type == 2) sprite = foodSpr;
    if (type == 3) sprite = playerSpr;
    
    if (type == 4) sprite = wallSpr;
    if (type == 5) sprite = boxSpr;
    
    if (type == 6) sprite = bomb2Spr;
    if (type == 7) sprite = bomb3Spr;
    
    
    if (type == 8) sprite = exploBotBotSpr;
    if (type == 9) sprite = exploBotMidSpr;
    if (type == 10) sprite = exploLeftLeftSpr;
    if (type == 11) sprite = exploLeftMidSpr;
    
    if (type == 12) sprite = exploMidSpr;
    
    if (type == 13) sprite = exploRightRightSpr;
    if (type == 14) sprite = exploRightMidSpr;
    if (type == 15) sprite = exploTopTopSpr;
    if (type == 16) sprite = exploTopMidSpr;
    
    return sprite;
}


//color getColorFromType(int type) {
//    color c = color(255);
//    if (type == 0) c = color(230);              // empty sqaures
//    if (type == 1) c = color(255, 0, 0);        // enemies
//    if (type == 2) c = color(0, 255, 0);        // food
//    if (type == 3) c = color(0, 0, 255);        // player
//    if (type == 4) c = color(0, 255, 255);      // unknown for now
//    return c;
//}
/* GAME LOGIC */

public void playGame() {
    clearBoard();                // reset board
    resolveCollisions();         // check for player collisions
    
    updateBombs();               // update bombs GFX
    updateBoxs();                // update boxs GFX
    updateEntities();            // update player board GFX
    updateEnemies();             // update enemy positions and enemy board GFX
    updateFoods();               // update food positions and food GFX
    updateExplosions();          // update active explosions
    
    resolveCollisions();         // check for player collisions
    drawBoard();                 // draw the board
}

public void clearBoard() {
    for (int x = 0; x < grid.length; x++) {
        for (int y = 0; y < grid[0].length; y++) {
            if (grid[x][y] == 4 || grid[x][y] == 5) continue;                  // reset tiles that are not walls, boxs
            else grid[x][y] = 0;
        }
    }
}


public void updateBombs() {
    for (int i = 0; i < activeBombs.size(); i ++) {
        Bombs currentBomb = activeBombs.get(i);
        currentBomb.tick();
        grid[currentBomb.x][currentBomb.y] = currentBomb.type;
    }
}


public void updateBoxs() {
    for (int i = 0; i < boxs.length; i++) {
        grid[boxs[i].x][boxs[i].y] = boxs[i].type;
    }
}


public void updateEntities() {
    grid[player.x][player.y] = player.type;
}


public void updateEnemies() {
    for (int i = 0; i < enemies.length; i++) {
        enemies[i].moveEnemy();                                    
        grid[enemies[i].x][enemies[i].y] = enemies[i].type;
    }
}


public void updateFoods() {
    for (int i = 0; i < foods.length; i++) {
        foods[i].checkDistanceToPlayerPos();
        foods[i].checkDistanceToTargetPos();
        
        foods[i].moveFood();
        grid[foods[i].x][foods[i].y] = foods[i].type;
    }
}


public void updateExplosions() {
    for (int i = 0; i < activeExplosions.size(); i++) {
        Explosions currentExplosions = activeExplosions.get(i);
        currentExplosions.triggerExplosions();
    }
}



public void drawBoard() {
    for (int x = 0; x < grid.length; x++) {
        for (int y = 0; y < grid[0].length; y++) {

            if (grid[x][y] == 3) {
                image(groundSpr, x * gridSquareSize, y * gridSquareSize, gridSquareSize, gridSquareSize);        // draw ground under player
            }

            PImage sprite = getSpriteFromType(grid[x][y]);
            image(sprite, x * gridSquareSize, y * gridSquareSize, gridSquareSize, gridSquareSize);
        }
    }
}


public void resolveCollisions() {
    int[] explosionsType = {8, 9, 10, 11, 12, 13, 14, 15, 16};
    
    /* Enemy collisions */
    for (int i = 0; i < enemies.length; i++) {                                            
        if (player.x == enemies[i].x && player.y == enemies[i].y) {                   // check for enemy collisions
            player.takeDamage();                                                          // an enemy has collided with the player!
            enemies[i].spawnEnemy();                                                      // spawn new enemy
        } 
        for (int j = 0; j < explosionsType.length; j++) {
            if (grid[enemies[i].x][enemies[i].y] == explosionsType[j]) {              // check if enemy collided with explosion
                player.increaseScore();
                player.enemyKilled++;
                
                enemies[i].spawnEnemy();
            }
        }
    }

    /* Food collisions */
    for (int i = 0; i < foods.length; i++) {                                              
        if (player.x == foods[i].x && player.y == foods[i].y) {                       // check for food collisions
            player.increaseScore();                                                       // the player collided with food!
            player.foodEaten++;
            foods[i].spawnFood();                                                         // spawn new food
        }
        for (int j = 0; j < explosionsType.length; j++) {                             // check if food collided with explosion
            if (grid[foods[i].x][foods[i].y] == explosionsType[j]) {
                //player.increaseScore();
                player.foodKilled++;

                foods[i].spawnFood();
            }
        }        
    }
    
    /* Box collisions */
    for (int i = 0; i < boxs.length; i++) {
        if (player.x == boxs[i].x && player.y == boxs[i].y) {
            if (player.canCarryMoreBombs()) {
                player.increaseBombs();
                boxs[i].spawnBox();
            }
        } 
    }
    
    /* PLayer collisions */
    for (int i = 0; i < explosionsType.length; i++) {
        if (grid[player.x][player.y] == explosionsType[i]) {
            player.takeDamage();
        }
    }
}
/* MOVEMENT */

public void keyPressed() {
    if (key == 'a' || keyCode == LEFT) movingLeft = true;     // move left 
    if (key == 'd' || keyCode == RIGHT) movingRight = true;    // move right
    if (key == 'w' || keyCode == UP) movingUp = true;       // move up
    if (key == 's' || keyCode == DOWN) movingDown = true;     // move down

    if (key == ' ') player.spawnBomb();    // spawn bomb with space

    if (key == BACKSPACE) player.takeDamage();
    if (key == 'z' && debug) printIntArray(grid);    // show debug log in console
}


public void keyReleased() {
    if (key == 'a' || keyCode == LEFT) movingLeft = false;    // stop move left 
    if (key == 'd' || keyCode == RIGHT) movingRight = false;   // stop move right
    if (key == 'w' || keyCode == UP) movingUp = false;      // stop move up
    if (key == 's' || keyCode == DOWN) movingDown = false;    // stop move down
}







/* DEBUGGING */
public void printIntArray(int[][] arr) {
    println("");
    println("");
    println("");
    for (int x = 0; x < arr.length; x++) {
        for (int y = 0; y < arr[0].length; y++) {
            print(arr[x][y] + ", ");
        }
        println();
    }
}
class Pathfind {

    /* A* pathfinding */
    
    PathfindGrid pGrid;
    
    Pathfind(PathfindGrid _pGrid) {
        this.pGrid = _pGrid;
    }


    public void findPath(Enemy enemy, Food food, int startX, int startY, int targetX, int targetY) {
        PathfindNode startNode = pGrid.nodeFromWorldPoint(startX, startY);        // get pGrid node as start node
        PathfindNode targetNode = pGrid.nodeFromWorldPoint(targetX, targetY);     // get pGrid node as target node

        ArrayList<PathfindNode> openSet = new ArrayList<PathfindNode>();          // create array list that will store nodes to be evaluated
        HashSet<PathfindNode> closedSet = new HashSet<PathfindNode>();            // create empty array list that will store nodes that ALREADY are evaluated
        openSet.add(startNode);                                                   // add startNode to our openset list as this is the node we want to move from

        while (openSet.size() > 0) {                                              // enter loop to find path to targetNode
            PathfindNode currentNode = openSet.get(0);                            // set start location to look for path as first element in the openSet
            for (int i = 1; i < openSet.size(); i++) {                            // loop through all elements of openSet to find path with lowest cost
                if ((openSet.get(i).fCost() < currentNode.fCost()) ||             // check if cost to move (fCost) to index node is lower that current node cost to move (fCost)
                    (openSet.get(i).fCost() == currentNode.fCost() &&             // or if fCost is equal..
                    openSet.get(i).hCost < currentNode.hCost))                    // check which node is closest to the end node by comparing estimates of cheapest path (hCost)
                {
                    currentNode = openSet.get(i);                                 // set currentNode as the new pathfind start point
                }
            }

            openSet.remove(currentNode);                                          // remove currentNode from openSet as it has now been evaluated
            closedSet.add(currentNode);                                           // add currentNode from closedSet since its been evaluated

            if (currentNode == targetNode) {                                      // path found successfull!
                if (enemy != null) enemy.enemyPath = retracePath(startNode, targetNode);
                if (food != null) food.foodPath = retracePath(startNode, targetNode);
            }   

            for (int i = 0; i < pGrid.getNeighbours(currentNode).size(); i++) {           // loop through all the neighbours of currentNode
                PathfindNode neighbour = pGrid.getNeighbours(currentNode).get(i);         // store current index as a node - if only we had foreach..
                if (!neighbour.walkable || closedSet.contains(neighbour)) {               // check if index node is walkable and neighbour not in closedset, if neighbour is = continue
                    continue;
                }
                
                int newMovementCostToNeighbour = currentNode.gCost + getDistance(currentNode, neighbour);    // store the cost of moving to neighbour
                if (newMovementCostToNeighbour < neighbour.gCost || !openSet.contains(neighbour)) {          // check if new path is shorter than current path or neighbour not in openSet
                    neighbour.gCost = newMovementCostToNeighbour;                                            // set neighbour total cost of path from start to target as gCost
                    neighbour.hCost = getDistance(neighbour, targetNode);                                    // set neighbour cost of the cheapest path as hCost
                    neighbour.parent = currentNode;                                                          // set parent as the current node
                    
                    if (!openSet.contains(neighbour)) openSet.add(neighbour);                                // add neighbour to openSet if openSet does not already contain neighbour
                } 
            }
        }
    }
    
    
    // retrace nodes to find pathfinding path (basically just reverses array)
    public ArrayList<PathfindNode> retracePath(PathfindNode startNode, PathfindNode endNode) {
        ArrayList<PathfindNode> path = new ArrayList<PathfindNode>();             // new array list for storing nodes in correct order 
        PathfindNode currentNode = endNode;                                       // temporarly store endNode
        
        while (currentNode != startNode) {                                        // loop until path is retraced
            path.add(currentNode);                                                // add currentNode to path array
            currentNode = currentNode.parent;                                     // change currentNode to parent and repeat
        }
        
        Collections.reverse(path);                                                // reverse the array list to get the nodes in correct order from start to target
        
        return path;
    }
    
    
    public int getDistance(PathfindNode nodeA, PathfindNode nodeB) {                     // measure distance between 2 nodes
        int dstX = Math.abs(nodeA.x - nodeB.x);                                   // distance on the x axis
        int dstY = Math.abs(nodeA.y - nodeB.y);                                   // distance on the y axis
        
        if (dstX > dstY) return 14*dstY + 10*(dstX - dstY);                       // where the magic happens (A* equation)
        else return 14*dstX + 10*(dstY - dstX);
    }
}
class PathfindGrid {

    PathfindNode[][] pathGrid;
    int gridLength;

    PathfindGrid(int _gridLength) {
        this.gridLength = _gridLength;
    }


    public void createGrid() {                                                    // create a grid for A* pathfinding algo
        pathGrid = new PathfindNode[gridLength][gridLength];
          
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {  

                Boolean walkable = true;
                if (grid[x][y] == 4) walkable = false;                     // if grid type is wall, then grid square is unwalkable
                pathGrid[x][y] = new PathfindNode(walkable, x, y);         // add a PathfindNode to the current position in loop
            }
        }
    }


    public PathfindNode nodeFromWorldPoint(int x, int y) {                        // get pGrid node from a set of coords
        return pathGrid[x][y];
    }


    public ArrayList<PathfindNode> getNeighbours(PathfindNode node) {             // find neighbouring nodes in a 3x3 area around node
        ArrayList<PathfindNode> neighbours = new ArrayList<PathfindNode>();

        for (int x = -1; x <= 1; x++) {                                    // start search in a 3x3 area around the node
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;                            // were in the middle of the 3x3, which is our current node, skip this node

                int checkX = node.x + x;                                   // local variables used to check if position is inside of 3x3 area
                int checkY = node.y + y;

                if (checkX >= 0 && checkX < gridLength &&                  // check if checkX is inside of grid &&
                    checkY >= 0 && checkY < gridLength)                    // check if checkY is inside of grid
                {
                    neighbours.add(pathGrid[checkX][checkY]);              // position is valid, add position to neighbours array list
                }
            }
        }
        return neighbours;
    }
}
/* These are the nodes that we use for searching for optimal path with A* algo */

class PathfindNode {
    Boolean walkable;

    int x;
    int y;

    int gCost;                    // cost of path from the start node to target node
    int hCost;                    // estimate of the cost of the cheapest path from node to the target node
    public int fCost() {                 // total cost of path from start to target
        return gCost + hCost;
    }

    PathfindNode parent;          // store current node as parent in pathfinding algo

    PathfindNode(Boolean _walkable, int _x, int _y) {
        this.walkable = _walkable;
        this.x = _x;
        this.y = _y;
    }
}
class Player {
    /* Position stuff */
    int x; 
    int y; 
    int wantedPosX;
    int wantedPosY;
    
    /* Stats */
    int HP;
    int score;
    int foodEaten = 0;
    int foodKilled = 0;
    int enemyKilled = 0;
    
    /* Bombs */
    int bombs;
    int maxB;
    
    /* GFX */
    int type = 3; 

    /* Constructor */
    Player(int x, int y, int playerHP, int playerScore, int startingBombs, int maxBombs) {
        this.x = x;
        this.y = y;
        this.HP = playerHP;
        this.score = playerScore; 
        this.bombs = startingBombs;
        this.maxB = maxBombs;
    }

    /* Movement */
    public void moveLeft() {
        if (insideGrid('x', -1)) {
            wantedPosX = x - 1;
            wantedPosY = y;
            if (grid[wantedPosX][wantedPosY] != 4) 
                x--;
        }
    }
    public void moveRight() {
        if (insideGrid('x', 1)) {
            wantedPosX = x + 1;
            wantedPosY = y;
            if (grid[wantedPosX][wantedPosY] != 4) 
                x++;
        }        
    }
    public void moveUp() {
        if (insideGrid('y', -1)) {
            wantedPosX = x;
            wantedPosY = y - 1;
            if (grid[wantedPosX][wantedPosY] != 4) 
                y--;
        }        
    }
    public void moveDown() {
        if (insideGrid('y', 1)) {
            wantedPosX = x;
            wantedPosY = y + 1;
            if (grid[wantedPosX][wantedPosY] != 4) 
                y++;
        }         
    }


    public Boolean insideGrid(char xy, int wantedDirection) {
        if (xy == 'x') {
            if (x == 0 && wantedDirection == -1) return false;
            if (x == gridLength-1 && wantedDirection == 1) return false;
        }
        if (xy == 'y') {
            if (y == 0 && wantedDirection == -1) return false;
            if (y == gridLength-1 && wantedDirection == 1) return false;
        }
        return true;
    }


    public void takeDamage() {
        HP--;
        checkLives();            // enable to have game over screen
    }
    
    
    public void increaseScore() {
        score += 100;
    }
    
    
    public void checkLives() {
        if (HP <= 0) gameOver = true;
    }
    
    
    public Boolean canCarryMoreBombs() {
        if (bombs >= maxB) return false;
        else return true;
    }
    
    
    public void increaseBombs() {
        bombs++;
    }
    
    
    public void spawnBomb() {
        if (bombs > 0) {
            Bombs bomb = new Bombs(x, y, bombExplodeTick);
            activeBombs.add(bomb);
            bombs--;
        }
    }    
}
public class Positions {


    public int[] randomPosition(int minDistance) {
        Boolean positionNotFound = true;
        while (positionNotFound) {
            int randX = PApplet.parseInt(random(2, gridLength -3));
            int randY = PApplet.parseInt(random(2, gridLength -3));

            if ((measureDistance('x', player.x, randX) + measureDistance('y', player.y, randY)) / 2 <= minDistance) {        // check distance to player
                continue;
            } else {

                if (grid[randX][randY] != 4) {                                // check position is not wall
                    
                    Boolean areSurroundingTilesWalls = false;                 // check position is open area (5x5)
                    for (int x = -2; x <= 2; x++) {
                        for (int y = -2; y <= 2; y++) {
                            if (grid[randX + x][randY +y] == 4) areSurroundingTilesWalls = true;
                        }
                    }
                    
                    if (!areSurroundingTilesWalls) {
                        int[] randomPositions = {randX, randY};
                        positionNotFound = false;
                        return randomPositions;                    
                    } else continue;
                    

                } else {
                    continue;
                }
            }
        }
        int[] fail = {1, 1};
        return fail;
    }


    public Boolean moveIsRandom(int difficulty) {
        int randomMovementChance = Math.round(random(1, difficulty));
        Boolean moveRandom = false;

        if (randomMovementChance == difficulty) moveRandom = true;
        return moveRandom;
    }


    public int[] aStarPathfinding(Enemy enemy, Food food, int difficulty) {
        int[] newPos = {0, 0};

        if (enemy != null) {                                                              // A* enemy algo
            if (moveIsRandom(difficulty)) newPos = randomClosePosition(enemy.x, enemy.y);
            else {
                pathfind.findPath(enemy, null, enemy.x, enemy.y, player.x, player.y);

                try {
                    int[] enemyNewPos = {enemy.enemyPath.get(0).x, enemy.enemyPath.get(0).y};
                    newPos = enemyNewPos;
                } 
                catch(Exception e) {
                    return oldPathfinding(true, difficulty, enemy.x, enemy.y);
                }
            }
        }

        if (food != null) {                                                                // A* food algo
            if (moveIsRandom(difficulty)) newPos = randomClosePosition(food.x, food.y);
            else {
                pathfind.findPath(null, food, food.x, food.y, food.targetX, food.targetY);

                try {
                    int[] foodNewPos = {food.foodPath.get(0).x, food.foodPath.get(0).y};
                    newPos = foodNewPos;
                } 
                catch(Exception e) {
                    return oldPathfinding(false, difficulty, food.x, food.y);
                }
            }
        }
        return newPos;
    }


    public int[] oldPathfinding(Boolean moveTowardsPlayer, int difficulty, int x, int y) {
        int[] newPos = {x, y};
        Boolean moveRandom = false;

        if (moveIsRandom(difficulty)) {                                                       // the higher the difficulty, the lower the chance of random movement    
            moveRandom = true;
            newPos = randomClosePosition(x, y);
        }

        if (!moveRandom && moveTowardsPlayer) {                                                // enemy algo
            if (measureDistance('x', player.x, x) >= measureDistance('y', player.y, y)) {
                if (x > player.x) newPos = checkPosition('x', -1, x, y);
                else newPos = checkPosition('x', 1, x, y);
            } else {
                if (y > player.y) newPos = checkPosition('y', -1, x, y);
                else newPos = checkPosition('y', 1, x, y);
            }
        }

        if (!moveRandom && !moveTowardsPlayer) {                                               // food algo
            if (measureDistance('x', player.x, x) >= measureDistance('y', player.y, y)) {
                if (x < player.x) newPos = checkPosition('x', -1, x, y);
                else newPos = checkPosition('x', 1, x, y);
            } else {
                if (y < player.y) newPos = checkPosition('y', -1, x, y);
                else newPos = checkPosition('y', 1, x, y);
            }
        }
        return newPos;
    }


    public int[] randomClosePosition(int x, int y) {
        Boolean generatingClosePosition = true;
        int[] newPos = {x, y};

        while (generatingClosePosition) {
            int randomDirection = Math.round(random(1, 4));
            
            if (randomDirection == 1) newPos = checkPosition('x', 1, x, y);
            if (randomDirection == 2) newPos = checkPosition('x', -1, x, y);
            if (randomDirection == 3) newPos = checkPosition('y', 1, x, y);              
            if (randomDirection == 4) newPos = checkPosition('y', -1, x, y);
            
            if (grid[newPos[0]][newPos[1]] != 4) {
                generatingClosePosition = false;
                break;
            } else {
                // println("randomClosePosition failed, trying again");
                continue;
            }
        }
        return newPos;
    }


    public int[] checkPosition(char xy, int wantedDirection, int x, int y) {              // check if the wanted direction is outside of array bounds, if not, move unit
        if (xy == 'x') {
            if (x <= 0 && wantedDirection == -1) wantedDirection = 1;              // change movement direction if wantedDirection is out of bounds
            if (x >= gridLength-1 && wantedDirection == 1) wantedDirection = -1;   // change movement direction if wantedDirection is out of bounds
            x = x + wantedDirection;
        }
        if (xy == 'y') {
            if (y <= 0 && wantedDirection == -1) wantedDirection = 1;              // change movement direction if wantedDirection is out of bounds
            if (y >= gridLength-1 && wantedDirection == 1) wantedDirection = -1;   // change movement direction if wantedDirection is out of bounds
            y = y + wantedDirection;
        }
        //if (grid[x][y] == 4) {
        //    return randomClosePosition(x, y);
        //}
        int[] newPos = {x, y};
        return newPos;
    }


    public int measureDistance(char xy, int pos1, int pos2) {
        int distance = 0;
        if (xy == 'x') distance = Math.abs(pos1 - pos2);
        else if (xy == 'y') distance = Math.abs(pos1 - pos2);
        return distance;
    }



    /* downward not working */


    //Boolean positionOccupied(int x1, int y1, int x2, int y2) {
    //    if (x1 == x2 && y1 == y2) return true;
    //    else return false;
    //}


    //int[] checkPositionNotOccupied(int currentX, int currentY, int newX, int newY) {
    //    int[] newPos = {newX, newY};

    //    Boolean checkingIfPosOccupied = true;
    //    while (checkingIfPosOccupied) {

    //        Boolean posOccupied = false;
    //        for (int i = 0; i < enemies.length; i++) {            // change this so we can use it for food and enemies
    //            int indexX = enemies[i].x;
    //            int indexY = enemies[i].y;

    //            if (currentX == indexX && currentY == indexY) continue;                                    // were not interested in checking if out own position = our own position... continue

    //            if (newX == indexX && newY == indexY) {
    //                posOccupied = true;
    //            }
    //        }

    //        if (posOccupied == true) {
    //            int[] newPos2 = randomNewPosition(currentX, currentX);
    //            newX = newPos2[0];
    //            newY = newPos2[1];
    //            // println("New random position generated!"+ newX, newY);
    //            // continue;
    //        }

    //        if (posOccupied == false) {
    //            checkingIfPosOccupied = false;
    //            // break;
    //            return newPos;
    //        }
    //    }
    //    return newPos;
    //}
}
/* Handles mouse presses */

public void mousePressed() {  
    if (gameOver) {
        /* GAME OVER - menu button */
        if (mouseX >= menuButton[0] && mouseX <= menuButton[0] + menuButton[2] && mouseY >= menuButton[1] && mouseY <= menuButton[1] + menuButton[3]) {
            startMenu = true;
            startMenuUI();           
        }     
        /* GAME OVER - restart button */
        if (mouseX >= startButton2[0] && mouseX <= startButton2[0] + startButton2[2] && mouseY >= startButton2[1] && mouseY <= startButton2[1] + startButton2[3]) {                
            resetGame();           
        }        
    }
    if (startMenu && !howToPlay) {
        /* MAIN MENU - start button */
        if (mouseX >= startButton[0] && mouseX <= startButton[0] + startButton[2] && mouseY >= startButton[1] && mouseY <= startButton[1] + startButton[3]) {                    
            startMenu = false;
            init();            
        }
        /* MAIN MENU - how to play button */
        if (mouseX >= how2playButton[0] && mouseX <= how2playButton[0] + how2playButton[2] && mouseY >= how2playButton[1] && mouseY <= how2playButton[1] + how2playButton[3]) {
            //startMenu = false;
            howToPlay = true;
            howToPlayUI();          
        }     
        /* MAIN MENU - settings button */
        //if (mouseX >= settingsButton[0] && mouseX <= settingsButton[0] + settingsButton[2] && mouseY >= settingsButton[1] && mouseY <= settingsButton[1] + settingsButton[3]) {
        //    //startMenu = false;
        //    println("Settings");            
        //}         
    }
    if (howToPlay) {
        /* HOW2PLAY - menu button */
        if (mouseX >= menuButton[0] && mouseX <= menuButton[0] + menuButton[2] && mouseY >= menuButton[1] && mouseY <= menuButton[1] + menuButton[3]) {
            howToPlay = false;
            //startMenu = true;
            startMenuUI();           
        }     
        /* HOW2PLAY - start button */
        if (mouseX >= startButton2[0] && mouseX <= startButton2[0] + startButton2[2] && mouseY >= startButton2[1] && mouseY <= startButton2[1] + startButton2[3]) {
            howToPlay = false;
            startMenu = false;
            init();           
        }        
    }
}
/* ============ */
/* GAME OVER UI */
/* ============ */

public void gameOverUI() {
    displayGameOver();
    displayGameOverButtons();
}


public void displayGameOver() {
    highScore = player.score;

    rectMode(CORNERS);
    fill(140, 140, 140, 40);
    rect(100, 100, width - 100, height - 70);        // game over background
    
    textAlign(CENTER, CENTER);
    textSize(120); 
    fill(0, 0, 0, 255);
    String s = "Game Over\nYour score was\n"+player.score;
    text(s, width / 2, (height / 2) - 180);
    
    textAlign(RIGHT, CENTER);
    textSize(60);
    
    s = player.foodEaten + " food eaten";
    text(s, width / 2 + 200, (height / 2) + 50);
    
    s = player.foodKilled + " food killed";
    text(s, width / 2 + 200, (height / 2) + 100);
    
    s = player.enemyKilled +" enemies killed";
    text(s, width / 2 + 200, (height / 2) + 150);
}


public void displayGameOverButtons() {
    image(btnMenu, menuButton[0], menuButton[1], menuButton[2], menuButton[3]);                    // menu button
    image(btnRestart, startButton2[0], startButton2[1], startButton2[2], startButton2[3]);         // restart button
}


public void resetGame() {

    for (int x = 0; x < grid.length; x++) {
        for (int y = 0; y < grid[0].length; y++) {
            grid[x][y] = 0;
        }
    }
    gameIteration = (int)random(100);
    init();
}
/* =============== */
/* GAME RUNNING UI */
/* =============== */

public void updateUI() {
    uiBackground();
    displayScore();
    displayLives();
    displayBombs();
    updateHighScore();
    displayHighScore();
}

public void uiBackground() {
    rectMode(CORNERS);
    fill(150);
    rect(width - 240, 190, width - 20, height - 210);        // shadow
    
    stroke(150);
    fill(180);
    rect(width - 250, 200, width - 30, height - 200);
}


public void displayLives() {
    int heartX = width - 240;
    for (int i = 0; i < player.HP; i++) {
        image(heartSpr, heartX, 220, 60, 60);
        heartX += 70;
    }
}


public void displayBombs() {
    int bombX = width - 240;
    for (int i = 0; i < player.bombs; i++) {
        image(bombSpr, bombX, 300, 60, 60);
        bombX += 70;
    }
}


public void displayScore() {
    String s = "SCORE\n"+player.score;
    textAlign(CENTER, CENTER);
    textSize(45);    
    fill(0);
    text(s, width - 140, 450);        // middle
}


public void updateHighScore() {
    if (player.score > highScore) {
        highScore = player.score;
    }
}


public void displayHighScore() {
    String s = "HIGH SCORE\n"+highScore;
    textSize(45);
    fill(0);
    text(s, width - 140, 620);
}
/* ============= */
/* START MENU UI */
/* ============= */

int[] startButton =    {130, 250, 300, 120};
int[] how2playButton = {130, 450, 300, 120};
int[] settingsButton = {130, 650, 300, 120};

int[] menuButton =     {250, 700, 300, 120};
int[] startButton2 =   {650, 700, 300, 120};

int orange = color(249, 180, 32);

public void startMenuUI() {   
    image(menuBackground, 0, 0, width, height);        // background

    image(btnStart, startButton[0], startButton[1], startButton[2], startButton[3]);                    // start button
    image(btnHow2play, how2playButton[0], how2playButton[1], how2playButton[2], how2playButton[3]);     // how 2 play button
    // image(btnSettings, settingsButton[0], settingsButton[1], settingsButton[2], settingsButton[3]);     // how 2 play button

    fill(orange);
    rectMode(CORNERS);
    noStroke();
    rect(width/2 - 430, 70, width/2 + 430, 170);
    rect(width/2 - 425, 65, width/2 + 425, 175);
    rect(width/2 - 420, 60, width/2 + 420, 180);

    String s = "Bomb Runner";                                                                      // title
    textAlign(CENTER, CENTER);
    textSize(150);
    fill(0);
    text(s, width/2, 100);
}


public void howToPlayUI() {
    background(190);

    image(btnMenu, menuButton[0], menuButton[1], menuButton[2], menuButton[3]);                    // menu button
    image(btnStart, startButton2[0], startButton2[1], startButton2[2], startButton2[3]);           // start button

    String s = "How to play Bomb runner";                                                          // title
    textAlign(CENTER, CENTER);
    textSize(100);
    text(s, width/2, 50);

    textAlign(LEFT, TOP);
    textSize(50);

    s = "move around using wasd or arrow keys";
    text(s, 150, 140);    

    s = "place bombs by pressing space";
    text(s, 150, 230);
    image(bombSpr, 900, 220);

    s = "gather boxes to get more bombs\nYou can hold a max of 3 bombs";
    text(s, 150, 320);
    image(box2Spr, 900, 335);

    s = "you play as";
    text(s, 150, 450);
    image(playerSpr, 450, 450, 50, 50);
    
    s = "close enemies will damage you";
    text(s, 150, 540);
    image(enemySpr, 903, 535, 50, 50);
    
    s = "eat food to increase score";
    text(s, 150, 630);
    image(foodSpr, 903, 625, 50, 50);
}
class Walls {
    
    /* Generate random unwalkable walls using perlin noise */
    
    float perlinCap;
    float scl;
    

    Walls(float _perlinCap, float _scl) {
        this.perlinCap = _perlinCap;
        this.scl = _scl;
    }


    public void wallGeneration() {
        generateWalls();
        checkWallHoles();
        checkWallHoles();        // run twice is good right? xD
    }


    public void generateWalls() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {        
                float perlinPos = noise((x +gameIteration) * scl, (y +gameIteration) * scl);
                
                if (x < 5 || x > gridLength - 5 || y < 5 || y > gridLength -5) continue;                                                                    // dont make walls around border of board
                if ((x >= (gridLength / 2) - 5) && (x <= (gridLength / 2) + 5) && (y >= (gridLength / 2) - 5) && (y <= (gridLength / 2) + 5)) continue;     // dont make walls in 10x10 area around player spawn      
                
                if (perlinPos >= wallPerlinCap) {                                      // perlin noise wall is allowed if greater than perlin cap
                    // grid[x][y] = 4;
                    
                    for (int sorroundX = -1; sorroundX <= 1; sorroundX++) {            // make walls in 3x3 area around allowed tile
                        for (int sorroundY = -1; sorroundY <= 1; sorroundY++) {
                            grid[x + sorroundX][y + sorroundY] = 4;
                        }
                    }                    
                }
            }
        }
    }
    
    
    public void checkWallHoles() {                                                            // maybe change to search tiles above and to the sides instead of 3x3 area
        for (int x = 2; x < grid.length - 3; x++) {
            for (int y = 2; y < grid[0].length - 3; y++) {
                
                Boolean holeSurroundedByWalls1 = true;
                
                if (grid[x][y] == 0) {                                                 // tile is ground
                    for (int sorroundX = -1; sorroundX <= 1; sorroundX++) {            // check in 3x3 area around current tile to see if surroundings are walls 
                        for (int sorroundY = -1; sorroundY <= 1; sorroundY++) {
                            if (sorroundX == x && sorroundY == y) continue;            // this is the current tile were checking, continue
                            
                            if (grid[x + sorroundX][y + sorroundY] != 4) holeSurroundedByWalls1 = false;
                        }
                    }
                }
                
                if (holeSurroundedByWalls1) grid[x][y] = 4;
                
                if (grid[x][y] == 0) {
                    Boolean holeSurroundedByWallsCheck1 = false;
                    Boolean holeSurroundedByWallsCheck2 = false;
                    if (grid[x-1][y] == 4 && grid[x+1][y] == 4) holeSurroundedByWallsCheck1 = true;
                    if (grid[x][y-1] == 4 && grid[x][y+1] == 4) holeSurroundedByWallsCheck2 = true;
                    
                    if (holeSurroundedByWallsCheck1 && holeSurroundedByWallsCheck2) grid[x][y] = 4;
                }
            }
        }
    }
}
    public void settings() {  size(1200, 921); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "BombRunner" };
        if (passedArgs != null) {
          PApplet.main(concat(appletArgs, passedArgs));
        } else {
          PApplet.main(appletArgs);
        }
    }
}
