package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.ItemEffect;
import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.map.Tile;

public class Key extends Wearable implements Storable {

  private int keyID;

  //todo: Fab better method to get trait into the constructor
  public Key(Tile where, String trait, CharacterModifier effect, Character c, int kid) {
    super(where, trait=KEY, effect, c);
    keyID = kid;

  }

  public int getKeyID() {
    return this.keyID;
  }

  @Override

  public void marshal(MarshallingContext c) {
    super.marshal(c);
    // c.write("stair", this.stair);
    c.write("keyID", this.keyID);
  }

  //this.stair = c.read("stair");

  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.keyID = c.readInt("keyID");
  }

}
