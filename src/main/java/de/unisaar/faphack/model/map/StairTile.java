package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.MarshallingContext;

/**
 * @author
 *
 */
public class StairTile extends Tile {
  protected Stair stair;

  protected Trap trap;

  public StairTile() {
    trait = STAIR;
  }

  public StairTile(int x, int y, Room room){
    super(x, y, room);
    trait = STAIR;
  }

  /**
   * A stair can (possibly) be used in both directions: it depends on where you
   * are currently.
   *
   * Remember to update the level of the character.
   *
   * @return the new tile, or null if not possible to use
   */
  @Override
  public Tile willTake(Character c) {
    // TODO: FILL THIS
    if (c == null) {
      return null;
    } // what to return
    // check if the stair is one-way or two-ways
    if (this.stair.onlyDown()) {

      // only down: 'from' -> 'to'
      // doesn't go: 'to' -> 'from'
      // if the character stands on the 'to' tile: return null

      // the character can not stand on a stair. The stair leads directly to another room (portal)

      if (c.getTile() == this.stair.toTile) {
        return null;
      } else {
        c.levelDown();
        return this.stair.toTile;
      }
    }
    // check to and from
    if (c.getTile() == this.stair.toTile) {
      c.levelUp();
      return this.stair.fromTile;
    } else {
      c.levelDown();
      return this.stair.toTile;
    }
  }

  /** Return non-null if this is a trap */
  @Override
  public Trap hasTrap() {
    return trap;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("stair", this.stair);
    c.write("trap", this.trap);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.stair = c.read("stair");
    this.trap = c.read("trap");
  }

  public Stair getStair(){
    return stair;
  }
}
