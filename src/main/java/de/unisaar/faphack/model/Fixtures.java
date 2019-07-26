package de.unisaar.faphack.model;

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
    super(null, "chest", null);
    // todo: what kind of effect?
    // todo: what to do with trait? chest, fountain trait
  }

}
