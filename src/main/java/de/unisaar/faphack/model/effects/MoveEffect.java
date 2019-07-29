package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;

import static java.lang.Math.abs;

public class MoveEffect implements Effect<Character, Boolean> {
  private Direction dir;

  public MoveEffect(Direction d) {
    this.dir = d;
    if (d == null) { // todo what if d is null? have Direction(0,0) as default or throw exception?
      // this.dir = new Direction(0, 0);
      throw new IllegalArgumentException("Cannot initialize Move effect with null direction");
    }
    //Checks whether the move exceeds the distance limitations
    if (abs(this.dir.x)>1 || abs(this.dir.y)>1) {  //
      throw new IllegalArgumentException("Move exceeds distance limitations.");
    }
  }

  /**
   * tries to move the character into the given direction.
   * If the character's power == 0 only moves with direction (0,0) are possible,
   * i.e. the character is resting and its power increases by 5
   * resting should only be allowed if power is 0 or below a threshold.
   * Otherwise you could gain infinite power just by resting.
   * @param c the character to move
   * param dir the direction to move the character (max one tile: only adjacent tile)
   * e.g.  1  2  3     if you are on 2, then  can move to 1,2,3,4,5,6
   *       4  5  6     if you are at 5 you can move to all numbers
   *       7  8  9
   * @return true if successful, false otherwise
   */
  public Boolean apply(Character c) {
    if (c == null) {
      return false;
    }

    Tile cpos = c.getTile(); // current tile: starting point of character

    // ask the room the tile is in where we will end up if we move in the direction
    Room currentroom = cpos.getRoom();
    Tile desttile = currentroom.getNextTile(cpos, dir);  // maybe not full direction


    // ask the destination tile whether it will take the character (answer null if cannot be moved)
    Tile t = desttile.willTake(c);

    if (t == null) {
      return false;
    }

    // If move direction is 0 0 then rest
    if (this.dir.x == 0 && this.dir.y == 0) {
      c.rest();
    }
    // If move direction is not 0 0 and power is zero
    else if (c.getPower() == 0){
      // todo shall we allow the character to rest/move even when his power is zero (== dead?) -> ZOMBIE!
      // todo return here? if move again maybe envoke entering effects (fountain?)
      return false;
    }


    c.move(t);
    return true;
  }

}
