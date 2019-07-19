package de.unisaar.faphack.model.map;

import java.util.List;

import de.unisaar.faphack.model.Game;

/**
 * @author
 *
 */
public class World implements Storable {
  private Game g;

  private List<Room> mapElements;

  public World() {}

  @Override
  public void marshal(MarshallingContext c) {
    // TODO: FILL THIS
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    // TODO: FILL THIS
  }

  public List<Room> getMapElements(){
    return mapElements;
  }
}
