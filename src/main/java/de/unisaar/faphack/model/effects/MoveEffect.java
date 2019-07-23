package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.map.Tile;

public class MoveEffect implements Effect<Character, Boolean> {
  private Direction dir;

  public MoveEffect(Direction d) {
    dir = d;
  }

  /**
   * @param c the character to move
   * @return true if successful, false otherwise
   */
  public Boolean apply(Character c) {
    // TODO: FILL THIS
    return false;
  }

}
