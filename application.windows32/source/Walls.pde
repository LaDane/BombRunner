class Walls {
    
    /* Generate random unwalkable walls using perlin noise */
    
    float perlinCap;
    float scl;
    

    Walls(float _perlinCap, float _scl) {
        this.perlinCap = _perlinCap;
        this.scl = _scl;
    }


    void wallGeneration() {
        generateWalls();
        checkWallHoles();
        checkWallHoles();        // run twice is good right? xD
    }


    void generateWalls() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {        
                float perlinPos = noise((x +gameIteration) * scl, (y +gameIteration) * scl);
                
                if (x < 5 || x > gridLength - 5 || y < 5 || y > gridLength -5) continue;                                                                    // dont make walls around border of board
                if ((x >= (gridLength / 2) - 5) && (x <= (gridLength / 2) + 5) && (y >= (gridLength / 2) - 5) && (y <= (gridLength / 2) + 5)) continue;     // dont make walls in 10x10 area around player spawn      
                
                if (perlinPos >= wallPerlinCap) {                                      // perlin noise wall is allowed if greater than perlin cap
                    // grid[x][y] = 4;
                    
                    for (int sorroundX = -1; sorroundX <= 1; sorroundX++) {            // make walls in 3x3 area around allowed tile
                        for (int sorroundY = -1; sorroundY <= 1; sorroundY++) {
                            grid[x + sorroundX][y + sorroundY] = 4;
                        }
                    }                    
                }
            }
        }
    }
    
    
    void checkWallHoles() {                                                            // maybe change to search tiles above and to the sides instead of 3x3 area
        for (int x = 2; x < grid.length - 3; x++) {
            for (int y = 2; y < grid[0].length - 3; y++) {
                
                Boolean holeSurroundedByWalls1 = true;
                
                if (grid[x][y] == 0) {                                                 // tile is ground
                    for (int sorroundX = -1; sorroundX <= 1; sorroundX++) {            // check in 3x3 area around current tile to see if surroundings are walls 
                        for (int sorroundY = -1; sorroundY <= 1; sorroundY++) {
                            if (sorroundX == x && sorroundY == y) continue;            // this is the current tile were checking, continue
                            
                            if (grid[x + sorroundX][y + sorroundY] != 4) holeSurroundedByWalls1 = false;
                        }
                    }
                }
                
                if (holeSurroundedByWalls1) grid[x][y] = 4;
                
                if (grid[x][y] == 0) {
                    Boolean holeSurroundedByWallsCheck1 = false;
                    Boolean holeSurroundedByWallsCheck2 = false;
                    if (grid[x-1][y] == 4 && grid[x+1][y] == 4) holeSurroundedByWallsCheck1 = true;
                    if (grid[x][y-1] == 4 && grid[x][y+1] == 4) holeSurroundedByWallsCheck2 = true;
                    
                    if (holeSurroundedByWallsCheck1 && holeSurroundedByWallsCheck2) grid[x][y] = 4;
                }
            }
        }
    }
}
