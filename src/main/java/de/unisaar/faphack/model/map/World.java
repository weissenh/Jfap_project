package de.unisaar.faphack.model.map;

import java.util.List;

import de.unisaar.faphack.model.Game;
import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.Storable;

/**
 * @author
 *
 */
public class World implements Storable {
  public Game g;

  private List<Room> mapElements;

  public World() {}

  @Override
  public void marshal(MarshallingContext c) {
    c.write("game", this.g);
    c.write("mapElements", this.mapElements);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    this.g = c.read("game");
    c.readAll("mapElements", this.mapElements);
  }

  public List<Room> getMapElements(){
    return mapElements;
  }
}
