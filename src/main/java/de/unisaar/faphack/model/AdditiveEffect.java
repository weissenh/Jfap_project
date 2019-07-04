package de.unisaar.faphack.model;

/**
 * Additive effects directly modify the health and magic of a character.
 * 
 * @author
 *
 */
public class AdditiveEffect implements Effect {
	/**
	 * The amount by which the health of the affected character is increased/dec
	 * reased.
	 */
	protected int health = 0;
	/**
	 * The amount by which the magic of the affected character is increased/decr
	 * eased.
	 */
	protected int magic = 0;
	/**
	 * This integer defines how long an effect applies. The value decreases when
	 * ever a character moves or performs an action.
	 */
	protected int howLong = 1;

	public AdditiveEffect() {

	}

	/**
	 * Modifies the values the given character.
	 * 
	 * @param on
	 * @return void
	 */
	public void apply(Character on) {
		// TODO Auto-generated method stub
	}

}
