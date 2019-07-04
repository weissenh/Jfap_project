package de.unisaar.faphack.model;

/**
 * Multiplicative effects can be used to modify additive effects.
 * 
 * @author
 *
 */
public class MultiplicativeEffect implements Effect {
	protected double health = 1;
	protected double magic = 1;
	/**
	 * This integer defines how long an effect applies. The value decreases when
	 * ever a character moves or performs an action.
	 */
	protected int howLong = 1;

	public MultiplicativeEffect() {

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
