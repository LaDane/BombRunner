class Box {
    
    /* Boxes that can be picked to get bombs */
    
    /* Position stuff */
    int x;
    int y;
    int minDistanceToPlayer;
    
    /* GFX */
    int type = 5;
    
    /* Componenets */
    Positions positions = new Positions();
    
    /* Constructor */
    Box(int _minDistanceToPlayer) {
        this.minDistanceToPlayer = _minDistanceToPlayer;
    }
    
    void spawnBox() {
        int[] spawnPoints = positions.randomPosition(minDistanceToPlayer);
        x = spawnPoints[0];
        y = spawnPoints[1];  
    }

}
