package de.unisaar.faphack.model.map;

import java.util.List;

import de.unisaar.faphack.model.Direction;
import static java.lang.Math.max;
import java.lang.IndexOutOfBoundsException;

/**
 * @author
 *
 */
public class Room {

  /** The world this room belongs to */
  private World w;

  /** The Characters that currently are in this room */
  private List<Character> inhabitants;

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
   *
   * @return size of room on the xaxis (length of tiles)
   */
  private int get_x_dimension_size() {
    return tiles.length;
  }

  /**
   *
   * @return size of room on yaxis (length of sublists of tiles)
   */
  private int get_y_dimension_size() {
    int l1 = get_x_dimension_size();
    int l2 = 0;
    if (l1 != 0) {
      l2 = tiles[0].length;
      // todo: asserts all sublists have the same length!
    }
    return l2;
  }

  /**
  * Take tile and return which you reach when you follow the direction
   * @param t Tile starting point
   * @param d where to move
  * */
  public Tile getNextTile(Tile t, Direction d) {
    int newx = max(t.x + d.x, 0);  // negative positions are not allowed
    int newy = max(t.y + d.y, 0);
    // todo: what if room is empty? should raise Exception todo define own exception: room is empty exception?
    // todo: what if tile not in room? your fault, don't care but maybe cannot change to other tile..
    // todo: if total direction not possible, raise Exception or try to get as close as possible?
    // todo write tests, afterwards proceed to character
    int xdimsize = get_x_dimension_size();
    int ydimsize = get_y_dimension_size();
    // If one of the room dimensions is empty, cannot move...
    if (xdimsize == 0 || ydimsize == 0) throw new IndexOutOfBoundsException("Room is empty, cannot get next tile!");
    // if we would move out of the room (too high index, set to maximum index
    // todo: assert  xdimsize, ydimsize != 0
    if (newx >= xdimsize) { newx = xdimsize-1; }
    if (newy >= ydimsize) { newy = ydimsize-1; }
    Tile desttile = tiles[newx][newy];
    return desttile;
  }

}
