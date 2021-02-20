/* ============ */
/* GAME OVER UI */
/* ============ */

void gameOverUI() {
    displayGameOver();
    displayGameOverButtons();
}


void displayGameOver() {
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


void displayGameOverButtons() {
    image(btnMenu, menuButton[0], menuButton[1], menuButton[2], menuButton[3]);                    // menu button
    image(btnRestart, startButton2[0], startButton2[1], startButton2[2], startButton2[3]);         // restart button
}


void resetGame() {

    for (int x = 0; x < grid.length; x++) {
        for (int y = 0; y < grid[0].length; y++) {
            grid[x][y] = 0;
        }
    }
    gameIteration = (int)random(100);
    init();
}
