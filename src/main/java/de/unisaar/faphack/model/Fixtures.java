package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.FloorTile;

/**
 * Fixtures are Items that can't be carried by a Character. They are placed on a
 * Tile and the Character can interact with them through actions (healing fount
 * ain, chests) or by running into them (e.g. traps).
 *
 * @author weissenh
 *
 */
public class Fixtures extends Item {

  public Fixtures() {
    super(null, FOUNTAIN, null);
    // super(null, CHEST, null);
    // todo: what kind of effect?
    // todo: what to do with trait? chest, fountain trait
  }

  public Fixtures(FloorTile where, String trait, CharacterModifier effect) {
    // assert (trait != null);   // todo: don't do input validation with assert
    // assert (FOUNTAIN.equals(trait) || CHEST.equals(trait));
    super(where, trait, effect);
  }
  // todo: do anything special with chests to allow interaction with them?
  // e.g. chest don't have character modifier but
  // if you interact with them new wearables may appear on the current tile (but can interact only once?)
}
