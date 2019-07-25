package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 *
 */
public class DoorTile extends WallTile implements Storable, Observable<DoorTile> {
  private boolean open = false;

  private boolean locked = false;

  private Hallway hallway;

  private List<Observer<DoorTile>> observers; // todo: marshal???

  /**
   * To be opened by an item (key) the Effect of that item needs to create a m
   * atching ID.
   */
  private int keyId;



  public DoorTile() {
  }

  public DoorTile(int x, int y, Room room){
    super(x, y, room);
  }

  @Override
  public Tile willTake(Character c) {
    // TODO: FILL THIS

    // check if the door is open
    // if the door is closed, is it locked? NO
    // open and closed but not locked doors are the same

    // check if the door is open:
    if (c == null) return null;
    if (this.open) {
      return hallway.toTile; // return the toTile;
    } else if (!this.locked) {
      return hallway.toTile; //return the toTile;
    } else {
      // check if the character has the key
      Key k = new Key(this.keyId);
      if (c.owns(k)) {
        this.locked = false;
        return hallway.toTile;
      }
    }

    return null;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("open", this.open ? 1 : 0);
    c.write("locked", this.locked ? 1 : 0);
    c.write("hallway", this.hallway);
    c.write("keyId", this.keyId);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.open = c.readInt("open") == 1;
    this.locked = c.readInt("locked") == 1;
    this.hallway = c.read("hallway");
    this.keyId = c.readInt("keyId");
  }

  public Hallway getHallway(){
    return hallway;
  }

  @Override
  public String getTrait() { return open ? OPENDOOR : DOOR; }

  @Override
  public void register(Observer<DoorTile> observer) {
    // lazy initialization
    // TODO please implement me!

  }

  @Override
  public void notifyObservers(DoorTile object) {
    // TODO please implement me!
  }


}
