import java.util.*;
import java.util.stream.*;

class Island {
    int myUnits = 0;
    int oppoUnits = 0;
    int neutrals = 0;
    int myTiles = 0;
    int oppoTiles = 0;
    Set<Tile> tiles;

    public Island(Set<Tile> tiles)
    {
        this.tiles = new HashSet<>(tiles);
        for(Tile tile : this.tiles)
        {
            if(tile.owner == 1)
            {
                myTiles++;
                myUnits += tile.units;
            }else if(tile.owner == 0)
            {
                oppoTiles ++;
                oppoUnits += tile.units;
            }else{
                neutrals++;
            }
        }
    }
    public boolean isTotalMyIsland()
    {
        return myTiles == tiles.size();
    }
    public boolean oppoUnitsIsMore(){return oppoUnits > myUnits;}
    public boolean myUnitsIsMore(){return oppoUnits < myUnits;}
    public boolean unitsIsTie(){return oppoUnits == myUnits;}
    public boolean ihaveunitsbutopponentdoesnot(){
        return myUnits>0 && oppoUnits == 0;
    }
}

class Tile {
    int x, y, scrapAmount, owner, units;
    boolean recycler, canBuild, canSpawn, inRangeOfRecycler;

    public Tile(int x, int y, int scrapAmount, int owner, int units, boolean recycler, boolean canBuild, boolean canSpawn,
                boolean inRangeOfRecycler) {
        this.x = x;
        this.y = y;
        this.scrapAmount = scrapAmount;
        this.owner = owner;
        this.units = units;
        this.recycler = recycler;
        this.canBuild = canBuild;
        this.canSpawn = canSpawn;
        this.inRangeOfRecycler = inRangeOfRecycler;
    }

    @Override
    public String toString()
    {
        return "number units = " + units + "x = " + x + "y = " + y;
    }
    public boolean canbemoveto()
    {
        return  ! this.recycler && this.scrapAmount > 0;
    }

    public int getScrapsNumberAround(Tile[][] map)
    {
        int count =  this.scrapAmount;
        if(x-1>=0)
            count += map[x-1][y].scrapAmount;
        if(x+1<map.length)
            count += map[x+1][y].scrapAmount;
        if(y-1>=0)
            count += map[x][y-1].scrapAmount;
        if(y+1 < map[0].length)
            count += map[x][y+1].scrapAmount;
        return count;
    }
    public int getdis(Tile dest)
    {
        return Math.abs(x-dest.x) +  Math.abs(y-dest.y);
    }

    public Tile getnearestdestomove(   List<Tile> desTiles )
    {
        Collections.sort(desTiles, (a,b) -> getdis(a) - getdis(b) );
        return desTiles.get(0);
    }

    public boolean reachableTile(Tile tile,Tile[][] map )
    {
        HashSet<Tile> visited= new HashSet<>();
        ArrayDeque<Tile> queue = new ArrayDeque<>();
        queue.add(this);
        visited.add(this);
        while(!queue.isEmpty())
        {
            int size = queue.size();
            for(int i = 0 ; i < size ; i ++)
            {
                Tile t = queue.poll();
                if(t == tile){
                    return true;
                }
                int x= t.x; int y = t.y;
                if(x-1>=0 && map[x-1][y].canbemoveto() && !visited.contains(map[x-1][y]) ) {
                    queue.add(map[x-1][y]);
                    visited.add(map[x-1][y]);
                }
                if(y-1>=0 && map[x][y-1].canbemoveto() && !visited.contains(map[x][y-1]) ){
                    queue.add(map[x][y-1]);
                    visited.add(map[x][y-1]);
                }
                if(x+1<map.length && map[x+1][y].canbemoveto() && !visited.contains(map[x+1][y]) ) {
                    queue.add(map[x+1][y]);
                    visited.add(map[x+1][y]);
                }
                if(y+1<map[0].length && map[x][y+1].canbemoveto() && !visited.contains(map[x][y+1]) ) {
                    queue.add(map[x][y+1]);
                    visited.add(map[x][y+1]);
                }
            }
        }
        return false;
    }

