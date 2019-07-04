package de.unisaar.faphack.model;

/**
 * Effects are connected to character values and items. They are used to modify
 * a character or other effects.
 * 
 * @author
 *
 */
public interface Effect {
	/**
	 * Combines this Effect and the given effect, either by adding or multiplyin g
	 * their values.
	 * 
	 * @param effect
	 * @return Effect
	 */
	public Effect apply(Effect effect);

	/**
	 * Modifies the values the given character.
	 * 
	 * @param on
	 * @return void
	 */
	public abstract void apply(Character on);

}
