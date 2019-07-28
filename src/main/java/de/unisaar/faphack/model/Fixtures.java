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
    this(null, FOUNTAIN, null);
    // this(null, CHEST, null);
    // todo: what to do with trait? chest, fountain trait as default?
  }

  public Fixtures(FloorTile where, CharacterModifier effect) {
    this(where, FOUNTAIN, effect);
  }

  private Fixtures(FloorTile where, String trait, CharacterModifier effect) {
    // assert (trait != null);   // todo: don't do input validation with assert
    // assert (FOUNTAIN.equals(trait) || CHEST.equals(trait));
    super(where, trait, effect);
    if (where != null) {
      // TODO ADD FIXTURE TO LIST OF ITEMS OF THE TILE!??
      // onTile.addItem(this);  // todo doesn't work currently because only wearable allowed
    }
  }
  // todo: do anything special with chests to allow interaction with them?
  // e.g. chest don't have character modifier but
  // if you interact with them new wearables may appear on the current tile (but can interact only once?)
}
