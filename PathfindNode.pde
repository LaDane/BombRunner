/* These are the nodes that we use for searching for optimal path with A* algo */

class PathfindNode {
    Boolean walkable;

    int x;
    int y;

    int gCost;                    // cost of path from the start node to target node
    int hCost;                    // estimate of the cost of the cheapest path from node to the target node
    int fCost() {                 // total cost of path from start to target
        return gCost + hCost;
    }

    PathfindNode parent;          // store current node as parent in pathfinding algo

    PathfindNode(Boolean _walkable, int _x, int _y) {
        this.walkable = _walkable;
        this.x = _x;
        this.y = _y;
    }
}
