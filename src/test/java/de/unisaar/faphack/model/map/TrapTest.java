package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.*;
import de.unisaar.faphack.model.Character;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.createBaseCharacter;
import static de.unisaar.faphack.model.TestUtils.createSimpleRoom;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author weissenh
 */
class TrapTest {

  @Test
  void move2trap() {
    CharacterModifier effect = new CharacterModifier(-2, 0, -1, 2);
    Room room = createSimpleRoom(4,4,1);
    FloorTile tile1 = new FloorTile(1, 1, room);
    FloorTile tile2 = new FloorTile(2, 2, room);
    // StairTile from = new StairTile()
    // Stair stair = new Stair(null, null, false);
    StairTile hiddenstairtile = new StairTile(1,1, room);
    Trap trap1 = new Trap(tile1, hiddenstairtile, effect);
    Trap trap2 = new Trap(tile2, null, effect);
    // test proper initialization
    assertEquals(trap1.getTile(), tile1);
    assertEquals(trap1.getTrait(), TraitOwner.FLOOR);
    assertEquals(trap1.getCharacterModifier(), effect);
    assertEquals(trap2.getTile(), tile2);
    assertEquals(trap2.getTrait(), TraitOwner.FLOOR);
    assertEquals(trap2.getCharacterModifier(), effect);
    // move character onto trap and see what happens
    Character person = createBaseCharacter("person1", 2, 10);
    // initialized with health 100, magic 50
    assertEquals(person.getHealth(), 100); // todo value might change (100)
    assertEquals(50, person.getMagic()); // todo value might change (50)
    person.move(tile1);
    assertEquals(person.getTile(), hiddenstairtile.stair.toTile); // todo what is the next tile?
    assertEquals(98, person.getHealth());
    assertEquals(99, person.getPower());
    assertEquals(50, person.getMagic());
    // todo: also try with trap with no trapdoor?
    person.move(tile2);
    assertEquals(person.getTile(), tile2); // todo what is the next tile?
    assertEquals(96, person.getHealth());
    assertEquals(98, person.getPower());
  }

}