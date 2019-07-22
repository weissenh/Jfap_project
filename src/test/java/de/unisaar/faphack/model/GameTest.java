package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.*;
import static de.unisaar.faphack.model.TestUtils.placeCharacter;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

  /**
   * Check if the a character, out testObject, is able to pickup the item correctly.
   * The item should no longer be in the tiles items list but in the inventory of the character
   */
  @Test
  void pickUp() {
    Game game = TestUtils.createGame();
    Room room = game.getWorld().getMapElements().get(0);
    Character testObject = createBaseCharacter("Foo", 2, 2);
    addCharacter(room, 1, 2, testObject);
    Wearable item1 = createWearable(2, false);
    placeItemsInRoom(room, 1,2,item1);
    assertTrue(game.pickUp(testObject, item1));
    // the item should have been removed from the tile and moved into the inventory of the character
    assertTrue(testObject.items.contains(item1));
    assertTrue(!room.getTiles()[1][2].onTile().contains(item1));

  }
}