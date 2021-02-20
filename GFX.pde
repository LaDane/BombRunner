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

void assignSprites() {
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


PImage getSpriteFromType(int type) {
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