    public int getMyUnitsNumberAround(Tile[][] map)
    {
        int count =  0;
        if(x-1>=0 && map[x-1][y].owner == 1 )
            count += map[x-1][y].units;
        if(x+1<map.length  && map[x+1][y].owner == 1 )
            count += map[x+1][y].units;

        if(y-1>=0  && map[x][y-1].owner == 1 )
            count += map[x][y-1].units;
        if(y+1 < map[0].length && map[x][y+1].owner == 1 )
            count += map[x][y+1].units;
        return count;
    }

    public boolean hasMyRecyclerNear( List<Tile>  myRecyclers){
        for(Tile tile : myRecyclers){
            if(getdis(tile) <=2){
                return true;
            }
        }
        return false;
    }
}

class Player {

    static final int ME = 1;
    static final int OPP = 0;
    static final int NOONE = -1;
    static int buildtime = 0;
    static  int width ;
    static  int exploreround ;
    static boolean leftside;
    static Tile lastplacetobuild = null;

    public static  List<Island> detectIslands(Tile[][] map,   Set<Tile> alltiles , Map<Tile, Island> tileToIsland ) {
        List<Island> islands = new ArrayList<>();
        Set<Tile> current = new HashSet<>();
        Set<Tile> visited = new HashSet<>();
        for(Tile  tile : alltiles)
        {
            if(!tile.canbemoveto()) continue;
            if(!visited.contains(tile))
            {
                ArrayDeque<Tile> queue = new ArrayDeque<>();
                queue.add(tile);
                visited.add(tile);
                while(!queue.isEmpty())
                {
                    Tile t = queue.poll();
                    int x= t.x; int y = t.y;
                    if(x-1>=0 && map[x-1][y].canbemoveto() && !visited.contains(map[x-1][y]) ) {
                        queue.add(map[x-1][y]);
                        visited.add(map[x-1][y]);
                    }
                    if(y-1>=0 && map[x][y-1].canbemoveto() && !visited.contains(map[x][y-1]) ){
                        queue.add(map[x][y-1]);
                        visited.add(map[x][y-1]);
                    }
                    if(x+1<map.length && map[x+1][y].canbemoveto() && !visited.contains(map[x+1][y]) ) {
                        queue.add(map[x+1][y]);
                        visited.add(map[x+1][y]);
                    }
                    if(y+1<map[0].length && map[x][y+1].canbemoveto() && !visited.contains(map[x][y+1]) ) {
                        queue.add(map[x][y+1]);
                        visited.add(map[x][y+1]);
                    }
                    current.add(t);
                }
                Island island = new Island(current);
                islands.add(island);
                for(Tile t : current)
                {
                    tileToIsland.put(t, island);
                }
                current.clear();
            }
        }
        return islands;
    }
    //best place to build
    public static Tile besttiletobuild(int round, List<Tile> canBuildTiles,  Tile[][] map, List<Tile>  myRecyclers, Map<Tile, Island> tileToIsland ){
        //clean canBuildTiles
        List<Tile> toRemove = new ArrayList<>();
        for(Tile canBuild  : canBuildTiles)
        {
            Island island = tileToIsland.get(canBuild);

            if(canBuild.hasMyRecyclerNear(myRecyclers)){
                toRemove.add(canBuild);
            }

            if( tileToIsland.size() >=2  && (island.isTotalMyIsland() || island.myUnitsIsMore() ))
            {
                toRemove.add(canBuild);
            }
            if(canBuild.getMyUnitsNumberAround(map)>=4){
                toRemove.add(canBuild);
            }
        }
        canBuildTiles.removeAll(toRemove);
        if(canBuildTiles.size()>0){
            if(round < exploreround)
            {
                Collections.sort(canBuildTiles, (a,b) -> b.getScrapsNumberAround(map) - a.getScrapsNumberAround(map));
                if(lastplacetobuild == null)
                {
                    lastplacetobuild = canBuildTiles.get(0);
                    return lastplacetobuild;
                }else{
                    int i = 0;
                    while(i < canBuildTiles.size())
                    {
                        Tile tobuild = canBuildTiles.get(i++);

                        if(leftside)
                        {
                            if( tobuild.x >= lastplacetobuild.x)
                            {
                                lastplacetobuild = tobuild;
                                return tobuild;
                            }
                        }else{
                            if( tobuild.x <= lastplacetobuild.x)
                            {
                                lastplacetobuild = tobuild;
                                return tobuild;
                            }

                        }
                    }
                }
            }else{
                if(leftside)
                {
                    Collections.sort(canBuildTiles, (a,b) -> b.x - a.x);
                }else{
                    Collections.sort(canBuildTiles, (a,b) -> a.x - b.x);
                }
                return canBuildTiles.get(0);
            }
        }

        return null;
    }

