/* GAME LOGIC */

void playGame() {
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

void clearBoard() {
    for (int x = 0; x < grid.length; x++) {
        for (int y = 0; y < grid[0].length; y++) {
            if (grid[x][y] == 4 || grid[x][y] == 5) continue;                  // reset tiles that are not walls, boxs
            else grid[x][y] = 0;
        }
    }
}


void updateBombs() {
    for (int i = 0; i < activeBombs.size(); i ++) {
        Bombs currentBomb = activeBombs.get(i);
        currentBomb.tick();
        grid[currentBomb.x][currentBomb.y] = currentBomb.type;
    }
}


void updateBoxs() {
    for (int i = 0; i < boxs.length; i++) {
        grid[boxs[i].x][boxs[i].y] = boxs[i].type;
    }
}


void updateEntities() {
    grid[player.x][player.y] = player.type;
}


void updateEnemies() {
    for (int i = 0; i < enemies.length; i++) {
        enemies[i].moveEnemy();                                    
        grid[enemies[i].x][enemies[i].y] = enemies[i].type;
    }
}


void updateFoods() {
    for (int i = 0; i < foods.length; i++) {
        foods[i].checkDistanceToPlayerPos();
        foods[i].checkDistanceToTargetPos();
        
        foods[i].moveFood();
        grid[foods[i].x][foods[i].y] = foods[i].type;
    }
}


void updateExplosions() {
    for (int i = 0; i < activeExplosions.size(); i++) {
        Explosions currentExplosions = activeExplosions.get(i);
        currentExplosions.triggerExplosions();
    }
}



void drawBoard() {
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


void resolveCollisions() {
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
