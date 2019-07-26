package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.*;

import java.util.Collections;
import java.util.List;

/**
 * @author
 *
 */

//// Tiles
//static final String FLOOR = "floor";
//static final String DOOR = "door";
//static final String OPENDOOR = "open_door";
//static final String WALL = "wall";
//static final String STAIR = "stair";

public abstract class Tile implements Storable, TraitOwner {

  protected int x;
  protected int y;

  /**
   * The room this tile is located in. This must not be null.
   */
  protected Room room;

  /**
   * The trait of this item.
   */
  protected String trait;

  public Tile() {

  }

  public Tile(int x, int y, Room room){
    this.room = room;
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Room getRoom() {
    return room;
  }

  /**
   * Given the "vector" d, what's the tile you get in return? (Hint: ask the room)
   *
   * @return the next tile in direction d
   */
  public Tile getNextTile(Direction d) {
    return this.room.getNextTile(this, d);
  }

  /**
   * Can c proceed onto this tile?
   *
   * @return the current tile if you can move the Character c onto this tile, null
   *         otherwise
   */
  // check if it is a tile you can move to (~wall)
  // If it is a wall, check if it is destructed or not

  // this is abstract, we define for each tile

  public abstract Tile willTake(Character c);

  /**
   *  Almost all tiles can not have items on them.
   */
  // what tiles can have items? FLOOR, STAIR? check items on tiles
  public List<Item> onTile() {
    return Collections.emptyList();
  }

  @Override
  public String getTrait() { return trait; }

  /**
   * Most tiles have no trap
   */
  // which ones do? ask getTrap
  public Trap hasTrap() {
    return null;
  }

  public void marshal(MarshallingContext c) {
    // write int x, int y, Room room, String trait
    c.write("x", this.x);
    c.write("y", this.y);
    c.write("room", this.room);
    c.write("trait", this.trait);
    // todo: save? identity?
  }

  public void unmarshal(MarshallingContext c) {
    this.x = c.readInt("x");
    this.y = c.readInt("y");
    this.room = c.read("room");
    this.trait = c.readString("trait");

  }

  public boolean removeItem(Wearable what) {
    return false;
  }
  // only for tiles that can have items

  public boolean addItem(Wearable what){
    return false;
  }
  // only for tiles that can have items

  /**
   *  Almost all tiles can not be occupied by a character.
   */
  // for FLOOR tiles and STAIR tiles
  public boolean isOccupied(Character c){
    return false;
  }

}
