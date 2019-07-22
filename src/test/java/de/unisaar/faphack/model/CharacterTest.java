package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.MoveEffect;
import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class CharacterTest {

  @Test
  void moveTo() {
    Room room = TestUtils.createSimpleRoom(8,8,1);
    Tile[][] tiles = room.getTiles();
    Character testObject = createBaseCharacter("Foo", 2, 2);
    addCharacter(room, 1, 1, testObject);
    MoveEffect moveEffect = new MoveEffect(new Direction(0,1));
    moveEffect.apply(testObject);
    assertEquals(tiles[1][2], testObject.tile);
    moveEffect = new MoveEffect(new Direction(0,-1));
    moveEffect.apply(testObject);
    assertEquals(tiles[1][1], testObject.tile);
    moveEffect = new MoveEffect(new Direction(-1,0));
    moveEffect.apply(testObject);
    assertEquals(tiles[1][1], testObject.tile);
    assertThrows(IllegalArgumentException.class, () ->  new MoveEffect(new Direction(0, 2)));
  }

  @Test
  void pickUp() {
    Room room = TestUtils.createSimpleRoom(8,8,1);
    Tile[][] tiles = room.getTiles();
    Character testObject = createBaseCharacter("Foo", 2, 2);
    addCharacter(room, 1, 1, testObject);
    Wearable item1 = createWearable(2, false);
    Wearable item2 = createWearable(2, false);
    placeItemsInRoom(room, 1,2,item1, item2);
    // the item is not on the field the character is
    assertFalse(testObject.pickUp(item1));
    // Wearable at [1][2]: picking this up should work
    placeCharacter(testObject, tiles[1][2]);
    assertTrue(testObject.pickUp(item1));
    assertTrue(testObject.items.contains(item1));
    // the item is too heavy for the character
    assertFalse(testObject.pickUp(item2));
  }


  @Test
  void applyAttack() {
    // create a character without any armor
    Character testObject = TestUtils.createBaseCharacter("Foo", 2, 2);
    CharacterModifier characterModifier = TestUtils.createAttack(-10, -15, -1, 1);
    testObject.applyAttack(characterModifier);
    assertEquals(90, testObject.health);
    assertEquals( 35, testObject.magic);
    assertEquals(1, testObject.power);
    // the testObject wears a armor now
    Armor armor = TestUtils.createArmor(0.5, 0.0, 0.0);
    TestUtils.equipArmor(armor, testObject);
    characterModifier = TestUtils.createAttack(-10, -15, -1, 1);
    testObject.applyAttack(characterModifier);
    assertEquals(85, testObject.health);
    assertEquals( 35, testObject.magic);
    assertEquals(1, testObject.power);
  }

  @Test
  void applyItem() {
    fail("Please implement me");
  }
}
