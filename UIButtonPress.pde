/* Handles mouse presses */

void mousePressed() {  
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
