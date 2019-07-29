package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.MultiplicativeEffect;
import de.unisaar.faphack.model.map.*;
import org.junit.jupiter.api.Test;
import org.lwjgl.Sys;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TestUtils {

  private static final File testResourceDir = new File("src/test/resources");

  private static void modifyField(Object modifierObject,boolean inSuperClass, String fieldname, Object value)  {
    try {
      Field f;
      if (inSuperClass){
        f = modifierObject.getClass().getSuperclass().getDeclaredField(fieldname);
      } else {
        f = modifierObject.getClass().getDeclaredField(fieldname);
      }
      f.setAccessible(true);
      f.set(modifierObject, value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void modifySuperSuperField(Object modifiedObject, String fieldname, Object value){
    try {
      Field f = modifiedObject.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldname);
      f.setAccessible(true);
      f.set(modifiedObject, value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getTestResource(String subdir, String name) {
    return getTestResourceFile(subdir, name).getPath();
  }

  public static File getTestResourceFile(String subdir, String name) {
    return new File(new File(testResourceDir, subdir), name);
  }


  /**
   * Create a default game, the default world ( three rooms, connected by stairs and hallways, two characters and some items)
   * @return an instance of the default game
   */
  public static Game createGame(){
    Game game = new Game();
    World world = createWorld();
    // register the world with the game and vice versa
    modifyField(game, false, "world", world);
    modifyField(world, false, "g", game);

    // add protagonist
    addProtagonist(game, "The guy");

    return game;
  }

  /**   CreateToyGame
   *
   *    # = Wall indestructible
   *    % = Wall destructible
   *    . = Wall destroyed
   *    c = Character initial
   *    F = Fixture
   *    K = Key
   *    T = Trap
   *    S = Stairway
   *    w = Wearable //not capitalized because it is not stationary
   *
   *
   *    This is the final plan, only room 1 is fully implemented yet
   *    Room 1:    Room 2
   *    ####       ####
   *    #c #      =D  #
   *    #F%D====== #  #
   *    ##S#       ####
   *      |
   *    ##S#       ####
   *    #ww#       #  #
   *    #wwD=======D  #
   *    ####       ####
   *    Room 3     Room 4
   *
   *
   * @return Game
   * todo Test what happens if room has no or little occupiable floor tiles
   */
  public static Game createToyGame(){
    Game game = new Game();
    World world = new World();
    List<Room> mapElements = new ArrayList<>();

    // add protagonist
    addProtagonist(game, "The walking potato"); //PW todo add to specific tile, delete in GameTest

    //First room contains obstacles, e.g. fountain and destroyable rock
    Room room1 = createEvenSimplerRoom(4,4,  1);

    //Second room contains nothing (could implement a trap here)
    Room room2 = createEvenSimplerRoom(4,4, 2);

    //Third room todo: holds weapons and wearables with effect
    Room room3 = createEvenSimplerRoom(4,4, 3);

    //Fourth room todo: specify e.g. holds other characters that can be attacked
    Room room4 = createEvenSimplerRoom(4,4, 4);

    //1. First room (obstacles)
    //Tile[1][1] holds a fixture
    CharacterModifier fixtureeffect = new CharacterModifier(-10, 0, 0, 1);
    FloorTile fixtureposition = (FloorTile) room1.getTiles()[1][1];
    Item mirrorOfHell = new Fixtures(fixtureposition, TraitOwner.FOUNTAIN, fixtureeffect);
    assertEquals(fixtureposition.getFixtures().get(0), mirrorOfHell);
    assertEquals(mirrorOfHell.getTile(), fixtureposition);

    // Tile[2][1] contains a destructible wall
    WallTile destructiblewall = new WallTile(2, 1 , room1, 2);
    // room1.replaceTile(2, 1, destructiblewall); // not necessary anymore

    // Tile[3][1] contains a door, connects to second room Tile[0][2]
    DoorTile door1to2 = new DoorTile(3, 1, room1, false, true);
    DoorTile door2to1 = new DoorTile(0, 2, room2, false, true);
    connectTiles(door1to2, door2to1);

    // Tile[2][0] contains a stairway, connects to the third room Tile[2][3]
    StairTile stair1to3 = new StairTile(2,0, room1);
    StairTile stair3to1 = new StairTile(2,3, room3);
    connectStairTiles(stair1to3, stair3to1,false);

    //Third room Weapons and Items with effect
    Tile[][] room3tiles = room3.getTiles();
    Wearable spear = new Wearable(room3tiles[1][2], TraitOwner.PIKE, null, null, 2);
    Armor shield = new Armor(new MultiplicativeEffect(20, 0, 0, 1), room3tiles[2][2]);
    Wearable poison = new Wearable(room3tiles[1][1], TraitOwner.POTION, new CharacterModifier(-2, 0, 0, 1), null, 1);
    Wearable sauerkrautSaft = new Wearable(room3tiles[2][1], TraitOwner.POTION, new CharacterModifier(+3, 0, 0, 1), null, 1);

//    specify tile types (for all objects)
    DoorTile door3to4 = new DoorTile(3, 2, room3, false, false);
    DoorTile door4to3 = new DoorTile(0, 1, room4, false, false);
    connectTiles(door3to4, door4to3);
    // todo stairs and doors without end?

    // deleted other stuff about not-completely initilaized stairs and doors

    //Add Rooms to the world after modifications
    modifyField(room1, false,"w", world);
    mapElements.add(room1);
    modifyField(room2, false,"w", world);
    mapElements.add(room2);
    modifyField(room3, false,"w", world);
    mapElements.add(room3);
    modifyField(room4, false,"w", world);
    mapElements.add(room4);

    //Add mapElements to the world though mapElements
    modifyField(world, false,"mapElements", mapElements);

    //Add game to the world and vice versa
    modifyField(game, false, "world", world);
    modifyField(world, false, "g", game);

    //Place Character
    Character protagonist = game.getProtagonist();
    Tile[][] first_room_tiles = room1.getTiles();
    protagonist.tile = first_room_tiles[1][2];
    List<Character> inhabitants = room1.getInhabitants();
    inhabitants.add(protagonist);

    // add monsters in fourth room
    Character m1 = createBaseCharacter("flyingSpaghettiMonster", 10, 2);
    Character m2 = createBaseCharacter("evilHamster", 2, 1);
    addCharacter(room4, 1, 2, m1);
    addCharacter(room4, 2, 2, m2);
    return game;
  }

  /**
   * the default world ( three rooms, connected by stairs and hallways, two characters and some items)
   * @return an instance of the default world
   */
  public static World createWorld()  {
    World world = new World();
    List<Room> mapElements = new ArrayList<>();
    // Create the rooms
    Room room1 = createSimpleRoom(8,8,  1);
    modifyField(room1, false,"w", world);
    mapElements.add(room1);
    Room room2 = createSimpleRoom(7,7, 2);
    modifyField(room2, false,"w", world);
    mapElements.add(room2);
    Room room3 = createSimpleRoom(4, 10, 3);
    modifyField(room3, false,"w", world);
    mapElements.add(room3);
    modifyField(world, false,"mapElements", mapElements);

    // Connect Room 1 and 2 with a hallway
    DoorTile doorTile1 = (DoorTile) room1.getTiles()[0][8/2];
    DoorTile doorTile2 = (DoorTile) room2.getTiles()[0][7/2];
    connectTiles(doorTile1, doorTile2);
    // Connect Room 2 and 3 with stairs
    StairTile stairTile1 = (StairTile) room2.getTiles()[7/2][7/2];
    StairTile stairTile2 = (StairTile) room3.getTiles()[4/2][10/2];
    connectStairTiles(stairTile1, stairTile2,false);

    // add two characters to the world
    Character c1 = createBaseCharacter("Foo", 10, 10);
    Character c2 = createBaseCharacter("Bar", 10, 10);
    addCharacter(room1, 2,2,c1);
    addCharacter(room3, 1,4,c2);

    // create a bunch of items and place them in the world
    Wearable sword = createWearable(2, true);
    modifyField(sword, false, "character",  c1);
    modifyField(c1, false, "activeWeapon", sword );
    c1.items.add(sword);
    Item fountain = new Fixtures();
    List<Item> onTile = new ArrayList<>();
    onTile.add(fountain);
    modifyField(fountain, true, "onTile", room1.getTiles()[3][3]);
    modifyField(room1.getTiles()[3][3], false, "items", onTile);

    Wearable rottenApple = createWearable(1, false);
    placeItemsInRoom(room3,2,8, rottenApple);
    modifyField(rottenApple, true, "effect", new CharacterModifier(1, 0, 0, 1));
    return world;
  }

  /** Create room with floor tiles inside and walltiles around them */
  public static Room createEvenSimplerRoom(int x, int y, int roomNo){
    Room room = new Room();
    Tile[][] tiles;
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
    tiles = result;
    modifyField(room, false,"tiles", tiles);
    return room;
  }


  /**
   * Creates a simple room of the size x,y ... the room number can be used to identify the room when debugging
   * @param x the 1 dimension of the room
   * @param y the 2 dimension of the room
   * @param roomNo the room number
   * @return a simple room
   */
  public static Room createSimpleRoom(int x, int y, int roomNo){
    Room r = new Room();
    Tile[][] tiles = createTiles(x, y, r, roomNo);
    modifyField(r, false,"tiles", tiles);
    return r;
  }

  /**
   * create the tiles used to create a room
   * @param x the 1 dimension of the room
   * @param y the 2 dimension of the room
   * @param room the room the tiles belong to
   * @param roomNo the room's number
   * @return a Tile[][] used in the room
   */
  private static Tile[][] createTiles(int x, int y, Room room, int roomNo){
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
    if (roomNo < 3) result[0][y/2] = new DoorTile(0, y/2 , room);
    // place a stair tile right in the center of the room
    if (roomNo > 1) result[x/2][y/2] = new StairTile(x/2, y/2, room);
    return result;
  }


  /**
   * Add the given Character to the room at the given position
   * @param room the room the character should be added to
   * @param x the x position
   * @param y
   * @param character
   */
  public static void addCharacter(Room room, int x, int y, Character character){
    List<Character> inhabitants = room.getInhabitants();
    inhabitants.add(character);
    modifyField(room,false, "inhabitants", inhabitants);
    placeCharacter(character,room.getTiles()[x][y]);
    //modifyField(character, false, "tile", room.getTiles()[x][y]);
  }

  public static CharacterModifier createCharacterModifier(int health, int magic, int power, int howlong ) {
    CharacterModifier attack = new CharacterModifier();
    attack.health = health;
    attack.magic = magic;
    attack.power = power;
    modifyField(attack, false, "howLong", howlong);
    return attack;
  }

  @Test
  public void testCreateWorld(){
    World testObject = createWorld();
    testDefaultWordData(testObject);
  }


  /**
   * This method can be used to check whether everything in out test/default world is at its place.
   * @param testObject the world to test.
   */
  public static void testDefaultWordData(World testObject){
    assertNotNull(testObject);
    List<Room> mapElements = testObject.getMapElements();
    assertEquals(3, mapElements.size());
    // Test if the rooms are instantiated and connected correctly
    Room r1 = null;
    Room r2 = null;
    Room r3 = null;
    for(Room r : mapElements){
      if(r.getTiles().length == 8 && r.getTiles()[0].length == 8){
        r1 = r;
      } else if(r.getTiles().length == 7 && r.getTiles()[0].length == 7){
        r2 = r;
      } else if(r.getTiles().length == 4 && r.getTiles()[0].length == 10){
        r3 = r;
      }

    }
    // Test if the rooms are instantiated and connected correctly
    assertNotNull(r1);
    assertNotNull(r2);
    assertNotNull(r3);
    // r1 should be of size [8][8], have a door at [0][4]
    assertEquals(8,r1.getTiles().length);
    assertEquals(8,r1.getTiles()[0].length);
    DoorTile dr1 = (DoorTile) r1.getTiles()[0][4];
    assertEquals(DoorTile.class, dr1.getClass());
    // r2 should be of size [7][7], have a door at [0][7/2] and stairs at [7/2][7/2]
    assertEquals(7,r2.getTiles().length);
    assertEquals(7,r2.getTiles()[0].length);
    DoorTile dr2 = (DoorTile) r2.getTiles()[0][7/2];
    StairTile sr2 = (StairTile) r2.getTiles()[7/2][7/2];
    assertEquals(DoorTile.class, dr2.getClass());
    // both doors are connected
    assertEquals(dr1.getHallway(), dr2.getHallway());
    // r3 should be of size [4][10], have a door at [0][5] and stairs at [2][5]
    assertEquals(4,r3.getTiles().length);
    assertEquals(10,r3.getTiles()[0].length);
    StairTile sr3 = (StairTile) r3.getTiles()[2][5];
    // both stairs are connected
    assertEquals(sr2.getStair(), sr3.getStair());
    // there should be a character named "Foo" in room1 carrying a sword
    Character foo = r1.getInhabitants().get(0);
    assertNotNull(foo);
    assertEquals("Foo",foo.name);
    assertNotNull(foo.activeWeapon);
    assertEquals(2,foo.activeWeapon.weight);
    // there should be a character named "Bar" in room3
    Character bar = r3.getInhabitants().get(0);
    assertNotNull(bar);
    // there should be a fixture in room 1 on tile [3][3]
    assertEquals(Fixtures.class, r1.getTiles()[3][3].onTile().get(0).getClass());
    // there should be a Wearable  in room 3 on tile [2][8]
    assertEquals(Wearable.class, r3.getTiles()[2][8].onTile().get(0).getClass());
  }

  /**
   * Place items at the specified position in the room
   * @param x the x coordinate
   * @param room the room in which the items should appear
   * @param y the y coordinate
   * @param items the items to be added
   */
  public static void placeItemsInRoom(Room room, int x, int y, Item... items) {
    List<Item> onTile = new ArrayList<>(Arrays.asList(items));
    for(Item wearable : onTile){
      if (wearable instanceof  Armor)
        modifySuperSuperField(wearable, "onTile", room.getTiles()[x][y]);
      else
        modifyField(wearable, true, "onTile", room.getTiles()[x][y]);
    }
    modifyField(room.getTiles()[x][y], false, "items", onTile);
  }

  /**
   * Place the character on the given tile.
   * @param testObject
   * @param tile
   */
  public static void placeCharacter(Character testObject, Tile tile) {
    modifyField(testObject, false, "tile", tile);
  }

  /**
   * Create a simple instance of Wearable
   * @param weight the weight of the wearable
   * @param isWeapon boolean determining whether the wearable is a weapon
   * @return the new wearable
   */
  public static Wearable createWearable(int weight, boolean isWeapon) {
    Wearable item = new Wearable();
    modifyField(item, false, "weight", weight);
    modifyField(item, false, "isWeapon", isWeapon );
    return item;
  }

  /**
   * create a doortile with the given properties
   * @param destructible 0 if the door is indestructible, else power needed to force door open
   * @param isLocked true if the door is locked, otherwise false
   * @return
   */
  public static DoorTile createDoorTile(int destructible, boolean isLocked) {
    DoorTile doorTile = new DoorTile();
    modifyField(doorTile, false, "locked", isLocked);
    modifyField(doorTile, true, "destructible", destructible);
    return doorTile;
  }

  /**
   * create a walltile with the given properties
   * @param destructible 0 if the door is indestructible, else power needed to force door open
   * @return
   */
  public static WallTile createWallTile(int destructible){
    WallTile wallTile = new WallTile();
    modifyField(wallTile, false, "destructible", destructible);
    return wallTile;
  }

  /**
   * Create a default character, specified by it name, power, and the max weight he can carry
   * @param name the name of the character
   * @param power the character's power
   * @param maxWeight the max weight the character can carry
   * @return the default character
   */
  public static Character createBaseCharacter(String name, int power, int maxWeight ){
    Character character = new Character();
    modifyField(character, false, "name", name);
    modifyField(character,false, "items", new ArrayList<>());
    modifyField(character, false, "maxWeight", maxWeight);
    modifyField(character, false, "power", power);
    modifyField(character, false, "magic", 50);
    modifyField(character, false, "role", character.WARRIOR);
    return character;
  }


  /**
   * connect two doorTiles with a hallway
   * @param t1 the "start" doorTile
   * @param t2 the "end" doorTile
   * @return a new instance of Hallway
   */
  public static Hallway connectTiles(DoorTile t1, DoorTile t2) {
    Hallway connector = new Hallway();
    modifyField(connector,true,"fromTile", t1);
    modifyField(connector,true,"toTile", t2);
    modifyField(t1, false, "hallway", connector);
    TestUtils.modifyField(t2, false, "hallway", connector);
    return connector;
  }

  /**
   * connect two stairtiles with a stairs
   * @param t1 the "start" doorTile
   * @param t2 the "end" doorTile
   * @return a new instance of stair
   */
  public static Stair connectStairTiles(StairTile t1, StairTile t2, boolean isOneWay) {
    Stair connector = new Stair();
    modifyField(connector, false, "oneWay", isOneWay);
    modifyField(connector,true,"fromTile", t1);
    modifyField(connector,true,"toTile", t2);
    modifyField(t1, false, "stair", connector);
    TestUtils.modifyField(t2, false, "stair", connector);
    return connector;
  }

  /**
   * creates an armor
   * @param health double defining by how much the armor protects the characters health
   * @param magic double defining by how much the armor protects the characters magic
   * @param power double defining by how much the armor protects the characters power
   * @return
   */
  public static Armor createArmor(double health, double magic, double power ){
    Armor armor = new Armor();
    MultiplicativeEffect multiplicativeEffect = new MultiplicativeEffect();
    modifyField(multiplicativeEffect, true, "health", health);
    modifyField(multiplicativeEffect, true, "magic", magic);
    modifyField(multiplicativeEffect, true, "power", power);
    modifyField(armor, false, "modifyingEffect", multiplicativeEffect);
    return armor;
  }

  /**
   * equip the given wearable as the active weapon of the character
   * @param wearable the weapon
   * @param character the character that will carry it
   */
  public static void equipWeapon(Wearable wearable, Character character){
    character.activeWeapon = wearable;
  }

  /**
   * equip the given armor as the armor of the character
   * @param armor the armor, and
   * @param character the character that will carry it
   */
  public static void equipArmor(Armor armor, Character character){
    character.armor.add(armor);
    character.items.add(armor);
  }

  /** Add a protagonist with given name to the game */
  public static Character addProtagonist(Game game, String name){
    Character character = new Character();
    modifyField(game, false, "protagonist", character);
    modifyField(character, false, "name", name);
    modifyField(character, false, "role", character.WARRIOR);
    return character;
  }

  public static void placeTrapOnTile(Trap trap, Tile tile){
    trap.onTile = tile;
    if (tile instanceof  StairTile) {
      modifyField(trap, false, "trapDoor", tile);
      modifyField(tile, false, "trap", trap);
    }
  }
}