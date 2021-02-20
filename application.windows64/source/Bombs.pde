class Bombs {

    int x;
    int y;

    int bombTick = 0;
    int explodeTick;

    int type = 6;

    Bombs(int _x, int _y, int _explodeTick) {
        this.x = _x;
        this.y = _y;
        this.explodeTick = _explodeTick;
    }

    void tick() {
        bombTick++;

        if (type == 6) type = 7;            // change color of bomb to illustrate ticking
        else type = 6;

        if (bombTick >= explodeTick) {
            bombExplode();
        }
    }


    void bombExplode() {
        Bombs currentBomb = activeBombs.get(0);
        activeBombs.remove(currentBomb);

        Explosions explosion = new Explosions(x, y, explosionTickLength);
        activeExplosions.add(explosion);
    }
}
