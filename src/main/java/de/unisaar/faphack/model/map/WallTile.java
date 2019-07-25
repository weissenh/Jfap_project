package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.CharacterModifier;
import de.unisaar.faphack.model.MarshallingContext;

/**
 * Walltiles are used to determine the arrangement of a room. They usually
 * define the outer borders of a room, but might also be used within a room to
 * separate areas.
 * __________________
 * |                |
 * |                |
 * |   _______      |
 * |  |_____  |     D
 * |________________|
 *
 * @author
 *
 */
public class WallTile extends Tile {
  /** -1 means infinitely strong, 0 means destroyed, f must apply at least force f*/
  protected int destructible;

  public WallTile() {
    trait = WALL;
  }

  public WallTile(int x, int y, Room room, int destructible){
    super(x, y, room);
    trait = WALL;
    destructible = destructible;
  }

  //todo: change back to standard
  public WallTile(int x, int y, Room room){
    super(x, y, room);
    trait = WALL;
    //this(x,y,room,-1);
  }



  @Override
  public Tile willTake(Character c) {
    //todo: delete this;
    this.destructible = -1;
    //Wall tile already destroyed
    if (this.destructible == 0){
      return this;
    }

    //Wall tile undestroyable
    else if (this.destructible == -1){
      return null;
    }

    //Wall is destructed if character has sufficient power
    else if(this.destructible <= c.getPower()){
      this.destructible = 0;

      int p = - this.destructible;
      int h = 0;
      int m = 0;
      int hl = 1;
      CharacterModifier characterModifier = new CharacterModifier(h, m, p, hl);
      c.applyItem(characterModifier);
      return this;
    }

    return null;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("destructible", this.destructible);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    c.readInt("destructible");
  }


}
