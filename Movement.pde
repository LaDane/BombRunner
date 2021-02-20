/* MOVEMENT */

void keyPressed() {
    if (key == 'a' || keyCode == LEFT) movingLeft = true;     // move left 
    if (key == 'd' || keyCode == RIGHT) movingRight = true;    // move right
    if (key == 'w' || keyCode == UP) movingUp = true;       // move up
    if (key == 's' || keyCode == DOWN) movingDown = true;     // move down

    if (key == ' ') player.spawnBomb();    // spawn bomb with space

    if (key == BACKSPACE) player.takeDamage();
    if (key == 'z' && debug) printIntArray(grid);    // show debug log in console
}


void keyReleased() {
    if (key == 'a' || keyCode == LEFT) movingLeft = false;    // stop move left 
    if (key == 'd' || keyCode == RIGHT) movingRight = false;   // stop move right
    if (key == 'w' || keyCode == UP) movingUp = false;      // stop move up
    if (key == 's' || keyCode == DOWN) movingDown = false;    // stop move down
}







/* DEBUGGING */
void printIntArray(int[][] arr) {
    println("");
    println("");
    println("");
    for (int x = 0; x < arr.length; x++) {
        for (int y = 0; y < arr[0].length; y++) {
            print(arr[x][y] + ", ");
        }
        println();
    }
}
