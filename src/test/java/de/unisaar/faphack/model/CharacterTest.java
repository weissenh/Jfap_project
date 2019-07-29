package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.FloorTile;
import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class CharacterTest {

  // todo what if a character is dead? can he/she still do all the things below (move, equip, applyAttack...)?

  /**
   * Move a character which is placed on tile 1x1 in a square 4 x 4 room .
   * 1: Direction (0,1) to the right
   * 2: Direction (0,-1) to the left ( now we should be back where we started)
   * 3. Direction (-1, 1) down (this should have now effect as there is a wall )
   * 4. Direction (0,2) exception expected as only directions with values from -1 to 1 are allowed in MoveEffects
   */
  @Test
  void move() {
    Room room = TestUtils.createSimpleRoom(4,4,1);
    Tile[][] tiles = room.getTiles();
    Character testObject = createBaseCharacter("Foo", 2, 2);
    addCharacter(room, 1, 1, testObject);
    testObject.move(room.getNextTile(testObject.tile, new Direction(0,1 )));
    assertEquals(tiles[1][2], testObject.tile);
    testObject.move(room.getNextTile(testObject.tile, new Direction(0,-1 )));
    assertEquals(tiles[1][1], testObject.tile);
  }


  /**
   * Let a character which is placed on tile 1x1 in a square 4 x 4 room pickup some items.
   * 1. Item on tile 1x2 but character on tile 1x1, return false
   * 2. Character and item on the same tile (return true and item is in the characters inventory)
   * 3. Another item, this time the maxWeight limit is exceeded, return false
   */
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
    assertEquals(testObject, item1.character);
    assertNull(item1.onTile);
  }


  /**
   * Apply an attack that decreases health by 10, magic by 15 and power by 1 to a character with
   *  100 health, 50 magic and 2 power.
   *
   * 1. Character wears no armor,
   *      the character's values after the attack health: 90, magic: 35 and power: 1
   * 2. Character wears an armor reducing health damage by 50% and blocking all other damage (It's a really good armor!),
   *      character's values after the attack health: 85, magic: 35, power: 1
   *
   */
  @Test
  void applyAttack() {
    // create a character without any armor
    Character testObject = TestUtils.createBaseCharacter("Foo", 2, 2);
    CharacterModifier characterModifier = TestUtils.createCharacterModifier(-10, -15, -1, 1);
    testObject.applyAttack(characterModifier);
    assertEquals(90, testObject.health);
    assertEquals( 35, testObject.magic);
    assertEquals(1, testObject.power);
    // the testObject wears a armor now
    Armor armor = TestUtils.createArmor(0.5, 0.0, 0.0);
    TestUtils.equipArmor(armor, testObject);
    characterModifier = TestUtils.createCharacterModifier(-10, -15, -1, 1);
    testObject.applyAttack(characterModifier);
    assertEquals(85, testObject.health);
    assertEquals( 35, testObject.magic);
    assertEquals(1, testObject.power);
    // use 2 pieces of armor
    Armor armor1 = TestUtils.createArmor(0.5, 0.0, 0.0);
    TestUtils.equipArmor(armor1, testObject);
    characterModifier = TestUtils.createCharacterModifier(-10, -15, -1, 1);
    testObject.applyAttack(characterModifier);
    assertEquals(83, testObject.health);
    assertEquals( 35, testObject.magic);
    assertEquals(1, testObject.power);
  }

  /**
   * Test whether items, mor specific, the <code>CharacterModifier</code>s connected to them,
   * are correctly applied to our test subject (100 health, 50 magic and 2 power)
   */
  @Test
  void applyItem() {
    // create our test subject
    Character testObject = TestUtils.createBaseCharacter("Foo", 2, 2);
    // some poison
    CharacterModifier characterModifier = TestUtils.createCharacterModifier(-10, -15, -1, 1);
    testObject.applyItem(characterModifier);
    assertEquals(90, testObject.health);
    assertEquals( 35, testObject.magic);
    assertEquals(1, testObject.power);
    // some healing potion
    characterModifier = TestUtils.createCharacterModifier(10, 15, 1, 1);
    testObject.applyItem(characterModifier);
    assertEquals(100, testObject.health);
    assertEquals( 50, testObject.magic);
    assertEquals(2, testObject.power);
    // some food providing extra power
    characterModifier = TestUtils.createCharacterModifier(0, 0, 40, 1);
    testObject.applyItem(characterModifier);
    assertEquals(100, testObject.health);
    assertEquals( 50, testObject.magic);
    assertEquals(42, testObject.power);
  }

  /**
   * Resting will increase a character's power by 5.
   */
  @Test
  void rest() {
    Character foo = createBaseCharacter("Foo", 2, 2);
    foo.rest();
    assertEquals(7, foo.getPower());
  }

  @Test
  void dropItem() {
    Character character = createBaseCharacter("Foo", 10, 10);
    placeCharacter(character, new FloorTile());
    Wearable sword = createWearable(2, true);
    character.items.add(sword);
    character.activeWeapon = sword;
    Armor armor = createArmor(1,1,1);
    equipArmor(armor, character);
    assertTrue(character.dropItem(sword));
    // the inventory should be empty now
    assertTrue(!character.items.contains(sword));
    // the active weapon of the character should be null
    assertNull(character.getActiveWeapon());
    // the dropped wearable should be placed on the tile of the character
    assertTrue(character.tile.onTile().contains(sword));
    // now remove the armor from the inventory
    assertTrue(character.dropItem(armor));
    assertTrue(!character.items.contains(armor));
    assertTrue(!character.armor.contains(armor));
    assertTrue(character.tile.onTile().contains(armor));
    // try to remove an item which is not part of the inventory : returns false
    Wearable w = createWearable(1,false);
    assertFalse(character.dropItem(w));
  }

  @Test
  void equipItem() {
    // this character has only one wearable in its inventory, which is also the character's active weapon
    Character character = createBaseCharacter("Foo", 10, 10);
    Wearable sword = createWearable(2, true);
    character.items.add(sword);

    Armor armor = createArmor(1,1,1);
    character.items.add(armor);;
    // Equip an armor
    assertTrue(character.equipItem(armor));
    assertTrue(character.armor.contains(armor));

    // Equip a weapon
    assertTrue(character.equipItem(sword));
    assertTrue(character.activeWeapon == sword);

    // Illegal equip ( item not in inventory)
    Wearable item = createWearable(1, true);
    assertFalse(character.equipItem(item));
  }
}