    //best place to spawn
    public static Tile  getbesttileToSpawn(int round, List<Tile> canSpawnTiles , Map<Tile, Island> tileToIsland,   List<Island> islands)
    {

        if(round < exploreround){
            if(leftside){
                Collections.sort(canSpawnTiles, (a,b) -> b.x - a.x);

            }else{ // sort canSpawnTiles from left to right
                Collections.sort(canSpawnTiles, (a,b) -> a.x - b.x);
            }
            return canSpawnTiles.get(0);
        }

        List<Tile> toremove = new ArrayList<>();
        for(Tile tile : canSpawnTiles)
        {
            if(islands.size() >= 2)
            {
                Island island = tileToIsland.get(tile);
                if(island.oppoUnitsIsMore())
                {
                    return tile;
                }

                if(island.ihaveunitsbutopponentdoesnot() || island.myUnitsIsMore()){
                    toremove.add(tile);
                }


            }
        }
        canSpawnTiles.removeAll(toremove);
        if(canSpawnTiles.size()>0) return canSpawnTiles.get(0);
        return null;
    }

    //best place to move
    public static Tile  getBestPlaceToMove(int round, Tile tile, Tile[][] map,   List<Tile> desTiles)
    {
        int x = tile.x;
        int y = tile.y;
        if(round  < exploreround){
            // move to opponent
            if( leftside && x+1<map.length &&   map[x+1][y].canbemoveto() &&
                    ( map[x+1][y].owner == -1  ||  map[x+1][y].owner == 0 && map[x+1][y].units < tile.units ))
            {
                return map[x+1][y];
            }
            if( !leftside && x-1>=0 &&   map[x-1][y].canbemoveto() &&
                    (map[x-1][y].owner == -1   ||  map[x-1][y].owner == 0 && map[x-1][y].units < tile.units )  )
            {
                return map[x-1][y];
            }

        }

        if(y+1 < map[0].length &&  map[x][y+1].canbemoveto() &&
                (map[x][y+1].owner == -1 ||  map[x][y+1].owner == 0 && map[x][y+1].units < tile.units ) )
        {
            return map[x][y+1];
        }
        if(y -1 >= 0  && map[x][y-1].canbemoveto() &&
                (map[x][y-1].owner == -1 ||  map[x][y-1].owner == 0 && map[x][y-1].units < tile.units )  )
        {
            return map[x][y-1];
        }



        Collections.sort(desTiles, (a,b) -> tile.getdis(a) -  tile.getdis(b));
        int i = 0;
        while(i < desTiles.size())
        {
            Tile des = desTiles.get(i++);
            System.err.println("des " + des + " is reachable for tile " + tile + " =  " + tile.reachableTile(des, map));
            if( tile.reachableTile(des, map))
            {
                return des;
            }
        }
        return null;
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        width = in.nextInt();
        exploreround = (int)(width *0.75);
        int height = in.nextInt();
        Tile[][] map = new Tile[width][height];
        int round = 0;
        // game loop
        while (true) {
            round++;
            Set<Tile> alltiles = new HashSet<>();
            List<Tile> tiles = new ArrayList<>();
            List<Tile> myTiles = new ArrayList<>();
            List<Tile> oppTiles = new ArrayList<>();
            List<Tile> neutralTiles = new ArrayList<>();
            List<Tile> myUnits = new ArrayList<>();
            List<Tile> oppUnits = new ArrayList<>();
            List<Tile> myRecyclers = new ArrayList<>();
            List<Tile> oppRecyclers = new ArrayList<>();
            List<Tile> canBuildTiles = new ArrayList<>();
            List<Tile> canSpawnTiles = new ArrayList<>();
            int myMatter = in.nextInt();
            int oppMatter = in.nextInt();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile tile = new Tile(
                            x,
                            y,
                            in.nextInt(),
                            in.nextInt(),
                            in.nextInt(),
                            in.nextInt() == 1,
                            in.nextInt() == 1,
                            in.nextInt() == 1,
                            in.nextInt() == 1);

                    tiles.add(tile);
                    alltiles.add(tile);
                    map[tile.x][tile.y] = tile;
                    if (tile.owner == ME) {
                        myTiles.add(tile);
                        if(tile.canBuild && !tile.inRangeOfRecycler)
                        {
                            canBuildTiles.add(tile);
                        }

                        if(tile.canSpawn)
                        {
                            canSpawnTiles.add(tile);
                        }

                        if (tile.units > 0) {
                            myUnits.add(tile);
                        } else if (tile.recycler) {
                            myRecyclers.add(tile);
                        }
                    } else if (tile.owner == OPP) {
                        if(tile.canbemoveto()){
                            oppTiles.add(tile);
                        }

                        if (tile.units > 0) {
                            oppUnits.add(tile);
                        } else if (tile.recycler) {
                            oppRecyclers.add(tile);
                        }
                    } else {
                        if(tile.canbemoveto()){
                            neutralTiles.add(tile);
                        }
                    }
                }
            }

