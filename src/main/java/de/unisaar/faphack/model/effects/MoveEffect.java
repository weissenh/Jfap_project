package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;

import static java.lang.Math.abs;

public class MoveEffect implements Effect<Character, Boolean> {
  private Direction dir;

  public MoveEffect(Direction d) {
    dir = d;

    //Checks whether the move exceeds the distance limitations
    if (abs(this.dir.x)>1 || abs(this.dir.y)>1) {  //
      throw new IllegalArgumentException("Move exceeds distance limitations.");
    }
  }

  /**
   * tries to move the character into the given direction.
   * If the character's power == 0 only moves with direction (0,0) are possible,
   * i.e. the character is resting and its power increases by 5
   * @param c the character to move
   * param dir the direction to move the character (max one tile: only adjacent tile)
   * e.g.  1  2  3     if you are on 2, then  can move to 1,2,3,4,5,6
   *       4  5  6     if you are at 5 you can move to all numbers
   *       7  8  9
   * @return true if successful, false otherwise
   */
  public Boolean apply(Character c) {
    // todo: check whether we only move one tile
    // todo: question: what if destination tile contains a trap?

    Tile cpos = c.getTile(); // current tile: starting point of character
    // ask the room the tile is in where we will end up if we move in the direction
    Room currentroom = cpos.getRoom();
    Tile desttile = currentroom.getNextTile(cpos, dir);  // maybe not full direction
    // ask the destination tile whether it will take the character (answer null if cannot be moved)

    Tile t = desttile.willTake(c);
    if (t == null) {
      return false;
    }

    //Verifies whether the character still has power left, returns false if not
    if (c.getPower() == 0) {
      if (this.dir.x == 0 && this.dir.y == 0) {
        //does not need to move as c is already on correct tile
        c.rest(); //increases characters power by 5
      } else return false;
    }

    c.move(desttile);
    return true;
  }

}
