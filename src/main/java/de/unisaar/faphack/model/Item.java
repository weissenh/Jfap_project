package de.unisaar.faphack.model;

/**
 * @author
 *
 */
public abstract class Item {
	/**
	 * The Tile on which the item is placed. This is null if the Item is in the
	 * inventory of a character.
	 */
	protected Tile onTile;
	/**
	 * The room in which the tile is located. This is null if the Item is in the
	 * inventory of a character.
	 */
	protected Room room;
	/**
	 * The Effect connected to the item.
	 */
	protected Effect effect;

	public Item() {

	}

}
