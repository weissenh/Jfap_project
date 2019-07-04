package de.unisaar.faphack.model;

import java.util.List;

/**
 * @author
 *
 */
public class Tile {
	protected int x;
	protected int y;
	/**
	 * The room this tile is located in. This must not be null.
	 */
	protected Room room;
	/**
	 * The items placed on this tile.
	 */
	protected List<Item> items;

	public Tile() {

	}

}
