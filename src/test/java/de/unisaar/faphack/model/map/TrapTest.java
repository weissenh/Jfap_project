package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.*;
import de.unisaar.faphack.model.Character;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.createBaseCharacter;
import static de.unisaar.faphack.model.TestUtils.createSimpleRoom;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Traps
 *
 * @author weissenh
 */
class TrapTest {

  @Test
  void move2trap() {
    CharacterModifier effect = new CharacterModifier(-2, 0, -1, 2);
    Room room = createSimpleRoom(4,4,1);
    FloorTile tile1 = new FloorTile(1, 1, room);
    FloorTile tile2 = new FloorTile(2, 2, room);
    // have two stairs: one from (1,1) to (1,2) , the second from (2,1) to (2,2)
    // a) initialize starting and ending point of the stairs
    StairTile stair1from = new StairTile(1,1, room);
    StairTile stair1to = new StairTile(1,2, room);
    StairTile stair2from = new StairTile(2,1, room);
    StairTile stair2to = new StairTile(2,2, room);
    // b) initialize the stairs with its start and end
    Stair stair1 = new Stair(stair1from, stair1to, false);
    Stair stair2 = new Stair(stair2from, stair2to, false);
    // c) place traps on the start tiles: 1st is a trapdoor, second doesn't lead anywhere
    Trap trap1 = new Trap(tile1, stair1from, effect);
    Trap trap2 = new Trap(tile2, null, effect);
    // d) test proper initialization
    assertEquals(stair1from.stair.toTile, stair1to);
    assertEquals(stair1to.stair.fromTile, stair1from);
    assertEquals(trap1, stair1from.hasTrap());
    assertEquals(stair1, stair1from.getStair());
    assertEquals(trap1, stair1to.hasTrap());
    assertEquals(stair1, stair1to.getStair());
    assertNull(stair2from.hasTrap());
    assertNull(stair2to.hasTrap());
    assertEquals(stair2, stair2from.getStair());
    assertEquals(stair2, stair2to.getStair());
    assertEquals(trap1.getTile(), tile1);
    assertEquals(trap1.getTrait(), TraitOwner.FLOOR);
    assertEquals(trap1.getCharacterModifier(), effect);
    assertEquals(trap2.getTile(), tile2);
    assertEquals(trap2.getTrait(), TraitOwner.FLOOR);
    assertEquals(trap2.getCharacterModifier(), effect);
    // e) initialize character (with health 100, magic 50)
    Character person = createBaseCharacter("person1", 100, 10);
    assertNull(person.getTile());
    assertEquals(100, person.getHealth()); // todo value might change (100)
    assertEquals(100, person.getPower());
    assertEquals(50, person.getMagic()); // todo value might change (50)
    // f) move character onto first trap and see what happens
    person.move(tile1);
    assertEquals(stair1from.stair.toTile, person.getTile()); // trap door
    assertEquals(stair1to, person.getTile());
    assertEquals(98, person.getHealth()); // trap effect
    assertEquals(99, person.getPower());
    assertEquals(50, person.getMagic());
    // g) try out second trap
    person.move(tile2);
    assertEquals(tile2, person.getTile()); // no trap door this time
    assertEquals(96, person.getHealth()); // trap effect
    assertEquals(98, person.getPower());
  }

}