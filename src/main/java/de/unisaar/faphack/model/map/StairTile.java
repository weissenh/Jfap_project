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
    // check if we have a real character
    if (c == null) {return null;}

    // define the default goal tile
    Tile goalTile = stair.toTile;

    // check if the stair is one-way or two-ways
    if (this.stair.onlyDown()) {
      // only down: 'from' -> 'to'
      // doesn't go: 'to' -> 'from'
      // if the character stands on the 'to' tile: return null
      if (this == stair.toTile) {
        return null;
      } else {
        c.levelDown();
      }
    }

//    if we want to take the toTile, our goal tile is fromTile
    if (this == stair.toTile) {
      c.levelUp();
      goalTile = stair.fromTile;
    } else if (this == stair.fromTile){
      c.levelDown();
    }
    return goalTile;
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
