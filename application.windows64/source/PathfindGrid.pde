class PathfindGrid {

    PathfindNode[][] pathGrid;
    int gridLength;

    PathfindGrid(int _gridLength) {
        this.gridLength = _gridLength;
    }


    void createGrid() {                                                    // create a grid for A* pathfinding algo
        pathGrid = new PathfindNode[gridLength][gridLength];
          
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {  

                Boolean walkable = true;
                if (grid[x][y] == 4) walkable = false;                     // if grid type is wall, then grid square is unwalkable
                pathGrid[x][y] = new PathfindNode(walkable, x, y);         // add a PathfindNode to the current position in loop
            }
        }
    }


    PathfindNode nodeFromWorldPoint(int x, int y) {                        // get pGrid node from a set of coords
        return pathGrid[x][y];
    }


    ArrayList<PathfindNode> getNeighbours(PathfindNode node) {             // find neighbouring nodes in a 3x3 area around node
        ArrayList<PathfindNode> neighbours = new ArrayList<PathfindNode>();

        for (int x = -1; x <= 1; x++) {                                    // start search in a 3x3 area around the node
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;                            // were in the middle of the 3x3, which is our current node, skip this node

                int checkX = node.x + x;                                   // local variables used to check if position is inside of 3x3 area
                int checkY = node.y + y;

                if (checkX >= 0 && checkX < gridLength &&                  // check if checkX is inside of grid &&
                    checkY >= 0 && checkY < gridLength)                    // check if checkY is inside of grid
                {
                    neighbours.add(pathGrid[checkX][checkY]);              // position is valid, add position to neighbours array list
                }
            }
        }
        return neighbours;
    }
}
