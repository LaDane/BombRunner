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


    void spawnFood() {            // Generate a random spawn position for food
        int[] spawnPoints = positions.randomPosition(minDistanceToPlayer);
        x = spawnPoints[0];
        y = spawnPoints[1];
        generateTargetPos();
    }


    void moveFood() {             // move food away from player
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


    void generateTargetPos() {
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


    void checkDistanceToTargetPos() {
        if ((positions.measureDistance('x', x, targetX) + positions.measureDistance('y', y, targetY)) / 2 <= 5) {
            generateTargetPos();
        }
    }


    void checkDistanceToPlayerPos() {
        if ((positions.measureDistance('x', x, player.x) + positions.measureDistance('y', y, player.y)) / 2 <= safeDistance) {
            generateTargetPos();
            runAwayFromPlayer = true;
        } else runAwayFromPlayer = false;
    }
}
