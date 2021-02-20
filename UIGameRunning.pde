/* =============== */
/* GAME RUNNING UI */
/* =============== */

void updateUI() {
    uiBackground();
    displayScore();
    displayLives();
    displayBombs();
    updateHighScore();
    displayHighScore();
}

void uiBackground() {
    rectMode(CORNERS);
    fill(150);
    rect(width - 240, 190, width - 20, height - 210);        // shadow
    
    stroke(150);
    fill(180);
    rect(width - 250, 200, width - 30, height - 200);
}


void displayLives() {
    int heartX = width - 240;
    for (int i = 0; i < player.HP; i++) {
        image(heartSpr, heartX, 220, 60, 60);
        heartX += 70;
    }
}


void displayBombs() {
    int bombX = width - 240;
    for (int i = 0; i < player.bombs; i++) {
        image(bombSpr, bombX, 300, 60, 60);
        bombX += 70;
    }
}


void displayScore() {
    String s = "SCORE\n"+player.score;
    textAlign(CENTER, CENTER);
    textSize(45);    
    fill(0);
    text(s, width - 140, 450);        // middle
}


void updateHighScore() {
    if (player.score > highScore) {
        highScore = player.score;
    }
}


void displayHighScore() {
    String s = "HIGH SCORE\n"+highScore;
    textSize(45);
    fill(0);
    text(s, width - 140, 620);
}
