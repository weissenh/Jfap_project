package de.unisaar.faphack.model.map;

import java.util.Collections;
import java.util.List;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.Item;
import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.Storable;

/**
 * @author
 *
 */
public abstract class Tile implements Storable {

  protected int x;
  protected int y;
  /**
   * The room this tile is located in. This must not be null.
   */
  protected Room room;

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

  /**
   * Given the "vector" d, what's the tile you get in return? (Hint: ask the room)
   *
   * @return the next tile in direction d
   */
  public Tile getNextTile(Direction d) {
    // TODO: FILL THIS
    return null;
  }

  /**
   * Can c proceed onto this tile?
   *
   * @return the current tile if you can move the Character c onto this tile, null
   *         otherwise
   */
  public abstract Tile willTake(Character c);

  /**
   *  Almost all tiles can not have items on them.
   */
  public List<Item> onTile() {
    return Collections.emptyList();
  }

  /**
   * Most tiles have no trap
   */
  public Trap hasTrap() {
    return null;
  }

  public void marshal(MarshallingContext c) {
    // TODO: FILL THIS
  }

  public void unmarshal(MarshallingContext c) {
    // TODO: FILL THIS
  }

}
