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


    void spawnEnemy() {
        int[] spawnPoints = positions.randomPosition(minDistanceToPlayer);
        x = spawnPoints[0];
        y = spawnPoints[1];
    }


    void moveEnemy() {
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
