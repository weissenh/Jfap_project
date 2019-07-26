package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.Item;
import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.Wearable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 *
 */
public class FloorTile extends Tile {
  /**
   * The items placed on this tile.
   */
  protected List<Item> items = new ArrayList<>();

  public FloorTile() {
    trait = FLOOR;
  }

  public FloorTile(int x, int y, Room room){
    super(x, y, room);
    trait = FLOOR;
  }

  /**
   *
   * @param c the character that wants to enter this tile
   * @return  returns <code>this</code> tile if
   *            1. the class is not already occupied by an character or
   *            2. is occupied by the character itself
   *           else null
   *
   */
  @Override
  public Tile willTake(Character c) {
    // check if we have a real character
    if (c == null) {return null;}

    // we can't go on the tile if it's already occupied
    // search for inhabitants in the room & check if the character is our character itself
    for (Character character : room.getInhabitants()) {
      if (this.isOccupied(character) & character != c) {return null;}
      else if (this.isOccupied(c)) {return this;}
    }

    // we can normally go on a floor tile
    return this;
  }

  /** FloorTiles can have items on them */
  public List<Item> onTile() {
    return items;
  }

  @Override
  public boolean removeItem(Wearable what) {
    return items.remove(what);
  }

  @Override
  public boolean addItem(Wearable what) {
    return items.add(what);
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("items", this.items);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    c.readAll("items", this.items);
  }

  /**
   *
   * @return true if the tile is occupied by a character
   */
  @Override
  public boolean isOccupied(Character character){
    // TODO please implement me!
    if (character == null) {return false;}
    return character.getTile() == this;
  }

}
