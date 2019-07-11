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

  Room(Tile[][] tiles){
    this.tiles = tiles;
  }
  
  /**
  * Take tile and return which you reach when you follow the direction
   * @param t Tile starting point
   * @param d where to move
  * */
  public Tile getNextTile(Tile t, Direction d) {
    int newx = t.x + d.x;
    int newy = t.y + d.y;
    // todo check if indices valid
    // tiles.length  if tiles.length > 0 : tiles[0].length  // maybe write get_room_size
    // todo write tests, afterwards proceed to character
    Tile desttile = tiles[newx][newy];
    return desttile;
  }

}
