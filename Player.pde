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
    void moveLeft() {
        if (insideGrid('x', -1)) {
            wantedPosX = x - 1;
            wantedPosY = y;
            if (grid[wantedPosX][wantedPosY] != 4) 
                x--;
        }
    }
    void moveRight() {
        if (insideGrid('x', 1)) {
            wantedPosX = x + 1;
            wantedPosY = y;
            if (grid[wantedPosX][wantedPosY] != 4) 
                x++;
        }        
    }
    void moveUp() {
        if (insideGrid('y', -1)) {
            wantedPosX = x;
            wantedPosY = y - 1;
            if (grid[wantedPosX][wantedPosY] != 4) 
                y--;
        }        
    }
    void moveDown() {
        if (insideGrid('y', 1)) {
            wantedPosX = x;
            wantedPosY = y + 1;
            if (grid[wantedPosX][wantedPosY] != 4) 
                y++;
        }         
    }


    Boolean insideGrid(char xy, int wantedDirection) {
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


    void takeDamage() {
        HP--;
        checkLives();            // enable to have game over screen
    }
    
    
    void increaseScore() {
        score += 100;
    }
    
    
    void checkLives() {
        if (HP <= 0) gameOver = true;
    }
    
    
    Boolean canCarryMoreBombs() {
        if (bombs >= maxB) return false;
        else return true;
    }
    
    
    void increaseBombs() {
        bombs++;
    }
    
    
    void spawnBomb() {
        if (bombs > 0) {
            Bombs bomb = new Bombs(x, y, bombExplodeTick);
            activeBombs.add(bomb);
            bombs--;
        }
    }    
}
