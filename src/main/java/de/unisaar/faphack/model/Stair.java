package de.unisaar.faphack.model;

/**
 * (NULL)
 * 
 * @author
 *
 */
public class Stair extends Connector {
	private StairTile fromTile;
	private StairTile toTile;
	/**
	 * If true, can only be used in direction from -> to
	 */
	private boolean oneWay = false;

	public Stair() {

	}

}
