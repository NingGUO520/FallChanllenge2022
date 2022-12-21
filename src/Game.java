
class Contestant {
    Map<Coord, Integer> units;
    List<Action> builds;
    List<Action> spawns;
    List<Action> moves;
    int money;
    public Contestant() {
        units = new HashMap<>();
        builds = new ArrayList<>();
        spawns = new ArrayList<>();
        moves = new ArrayList<>();
    }
    public int getUnitAt(Coord coord) {
        return units.getOrDefault(coord,0);
    }

    public void placeUnits(Coord target, int amount) {
        units.put(target, getUnitAt(target)+  amount);
    }

    public void removeUnits(Coord target, int amount) {
        int number =  getUnitAt(target);
        if(amount < number)
        {
            units.put(target,number -  amount);
        }else{
            units.remove(target);
        }

    }


    public void addAction(Action action) {
        switch (action.type) {
            case BUILD:
                builds.add(action);
                break;
            case MOVE:
                moves.add(action);
                break;
            case SPAWN:
                spawns.add(action);
                break;
            default:
                break;
        }
    }
}

class Recycler {

    Coord coord;
    Contestant owner;

    public Recycler(Coord coord, Contestant owner) {
        this.coord = coord;
        this.owner = owner;
    }
}


class Coord {
    int x;
    int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coord add(int x, int y) {
        return new Coord(this.x + x, this.y + y);
    }

    public Coord add(Coord c) {
        return add(c.x, c.y);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        Coord other = (Coord) obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int manhattanTo(Coord other) {
        return manhattanTo(other.x, other.y);
    }

    public int manhattanTo(int x, int y) {
        return Math.abs(x - this.x) + Math.abs(y - this.y);
    }
}

class Action {

    enum ActionType {
        MOVE,
        SPAWN,
        BUILD,
        WAIT
    }

    final ActionType type;
    Coord coord;
    int amount;
    Coord originCoord;
    public Action(ActionType type) {
        this.type = type;
    }
}

class Session {
    int width;
    int height;
    int gameTurn;
    Tile[][] cells;
    Contestant me;
    Contestant opponent;
    List<Tile> myTiles = new ArrayList<>();
    List<Tile> oppTiles = new ArrayList<>();
    List<Tile> neutralTiles = new ArrayList<>();
    List<Tile> myUnits = new ArrayList<>();
    List<Tile> oppUnits = new ArrayList<>();
    List<Recycler> myRecyclers = new ArrayList<>();
    List<Recycler> oppRecyclers = new ArrayList<>();
    List<Recycler> recyclers = new ArrayList<>();

    List<Contestant> players;
    Set<Coord> fightLocations = new HashSet<>();
    public Session(  List<Tile> myTiles,   List<Tile> oppTiles ,    List<Tile> neutralTiles,
                     List<Tile> myUnits,  List<Tile> oppUnits,    List<Recycler> myRecyclers ,   List<Recycler> oppRecyclers,
                     int width,int height,   Tile[][] cells)
    {
        this.width = width;
        this.height = height;
        this.cells = cells;
        this.myTiles = myTiles;
        this.oppTiles =oppTiles;
        this.neutralTiles =neutralTiles;
        this.myUnits =myUnits;
        this.oppUnits = oppUnits;
        this.myRecyclers =myRecyclers;
        this.oppRecyclers =oppRecyclers;
        me = new Contestant();
        opponent = new Contestant();
        me.money = 10;
        opponent.money = 10;
        gameTurn = 0;
        players.add(me);
        players.add(opponent);
        recyclers.addAll(myRecyclers);
        recyclers.addAll(oppRecyclers);
    }
    void performGameUpdate() {
        doBuilds();
        doUnits();
        doRecycle();
        gameTurn++;
    }

    void doBuilds() {
        for (Action build : me.builds) {
            Coord buildTarget = build.coord;
            myRecyclers.add(new Recycler(buildTarget, me));
            me.money -=10;
        }
        for (Action build : opponent.builds) {
            Coord buildTarget = build.coord;
            oppRecyclers.add(new Recycler(buildTarget, opponent));
            opponent.money -=10;
        }
    }

    void doUnits() {
        doSpawn();
        doMove();
        doFights();
    }
    void doRecycle() {
        for (Recycler recycler : recyclers) {

        }
    }

    void doSpawn() {
        for(Contestant player : players)
        {
            for (Action spawn : player.spawns) {
                Coord target = spawn.coord;
                player.placeUnits(target, spawn.amount);
                player.money -= 10;
                fightLocations.add(target);
            }
        }

    }
    void doMove() {
        List<Coord> restricted = new ArrayList<>();
        for(Recycler recycler : myRecyclers) restricted.add(recycler.coord);
        for(Recycler recycler : oppRecyclers) restricted.add(recycler.coord);
        for(Contestant player : players) {
            for (Action move : player.moves) {
                Coord origin = move.originCoord;
                Coord target = move.coord;


            }
        }
    }
    void doFights() {
        for (Coord coord : fightLocations) {


            int myStrength = me.getUnitAt(coord);
            int oppoStrength = opponent.getUnitAt(coord);
            if (myStrength > 0 && oppoStrength > 0) {
                Contestant winner = null;
                if (myStrength > oppoStrength) {
                    winner = me;
                } else if (oppoStrength > myStrength) {
                    winner = opponent;
                }
            }
            me.removeUnits(coord, oppoStrength);
            opponent.removeUnits(coord, myStrength);
            Tile tile = cells[coord.x][coord.y];
            if (myStrength > oppoStrength && tile.owner != 0) {
                tile.owner = 0;
            } else if (oppoStrength > myStrength && tile.owner != 1) {
                tile.owner = 1;
            }
        }
    }
}


