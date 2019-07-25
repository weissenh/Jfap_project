package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.ItemEffect;
import de.unisaar.faphack.model.MarshallingContext;

public class Key extends Wearable implements Storable {

  private int keyID;

  public Key(int kid) {
    weight = 0;
    isWeapon = false;
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
