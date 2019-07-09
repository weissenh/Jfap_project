package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Direction;

/**
 * @author
 *
 */
public class Room {

	/** The world this room belongs to */
	private World w;

	/**
	 * A 2-dimensional Array defining the layout of the tiles in the room.
	 */
	private Tile[][] tiles;

	public Room() {

	}

	public Tile getNextTile(Tile t, Direction d) {
		return null;
	}

}
