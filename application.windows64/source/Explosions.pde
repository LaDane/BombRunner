class Explosions {
    int x;
    int y;

    int currentTick = 0;
    int explosionTickLength;

    Explosions(int _x, int _y, int _explosionTickLength) {
        this.x = _x;
        this.y = _y;
        this.explosionTickLength = _explosionTickLength;
    }


    void triggerExplosions() {
        currentTick++;
        if (currentTick < explosionTickLength) {
            
            grid[x][y] = 12;        // mid
            
            for (int i = 1; i <= currentTick; i++) {
                if (x - i >= 0) 
                    grid[x-i][y] = 11;                 // left mid
                
                if (y - i >= 0) 
                    grid[x][y-i] = 16;                 // top mid
                
                if (x + i <= gridLength - 1) 
                    grid[x+i][y] = 14;                 // right mid
               
                if (y + i <= gridLength - 1)
                    grid[x][y+i] = 9;                  // bot mid
            }
            if (x - currentTick - 1 >= 0)
                grid[x - currentTick - 1][y] = 10;     // left left
                
            if (y - currentTick - 1 >= 0)
                grid[x][y - currentTick - 1] = 15;     // top top
                
            if (x + currentTick + 1 <= gridLength - 1)
                grid[x + currentTick + 1][y] = 13;     // right right
            
            if (y + currentTick + 1 <= gridLength -1)
                grid[x][y + currentTick + 1] = 8;      // bot bot
            
            
        } else {
            activeExplosions.remove(this);
            pathfindGrid.createGrid();
        }
    }
}