            if(round == 1 ){
                if(myUnits.size()> 0 && myUnits.get(0).x <  (width/2)){
                    leftside = true;
                    System.err.println("set leftside true myUnits.get(0).x = " + myUnits.get(0).x + " width = "+ width);

                }else if ( myUnits.size()> 0 && myUnits.get(0).x >  (width/2)){
                    leftside = false;
                    System.err.println("set leftside false myUnits.get(0).x = " + myUnits.get(0).x + " width = "+ width);

                }

            }
            List<String> actions = new ArrayList<>();
            //ilands
            Map<Tile, Island> tileToIsland = new HashMap<>();
            List<Island> islands =  detectIslands( map,   alltiles , tileToIsland );

            //  System.err.println("number of goodplacetobuild = " + goodplacetobuild.size());
            // build one recycler each turn
            boolean shouldBuild = round > width/6 && myMatter >= 10 && myRecyclers.size() < width/4 && canBuildTiles.size() > 0;

            if(shouldBuild)
            {
                Tile besttiletobuild =
                        besttiletobuild(round, canBuildTiles, map, myRecyclers,tileToIsland);

                if(besttiletobuild!= null  ) {
                    actions.add(String.format("BUILD %d %d", besttiletobuild.x, besttiletobuild.y));
                    buildtime++;
                    myMatter -=10;
                }

            }

            //   System.err.println("mbuildtime" + buildtime + " number of recy*cle = " + myRecyclers.size());



            System.err.println("I am on leftside = " + leftside  );

            //Spawn
            if(canSpawnTiles.size()>0)
            {
                Tile tospawn = getbesttileToSpawn(round, canSpawnTiles, tileToIsland,  islands);

                if (myMatter > 10 && tospawn != null) {
                    int amount = myMatter /10; //  pick amount of robots to spawn here
                    if (amount > 0) {
                        actions.add(String.format("SPAWN %d %d %d", amount, tospawn.x, tospawn.y));

                    }
                    myMatter -= amount*10;

                }

            }

            //MOVE
            List<Tile> desTiles = new ArrayList<>();
            desTiles.addAll(neutralTiles);
            desTiles.addAll(oppTiles);

            for( Tile tile : myUnits) {

                if( desTiles.size() >0 )
                {
                    Tile target =  getBestPlaceToMove(round, tile, map, desTiles );
                    if(target != null){
                        int amount = tile.units; //  pick amount of units to move
                        actions.add(String.format("MOVE %d %d %d %d %d", amount, tile.x, tile.y, target.x, target.y));
                        //  System.err.println("current tile to move  " + tile.x + " , " + tile.y);
                        //   System.err.println("find target " + target.x + " , " + target.y);
                    }

                }
            }


            // To debug: System.err.println("Debug messages...");
            if (actions.isEmpty()) {
                System.out.println("WAIT");
            } else {
                String result =  actions.stream().collect(Collectors.joining(";"));
                System.out.println(result);
            }
        }
    }
}