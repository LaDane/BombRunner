class Pathfind {

    /* A* pathfinding */
    
    PathfindGrid pGrid;
    
    Pathfind(PathfindGrid _pGrid) {
        this.pGrid = _pGrid;
    }


    void findPath(Enemy enemy, Food food, int startX, int startY, int targetX, int targetY) {
        PathfindNode startNode = pGrid.nodeFromWorldPoint(startX, startY);        // get pGrid node as start node
        PathfindNode targetNode = pGrid.nodeFromWorldPoint(targetX, targetY);     // get pGrid node as target node

        ArrayList<PathfindNode> openSet = new ArrayList<PathfindNode>();          // create array list that will store nodes to be evaluated
        HashSet<PathfindNode> closedSet = new HashSet<PathfindNode>();            // create empty array list that will store nodes that ALREADY are evaluated
        openSet.add(startNode);                                                   // add startNode to our openset list as this is the node we want to move from

        while (openSet.size() > 0) {                                              // enter loop to find path to targetNode
            PathfindNode currentNode = openSet.get(0);                            // set start location to look for path as first element in the openSet
            for (int i = 1; i < openSet.size(); i++) {                            // loop through all elements of openSet to find path with lowest cost
                if ((openSet.get(i).fCost() < currentNode.fCost()) ||             // check if cost to move (fCost) to index node is lower that current node cost to move (fCost)
                    (openSet.get(i).fCost() == currentNode.fCost() &&             // or if fCost is equal..
                    openSet.get(i).hCost < currentNode.hCost))                    // check which node is closest to the end node by comparing estimates of cheapest path (hCost)
                {
                    currentNode = openSet.get(i);                                 // set currentNode as the new pathfind start point
                }
            }

            openSet.remove(currentNode);                                          // remove currentNode from openSet as it has now been evaluated
            closedSet.add(currentNode);                                           // add currentNode from closedSet since its been evaluated

            if (currentNode == targetNode) {                                      // path found successfull!
                if (enemy != null) enemy.enemyPath = retracePath(startNode, targetNode);
                if (food != null) food.foodPath = retracePath(startNode, targetNode);
            }   

            for (int i = 0; i < pGrid.getNeighbours(currentNode).size(); i++) {           // loop through all the neighbours of currentNode
                PathfindNode neighbour = pGrid.getNeighbours(currentNode).get(i);         // store current index as a node - if only we had foreach..
                if (!neighbour.walkable || closedSet.contains(neighbour)) {               // check if index node is walkable and neighbour not in closedset, if neighbour is = continue
                    continue;
                }
                
                int newMovementCostToNeighbour = currentNode.gCost + getDistance(currentNode, neighbour);    // store the cost of moving to neighbour
                if (newMovementCostToNeighbour < neighbour.gCost || !openSet.contains(neighbour)) {          // check if new path is shorter than current path or neighbour not in openSet
                    neighbour.gCost = newMovementCostToNeighbour;                                            // set neighbour total cost of path from start to target as gCost
                    neighbour.hCost = getDistance(neighbour, targetNode);                                    // set neighbour cost of the cheapest path as hCost
                    neighbour.parent = currentNode;                                                          // set parent as the current node
                    
                    if (!openSet.contains(neighbour)) openSet.add(neighbour);                                // add neighbour to openSet if openSet does not already contain neighbour
                } 
            }
        }
    }
    
    
    // retrace nodes to find pathfinding path (basically just reverses array)
    ArrayList<PathfindNode> retracePath(PathfindNode startNode, PathfindNode endNode) {
        ArrayList<PathfindNode> path = new ArrayList<PathfindNode>();             // new array list for storing nodes in correct order 
        PathfindNode currentNode = endNode;                                       // temporarly store endNode
        
        while (currentNode != startNode) {                                        // loop until path is retraced
            path.add(currentNode);                                                // add currentNode to path array
            currentNode = currentNode.parent;                                     // change currentNode to parent and repeat
        }
        
        Collections.reverse(path);                                                // reverse the array list to get the nodes in correct order from start to target
        
        return path;
    }
    
    
    int getDistance(PathfindNode nodeA, PathfindNode nodeB) {                     // measure distance between 2 nodes
        int dstX = Math.abs(nodeA.x - nodeB.x);                                   // distance on the x axis
        int dstY = Math.abs(nodeA.y - nodeB.y);                                   // distance on the y axis
        
        if (dstX > dstY) return 14*dstY + 10*(dstX - dstY);                       // where the magic happens (A* equation)
        else return 14*dstX + 10*(dstY - dstX);
    }
}
