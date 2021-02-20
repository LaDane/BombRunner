/* ============= */
/* START MENU UI */
/* ============= */

int[] startButton =    {130, 250, 300, 120};
int[] how2playButton = {130, 450, 300, 120};
int[] settingsButton = {130, 650, 300, 120};

int[] menuButton =     {250, 700, 300, 120};
int[] startButton2 =   {650, 700, 300, 120};

color orange = color(249, 180, 32);

void startMenuUI() {   
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


void howToPlayUI() {
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
