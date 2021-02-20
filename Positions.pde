public class Positions {


    int[] randomPosition(int minDistance) {
        Boolean positionNotFound = true;
        while (positionNotFound) {
            int randX = int(random(2, gridLength -3));
            int randY = int(random(2, gridLength -3));

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


    Boolean moveIsRandom(int difficulty) {
        int randomMovementChance = Math.round(random(1, difficulty));
        Boolean moveRandom = false;

        if (randomMovementChance == difficulty) moveRandom = true;
        return moveRandom;
    }


    int[] aStarPathfinding(Enemy enemy, Food food, int difficulty) {
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


    int[] oldPathfinding(Boolean moveTowardsPlayer, int difficulty, int x, int y) {
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


    int[] randomClosePosition(int x, int y) {
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


    int[] checkPosition(char xy, int wantedDirection, int x, int y) {              // check if the wanted direction is outside of array bounds, if not, move unit
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


    int measureDistance(char xy, int pos1, int pos2) {
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
