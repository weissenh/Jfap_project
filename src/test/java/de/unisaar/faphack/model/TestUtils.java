package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.*;

import java.lang.reflect.Field;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class TestUtils {

  private static final File testResourceDir = new File("src/test/resources");

  public static void modifyField(Object modifierdObject,boolean inSuperClass, String fieldname, Object value)  {
    try {
      Field f;
      if (inSuperClass){
        f = modifierdObject.getClass().getSuperclass().getDeclaredField(fieldname);
      } else {
        f = modifierdObject.getClass().getDeclaredField(fieldname);
      }
      f.setAccessible(true);
      f.set(modifierdObject, value);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }

  public static String getTestResource(String subdir, String name) {
    return new File(new File(testResourceDir, subdir), name).getPath();
  }

  public static Game createGame(){
    Game game = new Game();
    World world = createWorld(game);
    modifyField(game, false, "world", world);
    return game;
  }

  private static World createWorld(Game game)  {
    World world = new World();
    List<Room> mapElements = new ArrayList<>();
    // Create the rooms
    Room room1 = createSimpleRoom(8,8, world);
    mapElements.add(room1);
    Room room2 = createSimpleRoom(7,7, world);
    mapElements.add(room2);
    Room room3 = createSimpleRoom(4, 10, world);
    mapElements.add(room3);
    modifyField(world, false,"mapElements", mapElements);

    // Connect Room 1 and 2 with a hallway
    Hallway hallway = new Hallway();
    DoorTile doorTile1 = (DoorTile) room1.getTiles()[0][8/2];
    modifyField(doorTile1, false,"hallway", hallway);
    modifyField(hallway, true,"fromTile",doorTile1 );
    DoorTile doorTile2 = (DoorTile) room2.getTiles()[0][7/2];
    modifyField(doorTile2, false,"hallway", hallway);
    modifyField(hallway, true,"toTile", doorTile2);

    // Connect Room 2 and 3 with stairs
    Stair stair = new Stair();
    StairTile stairTile1 = (StairTile) room2.getTiles()[7/2][7/2];
    modifyField(stairTile1, false, "stair", stair);
    modifyField(stair, true, "fromTile", stairTile1);
    StairTile stairTile2 = (StairTile) room3.getTiles()[4/2][10/2];
    modifyField(stairTile2, false, "stair", stair);
    modifyField(stair, true, "toTile", stairTile2);

    // add two characters to the world
    Character c1 = addCharacter(room1, 2,2,"Foo");
    Character c2 = addCharacter(room3, 1,4,"Bar");

    // create a bunch of items and place them in the world
    Item sword = new Wearable();
    modifyField(sword, false, "weight", 2 );
    modifyField(sword, false, "isWeapon", true );
    modifyField(sword, false, "character",  c1);
    modifyField(c1, false, "activeWeapon", sword );
    Item fountain = new Fixtures();
    modifyField(fountain, true, "onTile", room1.getTiles()[3][3]);
    Item rottenApple = new Wearable();
    modifyField(rottenApple, true, "onTile", room3.getTiles()[3][9]);

    // register the world with the game
    modifyField(world, false, "g", game);
    return world;
  }

  public static Room createSimpleRoom(int x, int y, World world){
    Room r = new Room();
    Tile[][] tiles = createTiles(x, y, r);
    modifyField(r, false,"tiles", tiles);
    modifyField(r, false,"w", world);
    return r;
  }

  private static Tile[][] createTiles(int x, int y, Room room){
    Tile[][] result = new Tile[x][y];
    for(int i = 0; i < x; i++){
      for(int j = 0; j< y; j++){
        if (i == 0 || j == 0 || i == (x-1) || j == (y-1)){
          result[i][j] = new WallTile(i, j ,room);
        } else {
          result[i][j] = new FloorTile(i, j ,room);
        }
      }
    }
    // place a door
    result[0][y/2] = new DoorTile(0, y/2 , room);
    // place a stair tile right in the center of the room
    result[x/2][y/2] = new StairTile(x/2, y/2, room);
    return result;
  }



  private static Character addCharacter(Room room, int x, int y, String name){
    Character character = new Character();
    List<Character> inhabitants = new ArrayList<>();
    inhabitants.add(character);
    modifyField(room,false, "inhabitants", inhabitants);
    modifyField(character, false, "tile", room.getTiles()[x][y]);
    modifyField(character, false, "name", name);
    return character;
  }


}